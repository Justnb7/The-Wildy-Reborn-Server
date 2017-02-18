package com.model.game.shop;

import com.model.game.character.player.Player;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * The enumerated type whose elements represent all of the different currencies
 * that can be used with shops.
 *
 * @author lare96 <http://github.com/lare96>
 */
public enum Currency {
	ACHIEVEMENT_POINTS(new GeneralCurrency() {
		@Override
		public void takeCurrency(Player player, int amount) {
			player.getAchievements().setPoints(player.getAchievements().getPoints() - amount);
		}

		@Override
		public void recieveCurrency(Player player, int amount) {
			player.getAchievements().setPoints(player.getAchievements().getPoints() + amount);
		}

		@Override
		public int currencyAmount(Player player) {
			return player.getAchievements().getPoints();
		}

		@Override
		public boolean canRecieveCurrency(Player player) {
			return true;
		}
	}) {
		@Override
		public int calculateCurrency(Player player, int id) {
			return player.getAchievements().getCost(id);
		}
	},
	COINS(new ItemCurrency(995)),
	TOKKUL(new ItemCurrency(6529)) {
		@Override
		public int calculateCurrency(Player player, int id) {
			return Shop.getTokkulValue(id);
		}
	},
	BLOOD_MONEY(new ItemCurrency(13307)) {
		@Override
		public int calculateCurrency(Player player, int id) {
			return Shop.getBMShopValue(id);
		}
	},
	DONATOR_TICKETS(new ItemCurrency(4067)) {
		@Override
		public int calculateCurrency(Player player, int id) {
			return Shop.getDonateValue(id);
		}
	},
	FREE_POINTS(new GeneralCurrency() {
		@Override
		public void takeCurrency(Player player, int amount) {
			player.setGearPoints(player.getGearPoints() - amount);
		}

		@Override
		public void recieveCurrency(Player player, int amount) {
			player.setGearPoints(player.getGearPoints() + amount);
			
		}

		@Override
		public int currencyAmount(Player player) {
			return player.getGearPoints();
		}

		@Override
		public boolean canRecieveCurrency(Player player) {
			return true;
		}
	}) {
		@Override
		public int calculateCurrency(Player player, int id) {
			return Shop.getFreePointsValue(id);
		}
	},
	SLAYER_POINTS(new GeneralCurrency() {
		@Override
		public void takeCurrency(Player player, int amount) {
			player.setSlayerPoints(player.getBountyPoints() - amount);
		}

		@Override
		public void recieveCurrency(Player player, int amount) {
			player.setSlayerPoints(player.getBountyPoints() + amount);
		}

		@Override
		public int currencyAmount(Player player) {
			return player.getSlayerPoints();
		}

		@Override
		public boolean canRecieveCurrency(Player player) {
			return true;
		}
	}) {
		@Override
		public int calculateCurrency(Player player, int id) {
			return Shop.getSlayerValue(id);
		}
	},
	BOUNTIES(new GeneralCurrency() {
			@Override
			public void takeCurrency(Player player, int amount) {
				player.setBountyPoints(player.getBountyPoints() - amount);
			}

			@Override
			public void recieveCurrency(Player player, int amount) {
				player.setBountyPoints(player.getBountyPoints() + amount);
			}

			@Override
			public int currencyAmount(Player player) {
				return player.getBountyPoints();
			}

			@Override
			public boolean canRecieveCurrency(Player player) {
				return true;
			}
		}) {
			@Override
			public int calculateCurrency(Player player, int id) {
				return Shop.getBounties(id);
			}
		},
	PK_POINTS(new GeneralCurrency() {
		@Override
		public void takeCurrency(Player player, int amount) {
			player.setPkPoints(player.getPkPoints() - amount);
		}

		@Override
		public void recieveCurrency(Player player, int amount) {
			player.setPkPoints(player.getPkPoints() + amount);
		}

		@Override
		public int currencyAmount(Player player) {
			return player.getPkPoints();
		}

		@Override
		public boolean canRecieveCurrency(Player player) {
			return true;
		}
	}) {
		@Override
		public int calculateCurrency(Player player, int id) {
			return Shop.getPKShopValue(id);
		}
	},
	VOTE_POINTS(new GeneralCurrency() {
		@Override
		public void takeCurrency(Player player, int amount) {
			player.setVotePoints(player.getVotePoints() - amount);
		}

		@Override
		public void recieveCurrency(Player player, int amount) {
			player.setVotePoints(player.getVotePoints() + amount);
		}

		@Override
		public int currencyAmount(Player player) {
			return player.getVotePoints();
		}

		@Override
		public boolean canRecieveCurrency(Player player) {
			return true;
		}
	}) {
		@Override
		public int calculateCurrency(Player player, int id) {
			return Shop.getVoteValue(id);
		}};

	/**
	 * The currency that is represented by this element.
	 */
	private final GeneralCurrency currency;

	/**
	 * Creates a new {@link Currency}.
	 *
	 * @param currency
	 *            the currency that is represented by this element.
	 */
	private Currency(GeneralCurrency currency) {
		this.currency = currency;
	}

	@Override
	public final String toString() {
		return name().toLowerCase().replace('_', ' ');
	}

	public int calculateCurrency(Player player, int id) {
		if (ItemDefinition.forId(id) == null) {
			return 0;
		}
		return ItemDefinition.forId(id).getShopValue();
	}

	/**
	 * Gets the currency that is represented by this element.
	 *
	 * @return the currency that is represented.
	 */
	public final GeneralCurrency getCurrency() {
		return currency;
	}
}