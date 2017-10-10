package com.venenatis.game.content.achievements;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.venenatis.game.model.entity.player.Player;


public class Achievements {
	
	/**
	 * List of all achievements.
	 * 
	 * @author Daniel
	 *
	 */
	public enum Achievement {

		/* Easy Achievements */
		FIRST_KILL("First kill", "Get your first kill in PvP.", 1, AchievementDifficulty.EASY),
		YUM("Yum", "Eat 300 consumables.", 300, AchievementDifficulty.EASY),
		OOH_FANCY("Ooh Fancy", "Setup your first preloading set.", 1, AchievementDifficulty.EASY),
		TASTE_VENGEANCE("Vengeance", "Cast Vengeance 30 times.", 30, AchievementDifficulty.EASY),
		DRAGON_SLAYER("Dragon slayer", "Kill atleast 25 dragons.", 25, AchievementDifficulty.EASY),
		NOVICE_FISHERMAN("Novice Fisherman", "Catch 350 fishies.", 350, AchievementDifficulty.EASY),
		NOVICE_COOK("Novice cook", "Successfully cook 500 consumables.", 500, AchievementDifficulty.EASY),
		NOVICE_MINER("Novice miner", "Mine 350 rocks.", 350, AchievementDifficulty.EASY),
		NOVICE_SMELTER("Novice smelter", "Smelt 250  bars of any kind.", 250, AchievementDifficulty.EASY),
		NOVICE_FARMER("Novice farmer", "Pick 100 herbs.", 100, AchievementDifficulty.EASY),
		NOVICE_SCIENTIST("Novice Scientist", "Mix 250 potions of any kind.", 250, AchievementDifficulty.EASY),
		NOVICE_CHOPPER("Novice chopper", "Chop down more than 1000 trees.", 1000, AchievementDifficulty.EASY),
		NOVICE_FLETCHER("Novice Fletcher", "String atleast 1000 bows.", 1000, AchievementDifficulty.EASY),
		NOVICE_PYRO("Novice Pyro", "Burn atleast 500 logs.", 500, AchievementDifficulty.EASY),
		NOVICE_THIEF("Novice Thief", "Sucessfully steal 500 times.", 500, AchievementDifficulty.EASY),
		NOVICE_RUNNER("Novice runner", "Complete 100 laps of any agility course.", 100, AchievementDifficulty.EASY),
		LAPIDARIST("Lapidarist", "Cut more than 250 gems", 250, AchievementDifficulty.EASY),
		WHATS_IN_THE_BOX("What's in the box?", "Open the mystery box 1 time.", 1, AchievementDifficulty.EASY),

		/* Medium Achievements */
		LEARNING_CURVE("Learning Curve", "Kill 150 players in PvP.", 150, AchievementDifficulty.MEDIUM),
		OUCH("Ouch", "Hit a 35 with DDS special.", 1, AchievementDifficulty.MEDIUM),
		MR_POKEY("Mr Pokey", "Use the Dragon dagger special attack 150 times.", 150, AchievementDifficulty.MEDIUM),
		SPENDER("Spender", "Spend 10,000,000 coins on shop items,", 10_000_000, AchievementDifficulty.MEDIUM),
		BOX_LOVE("Box Love", "Open the mystery box 150 times.", 150, AchievementDifficulty.MEDIUM),
		PEST_CONTROL_ROUNDS("Bug Exterminator", "Complete atleast 50 laps of Pest Control.", 50, AchievementDifficulty.MEDIUM),

		/* Hard Achievements */
		MASTER("Master", "Kill 1,000 players in PvP.", 1_000, AchievementDifficulty.HARD),
		SETTING_THE_RECORD("Setting the Record", "Get a killstreak of 30.", 1, AchievementDifficulty.HARD),
		POPSTAR("Popstar", "Have 50 members in your clan chat channel.", 1, AchievementDifficulty.HARD),
		TOO_OP("Too OP", "Spec 75 with an Armadyl godsword on a player.", 1, AchievementDifficulty.HARD),
		HAMMER_TIME("Hammer Time", "Use the dragon warhammer special on a player 100 \\n times while in PvP.", 100, AchievementDifficulty.HARD),
		RECORD_BREAKER("Record Breaker", "Kill a player with a minimum killstreak of 30.", 1, AchievementDifficulty.HARD),
		KRIL("K'ril", "Kill K'ril Tsutsaroth 200 times.", 200, AchievementDifficulty.HARD),
		CORPOREAL("Corporeal", "Kill Corporeal Beast 350 times.", 350, AchievementDifficulty.HARD),
		GENERAL("General", "Kill General Graardor 500 times.", 500, AchievementDifficulty.HARD),
		KING("King", "Kill King Black Dragon 1,000 times.", 1000, AchievementDifficulty.HARD),
		MYSTERY("Mystery", "Open the mystery box 500 times.", 500, AchievementDifficulty.HARD),
		MASTER_CHEF("Master chef", "Succesfully cook food 1000 times.", 1000, AchievementDifficulty.HARD),
		FISHERMAN("Master fisherman", "Catch a thousand fishes", 1000, AchievementDifficulty.HARD),
		LUMBERJACK("Lumberjack", "Cut a thousand logs", 1000, AchievementDifficulty.HARD),
		;
		
		public static final Set<Achievement> ACHIEVEMENTS = EnumSet.allOf(Achievement.class);

		public static List<Achievement> asList(AchievementDifficulty difficulty) {
			return Arrays.stream(values()).filter(a -> a.getDifficulty() == difficulty).sorted((a, b) -> a.name().compareTo(b.name())).collect(Collectors.toList());
		}

		private final String name;
		private final String description;
		private final int completeAmount;
		private final AchievementDifficulty difficulty;

		private Achievement(String name, String description, int completeAmount, AchievementDifficulty difficulty) {
			this.name = name;
			this.description = description;
			this.completeAmount = completeAmount;
			this.difficulty = difficulty;
		}

		public int getCompleteAmount() {
			return completeAmount;
		}

		public String getDescription() {
			return description;
		}

		public AchievementDifficulty getDifficulty() {
			return difficulty;
		}

		public String getName() {
			return name;
		}

		public static Achievement getAchievement(String name) {
			for (Achievement achievement : Achievement.values())
				if (achievement.getName().equalsIgnoreCase(name))
					return achievement;
			return null;
		}

		public int getReward() {
			switch (difficulty) {
			case MEDIUM:
				return 2;
			case HARD:
				return 3;
			case EASY:
			default:
				return 1;
			}
		}
		
		public static int getTotal() {
			return values().length;
		}
		
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
	public static void activate(Player player, Achievement achievement, int increase) {
		
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
	public static boolean isCompleted(Player player, Achievement achievement) {
		return player.getPlayerAchievements().get(achievement) >= achievement.getCompleteAmount();
	}
	
}
