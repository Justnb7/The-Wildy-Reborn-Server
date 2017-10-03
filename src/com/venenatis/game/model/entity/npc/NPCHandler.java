package com.venenatis.game.model.entity.npc;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.World;

public final class NPCHandler {
	
	public NPC spawn(Player player, int id, Location spawn, int direction, boolean attacksEnemy, boolean hasHeadIcon) {
		NPC npc = new NPC(id, spawn, direction);
		
		npc.spawnDirection = direction;
		npc.getWalkingQueue().lastDirectionFaced = direction;
		npc.spawnedBy = player;
		npc.face(player.getLocation());
		if (attacksEnemy) {
			if (player != null) {
				npc.targetId = player.getIndex();
			}
		}
		if (hasHeadIcon) {
			player.getActionSender().drawHeadIcon(1, npc.getIndex(), 0, 0);
		}
		World.getWorld().register(npc);
		return npc;
	}
}