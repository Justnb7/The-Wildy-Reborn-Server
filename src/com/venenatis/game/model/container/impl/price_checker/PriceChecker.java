package com.venenatis.game.model.container.impl.price_checker;

import java.text.NumberFormat;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.container.Container;
import com.venenatis.game.model.container.impl.InterfaceConstants;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;

public class PriceChecker extends Container {

	/**
	 * All the strings for the price checker data
	 */
	private int[] STRINGS = { 49550, 49551, 49552, 49553, 49554, 49555, 49556, 49557, 49558, 49559, 49560, 49561, 49562, 49563, 49564, 49565, 49566, 49567, 49568, 49569, 49570, 49571, 49572, 49573, 49574, 49575, 49576, 49577, };

	/**
	 * The types of data
	 *
	 */
	public enum PriceType {
		//Checks based on shop value
		VALUE(),
		
		//Checks based on high alch value
		HIGH_ALCH_VALUE();
	}

	/**
	 * The local player
	 */
	public Player player;

	/**
	 * The price type, Example, HIGH_ALCH_VALUE
	 */
	private PriceType priceType;
	
	/**
	 * Item searching feature
	 */
	private Item itemSearching;

	/**
	 * The price checker constructor
	 * 
	 * @param player
	 *            The player opening the price checker
	 */
	public PriceChecker(Player player) {
		super(28, ContainerType.DEFAULT);
		this.player = player;
		setPriceType(PriceType.VALUE);
	}

	/**
	 * Opens the price checker and sents all data
	 */
	public void open() {
		
		if (player.getCombatState().inCombat()) {
			player.getActionSender().sendMessage("You can not open the price checker while in combat.");
			return;
		}
		
		for (final int id : STRINGS) {
			player.getActionSender().sendString("", id);
		}
		player.getActionSender().sendConfig(237, getPriceType() == PriceType.VALUE ? 1 : 0);
		player.getActionSender().sendItemOnInterface(49581, new Item[] { null });
		player.getActionSender().sendString("", 49582);
		player.getActionSender().sendString("", 49583);
		refresh();
		player.getAttributes().put("price_checker", Boolean.TRUE);
		player.getActionSender().sendInterfaceWithInventoryOverlay(49500, 5063);
	}

	/**
	 * Closes the price checker removes all data
	 */
	public void close() {
		player.getActionSender().removeAllInterfaces();
		withdrawAll();
		clear(false);
		player.getAttributes().put("price_checker", false);
		setItemSearching(null);
	}
	
	/**
	 * Allows us to search an item, function WIP never finished.
	 * 
	 * @param item
	 *            The item we're trying to search
	 */
	public void searchItem(Item item) {
		setItemSearching(item);
		Item[] itemSearch = { getItemSearching() };
		player.getActionSender().sendItemOnInterface(49581, itemSearch);
		player.getActionSender().sendString("<col=ffb000>" + getItemSearching().getName() + ":", 49582);
		player.getActionSender().sendString(Utility.formatDigits((player.getPriceChecker().getPriceType() == PriceType.VALUE ? getItemSearching().getValue() : getItemSearching().getHighAlch())) + "", 49583);
		player.getActionSender().sendMessage("Now displaying <col=ff0000>" + (player.getPriceChecker().getPriceType() == PriceType.VALUE ? "item value" : "item high alch") + "</col> price information for " + getItemSearching().getName() + ".");
	}

	/**
	 * The deposit action we call in our packets
	 * 
	 * @param id
	 *            The item id
	 * @param slot
	 *            The slot of the item
	 * @param amount
	 *            The item amount
	 */
	public int deposit(int id, int slot, int amount) {
		return deposit(id, slot, amount, true);
	}

	/**
	 * The main deposit action
	 * 
	 * @param id
	 *            The item id
	 * @param slot
	 *            The slot of the item
	 * @param amount
	 *            The item amount
	 * @param refresh
	 *            Are we refreshing the container?
	 */
	public int deposit(int id, int slot, int amount, boolean refresh) {
		if (player.getInterfaceState().getCurrentInterface() != 49500) {
			return 0;
		}
		
		Item item = player.getInventory().get(slot);

		if (item == null || item.getId() != id) {
			return 0;
		}

		if (!item.isTradeable()) {
			player.getActionSender().sendMessage("This is item is untradeable!");
			return 0;
		}

		if (item.getAmount() < amount) {
			amount = item.getAmount();
		}

		final int count = player.getInventory().getAmount(id);

		if (count < amount) {
			amount = count;
		}

		final int nextSlot = nextSlot() - (item.isStackable() && contains(id) ? -1 : 0);

		final int added = add(item.getId(), amount, refresh);

		if (item.isStackable() || amount == 1) {
			player.getInventory().removeSlot(slot, added, refresh);
		} else {
			player.getInventory().remove(id, added, refresh);
		}

		if (added <= 0) {
			return 0;
		}

		final int newNextSlot = nextSlot();

		for (int index = nextSlot; index < newNextSlot; index++) {
			if (index < 0) {
				continue;
			}

			if (index >= STRINGS.length) {
				break;
			}

			item = get(index);
			if (item != null) {
				if (getPriceType() == PriceType.VALUE) {
					if (item.isStackable()) {
						player.getActionSender().sendString(NumberFormat.getInstance().format(item.getAmount()) + " x " + NumberFormat.getInstance().format(item.getValue()) + "\\n" + "= " + NumberFormat.getInstance().format(item.getValue() * item.getAmount()), STRINGS[index]);
					} else {
						player.getActionSender().sendString(NumberFormat.getInstance().format(item.getValue()), STRINGS[index]);
					}
				} else {
					if (item.isStackable()) {
						player.getActionSender().sendString(NumberFormat.getInstance().format(item.getAmount()) + " x " + NumberFormat.getInstance().format(item.getHighAlch()) + "\\n" + "= " + NumberFormat.getInstance().format(item.getHighAlch() * item.getAmount()), STRINGS[index]);
					} else {
						player.getActionSender().sendString(NumberFormat.getInstance().format(item.getHighAlch()), STRINGS[index]);
					}
				}
			} else {
				player.getActionSender().sendString("", STRINGS[index]);
			}
		}

		if (refresh) {
			refresh();
		}

		return added;
	}

	/**
	 * Deposits all the items into the price checker
	 */
	public void depositAll() {
		if (player.getInterfaceState().getCurrentInterface() != 49500) {
			return;
		}
		
		final Item[] items = player.getInventory().toArray();
		for (int slot = 0; slot < items.length; slot++) {
			final Item item = items[slot];
			if (item == null) {
				continue;
			}
			final int added = deposit(item.getId(), slot, item.getAmount(), false);
			player.getInventory().removeSlot(slot, added, false);
		}
		refresh();
		player.getActionSender().sendMessage("You have deposited some of the items in your inventory.");
	}

	public int withdraw(int id, int slot, int amount) {
		return withdraw(id, slot, amount, true);
	}

	public int withdraw(int id, int slot, int amount, boolean refresh) {
		if (player.getInterfaceState().getCurrentInterface() != 49500) {
			return 0;
		}
		
		Item item = get(slot);

		if (item == null || item.getId() != id) {
			return 0;
		}

		if (amount > item.getAmount()) {
			amount = item.getAmount();
		}

		final int added = player.getInventory().add(id, amount, false);

		if (item.isStackable() || amount == 1) {
			removeSlot(slot, added, false);
		} else {
			remove(id, added, false);
		}

		shift(refresh);

		final int nextSlot = nextSlot();

		for (int index = slot; index <= nextSlot; index++) {
			if (index < 0) {
				continue;
			}

			if (index >= STRINGS.length) {
				break;
			}

			item = get(index);
			if (item != null) {
				if (getPriceType() == PriceType.VALUE) {
					if (item.isStackable()) {
						player.getActionSender().sendString(NumberFormat.getInstance().format(item.getAmount()) + " x " + NumberFormat.getInstance().format(item.getValue()) + "\\n" + "= " + NumberFormat.getInstance().format(item.getValue() * item.getAmount()), STRINGS[index]);
					} else {
						player.getActionSender().sendString(NumberFormat.getInstance().format(item.getValue()), STRINGS[index]);
					}
				} else {
					if (item.isStackable()) {
						player.getActionSender().sendString(NumberFormat.getInstance().format(item.getAmount()) + " x " + NumberFormat.getInstance().format(item.getHighAlch()) + "\\n" + "= " + NumberFormat.getInstance().format(item.getHighAlch() * item.getAmount()), STRINGS[index]);
					} else {
						player.getActionSender().sendString(NumberFormat.getInstance().format(item.getHighAlch()), STRINGS[index]);
					}
				}
			} else {
				player.getActionSender().sendString("", STRINGS[index]);
			}
		}

		return added;
	}

	public void withdrawAll() {
		
		final int slots = getTakenSlots();
		for (int index = 0; index < slots; index++) {
			if (index >= STRINGS.length) {
				break;
			}

			Item item = get(index);

			if (item != null) {
				final int added = player.getInventory().add(item.getId(), item.getAmount(), false);
				remove(item.getId(), added, false);
				item = get(index);
			}

			if (item != null) {
				if (getPriceType() == PriceType.VALUE) {
					if (item.isStackable()) {
						player.getActionSender().sendString(NumberFormat.getInstance().format(item.getAmount()) + " x " + NumberFormat.getInstance().format(item.getValue()) + "\\n" + "= " + NumberFormat.getInstance().format(item.getValue() * item.getAmount()), STRINGS[index]);
					} else {
						player.getActionSender().sendString(NumberFormat.getInstance().format(item.getValue()), STRINGS[index]);
					}
				} else {
					if (item.isStackable()) {
						player.getActionSender().sendString(NumberFormat.getInstance().format(item.getAmount()) + " x " + NumberFormat.getInstance().format(item.getHighAlch()) + "\\n" + "= " + NumberFormat.getInstance().format(item.getHighAlch() * item.getAmount()), STRINGS[index]);
					} else {
						player.getActionSender().sendString(NumberFormat.getInstance().format(item.getHighAlch()), STRINGS[index]);
					}
				}
			} else {
				player.getActionSender().sendString("", STRINGS[index]);
			}
		}
		refresh();
	}
	
	/**
	 * Handles all the button click actions.
	 * 
	 * @param button
	 *            The button being clicked
	 */
	public boolean buttonAction(int button) {
		switch(button) {
		case 108003:
			if (!player.getCombatState().inCombat()) {
				open();
			} else {
				player.getActionSender().sendMessage("You cannot open the price checker screen while in combat.");
			}
			return true;
		case 193094:
			close();
			return true;
		case 189194:
			withdrawAll();
			return true;
		case 193097:
			depositAll();
			return true;
		case 189124:
			player.getPriceChecker().setPriceType(PriceType.VALUE);
			player.getActionSender().sendConfig(237, 1);
			player.getActionSender().sendMessage("Price checker will now be calculating prices based of item values.");
			if (player.getPriceChecker().getItemSearching() != null) {
				player.getPriceChecker().searchItem(player.getPriceChecker().getItemSearching());
			}
			return true;
		}
		return false;
	}

	@Override
	public void onFillContainer() {
		player.getActionSender().sendMessage("Your price checker is full!");
	}

	@Override
	public void onMaxStack() {
		player.getActionSender().sendMessage("You do not have enough space to hold that.");
	}

	@Override
	public void refresh() {
		player.getInventory().refresh();
		player.getActionSender().sendItemOnInterface(InterfaceConstants.INVENTORY_STORE, player.getInventory().toArray());
		player.getActionSender().sendItemOnInterface(49542, toArray());
		player.getActionSender().sendString("" + (getPriceType() == PriceType.VALUE ? NumberFormat.getInstance().format(containerValue()) : NumberFormat.getInstance().format(containerHighAlchValue())), 49513);
	}

	@Override
	public void refresh(int... slots) {
		for (final int slot : slots) {
			player.getActionSender().sendItemOnInterfaceSlot(49542, stack[slot], slot);
		}
	}

	public PriceType getPriceType() {
		return priceType;
	}

	public void setPriceType(PriceType priceType) {
		this.priceType = priceType;
	}
	
	public Item getItemSearching() {
		return itemSearching;
	}

	public void setItemSearching(Item itemSearching) {
		this.itemSearching = itemSearching;
	}

}