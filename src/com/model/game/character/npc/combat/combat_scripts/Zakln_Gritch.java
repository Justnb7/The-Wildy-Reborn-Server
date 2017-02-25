package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.character.Graphic;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.player.Player;

public class Zakln_Gritch extends Boss {

	public Zakln_Gritch(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		npc.attackStyle = 1;
		npc.playGraphics(Graphic.create(1222, 0, 0));
		int offX = (npc.getY() - player.getY()) * -1;
		int offY = (npc.getX() - player.getX()) * -1;
		player.getProjectile().createPlayersProjectile(npc.getX() + 1, npc.getY() + 1, offX, offY, 50, 106, 1223, 31, 31, -player.getId() - 1, 76, 0);
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
