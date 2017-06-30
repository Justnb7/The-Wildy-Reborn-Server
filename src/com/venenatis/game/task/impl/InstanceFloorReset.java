package com.venenatis.game.task.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;

public class InstanceFloorReset extends Task {

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