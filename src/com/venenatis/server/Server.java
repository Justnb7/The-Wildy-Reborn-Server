package com.venenatis.server;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.motiservice.Motivote;
import com.venenatis.game.content.quest_tab.QuestTabPageHandler;
import com.venenatis.game.content.quest_tab.QuestTabPages;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.npc.NPCHandler;
import com.venenatis.game.model.entity.npc.drop_system.DropManager;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.updating.PlayerUpdating;
import com.venenatis.game.task.TaskQueue;
import com.venenatis.game.util.Stopwatch;
import com.venenatis.game.util.SystemLogger;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
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
	 * Creates the Venenatis server.
	 */
	public Server() {
		LOGGER.info("Starting Venenatis...");
	}
	
	public static final Motivote MOTIVOTE = new Motivote("venenatis", "0d3dd0d69abbe4a8dd03a15f24ceb1cb");
	
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

			System.setErr(new SystemLogger(System.err, new File("./data/logs/err")));
			server.getBootstrap().build().bind();
			GameEngine.start();
			MOTIVOTE.checkUnredeemedPeriodically((result) -> {
			result.votes().forEach((vote) -> {
				boolean online = vote.username() != null && PlayerUpdating.isPlayerOn(vote.username());
				
				if (online) {
					Player player = World.getWorld().lookupPlayerByName(vote.username());
					
					if (player != null && player.isActive() == true) {
						MOTIVOTE.redeemFuture(vote).thenAccept((r2) -> {
							if (r2.success()) {
								int mystery_box_roll = Utility.random(10);
								if(mystery_box_roll == 8) {
									player.getInventory().addOrCreateGroundItem(player, new Item(6199));
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
		});
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
	
	public static DropManager getDropManager() {
		return dropManager;
	}
	
}
