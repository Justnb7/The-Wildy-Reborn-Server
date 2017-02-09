package com.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionListener;
import com.model.game.item.ItemHandler;
import com.model.game.object.GlobalObjects;
import com.model.game.sync.GameDataLoader;
import com.model.game.sync.GameLogicService;
import com.model.net.network.NettyChannelHandler;
import com.model.net.network.codec.RS2Encoder;
import com.model.net.network.handshake.HandshakeDecoder;
import com.model.task.TaskScheduler;
import com.model.utility.Stopwatch;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;


/**
 * Server.java
 * 
 * @author Sanity
 * @author Graham
 * @author Blake
 * @author Ryan Lmtruck30
 */

public class Server {

	/**
	 * A logger for the {@link Server} class
	 */
	private static final Logger logger = Logger.getLogger(Server.class.getName());

	/**
	 * The elapsed time the server has been running for.
	 */
	public static Stopwatch stopwatch;
	
	/**
	 * The bootstrap for the netty networking implementation
	 */
	private static final ServerBootstrap BOOTSTRAP = new ServerBootstrap();

	/**
	 * The task scheduler.
	 */

	private static final TaskScheduler scheduler = new TaskScheduler();

	/**
	 * Server updating.
	 */
	public static boolean UpdateServer = false;

	/**
	 * Determines if the server is live, mysql queries are disabled while not
	 * live
	 */
	private static boolean live;

	/**
	 * The state of the server
	 */
	public static ServerState state = ServerState.STARTED;

	public static NPCHandler npcHandler = new NPCHandler();

	/**
	 * Starts up the server
	 * 
	 * @param args
	 *            The arguements presented when starting the server
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		stopwatch = new Stopwatch();
		try {

			if (args.length < 1) {
				logger.info("The server will start without live mode!");
				live = false;
			} else {
				live = Boolean.parseBoolean(args[0]);
				logger.info("The server will start " + (live ? "in" : "without") + " live mode!");
			}

			logger.info("Starting up " + Constants.SERVER_NAME + "!");

			state = ServerState.LOADING;

			new Thread(new GameDataLoader()).start();
			globalObjects.pulse();

			while (state != ServerState.LOADED) {
				Thread.sleep(20);
			}

			if (isLive()) {
				// CharacterBackup.start(FileUtils.normalize("data/characters/"),  12, TimeUnit.HOURS);
			}
			GameLogicService.initialize();
			World.getWorld().init();
			//new Motivote(new RewardHandler(), "http://luzoxpk.biz/vote", "c74f2eb9").start();
			//StoreDatabase.init();
			globalObjects.pulse();
			globalObjects.loadGlobalObjectFile();
			globalObjects.pulse();
			ResourceLeakDetector.setLevel(io.netty.util.ResourceLeakDetector.Level.PARANOID);
			bind(Constants.SERVER_PORT);
			logger.info(Constants.SERVER_NAME + " has been Succesfully started.");

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("A fatal exception has been thrown!");
			System.exit(1);
		}
	}

	/**
	 * Binds the networking to the provided port
	 * 
	 * @param port
	 *            The port to bind the networking too
	 */
	private static void bind(int port) {
		try {
			logger.info("Attempting to bind to port: " + port);
			BOOTSTRAP.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast("encoder", new RS2Encoder());
					ch.pipeline().addLast("decoder", new HandshakeDecoder());
					ch.pipeline().addLast("handler", new NettyChannelHandler());
				}
			});
			BOOTSTRAP.channel(NioServerSocketChannel.class);
			BOOTSTRAP.group(new NioEventLoopGroup());
			BOOTSTRAP.bind(port).syncUninterruptibly();
			logger.info("Successfully binded to port: " + port);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to bind to port: " + port + ", shutting down the server.", e);
			System.exit(0);
		}
	}

	/**
	 * Gets the task scheduler.
	 * 
	 * @return The task scheduler.
	 */

	public static TaskScheduler getTaskScheduler() {
		return scheduler;
	}

	/**
	 * Returns if the server is live
	 * 
	 * @return If the server is live
	 */
	public static boolean isLive() {
		return live;
	}

	private static GlobalObjects globalObjects = new GlobalObjects();

	public static GlobalObjects getGlobalObjects() {
		return globalObjects;
	}
	
	/**
	 * Calls the usage of player items.
	 */
	public static ItemHandler itemHandler = new ItemHandler();

	/**
	 * Gets the elapsed time the server has been running for.
	 * 
	 * @return The stopwatch.
	 */
	public static Stopwatch getUptime() {
		return stopwatch;
	}

	private static MultiplayerSessionListener multiplayerSessionListener = new MultiplayerSessionListener();

	public static MultiplayerSessionListener getMultiplayerSessionListener() {
		return multiplayerSessionListener;
	}
	
}
