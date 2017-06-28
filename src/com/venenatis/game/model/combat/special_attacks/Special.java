package com.venenatis.game.model.combat.special_attacks;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;

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
		if (target == null) {
			return;
		}
		
		attacker.getWeaponInterface().sendSpecialBar(attacker.getEquipment().get(EquipmentConstants.WEAPON_SLOT));
		attacker.getWeaponInterface().refreshSpecialAttack();
		attacker.setUsingSpecial(true);

		Item weapon = attacker.getEquipment().get(EquipmentConstants.WEAPON_SLOT);

		if (weapon != null) {
			SpecialAttack special = SpecialAttackHandler.forId(weapon.getId());

			if (special == null) {
				System.out.println("Invalid special attack: " + weapon);
				resetSpecial(attacker);
				return;
			}
			if (!attacker.getController().canUseSpecial(attacker)) {
				attacker.debug("controller says no");
				return;
			}

			if (attacker.getSpecialAmount() >= special.amountRequired()) {
				if (special.meetsRequirements(attacker, target)) {
					attacker.setSpecialAmount(attacker.getSpecialAmount() - special.amountRequired());
					special.handleAttack(attacker, target);

					attacker.debug("speced");
					attacker.logoutDelay.reset();
					if (attacker.getCombatState().getTarget().isPlayer()) { // playerIndex is the indexId of the player we're attacking
						Player targPlayer = (Player) target; // type cast
						targPlayer.putInCombat(attacker.getIndex());
						targPlayer.logoutDelay.reset();
						attacker.updateLastCombatAction();
						attacker.setInCombat(true);
					}
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
		Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		
		player.setUsingSpecial(false);
		player.getWeaponInterface().refreshSpecialAttack();
		player.getWeaponInterface().sendSpecialBar(weapon);
	}
}