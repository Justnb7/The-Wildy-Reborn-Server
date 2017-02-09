package com.model.game.character.player.content.questtab;

import java.util.Objects;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendString;

/**
 * Handles the quest tab pages
 * 
 * @author Patrick van Elderen
 *
 */
public class QuestTabPageHandler {

	/**
	 * The key for the quest tab page
	 */
	public static final String QUEST_TAB_PAGE = "QUEST_TAB_PAGE";

	/**
	 * Resets the quest tab page
	 * 
	 * @param player
	 *            The {@link Player} to send the data too
	 */
	public static void reset(Player player) {
		for (int i = 1; i < 100; i++) {
			player.write(new SendString("", getQuestLine(i)));
		}
	}

	/**
	 * Writes the quest tab page
	 * 
	 * @param player
	 *            the {@link Player} to write the quest tab page too
	 * @param page
	 *            The {@link QuestTabPages} to write
	 */
	public static void write(Player player, QuestTabPages page) {
		Objects.requireNonNull(page);

		/*
		 * Reset the page before writing new data
		 */
		reset(player);

		/*
		 * Set the quest tab page for the player
		 */
		player.setAttribute(QUEST_TAB_PAGE, page);

		/*
		 * Fetch our quest tab page
		 */
		QuestTabPage questPage = page.getPage();

		/*
		 * Now write the page
		 */
		questPage.write(player);
	}

	/**
	 * Gets a quest page line
	 * 
	 * @param line
	 *            The line to get
	 * @return The id for the quest page line
	 */
	public static int getQuestLine(int line) {
		return 29501 + line;
	}

	/**
	 * Checks if a player is on a {@link QuestTabPages}
	 * 
	 * @param player
	 *            The {@link Player} to check
	 * @param page
	 *            The {@link QuestTabPages} to check for
	 * @return If the player is on the provided {@link QuestTabPages}
	 */
	public static boolean onQuestTabPage(Player player, QuestTabPages page) {
		return player.getAttribute(QUEST_TAB_PAGE, QuestTabPages.HOME_PAGE) == page;
	}

}
