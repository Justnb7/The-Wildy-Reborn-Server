package com.model.game.character.combat.pvm;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.combat_data.CombatData;
import com.model.game.character.combat.combat_data.CombatExperience;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.magic.MagicCalculations;
import com.model.game.character.combat.nvp.NPCCombatData;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.ProjectilePathFinder;
import com.model.game.character.player.Skills;
import com.model.game.character.player.instances.impl.KrakenInstance;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.walking.PathFinder;
import com.model.game.location.Position;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

/**
 * Handles Player Vs Npc combat
 * 
 * @author Sanity
 * @author Mobster
 *
 */
public class PlayerVsNpcCombat {
	
	public static boolean isWearingSpear(Player player) {
		String weapon = player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase();
		if (weapon.contains("spear") || weapon.contains("hasta"))
			return true;
		return false;
	}


	/**
	 * Applys magic damage to the {@link Npc}
	 * 
	 * @param player
	 *            The {@link Player} attacking the npc
	 * @param npc
	 *            The {@link Npc} being attacked
	 */
	public static void applyNpcMagicDamage(Player player, Npc npc) {
		int damage = 0;
		player.usingMagic = true;
        damage = MagicCalculations.magicMaxHitModifier(player);
        
        CombatExperience.handleCombatExperience(player, damage, CombatType.MAGIC);
        
		if (player.getCombat().godSpells()) {
			if (System.currentTimeMillis() - player.godSpellDelay < 300000) {
				damage += Utility.getRandom(10);
			}
		}

		
		if (npc.npcId == 5535) {
			damage = 0;
		}
		
		boolean magicFailed = false;
		if (!CombatFormulae.getAccuracy(player, npc, 2, 1.0)) {
			damage = 0;
			magicFailed = true;
		} else if (npc.npcId == 2265 || npc.npcId == 2266) {
			player.write(new SendMessagePacket("The dagannoth is currently resistant to that attack!"));
			magicFailed = true;
			return;
		} else if (player.playerEquipment[player.getEquipment().getWeaponId()] == 11907 || player.playerEquipment[player.getEquipment().getWeaponId()] == 12899) {
            Utility.getRandom(npc.getDefinition().getMagicDefence());
		}
		
		if (npc.currentHealth - damage < 0) {
			damage = npc.currentHealth;
		}

		if (player.getCombat().getEndGfxHeight() == 100 && !magicFailed) { // end GFX
			npc.playGraphics(Graphic.create(player.MAGIC_SPELLS[player.oldSpellId][5], 0, 100));
			if (npc.attackTimer < 5)
				npc.playAnimation(Animation.create(NPCCombatData.getNPCBlockAnimation(npc)));
		} else if (!magicFailed) {
			npc.playGraphics(Graphic.create(player.MAGIC_SPELLS[player.oldSpellId][5], 0, 0));
		}

		if (magicFailed) {
			if (npc.attackTimer < 5) {
				npc.playAnimation(Animation.create(NPCCombatData.getNPCBlockAnimation(npc)));
			}
			npc.playGraphics(Graphic.create(85, 0, 100));
		}
		if (!magicFailed) {
			int freezeDelay = player.getCombat().getFreezeTime();// freeze
			if (freezeDelay > 0 && npc.refreezeTicks == 0) {
				npc.freeze(freezeDelay);
				System.out.println("Freeze timer: "+npc.refreezeTicks+ " Freezedelay: "+freezeDelay);
			}
			switch (player.MAGIC_SPELLS[player.oldSpellId][0]) {
			case 12901:
			case 12919: // blood spells
			case 12911:
			case 12929:
				int heal = Utility.getRandom(damage / 2);
				if (player.getSkills().getLevel(Skills.HITPOINTS) + heal >= player.getSkills().getLevelForExperience(Skills.HITPOINTS)) {
					player.getSkills().setLevel(Skills.HITPOINTS, player.getSkills().getLevelForExperience(Skills.HITPOINTS));
				} else {
					player.getSkills().setLevel(Skills.HITPOINTS, player.getSkills().getLevel(Skills.HITPOINTS) + heal);
				}
				break;
			}
		}
		npc.retaliate(player);
		if (MagicCalculations.magicMaxHitModifier(player) != 0) {
			npc.addDamageReceived(player.getName(), damage);
			npc.damage(new Hit(damage));
		}
		npc.updateRequired = true;
		player.usingMagic = false;
		player.castingMagic = false;
		player.oldSpellId = 0;
	}

	
	public static void kraken(Player player, Npc npc, int damage) {
		
		if (npc.npcId == 5534 && npc.transformId != 5535) {
			npc.transforming = true;
			npc.playAnimation(Animation.create(3860));
			npc.requestTransform(5535);
			npc.aggressive = true;
			npc.currentHealth = 120;//reset hp when disturbed
			npc.currentHealth -= damage;

			Server.getTaskScheduler().schedule(new ScheduledTask(3) {
				
				@Override
				public void execute() {
					npc.transforming = false; //enable attacking 3 cycles later
					this.stop();
				}
			});
		}
		if (npc.npcId == 496 && npc.transformId != 494) { // small whirlpools of Cave_krakens
			npc.transforming = true;
			npc.playAnimation(Animation.create(7135));
			npc.requestTransform(494);
			npc.aggressive = true;
			npc.currentHealth = 255;//reset hp when disturbed
			npc.currentHealth -= damage;

			Server.getTaskScheduler().schedule(new ScheduledTask(3) {
				
				@Override
				public void execute() {
					npc.transforming = false; //enable attacking 3 cycles later
					this.stop();
				}
			});
		}
		
		//Cave kraken - NPCID = 492 // whirlpool (lvl 127) -> 493
		if (npc.npcId == 493 && npc.transformId != 492) { // small whirlpools of Cave_krakens
			npc.transforming = true;
			npc.playAnimation(Animation.create(7135));
			npc.requestTransform(492);
			
			npc.currentHealth = 125;//reset hp when disturbed
			npc.currentHealth -= damage;

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
	 * Validates if the {@link Player} can attack the {@link Npc}
	 * 
	 * @param player
	 *            The {@link Player} attacking the npc
	 * @param npc
	 *            The {@link Npc} which is being attacked
	 * @return If the player can attack the npc
	 */
	public static boolean canTouch(Player player, Npc npc, boolean findpath) {
		boolean ignoreClip = npc.getId() == 494 || npc.getId() == 492 || npc.getId() == 493 || npc.getId() == 496 || npc.getId() == 5534 || npc.getId() == 5535 || npc.getId() == 2054 || npc.getId() == 5947;
		if (ignoreClip)
			return true;
		boolean projectile = player.getCombatType() == CombatType.RANGED || player.getCombatType() == CombatType.MAGIC;
		if (projectile) {
			for (Position pos : npc.getBorder()) {
				if (ProjectilePathFinder.isProjectilePathClear(player.getPosition(), pos)) {
					return true;
				}
			}
		} else {
			for (Position pos : npc.getBorder()) {
				if (ProjectilePathFinder.isInteractionPathClear(player.getPosition(), pos)) {
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

	public static boolean canAttackNpc(Player player, Npc npc) {
		if (npc.transforming)
			return false;
		if (NPCHandler.isArmadylNpc(npc.getIndex()) && player.getCombatType() == CombatType.MELEE) {
			player.write(new SendMessagePacket("You can only use range against this."));
			Combat.resetCombat(player);
			return false;
		}
		if ((npc.npcId == 6611 || npc.npcId == 6612) && npc.dogs > 0) {
			Combat.resetCombat(player);
			player.write(new SendMessagePacket("You must vanquish Vet'ions dogs."));
			return false;
		}

		if (npc.npcId == 496 && npc.transformId != 494) {
			KrakenInstance i = player.getKraken();
			if (i != null && i.npcs != null && i.npcs[0] == npc) {
				for (Npc n : i.npcs) {
					if (n.npcId == 5534) {
						player.write(new SendMessagePacket("You can't disturb the kraken while the whirlpools are undisturbed."));
						Combat.resetCombat(player);
						return false;
					}
				}
			}
		}

		if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS) && !Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS)) {
			Combat.resetCombat(player);
			player.write(new SendMessagePacket("You cannot attack that npc from outside the room."));
			return false;
		}
		if (npc.underAttackBy > 0 && npc.underAttackBy != player.getIndex() && !npc.inMulti()) {
			player.getCombat().reset();
			player.write(new SendMessagePacket("This monster is already in combat."));
			return false;
		}

		if ((player.underAttackBy > 0 || player.underAttackBy2 > 0) && player.underAttackBy2 != npc.getIndex() && !player.getArea().inMulti() && !Boundary.isIn(player, Boundary.KRAKEN)) {
			Combat.resetCombat(player);
			player.write(new SendMessagePacket("I am already under attack."));
			return false;
		}

		if (!player.getCombat().goodSlayer(npc.npcId)) {
			Combat.resetCombat(player);
			return false;
		}
		if (npc.spawnedBy != player.getIndex() && npc.spawnedBy > 0) {
			Combat.resetCombat(player);
			player.write(new SendMessagePacket("This monster was not spawned for you."));
			return false;
		}

		if (!player.getController().canAttackNPC(player)) {
			//System.out.println("blocked");
			return false;
		}
		// Otherwise, we're good to go!
		return true;
	}

	public static void moveOutFromUnderLargeNpc(Player player, Npc npc) {

		boolean inside = false;
		boolean projectiles = player.getCombatType() != CombatType.MELEE;
		for (Position tile : npc.getTiles()) {
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

					Position location = new Position(x3, y3, z);
					double d = location.distance(player.getPosition());
					if (d < lowDist) {
						if (ignoreClip || !projectiles || projectiles
								&& ProjectilePathFinder.isProjectilePathClear(location, npc.getPosition())) {
							if (ignoreClip || projectiles || !projectiles
									&& ProjectilePathFinder.isInteractionPathClear(location, npc.getPosition())) {
								lowDist = d;
								lowX = x3;
								lowY = y3;
							}
						}
					}
				}
			}

			if (lowX > 0 && lowY > 0) {
				player.getPA().playerWalk(lowX, lowY);
			}
			/*
			 * int r = Misc.random(3); switch (r) { case 0:
			 * player.getPA().walkTo(0, -1); return; case 1:
			 * player.getPA().walkTo(0, 1); return; case 2:
			 * player.getPA().walkTo(1, 0); return; case 3:
			 * player.getPA().walkTo(-1, 0); return; }
			 */

		}
	}

	public static boolean inDistance(Player player, Npc npc) {
		boolean hasDistance = npc.npcId == 5535 ? true : false; // force 5535 tents to always be hittable
		for (Position pos : npc.getTiles()) {
			double distance = pos.distance(player.getPosition());
			if(CombatData.usingHalberd(player)) {
				if(distance <= 2) {
					hasDistance = true;
					break;
				}
			}
			if (player.getCombatType() == CombatType.MELEE) {
				if (distance <= 1) {
					hasDistance = true;
					break;
				}
			} else {
				if (distance <= (player.getCombatType() == CombatType.RANGED ? 10 : 15)) {
					hasDistance = true;
					break;
				}
			}
		}

		if (hasDistance) {
			player.stopMovement();
		} else {
			//player.write(new SendGameMessage("No fucking distance?");
			return false;
		}
		return true;
	}
}