package com.venenatis.game.content.achievements;

import java.util.Arrays;
import java.util.List;

import com.venenatis.game.content.achievements.Achievements.Achievement;
import com.venenatis.game.content.interfaces.InterfaceWriter;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.StringUtils;
import com.venenatis.game.util.Utility;

/**
 * Handles the achievement interfaces
 * 
 * @author Daniel
 * @author Michael
 */
public class AchievementInterface extends InterfaceWriter {

	/**
	 * Sends the achievement completion interface
	 * 
	 * @param player
	 * @param achievement
	 */
	public static void sendCompleteInterface(final Player player, final Achievement achievement) {
		int color = 0;

		switch (achievement.getDifficulty()) {
		case EASY:
			color = 0x1C889E;
			break;
		case MEDIUM:
			color = 0xD9750B;
			break;
		case HARD:
			color = 0xC41414;
			break;
		}

		player.getActionSender().sendBanner("You've completed an achievement!", achievement.getName(), color);

	}

	/**
	 * Sends the achievement information interface
	 * 
	 * @param player
	 * @param achievement
	 */
	public static void sendInterfaceForAchievement(final Player player, Achievement achievement) {
		final String difficulty = StringUtils.capitalize(achievement.getDifficulty().name().toLowerCase());
		final int completed = player.getPlayerAchievements().get(achievement);
		final int progress = (int) (completed * 100 / (double) achievement.getCompleteAmount());
		player.getActionSender().sendString("<col=ff9040>" + achievement.getName(), 35006);
		player.getActionSender().sendString("<col=ff7000>" + achievement.getDescription(), 35008);
		player.getActionSender().sendString("<col=ff7000>" + difficulty, 35010);
		player.getActionSender().sendString("<col=ff7000>" + Utility.formatDigits(completed) + " / " + Utility.formatDigits(achievement.getCompleteAmount()) + " ( " + progress + "% )", 35012);
		player.getActionSender().sendString("<col=ff7000>" + achievement.getReward() + " achievement point" + (achievement.getReward() == 1 ? "" : "s") + ".", 35014);
		boolean isCompleted = completed >= achievement.getCompleteAmount();
		player.getActionSender().sendConfig(694, isCompleted ? 1 : 0);
	}

	private final String[] text;

	public AchievementInterface(Player player, AchievementDifficulty difficulty) {
		super(player);
		int shift = 0;

		final int total = Achievement.values().length;
		
		switch (difficulty) {
		case EASY:
			player.getActionSender().sendScrollBar(35030, 0);
			break;
		case MEDIUM:
			player.getActionSender().sendScrollBar(35030, 0);
			break;
		case HARD:
			player.getActionSender().sendScrollBar(35030, 350);
			break;
		}

		player.getActionSender().sendString("</col>Completed: <col=65280>" + player.achievementsCompleted() + "</col>/" + total, 35015);
		player.getActionSender().sendString("</col>Points: <col=65280>" + player.getAchievementsPoints(), 35016);

		final List<Achievement> list = Achievement.asList(difficulty);

		text = new String[total];

		Arrays.fill(text, "");

		for (final Achievement achievement : list) {
			int completed = player.getPlayerAchievements().get(achievement);
			if (completed > achievement.getCompleteAmount()) {
				completed = achievement.getCompleteAmount();
			}
			
			int color = completed == achievement.getCompleteAmount() ? 0x00FF00 : completed > 0 ? 0xFFFF00 : 0xFF0000;
			
			player.getActionSender().sendStringColor(startingLine() + shift, color);
			
			text[shift++] = " " + achievement.getName();
		}
	}

	public String getColor(int amount, int max) {
		if (amount == 0) {
			return "<col=FF0000>";
		}
		if (amount >= max) {
			return "<col=00FF00>";
		}
		return "<col=FFFF00>";
	}

	@Override
	protected int startingLine() {
		return 35031;
	}

	@Override
	protected String[] text() {
		return text;
	}

}