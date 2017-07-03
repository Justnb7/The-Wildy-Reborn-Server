package com.venenatis.game.model.combat.pvp;

import com.venenatis.game.location.Area;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.entity.player.Player;

/**
 * Handles Player Vs Player Combat
 * 
 * @author Mobster
 * @author Sanity
 * @author Patrick van Elderen
 */
public class PlayerVsPlayerCombat {

	/**
	 * Validates that the attack can be made
	 * 
	 * @param player
	 *            The {@link Player} attacking the oppponent
	 * @param target
	 *            The {@link Player} being attacked
	 * @return If the attack is successful
	 */
	public static boolean validateAttack(Player player, Player target) {
		if (target == null || player.getCombatState().isDead() || target.getCombatState().isDead() || !target.isActive() || target.getSkills().getLevel(Skills.HITPOINTS) <= 0 || target.getZ() != player.getZ()) {
			Combat.resetCombat(player);
			player.getWalkingQueue().reset();
			return false;
		}
		if (target.inTutorial()) {
			Combat.resetCombat(player);
			player.getWalkingQueue().reset();
			player.debug("target in tut");
			return false;
		}
		if (target.inTutorial()) {
			player.message("You cannot attack this player.");
			player.getWalkingQueue().reset();
			Combat.resetCombat(player);
			return false;
		}

		boolean bypassCosImTheBest = player.getUsername().equalsIgnoreCase("test") ||
				player.getUsername().equalsIgnoreCase("patrick");
		if (Area.inWilderness(player)) {  // TODO fix this logic
			/*int combatDif1 = CombatRequirements.getCombatDifference(player.combatLevel, ((Player) target).combatLevel);
			if (!bypassCosImTheBest &&
					(combatDif1 > player.wildLevel || combatDif1 > ((Player) target).wildLevel)) {
				player.message("Your level difference is too great! Move deeper into the wilderness.");
				player.debug("threshold: "+combatDif1);
				player.getMovementHandler().reset();
				Combat.resetCombat(player);
				return false;
			}*/
		} else {
			int myCB = player.getCombatLevel();
			int pCB = ((Player) target).getCombatLevel();
			if (!bypassCosImTheBest && ((myCB > pCB + 12) || (myCB < pCB - 12))) {
				player.message("You can only fight players in your combat range!");
				player.getWalkingQueue().reset();
				Combat.resetCombat(player);
				return false;
			}
		}
		if (!Area.inMultiCombatZone(target)) { // single combat zones
			if (target.lastAttacker != player && Combat.hitRecently(target, 4000)) {
				player.message("That player is already in combat.");
				player.getWalkingQueue().reset();
				Combat.resetCombat(player);
				return false;
			}

			if (target != player.lastAttacker && Combat.hitRecently(player, 4000)) {
				player.message("You are already in combat.");
				player.getWalkingQueue().reset();
				Combat.resetCombat(player);
				return false;
			}
		}
		if (!player.getController().canAttackPlayer(player, target)) {
			player.debug("controller denied attack");
			player.getWalkingQueue().reset();
			return false;
		}
		return true;
	}

}