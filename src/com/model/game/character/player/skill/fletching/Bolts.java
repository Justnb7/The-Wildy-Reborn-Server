package com.model.game.character.player.skill.fletching;

/**
 * Bolt data for the fletching skill
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public enum Bolts {
	
	BRONZE(9375, -1, 877, 9, 1, "bolt"),
	IRON(9377, -1, 9140, 39, 2, "bolt"),
	STEEL(9378, -1, 9141, 46, 4, "bolt"),
	MITHRIL(9379, -1, 9142, 54, 5, "bolt"),
	ADAMANT(9380, -1, 9143, 61, 7, "bolt"),
	RUNE(9381, -1, 9144, 69, 10, "bolt"),
	OPAL(877, 45, 879, 11, 2, "boltGem"),
	PEARL(9140, 46, 880, 41, 3, "boltGem"),
	REDTOPAZ(9141, 9188, 9336, 48, 4, "boltGem"),
	SAPPHIRE(9142, 9189, 9337, 56, 5, "boltGem"),
	EMERALD(9142, 9190, 9338, 58, 6, "boltGem"),
	RUBY(9143, 9191, 9339, 63, 6, "boltGem"),
	DIAMOND(9143, 9192, 9340, 65, 7, "boltGem"),
	DRAGON(9144, 9193, 9341, 71, 8, "boltGem"),
	ONYX(9144, 9194, 9342, 73, 9, "boltGem"),
	MITHRIL_DART(822, -1, 809, 52, 1, "bolt"),
	ADAMANT_DART(823, -1, 810, 52, 18, "bolt"),
	RUNITE_DART(824, -1, 811, 52, 23, "bolt");

	/**
	 * Unfinished bolts
	 */
	private final int unfinishedBolts;
	
	/**
	 * The bolt tips
	 */
	private final int  boltTips;
	
	/**
	 * The reward
	 */
	private final int boltsReward;
	
	/**
	 * Level required to create bolts
	 */
	private final int levelRequirement;
	
	/**
	 * Experience gained
	 */
	private final int experience;
	
	/**
	 * The type bolts or boltgems
	 */
	private String type = "";

	/**
	 * The unfinished bolts
	 * @return
	 */
	public int getUnfBolts() {
		return unfinishedBolts;
	}

	/**
	 * The bolt tips
	 * @return boltTips
	 */
	public int getBoltTips() {
		return boltTips;
	}

	/**
	 * The reward
	 * @return boltsReward
	 */
	public int getReward() {
		return boltsReward;
	}

	/**
	 * Level required
	 * @return levelRequirement
	 */
	public int getLevelRequirement() {
		return levelRequirement;
	}

	/**
	 * Amount of experience we receive for creating bolts
	 * @return
	 */
	public int getExperience() {
		return experience;
	}

	/**
	 * The bolt type
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/***
	 * Creates the bolt(s)
	 * @param unfBolts
	 * @param tips
	 * @param reward
	 * @param levelReq
	 * @param exp
	 * @param type
	 */
	private Bolts(int unfBolts, int tips, int reward, int levelReq, int exp, String type) {
		this.unfinishedBolts = unfBolts;
		this.boltTips = tips;
		this.boltsReward = reward;
		this.levelRequirement = levelReq;
		this.experience = exp;
		this.type = type;
	}
}
