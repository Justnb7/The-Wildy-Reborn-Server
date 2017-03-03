package com.model.game.character.combat.pvp;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Graphic;
import com.model.game.character.Hit;
import com.model.game.character.HitType;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.CombatFormulae;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.combat_data.CombatAnimation;
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
import com.model.game.character.combat.weaponSpecial.Special;
import com.model.game.character.player.Player;
import com.model.game.character.player.PlayerAssistant;
import com.model.game.character.player.ProjectilePathFinder;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.music.sounds.PlayerSounds;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.out.SendRemoveInterface;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.walking.PathFinder;
import com.model.game.item.Item;
import com.model.game.item.equipment.EquipmentSet;
import com.model.game.item.equipment.EquipmentSlot;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

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
		
		if (EquipmentSet.DHAROK.isWearingBarrows(attacker) && attacker.getItems().isWearingItem(12853) && Utility.getRandom(100) < 25) {
			int damageDealt = (int) Math.floor(damage * .15);
			if (damageDealt < 1) {
				return;
			}
			defender.damage(new Hit(damageDealt, HitType.NORMAL));
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
	 * Applies the players hit to the opponent
	 * 
	 * @param player
	 *            The {@link Player} attacking the opponent
	 * @param index
	 *            The index of the opponent
	 * @param item
	 *            The {@link Item} the player is holding
	 */
	public static void applyPlayerHit(final Player player, final int index, Item item) {//mine
		Player target = World.getWorld().getPlayers().get(index);
		if (target != null) {
			if (target.isDead() || player.isDead() || target.getSkills().getLevel(Skills.HITPOINTS) <= 0 || player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
				player.playerIndex = 0;
				return;
			}
			if (target.isDead()) {
				player.faceUpdate(0);
				player.playerIndex = 0;
				return;
			}

			if (target.playerIndex <= 0 && target.npcIndex <= 0 || target.playerIndex == player.getIndex()) {
				if (target.isAutoRetaliating()) {
					target.playerIndex = player.getIndex();
				}
			}
			if (target.attackDelay <= 3 || target.attackDelay == 0 && target.playerIndex == 0 && !player.castingMagic) {
				target.playAnimation(Animation.create(CombatAnimation.getDefendAnimation(target)));
			}

			if (Trading.isTrading(target)) {
				Trading.decline(target);
			}
			
			target.write(new SendRemoveInterface());
			applyCombatDamage(player, target, player.getCombatType(), item, index);
		}

		player.getPA().requestUpdates();
		if (player.bowSpecShot <= 0) {
			player.oldPlayerIndex = 0;
			player.bowSpecShot = 0;
			player.lastWeaponUsed = 0;
		}
		player.bowSpecShot = 0;
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
					switch (attacker.playerEquipment[EquipmentSlot.ARROWS.getId()]) {
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
                        int shield = defender.playerEquipment[EquipmentSlot.SHIELD.getId()];
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
			attacker.getItems().dropArrowPlayer();
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
			if (System.currentTimeMillis() - defender.reduceStat > 35000) {
				defender.reduceStat = System.currentTimeMillis();
				switch (attacker.MAGIC_SPELLS[attacker.oldSpellId][0]) {
				case 12987:
				case 13011:
				case 12999:
				case 13023:
					defender.getSkills().setLevel(Skills.ATTACK, attacker.getSkills().getLevelForExperience(Skills.ATTACK) * 1 / 100);
					break;
				}
			}

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
	 * Sets the player attackable after 60 ticks
	 * 
	 * @param player
	 */
	private static void isAttackable(Player player) {
		if (player.inTask)
			return;
		Server.getTaskScheduler().schedule(new ScheduledTask(60) {
			@Override
			public void execute() {
				this.stop();

			}

			public void onStop() {
				player.write(new SendMessagePacket("You're now attackable.."));
				player.attackable = true;
				player.inTask = false;
			}
		}.attach(player));
	}

	/**
	 * Handles attacking a player
	 * 
	 * @param player
	 *            The {@link Player} attacking
	 * @param i
	 *            The index of the player being attacked
	 */
	public static void attackPlayer(Player player, int i) {
		Player defender = World.getWorld().getPlayers().get(i);
		
		if (!validateAttack(player, defender)) {
			return;
		}

		player.followId = i;
		player.followId2 = 0;

		if (player.attackDelay > 0) {
			// don't attack as our timer hasnt reached 0 yet
			return;
		}
		
		Player attacker = World.getWorld().PLAYERS.get(defender.underAttackBy);
		player.getActionSender().sendString(defender.getName()+ "-"+player.getSkills().getLevelForExperience(Skills.HITPOINTS)+"-"+defender.getSkills().getLevel(Skills.HITPOINTS)+ ((attacker != null) ? "-"+ attacker.getName() : ""), 35000);

		player.usingBow = player.usingBow = player.usingArrows = player.throwingAxe = false;
		player.rangeItemUsed = 0;
		player.usingCross = player.getEquipment().isCrossbow(player);
		player.setCombatType(player.usingCross ? CombatType.RANGED : CombatType.MELEE);

		boolean sameSpot = player.getX() == defender.getX() && player.getY() == defender.getY();
		if (sameSpot) {
			if (player.frozen()) {
				Combat.resetCombat(player);
				return;
			}
			player.followId = i;
			player.attackDelay = 0;
			return;
		}
		
		/*
		 * Check if we are using magic
		 */
		if (player.autoCast) {
			player.spellId = player.autocastId;
			player.usingMagic = true;
			player.setCombatType(CombatType.MAGIC);
		}
		
		if (player.getSpellId() > 0) {
			player.usingMagic = true;
			player.setCombatType(CombatType.MAGIC);
		}
		
		/*
		 * Check if we are using ranged
		 */
		if (player.getCombatType() != CombatType.MAGIC) {
			player.usingBow = player.getEquipment().isBow(player);
			player.throwingAxe = player.getEquipment().isThrowingWeapon(player);
			player.usingCross = player.getEquipment().isCrossbow(player);
			player.usingArrows = player.getEquipment().isArrow(player);
			boolean bolt = player.getEquipment().isBolt(player);
			boolean javalin = player.getCombat().properJavalins();
			
			if(player.throwingAxe || player.usingCross || player.usingBow || player.getEquipment().wearingBallista(player) || player.getEquipment().wearingBlowpipe(player)) {
				player.setCombatType(CombatType.RANGED);
			}
			
			if(player.throwingAxe) {
				player.throwingAxe = true;
			}
			
			if(bolt || javalin || player.usingArrows) {
				player.usingArrows = true;
			}
		}

		if (!player.getController().canAttackPlayer(player, defender) && Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL) == null) {
			return;
		}
		
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
				player.playerIndex = 0;
				return;
			}

			if (player.getCombat().correctBowAndArrows() < player.playerEquipment[player.getEquipment().getQuiverId()] 
					&& player.usingBow
					&& !player.getEquipment().usingCrystalBow(player)
					&& !player.getEquipment().isCrossbow(player) && !player.getEquipment().wearingBlowpipe(player)) {
				player.write(new SendMessagePacket("You can't use " + player.getItems().getItemName(player.playerEquipment[player.getEquipment().getQuiverId()]).toLowerCase() + "s with a " + player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase() + "."));
				player.stopMovement();
				player.playerIndex = 0;
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

		/**
		 * Since we can attack, lets verify if we're close enough to attack
		 */

		if (CombatData.isWithinAttackDistance(player, defender)) {
			/**
			 * We're close enough, so stop us from moving
			 */
			// c.getMovementHandler().reset();
		} else {
			/**
			 * We aren't close enough yet, continue to reset our timer until we
			 * are.
			 */
			player.attackDelay = 1;
			return;
		}


		if (!player.getMovementHandler().isMoving() && !defender.getMovementHandler().isMoving()) {
			if (player.getX() != defender.getX() && defender.getY() != player.getY()
					&& player.getCombatType() == CombatType.MELEE) {
				PlayerAssistant.stopDiagonal(player, defender.getX(), defender.getY());
				return;
			}
		}

		/*
		 * Add a skull if needed
		 */
		if (player != null && player.attackedPlayers != null && World.getWorld().getPlayers().get(player.playerIndex) != null && World.getWorld().getPlayers().get(player.playerIndex).attackedPlayers != null && !World.getWorld().getPlayers().get(player.playerIndex).getArea().inDuelArena()) {
			if (!player.attackedPlayers.contains(player.playerIndex) && !World.getWorld().getPlayers().get(player.playerIndex).attackedPlayers.contains(player.getIndex())) {
				player.attackedPlayers.add(player.playerIndex);
				player.isSkulled = true;
				player.skullTimer = 500;
				player.skullIcon = 0;
				player.getPA().requestUpdates();
			}
		}

		player.faceUpdate(i + 32768);
		player.delayedDamage = player.delayedDamage2 = 0;

		/*
		 * Check if we are using a special attack
		 */
		if (player.isUsingSpecial() && player.getCombatType() != CombatType.MAGIC) {
			Special.handleSpecialAttack(player, defender);
			player.followId = player.playerIndex;
			return;
		}
		
		/*
		 * Start the attack animation
		 */
		
		if (!player.usingMagic && player.playerEquipment[player.getEquipment().getWeaponId()] != 22494 && player.playerEquipment[player.getEquipment().getWeaponId()] != 2415 && player.playerEquipment[player.getEquipment().getWeaponId()] != 2416 && player.playerEquipment[player.getEquipment().getWeaponId()] != 2417) {
			player.playAnimation(Animation.create(CombatAnimation.getAttackAnimation(player, player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase())));
			player.mageFollow = false;
		} else {
			player.playAnimation(Animation.create(player.MAGIC_SPELLS[player.getSpellId()][2]));
			player.mageFollow = true;
			player.followId = player.playerIndex;
			if (!player.autoCast) {
				player.stopMovement();
				player.followId = 0;
				player.followId2 = 0;
			}
		}

		/*
		 * Set the target in combat since we just attacked him/her
		 */
		defender.putInCombat(player.getIndex());
		defender.killerId = player.getIndex();
		player.updateLastCombatAction();
		player.setInCombat(true);
		/*if (player.petBonus) {
			player.getCombat().handlePetHit(World.getWorld().getPlayers().get(player.playerIndex));
		}*/
	    player.rangeItemUsed = 0;

		/*
		 * Set the delay before the damage is applied
		 */
		player.hitDelay = CombatData.getHitDelay(player, player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]).toLowerCase());

		/*
		 * Set our combat values based on the combat style
		 */

		if (player.getCombatType() == CombatType.MELEE) {

			player.followId = player.playerIndex;
			player.getPA().followPlayer(true);
			player.delayedDamage = Utility.getRandom(player.getCombat().calculateMeleeMaxHit());
			player.delayedDamage2 = Utility.getRandom(player.getCombat().calculateMeleeMaxHit());
			player.oldPlayerIndex = i;
		} else if (player.getCombatType() == CombatType.RANGED && !player.throwingAxe) {
				player.rangeItemUsed = player.playerEquipment[player.getEquipment().getQuiverId()];
				player.getItems().deleteArrow();
			if (player.getAttackStyle() == 2)
				player.attackDelay--;
			if (player.usingCross)
				player.usingBow = true;
			player.usingBow = true;
			player.followId = World.getWorld().getPlayers().get(player.playerIndex).getIndex();
			player.getPA().followPlayer(true);
			player.lastWeaponUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
			player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 100));
			player.oldPlayerIndex = i;
			player.getCombat().fireProjectilePlayer();
		} else if (player.getCombatType() == CombatType.RANGED && player.throwingAxe) {
			player.rangeItemUsed = player.playerEquipment[player.getEquipment().getWeaponId()];
			player.getItems().deleteEquipment();
			World.getWorld();
			player.followId = World.getWorld().getPlayers().get(player.playerIndex).getIndex();
			player.getPA().followPlayer(true);
			player.playGraphics(Graphic.create(player.getCombat().getRangeStartGFX(), 0, 100));
			if (player.getAttackStyle() == 2)
				player.attackDelay--;
			player.oldPlayerIndex = i;
			player.getCombat().fireProjectilePlayer();
		} else if (player.getCombatType() == CombatType.MAGIC) {
			int pX = player.getX();
			int pY = player.getY();
			int nX = defender.getX();
			int nY = defender.getY();
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
			if (player.MAGIC_SPELLS[player.getSpellId()][4] > 0) {
				player.getProjectile().createPlayersProjectile(pX, pY, offX, offY, 50, 78,
						player.MAGIC_SPELLS[player.getSpellId()][4], player.getCombat().getStartHeight(),
						player.getCombat().getEndHeight(), -i - 1, player.getCombat().getStartDelay());
			}
			if (player.autocastId > 0) {
				player.followId = player.playerIndex;
				player.followDistance = 5;
			}
			player.oldPlayerIndex = i;
			player.oldSpellId = player.getSpellId();
			player.setSpellId(0);

			if (player.MAGIC_SPELLS[player.oldSpellId][0] == 12891 && defender.getMovementHandler().isMoving()) {
				player.getProjectile().createPlayersProjectile(pX, pY, offX, offY, 50, 85, 368, 25, 25, -i - 1,
						player.getCombat().getStartDelay());
			}

			player.magicFailed = !CombatFormulae.getAccuracy(player, defender, 2, 1.0);
			int spellFreezeTime = player.getCombat().getFreezeTime();
			if (spellFreezeTime > 0 && !defender.frozen() && !player.magicFailed) {
				
				defender.freeze(spellFreezeTime);
				defender.getMovementHandler().resetWalkingQueue();
				defender.write(new SendMessagePacket("You have been frozen."));
				defender.frozenBy = player.getIndex();
			}
			if (!player.autoCast && player.spellId <= 0)
				player.playerIndex = 0;
			
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
		if (player.getBankPin().requiresUnlock()) {
			player.setBanking(false);
			player.getBankPin().open(2);
			Combat.resetCombat(player);
			return false;
		}

		if (target.getBankPin().requiresUnlock()) {
			if (target.attackable) {

			} else {
				isAttackable(target);
				target.inTask = true;
				player.write(new SendMessagePacket("The other player needs to unlock the account before they can be in combat."));
				player.write(new SendMessagePacket("The account will become attackable after 1 minute"));
				Combat.resetCombat(player);
				return false;
			}
		}
		
		if (player.playerEquipment[player.getEquipment().getWeaponId()] == 12924) {
			player.write(new SendMessagePacket("You must load this weapon with zulrah scales!"));
			Combat.resetCombat(player);
			return false;
		}
		if (target.isDead()) {
			target.playerIndex = 0;
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