package com.model.task.impl;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;

public class InstanceFloorReset extends ScheduledTask {

	public InstanceFloorReset() {
		super(4000);
	}

	@Override
	public void execute() {
		for (Player player : World.getWorld().getPlayers())
			if(player != null) {
				player.instanceFloorReset();
			}
	}
}