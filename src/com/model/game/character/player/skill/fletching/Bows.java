package com.model.game.character.player.skill.fletching;

import com.model.game.character.Animation;

/**
 * Bow stringing data for the fletching skill
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public enum Bows {
	
	SHORTBOW(1511, 1, 5, 50, 841, Animation.create(6678), "short"), 
	OAK_SHORTBOW(1521, 20, 17, 54, 843, Animation.create(6679), "short"), 
	WILLOW_SHORTBOW(1519, 35, 33, 60, 849, Animation.create(6680), "short"), 
	MAPLE_SHORTBOW(1517, 50, 50, 64, 853, Animation.create(6681), "short"), 
	YEW_SHORTBOW(1515, 65, 68, 68, 857, Animation.create(6682), "short"), 
	MAGIC_SHORTBOW(1513, 80, 83, 72, 861, Animation.create(6683), "short"), 
	LONGBOW(1511, 10, 10, 48, 839, Animation.create(6684), "long"), 
	OAK_LONGBOW(1521, 25, 25, 56, 845, Animation.create(6685), "long"), 
	WILLOW_LONGBOW(1519, 40, 42, 60, 847, Animation.create(6686), "long"), 
	MAPLE_LONGBOW(1517, 55, 58, 62, 851, Animation.create(6687), "long"), 
	YEW_LONGBOW(1515, 65, 70, 66, 855, Animation.create(6688), "long"), 
	MAGIC_LONGBOW(1513, 85, 92, 70, 859, Animation.create(6689), "long");
	
	/**
	 * The log type normal, oak, willow, maple, yew, magic
	 */
	private int logType;
	
	/**
	 * The level requirement to string
	 */
	private int levelRequirement;
	
	/**
	 * The experience table
	 */
	private int experience;
	
	/**
	 * The unstrung bow
	 */
	private int unstrungBow;
	
	/**
	 * The reward
	 */
	private int bowStrung;
	
	/**
	 * The animation
	 */
	private Animation animation;

	/**
	 * The type of the bow longbow or shortbow
	 */
	private String bowType = "";

	/**
	 * The log were cutting
	 * @return logType
	 */
	public int getLog() {
		return logType;
	}

	/**
	 * The level we require in order to string a bow
	 * @return levelRequirement
	 */
	public int getLevelReq() {
		return levelRequirement;
	}

	/**
	 * The experience we receive
	 * @return experience
	 */
	public int getExperience() {
		return experience;
	}

	/**
	 * The unstrung bow we want to attach with a string
	 * @return unstrungBow
	 */
	public int getUnstrungBow() {
		return unstrungBow;
	}

	/**
	 * The reward we receive, a strung bow
	 * @return bowStrung
	 */
	public int getStrungBow() {
		return bowStrung;
	}

	/**
	 * Sents the animation
	 * @return animation
	 */
	public Animation getAnimation() {
		return animation;
	}

	/**
	 * Gets the bow type bows can be short and long
	 * @return bowType
	 */
	public String getBowType() {
		return bowType;
	}

	/**
	 * Creates the bow data
	 * @param log
	 * @param requirement
	 * @param experience
	 * @param untrung
	 * @param strung
	 * @param animation
	 * @param type
	 */
	private Bows(int log, int requirement, int experience, int untrung, int strung, Animation animation, String type) {
		this.logType = log;
		this.levelRequirement = requirement;
		this.experience = experience;
		this.unstrungBow = untrung;
		this.bowStrung = strung;
		this.animation = animation;
		this.bowType = type;
	}
}
