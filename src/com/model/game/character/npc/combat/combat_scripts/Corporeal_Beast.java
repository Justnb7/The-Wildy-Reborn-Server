package com.model.game.character.npc.combat.combat_scripts;

import com.model.game.character.Animation;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.npc.combat.MobAttackType;
import com.model.game.character.player.Player;
import com.model.task.events.CycleEvent;
import com.model.task.events.CycleEventContainer;
import com.model.task.events.CycleEventHandler;
import com.model.utility.Utility;

public class Corporeal_Beast extends Boss {

	public static final int CORPOREAL_BEAST_ID = 319;
	public static final int DARK_CORE_ID = 320;

	public static final int MAX_HIT_MELEE = 51;
	public static final int MAX_HIT_MAGIC = 65;

	public static final int MELEE_ANIMATION = 1682;
	public static final int MAGIC_ANIMATION = 1680;

	public static final int SMALL_MAGIC_PROJECTILE_ID = 314;
	public static final int REGULAR_MAGIC_PROJECTILE_ID = 315;
	public static final int BIG_MAGIC_PROJECTILE_ID = 316;

	public static final int SPLATTER_GFX_ID = 317;

	public static final int MAGIC_PROJECTILE_ID = 315;
	public static final int EXPLOSION_GFX_ID = 318;

	public static final int DARK_CORE_PROJECTILE_ID = 319;

	public Corporeal_Beast(int npcId) {
		super(npcId);
	}

	@Override
	public void execute(Npc npc, Player player) {
		
		npc.attackStyle = MobAttackType.MAGIC;

		if (player.distanceToPoint(npc.absX, npc.absY) < 3 && Utility.getRandom(3) == 0) {
			npc.attackStyle = MobAttackType.MELEE;
		}

		if (npc.attackStyle == MobAttackType.MELEE) {
			npc.playAnimation(Animation.create(MELEE_ANIMATION));
			npc.endGfx = EXPLOSION_GFX_ID;
		} else {
			int damage = Utility.getRandom(MAX_HIT_MAGIC);
			npc.playAnimation(Animation.create(MAGIC_ANIMATION));
			int style = Utility.getRandom(4);
			if (style == 0) {
				player.getProjectile().createNpcProjectile(BIG_MAGIC_PROJECTILE_ID, npc, 2, 3, 50);
			} else if (style == 1) {
				player.getProjectile().createNpcProjectile(REGULAR_MAGIC_PROJECTILE_ID, npc, 2, 3, 50);
				if (damage > 0) {
					createSplatter(player, npc);
				}
			} else {
				player.getProjectile().createNpcProjectile(SMALL_MAGIC_PROJECTILE_ID, npc, 2, 3, 50);
			}
		}
	}

	private void createSplatter(Player player, Npc npc) {
		int splatters = 4 + Utility.getRandom(3);
		for (int i = 0; i < splatters; i++) {
			createSplatterProjectile(player, -3 + Utility.getRandom(6), -3 + Utility.getRandom(6));
		}
	}

	private void createSplatterProjectile(Player player, int offsetX, int offsetY) {
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				player.getProjectile().createLocationPorjectile(REGULAR_MAGIC_PROJECTILE_ID, player.absX + offsetX, player.absY + offsetY, 0, 21);
				player.getProjectile().createPlayersStillGfx(SPLATTER_GFX_ID, player.absX + offsetX, player.absY + offsetY, 0, 21);
				container.stop();
			}
		}, 5);
	}

	@Override
	public int getProtectionDamage(Player player, int damage) {
		if (player.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
			return damage *=.1;
		} else if (player.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
			return damage *=.5;
		}
		return damage;
	}

	@Override
	public int getMaximumDamage(int attackType) {
		return attackType == 0 ? MAX_HIT_MELEE : MAX_HIT_MAGIC;
	}

	@Override
	public int getAttackEmote(Npc npc) {
		switch(npc.attackStyle) {
		case 2:
			return 1679;
		case 1: 
			return 1683;
		case 0:
			return 1682;
			default:
				return 1679;
		}
	}

	@Override
	public int getAttackDelay(Npc npc) {
		return npc.getDefinition().getAttackSpeed();
	}

	@Override
	public int getHitDelay(Npc npc) {
		if (npc.attackStyle == 0) {
			return 2;
		} else if (npc.attackStyle == 2) {
			return 4;
		} else if (npc.attackStyle == 5) {
			return 3;
		}
		return 2;
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
		return 7;
	}

	@Override
	public int offSet(Npc npc) {
		return 3;
	}

	@Override
	public boolean damageUsesOwnImplementation() {
		return false;
	}

}
