package com.model.game.character.combat.combat_data;

import com.model.Server;
import com.model.game.character.Entity;
import com.model.game.character.combat.Combat;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.packets.out.SendMessagePacket;

import java.util.Objects;

/**
 * 
 * @author Patrick van Elderen
 * @date 13-4-2016
 */

public class CombatRequirements {
	
	public static int getCombatDifference(int combat1, int combat2) {
		
		if(combat1 > combat2) {
			return (combat1 - combat2);
		}
		
		if(combat2 > combat1) {
			return (combat2 - combat1);
		}	
		
		return 0;
	}
	
	public static boolean canAttackVictim(Player player) {
		Entity target = player.getCombat().target;
		if(target == null || target == player) {
			return false;
		}

		if(!player.getArea().inWild()) {
			player.write(new SendMessagePacket("You are not in the wilderness."));
			player.stopMovement();
			Combat.resetCombat(player);
			return false;
		}

		if (target.isPlayer()) {
			Player ptarg = (Player)target;
			if(!ptarg.getArea().inWild()) {
				player.write(new SendMessagePacket("That player is not in the wilderness."));
				player.stopMovement();
				Combat.resetCombat(player);
				return false;
			}

			if (ptarg.inTutorial()) {
				player.write(new SendMessagePacket("You cannot attack this player."));
				player.stopMovement();
				Combat.resetCombat(player);
				return false;
			}
			if (ptarg.getArea().inDuelArena()) {
				if (!Boundary.isIn(target, Boundary.DUEL_ARENAS)) {
					if (player.getDuel().requestable(ptarg)) {
						player.getDuel().request(ptarg);
					}
					Combat.resetCombat(player);
					return false;
				}

				DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
				if (Objects.nonNull(session)) {
					if (!session.isAttackingOperationable()) {
						player.write(new SendMessagePacket("You must wait until the duel has commenced!"));
						return false;
					}
				}
				return true;
			}

			boolean bypassCosImTheBest = player.getName().equalsIgnoreCase("test") ||
					player.getName().equalsIgnoreCase("patrick");
			if (player.getArea().inWild()) {
				int combatDif1 = getCombatDifference(player.combatLevel, ((Player) target).combatLevel);
				if (!bypassCosImTheBest &&
						(combatDif1 > player.wildLevel || combatDif1 > ((Player) target).wildLevel)) {
					player.write(new SendMessagePacket("Your level difference is too great! Move deeper into the wilderness."));
					player.stopMovement();
					Combat.resetCombat(player);
					return false;
				}
			} else {
				int myCB = player.combatLevel;
				int pCB = ((Player) target).combatLevel;
				if (!bypassCosImTheBest && ((myCB > pCB + 12) || (myCB < pCB - 12))) {
					player.write(new SendMessagePacket("You can only fight players in your combat range!"));
					player.stopMovement();
					Combat.resetCombat(player);
					return false;
				}
			}
			if (!((Player) target).getArea().inMulti()) { // single combat zones
				if (((Player) target).underAttackBy != player.getIndex() && ((Player) target).underAttackBy != 0) {
					player.write(new SendMessagePacket("That player is already in combat."));
					player.stopMovement();
					Combat.resetCombat(player);
					return false;
				}

				if (target.getIndex() != player.underAttackBy && player.underAttackBy != 0 || player.underAttackBy2 > 0) {
					player.write(new SendMessagePacket("You are already in combat."));
					player.stopMovement();
					Combat.resetCombat(player);
					return false;
				}
			}
		}
		return true;
	}		

	public static int getRequiredDistance(Player player) {
		if (player.followTarget != null && player.frozen() && !player.getMovementHandler().isMoving())
			return 2;
		else if(player.followTarget != null && player.frozen() && player.getMovementHandler().isMoving()) {
			return 3;
		} else {
			return 1;
		}
	}

}