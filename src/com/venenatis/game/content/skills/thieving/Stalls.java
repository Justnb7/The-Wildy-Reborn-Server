package com.venenatis.game.content.skills.thieving;

import com.venenatis.game.model.Item;

/**
 * This enum stores all our thieving stalls data.
 * @author Patrick van Elderen
 *
 */
public enum Stalls {
	
	BAKERS_STALL(5, 16, 1, new Item[] {new Item(1891), new Item(1901), new Item(2309)}),
	GEM_STALL(75, 160, 5, new Item[] {new Item(1623), new Item(1621), new Item(1619), new Item(1617)}),
	FUR_STALL(35, 36, 2, new Item[] {new Item(958)}),
	SILVER_STALL(50, 54, 3, new Item[] {new Item(442)}),
	MARKET_STALL(90, 200, 7, new Item[] {new Item(1333), new Item(385), new Item(442), new Item(958), new Item(1623), new Item(1621), new Item(1619), new Item(1617), new Item(1891), new Item(1901), new Item(2309)});
	
	/**
	 * The level required to steal from the stall
	 */
	private final int levelRequirement;
	
	/**
	 * The experience gained in thieving from a single stall thieve
	 */
	private final int experience;
	
	/**
	 * The click timer
	 */
	private final int clickTime;
	
	/**
	 * The rewards received from the stall
	 */
	private final Item[] rewards;
	
	/**
	 * The stalls we can steal from
	 * @param requirement
	 *        the level required to steal from
	 * @param exp
	 *        the experience gained from stealing
	 * @param time
	 *        the time untill we can steal again
	 * @param loot
	 *        the item obtained from stealing
	 */
	private Stalls(final int requirement, final int exp, final int time, final Item[] loot) {
		this.levelRequirement = requirement;
		this.experience = exp;
		this.clickTime = time;
		this.rewards = loot;
	}
	
	/**
	 * The level required in order to steal
	 * @return levelRequirement
	 */
	public int getRequirement() {
		return levelRequirement;
	}
	
	/**
	 * The experience gained
	 * @return experience
	 */
	public int getExperience() {
		return experience;
	}
	
	/**
	 * The clicking timer, we can't spam click for faster experience gain,
	 * or rewards.
	 * @return clickTime
	 */
	public int getClickTimer() {
		return clickTime;
	}
	
	/**
	 * The loot a.k.a reward we receive, this is random at any time
	 * @return rewards
	 */
	public Item[] getLoot() {
		return rewards;
	}

}
