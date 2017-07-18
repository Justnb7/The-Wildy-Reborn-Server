package com.venenatis.game.model.entity.player.dialogue;

import com.venenatis.game.model.entity.player.Player;

/**
 * An abstract class for handling dialogue options.
 * 
 * @author Professor Oak
 */
public abstract class DialogueOptions {

	public abstract void handleOption(Player player, int option);
}