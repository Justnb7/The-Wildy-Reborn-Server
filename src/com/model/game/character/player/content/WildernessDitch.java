package com.model.game.character.player.content;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;

public class WildernessDitch {
	
	private static final int ANIMATION_ID = 6132;
	private static int OFFSET_Y = 3;
	
	/**
	 * Whether our player is in the wilderness.
	 * @param c	the player
	 * @return true if the player's absolute x positiion is 3523
	 */
	public static boolean inWilderness(Player player) {
		return player.getY() >= 3523;
	}

	/**
	 * Crosses the ditch.
	 * @param c	the player
	 * @param x	the x-coordinate to walk to
	 * @param y the y-coordinate to walk to
	 */
	public static void crossDitch(Player player, int x, int y) {
		player.getMovementHandler().resetWalkingQueue();
		player.teleportToX = x;
       	player.teleportToY = y;
		player.getPA().requestUpdates();
	}

	/**
	 * Starts the enter process for the player.
	 * @param c	the player
	 */
	public static void enter(final Player player) {
		player.playAnimation(Animation.create(ANIMATION_ID));
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			@Override
			public void execute() {
				crossDitch(player, player.absX, player.absY + OFFSET_Y);
				if (player.absY <= 3523) {
					resetWalkIndex(player);
					stop();
				} else if (player.absX <= 2998) {
					resetWalkIndex(player);
					stop();
				}
			}
		});
	}
	
	

	/**
	 * Starts the leave process for the player.
	 * @param c	the player
	 */
	public static void leave(final Player player) {
		player.playAnimation(Animation.create(ANIMATION_ID));
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			@Override
			public void execute() {
				crossDitch(player, player.absX, player.absY - OFFSET_Y);
				if (player.absY <= 3523) {
					resetWalkIndex(player);
					stop();
				} else if (player.absX <= 2995) {
					resetWalkIndex(player);
					stop();
				}
			}
		});
	}
	
	/**
	 * Resets the player's walk index when the event has concluded.
	 * @param c	the player
	 */
	private static void resetWalkIndex(Player player) {
		player.setRunning(true);
		player.getActionSender().sendConfig(173, 1);
		player.walkAnimation = 0x333;
		player.getPA().requestUpdates();
	}
}