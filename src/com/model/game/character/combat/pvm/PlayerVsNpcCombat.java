package com.model.game.character.combat.pvm;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.ProjectilePathFinder;
import com.model.game.character.player.instances.impl.KrakenInstance;
import com.model.game.character.player.minigames.warriors_guild.WarriorsGuild;
import com.model.game.character.walking.PathFinder;
import com.model.game.item.container.impl.Equipment;
import com.model.game.location.Location;
import com.model.task.ScheduledTask;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * Handles Player Vs Npc combat
 * 
 * @author Sanity
 * @author Mobster
 * @author Patrick van Elderen
 */
public class PlayerVsNpcCombat {
	
	public static boolean isWearingSpear(Player player) {
		String weapon = ItemDefinition.forId(player.getEquipment().getId(Equipment.WEAPON_SLOT)).getName().toLowerCase();
		if (weapon.contains("spear") || weapon.contains("hasta"))
			return true;
		return false;
	}
	
	public static void kraken(Player player, NPC npc, int damage) {
		System.out.println("ok");
		if (npc.getId() == 5534 && npc.transformId != 5535) {
			npc.transforming = true;
			npc.playAnimation(Animation.create(3860));
			npc.requestTransform(5535);
			npc.aggressive = true;
			npc.setHitpoints(120);//reset hp when disturbed
			npc.setHitpoints(npc.getHitpoints() - damage);

			Server.getTaskScheduler().schedule(new ScheduledTask(3) {
				
				@Override
				public void execute() {
					npc.transforming = false; //enable attacking 3 cycles later
					this.stop();
				}
			});
		}
		if (npc.getId() == 496 && npc.transformId != 494) { // small whirlpools of Cave_krakens
			npc.transforming = true;
			npc.playAnimation(Animation.create(7135));
			npc.requestTransform(494);
			npc.aggressive = true;
			npc.setHitpoints(255);//reset hp when disturbed
			npc.setHitpoints(npc.getHitpoints() - damage);

			Server.getTaskScheduler().schedule(new ScheduledTask(3) {
				
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

			Server.getTaskScheduler().schedule(new ScheduledTask(3) {
				
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
			PathFinder.getPathFinder().findRoute(player, npc.absX, npc.absY, true, 1, 1);
		}
		//player.write(new SendGameMessage("debug");
		return false;
	}

	public static boolean canAttackNpc(Player player, NPC npc) {
		if (npc.isDead || npc.getMaxHitpoints() <= 0 || player.isDead()) {
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
		
		if (npc.getId() == 2463 || npc.getId() == 2464) {
			if (Boundary.isIn(player, WarriorsGuild.CYCLOPS_BOUNDARY)) {
				if (!player.getWarriorsGuild().isActive()) {
					player.getActionSender().sendMessage("You cannot attack a cyclops without talking to kamfreena.");
					Combat.resetCombat(player);
					return false;
				}
			}
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

		if (Combat.incombat(player) && player.lastAttacker != npc && !player.getArea().inMulti() && !Boundary.isIn(player, Boundary.KRAKEN)) {
			Combat.resetCombat(player);
			player.getActionSender().sendMessage("I am already under attack.");
			return false;
		}

		if (npc.spawnedBy != player.getIndex() && npc.spawnedBy > 0) {
			Combat.resetCombat(player);
			player.getActionSender().sendMessage("This monster was not spawned for you.");
			return false;
		}

		if (!player.getController().canAttackNPC(player)) {
			//System.out.println("blocked");
			return false;
		}
		// Otherwise, we're good to go!
		return true;
	}

	public static void moveOutFromUnderLargeNpc(Player player, NPC npc) {

		boolean inside = false;
		boolean projectiles = player.getCombatType() != CombatStyle.MELEE;
		for (Location tile : npc.getTiles()) {
			if (player.absX == tile.getX() && player.absY == tile.getY()) {
				inside = true;
				break;
			}
		}

		if (inside) {
			double lowDist = 99;
			int lowX = 0;
			int lowY = 0;
			int z = npc.heightLevel;
			int x2 = npc.getX();
			int y2 = npc.getY();
			int x3 = x2;
			int y3 = y2 - 1;
			boolean ignoreClip = npc.getId() == 494 || npc.getId() == 5535 || npc.getId() == 5534 || npc.getId() == 492 || npc.getId() == 493 || npc.getId() == 496;

			for (int k = 0; k < 4; k++) {
				for (int i = 0; i < npc.getSize() - (k == 0 ? 1 : 0); i++) {
					if (k == 0) {
						x3++;
					} else if (k == 1) {
						if (i == 0) {
							x3++;
						}
						y3++;
					} else if (k == 2) {
						if (i == 0) {
							y3++;
						}
						x3--;
					} else if (k == 3) {
						if (i == 0) {
							x3--;
						}
						y3--;
					}

					Location location = new Location(x3, y3, z);
					double d = location.distance(player.getLocation());
					if (d < lowDist) {
						if (ignoreClip || !projectiles || projectiles
								&& ProjectilePathFinder.isProjectilePathClear(location, npc.getLocation())) {
							if (ignoreClip || projectiles || !projectiles
									&& ProjectilePathFinder.isInteractionPathClear(location, npc.getLocation())) {
								lowDist = d;
								lowX = x3;
								lowY = y3;
							}
						}
					}
				}
			}

			if (lowX > 0 && lowY > 0) {
				player.getPlayerFollowing().playerWalk(lowX, lowY);
			}
		}
	}

	public static boolean inDistance(Player player, NPC npc) {
		boolean hasDistance = npc.getId() == 5535 ? true : false; // force 5535 tents to always be hittable
		for (Location pos : npc.getTiles()) {
			double distance = pos.distance(player.getLocation());
			if(Combat.usingHalberd(player) && distance <= 2) {
				hasDistance = true;
				break;
			}
			if (player.getCombatType() == CombatStyle.MELEE) {
				if (distance <= 1) {
					hasDistance = true;
					break;
				}
			} else {
				if (distance <= (player.getCombatType() == CombatStyle.RANGE ? 10 : 15)) {
					hasDistance = true;
					break;
				}
			}
		}

		if (hasDistance) {
			player.getMovementHandler().stopMovement();
		} else {
			//player.write(new SendGameMessage("No fucking distance?");
			return false;
		}
		return true;
	}
}