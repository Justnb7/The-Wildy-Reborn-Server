package com.model.game.item.container.impl.shop;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.server.Server;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

/**
 * Handles all the functions of shopping
 * 
 * @author Daniel
 *
 */
public class ShopManager {

	/**
	 * Allowed shops for Ultimate Iron man accounts.
	 */
	private final static int[] IRON_SHOPS = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

	/*
	 * Task for updating and re-stocking shops.
	 */
	static {
		Server.getTaskScheduler().schedule(new ScheduledTask(100) {
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
	 * Opens a shop based on the id given.
	 * 
	 * @param player
	 * @param id
	 */
	public static void open(Player player, int id) {
		if (player.getRights().isIronMan()) {
			boolean can = false;

			for (int shops : IRON_SHOPS) {
				if (shops == id) {
					can = true;
					break;
				}
			}

			if (!can) {
				player.getActionSender().sendMessage("Your current game mode restricts you from this shop.");
				return;
			}
		}

		final Shop shop = Shop.SHOPS.get(id);

		if (shop == null) {
			return;
		}

		player.getActionSender().sendString(shop.getName(), 40002);

		player.getAttributes().put("shopping", true);

		player.setShopId(shop.getShopId());
		
//		for (int index = 0; index < shop.toNonNullArray().length; index++) {
//			String name = shop.toArray()[index].getName();
//			String formatted_name = name.toUpperCase().replaceAll(" ", "_").replaceAll("'", "");
//			int item = shop.toArray()[index].getId();
//			int cost = shop.toArray()[index].getValue();
//			System.out.println(formatted_name + "(\"" + name + "\", " + item + ", " + cost + "),");
//		}

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

		player.getActionSender().sendMessage("<col=057A13>" + shop.get(item).getName() + " is valued at " + Utility.formatDigits(price) + " " + currency.getUtility().getCurrencyName() + ".");
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
			player.getActionSender().sendMessage("<col=057A13>You do not have enough inventory space to buy that.");
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
				player.getActionSender().sendMessage("<col=057A13>You do not have enough inventory space to buy all those.");
			}
		}

		int price = shop.get(item).getValue() * item.getAmount();

		if ((long) shop.get(item).getValue() > Integer.MAX_VALUE / (double) item.getAmount()) {
			price = shop.get(item).getValue() * (Integer.MAX_VALUE / shop.get(item).getValue());
		}

		final int removed = currency.getUtility().removeCurrency(player, price, shop.get(item).getValue());

		item.setAmount(removed / shop.get(item).getValue());

		if (removed == 0 && price > 0) {
			player.getActionSender().sendMessage("<col=057A13>You do not have enough " + currency.getUtility().getCurrencyName() + " to buy that.");
		} else {
			player.getInventory().add(id, item.getAmount(), true);
			shop.remove(id, item.getAmount(), false);
			shop.shift(false);
			update(player.getShopId());
			player.getActionSender().sendMessage("<col=057A13>You bought " + item.getAmount() + " " + item.getName() + " for " + Utility.formatDigits(price) + " " + currency.getUtility().getCurrencyName() + ".");
			player.getActionSender().sendItemOnInterface(3823, player.getInventory().toArray());
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
			player.getActionSender().sendMessage("<col=057A13>You can't sell any items to this shop!");
			return;
		}

		if (!item.isTradeable()) {
			player.getActionSender().sendMessage("<col=057A13>You can not sell this item.");
			return;
		}

		final int price = item.getValue() * item.getAmount();
		player.getActionSender().sendMessage("<col=057A13>Shop will buy this " + item.getName() + " for " + Utility.formatDigits(price) + " coin" + (price == 1 ? "" : "s") + ".");
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
			player.getActionSender().sendMessage("<col=057A13>You can't sell items to this shop.");
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
			player.getInventory().remove(id, removed);
			currency.getUtility().addCurrency(player, price);
			shop.shift(false);
			update(player.getShopId());
			player.getActionSender().sendItemOnInterface(3823, player.getInventory().toArray());
		}
	}

}