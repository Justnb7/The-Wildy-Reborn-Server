package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.character.Animation;
import com.model.game.character.Graphic;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.player.Player;

public class Growler extends Boss {

	public Growler(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		int nX = npc.getX();
		int nY = npc.getY();
		int pX = player.getX();
		int pY = player.getY();
		int offX = (nY - pY) * -1;
		int offY = (nX - pX) * -1;

		int distance = npc.distanceTo(player.getX(), player.getY());

		int speed = 75 + (distance * 5);

		boolean d = !player.goodDistance(npc.absX, npc.absY, player.getX(), player.getY(), 2);
		if (!d) {
			npc.playAnimation(Animation.create(7036));
			npc.attackStyle = 0;
		} else if (d) {
			npc.playAnimation(Animation.create(7037));
			npc.playGraphics(Graphic.create(1182, 0, 0));
			player.getProjectile().createPlayersProjectile(nX, nY, offX, offY, 50, speed, 1183, 10, 10, -player.getId() - 1, 65, 0, 36);
			npc.attackStyle = 2;
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
		return npc.attackStyle == 0 ? 7036 : 7037;
	}

	@Override
	public int getAttackDelay(Npc npc) {
		return npc.getDefinition().getAttackSpeed();
	}

	@Override
	public int getHitDelay(Npc npc) {
		return npc.attackStyle == 0 ? 2 : 4;
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
		return 2;
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