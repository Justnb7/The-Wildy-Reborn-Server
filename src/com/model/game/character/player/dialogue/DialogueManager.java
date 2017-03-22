package com.model.game.character.player.dialogue;

import com.model.game.character.player.Player;

/**
 *
 * Handles the {@link Player}s current {@link Dialogue}
 * 
 * @author Erik Eide
 */
public class DialogueManager {

	/**
	 *
	 */
	private final Player player;

	/**
	 * The current dialogue.
	 */
	private Dialogue dialogue = null;

	public DialogueManager(final Player player) {
		this.player = player;
	}

	public boolean input(final int value) {
		if (dialogue != null) {
			dialogue.input(value);
			return true;
		}

		return false;
	}

	public boolean input(final String value) {
		if (dialogue != null) {
			dialogue.input(value);
			return true;
		}

		return false;
	}

	public void interrupt() {
		if (dialogue != null) {
			dialogue.finish();
			dialogue = null;
		}
	}

	public boolean isActive() {
		return dialogue != null;
	}

	public boolean next() {
		if (dialogue != null) {
			dialogue.next();
			return true;
		}

		return false;
	}

	public boolean select(final int index) {
		if (dialogue != null) {
			dialogue.select(index);
			return true;
		}

		return false;
	}

	/**
	 * Starts a dialogue with a new dialogue block instead of repository.
	 *
	 * @param dialogue
	 *            The dialogue to start for the player
	 * @param parameters
	 *            Parameters to pass on to the dialogue
	 */
	public void start(Dialogue dialogue, Object... parameters) {
		this.dialogue = dialogue;
		if (dialogue != null) {
			dialogue.player = player;
			dialogue.start(parameters);
		} else {
			player.getActionSender().sendMessage("Invalid dialogue");
		}
	}

	/**
	 * Starts a new {@link Dialogue} without any parameters
	 * 
	 * @param dialogue
	 *            The {@link Dialogue} to start
	 */
	public void start(Dialogue dialogue) {
		start(dialogue, 0);
	}

	public void start(String name, Object... parameters) {
		dialogue = DialogueRepository.getDialogue(name);

		if (dialogue != null) {
			dialogue.player = player;
			dialogue.start(parameters);
		} else {
			player.getActionSender().sendMessage("Invalid dialogue: " + name);
		}
	}

	/**
	 * Starts a dialogue without any parameters
	 * 
	 * @param name
	 *            The name of the dialogue
	 */
	public void start(String name) {
		start(name, 0);
	}

}