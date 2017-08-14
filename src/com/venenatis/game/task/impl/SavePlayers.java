package com.venenatis.game.task.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;
import com.venenatis.server.GameEngine;

public class SavePlayers extends Task {

	public SavePlayers() {
		super(120);
	}

	@Override
	public void execute() {
		for (Player p2 : World.getWorld().getPlayers()) {
			if (p2 != null) {
				//PlayerSerialization.saveGame(p2);
				GameEngine.loginMgr.requestSave(p2);
			}
		}
	}

}
