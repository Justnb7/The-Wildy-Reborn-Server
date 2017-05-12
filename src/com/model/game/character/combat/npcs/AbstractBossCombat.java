package com.model.game.character.combat.npcs;

import com.model.game.character.Entity;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;

/**
 * An abstract bossing system.
 * @author Patrick van Elderen
 * @version 1.3
 * @date Feb, 23-2-2016
 * @edited 19-3-2017
 */
public abstract class AbstractBossCombat {
	
	protected int npcId;
	
	public AbstractBossCombat(int npcId) {
		this.npcId = npcId;
	}
	
	public abstract void execute(Entity attacker, Entity victim);
	
	public abstract int distance(Entity attacker);
	
	public abstract void dropLoot(Player player, NPC npc);
	
}
