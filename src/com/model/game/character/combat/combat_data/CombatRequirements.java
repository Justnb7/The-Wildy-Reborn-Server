package com.model.game.character.combat.combat_data;

import com.model.game.character.player.Player;

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