package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.player.Player;

public class Balfrug_Kreeyath extends Boss {

	public Balfrug_Kreeyath(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		boolean distance = !player.goodDistance(npc.absX, npc.absY, player.getX(), player.getY(), 2);
		if (!distance) {
			npc.attackStyle = 0;
		} else if (distance) {
			npc.attackStyle = 2;
			int offX = (npc.getY() - player.getY()) * -1;
			int offY = (npc.getX() - player.getX()) * -1;
			player.getProjectile().createPlayersProjectile(npc.getX() + 1, npc.getY() + 1, offX, offY, 50, 106, 1227, 31, 31, -player.getId() - 1, 76, 0);
		}
	}

	@Override
	public int getProtectionDamage(Player player, int damage) {
		if (player.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
			return 0;
		} else if (player.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
			return 0;
		}
		return damage;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return 16;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		return npc.attackStyle == 0 ? 64 : npc.getDefinition().getAttackAnimation();
	}

	@Override
	public int getAttackDelay(Npc npc) {
		return npc.getDefinition().getAttackSpeed();
	}

	@Override
	public int getHitDelay(Npc npc) {
		return npc.npcId == 0 ? 2 : 5;
	}

	@Override
	public boolean canMultiAttack(Npc npc) {
		return false;
	}

	@Override
	public boolean switchesAttackers() {
		return true;
	}

	@Override
	public int distanceRequired(Npc npc) {
		return 4;
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