package com.venenatis.game.model.entity.player.account.impl;

import java.util.Arrays;
import java.util.List;

import com.venenatis.game.model.entity.player.account.Account;
import com.venenatis.game.model.entity.player.account.AccountType;

/**
 * Represents an iron man account, a type chosen by a player.
 *
 * @author Patrick van Elderen
 */
public class IronManAccount extends AccountType {

    @Override
    public String alias() {
        return "Iron Man";
    }

    @Override
    public int getPrivilege() {
        return 22;
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
		return 1;
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