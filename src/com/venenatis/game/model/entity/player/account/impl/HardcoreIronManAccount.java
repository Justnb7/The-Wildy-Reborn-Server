package com.venenatis.game.model.entity.player.account.impl;

import com.venenatis.game.model.entity.player.account.AccountType;

/**
 * Represents an ultimate iron man account, a type chosen by a player.
 *
 * @author Patrick van Elderen
 */
public class HardcoreIronManAccount extends AccountType {

	@Override
	public String alias() {
		return "Hardcore Iron Man";
	}

	@Override
	public int gameMode() {
		return 3;
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
	public boolean isDonatingPermitted() {
		return false;
	}

	@Override
	public boolean loseStatusOnDeath() {
		return true;
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
