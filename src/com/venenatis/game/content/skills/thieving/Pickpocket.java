package com.venenatis.game.content.skills.thieving;

import com.venenatis.game.model.Item;

/**
 * This enum stores all our thieving pickpocket data.
 * @author Patrick van Elderen
 *
 */
public enum Pickpocket {
	MAN(1, 8, new Item[] {new Item(995, 5), new Item(995, 4), new Item(995, 5)}),
	FARMER(60, 65, new Item[] {new Item(5291), new Item(5292), new Item(5293), new Item(5294), new Item(5291), new Item(5292), new Item(5293), new Item(5294), new Item(5295), new Item(5296), new Item(5297), new Item(5298), new Item(5299), new Item(5300), new Item(5301), new Item(5302), new Item(5303), new Item(5304)});
	
	/**
	 * The level required to pickpocket
	 */
	private final int levelRequirement;
	
	/**
	 * The experience gained from the pick pocket
	 */
	private final int experience;
	
	/**
	 * The list of possible items received from the pick pocket
	 */
	private final Item[] rewards;
	
	/**
	 * The npcs we can pickpocket
	 * @param level			
	 *        the level required to steal from
	 * @param experience	
	 *        the experience gained from stealing
	 * @param item			
	 *        the item obtained from stealing, if any
	 */
	private Pickpocket(final int level, final int experience, final Item[] loot) {
		this.levelRequirement = level;
		this.experience = experience;
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
	 * The loot a.k.a reward we receive, this is random at any time
	 * @return rewards
	 */
	public Item[] getLoot() {
		return rewards;
	}
}
