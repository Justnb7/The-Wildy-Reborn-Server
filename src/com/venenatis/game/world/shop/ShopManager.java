package com.venenatis.game.world.shop;

import com.venenatis.game.content.achievements.AchievementHandler;
import com.venenatis.game.content.achievements.AchievementList;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.server.Server;

/**
 * Handles all the functions of shopping
 * 
 * @author Daniel
 *
 */
public class ShopManager {

	/*
	 * Task for updating and re-stocking shops.
	 */
	static {
		Server.getTaskScheduler().schedule(new Task(100) {
			@Override
			public void execute() {
				restock();
				update();
			}

			@Override
			public void onStop() {
			}
		});
	}

	/**
	 * Re-stocks the shop items.
	 */
	public static void restock() {
		for (final Shop shop : Shop.SHOPS) {
			for (int i = 0; i < shop.toArray().length; i++) {
				if (shop.get(i) == null) {
					continue;
				}
				if (i < shop.getDefaultStockAmounts().length) {
					if (shop.restock()) {
						if (shop.getDefaultStockAmounts()[i] < shop.get(i).getAmount()) {
							shop.get(i).add(-1);
						} else if (shop.getDefaultStockAmounts()[i] > shop.get(i).getAmount()) {
							shop.get(i).add(1);
						}
					}
				} else {
					shop.get(i).add(-1);
					if (shop.get(i).getAmount() <= 0) {
						shop.setSlot(i, null);
					}
				}
			}
		}
	}

	/**
	 * Updates all the shops for all players.
	 */
	public static void update() {
		for (final Player player : World.getWorld().getPlayers()) {
			if (player == null) {
				continue;
			}

			final Shop shop = Shop.SHOPS.get(player.getShopId());

			if (shop == null) {
				return;
			}

			shop.shift(false);

			player.getActionSender().sendItemOnInterface(40051, shop.toArray());

			for (int i = 0; i < shop.toArray().length; i++) {
				player.getActionSender().sendString(shop.toArray()[i] == null ? "0" : shop.toArray()[i].getValue() + "," + shop.getCurrency().getCurrencyId(), 40052 + i);
			}
			return;
		}
	}

	/**
	 * Updates a specific shop.
	 * 
	 * @param shopId
	 */
	public static void update(int shopId) {
		final Shop shop = Shop.SHOPS.get(shopId);

		if (shop == null) {
			return;
		}

		for (final Player player : World.getWorld().getPlayers()) {

			if (player == null || player.getShopId() != shopId) {
				continue;
			}

			player.getActionSender().sendItemOnInterface(40051, shop.toArray());

			for (int i = 0; i < shop.toArray().length; i++) {
				player.getActionSender().sendString(shop.toArray()[i] == null ? "0" : shop.toArray()[i].getValue() + "," + shop.getCurrency().getCurrencyId(), 40052 + i);
			}
			return;
		}
	}

	/**
	 * Writes the slayer shop onto the hardcoded interface
	 * @param player
	 */
	public static void slayerShop(Player player) {

		//The shop representing the 'Slayer Rewards'
		final Shop shop = Shop.SHOPS.get(10);

		if (shop == null) {
			return;
		}
		
		//Set attributes
		player.getAttributes().put("shopping", true);
		player.getActionSender().sendItemOnInterface(23016, shop.toNonNullArray());
		
		//So imma check null items send string "" which is empty? ye
		
		//print says 4 items in container yet they others are not removed
		
		System.out.println("shop items: "+shop.toNonNullArray().length);
		for (int i = 0; i < shop.toArray().length - 1; i++) { // ah yeah when i use toArray imma get some weird error
			player.getActionSender().sendString(shop.toArray()[i] == null ? "" : ""+ shop.toArray()[i].getValue(), 23017 + i);
			player.debug(String.format("%s", shop.toArray()[i]));
		}
		
		player.getActionSender().sendInterfaceWithInventoryOverlay(ShopConstants.SLAYER_REWARDS_INTERFACE, 3822);
	}
	
	/**
	 * Opens a shop based on the id given.
	 * 
	 * @param player
	 * @param id
	 */
	public static void open(Player player, int id) {

		final Shop shop = Shop.SHOPS.get(id);

		if (shop == null) {
			return;
		}

		player.getActionSender().sendString(shop.getName(), 40002);

		player.getAttributes().put("shopping", true);

		player.setShopId(shop.getShopId());

		player.getActionSender().sendItemOnInterface(40051, shop.toArray());

		for (int i = 0; i < shop.toArray().length; i++) {
			player.getActionSender().sendString(shop.toArray()[i] == null ? "0" : shop.toArray()[i].getValue() + "," + shop.getCurrency().getCurrencyId(), 40052 + i);
		}

		player.getActionSender().sendScrollBar(40050, shop.getScroll());

		player.getActionSender().sendInterfaceWithInventoryOverlay(ShopConstants.INTERFACE_ID, 3822);
		player.getInventory().refresh();
		player.getActionSender().sendItemOnInterface(3823, player.getInventory().toArray());
	}

	/**
	 * Sends player message of how much selected item is being sold for.
	 * 
	 * @param player
	 * @param slot
	 */
	public static void getShopValue(Player player, int slot) {
		if (player.getInterfaceState().getCurrentInterface() != ShopConstants.INTERFACE_ID) {
			return;
		}
		
		final Shop shop = Shop.SHOPS.get(player.getShopId());

		if (shop == null) {
			return;
		}

		final Item item = shop.get(slot);

		final Currency currency = shop.getCurrency();

		final int price = shop.get(item).getValue();

		player.getActionSender().sendMessage("" + shop.get(item).getName() + " is valued at " + Utility.formatDigits(price) + " " + currency.getUtility().getCurrencyName() + ".");
	}

	/**
	 * Handles buying items from shop.
	 * 
	 * @param player
	 * @param id
	 * @param amount
	 * @param slot
	 */
	public static void buy(Player player, int id, int amount, int slot) {
		if (player.getInterfaceState().getCurrentInterface() != ShopConstants.INTERFACE_ID) {
			return;
		}
		
		final Shop shop = Shop.SHOPS.get(player.getShopId());

		if (shop == null || shop.get(slot) == null || shop.get(slot).getId() != id) {
			return;
		}

		if (player.getInventory().getFreeSlots() == 0) {
			player.getActionSender().sendMessage("You do not have enough inventory space to buy that.");
			return;
		}

		final Item item = new Item(id, amount);

		final Currency currency = shop.getCurrency();

		if (shop.getAmount(id) < item.getAmount()) {
			item.setAmount(shop.getAmount(id));
		}

		if (!item.isStackable()) {
			if (item.getAmount() > player.getInventory().getFreeSlots()) {
				item.setAmount(player.getInventory().getFreeSlots());
				player.getActionSender().sendMessage("You do not have enough inventory space to buy all those.");
			}
		}

		int price = shop.get(item).getValue() * item.getAmount();

		if ((long) shop.get(item).getValue() > Integer.MAX_VALUE / (double) item.getAmount()) {
			price = shop.get(item).getValue() * (Integer.MAX_VALUE / shop.get(item).getValue());
		}

		final int removed = currency.getUtility().removeCurrency(player, price, shop.get(item).getValue());

		item.setAmount(removed / shop.get(item).getValue());

		if (removed == 0 && price > 0) {
			player.getActionSender().sendMessage("You do not have enough " + currency.getUtility().getCurrencyName() + " to buy that.");
		} else {
			player.getInventory().add(id, item.getAmount(), true);
			if (shop.getCurrency() != Currency.GEAR_POINTS) {
				player.debug("enter");
				shop.remove(id, item.getAmount(), false);
				shop.shift(false);
				update(player.getShopId());
			}
			player.getActionSender().sendMessage("You bought " + item.getAmount() + " " + item.getName() + " for " + Utility.formatDigits(price) + " " + currency.getUtility().getCurrencyName() + ".");
			player.getActionSender().sendItemOnInterface(3823, player.getInventory().toArray());
			AchievementHandler.activate(player, AchievementList.SPENDER, price);
			AchievementHandler.activate(player, AchievementList.RICHIE, price);
		}
	}

	/**
	 * Sends message to player of how much shop will buy selected item for.
	 * 
	 * @param player
	 * @param id
	 */
	public static void getSellValue(Player player, int id) {
		if (player.getInterfaceState().getCurrentInterface() != ShopConstants.INTERFACE_ID) {
			return;
		}

		final Shop shop = Shop.SHOPS.get(player.getShopId());

		if (shop == null) {
			return;
		}

		final Item item = new Item(id);

		if (item == null || item.getId() != id) {
			return;
		}

		if (!shop.canSell()) {
			player.getActionSender().sendMessage("You can't sell any items to this shop!");
			return;
		}

		if (!item.isTradeable()) {
			player.getActionSender().sendMessage("You can not sell this item.");
			return;
		}

		final int price = item.getValue() * item.getAmount();
		player.getActionSender().sendMessage("Shop will buy this " + item.getName() + " for " + Utility.formatDigits(price) + " coin" + (price == 1 ? "" : "s") + ".");
	}

	/**
	 * Handles selling item to shop.
	 * 
	 * @param player
	 * @param id
	 * @param amount
	 * @param slot
	 */
	public static void sell(Player player, int id, int amount, int slot) {
		if (player.getInterfaceState().getCurrentInterface() != ShopConstants.INTERFACE_ID) {
			return;
		}
		
		final Shop shop = Shop.SHOPS.get(player.getShopId());

		final Item item = player.getInventory().get(slot);

		if (shop == null || item == null || item.getId() != id) {
			return;
		}

		if (!shop.canSell()) {
			player.getActionSender().sendMessage("You can't sell items to this shop.");
			return;
		}

		if (amount > player.getInventory().getAmount(id)) {
			amount = player.getInventory().getAmount(id);
		}

		final Currency currency = shop.getCurrency();

		final int removed = player.getInventory().remove(id, amount);

		final int price = item.getValue() * removed;

		if (removed > 0) {
			shop.add(id, removed, false);
			currency.getUtility().addCurrency(player, price);
			shop.shift(false);
			update(player.getShopId());
			player.getActionSender().sendItemOnInterface(3823, player.getInventory().toArray());
		}
	}

}