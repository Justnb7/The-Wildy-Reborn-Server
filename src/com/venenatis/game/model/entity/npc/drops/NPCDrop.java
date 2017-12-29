package com.venenatis.game.model.entity.npc.drops;

/**
 * Represents a single NPCDrop that can be added to a NPC definitions class
 * 
 * @author Stan
 *
 */
public class NPCDrop {

	/**
	 * The in-game item id of this drop.
	 */
	private final int itemId;

	/**
	 * The minimum amount of the item that will be dropped.
	 */
	private final int minAmount;

	/**
	 * The maximum amount of the item that will be dropped.
	 */
	private final int maxAmount;
	
	/**
	 * The chance of the item that will be dropped
	 */
	private int chance;
	
	public NPCDrop(int itemId, int minAmount, int maxAmount) {
		this.itemId = itemId;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.chance = -1;
	}

	public NPCDrop(int itemId, int minAmount, int maxAmount, int chance) {
		this.itemId = itemId;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.chance = chance;
	}

	public int getItemId() {
		return itemId;
	}

	public int getMinAmount() {
		return minAmount;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public int getChance() {
		return chance;
	}
	
	public void setChance(int chance) {
		this.chance = chance;
	}

}