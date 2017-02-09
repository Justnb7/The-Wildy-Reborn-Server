package com.model.game.character.player.packets.actions.buttons;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.model.game.character.player.Player;

/**
 * Listens for action button events to be triggered
 * 
 * @author Mobster
 *
 */
public class ActionButtonEventListener {
	
	/**
	 * A list to store all of the buttons
	 */
	private static List<ActionButton> buttons = new ArrayList<>();
	
	static {
		submit(new PlayerSettingsActionButtonEvent());
	}
	
	/**
	 * Stores all of the buttons into a list
	 */
	public static void submit(ActionButtonEvent event) {
		buttons.addAll(event.getButtons());
	}
	
	/**
	 * Listens for an {@link ActionButtonEvent} to take place and handles it.
	 * 
	 * @param player
	 *            The {@link Player} clicking the button
	 * @param buttonId
	 *            The id of the button pressed
	 * @return If a button was pressed
	 */
	public static void onButtonClick(Player player, int buttonId) {
		Optional<ActionButton> opt = buttons.stream().filter(button -> button.getId() == buttonId).findAny();
		if (opt.isPresent()) {
			opt.get().getEvent().onActionButtonClick(player, opt.get());
		}
	}

}
