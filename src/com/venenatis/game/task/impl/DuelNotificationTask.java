package com.venenatis.game.task.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.ScheduledTask;
import com.venenatis.game.task.Stackable;
import com.venenatis.game.task.Walkable;

public class DuelNotificationTask extends ScheduledTask {

	private final Player player;
	private int time = 20;

	public DuelNotificationTask(Player player) {
		super(player, 1, true, Walkable.NON_WALKABLE, Stackable.NON_STACKABLE);
		this.player = player;
	}

	@Override
	public void execute() {
		if (!player.getDuelArena().isInSession()) {
			stop();
			return;
		}

		if (time <= 0) {
			stop();
			return;
		}

		time--;

		if (time % 2 == 0) {
			player.getActionSender().sendConfig(655, 1);
		} else {
			player.getActionSender().sendConfig(655, 0);
		}

	}

	@Override
	public void onStop() {
		player.getActionSender().sendConfig(655, 0);
	}

}