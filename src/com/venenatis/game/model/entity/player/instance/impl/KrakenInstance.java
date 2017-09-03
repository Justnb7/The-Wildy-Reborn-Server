package com.venenatis.game.model.entity.player.instance.impl;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.instance.InstancedAreaManager;
import com.venenatis.game.model.entity.player.instance.SingleInstancedArea;
import com.venenatis.game.task.Task;
import com.venenatis.server.Server;


/**
 * @author Patrick van Elderen
 */
public class KrakenInstance {
	
	/**
	 * The kraken instance
	 */
	private SingleInstancedArea instance;
	
	public NPC[] npcs;
	
	/**
	 * Begin the kraken instance
	 * @param player
	 */
	public void start(Player player) {
		instance = (SingleInstancedArea) InstancedAreaManager.getSingleton().createSingleInstancedArea(player, Boundary.KRAKEN);
		player.setTeleportTarget(new Location(3696, 5798, instance.getHeight()));
		startUp(player);
	}
	
	/**
	 * Spawn the kraken and whirlpools when entering the instance.
	 * @param player
	 */
	public void startUp(Player player) {
		if (player != null && instance != null) {
			npcs = new NPC[5];
			npcs[0] = Server.npcHandler.spawn(player, 496, new Location(3694, 5810, instance.getHeight()), 0, false, false);
			npcs[1] = Server.npcHandler.spawn(player, 5534, new Location(3691, 5810, instance.getHeight()), 0, false, false);
			npcs[2] = Server.npcHandler.spawn(player, 5534, new Location(3691, 5814, instance.getHeight()), 0, false, false);
			npcs[3] = Server.npcHandler.spawn(player, 5534, new Location(3700, 5814, instance.getHeight()), 0, false, false);
			npcs[4] = Server.npcHandler.spawn(player, 5534, new Location(3700, 5810, instance.getHeight()), 0, false, false);
		}
	}
	
	/**
	 * Respawn the wave.
	 * @param player
	 */
	public void spawnNextWave(Player player) { 
		npcs[0] = Server.npcHandler.spawn(player, 496, new Location(3694, 5810, instance.getHeight()), 0, false, false);
		npcs[1] = Server.npcHandler.spawn(player, 5534, new Location(3691, 5810, instance.getHeight()), 0, false, false);
		npcs[2] = Server.npcHandler.spawn(player, 5534, new Location(3691, 5814, instance.getHeight()), 0, false, false);
		npcs[3] = Server.npcHandler.spawn(player, 5534, new Location(3700, 5814, instance.getHeight()), 0, false, false);
		npcs[4] = Server.npcHandler.spawn(player, 5534, new Location(3700, 5810, instance.getHeight()), 0, false, false);
		for (NPC n : npcs) {
			n.setVisible(false);
		}
		Server.getTaskScheduler().schedule(new Task(56) { 
			@Override
			public void execute() {
				if (player != null && instance != null) {
					for (NPC n : npcs) {
						if (n.getId() == 5534) {
							//MINIONS
							n.setVisible(true);
						}
					}
				}
				this.stop();
			}
		});
		Server.getTaskScheduler().schedule(new Task(57) { 
			@Override
			public void execute() {
				if (player != null && instance != null) {
					for (NPC n : npcs) {
						if (n.getId() == 496) {
							n.setVisible(true);
						}
					}
				}
				this.stop();
			}
		});
	}
	
	/**
	 * get the instance
	 * @return the instance
	 */
	public SingleInstancedArea getInstance() {
		return instance;
	}

}
