package com.venenatis.game.content.achievements;

import java.util.HashMap;

import com.venenatis.game.content.achievements.Achievements.Achievement;
import com.venenatis.game.model.entity.player.Player;

/**
 * Handles the achievement buttons
 * 
 * @author Daniel
 * @author Michael
 */
public class AchievementButtons {

	private static final HashMap<Integer, Achievement> BUTTONS_1 = new HashMap<Integer, Achievement>();
	private static final HashMap<Integer, Achievement> BUTTONS_2 = new HashMap<Integer, Achievement>();
	private static final HashMap<Integer, Achievement> BUTTONS_3 = new HashMap<Integer, Achievement>();

	static {
		int button = 136215;
		button = 136215;
		for (final Achievement achievement : Achievement.asList(AchievementDifficulty.EASY)) {
			BUTTONS_1.put(button++, achievement);
		}
		button = 136215;
		for (final Achievement achievement : Achievement.asList(AchievementDifficulty.MEDIUM)) {
			BUTTONS_2.put(button++, achievement);
		}
		button = 136215;
		for (final Achievement achievement : Achievement.asList(AchievementDifficulty.HARD)) {
			BUTTONS_3.put(button++, achievement);
		}
	}

	public static boolean handleButtons(Player player, int buttonId) {
		if (player.getAttributes().get("ACHIEVEMENT_PAGE") == AchievementDifficulty.EASY && BUTTONS_1.containsKey(buttonId)) {
			AchievementInterface.sendInterfaceForAchievement(player, BUTTONS_1.get(buttonId));
			return true;
		}
		
		if (player.getAttributes().get("ACHIEVEMENT_PAGE") == AchievementDifficulty.MEDIUM && BUTTONS_2.containsKey(buttonId)) {
			AchievementInterface.sendInterfaceForAchievement(player, BUTTONS_2.get(buttonId));
			return true;
		}

		if (player.getAttributes().get("ACHIEVEMENT_PAGE") == AchievementDifficulty.HARD && BUTTONS_3.containsKey(buttonId)) {
			AchievementInterface.sendInterfaceForAchievement(player, BUTTONS_3.get(buttonId));
			return true;
		}
		return false;
	}

}