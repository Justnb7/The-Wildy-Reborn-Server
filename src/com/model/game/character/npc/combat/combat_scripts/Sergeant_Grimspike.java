package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.player.Player;

public class Sergeant_Grimspike extends Boss {

	public Sergeant_Grimspike(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		npc.attackStyle = 1;
		int offX = (npc.getY() - player.getY()) * -1;
		int offY = (npc.getX() - player.getX()) * -1;
		int distance = npc.distanceTo(player.getX(), player.getY());
		int speed = 75 + (distance * 5);
		player.getProjectile().createPlayersProjectile(npc.getX(), npc.getY(), offX, offY, 50, speed, 1220, 43, 31, -player.getId() - 1, 65, 0, 36);
	}

	@Override
	public int getProtectionDamage(Player player, int damage) {
		if (player.isActivePrayer(Prayers.PROTECT_FROM_MISSILE)) {
			return 0;
		}
		return damage;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return 21;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		return npc.getDefinition().getAttackAnimation();
	}

	@Override
	public int getAttackDelay(Npc npc) {
		return npc.getDefinition().getAttackSpeed();
	}

	@Override
	public int getHitDelay(Npc npc) {
		return 4;
	}
	
	@Override
	public int offSet(Npc npc) {
		return 0;
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
		return 6;
	}


	@Override
	public boolean damageUsesOwnImplementation() {
		return false;
	}
}
