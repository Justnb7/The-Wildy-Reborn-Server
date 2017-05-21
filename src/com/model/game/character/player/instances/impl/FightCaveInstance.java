package com.model.game.character.player.instances.impl;

import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.instances.InstancedAreaManager;
import com.model.game.character.player.instances.SingleInstancedArea;
import com.model.game.location.Location;

public class FightCaveInstance {
	
	private SingleInstancedArea instance;
	
	public void start(Player player) {
		instance = (SingleInstancedArea) InstancedAreaManager.getSingleton().createSingleInstancedArea(player, Boundary.FIGHT_CAVE);
		startUp(player);
	}

	public void startUp(Player player) {
		if (player != null && instance != null) {
			player.movePlayer(new Location(2413, 5117, instance.getHeight()));
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
