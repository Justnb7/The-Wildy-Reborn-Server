package com.model.game.character.player.packets.buttons;

/**
 * Represents a single action button
 * 
 * @author Mobster
 *
 */
public class ActionButton {

	/**
	 * The id of the button
	 */
	public final int id;

	/**
	 * The {@link ActionButtonEvent} for the button which handles the actions of
	 * the button
	 */
	private final ActionButtonEvent event;

	/**
	 * Constructs a new {@link ActionButton} with an {@link ActionButtonEvent}
	 * 
	 * @param id
	 *            The id of the button
	 * @param event
	 *            The {@link ActionButtonEvent} to handle the buttons actions
	 */
	public ActionButton(int id, ActionButtonEvent event) {
		this.id = id;
		this.event = event;
	}

	/**
	 * Gets the id of the button
	 * 
	 * @return The id of the button
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the {@link ActionButtonEvent} for the button
	 * 
	 * @return The {@link ActionButtonEvent}
	 */
	public ActionButtonEvent getEvent() {
		return event;
	}

}