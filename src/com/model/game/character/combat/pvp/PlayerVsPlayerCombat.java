package com.model.game.character.combat.pvp;

import com.model.game.character.combat.Combat;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSessionRules;
import com.model.game.definitions.ItemDefinition;
import com.model.game.item.container.impl.equipment.EquipmentConstants;
import com.model.server.Server;

import java.util.Objects;

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
			player.getMovementHandler().reset();
			Combat.resetCombat(player);
			return false;
		}
		if(!player.getArea().inWild()) {
			player.getActionSender().sendMessage("You are not in the wilderness.");
			player.getMovementHandler().reset();
			Combat.resetCombat(player);
			return false;
		}
		if (target.inTutorial()) {
			player.getActionSender().sendMessage("You cannot attack this player.");
			player.getMovementHandler().reset();
			Combat.resetCombat(player);
			return false;
		}
		if (target.getArea().inDuelArena()) {
			if (!Boundary.isIn(target, Boundary.DUEL_ARENAS)) {
				if (player.getDuel().requestable(target)) {
					player.getDuel().request(target);
				}
				Combat.resetCombat(player);
				return false;
			}

			DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(session)) {
				if (!session.isAttackingOperationable()) {
					player.getActionSender().sendMessage("You must wait until the duel has commenced!");
					return false;
				}
			}
			return true;
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
				player.getMovementHandler().reset();
				Combat.resetCombat(player);
				return false;
			}
		}
		if (!((Player) target).getArea().inMulti()) { // single combat zones
			if (target.lastAttacker != player && Combat.hitRecently(target, 4000)) {
				player.getActionSender().sendMessage("That player is already in combat.");
				player.getMovementHandler().reset();
				Combat.resetCombat(player);
				return false;
			}

			if (target != player.lastAttacker && Combat.hitRecently(player, 4000)) {
				player.getActionSender().sendMessage("You are already in combat.");
				player.getMovementHandler().reset();
				Combat.resetCombat(player);
				return false;
			}
		}
		if (!player.getController().canAttackPlayer(player, (Player) target) && Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL) == null) {
			return false;
		}
		if (Boundary.isIn(player, Boundary.DUEL_ARENAS)) {
			DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
			if (!Objects.isNull(session)) {
				if (session.getRules().contains(DuelSessionRules.Rule.NO_RANGE) && player.getCombatType() == CombatStyle.RANGE) {
					player.getActionSender().sendMessage("<col=CC0000>Range has been disabled in this duel!");
					Combat.resetCombat(player);
					return false;
				}
				if (session.getRules().contains(DuelSessionRules.Rule.NO_MELEE) && (player.getCombatType() != CombatStyle.RANGE && player.getCombatType() != CombatStyle.MAGIC)) {
					player.getActionSender().sendMessage("<col=CC0000>Melee has been disabled in this duel!");
					Combat.resetCombat(player);
					return false;
				}
				if (session.getRules().contains(DuelSessionRules.Rule.NO_MAGE) && player.getCombatType() == CombatStyle.MAGIC) {
					player.getActionSender().sendMessage("<col=CC0000>Magic has been disabled in this duel!");
					Combat.resetCombat(player);
					return false;
				}
				if (session.getRules().contains(DuelSessionRules.Rule.WHIP_AND_DDS)) {
					String weaponName = ItemDefinition.get(player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId()).getName().toLowerCase();
					if (!weaponName.contains("whip") && !weaponName.contains("dragon dagger") || weaponName.contains("tentacle")) {
						player.getActionSender().sendMessage("<col=CC0000>You can only use a whip and dragon dagger in this duel.");
						Combat.resetCombat(player);
						return false;
					}
				}
			}
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