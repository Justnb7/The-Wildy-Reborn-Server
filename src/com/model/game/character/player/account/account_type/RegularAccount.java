package com.model.game.character.player.account.account_type;

import java.util.Arrays;
import java.util.List;

import com.model.game.character.player.account.Account;
import com.model.game.character.player.account.AccountType;

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
	public int getPrivilege() {
		return 0;
	}

	@Override
	public boolean unownedDropsVisible() {
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
	public List<String> attackableTypes() {
		return Arrays.asList(Account.REGULAR_TYPE.alias(), Account.IRON_MAN_TYPE.alias(), Account.ULTIMATE_IRON_MAN_TYPE.alias(), Account.HARDCORE_IRON_MAN_TYPE.alias());
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