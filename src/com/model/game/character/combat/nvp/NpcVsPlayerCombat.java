package com.model.game.character.combat.nvp;

import java.util.List;

import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.combat_data.CombatAnimation;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.pvm.PlayerVsNpcCombat;
import com.model.game.character.npc.Npc;
import com.model.game.character.npc.combat.Boss;
import com.model.game.character.npc.combat.Bosses;
import com.model.game.character.npc.combat.MobAttackType;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.ProjectilePathFinder;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.music.sounds.MobAttackSounds;
import com.model.game.character.player.content.music.sounds.PlayerSounds;
import com.model.game.location.Position;
import com.model.utility.Utility;

/**
 * Handles all Npc Vs Player combat methods
 * 
 * @author Mobster
 *
 */
public class NpcVsPlayerCombat {

	/**
	 * Handles the npcs combat timers
	 * 
	 * @param npc
	 *            The {@link Npc} to handle combat timers for
	 */
	public static void handleCombatTimer(Npc npc) {
		npc.forceChat("attacktimre: "+npc.attackTimer+" "+npc.walkingHome+" "+npc.targetId);
		// TODO PI old system for making damage show up -> REMOVE ALL THIS CODE!! your new way of doing it is world.scheduler().submit(task() { target.damage(50) });
		if (npc.hitDelayTimer > 0) {
			npc.hitDelayTimer--;
		}

		boolean isBoss = Bosses.isBoss(npc.npcId);
		
		if(!isBoss) {
			if (npc.hitDelayTimer == 1) {
				npc.hitDelayTimer = 0;
				executeDamage(npc);
			}
		}

		// Delay before we can attack again
		if (npc.attackTimer > 0) {
			npc.attackTimer--;
			// npc.forceChat("atk timer: "+npc.attackTimer+" "+npc.walkingHome+"
			// "+npc.randomWalk);
		}

		// If we havent been attacked within last 5 secs reset who last attack us
		if (System.currentTimeMillis() - npc.lastDamageTaken > 5000) {
			npc.underAttackBy = 0;
		}

		// Call code to attack our target if we're alive
		if (!npc.isDead && !npc.walkingHome && npc.targetId > 0) {
			Player player = World.getWorld().getPlayers().get(npc.targetId);

			if (npc.followTarget != player)
				npc.followTarget = player;

			if (player == null) {
				// out of range
				npc.targetId = 0;
				npc.underAttack = false;
				npc.resetFace();
			} else {
				if (npc.attackTimer == 0) {
					NpcVsPlayerCombat.attackPlayer(player, npc);
				}
				// Following called in process()
			}
		}
	}

	/**
	 * Handles an npc attacking a player
	 * 
	 * @param player
	 *            The {@link Player} being attacked
	 * @param npc
	 *            The {@link Npc} attacking the player
	 */
	public static void attackPlayer(Player player, Npc npc) {
		if (npc != null) {
			if (npc.isDead) {
				return;
			}
			if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS)) {
				if (!Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS)) {
					npc.targetId = 0;
					npc.underAttack = false;
					return;
				}
			}
			if (!validateAttack(player, npc)) {
				return;
			}
			
			npc.faceEntity(player);
			
			if (goodDistance(npc.getX(), npc.getY(), player.getX(), player.getY(),
					NPCCombatData.distanceRequired(npc))) {
				npc.randomWalk = false;
				
				npc.attackStyle = 0;
				boolean isBoss = Bosses.isBoss(npc.npcId);
				Boss boss_cb = Bosses.get(npc.npcId);
				if (isBoss) {
					boss_cb.execute(npc, player);
					// don't do any code below this, boss script handles all.
					return;
				}
				npc.attackTimer = npc.getDefinition().getAttackSpeed();

				if (npc.attackStyle == 3) {
					npc.hitDelayTimer += 2;
				}

				if (npc.projectileId > 0) {
					int nX = npc.getX();
					int nY = npc.getY();
					int pX = player.getX();
					int pY = player.getY();
					int offX = (nX - pX) * -1;
					int offY = (nY - pY) * -1;
					player.getProjectile().createPlayersProjectile(nX, nY, offX, offY, 50,
							NPCCombatData.getProjectileSpeed(npc), npc.projectileId,
							NPCCombatData.getProjectileStartHeight(npc.npcId, npc.projectileId),
							NPCCombatData.getProjectileEndHeight(npc.npcId, npc.projectileId), -player.getId() - 1, 65);
				}
				player.underAttackBy2 = npc.getIndex();
				player.singleCombatDelay2.reset();
				npc.oldIndex = player.getIndex();
				npc.playAnimation(Animation.create(NPCCombatData.getAttackEmote(npc)));
				player.updateLastCombatAction();
				player.setInCombat(true);
				player.getActionSender().sendRemoveInterfacePacket();
			}
		}
	}

	public static boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		return ((objectX - playerX <= distance && objectX - playerX >= -distance)
				&& (objectY - playerY <= distance && objectY - playerY >= -distance));
	}

	/**
	 * Checks if the attack is okay on the player
	 * 
	 * @param player
	 *            The {@link Player} being attacked
	 * @param npc
	 *            The {@link Npc} attacking the player
	 * @return If the npc can attack the player
	 */
	private static boolean validateAttack(Player player, Npc npc) {
		if (npc.isDead || player.isDead()) {
			return false;
		}
		
		if (npc.npcId == 6617) {
			return false;
		}
		if (!player.isVisible()) {
			return false;
		}
		if (npc.npcId != 5535 && npc.npcId != 494) { // small tent and kraken can attack in single
			if (!npc.inMulti() && npc.underAttackBy > 0 && npc.underAttackBy != player.getIndex()) {
				npc.targetId = 0;
				return false;
			}
			if (!npc.inMulti() && (player.underAttackBy > 0 || (player.underAttackBy2 > 0 && player.underAttackBy2 != npc.getIndex()))) {
				npc.targetId = 0;
				return false;
			}
		}
		/**
		 * This doesn't work.
		 */
		if (npc.heightLevel != player.heightLevel) {
			npc.targetId = 0;
			return false;
		}

		player.combatCountdown = 10;
		if (player.inTutorial()) {
			npc.targetId = 0;
			return false;
		}
		if (PlayerVsNpcCombat.canTouch(player, npc, false)) {
			return true;
		}

		boolean ignoreClip = npc.getId() == 494 || npc.getId() == 492 || npc.getId() == 5862 || npc.getId() == 493
				|| npc.getId() == 496 || npc.getId() == 2054 || npc.getId() == 5947;
		if (ignoreClip) {
			return true;
		}

		// Always last
		if (npc.getCombatType() != CombatType.MELEE) {
			for (Position pos : npc.getBorder()) {
				if (ProjectilePathFinder.isProjectilePathClear(player.getPosition(), pos)) {
					return true;
				}
			}
		} else {
			for (Position pos : npc.getBorder()) {
				if (ProjectilePathFinder.isInteractionPathClear(player.getPosition(), pos)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Applies damage to the target
	 * 
	 * @param npc
	 *            The {@link Npc} attacking the player
	 */
	public static void executeDamage(Npc npc) {
		if (npc != null) {
			if (World.getWorld().getPlayers().get(npc.oldIndex) == null) {
				return;
			}

			Player player = World.getWorld().getPlayers().get(npc.oldIndex);

			if (npc.isDead || player == null || player.isDead()) {
				return;
			}
			// Autoretal
			if (player.getCombat().noTarget()) {
				if (player.isAutoRetaliating()) {
					player.getCombat().setTarget(npc);
				}
			}
			// block anim
			if (player.attackDelay <= 3 || player.attackDelay == 0) {
				player.playAnimation(Animation.create(CombatAnimation.getDefendAnimation(player)));
			}

			int damage = 0;
			int secondDamage = -1;
			damage = Utility.getRandom(npc.getDefinition().getMaxHit());

			if (npc.attackStyle == MobAttackType.MELEE) {
				if (!(CombatFormulae.getAccuracy(npc, player, npc.attackStyle, 1.0))) {
					damage = 0;
				}
				if (player.isActivePrayer(Prayers.PROTECT_FROM_MELEE) && npc.npcId != 1677) {
					damage = 0;
				}
				if (player.playerEquipment[player.getEquipment().getShieldId()] == 12817) {
					if (Utility.getRandom(100) > 30 && damage > 0) {
						damage *= .75;
					}
				}
				if (player.getSkills().getLevel(Skills.HITPOINTS) - damage < 0) {
					damage = player.getSkills().getLevel(Skills.HITPOINTS);
				}
			}

			if (npc.attackStyle == MobAttackType.RANGE) {
				if (!(CombatFormulae.getAccuracy(npc, player, 1, 1.0))) {
					damage = 0;
				}
				// overheads check
				if (player.isActivePrayer(Prayers.PROTECT_FROM_MISSILE)) {
					damage = 0;
				}
			}

			if (npc.attackStyle == MobAttackType.MAGIC) {

				if (!(CombatFormulae.getAccuracy(npc, player, 2, 1.0))) {
					damage = 0;
				}

				boolean magicFailed = false;
				if (player.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
					damage = 0;
				}

				if (player.getSkills().getLevel(Skills.HITPOINTS) - damage < 0) {
					damage = player.getSkills().getLevel(Skills.HITPOINTS);
				}
				magicFailed = damage > 0 ? false : true;
				if (npc.endGfx > 0 && (!magicFailed)) {
					player.playGraphics(Graphic.create(npc.endGfx, 0, 0));
				} else {
					player.playGraphics(Graphic.create(85, 0, 0));
				}
			}
			if (npc.attackStyle == 3) {
				double dragonfireReduction = CombatFormulae.dragonfireReduction(player);
				if (dragonfireReduction > 0) {
					damage -= (damage * dragonfireReduction);
					if (damage < 0) {
						damage = 0;
					}
				}
			}

			// final graphic
			if (npc.endGfx > 0 && damage > 0) {
				if (npc.npcId == 2205 || npc.npcId == 319) {
					player.playGraphics(Graphic.create(npc.endGfx, 0, 0));
				} else {
					player.playGraphics(Graphic.create(npc.endGfx, 0, 100));
				}
			}

			npc.endGfx = 0;

			if (npc.npcId == 3116) {
				if (player.getSkills().getLevel(Skills.PRAYER) - 1 > 0)
					player.getSkills().setLevel(Skills.PRAYER, -1);
			}
			int poisonDamage = getPoisonDamage(npc);
			if (poisonDamage > 0 && player.isSusceptibleToPoison() && Utility.getRandom(10) == 1) {
				player.setPoisonDamage((byte) poisonDamage);
			}
			if (player.getSkills().getLevel(Skills.HITPOINTS) - damage < 0
					|| secondDamage > -1 && player.getSkills().getLevel(Skills.HITPOINTS) - secondDamage < 0) {
				damage = player.getSkills().getLevel(Skills.HITPOINTS);
				if (secondDamage > -1) {
					secondDamage = 0;
				}
			}
			player.logoutDelay.reset(); // logout delay
			if (player.hasVengeance()) {
				player.getCombat().vengeance(npc, damage, 1);
			}
			if (npc.npcId == 319 && npc.attackStyle == 5) {
				List<Player> localPlayers = Npc.getSurroundingPlayers(npc, 16);
				if (npc.getId() == 319) {
					for (Player players : localPlayers) {
						players.damage(new Hit(damage));
						if (players.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
							players.updateRequired = true;
						}
					}
					return;
				}
			}

			if (player.isTeleporting()) {
				return;
			}
			PlayerSounds.sendBlockOrHitSound(player, damage > 0);
			MobAttackSounds.sendAttackSound(player, npc.getId(), npc.attackStyle, damage > 0);
			player.damage(new Hit(damage));
			if (secondDamage > -1) {
				player.damage(new Hit(secondDamage));
			}
			if (player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
				player.updateRequired = true;
			}
		}
	}

	private static int getPoisonDamage(Npc npc) {
		switch (npc.npcId) {
		case 3129:
			return 16;
		}
		return 0;
	}
}