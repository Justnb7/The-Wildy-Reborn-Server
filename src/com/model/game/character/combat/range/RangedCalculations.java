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

        if (player.getEquipment().wearingBlowpipe(player)) {
            rangeBonus = RangeData.getRangeStr(11230);
            player.playerBonus[4] += RangeData.getRangeStr(11230);
        }
        
        if (player.getEquipment().wearingBallista(player)) {
        	int ammo = player.playerEquipment[player.getEquipment().getQuiverId()];
            rangeBonus = RangeData.getRangeStr(ammo);
            player.playerBonus[4] += RangeData.getRangeStr(ammo);
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
        
        if(player.getEquipment().wearingBallista(player)) {
        	if (player.isUsingSpecial()) {
        		maxHit *= 1.35;
        	} else {
        		maxHit *= 1.08;
        	}
        }
        
        if(player.playerEquipment[player.getEquipment().getAmuletId()] == 19547) {
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
		
		switch(player.playerEquipment[player.getEquipment().getWeaponId()]) {
		case 11785:
			rangeLevel *= 2.0;
			break;
		case 18357:
			rangeLevel *= 2.5;
			break;
		case 21012:
			rangeLevel *= 3.0;
			break;
		}

		return (int) (rangeLevel + (rangeBonus * 1.95));
	}
}
