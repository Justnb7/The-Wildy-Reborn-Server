package com.venenatis.game.model.entity.player.instance.impl;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.instance.InstancedAreaManager;
import com.venenatis.game.model.entity.player.instance.SingleInstancedArea;
import com.venenatis.server.Server;

/**
 * The jad combat instance, only one player can fight TzTok-Jad.
 * @author Patrick van Elderen
 *
 */
public class FightCaveInstance {
	
	/**
	 * The TzTok-Jad instance
	 */
	private SingleInstancedArea instance;
	
	public NPC jad;
	
	
	public void enter_cave(Player player) {
		instance = (SingleInstancedArea) InstancedAreaManager.getSingleton().createSingleInstancedArea(player, Boundary.FIGHT_CAVE);
		player.setTeleportTarget(new Location(2413, 5117, instance.getHeight()));
		fight(player);
	}
	
	
	public void fight(Player player) {
		if (player != null && instance != null) {
			player.getDialogueManager().start("ENTER_FIGHT_CAVE", player);
			// tp player to jad pls
			//player.setTeleportTarget(new Location(2401, 5085, instance.getHeight()));
			jad = Server.npcHandler.spawn(player, 3127, new Location(2401, 5085, instance.getHeight()), 1, true, false);
		}
	}
	
	public void reward(Player player) { 
		player.setTeleportTarget(new Location(2438, 5168, 0));
		player.getDialogueManager().start("WON_FIGHT_CAVE", player);
	}
	
	public void stop(Player player) {
		player.setTeleportTarget(Location.create(2438, 5168, 0));
		player.getDialogueManager().start("LEAVE_FIGHT_CAVE", player);
	}
	
	/**
	 * get the instance
	 * @return the instance
	 */
	public SingleInstancedArea getInstance() {
		return instance;
	}

}
