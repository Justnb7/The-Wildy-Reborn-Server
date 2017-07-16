package com.venenatis.game.net;

import com.venenatis.game.model.entity.player.Player;

public class Connection {
	
	/**
	 * Checks if the killer and the opponent are using the same address.
	 * 
	 * @param opponent
	 *            The player that lost the battle
	 * @param killer
	 *            The player who won the battle
	 */
	public static boolean isSameConnection(Player opponent, Player killer) {
		return killer.getHostAddress().equals(opponent.getHostAddress()) || killer.getMacAddress().equals(opponent.getMacAddress()) || killer.getIdentity().equals(opponent.getIdentity());
	}

}
