package com.model.game.character.player.skill.fletching;

/**
 * Bolt data for the fletching skill
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public enum Arrows {
	
	BRONZE(1, 3, 39, 882), 
	IRON(15, 4, 40, 884), 
	STEEL(30, 6, 41, 886), 
	MITHRIL(45, 9, 42, 888), 
	ADAMANT(60, 10, 43, 890), 
	RUNE(75, 14, 44, 892), 
	DRAGON(90, 16, 11237, 11212);

	/**
	 * The level requirement
	 */
	private int levelRequirement;
	
	/**
	 * Experience gained
	 */
	private int experience;
	
	/**
	 * Arrow tips
	 */
	private int tips;
	
	/**
	 * Arrows reward
	 */
	private int arrow;

	/**
	 * The arrow tips required to make arrows
	 * @return tips
	 */
	public int getTips() {
		return tips;
	}
	
	/**
	 * The level required to make arrows
	 * @return levelRequirement
	 */
	public int getLevelReq() {
		return levelRequirement;
	}
	
	/**
	 * The experience we receive for making arrows
	 * @return experience
	 */
	public int getExperience() {
		return experience;
	}
	
	/**
	 * The arrows reward
	 * @return arrow
	 */
	public int getArrow() {
		return arrow;
	}

	/**
	 * The arrows creator
	 * @param req
	 * @param exp
	 * @param tips
	 * @param arrow
	 */
	private Arrows(int req, int exp, int tips, int arrow) {
		this.levelRequirement = req;
		this.experience = exp;
		this.tips = tips;
		this.arrow = arrow;
	}

	/**
	 * Gets the arrowTips by item id
	 * @param tips
	 *        The arrow tips
	 */
	public static Arrows forId(int tips) {
		for (Arrows arrow : Arrows.values())
			if (arrow.getTips() == tips)
				return arrow;
		return null;
	}
}
