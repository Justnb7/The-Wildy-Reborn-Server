package com.model.game.character.combat.pvp;

import com.model.Server;
import com.model.game.character.combat.Combat;
import com.model.game.character.combat.combat_data.CombatRequirements;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.ProjectilePathFinder;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSessionRules.Rule;
import com.model.game.character.walking.PathFinder;
import com.model.game.item.container.impl.Equipment;

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
		if (!CombatRequirements.canAttackVictim(player)) {
			return false;
		}
		if (Boundary.isIn(player, Boundary.DUEL_ARENAS)) {
			DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
			if (!Objects.isNull(session)) {
				if (session.getRules().contains(Rule.NO_RANGE) && (player.usingBow || player.getCombatType() == CombatStyle.RANGE)) {
					player.getActionSender().sendMessage("<col=CC0000>Range has been disabled in this duel!");
					Combat.resetCombat(player);
					return false;
				}
				if (session.getRules().contains(Rule.NO_MELEE) && (player.getCombatType() != CombatStyle.RANGE && !player.usingMagic)) {
					player.getActionSender().sendMessage("<col=CC0000>Melee has been disabled in this duel!");
					Combat.resetCombat(player);
					return false;
				}
				if (session.getRules().contains(Rule.NO_MAGE) && player.usingMagic) {
					player.getActionSender().sendMessage("<col=CC0000>Magic has been disabled in this duel!");
					Combat.resetCombat(player);
					return false;
				}
				if (session.getRules().contains(Rule.WHIP_AND_DDS)) {
					String weaponName = player.getItems().getItemName(player.getEquipment().getId(Equipment.WEAPON_SLOT)).toLowerCase();
					if (!weaponName.contains("whip") && !weaponName.contains("dragon dagger") || weaponName.contains("tentacle")) {
						player.getActionSender().sendMessage("<col=CC0000>You can only use a whip and dragon dagger in this duel.");
						Combat.resetCombat(player);
						return false;
					}
				}
			}
		}
		if (target.isDead()) {
			player.getCombat().reset();
			Combat.resetCombat(player);
			return false;
		}
		if (target.heightLevel != player.heightLevel) {
			Combat.resetCombat(player);
			return false;
		}
		boolean sameSpot = player.absX == target.getX() && player.absY == target.getY();
		if (!player.goodDistance(target.getX(), target.getY(), player.getX(), player.getY(), 25) && !sameSpot) {
			Combat.resetCombat(player);
			return false;
		}
		if (player.frozen() && !Combat.isWithinAttackDistanceForStopFollow(player, target)) {
			return false;
		}
		// TODO split into 2 methods, canAttack and canTouch??

		// Always last
		if (player.getCombatType() != CombatStyle.MELEE) {
			if (ProjectilePathFinder.isProjectilePathClear(player.getPosition(), target.getPosition())) {
				return true;
			}
		} else {
			if (ProjectilePathFinder.isInteractionPathClear(player.getPosition(), target.getPosition())) {
				return true;
			}
		}

		PathFinder.getPathFinder().findRoute(player, target.absX, target.absY, true, 1, 1);
		//return false;
		
		return true;
	}

}