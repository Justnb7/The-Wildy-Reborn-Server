package com.model.game.character.player.skill.crafting;

import java.util.HashMap;
import java.util.Map;

import com.model.game.item.Item;

/**
 * Our bolt tip data for the crafting skill
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public enum BoltTip {
	
	OPAL_TIP(11, 4, 1609, new Item(45, 12)),
	
	JADE_TIP(26, 5, 1611, new Item(9187, 12)),
	
	PEARL_TIP(41, 6, 411, new Item(46, 6)),
	
	RED_TOPAZ_TIP(48, 8, 1613, new Item(9188, 12)),
	
	SAPPHIRE_TIP(56, 10, 1607, new Item(9189, 12)),
	
	EMERALD_TIP(58, 15, 1605, new Item(9190, 12)),
	
	RUBY_TIP(63, 18, 1603, new Item(9191, 12)),
	
	DIAMOND_TIP(65, 20, 1601, new Item(9192, 12)),
	
	DRAGONSTONE_TIP(71, 25, 1615, new Item(9193, 12)),
	
	ONYX_TIP(73, 45, 6573, new Item(9194, 24));
	
	/**
	 * Required level
	 */
	private int levelRequirement;
	
	/**
	 * Amount of exp
	 */
	private int experience;
	
	/**
	 * The gem were cutting
	 */
	private int gem;
	
	/**
	 * The bolt tips
	 */
	private Item reward;
	
	/**
	 * The bolttip constructor
	 * @param levelReq
	 * @param xp
	 * @param gem
	 * @param reward
	 */
	BoltTip(int levelReq, int xp, int gem, Item reward) {
		this.levelRequirement = levelReq;
		this.experience = xp;
		this.gem = gem;
		this.reward = reward;
	}
	
	/**
	 * All bolt tips stored in a map
	 */
	private static Map<Integer, BoltTip> gems = new HashMap<Integer, BoltTip>();

	/**
	 * Get the bolt tips by gem id
	 * @param gem
	 *        The cut gem
	 */
	public static BoltTip forId(int gem) {
		return gems.get(gem);
	}

	static {
		for (BoltTip gem : BoltTip.values()) {
			gems.put(gem.gem, gem);
		}
	}

	/**
	 * The level required in order to make these bolt tips
	 * @return levelRequirement
	 */
	public int getLevelReq() {
		return levelRequirement;
	}
	
	/**
	 * The amount of experience we're awarded
	 * @return experience
	 */
	public int getExperience() {
		return experience;
	}
	
	/**
	 * The gem were going to cut
	 * @return gem
	 */
	public int getGem() {
		return gem;
	}
	
	/**
	 * The bolt tips we receive after cutting a gem
	 * @return reward
	 */
	public Item getReward() {
		return reward;
	}
}