package com.model.game.character.combat.pvp;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Entity;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.combat_data.*;
import com.model.game.character.combat.effect.CombatEffect;
import com.model.game.character.combat.effect.impl.RingOfRecoil;
import com.model.game.character.combat.effect.impl.Venom;
import com.model.game.character.combat.magic.MagicCalculations;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.combat.nvp.NPCCombatData;
import com.model.game.character.combat.pvm.PlayerVsNpcCombat;
import com.model.game.character.combat.range.RangeData;
import com.model.game.character.combat.weaponSpecial.Special;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.*;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSessionRules.Rule;
import com.model.game.character.player.content.music.sounds.PlayerSounds;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.walking.PathFinder;
import com.model.game.item.Item;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

import java.util.Objects;

/**
 * Handles Player Vs Player Combat
 * 
 * @author Mobster
 * @author Sanity
 *
 */
public class PlayerVsPlayerCombat {

	/**
	 * Applies a melee hit to the opponent
	 * 
	 * @param player
	 *            The {@link Player} attacking the opponent
	 * @param target
	 *            The {@link Player} being attacked
	 * @param item
	 *            The {@link Item} being held by the player
	 */
	
	public static void applyPlayerHit(final Player player, Player target, Item item) {
		applyPlayerMeleeDamage(player, target, 1); 
	}
	
	private static int[] poisonous = {5698, 13267, 13269, 13271};
	
	public static void applyPlayerMeleeDamage(Player attacker, Player defender, int damageMask) {
		
		int damage = damageMask == 1 ? attacker.delayedDamage : damageMask == 2 ? attacker.delayedDamage2 : 0;
		
		CombatEffect.applyRandomEffect(attacker, defender, damage);
		if (damageMask == 1)
			attacker.delayedDamage = 0;
		else if (damageMask == 2)
			attacker.delayedDamage2 = 0;

		for (int i : poisonous) {
			if (attacker.playerEquipment[attacker.getEquipment().getWeaponId()] == i) {
				if (defender.isSusceptibleToPoison() && Utility.getRandom(4) == 0) {
					defender.setPoisonDamage((byte) 6);
				}
			}
		}
		
		if (!CombatFormulae.getAccuracy(attacker, defender, 0, 1.0)) {
			damage = 0;
		}
		
		if (attacker.playerEquipment[attacker.getEquipment().getShieldId()] == 12817) {
			if (Utility.getRandom(100) > 30 && damage > 0) {
				damage *= .75;
			}
		}

		if (defender.isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
			damage = damage * 60 / 100;
		}
		
		if (defender.getSkills().getLevel(Skills.HITPOINTS) - damage <= 0) {
			damage = defender.getSkills().getLevel(3);// this was it xd
		}
		if (defender.hasVengeance()) {
			defender.getCombat().vengeance(attacker, damage, 1);
		}
		if (damage > 0) {
			RingOfRecoil recoil = new RingOfRecoil();
			if (recoil.isExecutable(attacker)) {
				recoil.execute(defender, attacker, damage);
			}
			// c.getCombat().applySpiritShield(damage, i);
		}

		if (attacker.isTeleporting() || defender.isTeleporting()) {
			Combat.resetCombat(attacker);
			Combat.resetCombat(defender);;
			return;
		}

		CombatExperience.handleCombatExperience(attacker, damage, CombatType.MELEE);
		defender.putInCombat(attacker.getIndex());
		defender.killerId = attacker.getIndex();
		attacker.updateLastCombatAction();
		attacker.setInCombat(true);

		if (attacker.killedBy != defender.getIndex())
		attacker.killedBy = defender.getIndex();
		attacker.getCombat().applySmite(defender, damage);
		defender.addDamageReceived(attacker.getName(), damage);
		defender.damage(new Hit(damage));
		PlayerSounds.sendBlockOrHitSound(defender, damage > 0);
	}



	/**
	 * Applies the damage based on the provided combat type
	 * 
	 * @param attacker
	 *            The {@link Player} attacking the opponent
	 * @param defender
	 *            The {@link Player} being attacked
	 * @param combatType
	 *            The {@link CombatType} for the attack
	 * @param item
	 *            The {@link Item} the player is holding
	 */
	private static void applyCombatDamage(Player attacker, Player defender, CombatType combatType, Item item, int index) {
		if(item != null && attacker != null) {
			PlayerSounds.SendSoundPacketForId(attacker, attacker.isUsingSpecial(), item.getId());
		}
		if(defender.infection != 2 && attacker.getEquipment().canInfect(attacker)) {
			int inflictVenom = Utility.getRandom(5);
			//System.out.println("Venom roll: "+inflictVenom);
			if(inflictVenom == 0 && defender.isSusceptibleToVenom()) {
				new Venom(defender);
			}
		}
		switch (combatType) {
		case MAGIC:
			applyPlayerMagicDamage(attacker, defender);
			break;
		case MELEE:
			applyPlayerHit(attacker, defender, item);
			break;
		case RANGED:
			applyPlayerRangeDamage(attacker, defender, index);
			break;
		default:
			throw new IllegalArgumentException("Invalid Combat Type: " + combatType);

		}
	}

	/**
	 * Applies the ranged damage to the opponent
	 * 
	 * @param attacker
	 *            The {@link Player} applying the damage
	 * @param defender
	 *            The {@link Player} being attacked
	 */
	private static void applyPlayerRangeDamage(Player attacker, Player defender, int i) {
		int primairy_damage = Utility.getRandom(attacker.getCombat().calculateRangeMaxHit());
		int secondairy_damage = -1;
		
		if (attacker.lastWeaponUsed == 11235 || attacker.bowSpecShot == 1) {
			secondairy_damage = attacker.getCombat().calculateRangeMaxHit();
		}
		
		attacker.rangeEndGFX = RangeData.getRangeEndGFX(attacker);
		
		if (!attacker.hasAttribute("ignore defence") && !CombatFormulae.getAccuracy(attacker, defender, 1, 1.0)) {
			//System.out.println("range def");
			primairy_damage = 0;
		}
		
		if (attacker.getEquipment().isCrossbow(attacker)) {
			if (Utility.getRandom(10) == 1) {
				if (primairy_damage > 0) {
					switch (attacker.playerEquipment[attacker.getEquipment().getQuiverId()]) {
					case 9236: // Lucky Lightning
						defender.playGraphics(Graphic.create(749, 0, 0));
						break;
					case 9237: // Earth's Fury
						defender.playGraphics(Graphic.create(755, 0, 0));
						break;
					case 9238: // Sea Curse
						defender.playGraphics(Graphic.create(750, 0, 0));
						break;
					case 9239: // Down to Earth
						defender.playGraphics(Graphic.create(757, 0, 0));
						break;
					case 9240: // Clear Mind
						defender.playGraphics(Graphic.create(751, 0, 0));
						break;
					case 9241: // Magical Posion
						defender.playGraphics(Graphic.create(752, 0, 0));
						break;
					case 9242: // Blood Forfiet
						defender.playGraphics(Graphic.create(754, 0, 0));
						int selfDamage = (int) (attacker.getSkills().getLevel(Skills.HITPOINTS) * 0.1);
                        if (selfDamage < attacker.getSkills().getLevel(Skills.HITPOINTS)) {
                        	primairy_damage += defender.getSkills().getLevel(Skills.HITPOINTS) * 0.2;
                            attacker.damage(new Hit(selfDamage));
                        }
						break;
					case 9243: // Armour Piercing
						defender.playGraphics(Graphic.create(758, 0, 100));
						attacker.setAttribute("ignore defence", true);
						if (CombatFormulae.wearingFullVoid(attacker, 2)) {
							primairy_damage = Utility.random(45, 57);
						} else {
							primairy_damage = Utility.random(42, 51);
						}
						if (attacker.isActivePrayer(Prayers.EAGLE_EYE)) {
							primairy_damage *= 1.15;
						}
						break;
					case 9244: // Dragon's Breath
						defender.playGraphics(Graphic.create(756, 0, 0));
						if (CombatFormulae.wearingFullVoid(attacker, 2)) {
							primairy_damage = Utility.random(45, 57);
						} else {
							primairy_damage = Utility.random(42, 51);
						}
						if (attacker.isActivePrayer(Prayers.EAGLE_EYE)) {
							primairy_damage *= 1.15;
						}
						boolean fire = true;
                        int shield = defender.playerEquipment[defender.getEquipment().getShieldId()];
                        if (shield == 11283 || shield == 1540) {
                            fire = false;
                        }
                        if (fire) {
                        	if (CombatFormulae.wearingFullVoid(attacker, 2)) {
                        		primairy_damage = Utility.random(45, 57);
                            } else {
                            	primairy_damage = Utility.random(42, 51);
                            }
                        	if (attacker.isActivePrayer(Prayers.EAGLE_EYE)) {
                        		primairy_damage *= 1.15;
                            }
                            double protectionPrayer = defender.isActivePrayer(Prayers.EAGLE_EYE) ? 0.40 : 1;
                            if (protectionPrayer != 1) {
                                double protectionHit = primairy_damage * protectionPrayer; // +1 as its exclusive
                                primairy_damage -= protectionHit;
                                if (primairy_damage < 1)
                                	primairy_damage = 0;
                            }
                        }
						break;
					case 9245: // Life Leech
						defender.playGraphics(Graphic.create(753, 0, 0));
						if (CombatFormulae.wearingFullVoid(attacker, 2)) {
							primairy_damage = Utility.random(45, 57);
						} else {
							primairy_damage = Utility.random(42, 51);
						}
						if (attacker.isActivePrayer(Prayers.EAGLE_EYE)) {
							primairy_damage *= 1.15;
						}
						break;
					}
				}
			}
		}
		
		if (attacker.lastWeaponUsed == 11235 || attacker.bowSpecShot == 1) {
			if (!CombatFormulae.getAccuracy(attacker, defender, 1, 1.0))
				secondairy_damage = 0;
		}

		if (defender.isActivePrayer(Prayers.PROTECT_FROM_MISSILE)) {
			primairy_damage = primairy_damage * 60 / 100;
			if (attacker.lastWeaponUsed == 11235 || attacker.bowSpecShot == 1)
				secondairy_damage = secondairy_damage * 60 / 100;
		}

		if (attacker.dbowSpec) {
			defender.playGraphics(Graphic.create(attacker.playerEquipment[attacker.getEquipment().getQuiverId()] == 11212 ? 1100 : 1103, 0, 0));
			if (primairy_damage < 8)
				primairy_damage = 8;
			if (secondairy_damage < 8)
				secondairy_damage = 8;
			attacker.dbowSpec = false;
		}

		if (defender.getSkills().getLevel(Skills.HITPOINTS) - primairy_damage < 0) {
			defender.getSkills().setLevel(Skills.HITPOINTS, primairy_damage);
		}
		if (defender.getSkills().getLevel(Skills.HITPOINTS) - primairy_damage - secondairy_damage < 0) {
			defender.getSkills().setLevel(Skills.HITPOINTS, secondairy_damage = primairy_damage);
		}
		if (primairy_damage < 0)
			primairy_damage = 0;
		if (secondairy_damage < 0 && secondairy_damage != -1)
			secondairy_damage = 0;
		if (defender.hasVengeance()) {
			defender.getCombat().vengeance(attacker, primairy_damage, 1);
			attacker.getCombat().vengeance(defender, secondairy_damage, 1);
		}
		
		if (primairy_damage > 0) {
			RingOfRecoil recoil = new RingOfRecoil();
			if (recoil.isExecutable(attacker)) {
				recoil.execute(defender, attacker, primairy_damage);
			}
		}
		if (secondairy_damage > 0) {
			RingOfRecoil recoil = new RingOfRecoil();
			if (recoil.isExecutable(attacker)) {
				recoil.execute(defender, attacker, primairy_damage);
			}
		}
		CombatExperience.handleCombatExperience(attacker, primairy_damage, CombatType.RANGED);
		boolean dropArrows = true;
		
		if(attacker.lastWeaponUsed == 12926 || attacker.lastWeaponUsed == 4222) {
			dropArrows = false;
		}
		
		if (dropArrows) {
			attacker.getItems().dropArrowUnderTarget();
		}
		
		if (attacker.rangeEndGFX > 0 && !attacker.getCombat().usingBolts(attacker.playerEquipment[attacker.getEquipment().getQuiverId()])) {
			if (attacker.rangeEndGFXHeight) {
				defender.playGraphics(Graphic.create(attacker.rangeEndGFX, 0, 0));
			} else {
				defender.playGraphics(Graphic.create(attacker.rangeEndGFX, 0, 0));
			}
		}
		defender.putInCombat(attacker.getIndex());
		defender.killerId = attacker.getIndex();
		defender.addDamageReceived(attacker.getName(), primairy_damage);
		attacker.updateLastCombatAction();
		attacker.setInCombat(true);
		attacker.killedBy = defender.getIndex();
		defender.damage(new Hit(primairy_damage));
		attacker.setAttribute("ignore defence", false);
		if (secondairy_damage != -1) {
			defender.addDamageReceived(attacker.getName(), secondairy_damage);
			defender.damage(new Hit(secondairy_damage));
		}
		//World.getWorld().getPlayers().get(i).setHitUpdateRequired(true);	
		defender.updateRequired = true;
		attacker.getCombat().applySmite(defender, primairy_damage);
		if (secondairy_damage != -1)
			attacker.getCombat().applySmite(defender, secondairy_damage);
	}

	/**
	 * Applies the magic damage for the player
	 * 
	 * @param attacker
	 *            The {@link Player} applying the damage
	 * @param defender
	 *            The {@link Player} being attacked
	 */
	private static void applyPlayerMagicDamage(Player attacker, Player defender) {
		int damage = 0;

		damage = MagicCalculations.magicMaxHitModifier(attacker);

		if (attacker.getCombat().godSpells()) {
			if (System.currentTimeMillis() - attacker.godSpellDelay < 300000) {
				damage += 10;
			}
		}
		
		if (attacker.getSpellBook() == SpellBook.MODERN && (attacker.playerEquipment[attacker.getEquipment().getWeaponId()] == 2415 || attacker.playerEquipment[attacker.getEquipment().getWeaponId()] == 2416 || attacker.playerEquipment[attacker.getEquipment().getWeaponId()] == 2417)) {
			damage = 0;
			attacker.write(new SendMessagePacket("You must be on the modern spellbook to cast this spell."));
			return;
		}
		
		if (attacker.magicFailed)
			damage = 0;

		if (defender.isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
			damage = damage * 60 / 100;
		}
		if (defender.getSkills().getLevel(Skills.HITPOINTS) - damage < 0) {
			defender.getSkills().setLevel(Skills.HITPOINTS, damage);
		}
		if (defender.hasVengeance()) {
			defender.getCombat().vengeance(attacker, damage, 1);
		}
		if (damage > 0) {
			RingOfRecoil recoil = new RingOfRecoil();
			if (recoil.isExecutable(attacker)) {
				recoil.execute(defender, attacker, damage);
			}
		}
		CombatExperience.handleCombatExperience(attacker, damage, CombatType.MAGIC);

		if (attacker.getCombat().getEndGfxHeight() == 100 && !attacker.magicFailed) { // end GFX
			defender.playGraphics(Graphic.create(attacker.MAGIC_SPELLS[attacker.oldSpellId][5], 0, 100));
		} else if (!attacker.magicFailed) {
			defender.playGraphics(Graphic.create(attacker.MAGIC_SPELLS[attacker.oldSpellId][5], 0, 100));
		} else if (attacker.magicFailed) {
			defender.playGraphics(Graphic.create(85, 0, 100));
		}

		if (!attacker.magicFailed) {

			switch (attacker.MAGIC_SPELLS[attacker.oldSpellId][0]) {
			case 12445: // teleblock
				if (defender.teleblock.elapsed(defender.teleblockLength)) {
					defender.teleblock.reset();
					defender.write(new SendMessagePacket("You have been teleblocked."));
					defender.putInCombat(1);
					if (defender.isActivePrayer(Prayers.PROTECT_FROM_MAGIC))
						defender.teleblockLength = 150000;
					else
						defender.teleblockLength = 300000;
				}
				break;

			case 12901:
			case 12919: // blood spells
			case 12911:
			case 12929:
				int heal = damage / 4;
				if (attacker.getSkills().getLevel(Skills.HITPOINTS) + heal > attacker.getMaximumHealth()) {
					attacker.getSkills().setLevel(Skills.HITPOINTS, attacker.getMaximumHealth());
				} else {
					attacker.getSkills().setLevel(Skills.HITPOINTS, attacker.getSkills().getLevel(Skills.HITPOINTS) + heal);
				}
				break;

			}
		}
		
		defender.putInCombat(attacker.getIndex());
		defender.killerId = attacker.getIndex();
		attacker.updateLastCombatAction();
		attacker.setInCombat(true);
		if (MagicCalculations.magicMaxHitModifier(attacker) != 0 && !attacker.magicFailed) {
			defender.addDamageReceived(attacker.getName(), damage);
			defender.damage(new Hit(damage));
		}
		attacker.getCombat().applySmite(defender, damage);
		attacker.killedBy = defender.getIndex();
		defender.updateRequired = true;
		attacker.usingMagic = false;
		attacker.castingMagic = false;
		attacker.oldSpellId = 0;
	}

	/**
	 * Handles attacking a player
	 * 
	 * @param player
	 *            The {@link Player} attacking
	 */
	public static void attackPlayer(Player player) {
		if (player.getCombat().noTarget())
			return;

		Entity target = player.getCombat().target;
		Combat.setCombatStyle(player);

		if (target.isPlayer()) {
			Player ptarg = (Player)target;
			if (!validateAttack(player, ptarg)) {
				return;
			}
			if (ptarg.inTutorial())
				return;
		} else {
			Npc npc = (Npc)target;
			// Clip check first. Get line of sight.
			if (!PlayerVsNpcCombat.canTouch(player, npc, true)) {
				return;
			}

			if (npc.isDead || npc.maximumHealth <= 0 || player.isDead()) {
				player.usingMagic = false;
				player.faceEntity(player);
				player.npcIndex = 0;
				return;
			}
			// Can attack check
			if (!PlayerVsNpcCombat.canAttackNpc(player, npc)) {
				return;
			}
		}

		player.setFollowing(target);


		if (target.isPlayer()) {
			Player ptarg = (Player) target;
			player.getActionSender().sendString(ptarg.getName()+ "-"+player.getSkills().getLevelForExperience(Skills.HITPOINTS)+"-"+ptarg.getSkills().getLevel(Skills.HITPOINTS) + "-" + player.getName() , 35000);
		} else {
			Npc npc = (Npc) target;
			if (npc.npcId != 493 || npc.npcId != 496 || npc.npcId != 5534) {
				Player attacker = World.getWorld().PLAYERS.get(npc.underAttackBy);
				//System.out.println(Npc.getName(npc.npcType).replaceAll("_", " ") + " - "+ npc.maximumHealth +" - "+ npc.HP +" - "+ ((attacker != null) ? "-"+attacker.getUsername() : "null"));
				player.getActionSender().sendString(Npc.getName(npc.npcId).replaceAll("_", " ") + "-" + npc.maximumHealth + "-" + npc.currentHealth + ((attacker != null) ? "-" + attacker.getName() : ""), 35000);
			}
		}
		
		boolean sameSpot = player.getX() == target.getX() && player.getY() == target.getY();
		if (sameSpot) {
			if (player.frozen()) {
				Combat.resetCombat(player);
				return;
			}
			if (target.isPlayer())
				player.setFollowing(target);
			else
				player.getPA().walkTo(0, 1); // TODO following Npcs properly
			player.attackDelay = 0;
			return;
		}
		if (target.isNPC()) {
			PlayerVsNpcCombat.moveOutFromUnderLargeNpc(player, (Npc)target);
		}

		if (target.isPlayer()) {
			if (!player.getController().canAttackPlayer(player, (Player)target) && Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL) == null) {
				return;
			}
		}
		if (target.isNPC() && !PlayerVsNpcCombat.inDistance(player, (Npc)target)) {
			return;
		}

		if (player.attackDelay > 0) {
			// don't attack as our timer hasnt reached 0 yet
			return;
		}
		// ##### BEGIN ATTACK - WE'RE IN VALID DISTANCE AT THIS POINT #######
		/*
		 * Set our attack timer so we dont instantly hit again
		 */
		player.attackDelay = CombatData.getAttackDelay(player, player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase());

		/*
		 * Verify if we have the proper arrows/bolts
		 */
		if (player.getCombatType() == CombatType.RANGED) {
			if (!player.usingCross
					&& !player.throwingAxe
					&& !player.usingArrows
					&& (player.playerEquipment[player.getEquipment().getWeaponId()] < 4212 || player.playerEquipment[player.getEquipment().getWeaponId()] > 4223)
					&& player.playerEquipment[player.getEquipment().getWeaponId()] != 12926) {
				player.write(new SendMessagePacket("There is no ammo left in your quiver."));
				player.stopMovement();
				player.getCombat().reset();
				return;
			}

			if (player.getCombat().correctBowAndArrows() < player.playerEquipment[player.getEquipment().getQuiverId()] 
					&& player.usingBow
					&& !player.getEquipment().usingCrystalBow(player)
					&& !player.getEquipment().isCrossbow(player) && !player.getEquipment().wearingBlowpipe(player)) {
				player.write(new SendMessagePacket("You can't use " + player.getItems().getItemName(player.playerEquipment[player.getEquipment().getQuiverId()]).toLowerCase() + "s with a " + player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase() + "."));
				player.stopMovement();
				player.getCombat().reset();
				return;
			}
			if (player.getEquipment().isCrossbow(player) && !player.getCombat().properBolts()) {
				player.write(new SendMessagePacket("You must use bolts with a crossbow."));
				player.stopMovement();
				Combat.resetCombat(player);
				return;
			}
			
			if(player.getEquipment().wearingBallista(player) && !player.getCombat().properJavalins()) {
				player.write(new SendMessagePacket("You must use javalins with a ballista."));
				player.stopMovement();
				Combat.resetCombat(player);
				return;
			}
			
			if (player.playerEquipment[player.getEquipment().getWeaponId()] == 4734 && !player.getCombat().properBoltRacks()) {
				player.write(new SendMessagePacket("You must use bolt racks with this bow."));
				player.stopMovement();
				Combat.resetCombat(player);
				return;
			}
		}
		/*
		 * Verify we can use the spell
		 */
		if (player.getCombatType() == CombatType.MAGIC) {
			if (!player.getCombat().checkMagicReqs(player.getSpellId())) {
				player.stopMovement();
				Combat.resetCombat(player);
				return;
			}
		}

		/*
		 * Since we can attack, lets verify if we're close enough to attack
		 */
		if (target.isPlayer() && !CombatData.isWithinAttackDistance(player, (Player)target)) {
			return;
		}

		if (target.isPlayer()) {
			Player ptarg = (Player)target;
			if (!player.getMovementHandler().isMoving() && !ptarg.getMovementHandler().isMoving()) {
				if (player.getX() != ptarg.getX() && ptarg.getY() != player.getY()
						&& player.getCombatType() == CombatType.MELEE) {
					PlayerAssistant.stopDiagonal(player, ptarg.getX(), ptarg.getY());
					return;
				}
			}
		}

		/*
		 * Add a skull if needed
		 */
		if (target.isPlayer()) {
			Player ptarg = (Player) target;
			if (player != null && player.attackedPlayers != null && ptarg.attackedPlayers != null && !ptarg.getArea().inDuelArena()) {
				if (!player.attackedPlayers.contains(target.getIndex()) && !ptarg.attackedPlayers.contains(player.getIndex())) {
					player.attackedPlayers.add(target.getIndex());
					player.isSkulled = true;
					player.skullTimer = 500;
					player.skullIcon = 0;
					player.getPA().requestUpdates();
				}
			}
		}
		if (target.isNPC()) {
			Npc npc = (Npc)target;
			/*
			 * Check if we are close enough to stop running towards the npc
			 */
			if (player.usingBow
					|| player.castingMagic
					|| player.throwingAxe
					|| (CombatData.usingHalberd(player) && player.goodDistance(player.getX(), player.getY(), npc.getX(),
					npc.getY(), 2))) {
				player.stopMovement();
			}
			npc.underAttackBy = player.getIndex();
			npc.lastDamageTaken = System.currentTimeMillis();
		}

		//player.getCombat().checkVenomousItems();
		player.faceEntity(target);
		player.delayedDamage = player.delayedDamage2 = 0;
		player.lastWeaponUsed = player.playerEquipment[player.getEquipment().getWeaponId()];

		/*
		 * Check if we are using a special attack
		 */
		if (player.isUsingSpecial() && player.getCombatType() != CombatType.MAGIC) {
			Special.handleSpecialAttack(player, target);
			player.setFollowing(player.getCombat().target);
			return;
		}
		
		/*
		 * Start the attack animation
		 */
		if (!player.usingMagic && player.playerEquipment[player.getEquipment().getWeaponId()] != 22494 && player.playerEquipment[player.getEquipment().getWeaponId()] != 2415 && player.playerEquipment[player.getEquipment().getWeaponId()] != 2416 && player.playerEquipment[player.getEquipment().getWeaponId()] != 2417) {
			player.playAnimation(Animation.create(CombatAnimation.getAttackAnimation(player, player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase())));
			player.mageFollow = false;

			// Npc block anim
			if (target.isNPC()) {
				Npc npc = (Npc) target;
				if (npc.maximumHealth > 0 && npc.attackTimer > 3) {
					if (npc.npcId != 2042 && npc.npcId != 2043 & npc.npcId != 2044 && npc.npcId != 3127) {
						npc.playAnimation(Animation.create(NPCCombatData.getNPCBlockAnimation(npc)));
					}
				}
			}
		} else {
			// Magic attack anim
			player.playAnimation(Animation.create(player.MAGIC_SPELLS[player.getSpellId()][2]));
			player.mageFollow = true;
			player.setFollowing(player.getCombat().target);
			if (!player.autoCast) {
				player.stopMovement();
				player.followId = 0;
				player.followId2 = 0;
			}
		}

		/*
		 * Set the target in combat since we just attacked him/her
		 */
		if (target.isPlayer()) {
			((Player)target).putInCombat(player.getIndex());
			((Player)target).killerId = player.getIndex();
		}
		player.updateLastCombatAction();
		player.setInCombat(true);
		/*if (player.petBonus) {
			player.getCombat().handlePetHit(World.getWorld().getPlayers().get(player.playerIndex));
		}*/
	    player.rangeItemUsed = 0;

		/*
		 * Set the delay before the damage is applied
		 */
		int hitDelay = CombatData.getHitDelay(player, player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase());
		Server.getTaskScheduler().schedule(new ScheduledTask(hitDelay) {
			public void execute() {
				// TODO hit code which i put in notepad
				this.stop();
			}
		});

		/*
		 * Set our combat values based on the combat style
		 */

		if (player.getCombatType() == CombatType.MELEE) {

			player.setFollowing(player.getCombat().target);
			player.getPA().followPlayer(true);
			player.delayedDamage = Utility.getRandom(player.getCombat().calculateMeleeMaxHit());
			player.delayedDamage2 = Utility.getRandom(player.getCombat().calculateMeleeMaxHit());

		} else if (player.getCombatType() == CombatType.RANGED && !player.throwingAxe) {
				player.rangeItemUsed = player.playerEquipment[player.getEquipment().getQuiverId()];
				player.getItems().deleteArrow();
			if (player.getAttackStyle() == 2)
				player.attackDelay--;
			if (player.usingCross)
				player.usingBow = true;
			player.usingBow = true;
			player.setFollowing(player.getCombat().target);
			player.getPA().followPlayer(true);
			player.lastWeaponUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
			player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 100));
			player.getCombat().fireProjectileAtTarget();

		} else if (player.getCombatType() == CombatType.RANGED && player.throwingAxe) {
			player.rangeItemUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
			player.getItems().deleteEquipment();
			World.getWorld();
			player.setFollowing(player.getCombat().target);
			player.getPA().followPlayer(true);
			player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 100));
			if (player.getAttackStyle() == 2)
				player.attackDelay--;
			player.getCombat().fireProjectileAtTarget();

		} else if (player.getCombatType() == CombatType.MAGIC) {
			int pX = player.getX();
			int pY = player.getY();
			int nX = target.getX();
			int nY = target.getY();
			int offX = (pY - nY) * -1;
			int offY = (pX - nX) * -1;
			player.castingMagic = true;
			if (player.MAGIC_SPELLS[player.getSpellId()][3] > 0) {
				if (player.getCombat().getStartGfxHeight() == 100) {
					player.playGraphics(Graphic.create(player.MAGIC_SPELLS[player.getSpellId()][3], 0, 0));
				} else {
					player.playGraphics(Graphic.create(player.MAGIC_SPELLS[player.getSpellId()][3], 0, 0));
				}
			}
			int targetIndex = -player.getCombat().target.getIndex() - 1;
			if (player.MAGIC_SPELLS[player.getSpellId()][4] > 0) {
				player.getProjectile().createPlayersProjectile(pX, pY, offX, offY, 50, 78,
						player.MAGIC_SPELLS[player.getSpellId()][4], player.getCombat().getStartHeight(),
						player.getCombat().getEndHeight(), targetIndex, player.getCombat().getStartDelay());
			}
			if (player.autocastId > 0) {
				player.setFollowing(player.getCombat().target);
				player.followDistance = 5;
			}
			player.oldSpellId = player.getSpellId();
			player.setSpellId(0);

			if (target.isPlayer()) {
				Player ptarg = (Player)target;
				if (player.MAGIC_SPELLS[player.oldSpellId][0] == 12891 && ptarg.getMovementHandler().isMoving()) {
					player.getProjectile().createPlayersProjectile(pX, pY, offX, offY, 50, 85, 368, 25, 25, targetIndex,
							player.getCombat().getStartDelay());
				}
			}

			player.magicFailed = !CombatFormulae.getAccuracy(player, target, 2, 1.0);
			int spellFreezeTime = player.getCombat().getFreezeTime();
			if (spellFreezeTime > 0 && !target.frozen() && !player.magicFailed) {

				target.freeze(spellFreezeTime);
				if (target.isPlayer()) {
					((Player)target).getMovementHandler().resetWalkingQueue();
					((Player)target).write(new SendMessagePacket("You have been frozen."));
					((Player)target).frozenBy = player.getIndex();
				}
			}
			if (!player.autoCast && player.spellId <= 0)
				player.getCombat().reset();
			
		}
	}

	/**
	 * Validates that the attack can be made
	 * 
	 * @param player
	 *            The {@link Player} attacking the oppponent
	 * @param target
	 *            The {@link Player} being attacked
	 * @return If the attack is successful
	 */
	private static boolean validateAttack(Player player, Player target) {
		if (target == null) {
			Combat.resetCombat(player);
			return false;
		}
		if (player.isDead() || target.isDead() || !target.isActive() || target.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
			Combat.resetCombat(player);
			return false;
		}
		if (!player.getArea().inWild() && !player.getArea().inDuelArena())
			return false;
		if (!CombatRequirements.canAttackVictim(player)) {
			return false;
		}
		if (Boundary.isIn(player, Boundary.DUEL_ARENAS)) {
			DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
			if (!Objects.isNull(session)) {
				if (session.getRules().contains(Rule.NO_RANGE) && (player.usingBow || player.getCombatType() == CombatType.RANGED)) {
					player.message("<col=CC0000>Range has been disabled in this duel!");
					Combat.resetCombat(player);
					return false;
				}
				if (session.getRules().contains(Rule.NO_MELEE) && (player.getCombatType() != CombatType.RANGED && !player.usingMagic)) {
					player.message("<col=CC0000>Melee has been disabled in this duel!");
					Combat.resetCombat(player);
					return false;
				}
				if (session.getRules().contains(Rule.NO_MAGE) && player.usingMagic) {
					player.message("<col=CC0000>Magic has been disabled in this duel!");
					Combat.resetCombat(player);
					return false;
				}
				if (session.getRules().contains(Rule.WHIP_AND_DDS)) {
					String weaponName = player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase();
					if (!weaponName.contains("whip") && !weaponName.contains("dragon dagger") || weaponName.contains("tentacle")) {
						player.message("<col=CC0000>You can only use a whip and dragon dagger in this duel.");
						Combat.resetCombat(player);
						return false;
					}
				}
			}
		}
		if (target.isDead()) {
			player.getCombat().reset();
			Combat.resetCombat(player);
			return false;
		}
		if (target.heightLevel != player.heightLevel) {
			Combat.resetCombat(player);
			return false;
		}
		boolean sameSpot = player.absX == target.getX() && player.absY == target.getY();
		if (!player.goodDistance(target.getX(), target.getY(), player.getX(), player.getY(), 25) && !sameSpot) {
			Combat.resetCombat(player);
			return false;
		}
		if (player.frozen() && !CombatData.isWithinAttackDistanceForStopFollow(player, target)) {
			return false;
		}
		// TODO split into 2 methods, canAttack and canTouch??

		// Always last
		if (player.usingBow || player.usingMagic || player.throwingAxe) {
			if (ProjectilePathFinder.isProjectilePathClear(player.getPosition(), target.getPosition())) {
				return true;
			}
		} else {
			if (ProjectilePathFinder.isInteractionPathClear(player.getPosition(), target.getPosition())) {
				return true;
			}
		}

		PathFinder.getPathFinder().findRoute(player, target.absX, target.absY, true, 1, 1);
		//return false;
		
		return true;
	}

}