package com.venenatis.game.model.entity.player.account.impl;

import com.venenatis.game.model.entity.player.account.AccountType;

/**
 * Represents a regular account, the default account type.
 * 
 * @author Jason MacKeigan
 * @author Patrick van Elderen
 * @date Sep 11, 2014, 8:26:36 PM
 * @edited March 11 2017, 21:42:58 PM
 */
public class RegularAccount extends AccountType {

	@Override
	public String alias() {
		return "Regular";
	}

	@Override
	public int gameMode() {
		return 0;
	}

	@Override
	public boolean canScavageItems() {
		return true;
	}

	@Override
	public boolean tradingPermitted() {
		return false;
	}
	
	@Override
	public boolean stakingPermitted() {
		return false;
	}
	
	@Override
	public boolean isShopAccessible() {
		return true;
	}
	
	@Override
	public boolean isPVPCombatExperienceGained() {
		return true;
	}
	
	@Override
	public boolean isDonatingPermitted() {
		return true;
	}

	@Override
	public boolean loseStatusOnDeath() {
		return false;
	}

	@Override
	public boolean canBank() {
		return true;
	}

	@Override
	public boolean canUseItemProtection() {
		return true;
	}

}