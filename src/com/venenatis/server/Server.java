package com.venenatis.server;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.venenatis.game.model.entity.player.clan.ClanManager;
import com.venenatis.game.task.TaskQueue;
import com.venenatis.game.util.Stopwatch;
import com.venenatis.game.util.SystemLogger;
import com.venenatis.game.util.XMLController;
import com.venenatis.game.util.time.GameCalendar;
import com.venenatis.game.world.object.GlobalObjects;


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
	 * The single logger for this class.
	 */
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	
	/**
	 * To check if the server needs to be updated.
	 */
	public static boolean UPDATE_SERVER = false;
	
	/**
	 * The flag that denotes the server is in debug mode.
	 */
	public static boolean SERVER_DEBUG = false;
	
	/**
	 * The flag that denotes the server has started.
	 */
	public static boolean SERVER_STARTED = false;
	
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
	private static final TaskQueue scheduler = new TaskQueue();
	
	/**
	 * The GlobalObjects that represents an gameobject
	 */
	private static GlobalObjects globalObjects = new GlobalObjects();	
	
	/**
	 * Represents our calendar with a given delay using the TimeUnit class
	 */
	private static GameCalendar calendar = new com.venenatis.game.util.time.GameCalendar(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"), "GMT-3:00");
	
	/**
	 * Creates the Venenatis server.
	 */
	public Server() {
		LOGGER.info("Starting Venenatis...");
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
			Server server = new Server();

			System.setErr(new SystemLogger(System.err, new File("./err")));
			server.getBootstrap().build().bind();
			GameEngine.start();
			XMLController.loadAllFiles();
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

	public static TaskQueue getTaskScheduler() {
		return scheduler;
	}

	public static GlobalObjects getGlobalObjects() {
		return globalObjects;
	}

	public static GameCalendar getCalendar() {
		return calendar;
	}
	
	private static ClanManager clanManager = new ClanManager();

	public static ClanManager getClanManager() {
		return clanManager;
	}
	
	
}
