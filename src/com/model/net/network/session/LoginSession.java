package com.model.net.network.session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.PlayerUpdating;
import com.model.game.character.player.serialize.PlayerSerialization;
import com.model.game.character.player.serialize.PlayerSerialization.PlayerSaveDetail;
import com.model.net.ConnectionHandler;
import com.model.net.network.NetworkConstants;
import com.model.net.network.codec.RS2Decoder;
import com.model.net.network.codec.RS2Encoder;
import com.model.net.network.login.LoginCredential;
import com.model.net.network.login.LoginResponse;
import com.model.server.GameEngine;
import com.model.server.Server;
import com.model.utility.NameUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class LoginSession extends Session {

	private static final Logger logger = LoggerFactory.getLogger(LoginSession.class);
	
	private final ChannelHandlerContext ctx;

	public LoginSession(Channel channel, ChannelHandlerContext ctx) {
		super(channel);
		this.ctx = ctx;
	}

	@Override
	public void receiveMessage(Object object) {
		if (!(object instanceof LoginCredential)) {
			return;
		}
		LoginCredential credential = (LoginCredential) object;
		int returnCode = 2;
		String name = credential.getName();
		String pass = credential.getPassword();
		name = name.trim();
		name = name.toLowerCase();
		pass = pass.toLowerCase();

		if (name.length() > 12) {
			sendReturnCode(ctx.channel(), 8);
			return;
		}

		Player player = new Player(name);
		player.setUsername(name);
		player.setPassword(pass);
		player.setIdentity(credential.getIdentity());
		player.setMacAddress(credential.getMacAddress());
		player.connectedFrom = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress(); 
		player.setAttribute("identity", credential.getIdentity());
		player.setAttribute("mac-address", credential.getMacAddress() );
	
		player.setInStreamDecryption(credential.getDecryptor());
		player.setOutStreamDecryption(credential.getEncryptor());
		player.outStream.packetEncryption = credential.getEncryptor();
		
		if (ConnectionHandler.isNamedBanned(player.getName())) {
			sendReturnCode(ctx.channel(), 4);
			System.out.println("Name banned");
			return;
		}
		if(ConnectionHandler.isMacBanned(player.getMacAddress())) {
			sendReturnCode(ctx.channel(), 4);
			System.out.println("Mac banned");
		}
		if (ConnectionHandler.isIpBanned((player.connectedFrom))) {
			sendReturnCode(ctx.channel(), 4);
			System.out.println("Ip banned");
			return;
		}
		if (PlayerUpdating.getPlayerCount() >= World.getWorld().getPlayers().capacity()) {
			System.out.println("Too many players online");
			sendReturnCode(ctx.channel(), 7);
			return;
		}
		
		// prevents users from logging in before the network has been fully
		// bound
		if (!Server.SERVER_STARTED) {
			sendReturnCode(ctx.channel(), 14);
		}

		// prevents users from logging in if the world is being updated
		if (World.updateRunning) {
			sendReturnCode(ctx.channel(), 14);
		}
		if (credential.getClientHash() == 0 || credential.getClientHash() == 99735086 || credential.getClientHash() == 69) {
			System.out.println("Invalid client hash.");
			sendReturnCode(ctx.channel(), 18);
			return;
		}
		if (credential.getClientHash() != 39623221) {
			System.out.println("UID invalid.");
			sendReturnCode(ctx.channel(), 18);
			return;
		}
		if (ConnectionHandler.isIdBanned(player.getIdentity())) {
			System.out.println("Identity banned");
			sendReturnCode(ctx.channel(), 4);
			return;
		}
		if (credential.getVersion() != Constants.CLIENT_VERSION) {
			sendReturnCode(ctx.channel(), 18);
			System.out.println("Client version invalid.");
			return;
		}
		int count = 0;
		for (Player plr : World.getWorld().getPlayers()) {
			if (plr == null)
				continue;
			if (plr.connectedFrom.equals(player.connectedFrom)) {
				count++;
			}
		}
		if (count >= 2) {
			sendReturnCode(ctx.channel(), 9);
			return;
		}
		if (returnCode == 2) {
			System.out.println("Enter logincode: "+returnCode);
			File file = new File("data/characters/details/" + NameUtils.formatNameForProtocol(credential.getName()) + ".json");
			if (!file.exists()) {
				System.out.println("File not here: data/characters/details/ " + NameUtils.formatNameForProtocol(credential.getName()) + ".json");
			}
			if (file.exists()) {
				try {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					final PlayerSaveDetail details = PlayerSerialization.SERIALIZE.fromJson(reader, PlayerSaveDetail.class);
					name = details.user();
					pass = details.password();
					
					System.out.printf("pass check '%s' vs '%s' %n", pass, credential.getPassword());
					if (!name.equals(NameUtils.formatName(credential.getName())) || !pass.equals(credential.getPassword())) {
						sendReturnCode(ctx.channel(), 3);
					}
				} catch(IOException ex) {
					logger.error("Unexpected problem occurred.", ex);
					sendReturnCode(ctx.channel(), 3);
				}
			}
			if (credential.getRequestType().equals("register")) {
				System.out.println("register");
				if (PlayerSerialization.playerExists(name)) {
					System.out.println("player exists");
					if (name.equals(NameUtils.formatName(credential.getName())) || !pass.equals(credential.getPassword())) {
						sendReturnCode(ctx.channel(), 22);
						System.out.println("create profile for existing player");
						return;
					} else {
						sendReturnCode(ctx.channel(), 23);
						System.out.println("unable to register the profile");
						return;
					}
				} else {
					sendReturnCode(ctx.channel(), 22);
					System.out.println("Creating profile");
					return;
				}
			}
			/*if (load == 3) {
				player.saveFile = false;
				sendReturnCode(ctx.channel(), 3);
				return;
			}*/
		}
		
		for (String disabled : Constants.BAD_USERNAMES) {
			if (name.contains(disabled)) {
				sendReturnCode(ctx.channel(), 25);
				return;
			}
		}
		
		/*
		 * This bit should be done after the players loaded
		 */
		if (GameEngine.getLoginQueue().contains(player) || World.getWorld().getPlayerByRealName(name).isPresent()) {
			sendReturnCode(ctx.channel(), 5);
			return;
		}

		LoginResponse response = new LoginResponse(returnCode, player.getRights().getValue(), 0);

		ChannelFuture future = ctx.writeAndFlush(response);

		if (response.getResponse() != 2) {
			future.addListener(ChannelFutureListener.CLOSE);
		} else {
			ctx.pipeline().replace("encoder", "encoder", new RS2Encoder());
			ctx.pipeline().replace("decoder", "decoder", new RS2Decoder(credential.getDecryptor()));
			
			/*
			 * Fill in if our stuff is null or empty
			 */
			if (player.getIdentity() == null || player.getIdentity().isEmpty()) {
				player.setIdentity(credential.getIdentity());
			}
			if (player.getMacAddress() == null || player.getMacAddress().isEmpty()) {
				player.setMacAddress(credential.getMacAddress());
			}
			player.setSession(new GameSession(player, getChannel()));
			ctx.attr(NetworkConstants.KEY).set(player.getSession());
			GameEngine.queueLogin(player);
		}
	}

	public static void sendReturnCode(Channel channel, int code) {
		ChannelFuture future = channel.writeAndFlush(new LoginResponse(code, 0, 0));
		future.addListener(ChannelFutureListener.CLOSE);
	}

}
