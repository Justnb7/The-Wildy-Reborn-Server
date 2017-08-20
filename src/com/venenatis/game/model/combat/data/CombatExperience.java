package com.venenatis.game.model.combat.data;

import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;

/**
 * 
 * @author Patrick van Elderen
 * @date 13-4-2016
 *
 */
public class CombatExperience {

	/**
	 * Add's the combat experience to the player.
	 */
	public static void handleCombatExperience(Player player, int damage, CombatStyle type, boolean drawDmg) {
		
		if(drawDmg) {
			player.getActionSender().sendExperienceCounter(255, damage);
		}
		
		switch (type) {
		
		case MAGIC:
			if (player.getEquipment().contains(19780)) {
				player.getSkills().addExperience(Skills.MAGIC, (4 * damage));
				player.getSkills().addExperience(Skills.HITPOINTS, (damage * 1.33));
			} else {
				player.getSkills().addExperience(Skills.MAGIC, (player.MAGIC_SPELLS[player.getSpellId()][7] + damage));
				player.getSkills().addExperience(Skills.HITPOINTS, (player.MAGIC_SPELLS[player.getSpellId()][7] + damage * 1.33));
			}
			break;
		case MELEE:
			switch (player.getAttackStyle()) {
			case AttackStyle.ACCURATE:
				player.getSkills().addExperience(Skills.ATTACK, (4 * damage));
				break;
			case AttackStyle.AGGRESSIVE:
				player.getSkills().addExperience(Skills.STRENGTH, (4 * damage));
				break;
			case AttackStyle.CONTROLLED:
				for (int skillId = 0; skillId < 3; skillId++) {
					player.getSkills().addExperience(skillId, (4 * damage));
				}
				break;
			case AttackStyle.DEFENSIVE:
				player.getSkills().addExperience(Skills.DEFENCE, (4 * damage));
				break;
			default:
				break;

			}
			player.getSkills().addExperience(Skills.HITPOINTS, (damage * 1.33));
			break;
		case RANGE:
			switch (player.getAttackStyle()) {
			case AttackStyle.ACCURATE:
			case AttackStyle.AGGRESSIVE:
				player.getSkills().addExperience(Skills.RANGE, (4 * damage));
				break;
			case AttackStyle.CONTROLLED:
				player.getSkills().addExperience(Skills.RANGE, (4 * damage));
				player.getSkills().addExperience(Skills.DEFENCE, (4 * damage));
				break;
			}
			player.getSkills().addExperience(Skills.HITPOINTS, (damage * 1.33));
			break;
		default:
			break;
		}
	}
}