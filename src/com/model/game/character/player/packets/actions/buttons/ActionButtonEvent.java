package com.model.game.character.player.packets.actions.buttons;

import java.util.List;

import com.model.game.character.player.Player;

/**
 * Represents a single ActionButtonEvent which takes place when a button is
 * pressed
 * 
 * @author Mobster
 *
 */
public interface ActionButtonEvent {

	/**
	 * Handles when a button is clicked
	 * 
	 * @param player
	 *            The {@link Player} clicking the button
	 * @param 
	 *            The id of the button pressed
	 */
	public void onActionButtonClick(Player player, ActionButton button);

	/**
	 * Gets the list of {@link ActionButton}'s
	 * 
	 * @return The list of {@link ActionButton}'s
	 */
	public List<ActionButton> getButtons();

}

