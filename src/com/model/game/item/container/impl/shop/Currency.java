package com.model.game.item.container.impl.shop;

import com.model.game.character.player.Player;
import com.model.game.item.Item;

public enum Currency {

	/**
	 * Coins
	 */
	COINS(new CurrencyUtility() {

		@Override
		public int addCurrency(Player player, int amount) {
			if (!player.getInventory().hasSpaceFor(new Item(995, amount))) {
				return 0;
			}
			return player.getInventory().add(995, amount);
		}

		@Override
		public int removeCurrency(Player player, int amount, int minimum) {
			if (player.getInventory().getAmount(995) < minimum) {
				return 0;
			}
			return player.getInventory().remove(995, amount);
		}

		@Override
		public String getCurrencyName() {
			return "coins";
		}
	}),

	/**
	 * Tokkul
	 */
	TOKKUL(new CurrencyUtility() {

		@Override
		public int addCurrency(Player player, int amount) {
			if (!player.getInventory().hasSpaceFor(new Item(6529, amount))) {
				return 0;
			}
			return player.getInventory().add(6529, amount);
		}

		@Override
		public int removeCurrency(Player player, int amount, int minimum) {
			if (player.getInventory().getAmount(6529) < minimum) {
				return 0;
			}
			return player.getInventory().remove(6529, amount);
		}

		@Override
		public String getCurrencyName() {
			return "tokkul";
		}
	}),

	/**
	 * Voting
	 */
	VOTING(new CurrencyUtility() {
		
		@Override
		public int addCurrency(Player player, int amount) {
			player.setVotePoints(player.getVotePoints() + amount);
			return amount;
		}

		@Override
		public int removeCurrency(Player player, int amount, int minimum) {
			if (player.getVotePoints() < minimum) {
				return 0;
			}
			
			if (amount > player.getVotePoints()) {
				amount = (player.getVotePoints() / amount) * minimum;
			}
			
			player.setVotePoints(player.getVotePoints() - amount);
			return amount;
		}

		@Override
		public String getCurrencyName() {
			return "Voting points";
		}
	}),

	/**
	 * Slayer
	 */
	SLAYER(new CurrencyUtility() {
		
		@Override
		public int addCurrency(Player player, int amount) {
			player.setSlayerPoints(player.getSlayerPoints() + amount);
			return amount;
		}

		@Override
		public int removeCurrency(Player player, int amount, int minimum) {
			if (player.getSlayerPoints() < minimum) {
				return 0;
			}
			
			if (amount > player.getSlayerPoints()) {
				amount = (player.getSlayerPoints() / amount) * minimum;
			}
			
			player.setSlayerPoints(player.getSlayerPoints() - amount);
			return amount;
		}

		@Override
		public String getCurrencyName() {
			return "Slayer points";
		}
	}),

	/**
	 * Pestcontrol
	 */
	PESTCONTROL(new CurrencyUtility() {

		@Override
		public int addCurrency(Player player, int amount) {
			player.setPestControlPoints(player.getPestControlPoints() + amount);
			return amount;
		}

		@Override
		public int removeCurrency(Player player, int amount, int minimum) {
			if (player.getPestControlPoints() < minimum) {
				return 0;
			}
			
			if (amount > player.getPestControlPoints()) {
				amount = (player.getPestControlPoints() / amount) * minimum;
			}
			
			player.setPestControlPoints(player.getPestControlPoints() - amount);
			return amount;
		}
		@Override
		public String getCurrencyName() {
			return "Pest control";
		}
	}),

	;

	private CurrencyUtility utility;

	private Currency(CurrencyUtility utility) {
		this.utility = utility;
	}

	public int getCurrencyId() {
		switch (this) {

		case COINS:
			return 0;

		case TOKKUL:
			return 1;

		case SLAYER:
			return 4;

		case PESTCONTROL:
			return 5;

		case VOTING:
			return 6;

		default:
			return 0;

		}
	}

	public CurrencyUtility getUtility() {
		return utility;
	}

}