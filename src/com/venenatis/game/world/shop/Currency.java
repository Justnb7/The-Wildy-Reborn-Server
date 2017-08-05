package com.venenatis.game.world.shop;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;

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
	 * Donator tickets
	 */
	DONATOR_TICKETS(new CurrencyUtility() {

		@Override
		public int addCurrency(Player player, int amount) {
			if (!player.getInventory().hasSpaceFor(new Item(4067, amount))) {
				return 0;
			}
			return player.getInventory().add(4067, amount);
		}

		@Override
		public int removeCurrency(Player player, int amount, int minimum) {
			if (player.getInventory().getAmount(4067) < minimum) {
				return 0;
			}
			return player.getInventory().remove(4067, amount);
		}

		@Override
		public String getCurrencyName() {
			return "Donator tickets";
		}
	}),
	
	/**
	 * PkP
	 */
	PK_POINTS(new CurrencyUtility() {

		@Override
		public int addCurrency(Player player, int amount) {
			player.setPkPoints(player.getPkPoints() + amount);
			return amount;
		}

		@Override
		public int removeCurrency(Player player, int amount, int minimum) {
			if (player.getPkPoints() < minimum) {
				return 0;
			}
			
			if (amount > player.getPkPoints()) {
				amount = (player.getPkPoints() / amount) * minimum;
			}
			
			player.setPkPoints(player.getPkPoints() - amount);
			return amount;
		}
		@Override
		public String getCurrencyName() {
			return "PK points";
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
	
	/**
	 * Achievement
	 */
	ACHIEVEMENT_POINTS(new CurrencyUtility() {

		@Override
		public int addCurrency(Player player, int amount) {
			player.setAchievementPoints(player.getAchievementsPoints() + amount);
			return amount;
		}

		@Override
		public int removeCurrency(Player player, int amount, int minimum) {
			if (player.getAchievementsPoints() < minimum) {
				return 0;
			}
			
			if (amount > player.getAchievementsPoints()) {
				amount = (player.getAchievementsPoints() / amount) * minimum;
			}
			
			player.setAchievementPoints(player.getAchievementsPoints() - amount);
			return amount;
		}

		@Override
		public String getCurrencyName() {
			return "Achievement points";
		}
	}),
	
	/**
	 * Bounty hunter
	 */
	BOUNTIES(new CurrencyUtility() {

		@Override
		public int addCurrency(Player player, int amount) {
			player.setBountyPoints(player.getBountyPoints() + amount);
			return amount;
		}

		@Override
		public int removeCurrency(Player player, int amount, int minimum) {
			if (player.getBountyPoints() < minimum) {
				return 0;
			}
			
			if (amount > player.getBountyPoints()) {
				amount = (player.getBountyPoints() / amount) * minimum;
			}
			
			player.setBountyPoints(player.getBountyPoints() - amount);
			return amount;
		}
		@Override
		public String getCurrencyName() {
			return "Bounties";
		}
	}),

	/**
	 * Gear points
	 */
	GEAR_POINTS(new CurrencyUtility() {

		@Override
		public int addCurrency(Player player, int amount) {
			player.setGearPoints(player.getGearPoints() + amount);
			return amount;
		}

		@Override
		public int removeCurrency(Player player, int amount, int minimum) {
			if (player.getGearPoints() < minimum) {
				return 0;
			}
			
			if (amount > player.getGearPoints()) {
				amount = (player.getGearPoints() / amount) * minimum;
			}
			
			player.setGearPoints(player.getGearPoints() - amount);
			return amount;
		}
		@Override
		public String getCurrencyName() {
			return "Gear points";
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
			
		case DONATOR_TICKETS:
			return 2;
			
		case PK_POINTS:
		case BOUNTIES:
			return 3;

		case GEAR_POINTS:
			return -1;
			
		case SLAYER:
			return 4;

		case PESTCONTROL:
			return 5;

		case VOTING:
			return 6;
			
		case ACHIEVEMENT_POINTS:
			return 8;

		default:
			return 0;

		}
	}

	public CurrencyUtility getUtility() {
		return utility;
	}

}