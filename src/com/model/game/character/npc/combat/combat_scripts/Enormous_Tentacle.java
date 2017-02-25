package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.player.Player;

public class Enormous_Tentacle extends Boss {

	public Enormous_Tentacle(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		npc.attackStyle = 2;
		int nX = npc.getX();
		int nY = npc.getY();
		int pX = player.getX();
		int pY = player.getY();
		int offX = (nY - pY) * -1;
		int offY = (nX - pX) * -1;

		int distance = npc.distanceTo(player.getX(), player.getY());

		int speed = 75 + (distance * 5);

		player.getProjectile().createPlayersProjectile(nX, nY, offX, offY, 50, speed, 162, 53, 43, -player.getId() - 1, 65, 15, 36);

		npc.endGfx = 163;
	}

	@Override
	public int getProtectionDamage(Player player, int damage) {
		if (player.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
			return damage /2;
		}
		return damage;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return 2;
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
	public boolean canMultiAttack(Npc npc) {
		return false;
	}

	@Override
	public boolean switchesAttackers() {
		return false;
	}

	@Override
	public int distanceRequired(Npc npc) {
		return 20;
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
