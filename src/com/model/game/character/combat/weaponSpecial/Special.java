package com.model.game.character.combat.weaponSpecial;

import com.model.game.character.Entity;
import com.model.game.character.combat.combat_data.CombatType;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.player.packets.out.SendString;

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
		
		attacker.getWeaponInterface().sendSpecialBar(attacker.playerEquipment[attacker.getEquipment().getWeaponId()]);
		attacker.getWeaponInterface().refreshSpecialAttack();
		
		if (target == null) {
			return;
		}

		if (!attacker.getController().canUseSpecialAttack(attacker)) {
			return;
		}
		attacker.setUsingSpecial(true);
		attacker.setCombatType(CombatType.MELEE);
		attacker.logoutDelay.reset();
		
		if (attacker.npcIndex > 0) { // indexid of an npc we're attacking
			attacker.oldNpcIndex = target.getIndex();
			
		} else if (attacker.playerIndex > 0) { // playerIndex is the indexId of the player we're attacking
			Player targPlayer = (Player) target; // type cast
			attacker.oldPlayerIndex = target.getIndex();
			targPlayer.putInCombat(attacker.getIndex());
			targPlayer.killerId = attacker.getIndex();
			targPlayer.logoutDelay.reset();
			targPlayer.singleCombatDelay.reset();
			attacker.updateLastCombatAction();
			attacker.setInCombat(true);
		}
		attacker.write(new SendString(""+attacker.getSpecialAmount(), 12001));
		
		int weapon = attacker.playerEquipment[attacker.getEquipment().getWeaponId()];

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
				attacker.write(new SendMessagePacket("You do not have the required special amount."));
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
		int weapon = player.playerEquipment[player.getEquipment().getWeaponId()];
		
		player.setUsingSpecial(false);
		player.getWeaponInterface().refreshSpecialAttack();
		player.getWeaponInterface().sendSpecialBar(weapon);
	}
}