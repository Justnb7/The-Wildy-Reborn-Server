package com.model.game.character.player.skill.fletching;

import java.util.HashMap;
import java.util.Map;

public enum ArrowTip {
	
	BRONZE(39, 882, 1, 2.6),
	
	IRON(40, 884, 15, 3.8),
	
	STEEL(41, 886, 30, 6.3),
	
	MITHRIL(42, 888, 45, 8.8),
	
	ADAMANT(43, 890, 60, 11.3),
	
	RUNE(44, 892, 75, 13.8)
	;
	
	/**
	 * The id 
	 */
	private int id;
	
	/**
	 * The reward;
	 */
	private int reward;
	
	/**
	 * The level required.
	 */
	private int levelRequired;
	
	/**
	 * The experience granted.
	 */
	private double experience;

	/**
	 * A map of item ids to arrow tips.
	 */
	private static Map<Integer, ArrowTip> arrowtips = new HashMap<Integer, ArrowTip>();

	/**
	 * Gets an arrow tip by an item id.
	 * @param item The item id.
	 * @return The ArrowTip, or <code>null</code> if the object is not a arrow tip.
	 */
	public static ArrowTip forId(int item) {
		return arrowtips.get(item);
	}

	/**
	 * Populates the log map.
	 */
	static {
		for (ArrowTip arrowtip : ArrowTip.values()) {
			arrowtips.put(arrowtip.id, arrowtip);
		}
	}
	
	private ArrowTip(int id, int reward, int levelRequired, double experience) {
		this.id = id;
		this.reward = reward;
		this.levelRequired = levelRequired;
		this.experience = experience;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the reward
	 */
	public int getReward() {
		return reward;
	}

	/**
	 * @return the levelRequired
	 */
	public int getLevelRequired() {
		return levelRequired;
	}

	/**
	 * @return the experience
	 */
	public double getExperience() {
		return experience;
	}
}
