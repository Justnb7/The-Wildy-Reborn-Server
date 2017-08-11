package com.venenatis.game.content.achievements;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.venenatis.game.content.achievements.AchievementHandler.AchievementDifficulty;

/**
 * List of all achievements.
 * 
 * @author Daniel
 *
 */
public enum AchievementList {

	/* Easy Achievements */
	VIRGIN("Virgin", "Get your first kill in PvP.", 1, AchievementDifficulty.EASY),
	YUM("Yum", "Eat 300 foods.", 300, AchievementDifficulty.EASY),
	BOX_MAN("Box Man", "Open 35 armour sets.", 35, AchievementDifficulty.EASY),
	OOH_FANCY("Ooh Fancy", "Setup your first preloading set.", 1, AchievementDifficulty.EASY),
	FFS_MAN("FFS Man", "Die by poison 5 times.", 5, AchievementDifficulty.EASY),
	SCHOOL_BASICS("School Basics", "Successfully answer 10 TriviaBot questions.", 10, AchievementDifficulty.EASY),
	THE_GIVER("The Giver", "Cast Vengeance other 30 times.", 30, AchievementDifficulty.EASY),
	MONEY_MAKER("Money Maker", "Cast the high alchemy spell 500 times.", 500, AchievementDifficulty.EASY),
	WHATS_IN_THE_BOX("What's in the box?", "Open the mystery box 1 time.", 1, AchievementDifficulty.EASY),

	/* Medium Achievements */
	LEARNING_CURVE("Learning Curve", "Kill 150 players in PvP.", 150, AchievementDifficulty.MEDIUM),
	OUCH("Ouch", "Hit a 35 with DDS special.", 1, AchievementDifficulty.MEDIUM),
	YOUR_COMING_WITH_ME("You're Coming With Me", "Kill a player with the Retribution prayer. ", 1, AchievementDifficulty.MEDIUM), // TODO
	MR_POKEY("Mr Pokey", "Use the Dragon dagger special attack 150 times.", 150, AchievementDifficulty.MEDIUM),
	SPENDER("Spender", "Spend 10,000,000 coins on shop items,", 10_000_000, AchievementDifficulty.MEDIUM),
	TASTE_ME("Taste Me", "Use the Vengeance spell 200 times.", 200, AchievementDifficulty.MEDIUM),
	BOX_LOVE("Box Love", "Open the mystery box 150 times.", 150, AchievementDifficulty.MEDIUM),

	/* Hard Achievements */
	MASTER("Master", "Kill 1,000 players in PvP.", 1_000, AchievementDifficulty.HARD),
	TIME_FOR_LESSON("Time for Lessons", "Obtain 1,000 deaths", 1_000, AchievementDifficulty.HARD),
	SETTING_THE_RECORD("Setting the Record", "Get a killstreak of 30.", 1, AchievementDifficulty.HARD),
	THE_OBESITY_IS_REAL("The Obesity Is Real", "Eat 10,000 foods.", 10_000, AchievementDifficulty.HARD),
	OFFICIALLY_AN_ALCHOLIC("Officially an Alcholic", "Eat 10,000 foods.", 10_000, AchievementDifficulty.HARD),
	POPSTAR("Popstar", "Have 50 members in your clan chat channel.", 1, AchievementDifficulty.HARD),
	TOO_OP("Too OP", "Spec 75 with an Armadyl godsword on a player.", 1, AchievementDifficulty.HARD),
	RICHIE("Richie", "Spend 250,000,000 coins on shop items,", 250_000_000, AchievementDifficulty.HARD),
	CHARGED_UP("Charged Up", "Charge 100 items.", 100, AchievementDifficulty.HARD),
	DUELIST("Duelist", "Win 600 duels.", 600, AchievementDifficulty.HARD),
	BRAINS("Brains", "Successfully answer 200 TriviaBot questions.", 200, AchievementDifficulty.HARD),
	HAMMER_TIME("Hammer Time", "Use the dragon warhammer special on a player 500 \\n times while in PvP.", 500, AchievementDifficulty.HARD),
	RECORD_BREAKER("Record Breaker", "Kill a player with a minimum killstreak of 30.", 1, AchievementDifficulty.HARD),
	KRIL("K'ril", "Kill K'ril Tsutsaroth 200 times.", 200, AchievementDifficulty.HARD),
	CORPOREAL("Corporeal", "Kill Corporeal Beast 350 times.", 350, AchievementDifficulty.HARD),
	GENERAL("General", "Kill General Graardor 500 times.", 500, AchievementDifficulty.HARD),
	KING("King", "Kill King Black Dragon 1,000 times.", 1000, AchievementDifficulty.HARD),
	MYSTERY("Mystery", "Open the mystery box 500 times.", 500, AchievementDifficulty.HARD),
	
	;

	public static List<AchievementList> asList(AchievementDifficulty difficulty) {
		return Arrays.stream(values()).filter(a -> a.getDifficulty() == difficulty).sorted((a, b) -> a.name().compareTo(b.name())).collect(Collectors.toList());
	}

	private final String name;
	private final String description;
	private final int completeAmount;

	private final AchievementDifficulty difficulty;

	private AchievementList(String name, String description, int completeAmount, AchievementDifficulty difficulty) {
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

	public static AchievementList getAchievement(String name) {
		for (AchievementList achievements : AchievementList.values())
			if (achievements.getName().equalsIgnoreCase(name))
				return achievements;
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
