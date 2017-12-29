package com.venenatis.game.model.container.impl.bank;

import java.util.Arrays;

import com.venenatis.game.content.emotes.Emotes;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.container.Container;
import com.venenatis.game.model.container.impl.InterfaceConstants;
import com.venenatis.game.model.entity.player.Player;

/**
 * The OSRS bank container
 * @author Patrick van Elderen && Battle OS team main class owners
 *
 */
public class BankContainer extends Container {

	/**
	 * The player owning the bank container
	 */
	private final Player player;

	/**
	 * Checks if we're noting the items, default is false (No)
	 */
	private boolean noting = false;
	
	public boolean getNoting() {
		return noting;
	}
	
	/**
	 * Are we allowed to insert items, default is false (No)
	 */
	private boolean inserting = false;

	/**
	 * The total of tab amounts
	 */
	private final int[] tabAmounts = new int[10];

	public int[] getTabAmounts() {
		return tabAmounts;
	}

	/**
	 * The regular bank tab a.k.a main tab
	 */
	private int bankTab = 0;

	/**
	 * The bank constructor
	 * 
	 * @param player
	 *            The player
	 */
	public BankContainer(Player player) {
		super(800, ContainerType.ALWAYS_STACK);
		this.player = player;
	}

	/**
	 * Changes the amount of tabs we have
	 * 
	 * @param tab
	 *            The tab that is collapsed or created
	 * @param amount
	 *            Amount of tabs already created
	 * @param collapse
	 *            Did we collapse a tab?
	 * @param collapseAll
	 *            Did we collapse all tabs?
	 */
	public void changeTabAmount(int tab, int amount, boolean collapse, boolean collapseAll) {
		tabAmounts[tab] += amount;
		if (tabAmounts[tab] <= 0 && collapse) {
			collapse(tab, 0, collapseAll);
		}
	}

	public void changeTabAmount(int tab, int amount, boolean collapse) {
		changeTabAmount(tab, amount, collapse, true);
	}

	@Override
	public void clear(boolean refresh) {
		super.clear(refresh);
		Arrays.fill(tabAmounts, 0);
	}

	/**
	 * The action buttons being clicked
	 * 
	 * @param button
	 *            The button we are clicking
	 */
	public boolean clickButton(int button) {
		if (button >= 234127 && button <= 234163) {
			final int tab = (234127 - button) / -4;
			if (button % 2 == 0) {
				bankTab = tab;
				player.getActionSender().sendString("", 60019);
			} else {
				collapse(tab, 0);
			}
			return true;
		}

		switch (button) {

		/* Preloading Gear */
		case 234168:
			player.getPresets().open();
			return true;

		/* Deposit Inventory */
		case 234104:
			depositeInventory(true);
			return true;

		/* Deposit Equipment */
		case 234107:
			depositeEquipment(true);
			return true;

		/* Bank Pin */
		case 234116:
			return true;

		/* Bank Insert/Swap */
		case 234102:
			inserting = !inserting;
			player.getActionSender().sendConfig(304, inserting ? 1 : 0);
			return true;

		/* Bank Noting */
		case 234103:
			noting = !noting;
			player.getActionSender().sendConfig(115, noting ? 1 : 0);
			return true;

		}
		return false;
	}

	/**
	 * Deposits the entire inventory
	 * 
	 * @param message
	 *            Sents the message that we did deposit our inventory
	 */
	public void depositeInventory(boolean message) {
		Item[] items = player.getInventory().toArray();
		for (int slot = 0; slot < items.length; slot++) {
			final Item item = items[slot];
			final int deposited = depositFromNothing(item, bankTab, false);
			player.getInventory().removeSlot(slot, deposited, false);
		}

		shift(true);
		player.getInventory().refresh();

		if (message) {
			player.getActionSender().sendMessage("You have deposited some of your inventory items.");
		}
	}

	/**
	 * Deposits the worn equipment
	 * 
	 * @param message
	 *            Sents the message that we did deposit our equipment
	 */
	public void depositeEquipment(boolean message) {
		Item[] items = player.getEquipment().toArray();
		for (int slot = 0; slot < items.length; slot++) {
			final Item item = items[slot];
			final int deposited = depositFromNothing(item, bankTab, false);
			player.getEquipment().removeSlot(slot, deposited, false);
		}

		shift(true);
		player.getEquipment().refresh();
		player.getEquipment().setBonus();
		Emotes.update(player);
		player.setDefaultAnimations();
		if (message) {
			player.getActionSender().sendMessage("You have deposited some of your worn-items.");
		}
	}

	public void collapse(int tab, int toTab) {
		collapse(tab, toTab, true);
	}

	/**
	 * Collapses the bank tab
	 * 
	 * @param tab
	 *            The tab to collapse
	 * @param toTab
	 *            Removes the tab and shifts them into the previous state
	 * @param collapseAll
	 *            Collapses all bank tabs
	 */
	public void collapse(int tab, int toTab, boolean collapseAll) {
		if (tab == 0 && collapseAll) {
			Arrays.fill(tabAmounts, 0);
			tabAmounts[0] = getTakenSlots();
			shift(true);
			return;
		}

		if (toTab == 0) {
			player.getActionSender().sendConfig(211, 0);
			bankTab = 0;
		}

		if (toTab == tab && tab != 0 || tab > 9) {
			shift(true);
			return;
		}

		final int initialTabAmount = tabAmounts[tab];

		for (int fromSlot = 0; fromSlot < initialTabAmount; fromSlot++) {
			changeTabAmount(tab, -1, false);
			changeTabAmount(toTab, 1, false);
			insert(getData(tab, 1) - tabAmounts[tab], getData(toTab, 1));
		}

		collapse(tab + 1, tab);
	}

	public void deposit(int id, int slot, int amount) {
		deposit(id, slot, amount, true);
	}

	/**
	 * Deposit items into the bank
	 * 
	 * @param id
	 *            The item we're adding in our bank
	 * @param slot
	 *            The slot the item fits, shifts other items
	 * @param amount
	 *            The amount we're adding to the bank
	 * @param refresh
	 *            Are we going to refresh the bank?
	 */
	public void deposit(int id, int slot, int amount, boolean refresh) {
		if (player.getInterfaceState().getCurrentInterface() == 60_000 || player.getInterfaceState().getCurrentInterface() == 4_465) {

			Item item = player.getInventory().get(slot);

			if (item == null || item.getId() != id || item.getAmount() <= 0 || amount <= 0) {
				return;
			}

			item = new Item(item.unnoted());
			
			if (item.getId() >= 13302 && item.getId() <= 13306) {
				return;
			}

			if (item.getAmount() > amount) {
				item.setAmount(amount);
			}

			final int count = player.getInventory().getAmount(id);

			if (count < amount) {
				amount = count;
			}

			final boolean contains = contains(item.getId());
			final int added = add(item.getId(), amount, false);

			if (item.isStackable() || amount == 1) {
				player.getInventory().removeSlot(slot, added, false);
			} else {
				player.getInventory().remove(id, added, false);
			}

			if (added > 0 && !contains) {
				changeTabAmount(bankTab, 1, false);
				insert(indexOf(item), bankTab == 0 ? tabAmounts[bankTab] - 1 : getData(bankTab, 1));
			}

			if (refresh) {
				refresh();
			}
		}
	}

	public int depositFromNothing(int id, int amount, int tab, boolean update) {
		return depositFromNothing(new Item(id, amount), tab, update);
	}

	/**
	 * Deposit
	 * @param item
	 * @param tab
	 * @param refresh
	 * @return
	 */
	public int depositFromNothing(Item item, int tab, boolean refresh) {
		if (item == null) {
			return 0;
		}

		item = item.unnoted();

		final boolean contains = contains(item.getId());
		final int added = add(item, false);

		if (!contains && added > 0) {
			changeTabAmount(tab, 1, false);
			insert(indexOf(item), tab == 0 ? tabAmounts[tab] - 1 : getData(tab, 1));
		}

		if (refresh) {
			refresh();
		}

		return added;
	}

	/**
	 * Grabs the bank tab data, the available slots
	 * 
	 * @param input
	 * @param type
	 * @return
	 */
	public int getData(int input, int type) {
		int totalSlots = 0;
		for (int tab = 0; tab < (type == 1 ? input + 1 : 10); tab++) {
			if (type == 0 && input <= totalSlots + tabAmounts[tab] - 1 && input >= totalSlots) {
				return tab;
			}
			totalSlots += tabAmounts[tab];
		}
		return totalSlots - 1;
	}

	/**
	 * Drags an item to a tab
	 * 
	 * @param slot
	 *            The slot we're dragging to
	 * @param toTab
	 *            The bank tab we're dragging the item into
	 * @param refresh
	 *            Refresh the container
	 */
	public void itemToTab(int slot, int toTab, boolean refresh) {
		final int fromTab = getData(slot, 0);

		if (fromTab == toTab || (toTab > 1 && tabAmounts[toTab - 1] == 0 && tabAmounts[toTab] == 0)) {
			return;
		}

		int to = getData(toTab, 1);

		if (to > slot) {
			changeTabAmount(toTab, 1, false);
			changeTabAmount(fromTab, -1, refresh, false);
			insert(slot, to);
		} else {
			changeTabAmount(toTab, 1, false);
			changeTabAmount(fromTab, -1, refresh, false);
			to = getData(toTab, 1);
			insert(slot, to);
		}

		if (refresh) {
			shift(true);
		}
	}

	@Override
	public void onFillContainer() {
		player.getActionSender().sendMessage("Your bank is full!");
	}

	@Override
	public void onMaxStack() {
		player.getActionSender().sendMessage("You do not have enough bank space to hold that.");
	}

	/**
	 * Open the bank interface
	 */
	public void open() {
		//Sents the items to the client
		player.getActionSender().sendString("", 60019);
		
		//Max items allowed
		player.getActionSender().sendString("800", 60018);
		//Shift the bank items
		shift(true);
		
		//Sent the interventory along with the bank container
		player.getActionSender().sendInterfaceWithInventoryOverlay(60000, 5063);
		
		//Set the banking attribute true
		player.getAttributes().put("banking", Boolean.TRUE);
	}

	@Override
	public void refresh() {
		player.getActionSender().sendItemOnInterface(InterfaceConstants.WITHDRAW_BANK, stack);
		if (player.getInterfaceState().isInterfaceOpen(InterfaceConstants.DEPOSIT_BOX)) {
			player.getActionSender().sendItemOnInterface(InterfaceConstants.DEPOSIT_BOX, player.getInventory().toArray());
		} else {
			player.getActionSender().sendString("The Bank of The Wildy", 60_005);
		}
		player.getActionSender().sendItemOnInterface(InterfaceConstants.INVENTORY_STORE, player.getInventory().toArray());
		player.getInventory().refresh();
	}

	@Override
	public void refresh(int... slots) {
		for (final int slot : slots) {
			player.getActionSender().sendItemOnInterfaceSlot(InterfaceConstants.WITHDRAW_BANK, stack[slot], slot);
		}
	}

	@Override
	public int remove(Item item, boolean refresh) {
		final int slot = indexOf(item);

		final int removed = super.remove(item, false);

		if (removed == 0 || slot <= -1) {
			return 0;
		}

		if (get(slot) == null || get(slot).getAmount() == 0) {
			final int tab = getData(slot, 0);
			changeTabAmount(tab, -1, true);
		}

		shift(refresh);
		return removed;
	}

	/**
	 * Removes items from the bank
	 * 
	 * @param id
	 *            The item we're removing
	 * @param amount
	 *            The amount we're removing
	 */
	public void delete(int id, int amount) {
		Item item = new Item(id, amount);

		int slot = indexOf(item);

		if (slot > -1) {
			removeSlot(slot, item.getAmount(), false);

			if (get(slot) == null || get(slot).getAmount() == 0) {
				final int tab = getData(slot, 0);

				tabAmounts[tab]--;

				if (tabAmounts[tab] <= 0) {
					collapse(tab, 0, false);
				}
			}

			shift(true);
		}
	}

	public void withdraw(int id, int slot, int amount) {
		withdraw(id, slot, amount, true);
	}

	/**
	 * Withdraw an item from the bank
	 * 
	 * @param id
	 *            The id to take
	 * @param slot
	 *            The slot to remove
	 * @param amount
	 *            The amount to remove
	 * @param check
	 *            Are we even in the actual bank interface?
	 */
	public void withdraw(int id, int slot, int amount, boolean check) {
		if (check && player.getInterfaceState().getCurrentInterface() != 60000) {
			return;
		}

		final Item item = get(slot = indexOf(new Item(id)));

		if (item == null) {
			return;
		}

		if (item.getAmount() < amount) {
			amount = item.getAmount();
		}

		if (noting) {
			if (item.noted().getId() == item.unnoted().getId()) {
				player.getActionSender().sendMessage("This item cannot be withdrawn as a note.");
			}
		}

		final int added = player.getInventory().add(noting ? item.noted().getId() : item.unnoted().getId(), amount);

		if (added >= 1) {
			removeSlot(slot, added, false);
			if (get(slot) == null || get(slot).getAmount() == 0) {
				final int tab = getData(slot, 0);
				tabAmounts[tab]--;
				if (tabAmounts[tab] <= 0) {
					collapse(tab, 0, false);
				}
			}
			shift(true);
		}
	}
}