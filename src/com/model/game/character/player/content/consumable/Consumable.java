package com.model.game.character.player.content.consumable;

import com.model.game.character.player.Player;

/**
 * Represents a consumable item
 * 
 * @author Arithium
 * 
 */
public abstract class Consumable {

	/**
	 * The player consuming the item
	 */
	private Player player;

	/**
	 * The delay before performing another action
	 */
	private long foodDelay;

	/**
	 * The current delay of the task
	 */
	private long currentFoodDelay;

	/**
	 * The potion delay set
	 */
	private long potionDelay;

	/**
	 * The current potion delay
	 */
	private long currentPotionDelay;

	/**
	 * Constructs a new consumable item action
	 * 
	 * @param player
	 *            The player consumign the item
	 * @param delay
	 *            The delay before the player can perform the task again
	 */
	public Consumable(Player player, long foodDelay, long potionDelay) {
		this.player = player;
		this.foodDelay = foodDelay;
		this.potionDelay = potionDelay;
	}

	/**
	 * Returns the tasks initial delay
	 * 
	 * @return
	 */
	public long getDelay(String type) {
		return type.equals("potion") ? potionDelay : foodDelay;
	}

	/**
	 * Sets the delay of the consumable type
	 * 
	 * @param type
	 *            The type of consumable
	 * @param delay
	 *            The delay of the consumable
	 */
	public void setDelay(String type, long delay) {
		if (type.equals("potion")) {
			potionDelay = delay;
		} else {
			foodDelay = delay;
		}
	}

	/**
	 * Sets the current delay after performing the task
	 */
	public void setCurrentDelay(String type) {
		if (type.equals("potion")) {
			currentPotionDelay = System.currentTimeMillis();
		} else {
			currentFoodDelay = System.currentTimeMillis();
		}
	}

	/**
	 * Returns the current delay
	 * 
	 * @return
	 */
	public long getCurrentDelay(String type) {
		return type.equals("potion") ? currentPotionDelay : currentFoodDelay;
	}

	/**
	 * Returns the player
	 * 
	 * @return
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Consumes the consumable item
	 */
	public abstract void consume();

}