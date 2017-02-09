package com.model.game.character.combat.melee;

import com.model.game.character.Entity;
import com.model.game.character.combat.PrayerHandler.Prayer;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.item.equipment.Equipment;

/**
 * 
 * @author Patrick van Elderen
 * @date 13-4-2016
 */

public class MeleeCalculations {
	
	public static final int STRENGTH_BONUS = 10;
	
	/**
     * Returns the entity's melee max hit.
     * 
     * @param attacker
     * @param target
     * @return the max hit
     */
	public static int calculateMeleeMaxHit(Entity attacker, Entity target) {
		
        Player player = (Player) attacker;
		double specialMultiplier = 1;
		double prayerMultiplier = 1;
		double strengthBonus = player.playerBonus[STRENGTH_BONUS];
		int strengthLevel = player.getSkills().getLevel(Skills.STRENGTH);
		int combatStyleBonus = weaponBonus(player);
		int currentHitpoints = player.getSkills().getLevel(Skills.HITPOINTS);
		int maximumHitpoints = player.getSkills().getLevelForExperience(Skills.HITPOINTS);

		if (player.isActivePrayer(Prayer.BURST_OF_STRENGTH)) {
			prayerMultiplier = 1.05;
		} else if (player.isActivePrayer(Prayer.SUPERHUMAN_STRENGTH)) {
			prayerMultiplier = 1.1;
		} else if (player.isActivePrayer(Prayer.ULTIMATE_STRENGTH)) {
			prayerMultiplier = 1.15;
		} else if (player.isActivePrayer(Prayer.CHIVALRY)) {
			prayerMultiplier = 1.18;
		} else if (player.isActivePrayer(Prayer.PIETY)) {
			prayerMultiplier = 1.23;
		}

		int effectiveStrengthDamage = (int) ((strengthLevel * prayerMultiplier) + combatStyleBonus);
		double base = (13 + effectiveStrengthDamage + (strengthBonus / 8) + ((effectiveStrengthDamage * strengthBonus) * 0.016865)) / 10;
		
		if (Equipment.fullDharok(player)) {
			double dharokEffect = ((maximumHitpoints - currentHitpoints) * 0.01) + 1;
			base *= dharokEffect;
		}

		if (player.isUsingSpecial()) {
			switch (player.playerEquipment[player.getEquipment().getWeaponId()]) {
			case 3101:
			case 3204:
			case 1215:
			case 1231:
			case 5680:
			case 5698:
			case 11802:
				specialMultiplier = 1.1;
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
		
		if (Equipment.wearingFullVoid(player, 0)) {
			base *= 1.05;
		}
		return (int) Math.floor(base);
	}
	
	/**
	 * Retrieves the players attackstyle.
	 * @param player
	 * @return {@link attackStyle EX: CONTROLLED}
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
	
	public static int bestMeleeDef(Player player) {
		if (player.playerBonus[5] > player.playerBonus[6] && player.playerBonus[5] > player.playerBonus[7]) {
			return 5;
		}
		if (player.playerBonus[6] > player.playerBonus[5] && player.playerBonus[6] > player.playerBonus[7]) {
			return 6;
		}
		return player.playerBonus[7] <= player.playerBonus[5] || player.playerBonus[7] <= player.playerBonus[6] ? 5 : 7;
	}

	public static int calculateMeleeDefence(Player player) {
		int defenceLevel = player.getSkills().getLevel(Skills.DEFENCE);
		int i = player.playerBonus[bestMeleeDef(player)];
		if (player.isActivePrayer(Prayer.THICK_SKIN)) {
			defenceLevel += player.getSkills().getLevelForExperience(Skills.DEFENCE) * 0.05;
		} else if (player.isActivePrayer(Prayer.ROCK_SKIN)) {
			defenceLevel += player.getSkills().getLevelForExperience(Skills.DEFENCE) * 0.1;
		} else if (player.isActivePrayer(Prayer.STEEL_SKIN)) {
			defenceLevel += player.getSkills().getLevelForExperience(Skills.DEFENCE) * 0.15;
		} else if (player.isActivePrayer(Prayer.CHIVALRY)) {
			defenceLevel += player.getSkills().getLevelForExperience(Skills.DEFENCE) * 0.20;
		} else if (player.isActivePrayer(Prayer.PIETY)) {
			defenceLevel += player.getSkills().getLevelForExperience(Skills.DEFENCE) * 0.25;
		}
		return (int) (defenceLevel + (defenceLevel * 0.15) + (i + i * 0.05));
	}

	public static int bestMeleeAtk(Player player) {
		if (player.playerBonus[0] > player.playerBonus[1] && player.playerBonus[0] > player.playerBonus[2]) {
			return 0;
		}
		if (player.playerBonus[1] > player.playerBonus[0] && player.playerBonus[1] > player.playerBonus[2]) {
			return 1;
		}
		return player.playerBonus[2] <= player.playerBonus[1] || player.playerBonus[2] <= player.playerBonus[0] ? 0 : 2;
	}

	public static int calculateMeleeAttack(Player player, boolean special) {
		int attackLevel = player.getSkills().getLevel(Skills.ATTACK);
		double specialMultiplier = 1.0;
		if (player.isActivePrayer(Prayer.CLARITY_OF_THOUGHT)) {
			attackLevel += player.getSkills().getLevelForExperience(Skills.ATTACK) * 1.05;
		} else if (player.isActivePrayer(Prayer.IMPROVED_REFLEXES)) {
			attackLevel += player.getSkills().getLevelForExperience(Skills.ATTACK) * 1.1;
		} else if (player.isActivePrayer(Prayer.INCREDIBLE_REFLEXES)) {
			attackLevel += player.getSkills().getLevelForExperience(Skills.ATTACK) * 1.15;
		} else if (player.isActivePrayer(Prayer.CHIVALRY)) {
			attackLevel += player.getSkills().getLevelForExperience(Skills.ATTACK) * 1.15;
		} else if (player.isActivePrayer(Prayer.PIETY)) {
			attackLevel += player.getSkills().getLevelForExperience(Skills.ATTACK) * 1.20;
		}
		
		if (special) {
			switch (player.playerEquipment[player.getEquipment().getWeaponId()]) {
			case 3101:
			case 3204:
			case 1215:
			case 1231:
			case 5680:
			case 5698:
			case 11694:
				specialMultiplier = 1.1;
				break;
			case 1305:
				specialMultiplier = 1.15;
				break;
			case 1434:
				specialMultiplier = 1.45;
				break;
			}
		}
		attackLevel *= specialMultiplier;
		double i = player.playerBonus[bestMeleeAtk(player)];
		if (Equipment.wearingFullVoid(player, 0)) {
			i *= 1.1;
		}
		int outcome = (int) (attackLevel + (attackLevel * 0.15) + (i + i * 0.05));
		return outcome;
	}

}
