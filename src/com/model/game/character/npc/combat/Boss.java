package com.model.game.character.npc.combat;

import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;

/**
 * 
 * @author Patrick van Elderen
 * @version 1.2
 * @date Feb, 23-2-2016
 * @edited 5-7-2016
 */
public abstract class Boss {
	
	protected int npcId;
	
	public Boss(int npcId) {
		this.npcId = npcId;
	}
	
	public abstract void execute(Npc npc, Player player);
	
    public abstract int getProtectionDamage(Player player, int damage);
	
	public abstract int getMaximumDamage(int attackType);
	
    public abstract int getAttackEmote(Npc npc);

    public abstract int getAttackDelay(Npc npc);
	
	public abstract int getHitDelay(Npc npc);
	
    public abstract boolean canMultiAttack(Npc npc);
	
	public abstract boolean switchesAttackers();
	
	public abstract int distanceRequired(Npc npc);
	
	public abstract int offSet(Npc npc);
	
	public abstract boolean damageUsesOwnImplementation();

}
