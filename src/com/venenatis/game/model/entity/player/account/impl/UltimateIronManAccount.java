package com.venenatis.game.model.entity.player.account.impl;

import com.venenatis.game.content.activity.minigames.impl.pest_control.PestControlRewards.RewardButton;
import com.venenatis.game.model.entity.player.account.AccountType;

/**
 * Represents an ultimate iron man account, a type chosen by a player.
 *
 * @author Patrick van Elderen
 */
public class UltimateIronManAccount extends AccountType {

	@Override
	public String alias() {
		return "Ultimate Iron Man";
	}

	@Override
	public int gameMode() {
		return 2;
	}

	@Override
	public boolean canScavageItems() {
		return false;
	}

	@Override
	public boolean tradingPermitted() {
		return true;
	}

	@Override
	public boolean stakingPermitted() {
		return true;
	}
	
	@Override
	public boolean isShopAccessible() {
		return false;
	}
	
	@Override
	public boolean isPVPCombatExperienceGained() {
		return false;
	}
	
	@Override
	public boolean isRewardSelectable(RewardButton reward) {
		return false;
	}

	@Override
	public boolean isDonatingPermitted() {
		return false;
	}

	@Override
	public boolean loseStatusOnDeath() {
		return false;
	}

	@Override
	public boolean canBank() {
		return false;
	}

	@Override
	public boolean canUseItemProtection() {
		return false;
	}

}