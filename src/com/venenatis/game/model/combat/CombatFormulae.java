package com.venenatis.game.model.combat;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.combat.RangeConstants.RangeWeaponType;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.container.impl.equipment.EquipmentContainer;
import com.venenatis.game.model.definitions.EquipmentDefinition;
import com.venenatis.game.model.definitions.WeaponDefinition;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;

/**
 * Handles the combat's accuracy and max hit formulas.
 * 
 * @author Gabriel | Wolfs Darker
 *
 */
public class CombatFormulae {
	
	/**
	 * Default private constructor.
	 */
	private CombatFormulae() {
		
	}

    private static int npcDef(NPC npc, int att_type) {
        if (att_type == 0)
            return npc.melee_defence;
        else if (att_type == 1)
            return npc.range_defence;
        else if (att_type == 2)
            return npc.magic_defence;
        throw new IllegalStateException();
    }

    /**
     * Calculates the accuracy between the attacker and the target.
     * 
     * @param attacker
     * @param target
     * @param att_type
     * @param additionalSpecMulti TODO
     * @return
     */
    public static boolean getAccuracy(Entity attacker, Entity target, int att_type, double additionalSpecMulti) {

        int att_curr_attack = attacker != null && attacker.isNPC() ? attacker.toNPC().getDefinition().getAttackBonus() : attacker.toPlayer().getSkills().getLevel(Skills.ATTACK);
        int att_curr_range = attacker != null && attacker.isNPC() ? attacker.toNPC().getDefinition().getAttackBonus() : attacker.toPlayer().getSkills().getLevel(Skills.RANGE);
        int att_curr_magic = attacker != null && attacker.isNPC() ? attacker.toNPC().getDefinition().getAttackBonus() : attacker.toPlayer().getSkills().getLevel(Skills.MAGIC);

        int tar_curr_magic = target != null && target.isNPC() ? target.toNPC().getDefinition().getMagicDefence() : target.toPlayer().getSkills().getLevel(Skills.MAGIC);
        int tar_curr_defence = target != null && target.isNPC() ? npcDef(target.toNPC(), att_type) : target.toPlayer().toPlayer().getSkills().getLevel(Skills.DEFENCE);

        int att_base_attack = attacker != null && attacker.isNPC() ? attacker.toNPC().getDefinition().getAttackBonus() : attacker.toPlayer().getSkills().getLevelForExperience(Skills.ATTACK);
        int att_base_range = attacker != null && attacker.isNPC() ? attacker.toNPC().getDefinition().getAttackBonus() : attacker.toPlayer().getSkills().getLevelForExperience(Skills.RANGE);
        int att_base_magic = attacker != null && attacker.isNPC() ? attacker.toNPC().getDefinition().getAttackBonus() : attacker.toPlayer().getSkills().getLevelForExperience(Skills.MAGIC);

        double att_prayer_bonus = 1.0;
        double att_style_bonus = 0;
        double att_spec_bonus = 1;

        double att_spell_bonus = 0;
        double att_weapon_bonus = 0;
        double att_additional_bonus = 1;
        double att_effective_attack = 0;
        double att_equipment_bonus = 1;
        double augmented_attack = 0;
        double att_hit_chance = 0;
        double att_void_bonus = 1;

        double tar_prayer_bonus = 1.0;
        double tar_style_bonus = 0;

        double tar_equipment_bonus = 1;
        double augmented_defence = 0;
        double tar_block_chance = 0;

        double tar_effective_defence = 0;

        if (attacker.isPlayer()) {
            Player p = attacker.toPlayer();

            switch (att_type) {
            
            case 0:
            	if (p.isActivePrayer(Prayers.CLARITY_OF_THOUGHT)) {
                    att_prayer_bonus += 0.05;
                }

                if (p.isActivePrayer(Prayers.IMPROVED_REFLEXES)) {
                    att_prayer_bonus += 0.1;
                }

                if (p.isActivePrayer(Prayers.INCREDIBLE_REFLEXES)) {
                    att_prayer_bonus += 0.15;
                }

                if (p.isActivePrayer(Prayers.CHIVALRY)) {
                    att_prayer_bonus += 0.15;
                }

                if (p.isActivePrayer(Prayers.PIETY)) {
                    att_prayer_bonus += 0.20;
                }

                att_equipment_bonus = p.getBonuses()[p.getAttackStyle() <= 1 ? p.getAttackStyle() : 1];
                att_void_bonus = wearingFullVoid(p, att_type) ? 1.1 : 1;
                break;
            case 1:
            	 if (p.isActivePrayer(Prayers.SHARP_EYE)) {
                     att_prayer_bonus += 0.05;
                 }

                 if (p.isActivePrayer(Prayers.HAWK_EYE)) {
                     att_prayer_bonus += 0.1;
                 }

                 if (p.isActivePrayer(Prayers.EAGLE_EYE)) {
                     att_prayer_bonus += 0.15;
                 }
                 
                 if (p.isActivePrayer(Prayers.RIGOUR)) {
                     att_prayer_bonus += 0.20;
                 }

                att_equipment_bonus = p.getBonuses()[4];
                att_void_bonus = wearingFullVoid(p, att_type) ? 1.1 : 1;
                break;
            case 2:
            	 if (p.isActivePrayer(Prayers.MYSTIC_WILL)) {
                     att_prayer_bonus += 0.05;
                 }

                 if (p.isActivePrayer(Prayers.MYSTIC_LORE)) {
                     att_prayer_bonus += 0.1;
                 }

                 if (p.isActivePrayer(Prayers.MYSTIC_MIGHT)) {
                     att_prayer_bonus += 0.15;
                 }
                 
                 if (p.isActivePrayer(Prayers.AUGURY)) {
                     att_prayer_bonus += 0.25;
                 }

                att_spell_bonus += ((att_base_magic - p.MAGIC_SPELLS[p.getSpellId()][1]) * 0.3);

                att_equipment_bonus = p.getBonuses()[3];
                att_void_bonus = wearingFullVoid(p, att_type) ? 1.3 : 1;
                break;
                
            default:
                break;
            }

            Item weapon = p.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
            
            if (weapon != null && att_type != 2) {
                att_weapon_bonus += (((att_type == 0 ? att_base_attack : att_base_range) - (att_type = 0)) * 0.3);
            }

            if (att_type != 2) {
                att_style_bonus = p.getAttackStyle() == 2 ? 1 : p.getAttackStyle() == 1 ? 3 : 0;
            }

        }

        switch (att_type) {
        case 0:
            att_effective_attack = Math
                .floor(((att_curr_attack * att_prayer_bonus) * att_additional_bonus) + att_style_bonus + att_weapon_bonus);
            break;
        case 1:
            att_effective_attack = Math
                .floor(((att_curr_range * att_prayer_bonus) * att_additional_bonus) + att_style_bonus + att_weapon_bonus);
            break;
        case 2:
            att_effective_attack = Math.floor(((att_curr_magic * att_prayer_bonus) * att_additional_bonus) + att_spell_bonus);
            break;
        default:
            break;
        }

        if (target.isPlayer()) {

            Player t = target.toPlayer();

            if (t.isActivePrayer(Prayers.THICK_SKIN)) {
                tar_prayer_bonus += 0.05;
            }

            if (t.isActivePrayer(Prayers.ROCK_SKIN)) {
                tar_prayer_bonus += 0.1;
            }

            if (t.isActivePrayer(Prayers.STEEL_SKIN)) {
                tar_prayer_bonus += 0.15;
            }

            if (t.isActivePrayer(Prayers.CHIVALRY)) {
                tar_prayer_bonus += 0.2;
            }

            if (t.isActivePrayer(Prayers.PIETY)) {
                tar_prayer_bonus += 0.25;
            }
            
            if (t.isActivePrayer(Prayers.RIGOUR)) {
                tar_prayer_bonus += 0.25;
            }
            
            if (t.isActivePrayer(Prayers.AUGURY)) {
                tar_prayer_bonus += 0.25;
            }

            tar_style_bonus = t.getAttackStyle() == 2 ? 1 : t.getAttackStyle() == 3 ? 3 : 0;

            switch (att_type) {
            case 0:
                tar_equipment_bonus = t.getBonuses()[attacker.isPlayer() ? attacker.toPlayer().getAttackStyle() <= 1
                    ? attacker.toPlayer().getAttackStyle() + 5 : 6 : 6];
                break;
            case 1:
                tar_equipment_bonus = t.getBonuses()[9];
                break;
            case 2:
                tar_equipment_bonus = t.getBonuses()[8];
                break;
            default:
                break;
            }
        }

        switch (att_type) {
        case 0:
            tar_effective_defence = Math.floor(((tar_curr_defence * tar_prayer_bonus) + tar_style_bonus));
            break;
        case 1:
            tar_effective_defence = Math.floor(((tar_curr_defence * tar_prayer_bonus) + tar_style_bonus));
            break;
        case 2:
            tar_effective_defence = Math.floor(((tar_curr_defence * tar_prayer_bonus) * 0.3)) + (Math.floor(tar_curr_magic * 0.7));
            break;
        default:
            break;
        }

        augmented_attack = Math.floor((((att_effective_attack + 8) * (att_equipment_bonus + 64)) / 10));

        augmented_defence = Math.floor((((tar_effective_defence + 8) * (tar_equipment_bonus + 64)) / 10));

        double hit_chance = 0;

        if (augmented_attack < augmented_defence) {
            hit_chance = ((augmented_attack - 1) / (augmented_defence * 2));
        } else {
            hit_chance = 1 - ((augmented_defence + 1) / (augmented_attack * 2));
        }

        switch (att_type) {
        case 0:
            if (target.isPlayer() && target.toPlayer().isActivePrayer(Prayers.PROTECT_FROM_MELEE)) {
                att_hit_chance = Math.floor((((hit_chance * att_spec_bonus) * att_void_bonus) * 0.6) * 100);
                tar_block_chance = Math.floor(101 - ((((hit_chance * att_spec_bonus) * att_void_bonus) * 0.6) * 100));
            } else {
                att_hit_chance = Math.floor(((hit_chance * att_spec_bonus) * att_void_bonus) * 100);
                tar_block_chance = Math.floor(101 - (((hit_chance * att_spec_bonus) * att_void_bonus) * 100));
            }
            break;
        case 1:
            if (target.isPlayer() && target.toPlayer().isActivePrayer(Prayers.PROTECT_FROM_MISSILE)) {
                att_hit_chance = Math.floor((((hit_chance * att_spec_bonus) * att_void_bonus) * 0.6) * 100);
                tar_block_chance = Math.floor(101 - ((((hit_chance * att_spec_bonus) * att_void_bonus) * 0.6) * 100));
            } else {
                att_hit_chance = Math.floor(((hit_chance * att_spec_bonus) * att_void_bonus) * 100);
                tar_block_chance = Math.floor(101 - (((hit_chance * att_spec_bonus) * att_void_bonus) * 100));
            }
            break;
        case 2:
            if (target.isPlayer() && target.toPlayer().isActivePrayer(Prayers.PROTECT_FROM_MAGIC)) {
                att_hit_chance = Math.floor(((hit_chance * att_void_bonus) * 0.6) * 100);
                tar_block_chance = Math.floor(101 - ((((hit_chance * att_spec_bonus) * att_void_bonus) * 0.6) * 100));
            } else {
                att_hit_chance = Math.floor((hit_chance * att_void_bonus) * 100);
                tar_block_chance = Math.floor(101 - (((hit_chance * att_spec_bonus) * att_void_bonus) * 100));
            }
            break;
        default:
            break;
        }
        att_hit_chance = Utility.getRandom((int) (att_hit_chance + 20 + (!attacker.isNPC() ? attacker.toPlayer().isUsingSpecial() ? 20 * additionalSpecMulti : 0 : 0)));
        tar_block_chance = Utility.getRandom((int) tar_block_chance);
        if (attacker.isPlayer())
        	attacker.asPlayer().debug(String.format("target %s hit = %s >? %s%n", target, att_hit_chance, tar_block_chance));
        return (int) att_hit_chance > (int) tar_block_chance;
    }
    
    /**
	 * Calculates a mob's melee max hit.
	 */
	public static int calculateMeleeMaxHit(Entity attacker, Entity target) {

		Player player = (Player) attacker;
		double specialMultiplier = 1;
		double prayerMultiplier = 1;
		double strengthBonus = player.getBonuses()[10];
		double otherBonusMultiplier = 1;
		int strengthLevel = player.getSkills().getLevel(Skills.STRENGTH);
		int combatStyleBonus = weaponBonus(player);

		if (player.isActivePrayer(Prayers.BURST_OF_STRENGTH)) {
			prayerMultiplier = 1.05;
		} else if (player.isActivePrayer(Prayers.SUPERHUMAN_STRENGTH)) {
			prayerMultiplier = 1.1;
		} else if (player.isActivePrayer(Prayers.ULTIMATE_STRENGTH)) {
			prayerMultiplier = 1.15;
		} else if (player.isActivePrayer(Prayers.CHIVALRY)) {
			prayerMultiplier = 1.18;
		} else if (player.isActivePrayer(Prayers.PIETY)) {
			prayerMultiplier = 1.23;
		}

		// Apply black mask/slayer helm bonus if the victim is the player's slayer task
		if (target != null)
			if (attacker.isPlayer() && target.isNPC()) {
				final NPC npc = (NPC) target;
				if (player.getSlayerTask() != -1 && player.getSlayerTask() == npc.getId() && hasBlackMaskOrSlayerHelm(player)) {
					otherBonusMultiplier = 1.15;
				}
			}

		if (fullDharok(player)) {
			double dharokMultiplier = ((1 - ((float) player.getSkills().getLevel(Skills.HITPOINTS) / (float) player.getSkills().getLevelForExperience(Skills.HITPOINTS)) * 1.7)) + 1;
			otherBonusMultiplier *= dharokMultiplier;
		}
		
		if(wearingFullVoid(player, 0)) {
			otherBonusMultiplier = 1.1;
		}
		
		if (hasBerserkerNecklaceBonus(player)) {
			otherBonusMultiplier = 1.2;
		}
		
		int effectiveStrengthDamage = (int) ((strengthLevel * prayerMultiplier * otherBonusMultiplier) + combatStyleBonus);
		double base = (13 + effectiveStrengthDamage + (strengthBonus / 8) + ((effectiveStrengthDamage * strengthBonus) * 0.016865)) / 10;
		Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		if (player.isUsingSpecial()) {
			switch (weapon.getId()) {
			case 11802:
				specialMultiplier = 1.42375;
				break;
			case 11804:
				specialMultiplier = 1.1825;
				break;
			case 11806:
			case 11808:
				specialMultiplier = 1.075;
				break;
			case 3101:
			case 3204:
			case 1215:
			case 1231:
			case 5680:
			case 5698:
				specialMultiplier = 1.25;
				break;
			case 1305:
				specialMultiplier = 1.15;
				break;
			case 1434:
				specialMultiplier = 1.45;
				break;
			}
			base *= specialMultiplier;
		}

		return (int) Math.floor(base);
	}

	/**
	 * Retrieves the players attackstyle.
	 * 
	 * @param player
	 */
	public static final int weaponBonus(Player player) {
		switch (player.getAttackStyle()) {
		case 2:
			return 3;
		default:
			break;
		}
		return 0;
	}
	
	/**
	 * Calculates a mob's range max hit.
	 */
	public static int calculateRangeMaxHit(Entity mob, Entity victim, boolean special, boolean ignoreArrowStr) {
		Player player = (Player) mob;
		int maxHit = 0;
		double specialMultiplier = 1;
		double prayerMultiplier = 1;
		double otherBonusMultiplier = 1;
		if (ignoreArrowStr && mob.isPlayer()) {
			EquipmentContainer.calcBonuses((Player)mob, true);
		}
		int rangedStrength = player.getBonuses().length < 13 ? 0 : player.getBonuses()[11];
		System.out.println("str used: "+rangedStrength);
		
		if (ignoreArrowStr && mob.isPlayer()) {
			// reset to normal afterwards for any future attacks
			EquipmentContainer.calcBonuses((Player)mob, false);
		}
		
		if (player.getBonuses().length < 13) {
			System.err.println("NO RANGE STR BONUSES DEFINED");
		}
		
		int rangeLevel = player.getSkills().getLevel(Skills.RANGE);
		int combatStyleBonus = 0;
		
		if (player.isActivePrayer(Prayers.SHARP_EYE)) {
			prayerMultiplier = 1.05;
		} else if (player.isActivePrayer(Prayers.HAWK_EYE)) {
			prayerMultiplier = 1.10;
		} else if (player.isActivePrayer(Prayers.EAGLE_EYE)) {
			prayerMultiplier = 1.15;
		} else if (player.isActivePrayer(Prayers.RIGOUR)) {
			prayerMultiplier = 1.23;
		}
		
		switch(player.getAttackStyle()) {
		case 0:
			combatStyleBonus = 3;
			break;
		default:
			break;
		}
		
		if(wearingFullVoid(player, 1)) {
			otherBonusMultiplier *= 1.2;
		}
		
		int effectiveRangeDamage = (int) ((rangeLevel * prayerMultiplier * otherBonusMultiplier) + combatStyleBonus);
		double baseDamage = 1.3 + (effectiveRangeDamage / 10) + (rangedStrength / 80) + ((effectiveRangeDamage * rangedStrength) / 640);
		
		
		if(special) {
			if(player.getEquipment().get(EquipmentConstants.AMMO_SLOT) != null) {
				switch(player.getEquipment().get(EquipmentConstants.AMMO_SLOT) .getId()) {
				case 11212:
					specialMultiplier = 1.5;
					break;
				case 9243:
					specialMultiplier = 1.15;
					break;
				case 9244:
					specialMultiplier = 1.45;
					break;
				case 9245:
					specialMultiplier = 1.15;
					break;
				case 9236:
					specialMultiplier = 1.25;
					break;
				case 882:
				case 884:
				case 886:
				case 888:
				case 890:
				case 892:
					if(player.getEquipment().get(EquipmentConstants.AMMO_SLOT) != null && player.getEquipment().get(EquipmentConstants.AMMO_SLOT).getId() == 11235) {
						specialMultiplier = 1.3;
					}
					break;
				}
			}
		}
		
		maxHit = (int) (baseDamage * specialMultiplier);
		
		return maxHit;
	}

	/**
	 * Checks if we're using a throwing weapon
	 * 
	 * @param player
	 *            The player wearing the throwing weapon
	 */
	static boolean usingThrowingWeapon(Player player) {
		Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		if (weapon == null) {
			return false;
		}
		EquipmentDefinition weaponEquipDef = weapon.getEquipmentDefinition();
		
		RangeWeaponType rangeWeaponType = weaponEquipDef.getRangeWeaponType();
		if (Combat.isHandWeapon(rangeWeaponType)) {
			return true;
		}
		return false;
	}

	public static boolean hasAmuletOfTheDamned(Player player) {
		return player.getEquipment().contains(12851);
	}

	public static boolean hasBerserkerNecklaceBonus(Player player) {
		return player.getEquipment().contains(11128)
				&& (player.getEquipment().contains(6523) || player.getEquipment().contains(6528)
						|| player.getEquipment().contains(6527) || player.getEquipment().contains(6525));
	}

	public static boolean hasBlackMaskOrSlayerHelm(Player player) {
		return player.getEquipment().contains(8901) || player.getEquipment().contains(11864)
				|| player.getEquipment().contains(11865);
	}

	public static boolean hasImbuedSlayerHelm(Player player) {
		return player.getEquipment().contains(11865);
	}

	public static boolean fullGuthan(Player player) {
		return player.getEquipment().contains(4724) && player.getEquipment().contains(4726)
				&& player.getEquipment().contains(4728) && player.getEquipment().contains(4730);
	}

	public static boolean fullTorag(Player player) {
		return player.getEquipment().contains(4745) && player.getEquipment().contains(4747)
				&& player.getEquipment().contains(4749) && player.getEquipment().contains(4751);
	}

	public static boolean fullKaril(Player player) {
		return player.getEquipment().contains(4732) && player.getEquipment().contains(4734)
				&& player.getEquipment().contains(4736) && player.getEquipment().contains(4738);
	}

	public static boolean fullAhrim(Player player) {
		return player.getEquipment().contains(4708) && player.getEquipment().contains(4710)
				&& player.getEquipment().contains(4712) && player.getEquipment().contains(4714);
	}

	public static boolean fullAhrimDamned(Player player) {
		return player.getEquipment().contains(4708) && player.getEquipment().contains(4710)
				&& player.getEquipment().contains(4712) && player.getEquipment().contains(4714)
				&& player.getEquipment().contains(12851);
	}

	public static boolean fullDharok(Player player) {
		return player.getEquipment().contains(4716) && player.getEquipment().contains(4718)
				&& player.getEquipment().contains(4720) && player.getEquipment().contains(4722);
	}

	public static boolean fullVerac(Player player) {
		return player.getEquipment().contains(4753) && player.getEquipment().contains(4755)
				&& player.getEquipment().contains(4757) && player.getEquipment().contains(4759);
	}

	/**
	 * Checks if the player is wearing full void.
	 * 
	 * @param player
	 * @return
	 */
	public static boolean wearingFullVoid(Player player, int type) {
		int complete = 0;

		if (type < 0) {
			return false;
		}

		boolean helmet = player.getEquipment().contains(type == 0 ? 11665 : type == 1 ? 11663 : 11664);

		boolean hasGloves = player.getEquipment().contains(8842);

		boolean hasDeflector = player.getEquipment().contains(19712);

		boolean hasLegs = player.getEquipment().contains(8840) || player.getEquipment().contains(13073);

		boolean hasPlate = player.getEquipment().contains(8839) || player.getEquipment().contains(13072);

		if (helmet) {
			complete++;
		}

		if (hasGloves) {
			complete++;
		}

		if ((hasDeflector && ((hasPlate && !hasLegs) || (!hasPlate && hasLegs))) || hasPlate && hasLegs) {
			complete++;
		}

		return complete >= 3;
	}
	
	/**
	 * The percentage of the hit reducted by antifire.
	 */
	public static double dragonfireReduction(Entity entity) {
		Player player = (Player) entity;
		boolean dragonfireShield = player.getEquipment().contains(1540) || player.getEquipment().contains(11283) || player.getEquipment().contains(11284) || player.getEquipment().contains(11285);
		boolean dragonfirePotion = false;
		if (entity.hasAttribute("antiFire")) {
			dragonfirePotion = System.currentTimeMillis() - (long)entity.getAttribute("antiFire", 0L) < 360000;
		} else if (entity.hasAttribute("extended_antiFire")) {
            dragonfirePotion = System.currentTimeMillis() - (long)entity.getAttribute("extended_antiFire", 0L) < 720000;
        }
		boolean protectPrayer = player.isActivePrayer(Prayers.PROTECT_FROM_MAGIC);
		if (dragonfireShield && dragonfirePotion) {
			player.message("You shield absorbs most of the dragon fire!");
			player.message("Your potion protects you from the heat of the dragon's breath!");
			return 1;
		} else if (dragonfireShield) {
			player.message("You shield absorbs most of the dragon fire!");
			return 0.8; // 80%
		} else if (dragonfirePotion) {
			player.message("Your potion protects you from the heat of the dragon's breath!");
			return 0.8; // 80%
		} else if (protectPrayer) {
			player.message("Your prayers resist some of the dragon fire.");
			return 0.6; // 60%
		}
		return 0;		
	}
	
	/**
	 * Get the attackers' weapon speed.
	 *
	 * @return A <code>long</code>-type value of the weapon speed.
	 */
	public static int getCombatCooldownDelay(Entity entity) {
		Player p = (Player) entity;
		Item weapon = p.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		int extra = 0;
		if(entity.getCombatType() == CombatStyle.RANGE) {
			if(p.attackStyle != 1) {
				/**
				 * If we are ranging and are not on rapid, combat speed is increased by 1 cycle
				 */
				extra = 1;
			}
		}
		return (p.getEquipment() != null && p.getEquipment().get(3) != null) ? WeaponDefinition.get(weapon.getId()).getAttackSpeed() + extra : 4;
	}

}