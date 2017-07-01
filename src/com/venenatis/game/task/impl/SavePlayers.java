package com.venenatis.game.task.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.save.PlayerSave;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;

public class SavePlayers extends Task {

	public SavePlayers() {
		super(120);
	}

	@Override
	public void execute() {
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null) {
				//PlayerSerialization.saveGame(player);
				PlayerSave.save(player);
			}
		}
	}

}
