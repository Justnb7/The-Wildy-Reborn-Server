package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.npc.combat.ProtectionPrayer;
import com.model.game.character.player.Player;

public class Zombie_Champion extends Boss {

	public Zombie_Champion(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getProtectionDamage(ProtectionPrayer protectionPrayer, int damage) {
		//Damage will be decreased by 50% when using protection prayers.
		return damage / 2;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return 0;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		return 0;
	}

	@Override
	public int getAttackDelay(Npc npc) {
		return 0;
	}

	@Override
	public int getHitDelay(Npc npc) {
		return 0;
	}

	@Override
	public boolean canMultiAttack(Npc npc) {
		//Range and magic attacks will hit multiple targets.
		if(npc.attackStyle == 1 || npc.attackStyle == 2) {
			return true;
		}
		return false;
	}

	@Override
	public boolean switchesAttackers() {
		return true;
	}

	@Override
	public int distanceRequired(Npc npc) {
		return 10;
	}

	@Override
	public int offSet(Npc npc) {
		return 0;
	}

	@Override
	public boolean damageUsesOwnImplementation() {
		return false;
	}

}
