package com.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.npc.drops.DropManager;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionListener;
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

	//public static final Motivote MOTIVOTE = new Motivote("luzoxpk", "c01d05751b28441cfbbd1ff1f87add95");
	
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
	 * The state of the server
	 */
	public static ServerState state = ServerState.STARTED;

	public static NPCHandler npcHandler = new NPCHandler();
	
	private static final DropManager dropManager = new DropManager();
	
	public static DropManager getDropManager() {
		return dropManager;
	}

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

			logger.info("Starting up " + Constants.SERVER_NAME + "!");

			state = ServerState.LOADING;

			new Thread(new GameDataLoader()).start();
			globalObjects.pulse();

			while (state != ServerState.LOADED) {
				Thread.sleep(20);
			}

			GameLogicService.initialize();
			World.getWorld().init();
			ResourceLeakDetector.setLevel(io.netty.util.ResourceLeakDetector.Level.PARANOID);
			bind(Constants.SERVER_PORT);
			
			logger.info(Constants.SERVER_NAME + " has been Succesfully started.");
			/*MOTIVOTE.checkUnredeemedPeriodically((result) -> {
				result.votes().forEach((vote) -> {
					boolean online = vote.username() != null && PlayerUpdating.isPlayerOn(vote.username());
					
					if (online) {
						Player player = World.getWorld().getPlayer(vote.username());
						
						if (player != null && player.isActive() == true) {
							MOTIVOTE.redeemFuture(vote).thenAccept((r2) -> {
								if (r2.success()) {
									int mystery_box_roll = Utility.random(10);
									if(mystery_box_roll == 8) {
										player.getItems().addOrCreateGroundItem(new Item(6199));
									}
									player.getActionSender().sendMessage("You've received your vote reward! Congratulations!");
									player.setTotalVotes(player.getTotalVotes() + 1);
									player.setVotePoints(player.getVotePoints() + 1);
									QuestTabPageHandler.write(player, QuestTabPages.HOME_PAGE);
								}
							});
						}
					}
				});
			});*/
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

	private static GlobalObjects globalObjects = new GlobalObjects();

	public static GlobalObjects getGlobalObjects() {
		return globalObjects;
	}

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
