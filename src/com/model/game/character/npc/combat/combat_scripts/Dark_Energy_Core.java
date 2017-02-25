package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.character.npc.Npc;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.player.Player;
import com.model.utility.Utility;

public class Dark_Energy_Core extends Boss {

	public Dark_Energy_Core(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		npc.attackStyle = 0;
		int damage = Utility.getRandom(13);
		if (damage > 0 && !player.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
			for (Npc corpNpc : NPCHandler.getNpcsById(Corporeal_Beast.CORPOREAL_BEAST_ID)) {
				corpNpc.currentHealth += damage;
				if (corpNpc.currentHealth > corpNpc.maximumHealth) {
					corpNpc.currentHealth = corpNpc.maximumHealth;
				}
			}
		}
	}

	@Override
	public int getProtectionDamage(Player player, int damage) {
		return 0;
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
		return false;
	}

	@Override
	public boolean switchesAttackers() {
		return false;
	}

	@Override
	public int distanceRequired(Npc npc) {
		return 0;
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
