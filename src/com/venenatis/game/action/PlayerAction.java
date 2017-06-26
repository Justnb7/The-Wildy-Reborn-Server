package com.venenatis.game.action;

import com.venenatis.game.model.entity.player.Player;

public abstract class PlayerAction extends Action {
	
	protected final Player player;
	
	protected PlayerAction(Player player) {
		this(player, 1);
	}

	public PlayerAction(Player player, int ticks) {
		super(player, ticks);
		this.player = player;
	}
	
	/**
	 * Contains checks for the action to continue cycling
	 */
	public abstract boolean validated();

}