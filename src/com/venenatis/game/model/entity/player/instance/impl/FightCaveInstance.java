package com.venenatis.game.model.entity.player.instance.impl;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.instance.InstancedAreaManager;
import com.venenatis.game.model.entity.player.instance.SingleInstancedArea;

public class FightCaveInstance {
	
	private SingleInstancedArea instance;
	
	public void start(Player player) {
		instance = (SingleInstancedArea) InstancedAreaManager.getSingleton().createSingleInstancedArea(player, Boundary.FIGHT_CAVE);
		startUp(player);
	}

	public void startUp(Player player) {
		if (player != null && instance != null) {
			player.setTeleportTarget(new Location(2413, 5117, instance.getHeight()));
			enterFightCaves(player);
		}
	}
	
	
	/**
	 * We're now entering the caves, we can start the waves
	 */
	private void enterFightCaves(Player player) {
		player.getActionSender().removeAllInterfaces();
		player.getActionSender().sendMessage("@red@Wave: 1");
		player.waveId = 0;
		player.getFightCave().startWave();
		player.dialogue().start("ENTER_FIGHT_CAVE");
	}

	/**
	 * get the instance
	 * @return the instance
	 */
	public SingleInstancedArea getInstance() {
		return instance;
	}
	
}
