package com.model.task.impl;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.serialize.PlayerSerialization;
import com.model.task.ScheduledTask;

public class SavePlayers extends ScheduledTask {

	public SavePlayers() {
		super(120);
	}

	@Override
	public void execute() {
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null) {
				//PlayerSerialization.saveGame(player);
				PlayerSerialization.save(player);
			}
		}
	}

}
