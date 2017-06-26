package com.venenatis.game.content.quest_tab;

import com.venenatis.game.model.entity.player.Player;

/**
 * Represents a single Quest Tab Page
 * 
 * @author Patrick van Elderen
 *
 */
public abstract class QuestTabPage {

	/**
	 * Writes the content to the quest tab page
	 * 
	 * @param player
	 *            The {@link Player} to write the interface for
	 */
	public abstract void write(Player player);

	/**
	 * Sent when a player clicks a button on the quest tab
	 * 
	 * @param player
	 *            {@link Player} clicking the button
	 * @param button
	 *            The id of the button being pressed
	 */
	public abstract void onButtonClick(Player player, int button);

	/**
	 * Writes a line of text to the quest page
	 * 
	 * @param player
	 *            {@link Player} to write the line for
	 * @param text
	 *            The text to write on the line
	 * @param line
	 *            The line of the quest tab to write the text too
	 */
	public void write(Player player, String text, int line) {
		player.getActionSender().sendString(text, QuestTabPageHandler.getQuestLine(line));
	}

}
