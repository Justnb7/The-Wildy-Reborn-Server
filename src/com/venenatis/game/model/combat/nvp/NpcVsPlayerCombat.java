package com.venenatis.game.model.combat.nvp;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.combat.npcs.AbstractBossCombat;
import com.venenatis.game.model.combat.pvm.PlayerVsNpcCombat;
import com.venenatis.game.model.definitions.WeaponDefinition;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.Hit;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.pathfinder.ProjectilePathFinder;

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
	 *            The {@link NPC} to handle combat timers for
	 */
	public static void handleCombatTimer(NPC npc) {
		//npc.forceChat("attacktimer: "+npc.attackTimer+" "+npc.walkingHome+" "+npc.targetId);
		
		boolean isBoss = AbstractBossCombat.isBoss(npc.getId());

		// Delay before we can attack again
		if (npc.getCombatState().getAttackDelay() > 0) {
			npc.getCombatState().decreaseAttackDelay(1);
			//npc.forceChat("atk timer: "+npc.attackTimer+" "+npc.walkingHome+" "+npc.randomWalk);
		}
		
		if(!isBoss) {
			if (npc.getCombatState().getAttackDelay() == 1) {
				executeDamage(npc);
			}
		}

		// If we havent been attacked within last 5 secs reset who last attack us
		if (System.currentTimeMillis() - npc.lastDamageTaken > 5000) {
			npc.underAttackBy = 0;
		}

		// Call code to attack our target if we're alive
		if (!npc.getCombatState().isDead() && !npc.walkingHome && npc.targetId > 0) {
			Player player = World.getWorld().getPlayers().get(npc.targetId);

			if (npc.followTarget != player)
				npc.followTarget = player;

			if (player == null) {
				// out of range
				npc.targetId = 0;
				npc.underAttack = false;
				npc.resetFace();
			} else {
				if (npc.getCombatState().getAttackDelay() == 0) {
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
	 *            The {@link NPC} attacking the player
	 */
	public static void attackPlayer(Player player, NPC npc) {
		if (npc != null) {
			if (npc.getCombatState().isDead()) {
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
				
				boolean isBoss = AbstractBossCombat.isBoss(npc.getId());
				AbstractBossCombat boss_cb = AbstractBossCombat.get(npc.getId());
				if (isBoss) {
					boss_cb.execute(npc, player);
					// don't do any code below this, boss script handles all.
				} else {
					npc.getCombatState().setAttackDelay(npc.getDefinition().getAttackSpeed());
					npc.playAnimation(Animation.create(npc.getAttackAnimation()));
				}
				player.lastAttacker = npc;
				player.lastWasHitTime = System.currentTimeMillis();
				npc.oldIndex = player.getIndex();
				player.updateLastCombatAction();
				player.setInCombat(true);
				player.getActionSender().removeAllInterfaces();
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
	 *            The {@link NPC} attacking the player
	 * @return If the npc can attack the player
	 */
	private static boolean validateAttack(Player player, NPC npc) {
		if (npc.getCombatState().isDead() || player.getCombatState().isDead()) {
			return false;
		}
		
		if (npc.getId() == 6617) {
			return false;
		}
		if (!player.isVisible()) {
			return false;
		}
		if (npc.getId() != 5535 && npc.getId() != 494) { // small tent and kraken can attack in single
			if (!npc.inMulti() && npc.underAttackBy > 0 && npc.underAttackBy != player.getIndex()) {
				npc.targetId = 0;
				return false;
			}
			if (!npc.inMulti()) {
				if ((npc.lastAttacker != player && Combat.hitRecently(npc, 4000)) || npc != player.lastAttacker && Combat.hitRecently(player, 4000)) {
					npc.targetId = 0;
					return false;
				}
			}
		}
		/*
		 * This doesn't work.
		 */
		if (npc.getZ() != player.getZ()) {
			npc.targetId = 0;
			return false;
		}

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
		if (npc.getCombatType() != CombatStyle.MELEE) {
			for (Location pos : npc.getBorder()) {
				if (ProjectilePathFinder.isProjectilePathClear(player.getLocation(), pos)) {
					return true;
				}
			}
		} else {
			for (Location pos : npc.getBorder()) {
				if (ProjectilePathFinder.isInteractionPathClear(player.getLocation(), pos)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Applies damage to the target, for non boss characters.
	 * 
	 * @param npc
	 *            The {@link NPC} attacking the player
	 */
	public static void executeDamage(NPC npc) {
		
		if (npc != null) {

			Player player = World.getWorld().getPlayers().get(npc.oldIndex);

			if (npc.getCombatState().isDead() || player == null || player.getCombatState().isDead()) {
				return;
			}
			// Autoretal
			if (player.getCombatState().noTarget()) {
				if (player.isAutoRetaliating()) {
					player.getCombatState().setTarget(npc);
				}
			}
			// block anim
			if (player.getCombatState().getAttackDelay() <= 3 || player.getCombatState().getAttackDelay() == 0) {
				//tried to make a instance didnt work ether
				player.playAnimation(Animation.create(WeaponDefinition.sendBlockAnimation(player)));
			}

			int damage = Utility.getRandom(npc.getDefinition().getMaxHit());
			// Set up a Hit instance
            Hit hitInfo = player.take_hit(npc, damage, npc.getCombatType(), false, false);

            // apply damage - call this, change the 'delay' param to whatever you want the delay to be
            // and the method submits the Event
            Combat.hitEvent(npc, player, 1, hitInfo, npc.getCombatType());
		}
	}

}