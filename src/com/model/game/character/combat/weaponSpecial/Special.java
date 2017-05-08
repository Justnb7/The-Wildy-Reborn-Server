package com.model.game.character.combat.weaponSpecial;

import com.model.game.character.Entity;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.player.Player;
import com.model.game.item.container.impl.Equipment;

/**
 * The class which represents functionality for the special attack.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 * @date 13-12-2016
 */
public class Special {

	/**
	 * Handles a special attack for a specific player
	 * 
	 * @param attacker
	 *            The player performing the special attack
	 */
	public static void handleSpecialAttack(Player attacker, Entity target) {
		
		attacker.getWeaponInterface().sendSpecialBar(attacker.getEquipment().getId(Equipment.WEAPON_SLOT));
		attacker.getWeaponInterface().refreshSpecialAttack();
		
		if (target == null) {
			return;
		}

		if (!attacker.getController().canUseSpecialAttack(attacker)) {
			return;
		}
		attacker.setUsingSpecial(true);
		attacker.setCombatType(CombatStyle.MELEE);
		attacker.logoutDelay.reset();
		
		if (attacker.getCombat().target.isPlayer()) { // playerIndex is the indexId of the player we're attacking
			Player targPlayer = (Player) target; // type cast
			targPlayer.putInCombat(attacker.getIndex());
			targPlayer.logoutDelay.reset();
			attacker.updateLastCombatAction();
			attacker.setInCombat(true);
		}
		
		int weapon = attacker.getEquipment().getId(Equipment.WEAPON_SLOT);

		if (weapon > 0) {
			SpecialAttack special = SpecialAttackHandler.forId(weapon);

			if (special == null) {
				System.out.println("Invalid special attack: " + weapon);
				resetSpecial(attacker);
				return;
			}

			if (attacker.getSpecialAmount() >= special.amountRequired()) {
				if (special.meetsRequirements(attacker, target)) {
					attacker.setSpecialAmount(attacker.getSpecialAmount() - special.amountRequired());
					special.handleAttack(attacker, target);
				}
			} else {
				attacker.getActionSender().sendMessage("You do not have the required special amount.");
			}
		}
		resetSpecial(attacker);
	}

	/**
	 * Resets the players special attack
	 * 
	 * @param player
	 *            The player resetting the special attack
	 */
	public static void resetSpecial(Player player) {
		int weapon = player.getEquipment().getId(Equipment.WEAPON_SLOT);
		
		player.setUsingSpecial(false);
		player.getWeaponInterface().refreshSpecialAttack();
		player.getWeaponInterface().sendSpecialBar(weapon);
	}
}