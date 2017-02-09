package com.model.game.character.player.account_type;

import java.util.List;

/**
 * Represents the type of account a singular player has. By default, the element
 * NORMAL is the primary type.
 * 
 * @author Jason MacKeigan
 * @date Sep 11, 2014, 3:27:50 PM
 */
public abstract class AccountType {
	/**
	 * A String representation of the type of account. This will assist in
	 * awknowledging exactly what account is what to players.
	 * 
	 * @return
	 */
	public abstract String alias();

	/**
	 * Player rights associated with the account type.
	 * 
	 * @return the player rights associated with the account.
	 */
	public abstract int getPrivilege();

	/**
	 * Determines if the account is permitted to see the item drops if they are
	 * not the owner
	 * 
	 * @return true if the type can drop items, otherwise false
	 */
	public abstract boolean unownedDropsVisible();

	/**
	 * Determins if the account is permitted to trade other players.
	 * 
	 * @return true if the type can trade, otherwise false
	 */
	public abstract boolean tradingPermitted();
	
	/**
	 * Determines if the account type is allowed to stake items.
	 * 
	 * @return true if they can, false if they cannot.
	 */
	public abstract boolean stakingPermitted();
	
	/**
	 * Returns the Collection of Strings that represents each AccountType.
	 * 
	 * @return a collection of account types
	 */
	public abstract List<String> attackableTypes();

	/**
	 * Determines if the shop is accessible to the player. This includes access,
	 * price checking, selling, and buying.
	 * 
	 * @param shop
	 *            the shop we're trying to determine the access of
	 * @return true if we have access, false if we do not.
	 */
	public abstract boolean shopAccessible(String shop);

	/**
	 * Determines if the account mode can be reversed to the default type which
	 * in turn will be a type known as Regular.
	 * 
	 * @return true if it's reversable, false if not.
	 */
	public abstract boolean changable();

	/**
	 * Determines if the drop announcements from certain monsters is visible to
	 * this mode
	 * 
	 * @return true if the drops are visible, false if not.
	 */
	public abstract boolean dropAnnouncementVisible();
	
	/**
	 * Player's game mode type associated with the account type.
	 * 
	 * @return the player's game mode associated with the account.
	 */
	public abstract int modeType();
	
	/**
	 * Determines if the player is allowed to use gambles on
	 * this mode
	 * 
	 * @return true if the player is allowed, false if not.
	 */
	public abstract boolean canGamble();
	
	/**
	 * Determines if the player is allowed to donate on
	 * this mode
	 * 
	 * @return true if the player is allowed, false if not.
	 */
	public abstract boolean canDonate();
	
	/**
	 * Determines if the player is allowed to spawn on
	 * this mode
	 * 
	 * @return true if the player is allowed, false if not.
	 */
	public abstract boolean canSpawn();

}
