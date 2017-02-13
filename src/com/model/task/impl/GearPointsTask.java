package com.model.task.impl;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.task.ScheduledTask;

public class GearPointsTask extends ScheduledTask {
	
	public GearPointsTask() {
		super(300);
	}

	@Override
	public void execute() {
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null) {
				player.setGearPoints(2500);
				player.write(new SendMessagePacket("<img=12>[@red@Server@bla@]: Your Gear Points refill to @blu@2500@bla@. Spend them at a Legends Guard."));
			}
		}
	}

}
