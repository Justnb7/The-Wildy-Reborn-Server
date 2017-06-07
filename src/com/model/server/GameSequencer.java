package com.model.server;

import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;
import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.item.ground.GroundItemHandler;
import com.model.task.events.CycleEventHandler;

/**
 * The implementation that sequentially executes game-related processes.
 * 
 * @author Seven
 */
public final class GameSequencer implements Runnable {
	
	/**
	 * The logger that will print important information.
	 */
	private static final Logger LOGGER = Logger.getLogger(GameSequencer.class.getName());

	private static final Queue<Player> loginQueue = new ConcurrentLinkedQueue<>();

	public static void queueLogin(Player player) {
		Preconditions.checkArgument(!loginQueue.contains(player), "Login queue already contains " + player);
		loginQueue.add(player);
	}

	public static Collection<Player> getLoginQueue() {
		return Collections.unmodifiableCollection(loginQueue);
	}

	private static final int LOGIN_THRESHOLD = 25;
	
	private boolean cycle;
	
	@Override
	public void run() {
		try {
			if (cycle) {
				cycle();
				cycle = false;
			} else {
				subcycle();
				cycle = true;
			}
		} catch (final Throwable t) {
			LOGGER.log(Level.SEVERE, "An error has occured during the main game sequence!", t);
		}
	}
	
	private void subcycle() {
		for (Player player : World.getWorld().getUnorderedPlayers()) {
			if (player != null) {
				player.debug("send");
				player.getSession().processSubQueuedPackets();
				player.getSession().processQueuedPackets();
			}
		}
	}
	
	private void cycle() {
		for (int count = 0; count < LOGIN_THRESHOLD; count++) {
			Player p = loginQueue.poll();
			if (p == null)
				break;
			World.getWorld().register(p);
		}
		World.getWorld().pulse();
		Server.getGlobalObjects().pulse();
		Server.getTaskScheduler().pulse();
		GroundItemHandler.pulse();
		CycleEventHandler.getSingleton().process();
	}
}