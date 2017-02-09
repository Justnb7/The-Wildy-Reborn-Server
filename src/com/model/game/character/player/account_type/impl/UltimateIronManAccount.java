package com.model.game.character.player.account_type.impl;

import java.util.Arrays;
import java.util.List;

import com.model.game.character.player.account_type.Account;
import com.model.game.character.player.account_type.AccountType;

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
	public int getPrivilege() {
		return 8;
	}

	@Override
	public boolean unownedDropsVisible() {
		return false;
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
    public List<String> attackableTypes() {
        return Arrays.asList(Account.ULTIMATE_IRON_MAN_TYPE.alias());
    }

	@Override
	public boolean shopAccessible(String shop) {
		return false;
	}

	@Override
	public boolean changable() {
		return false;
	}

	@Override
	public boolean dropAnnouncementVisible() {
		return false;
	}

	@Override
	public int modeType() {
		return 2;
	}

	@Override
	public boolean canGamble() {
		return false;
	}

	@Override
	public boolean canDonate() {
		return false;
	}

	@Override
	public boolean canSpawn() {
		return false;
	}

}
