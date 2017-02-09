package com.model.game.character.combat.range;

import com.model.game.character.Entity;
import com.model.game.character.combat.PrayerHandler.Prayer;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.item.equipment.Equipment;

public class RangedCalculations {
	
	public static int SHARP_EYE = 3, HAWK_EYE = 11, EAGLE_EYE = 19;
	
	/**
	 * Calculates the max hit you can deal with range
	 * 
	 * @param player
	 * @param special
	 * @return
	 */
	public static int calculateRangedMaxHit(Entity attacker, Entity target) {
		Player player = (Player) attacker;

        int rangedLevel = player.getSkills().getLevel(Skills.RANGE);

        if (player.isActivePrayer(Prayer.SHARP_EYE)) {
            rangedLevel *= 1.05;
        } else if (player.isActivePrayer(Prayer.HAWK_EYE)) {
            rangedLevel *= 1.10;
        } else if (player.isActivePrayer(Prayer.EAGLE_EYE)) {
            rangedLevel *= 1.15;
        }

        int combatStyleBonus = player.getAttackStyle() == 0 ? 3 : 0;

        double rangeBonus = RangeData.getRangeStr(player.usingBow ? player.lastArrowUsed : player.lastWeaponUsed);

        if (player.playerEquipment[player.getEquipment().getWeaponId()] == 12926) {
            rangeBonus = RangeData.getRangeStr(11230);
            player.playerBonus[4] += RangeData.getRangeStr(11230);
        }

        double effectiveStrength = (rangedLevel) + combatStyleBonus;

        double maxHit = (5 + ((effectiveStrength + 8) * (rangeBonus + 64) / 64)) / 10;
        
        if(player.getEquipment().wearingBlowpipe(player)) {
        	if (player.isUsingSpecial()) {
        		maxHit *= 2.25;
        	} else {
        		maxHit *= 1.49;
        	}
        }
        
        if(Equipment.wearingAnguish(player)) {
        	maxHit += 0.5;
        }

        if (Equipment.wearingFullVoid(player, 2)) {
            maxHit *= 1.204;
        }

        if (player.isUsingSpecial() && player.playerEquipment[3] > 0) {
            switch (player.playerEquipment[3]) {
            case 861:
                maxHit *= 1.1;
                break;
            }
        }
        return (int) Math.round(maxHit);
	}
	
	/**
	 * Calculates the range defence of the player
	 * 
	 * @param player
	 * @return
	 */
	public static int calculateRangeDefence(Player player) {
		int rangeDefence = player.getSkills().getLevel(Skills.DEFENCE);
		int rangeBonus = player.playerBonus[9];
		if (player.isActivePrayer(Prayer.THICK_SKIN)) {
			rangeDefence += player.getSkills().getLevelForExperience(Skills.DEFENCE) * 0.05;
		} else if (player.isActivePrayer(Prayer.ROCK_SKIN)) {
			rangeDefence += player.getSkills().getLevelForExperience(Skills.DEFENCE) * 0.10;
		} else if (player.isActivePrayer(Prayer.STEEL_SKIN)) {
			rangeDefence += player.getSkills().getLevelForExperience(Skills.DEFENCE) * 0.15;
		}
		return (int) (rangeDefence + rangeBonus + (rangeBonus / 2));
	}
	
	/**
	 * Calculates the attack bonus of the range attack
	 * 
	 * @param player
	 *            The player attacking
	 * @return
	 */
	public static int calculateRangeAttack(Player player) {
		int rangeLevel = player.getSkills().getLevel(4);
		int rangeBonus = player.playerBonus[4];
		if (player.isActivePrayer(Prayer.SHARP_EYE)) {
			rangeLevel *= player.getSkills().getLevelForExperience(Skills.RANGE) * 1.05;
		} else if (player.isActivePrayer(Prayer.HAWK_EYE)) {
			rangeLevel *= player.getSkills().getLevelForExperience(Skills.RANGE) * 1.10;
		} else if (player.isActivePrayer(Prayer.EAGLE_EYE)) {
			rangeLevel *= player.getSkills().getLevelForExperience(Skills.RANGE) * 1.15;
		}

		if (Equipment.wearingFullVoid(player, 1)) {
			rangeLevel *= 1.1;
		}

		return (int) (rangeLevel + (rangeBonus * 1.95));
	}
}
