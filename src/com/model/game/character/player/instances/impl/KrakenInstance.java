package com.model.game.character.player.instances.impl;

import com.model.Server;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.instances.InstancedAreaManager;
import com.model.game.character.player.instances.SingleInstancedArea;
import com.model.game.location.Location;
import com.model.task.ScheduledTask;




/**
 * @author Patrick van Elderen
 */

public class KrakenInstance {
	
	/**
	 * The kraken instance
	 */

	private SingleInstancedArea instance;
	
	public Npc[] npcs;
	
	/**
	 * Begin the kraken instance
	 * @param player
	 */

	public void start(Player player) {
		instance = (SingleInstancedArea) InstancedAreaManager.getSingleton().createSingleInstancedArea(player, Boundary.KRAKEN);
		player.getPA().movePlayer(new Location(3696, 5798, instance.getHeight()));
		startUp(player);
	}
	
	/**
	 * Spawn the kraken and whirlpools when entering the instance.
	 * @param player
	 */
	public void startUp(Player player) {
		if (player != null && instance != null) {
			npcs = new Npc[5];
			//Player player, int id, int x, int y, int heightLevel, int walkingType, int health, int maxHit, int attackBonus, int meleeDefence, int rangeDefence, int magicDefence, boolean attacksEnemy, boolean hasHeadIcon
			npcs[0] = NPCHandler.spawnNpc(player, 496, 3694, 5810, instance.getHeight(), 0, 255, 0, 0, 0, 0, 0, false, false);
			npcs[1] = NPCHandler.spawnNpc(player, 5534, 3691, 5810, instance.getHeight(), 0, 120, 0, 0, 0, 0, 0, false, false);
			npcs[2] = NPCHandler.spawnNpc(player, 5534, 3691, 5814, instance.getHeight(), 0, 120, 0, 0, 0, 0, 0, false, false);
			npcs[3] = NPCHandler.spawnNpc(player, 5534, 3700, 5814, instance.getHeight(), 0, 120, 0, 0, 0, 0, 0, false, false);
			npcs[4] = NPCHandler.spawnNpc(player, 5534, 3700, 5810, instance.getHeight(), 0, 120, 0, 0, 0, 0, 0, false, false);
		}
	}
	
	/**
	 * Respawn the wave.
	 * @param player
	 */
	public void spawnNextWave(Player player) { 
		npcs[0] = NPCHandler.spawnNpc(player, 496, 3694, 5810, instance.getHeight(), 0, 255, 0, 0, 0, 0, 0, false, false);
		npcs[1] = NPCHandler.spawnNpc(player, 5534, 3691, 5810, instance.getHeight(), 0, 120, 0, 0, 0, 0, 0, false, false);
		npcs[2] = NPCHandler.spawnNpc(player, 5534, 3691, 5814, instance.getHeight(), 0, 120, 0, 0, 0, 0, 0, false, false);
		npcs[3] = NPCHandler.spawnNpc(player, 5534, 3700, 5814, instance.getHeight(), 0, 120, 0, 0, 0, 0, 0, false, false);
		npcs[4] = NPCHandler.spawnNpc(player, 5534, 3700, 5810, instance.getHeight(), 0, 120, 0, 0, 0, 0, 0, false, false);
		for (Npc n : npcs) {
			n.setVisible(false);
		}
		Server.getTaskScheduler().schedule(new ScheduledTask(56) { 
			@Override
			public void execute() {
				if (player != null && instance != null) {
					for (Npc n : npcs) {
						if (n.npcId == 5534) {
							//MINIONS
							n.setVisible(true);
						}
					}
				}
				this.stop();
			}
		});
		Server.getTaskScheduler().schedule(new ScheduledTask(57) { 
			@Override
			public void execute() {
				if (player != null && instance != null) {
					for (Npc n : npcs) {
						if (n.npcId == 496) {
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
