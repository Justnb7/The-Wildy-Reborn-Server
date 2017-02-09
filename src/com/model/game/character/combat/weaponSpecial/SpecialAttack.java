package com.model.game.character.combat.weaponSpecial;

import com.model.game.character.Entity;
import com.model.game.character.player.Player;

/**
 * The class which represents functionality for a single special attack.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 * @date 13-12-2016
 */
public interface SpecialAttack {

	/**
	 * An array of weapons that can use this special attack
	 * 
	 * @return An array of weapons
	 */
	public abstract int[] weapons();

	/**
	 * Handles the special attack
	 * 
	 * @param player
	 *            The player activating the special attack
	 */
	public void handleAttack(Player player, Entity target);

	/**
	 * The amount of special required to perform the special attack
	 * 
	 * @return The amount of special required
	 */
	public int amountRequired();

	/**
	 * Determines if the entity meets the requirements to activate the special
	 * attack
	 * 
	 * @param player
	 *            The player activating the special attack
	 * @return
	 */
	public boolean meetsRequirements(Player player, Entity target);
	
	/**
	 * The multiplier of the special attack's accuracy
	 * 
	 * @return The special accuracy multiplier
	 */
	public double getAccuracyMultiplier();
	
	/**
	 * The multiplier of the special attack's maximum hit
	 * The multiplier is 1 by default.
	 * 
	 * @return The max hit multiplier
	 */
	public double getMaxHitMultiplier();

}