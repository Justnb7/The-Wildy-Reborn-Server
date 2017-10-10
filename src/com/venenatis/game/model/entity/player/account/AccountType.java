package com.venenatis.game.model.entity.player.account;

import com.venenatis.game.content.activity.minigames.impl.pest_control.PestControlRewards.RewardButton;


/**
 * Represents the type of account a singular player has. By default, the element
 * NORMAL is the primary type.
 * 
 * @author Jason MacKeigan
 * @author Patrick van Elderen
 * @date Sep 11, 2014, 8:26:36 PM
 * @edited March 11 2017, 21:42:58 PM
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
	public abstract int gameMode();

	/**
	 * Determines if the account is permitted to see the item drops if they are
	 * not the owner
	 * 
	 * @return true if the type can drop items, otherwise false
	 */
	public abstract boolean canScavageItems();

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
	 * Determines if a any shop is accessible
	 * @return {@code true} if the shop can be accessed, otherwise {@code false}
	 */
	public abstract boolean isShopAccessible();
	
	/**
	 * Determines if the player gains combat experience whilst in PVP
	 * 
	 * @return {@code true} if the player can gain combat experience, otherwise {@code false}
	 */
	public abstract boolean isPVPCombatExperienceGained();
	
	/**
	 * Determines if the particular reward from the pest control mini-game is selectable and ultimately purchasable.
	 * 
	 * @param reward the reward we're trying to select
	 * @return {@code true} if the reward can be selected.
	 */
	public abstract boolean isRewardSelectable(RewardButton reward);

	/**
	 * Determines if the player is permitted to claim items or other rewards from donating.
	 * 
	 * @return {@code true} if the player can claim donated items or rewards, otherwise {@code false}
	 */
	public abstract boolean isDonatingPermitted();
	
	/**
	 * Hardcore ironmen lose their status on death.
	 * @return true if the player loses their ironman status on death.
	 */
	public abstract boolean loseStatusOnDeath();
	
	/**
	 * Ultimate ironman can't bank, they can note/unnnote items when using them on a bank booth.
	 * @return true if the player is an ultimate ironman
	 */
	public abstract boolean canBank();
	
	/**
	 * Ultimate ironman can't use the protection item prayer.
	 * @return true if the player is an ultimate ironman
	 */
	public abstract boolean canUseItemProtection();

}