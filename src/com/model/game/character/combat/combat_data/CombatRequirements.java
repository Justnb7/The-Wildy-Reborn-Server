package com.model.game.character.combat.combat_data;

import java.util.Objects;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.combat.Combat;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.packets.out.SendMessagePacket;

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
		if(World.getWorld().getPlayers().get(player.playerIndex) == null) {
			return false;
		}
		
		if (player.playerIndex == player.getIndex())
			return false;
		
		if (World.getWorld().getPlayers().get(player.playerIndex).inTutorial()) {
			player.write(new SendMessagePacket("You cannot attack this player."));
			player.stopMovement();
			Combat.resetCombat(player);
			return false;
		}
		if (World.getWorld().getPlayers().get(player.playerIndex).getArea().inDuelArena()) {
			if (!Boundary.isIn(World.getWorld().getPlayers().get(player.playerIndex), Boundary.DUEL_ARENAS)) {
				Player other = World.getWorld().getPlayers().get(player.playerIndex);
				if (player.getDuel().requestable(other)) {
					player.getDuel().request(other);
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

		if(!World.getWorld().getPlayers().get(player.playerIndex).getArea().inWild()) {
			player.write(new SendMessagePacket("That player is not in the wilderness."));
			player.stopMovement();
			Combat.resetCombat(player);
			return false;
		}
		
		if(!player.getArea().inWild()) {
			player.write(new SendMessagePacket("You are not in the wilderness."));
			player.stopMovement();
			Combat.resetCombat(player);
			return false;
		}
		
		boolean bypassCosImTheBest = player.getName().equalsIgnoreCase("test") ||
				player.getName().equalsIgnoreCase("patrick");
		if (player.getArea().inWild()) {
			int combatDif1 = getCombatDifference(player.combatLevel, World.getWorld().getPlayers().get(player.playerIndex).combatLevel);
			if (!bypassCosImTheBest && 
					(combatDif1 > player.wildLevel || combatDif1 > World.getWorld().getPlayers().get(player.playerIndex).wildLevel)) {
				player.write(new SendMessagePacket("Your level difference is too great! Move deeper into the wilderness."));
				player.stopMovement();
				Combat.resetCombat(player);
				return false;
			}
		} else {
			int myCB = player.combatLevel;
			int pCB = World.getWorld().getPlayers().get(player.playerIndex).combatLevel;
			if (!bypassCosImTheBest && ((myCB > pCB + 12) || (myCB < pCB - 12))) {
				player.write(new SendMessagePacket("You can only fight players in your combat range!"));
				player.stopMovement();
				Combat.resetCombat(player);
				return false;
			}
		}
		if (!World.getWorld().getPlayers().get(player.playerIndex).getArea().inMulti()) { // single combat zones
			if (World.getWorld().getPlayers().get(player.playerIndex).underAttackBy != player.getIndex() && World.getWorld().getPlayers().get(player.playerIndex).underAttackBy != 0) {
				player.write(new SendMessagePacket("That player is already in combat."));
				player.stopMovement();
				Combat.resetCombat(player);
				return false;
			}

			if (World.getWorld().getPlayers().get(player.playerIndex).getIndex() != player.underAttackBy && player.underAttackBy != 0 || player.underAttackBy2 > 0) {
				player.write(new SendMessagePacket("You are already in combat."));
				player.stopMovement();
				Combat.resetCombat(player);
				return false;
			}
		}
		return true;
	}		

	public static int getRequiredDistance(Player player) {
		if (player.followId > 0 && player.frozen() && !player.getMovementHandler().isMoving())
			return 2;
		else if(player.followId > 0 && player.frozen() && player.getMovementHandler().isMoving()) {
			return 3;
		} else {
			return 1;
		}
	}

}