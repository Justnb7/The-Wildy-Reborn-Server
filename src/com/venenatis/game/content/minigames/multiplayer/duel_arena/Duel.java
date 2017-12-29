package com.venenatis.game.content.minigames.multiplayer.duel_arena;

import com.venenatis.game.model.entity.player.Player;

public class Duel {

	private final Player requester;
	private final Player requestee;
	
	public Duel(final Player requester, final Player requestee) {
		this.requester = requester;
		this.requestee = requestee;
	}

	/**
	 * @return the requester
	 */
	public Player getRequester() {
		return requester;
	}

	/**
	 * @return the requestee
	 */
	public Player getRequestee() {
		return requestee;
	}
	
}