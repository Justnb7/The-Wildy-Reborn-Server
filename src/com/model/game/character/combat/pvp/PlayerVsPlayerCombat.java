package com.model.game.character.combat.pvp;

import com.model.Server;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.combat_data.CombatData;
import com.model.game.character.combat.combat_data.CombatExperience;
import com.model.game.character.combat.combat_data.CombatRequirements;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.combat.effect.CombatEffect;
import com.model.game.character.combat.effect.impl.RingOfRecoil;
import com.model.game.character.combat.effect.impl.Venom;
import com.model.game.character.combat.magic.MagicCalculations;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.combat.range.RangeData;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.ProjectilePathFinder;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSessionRules.Rule;
import com.model.game.character.player.content.music.sounds.PlayerSounds;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.walking.PathFinder;
import com.model.game.item.Item;
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
	 * Validates that the attack can be made
	 * 
	 * @param player
	 *            The {@link Player} attacking the oppponent
	 * @param target
	 *            The {@link Player} being attacked
	 * @return If the attack is successful
	 */
	public static boolean validateAttack(Player player, Player target) {
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