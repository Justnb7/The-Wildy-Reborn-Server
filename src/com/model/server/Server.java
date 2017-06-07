package com.model.server;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.model.game.character.npc.NPCHandler;
import com.model.game.character.npc.drops.DropManager;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionListener;
import com.model.game.object.GlobalObjects;
import com.model.task.TaskScheduler;
import com.model.utility.Stopwatch;
import com.model.utility.SystemLogger;


/**
 * The core class of the server.
 * 
 * @author Sanity
 * @author Graham
 * @author Blake
 * @author Ryan Lmtruck30
 * @author Seven
 */

public class Server {

	/**
	 * The single logger for this class.
	 */
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

	//public static final Motivote MOTIVOTE = new Motivote("luzoxpk", "c01d05751b28441cfbbd1ff1f87add95");
	
	/**
	 * To check if the server needs to be updated.
	 */
	public static boolean UPDATE_SERVER = false;
	
	/**
	 * The flag that denotes the server is in debug mode.
	 */
	public static boolean SERVER_DEBUG = false;

	/**
	 * The state of the server
	 */
	public static ServerState state = ServerState.STARTED;
	
	/**
	 * The elapsed time the server has been running for.
	 */
	public static Stopwatch stopwatch;
	
	/**
	 * The bootstrap that will prepare the game and network.
	 */
	private final Bootstrap BOOTSTRAP = new Bootstrap();

	/**
	 * The task scheduler.
	 */
	private static final TaskScheduler scheduler = new TaskScheduler();

	/**
	 * The caller for the NPCHandler class
	 */
	public static NPCHandler npcHandler = new NPCHandler();
	
	/**
	 * The drop system
	 */
	private static final DropManager dropManager = new DropManager();
	
	/**
	 * The GlobalObjects that represents an gameobject
	 */
	private static GlobalObjects globalObjects = new GlobalObjects();
	
	/**
	 * Jason's dueling system
	 */
	private static MultiplayerSessionListener multiplayerSessionListener = new MultiplayerSessionListener();
	
	/**
	 * Creates the Venenatis server.
	 */
	public Server() {
		LOGGER.info("Starting Venenatis...");
	}
	
	/**
	 * The main entry point to the server.
	 * 
	 * @param args
	 *            The command line arguments.
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		stopwatch = new Stopwatch();
		state = ServerState.LOADING;
		try {
			Server server = new Server();
			System.setErr(new SystemLogger(System.err, new File("./data/logs/err")));
			server.getBootstrap().build().bind();
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "A problem has been encountered while starting the server.", t);
			System.exit(0);
		}
		LOGGER.info("Server Initialized. [Took " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds]");
	}
	
	/**
	 * Gets the elapsed time the server has been running for.
	 * 
	 * @return The stopwatch.
	 */
	public static Stopwatch getUptime() {
		return stopwatch;
	}
	
	/**
	 * Gets the bootstrap.
	 *
	 * @return The bootstrap.
	 */
	public Bootstrap getBootstrap() {
		return BOOTSTRAP;
	}
	
	/**
	 * Gets the task scheduler.
	 * 
	 * @return The task scheduler.
	 */

	public static TaskScheduler getTaskScheduler() {
		return scheduler;
	}

	public static GlobalObjects getGlobalObjects() {
		return globalObjects;
	}

	public static MultiplayerSessionListener getMultiplayerSessionListener() {
		return multiplayerSessionListener;
	}
	
	
	public static DropManager getDropManager() {
		return dropManager;
	}
	
}
