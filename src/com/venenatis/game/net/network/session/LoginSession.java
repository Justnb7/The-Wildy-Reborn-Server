package com.venenatis.game.net.network.session;

import static com.google.common.base.Preconditions.checkState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Sanctions;
import com.venenatis.game.model.entity.player.save.PlayerSave;
import com.venenatis.game.model.entity.player.save.PlayerSave.PlayerSaveDetail;
import com.venenatis.game.model.entity.player.updating.PlayerUpdating;
import com.venenatis.game.net.network.NetworkConstants;
import com.venenatis.game.net.network.codec.RS2Decoder;
import com.venenatis.game.net.network.codec.RS2Encoder;
import com.venenatis.game.net.network.login.LoginCredential;
import com.venenatis.game.net.network.login.LoginResponse;
import com.venenatis.game.util.NameUtils;
import com.venenatis.game.world.World;
import com.venenatis.server.GameEngine;
import com.venenatis.server.Server;

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
		
		final String username = credential.getName().trim();
		//So this final statement can only be used here cant be final cos you change it
		
		final String password = credential.getPassword();
		
		final String hostAddress = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
		
		checkState(username.matches("^[a-zA-Z0-9_ ]{1,12}$") && !password.isEmpty() && password.length() <= 20, "A player tried logging in with an invalid username. [username= %s]", username);
		
		Player player = new Player(username);
		
		player.setUsername(username);
		
		player.setPassword(password);
		
		player.setIdentity(credential.getIdentity());
		player.setAttribute("identity", credential.getIdentity());
		
		player.setMacAddress(credential.getMacAddress());
		player.setAttribute("mac-address", credential.getMacAddress());
		
		player.setHostAddress(hostAddress);
	
		player.setInStreamDecryption(credential.getDecryptor());
		player.setOutStreamDecryption(credential.getEncryptor());
		player.outStream.packetEncryption = credential.getEncryptor();
		
		if (player.getSanctions() == null) {
			player.setSanctions(new Sanctions(hostAddress, player.getMacAddress()));
		}
		
		// username too long or too short
		if (username.length() >= 13 || username.length() < 3) {
			sendReturnCode(ctx.channel(), LoginCode.SHORT_USERNAME);
			return;
		}
		
		// username contains invalid characters
		if(!username.matches("^[a-zA-Z0-9_ ]{1,12}$")) {
			sendReturnCode(ctx.channel(), LoginCode.INVALID_CREDENTIALS);
			return;
		}
		
		// check if this user has a valid username
		if(!Arrays.stream(Constants.USERNAME_EXCEPTIONS).anyMatch($it -> username.equalsIgnoreCase($it))) {//must be final here but down below
			for (String bad : Constants.BAD_USERNAMES) {
				if (username.toLowerCase().contains(bad.toLowerCase())) {
					sendReturnCode(ctx.channel(), LoginCode.BAD_USERNAME);
					return;
				}
			}
		}
		
		// evaluate the host
		if (player.getSanctions().isBanned()) {
			sendReturnCode(ctx.channel(), LoginCode.ACCOUNT_DISABLED);
			return;
		}
		
		// there is no password or password is too long
		if (password.isEmpty() || password.length() >= 20) {
			sendReturnCode(ctx.channel(), LoginCode.INVALID_CREDENTIALS);
			return;
		}
		
		if (player.getPassword() == null || player.getPassword().isEmpty()) {
			player.setPassword(password);
		}

		// there is no password or password is too long
		if (password.isEmpty() || password.length() >= 20) {
			sendReturnCode(ctx.channel(), LoginCode.INVALID_CREDENTIALS);
			return;
		}

		// password does not match password on file.
		if (!password.equals(player.getPassword())) {
			sendReturnCode(ctx.channel(), LoginCode.INVALID_CREDENTIALS);
			return;
		}
		
		// the world is currently full
		if (PlayerUpdating.getPlayerCount() >= World.getWorld().getPlayers().capacity()) {
			sendReturnCode(ctx.channel(), LoginCode.INVALID_CREDENTIALS);
			return;
		}
		
		// prevents users from logging in before the network has been fully bound
		if (!Server.SERVER_STARTED) {
			sendReturnCode(ctx.channel(), LoginCode.SERVER_BEING_UPDATED);
			return;
		}
		
		// prevents users from logging in if the world is being updated
		if (World.updateRunning) {
			sendReturnCode(ctx.channel(), LoginCode.SERVER_BEING_UPDATED);
		}
		
		// check if this users computer is on the banned list
		if (World.getWorld().getMacBans().contains(credential.getMacAddress())) {
			sendReturnCode(ctx.channel(), LoginCode.ACCOUNT_DISABLED);
			return;
		}
		
		if (credential.getClientHash() == 0 || credential.getClientHash() == 99735086 || credential.getClientHash() == 69) {
			sendReturnCode(ctx.channel(), LoginCode.BAD_SESSION_ID);
			return;
		}
		if (credential.getClientHash() != 39623221) {
			sendReturnCode(ctx.channel(), LoginCode.BAD_SESSION_ID);
			return;
		}

		if (credential.getVersion() != Constants.CLIENT_VERSION) {
			sendReturnCode(ctx.channel(), LoginCode.BAD_SESSION_ID);
			return;
		}
		
		//if (!username.equals(NameUtils.formatName(credential.getName())) || !password.equals(credential.getPassword())) {
			//sendReturnCode(ctx.channel(), 3);
		//}

		int players_online_sharing_one_host = 0;
		for (Player plr : World.getWorld().getPlayers()) {
			if (plr == null)
				continue;
			if (plr.getHostAddress().equals(player.getHostAddress())) {
				players_online_sharing_one_host++;
			}
		}
		
		if (players_online_sharing_one_host >= 2) {
			sendReturnCode(ctx.channel(), 9);
			return;
		}
		
		if (returnCode == 2) {
			// whats the purpose of this?
			//Logging in checking from the details of the player
			File file = new File("data/characters/details/" + NameUtils.formatNameForProtocol(username) + ".json");
			if (file.exists()) {
				try {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					final PlayerSaveDetail details = PlayerSave.SERIALIZE.fromJson(reader, PlayerSaveDetail.class);
					final String stored_password = details.password();
					// are you checking pw here? na u shouldnt be setting it, the info given by the player on login 
					// never changes
					if (!stored_password.equals(password)) { // there ya go ty
						sendReturnCode(ctx.channel(), LoginCode.INVALID_CREDENTIALS); // INVALID PW!
					}
				} catch(IOException ex) {
					logger.error("Unexpected problem occurred.", ex);
					sendReturnCode(ctx.channel(), LoginCode.INVALID_CREDENTIALS);
				}
			}
		}
		
		/*
		 * This bit should be done after the players loaded
		 */
		if (GameEngine.getLoginQueue().contains(player) || World.getWorld().getPlayerByRealName(username).isPresent()) {
			sendReturnCode(ctx.channel(), LoginCode.ACCOUNT_ONLINE);
			return;
		}

		LoginResponse response = new LoginResponse(returnCode, player.getRights().getCrown(), 0);

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
