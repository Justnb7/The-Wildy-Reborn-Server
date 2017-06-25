package com.venenatis.game.model.entity.player.account;

import java.util.HashMap;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.account.impl.HardcoreIronManAccount;
import com.venenatis.game.model.entity.player.account.impl.IronManAccount;
import com.venenatis.game.model.entity.player.account.impl.RegularAccount;
import com.venenatis.game.model.entity.player.account.impl.UltimateIronManAccount;

/**
 * The use of this class is fairly simple, it distinguishes what type of account
 * the player is currently assigned to. By default, the account type is regular.
 * 
 * @author Jason MacKeigan
 * @author Patrick van Elderen
 * @date Sep 11, 2014, 8:26:36 PM
 * @edited March 11 2017, 21:42:58 PM
 */
public class Account {

	/**
	 * A Map containing all of the possible AccountTypes available. The key is
	 * the alias of the account type.
	 */
	static HashMap<String, AccountType> types;

	/**
	 * A subclass of AccountType that stores basic information about the regular
	 * account.
	 */
	public static final RegularAccount REGULAR_TYPE = new RegularAccount();

	/**
	 * A subclass of AccountType that stores basic information about the iron
	 * man account.
	 */
	public static final IronManAccount IRON_MAN_TYPE = new IronManAccount();
	
	/**
	 * A subclass of AccountType that stores basic information about the ultimate iron
	 * man account.
	 */
	public static final UltimateIronManAccount ULTIMATE_IRON_MAN_TYPE = new UltimateIronManAccount();
	
	/**
	 * A subclass of AccountType that stores basic information about the hardcore iron
	 * man account.
	 */
	public static final HardcoreIronManAccount HARDCORE_IRON_MAN_TYPE = new HardcoreIronManAccount();

	/**
	 * The type of account, being regular, iron man, etcetera.
	 */
	AccountType type;

	/**
	 * The player associated with this account. All of the member of this class
	 * are specific for the player, and the player only.
	 */
	Player player;

	/**
	 * Constructs a new Account object for the player object.
	 * 
	 * @param player
	 *            the player
	 */
	public Account(Player player) {
		this.player = player;
	}

	/**
	 * Assigns an account type to this player
	 * 
	 * @param type
	 */
	public void setType(AccountType type) {
		this.type = type;
	}

	/**
	 * Determines what type of account this is.
	 * 
	 * @return the type
	 */
	public AccountType getType() {
		if (type == null)
			type = new RegularAccount();
		return type;
	}

	/**
	 * Returns an AccountType object with the same alias as the one passed to
	 * the function. If the alias does not exist, it will return the regular
	 * account type, by default.
	 * 
	 * @param alias
	 *            the alias of the account type
	 * @return the account type
	 */
	public static AccountType get(String alias) {
		for (String key : types.keySet()) {
			if (key.equalsIgnoreCase(alias))
				return types.get(key);
		}
		return null;
	}
	
	/**
	 * Determines if this account type matches another by comparing the name of
	 * the account type to another.
	 * @param type	the type we're comparing to
	 * @return		{@code true} if the names of both accounts match, otherwise {@code false}	
	 */
	public boolean matches(AccountType type) {
		return this.type.alias().equals(type.alias());
	}

	/**
	 * The static constructor that will assist us in storing some basic
	 * information into a hashmap when the class if first referenced. Creating a
	 * new AccountType for each entry may seem a bit odd but it ensures that the
	 * alias used in the creation of the subclass is the same as the key. It
	 * also avoids creating 2 instances instead of 1.
	 */
	static {
		types = new HashMap<>();
		types.put(REGULAR_TYPE.alias(), REGULAR_TYPE);
		types.put(IRON_MAN_TYPE.alias(), IRON_MAN_TYPE);
		types.put(ULTIMATE_IRON_MAN_TYPE.alias(), ULTIMATE_IRON_MAN_TYPE);
		types.put(HARDCORE_IRON_MAN_TYPE.alias(), HARDCORE_IRON_MAN_TYPE);
	}

}