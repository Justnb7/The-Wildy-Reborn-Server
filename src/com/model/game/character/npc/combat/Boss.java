package com.model.game.character.npc.combat;

import com.model.game.character.Entity;

/**
 * An abstract bossing system.
 * @author Patrick van Elderen
 * @version 1.3
 * @date Feb, 23-2-2016
 * @edited 19-3-2017
 */
public abstract class Boss {
	
	protected int npcId;
	
	public Boss(int npcId) {
		this.npcId = npcId;
	}
	
	public abstract void execute(Entity attacker, Entity victim);
	
	public abstract int distance();
	
}
