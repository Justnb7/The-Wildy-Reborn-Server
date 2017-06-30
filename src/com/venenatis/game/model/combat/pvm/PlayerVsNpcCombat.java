package com.venenatis.game.model.combat.pvm;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.location.Area;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.instance.impl.KrakenInstance;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.pathfinder.ProjectilePathFinder;
import com.venenatis.game.world.pathfinder.RouteFinder;
import com.venenatis.server.Server;

/**
 * Handles Player Vs Npc combat
 * 
 * @author Sanity
 * @author Mobster
 * @author Patrick van Elderen
 */
public class PlayerVsNpcCombat {
	
	public static boolean isWearingSpear(Player player) {
		
		String weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getName().toLowerCase();
		if (weapon.contains("spear") || weapon.contains("hasta"))
			return true;
		return false;
	}
	
	public static void kraken(Player player, NPC npc, int damage) {
		if (npc.getId() == 5534 && npc.transformId != 5535) {
			npc.transforming = true;
			npc.playAnimation(Animation.create(3860));
			npc.requestTransform(5535);
			npc.aggressive = true;
			npc.setHitpoints(120);//reset hp when disturbed
			npc.setHitpoints(npc.getHitpoints() - damage);

			Server.getTaskScheduler().schedule(new Task(3) {
				
				@Override
				public void execute() {
					npc.transforming = false; //enable attacking 3 cycles later
					this.stop();
				}
			});
		}
		if (npc.getId() == 496 && npc.transformId != 494) { // big whirlpools of Kraken
			npc.transforming = true;
			npc.playAnimation(Animation.create(7135));
			npc.requestTransform(494);
			npc.aggressive = true;
			npc.setHitpoints(255);//reset hp when disturbed
			npc.setHitpoints(npc.getHitpoints() - damage);

			Server.getTaskScheduler().schedule(new Task(3) {
				
				@Override
				public void execute() {
					npc.transforming = false; //enable attacking 3 cycles later
					this.stop();
				}
			});
		}
		
		//Cave kraken - NPCID = 492 // whirlpool (lvl 127) -> 493
		if (npc.getId() == 493 && npc.transformId != 492) { // small whirlpools of Cave_krakens
			npc.transforming = true;
			npc.playAnimation(Animation.create(7135));
			npc.requestTransform(492);
			
			npc.setHitpoints(125);//reset hp when disturbed
			npc.setHitpoints(npc.getHitpoints() - damage);

			Server.getTaskScheduler().schedule(new Task(3) {
				
				@Override
				public void execute() {
					npc.transforming = false; //enable attacking 3 cycles later
					this.stop();
				}
			});
		}
		
	}

	/**
	 * Validates if the {@link Player} can attack the {@link NPC}
	 * 
	 * @param player
	 *            The {@link Player} attacking the npc
	 * @param npc
	 *            The {@link NPC} which is being attacked
	 * @return If the player can attack the npc
	 */
	public static boolean canTouch(Player player, NPC npc, boolean findpath) {
		boolean ignoreClip = npc.getId() >= 1739 && npc.getId() <= 1742 || npc.getId() == 494 || npc.getId() == 492 || npc.getId() == 493 || npc.getId() == 496
				|| npc.getId() == 5534 || npc.getId() == 5535 || npc.getId() == 2054 || npc.getId() == 5947;
		if (ignoreClip)
			return true;
		boolean projectile = player.getCombatType() == CombatStyle.RANGE || player.getCombatType() == CombatStyle.MAGIC;
		if (projectile) {
			for (Location pos : npc.getBorder()) {
				if (ProjectilePathFinder.isProjectilePathClear(player.getLocation(), pos)) {
					return true;
				}
			}
		} else {
			for (Location pos : npc.getBorder()) {
				if (ProjectilePathFinder.isInteractionPathClear(player.getLocation(), pos)) {
					//player.write(new SendGameMessage("debug");
					return true;
				}
			}
		}

		if (findpath) {
			RouteFinder.getPathFinder().findRoute(player, npc.getX(), npc.getY(), true, 1, 1);
		}
		//player.write(new SendGameMessage("debug");
		return false;
	}

	public static boolean canAttackNpc(Player player, NPC npc) {
		if (npc.isDead() || npc.getMaxHitpoints() <= 0 || player.isDead()) {
			player.getCombatState().reset();
			return false;
		}
		if (npc.transforming)
			return false;
		
		//TODO ask Jak how to do thiss
		/*if(!Slayer.canAttack(player, npc)) {
			player.debug("is it cuz of own stupidity?");
			return false;
		}*/
		
		if (npc.isArmadylNpc() && player.getCombatType() == CombatStyle.MELEE) {
			player.getActionSender().sendMessage("You can only use range or magic against this npc.");
			Combat.resetCombat(player);
			return false;
		}
		
		if ((npc.getId() == 6611 || npc.getId() == 6612) && npc.dogs > 0) {
			Combat.resetCombat(player);
			player.getActionSender().sendMessage("You must vanquish Vet'ions dogs.");
			return false;
		}

		if (npc.getId() == 496 && npc.transformId != 494) {
			KrakenInstance i = player.getKraken();
			if (i != null && i.npcs != null && i.npcs[0] == npc) {
				for (NPC n : i.npcs) {
					if (n.getId() == 5534) {
						player.getActionSender().sendMessage("You can't disturb the kraken while the whirlpools are undisturbed.");
						Combat.resetCombat(player);
						return false;
					}
				}
			}
		}

		if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS) && !Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS)) {
			Combat.resetCombat(player);
			player.getActionSender().sendMessage("You cannot attack that npc from outside the room.");
			return false;
		}
		if (npc.underAttackBy > 0 && npc.underAttackBy != player.getIndex() && !npc.inMulti()) {
			player.getCombatState().reset();
			player.getActionSender().sendMessage("This monster is already in combat.");
			return false;
		}

		if (Combat.incombat(player) && player.lastAttacker != npc && !Area.inMultiCombatZone(player) && !Boundary.isIn(player, Boundary.KRAKEN)) {
			Combat.resetCombat(player);
			player.getActionSender().sendMessage("I am already under attack.");
			return false;
		}

		if (npc.spawnedBy != player.getIndex() && npc.spawnedBy > 0) {
			Combat.resetCombat(player);
			player.getActionSender().sendMessage("This monster was not spawned for you.");
			return false;
		}

		if (!player.getController().canAttackNPC()) {
			//System.out.println("blocked");
			return false;
		}
		// Otherwise, we're good to go!
		return true;
	}

}