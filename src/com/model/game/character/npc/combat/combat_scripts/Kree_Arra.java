package com.model.game.character.npc.combat.combat_scripts;

import java.util.Random;

import com.model.game.character.Animation;
import com.model.game.character.Graphic;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.player.Player;

public class Kree_Arra extends Boss {

	public Kree_Arra(int npcId) {
		super(npcId);
	}

	/**
	 * The random number generator.
	 */
	private final Random random = new Random();
	
	@Override
	public void execute(Npc npc, Player player) {
		int offX = (npc.getY() - player.getY()) * -1;
		int offY = (npc.getX() - player.getX()) * -1;
		switch(random.nextInt(5)) {
		case 0:
			npc.attackStyle = 0;	
			break;
		case 1:
		case 2:
			npc.attackStyle = 2;
			npc.playAnimation(Animation.create(6978));
			player.getProjectile().createPlayersProjectile(npc.getX() + 1, npc.getY() + 1, offX, offY, 50, 106, 1198,
					31, 31, -player.getId() - 1, 76, 0);
			player.playGraphics(Graphic.create(1196, 0, 100));
			break;
		default:
			npc.attackStyle = 1;
			player.getProjectile().createPlayersProjectile(npc.getX() + 1, npc.getY() + 1, offX, offY, 50, 106, 1199,
					31, 31, -player.getId() - 1, 76, 0);
			break;
		}
	}

	@Override
	public int getProtectionDamage(Player player, int damage) {
		if (player.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
			return 0;
		} else if (player.isActivePrayer(Prayers.PROTECT_FROM_MISSILE)) {
			return 0;
		} else if (player.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
			return 0;
		}
		return damage;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return attackType == 0 ? 26 : attackType == 1 ? 71 : 21;
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
		return 5;
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
