package com.venenatis.game.content.achievements;

import java.util.HashMap;

import com.venenatis.game.content.achievements.AchievementHandler.AchievementDifficulty;
import com.venenatis.game.model.entity.player.Player;

/**
 * Handles the achievement buttons
 * 
 * @author Daniel
 * @author Michael
 */
public class AchievementButtons {

	private static final HashMap<Integer, AchievementList> BUTTONS_1 = new HashMap<Integer, AchievementList>();
	private static final HashMap<Integer, AchievementList> BUTTONS_2 = new HashMap<Integer, AchievementList>();
	private static final HashMap<Integer, AchievementList> BUTTONS_3 = new HashMap<Integer, AchievementList>();

	static {
		int button = -30505;
		button = -30505;
		for (final AchievementList achievement : AchievementList.asList(AchievementDifficulty.EASY)) {
			BUTTONS_1.put(button++, achievement);
		}
		button = -30505;
		for (final AchievementList achievement : AchievementList.asList(AchievementDifficulty.MEDIUM)) {
			BUTTONS_2.put(button++, achievement);
		}
		button = -30505;
		for (final AchievementList achievement : AchievementList.asList(AchievementDifficulty.HARD)) {
			BUTTONS_3.put(button++, achievement);
		}
	}

	public static boolean handleButtons(Player player, int buttonId) {
		/*case BUTTONS_1.containsKey(buttonId):
			AchievementInterface.sendInterfaceForAchievement(player, BUTTONS_1.get(buttonId));
			return true;

		if (player.attr().get(PlayerAttributes.ACHIEVEMENT_PAGE) == AchievementDifficulty.MEDIUM && BUTTONS_2.containsKey(buttonId)) {
			AchievementInterface.sendInterfaceForAchievement(player, BUTTONS_2.get(buttonId));
			return true;
		}

		if (player.attr().get(PlayerAttributes.ACHIEVEMENT_PAGE) == AchievementDifficulty.HARD && BUTTONS_3.containsKey(buttonId)) {
			AchievementInterface.sendInterfaceForAchievement(player, BUTTONS_3.get(buttonId));
			return true;
		}*/
		return false;
	}

}