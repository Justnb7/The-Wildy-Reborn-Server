package com.venenatis.game.content.chest.impl.crystal_chest;

import com.venenatis.game.model.Item;

public enum CrystalKeyRewardTable {
	
	COMMON(0, 600, 
		new Item[][] { 
			{ new Item(1969, 1) }, //Spinach Roll
			{ new Item(384, 50) }, //Raw shark
			{ new Item(554, 500), new Item(555, 500), 
				new Item(556, 500), new Item(557, 500) }, //Elemental runes
			{ new Item(2437, 10), new Item(2441, 10), new Item(2443, 10) } //10 Super sets
		} 
	),
	UNCOMMON(601, 850, 
		new Item[][] {  
			{ new Item(1604, 5), new Item(1602, 5) }, //Diamons and rubies
			{ new Item(396, 50) }, //Raw sea turtle
			{ new Item(441, 500 ) }, //Iron ore
			{ new Item(985, 1) }, //Tooth key
			{ new Item(987, 1) }, //Loop half
			{ new Item(554, 1000), new Item(555, 1000), 
				new Item(556, 1000), new Item(557, 1000) }, //Elemental runes
			{ new Item(557, 200), new Item(560, 200), new Item(9075, 200) }, //Veng runes
			{ new Item(3025, 20), new Item(6686, 20) } //Brews and Super restores
		} 
	),
	RARE(851, 950, 
		new Item[][] { 
			{ new Item(1079, 1) }, //Rune legs
			{ new Item(1093, 1) }, //Rune skirt
			{ new Item(390, 50) }, //Raw manta rays
			{ new Item(2364, 20) }, //Rune bars
			{ new Item(454, 300) }, //Coal
			{ new Item(554, 2000), new Item(555, 2000), 
				new Item(556, 2000), new Item(557, 2000) }, //Elemental runes
			{ new Item(565, 1000), new Item(560, 1000), new Item(555, 1000) }, //Barrage runes
			{ new Item(12696, 10) } //Super combat potions
		} 
	),
	VERY_RARE(951, 998, 
		new Item[][] {
			{ new Item(4087, 1) }, //Dragon legs
			{ new Item(4585, 1) }, //Dragon skirt
			{ new Item(4587, 1) }, //Dragon scim
			{ new Item(995, 500_000) }//Coins
		} 
	),
	JACKPOT(999, 1000,
		new Item[][] {
			{ new Item(3140, 1) }, //Dragon chainbody
			{ new Item(995, 1_000_000) }, //Coins
		}
	);
	
	/**
	 * The minimum roll out of 1000 to hit this table.
	 */
	private final int minimumRoll;
	
	/**
	 * The maximum roll out of 1000 to hit this table.
	 */
	private final int maximumRoll;
	
	private final Item[][] rewardTable;
	
	private CrystalKeyRewardTable(int minimumRoll, int maximumRoll, Item[][] rewardTable) {
		this.minimumRoll = minimumRoll;
		this.maximumRoll = maximumRoll;
		this.rewardTable = rewardTable;
	}

	public int getMinimumRoll() {
		return minimumRoll;
	}

	public int getMaximumRoll() {
		return maximumRoll;
	}
	
	public Item[][] getRewardTable() {
		return rewardTable;
	}
	
	/**
	 * Get's the CrystalKeyRewardTable for the given roll. Will return a
	 * result that has the roll value between the minumumRoll and
	 * maximumRoll fields.
	 * 
	 * @param roll
	 *            The value of the roll.
	 * @return The CrystalKeyRewardTable that has the roll value between the
	 *         minumumRoll and maximumRoll fields.
	 */
	public static CrystalKeyRewardTable forRoll(final int roll) {
		for (CrystalKeyRewardTable table : values()) {
			if (roll >= table.getMinimumRoll() && roll <= table.getMaximumRoll()) {
				return table;
			}
		}
		return COMMON;
	}
	
}