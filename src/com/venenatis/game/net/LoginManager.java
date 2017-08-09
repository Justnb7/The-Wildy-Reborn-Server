package com.venenatis.game.net;

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
import com.venenatis.game.net.network.session.GameSession;
import com.venenatis.game.net.network.session.LoginCode;
import com.venenatis.game.util.NameUtils;
import com.venenatis.game.world.World;
import com.venenatis.server.GameEngine;
import com.venenatis.server.Server;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class contains the bulk of the login handling code such as loading profile and sending a responce. 
 * It has a ExecutorService which will contain threads to run this code seperately from the Game Logic Thread.
 * @author patrick
 *
 */
public class LoginManager {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginManager.class);

	private final GameEngine engine; // not used but oh well nice to have a private ref
	
	public static class LoginRequest {
		public LoginRequest(ChannelHandlerContext ctx2, Channel chan2, LoginCredential credential) {
			this.creds = credential;
			this.ctx = ctx2;
			this.chan = chan2;
		}
		public final LoginCredential creds;
		public final ChannelHandlerContext ctx;
		public final Channel chan;
		
		@Override
		public String toString() {
			return "req[u:"+creds.getName()+"|pw:"+creds.getPassword()+"]";
		}
	}
	
	// TODO change to queue see demenethium pretty sure they've got a good impl
	public final List<LoginRequest> loginRequests = new ArrayList<>(); // fuck cant remember types of queue l0l
	
	/**
	 * Submits the main Runnable which will handle login requests on the Login Thread.
	 * @param engine
	 */
	public LoginManager(GameEngine engine) {
		this.engine = engine;
        IO_THREAD.scheduleAtFixedRate(() -> {
        	
        	// Lock the loginRequests object .. this means other Threads cannot access it and add/remove items.
        	synchronized (loginRequests) {
        		
				// Handle requests.
        		Iterator<LoginRequest> it = loginRequests.iterator();
        		LoginRequest r;
        		while (it.hasNext()) {
        			r = it.next();
        			logger.info("Login req being handled: "+r);
        			handleRequest(r);
        			it.remove();
        		}
        	}
        }, 0, IO_PULE_RATE, TimeUnit.SECONDS);
	}

	/**
	 * Do file IO, loading profile, all other shit and send a responce to the client. If code=2, also submit a NEW type of 'login request'
	 * to the engine.. this is for logging in and assigning the Player instance into the engine's player array. 
	 * @param req
	 */
    private void handleRequest(LoginRequest req) {
    	LoginCredential credential = req.creds;
    	ChannelHandlerContext ctx = req.ctx;
    	Channel chan = req.chan;
    	// chan might not be used
    	
    	final long start = System.currentTimeMillis();

		int returnCode = 2;
		
		final String username = credential.getName().trim();
		
		final String password = credential.getPassword();
		
		final String hostAddress = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
		
		//checkState(username.matches("^[a-zA-Z0-9_ ]{1,12}$") && !password.isEmpty() && password.length() <= 20, "A player tried logging in with an invalid username. [username= %s]", username);
		
		if(username.matches("") || username.matches(" ")) {
			sendReturnCode(ctx.channel(), LoginCode.SHORT_USERNAME);
			return;
		}
		
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
		if(!Arrays.stream(Constants.USERNAME_EXCEPTIONS).anyMatch($it -> username.equalsIgnoreCase($it))) {
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

		// in traditial PI's with hash not used at all
		/*if (credential.getClientHash() == 0 || credential.getClientHash() == 99735086 || credential.getClientHash() == 69) {
			sendReturnCode(ctx.channel(), LoginCode.BAD_SESSION_ID);
			return;
		}
		if (credential.getClientHash() != 78305513) {
			System.err.println("Got "+credential.getClientHash()+" expected "+78305513);
			sendReturnCode(ctx.channel(), LoginCode.LOGIN_SERVER_REJECTED_SESSION);
			return;
		}*/

		if (credential.getVersion() != Constants.CLIENT_VERSION) {
			sendReturnCode(ctx.channel(), LoginCode.GAME_UPDATED);
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
		
		if (players_online_sharing_one_host >= 3) {
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
						return;
					}
				} catch(IOException ex) {
					logger.error("Unexpected problem occurred.", ex);
					sendReturnCode(ctx.channel(), LoginCode.INVALID_CREDENTIALS);
					return;
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

		boolean succesfulProfileLoad = false;
		
		if (returnCode == 2) { // only bother loading if so far so good, otherwise extra stress on engine not needed
			try {
				succesfulProfileLoad = PlayerSave.load(player);
			} catch (Exception e) {
				logger.error("Profile fail load", e);
				returnCode = LoginCode.COULD_NOT_COMPLETE_LOGIN;
			}
		}

		LoginResponse response = new LoginResponse(returnCode, player.getRights().getCrown(), 0);

		ChannelFuture future = ctx.writeAndFlush(response);

		if (response.getResponse() != 2) {
			// fuck off
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
			player.setSession(new GameSession(player, chan));
			ctx.attr(NetworkConstants.KEY).set(player.getSession());
			
			if (!succesfulProfileLoad) {
				player.setNewPlayer(true);
				player.setTutorial(true);
				player.setRunEnergy(100);
				player.getActionSender().sendRunEnergy();
			} else {
				player.setNewPlayer(false);
			}
			
			// Successful login, correct pw etc! Tell the GameEngine we want to register this player.
			GameEngine.queueLogin(player);
			logger.info("Login req for "+credential.getName()+" complete on the LOGIN thread in "+(start-System.currentTimeMillis())+"ms!");
		}
    	
	}

	public static void sendReturnCode(Channel channel, int code) {
		ChannelFuture future = channel.writeAndFlush(new LoginResponse(code, 0, 0));
		future.addListener(ChannelFutureListener.CLOSE);
		logger.info("Responded code "+code);
	}

	/**
     * The rate in which to pulse the IO tasks.
     */
    private static final int IO_PULE_RATE = 2;

    /**
     * Handles the IO processing, such as player saves.
     */
    private static final ScheduledExecutorService IO_THREAD = Executors.newSingleThreadScheduledExecutor();

    /**
     * Add the request to the Queue. Why have a method instead of just list.add()? Because we need to SYNCHONIZE on it to avoid other threads messing with it.
     * @param loginRequest
     */
	public static void requestLogin(LoginRequest loginRequest) {
		synchronized (GameEngine.loginMgr.loginRequests) {
			GameEngine.loginMgr.loginRequests.add(loginRequest);
		}
	}
	
	
}
