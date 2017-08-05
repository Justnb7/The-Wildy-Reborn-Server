package com.venenatis.game.task.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;

/**
 * This task will renew your gear points every 6 minutes.
 * @author Patrick van Elderen
 *
 */
public class GearPointsTask extends Task {

	public GearPointsTask() {
		super(600);
	}

	@Override
	public void execute() {
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null) {
				player.setGearPoints(2000);
				player.getActionSender().sendMessage("@blu@[Server]@bla@ Your Gear Points just refilled to 2500. Spend them at a Legends Guard.");
			}
		}
	}
}
