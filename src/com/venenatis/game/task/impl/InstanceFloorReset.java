package com.venenatis.game.task.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.ScheduledTask;
import com.venenatis.game.world.World;

public class InstanceFloorReset extends ScheduledTask {

	public InstanceFloorReset() {
		super(4000);
	}

	@Override
	public void execute() {
		for (Player player : World.getWorld().getPlayers())
			if (player != null) {
				player.instanceFloorReset();
			}
	}
}