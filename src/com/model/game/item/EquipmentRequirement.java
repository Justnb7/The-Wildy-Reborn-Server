package com.model.game.item;

import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.skill.SkillData;
import com.model.game.character.player.skill.SkillRequirement;
import com.model.game.definitions.EquipmentDefinition;
import com.model.utility.Utility;

/**
 * Represents the requirements for equipping items.
 * 
 * @author Seven
 */
public class EquipmentRequirement extends SkillRequirement {

	/**
	 * Creates a new {@link EquipmentRequirement}.
	 *
	 * @param level
	 *            The level required
	 *
	 * @param skill
	 *            The skill required.
	 */
	public EquipmentRequirement(int level, SkillData skill) {
		super(level, skill);
	}

	/**
	 * Determines if a player can equip a specified item.
	 * 
	 * @param player
	 *            The player that is equipping the item.
	 * 
	 * @param itemId
	 *            The id of the item to check.
	 */
	public static boolean canEquip(Player player, int itemId) {
		final EquipmentDefinition req = EquipmentDefinition.EQUIPMENT_DEFINITIONS.get(itemId);
		if (req != null) {
			for (final SkillRequirement r : req.getRequirements()) {

				if (r == null) {
					continue;
				}
				
				if (r.getSkill().getId() == Skills.PRAYER || r.getSkill().getId() == Skills.HITPOINTS) {
					if (player.getSkills().getXPForLevel(r.getSkill().getId()) < r.getLevel()) {
						player.getActionSender().sendMessage("You need " + Utility.getAOrAn(r.getSkill().toString()) + " " + r.getSkill().toString() + " level of " + r.getLevel() + " to equip this item.");
						return false;
					}
				} else {
					if (player.getSkills().getLevel(r.getSkill().getId()) < r.getLevel()) {
						player.getActionSender().sendMessage("You need " + Utility.getAOrAn(r.getSkill().toString()) + " " + r.getSkill().toString() + " level of " + r.getLevel() + " to equip this item.");
						return false;
					}
				}
			}
		}
		return true;
	}

}