package com.model.game.character.player.account_type.impl;

import java.util.Arrays;
import java.util.List;

import com.model.game.character.player.account_type.Account;
import com.model.game.character.player.account_type.AccountType;

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
        return 7;
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
        return Arrays.asList(Account.IRON_MAN_TYPE.alias(), Account.REGULAR_TYPE.alias());
    }

    @Override
    public boolean shopAccessible(String shop) {
        return true;
    }

    @Override
    public boolean changable() {
        return false;
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