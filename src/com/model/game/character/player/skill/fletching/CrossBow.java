package com.model.game.character.player.skill.fletching;

import com.model.game.character.Animation;

/**
 * Crossbow stringing data for the fletching skill
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public enum CrossBow {
	
	BRONZE_CROSSBOW(1511, 9, 6, 12, 9440, 9420, 9454, 9174, Animation.create(6671)), 
	IRON_CROSSBOW(1519, 24, 16, 32, 9444, 9423, 9457, 9177, Animation.create(6673)), 
	MITHRIL_CROSSBOW(1517, 54, 32, 64, 9448, 9427, 9461, 9181, Animation.create(6675)), 
	RUNE_CROSSBOW(1515, 69, 50, 100, 9452, 9431, 9465, 9185, Animation.create(6677));

	/**
	 * The log type normal, oak, willow, maple, yew, magic
	 */
	private int logType;
	
	/**
	 * The level required
	 */
	private int levelRequirement;
	
	/**
	 * 
	 */
	private int exp1;
	
	/**
	 * 
	 */
	private int exp2;
	
	/**
	 * 
	 */
	private int stock;
	
	/**
	 * The limb Id
	 */
	private int limbs;
	
	/**
	 * The unstrung crossbowId
	 */
	private int unstrungCrossbow;
	
	/**
	 * The reward, a strung crossbow
	 */
	private int crossbow;
	
	/**
	 * The animation
	 */
	private Animation animation;

	public int getLog() {
		return logType;
	}

	public int getLevelReq() {
		return levelRequirement;
	}

	public int getExp1() {
		return exp1;
	}

	public int getExp2() {
		return exp2;
	}

	public int getStock() {
		return stock;
	}

	public int getLimbs() {
		return limbs;
	}

	public int getCrossbow() {
		return crossbow;
	}

	public int getUnstrungCrossbow() {
		return unstrungCrossbow;
	}

	public Animation getAnimation() {
		return animation;
	}

	private CrossBow(int log, int req, int exp, int exp2, int stock, int limbs, int bowU, int bow, Animation animation) {
		this.logType = log;
		this.levelRequirement = req;
		this.exp1 = exp;
		this.exp2 = exp2;
		this.stock = stock;
		this.limbs = limbs;
		this.unstrungCrossbow = bowU;
		this.crossbow = bow;
		this.animation = animation;
	}

	public static CrossBow forID(int logId) {
		for (CrossBow bow : CrossBow.values())
			if (bow.getLog() == logId)
				return bow;
		return null;
	}

	public static CrossBow forUID(int bowU) {
		for (CrossBow bow : CrossBow.values())
			if (bow.getUnstrungCrossbow() == bowU)
				return bow;
		return null;
	}

	public static CrossBow forStockID(int stock) {
		for (CrossBow bow : CrossBow.values())
			if (bow.getStock() == stock)
				return bow;
		return null;
	}
}