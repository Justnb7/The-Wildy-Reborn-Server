package com.model.game.character.player.account_type.impl;

import java.util.Arrays;
import java.util.List;

import com.model.game.character.player.account_type.Account;
import com.model.game.character.player.account_type.AccountType;

/**
 * Represents a regular account, the default account type.
 * 
 * @author Jason MacKeigan
 * @date Sep 11, 2014, 8:26:36 PM
 */
public class RegularAccount extends AccountType {

	@Override
	public String alias() {
		return "Regular";
	}

	@Override
	public int getPrivilege() {
		return 0;
	}

	@Override
	public boolean unownedDropsVisible() {
		return true;
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
	public List<String> attackableTypes() {
		return Arrays.asList(Account.REGULAR_TYPE.alias());
	}

	@Override
	public boolean shopAccessible(String shop) {
		return true;
	}

	@Override
	public boolean changable() {
		return true;
	}

	@Override
	public boolean dropAnnouncementVisible() {
		return true;
	}
	
	@Override
	public int modeType() {
		return 0;
	}

	@Override
	public boolean canGamble() {
		return true;
	}

	@Override
	public boolean canDonate() {
		return true;
	}

	@Override
	public boolean canSpawn() {
		return true;
	}

}