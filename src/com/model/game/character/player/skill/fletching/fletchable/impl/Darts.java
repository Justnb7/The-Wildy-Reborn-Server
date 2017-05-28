package com.model.game.character.player.skill.fletching.fletchable.impl;

import java.util.HashMap;
import java.util.Map;

public enum Darts {
	
	BRONZE(819, 806, 1, 1.8),
	IRON(820, 807, 22, 3.8),
	STEEL(821, 808, 38, 7.5),
	MITHRIL(822, 809, 52, 11.2),
	ADAMANT(823, 810, 67, 15),
	RUNE(824, 811, 81, 18.8),
	DRAGON(11232, 11230, 95, 25);
	
	/**
	 * The dart tips
	 */
	private final int dartTip;
	
	/**
	 * The feathered dart
	 */
	private final int dart;
	
	/**
	 * The level requirement
	 */
	private final int requirement;
	
	/**
	 * The experience granted
	 */
	private final double experience;
	
	/**
	 * A map of item ids to dart tips.
	 */
	private final static Map<Integer, Darts> tips = new HashMap<Integer, Darts>();
	
	/**
	 * Gets an dart tip by an item id.
	 * 
	 * @param item
	 *            The item id.
	 * @return The DartTip, or <code>null</code> if the object is not a dart
	 *         tip.
	 */
	public static Darts get(int item) {
		return tips.get(item);
	}
	
	/**
	 * Populates the log map.
	 */
	static {
		for (Darts tip : Darts.values()) {
			tips.put(tip.dartTip, tip);
		}
	}
	
	/**
	 * The constructor for our enum data.
	 * 
	 * @param dartTip
	 *            The unfeathered dart tip
	 * @param reward
	 *            The finished dart
	 * @param requirement
	 *            The creation level requirement
	 * @param exp
	 *            The experience we gain
	 */
	private Darts(int dartTip, int reward, int requirement, double exp) {
		this.dartTip = dartTip;
		this.dart = reward;
		this.requirement = requirement;
		this.experience = exp;
	}
	
	/**
	 * @return the dartTip
	 */
	public int getDartTip() {
		return dartTip;
	}

	/**
	 * @return the dart reward
	 */
	public int getDartReward() {
		return dart;
	}

	/**
	 * @return the levelRequired
	 */
	public int getLevelRequired() {
		return requirement;
	}

	/**
	 * @return the experience
	 */
	public double getExperience() {
		return experience;
	}

}
