package com.venenatis.game.consumables.potion;

/**
 * Represents a single potion effect
 * 
 * @author Arithium
 * 
 */
public interface PotionEffect {

	/**
	 * Handles the potions effect after a player drinks it
	 * 
	 * @param objects
	 */
	public void handle(Object... objects);

}