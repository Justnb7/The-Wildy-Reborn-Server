package com.venenatis.game.task.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;

public class DuelNotificationTask extends Task {

	private final Player player;
	private int time = 20;

	public DuelNotificationTask(Player player) {
		super(player, 1, true, StackType.NEVER_STACK, BreakType.ON_MOVE);
		this.player = player;
	}

	@Override
	public void execute() {

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