package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.character.Animation;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.npc.combat.MobAttackType;
import com.model.game.character.npc.combat.ProtectionPrayer;
import com.model.game.character.player.Player;
import com.model.utility.Utility;

public class Barrelchest extends Boss {
	
	/**
	 * The max damage each attack can deal
	 * Range max hit 30
	 * Melee max is 35, which is his main attack
	 */
	public static final int MAX_HIT_MELEE = 35, MAX_HIT_RANGE = 30;
	
	/**
	 * Barrelchest minion id
	 */
	//private final static int MINIONID = 7497;
	
	/**
	 * Barrelchests animations
	 */
	private final Animation MELEE_ATTACK_ANIMATION = Animation.create(5894);
	private final Animation SPECIAL_ATTACK_ANIMATION = Animation.create(5895);
	private final Animation RANGE_ATTACK_ANIMATION = Animation.create(5896);
	
	public Barrelchest(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		int special = Utility.random(10);
		if (player.distanceToPoint(npc.absX, npc.absY) < 2) {
			npc.attackStyle = MobAttackType.MELEE;
			//player.write(new SendMessagePacket("[Debug] Melee distance"));
		} else if (player.distanceToPoint(npc.absX, npc.absY) > 5) {
			//player.write(new SendMessagePacket("[Debug] Range distance"));
			npc.attackStyle = MobAttackType.RANGE;
		}
		switch (npc.attackStyle) {
		case MobAttackType.MELEE:
			npc.playAnimation(MELEE_ATTACK_ANIMATION);
			//player.write(new SendMessagePacket("[Debug] Melee attack."));
			break;
		case MobAttackType.RANGE:
			npc.playAnimation(RANGE_ATTACK_ANIMATION);
			//player.write(new SendMessagePacket("[Debug] Range attack."));
			int nX = npc.getX();
			int nY = npc.getY();
			int pX = player.getX();
			int pY = player.getY();
			int offX = (nY - pY) * -1;
			int offY = (nX - pX) * -1;

			int distance = npc.distanceTo(player.getX(), player.getY());

			int speed = 55 + (distance * 5);

			player.getProjectile().createPlayersProjectile(nX, nY, offX, offY, 50, speed, 1340, 53, 33, -player.getId() - 1, 45, 15, 36);
			break;
		}
		if(special >= 4 && special <= 6) {
			special_attack(player, npc);
		}
	}

	@Override
	public int getProtectionDamage(ProtectionPrayer protectionPrayer, int damage) {
		switch (protectionPrayer) {
		case MELEE:
			return 0;
		case RANGE:
			return 0;
		case MAGE:
			break;
		default:
			break;

		}
		return damage;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return attackType == 0 ? MAX_HIT_MELEE : MAX_HIT_RANGE;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		return npc.attackStyle == 0 ? MELEE_ATTACK_ANIMATION.getId() : npc.attackStyle == 1 ? RANGE_ATTACK_ANIMATION.getId() : SPECIAL_ATTACK_ANIMATION.getId();
	}

	@Override
	public int getAttackDelay(Npc npc) {
		return 5;
	}

	@Override
	public int getHitDelay(Npc npc) {
		return npc.attackStyle == 0 ? 2 : npc.attackStyle == 1 ? 4 : 5;
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
		return 20;
	}

	@Override
	public int offSet(Npc npc) {
		return 2;
	}

	@Override
	public boolean damageUsesOwnImplementation() {
		return false;
	}
	
	private final void special_attack(Player player, Npc npc) {
		npc.playAnimation(SPECIAL_ATTACK_ANIMATION);
		npc.forceChat("PROTECTION PRAYERS ARE USELESS NOW!");
		npc.forcedChatRequired = true;
		npc.updateRequired = true;
		npc.attackStyle = MobAttackType.SPECIAL_ATTACK;
		if (player.distanceToPoint(npc.absX, npc.absY) < 2) {
			//player.write(new SendMessagePacket("[Debug]Special melee distance"));
		} else if (player.distanceToPoint(npc.absX, npc.absY) > 5) {
			//player.write(new SendMessagePacket("[Debug]Special range distance"));
			int offX = (npc.getY() - player.getX()) * -1;
			int offY = (npc.getX() - player.getY()) * -1;
			int speed = 55 + (npc.distanceTo(player.getX(), player.getY() * 5));
			player.getProjectile().createPlayersProjectile(npc.getX(), npc.getY(), offX, offY, 50, speed, 1340, 53, 33, -player.getId() - 1, 45, 15, 36);
		}
	}

}
