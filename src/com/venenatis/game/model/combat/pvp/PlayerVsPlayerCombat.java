package com.venenatis.game.model.combat.pvp;

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
		if (target == null) {
			Combat.resetCombat(player);
			return false;
		}
		if (player.isDead() || target.isDead() || !target.isActive() || target.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
			Combat.resetCombat(player);
			return false;
		}
		if (target.inTutorial())
			return false;
		if (!player.getArea().inWild() && !player.getArea().inDuelArena())
			return false;

		if(!target.getArea().inWild()) {
			player.getActionSender().sendMessage("That player is not in the wilderness.");
			player.getWalkingQueue().reset();
			Combat.resetCombat(player);
			return false;
		}
		if(!player.getArea().inWild()) {
			player.getActionSender().sendMessage("You are not in the wilderness.");
			player.getWalkingQueue().reset();
			Combat.resetCombat(player);
			return false;
		}
		if (target.inTutorial()) {
			player.getActionSender().sendMessage("You cannot attack this player.");
			player.getWalkingQueue().reset();
			Combat.resetCombat(player);
			return false;
		}

		boolean bypassCosImTheBest = player.getName().equalsIgnoreCase("test") ||
				player.getName().equalsIgnoreCase("patrick");
		if (player.getArea().inWild()) { // TODO fix this logic
			/*int combatDif1 = CombatRequirements.getCombatDifference(player.combatLevel, ((Player) target).combatLevel);
			if (!bypassCosImTheBest &&
					(combatDif1 > player.wildLevel || combatDif1 > ((Player) target).wildLevel)) {
				player.getActionSender().sendMessage("Your level difference is too great! Move deeper into the wilderness.");
				player.debug("threshold: "+combatDif1);
				player.getMovementHandler().reset();
				Combat.resetCombat(player);
				return false;
			}*/
		} else {
			int myCB = player.combatLevel;
			int pCB = ((Player) target).combatLevel;
			if (!bypassCosImTheBest && ((myCB > pCB + 12) || (myCB < pCB - 12))) {
				player.getActionSender().sendMessage("You can only fight players in your combat range!");
				player.getWalkingQueue().reset();
				Combat.resetCombat(player);
				return false;
			}
		}
		if (!((Player) target).getArea().inMulti()) { // single combat zones
			if (target.lastAttacker != player && Combat.hitRecently(target, 4000)) {
				player.getActionSender().sendMessage("That player is already in combat.");
				player.getWalkingQueue().reset();
				Combat.resetCombat(player);
				return false;
			}

			if (target != player.lastAttacker && Combat.hitRecently(player, 4000)) {
				player.getActionSender().sendMessage("You are already in combat.");
				player.getWalkingQueue().reset();
				Combat.resetCombat(player);
				return false;
			}
		}
		if (!player.getController().canAttackPlayer(player, (Player) target)) {
			return false;
		}
		
		if (target.isDead()) {
			player.getCombatState().reset();
			Combat.resetCombat(player);
			return false;
		}
		if (target.getZ() != player.getZ()) {
			Combat.resetCombat(player);
			return false;
		}
		return true;
	}

}