package com.venenatis.game.content.achievements;

import com.venenatis.game.model.entity.player.Player;

/**
 * Handles the achievements
 * 
 * @author Daniel
 * 
 */
public class AchievementHandler {

	/**
	 * Holds the types of achievements
	 * 
	 */
	public enum AchievementDifficulty {
		EASY,
		MEDIUM,
		HARD
	}

	/**
	 * Activates the achievement for the individual player. Increments the
	 * completed amount for the player. If the player has completed the
	 * achievement, they will receive their reward.
	 * 
	 * @param player
	 *            The player activating the achievement.
	 * @param achievement
	 *            The achievement for activation.
	 */
	public static void activate(Player player, AchievementList achievement, int increase) {
		if (increase == -1) {
			return;
		}
		
		if (achievement == null) {
			return;
		}

		if (player.getPlayerAchievements().get(achievement) >= achievement.getCompleteAmount()) {
			return;
		}

		final int current = player.getPlayerAchievements().get(achievement);

		if (current == 0) {
			player.getActionSender().sendMessage("<col=297A29>You have started the achievement: " + achievement.getName() + ".");
		}

		player.getPlayerAchievements().put(achievement, current + increase);

		if (player.getPlayerAchievements().put(achievement, current + increase) >= achievement.getCompleteAmount()) {
			AchievementInterface.sendCompleteInterface(player, achievement);
			player.setAchievementPoints(player.getAchievementsPoints() + achievement.getReward());
			int points = player.getAchievementsPoints();
			player.getActionSender().sendMessage("<col=297A29>Congratulations! You have completed an achievement. You now have " + points + " point" + (points == 1 ? "" : "s") + ".");
		}
	}

	/**
	 * Checks if the reward is completed.
	 * 
	 * @param player
	 *            The player checking the achievement.
	 * @param achievement
	 *            The achievement for checking.
	 */
	public static boolean isCompleted(Player player, AchievementList achievement) {
		return player.getPlayerAchievements().get(achievement) >= achievement.getCompleteAmount();
	}

}