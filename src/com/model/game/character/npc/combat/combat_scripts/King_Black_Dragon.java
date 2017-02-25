package com.model.game.character.npc.combat.combat_scripts;

import java.util.Random;

import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.npc.combat.MobAttackType;
import com.model.game.character.player.Player;

public class King_Black_Dragon extends Boss {
	
	private static Random r = new Random();

	public King_Black_Dragon(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		int attack = r.nextInt(20);
		int nX = npc.getX() + 2;
		int nY = npc.getY() + 2;
		int pX = player.getX();
		int pY = player.getY();
		int offX = (nY - pY) * -1;
		int offY = (nX - pX) * -1;
		
		if (npc.distanceToPoint(player.absX, player.absY) > 2) {
			attack = 9 + r.nextInt(11);
		}
		
		if (attack < 10) {
			npc.attackStyle = MobAttackType.MELEE;
		} else {
			int attackStyle = r.nextInt(100);
			if (attackStyle >= 0 && attackStyle < 25) {
				npc.attackStyle = MobAttackType.DRAGON_FIRE;
				player.getProjectile().createPlayersProjectile(nX, nY, offX, offY, 50, 80, 393, 43, 33, -player.getId() - 1, 50, 0, 36);
			}
			if (attackStyle >= 25 && attackStyle < 50) {
				player.getProjectile().createPlayersProjectile(nX, nY, offX, offY, 50, 80, 394, 43, 33, -player.getId() - 1, 50, 15, 36);
				npc.attackStyle = MobAttackType.DRAGON_FIRE;
			}
			if (attackStyle > 50 && attackStyle < 75) {
				player.getProjectile().createPlayersProjectile(nX, nY, offX, offY, 50, 80, 395, 43, 33, -player.getId() - 1, 50, 15, 36);
				npc.attackStyle = MobAttackType.DRAGON_FIRE;
			}
			if (attackStyle >= 75 && attackStyle <= 100) {
				player.getProjectile().createPlayersProjectile(nX, nY, offX, offY, 50, 80, 396, 43, 33, -player.getId() - 1, 50, 15, 36);
				npc.attackStyle = MobAttackType.DRAGON_FIRE;
			}
		}
		
	}

	@Override
	public int getProtectionDamage(Player player, int damage) {
		if (player.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
			return 0;
		}
		return damage;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return attackType == 0 ? 25 : 65;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		return npc.attackStyle == 0 ? r.nextInt(1) == 0 ? 91 : 80 : 81;
	}

	@Override
	public int getAttackDelay(Npc npc) {
		return npc.getDefinition().getAttackSpeed();
	}

	@Override
	public int getHitDelay(Npc npc) {
		return npc.attackStyle == 0 ? 2 : 3;
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
		return 8;
	}

	@Override
	public int offSet(Npc npc) {
		return 2;
	}

	@Override
	public boolean damageUsesOwnImplementation() {
		return false;
	}
}
