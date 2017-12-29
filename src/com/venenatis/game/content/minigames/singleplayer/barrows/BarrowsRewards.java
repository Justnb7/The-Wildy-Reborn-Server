package com.venenatis.game.content.minigames.singleplayer.barrows;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of all barrows item rewards
 * 
 * @author Stan
 * 
 */

public enum BarrowsRewards {	
		
	MIND_RUNE(558, 1400, 381, 505, 75),
	CHAOS_RUNE(562, 450, 506, 630, 70),
	DEATH_RUNE(560, 170, 631, 755, 65),
	BLOOD_RUNE(565, 80, 756, 880, 60),
	BOLT_RACK(4740, 240, 881, 1005, 15),
	TOOTH_HALF(985, 1, 1006, -1, 3),
	LOOP_HALF(987, 1, 1006, -1, 3),
	DRAGON_MED(1149, 1, 1012, -1, 1);
	
	private BarrowsRewards(int itemId, int maximumQuantity, int amountToUnlock, int quantityRequirement, int chance) {
		this.itemId = itemId;
		this.maximumQuantity = maximumQuantity;
		this.amountToUnlock = amountToUnlock;
		this.quantityRequirement = quantityRequirement;
		this.chance = chance;
	}
	
	public static List<BarrowsRewards> getPossibleRewards(int chance) {
		List<BarrowsRewards> defs = new ArrayList<>();
		for (BarrowsRewards def : values()) {
			if (def.getAmountToUnlock() <= chance) {
				defs.add(def);
			}
		}
		return defs;
	}
	
	public int getItemId() {
		return itemId;
	}

	public int getMaximumQuantity() {
		return maximumQuantity;
	}

	public int getAmountToUnlock() {
		return amountToUnlock;
	}

	public int getQuantityRequirement() {
		return quantityRequirement;
	}

	public int getChance() {
		return chance;
	}

	/**
	 * The id of the item reward
	 */	
	private final int itemId;
	
	/**
	 * The maximum quantity that can be rewarded of the item
	 */
	private final int maximumQuantity;
	
	/**
	 * Combat killcount needed for this item to be on the roll
	 */
	private final int amountToUnlock;
	
	/**
	 * Combat killcount needed for max quantity of the item
	 */
	private final int quantityRequirement;
	
	/**
	 * The percentage chance to roll this item (Adds onto other chances, so becomes lower)
	 */
	private final int chance;
	
}