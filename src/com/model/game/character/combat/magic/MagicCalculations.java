package com.model.game.character.combat.magic;

import com.model.game.character.combat.PrayerHandler.Prayer;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.item.equipment.Equipment;
import com.model.utility.Utility;

public class MagicCalculations {
	
	public static boolean contraMagic(Player player) {
		return player.playerEquipment[player.getEquipment().getChestId()] == 2503 || player.playerEquipment[player.getEquipment().getChestId()] == 11828 || player.playerEquipment[player.getEquipment().getChestId()] == 4736;
	}

	/**
	 * Returns the players magic defence bonus
	 * 
	 * @param player
	 * @return
	 */
	public static int calculateMagicDefence(Player player) {
		int defenceLevel = (((player.getSkills().getLevel(Skills.DEFENCE) - player.getSkills().getLevelForExperience(Skills.DEFENCE)) / 2) + ((player.getSkills().getLevel(Skills.MAGIC) - player.getSkills().getLevelForExperience(Skills.MAGIC)) / 2)) * 2;
		double formula = (int) (defenceLevel + player.playerBonus[8]);
		if (player.playerBonus[8] > 140)
			formula += (player.playerBonus[8] * 0.6);
		else if (player.playerBonus[8] > 100)
			formula += (player.playerBonus[8] * 0.4);
		else if (player.playerBonus[8] > 80)
			formula += (player.playerBonus[8] * 0.15);
		if (contraMagic(player))
			formula *= 1.4;
		if (player.isActivePrayer(Prayer.THICK_SKIN)) {
			formula += player.getSkills().getLevelForExperience(0) * 0.05;
		} else if (player.isActivePrayer(Prayer.ROCK_SKIN)) {
			formula += player.getSkills().getLevelForExperience(0) * 0.10;
		} else if (player.isActivePrayer(Prayer.STEEL_SKIN)) {
			formula += player.getSkills().getLevelForExperience(0) * 0.15;
		}
		return (int) formula;
	}

	/**
     * Returns the entity's magic hit
     * 
     * @param attacker
     * @return the max hit
     */
	public static int magicMaxHitModifier(Player player) {

        double damage = Utility.getRandom(player.MAGIC_SPELLS[player.oldSpellId][6]);
        double damageMultiplier = 1;

        if (player.getSkills().getLevel(Skills.MAGIC) > player.getSkills().getLevelForExperience(Skills.MAGIC)) {
            damageMultiplier += .03 * (player.getSkills().getLevel(Skills.MAGIC) - player.getSkills().getLevelForExperience(Skills.MAGIC));
        }

        switch (player.playerEquipment[player.getEquipment().getWeaponId()]) {
        case 4675: // Ancient Staff
        case 4710: // Ahrim's Staff
        case 4862: // Ahrim's Staff
        case 4864: // Ahrim's Staff
        case 4865: // Ahrim's Staff
        case 6914: // Master Wand
        case 8841: // Void Knight Mace
            damageMultiplier += 0.10;
            break;
        }

        if (player.playerEquipment[player.getEquipment().getAmuletId()] > 0) {
            switch (player.playerEquipment[player.getEquipment().getAmuletId()]) {
            case 12002: // Occult
                damageMultiplier += 0.10;
                break;
            }
        }

        damage *= damageMultiplier;

        if (damage > player.MAGIC_SPELLS[player.oldSpellId][6])
            damage = player.MAGIC_SPELLS[player.oldSpellId][6];
        return (int) damage;
	}
	
	/**
	 * Returns the players magic attack bonus
	 * 
	 * @param player
	 * @return
	 */
	public static int calculateMagicAttack(Player player) {
		int attack = player.getSkills().getLevel(Skills.MAGIC);
		// TODO: void bonus
		int magicBonus = player.playerBonus[3];
		if (player.isActivePrayer(Prayer.MYSTIC_WILL)) {
			attack *= player.getSkills().getLevelForExperience(0) * 1.05;
		} else if (player.isActivePrayer(Prayer.MYSTIC_LORE)) {
			attack *= player.getSkills().getLevelForExperience(0) * 1.10;
		} else if (player.isActivePrayer(Prayer.MYSTIC_MIGHT)) {
			attack *= player.getSkills().getLevelForExperience(0) * 1.15;
		}
		if (Equipment.wearingFullVoid(player, 2)) {
			attack *= 1.1;
		}
		return (int) (attack + (magicBonus * 2));
	}
	
}
