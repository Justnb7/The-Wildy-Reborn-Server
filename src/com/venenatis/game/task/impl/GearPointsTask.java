package com.venenatis.game.task.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;

/**
 * This task will renew your gear points every 5 mintes.
 * @author Patrick van Elderen
 *
 */
public class GearPointsTask extends Task {
	
	/**
	 * The player whose energy we are restoring.
	 */
	private Player player;

	public GearPointsTask(Player player) {
		super(500);
		this.player = player;
	}

	@Override
	public void execute() {
		player.setGearPoints(2500);
		player.getActionSender().sendMessage("You're gear points have been renewed, you now have 2500 gear points.");
	}

}
