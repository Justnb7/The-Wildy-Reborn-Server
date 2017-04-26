package com.model.game.item.container.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.SendSidebarInterfacePacket;
import com.model.game.item.Item;
import com.model.game.item.container.Container;
import com.model.game.item.container.ItemContainerPolicy;

/**
 * The class which represents functionality for the looting bag container.
 * 
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 */
public final class LootingBagContainer extends Container {

	/**
	 * The player this container is for.
	 */
	private final Player player;

	private final Item LOOTING_BAG = new Item(11941);

	/**
	 * Constructs a new {@link RunePouchContainer}.
	 */
	public LootingBagContainer(Player player) {
		super(ItemContainerPolicy.NORMAL, 28);
		this.player = player;
	}

	@Override
	public boolean contains(Item item) {
		return player.getInventory().playerHasItem(LOOTING_BAG) && super.contains(item);
	}

	/**
	 * Checks if the underlying player has a looting bag in his inventory and
	 * that the bag consists of atleast 1 or more items.
	 * 
	 * @return {@code true} if the player does, {@code false} otherwise.
	 */
	public boolean hasLootingBag() {
		return player.getInventory().playerHasItem(LOOTING_BAG) && this.size() > 0;
	}

	/**
	 * Opens the looting bag interface.
	 * 
	 * @param player
	 *            the player to open this for.
	 * @param id
	 *            the item id that was clicked.
	 * @return {@code true} if the interface was opened, {@code false}
	 *         otherwise.
	 */
	public boolean open(Player player, int id) {
		if (id != LOOTING_BAG.getId()) {
			return false;
		}
		showLootingBag();
		player.write(new SendSidebarInterfacePacket(3, 36342));
		return true;
	}

	/**
	 * Opens the deposit interface
	 * 
	 * @param player
	 *            The player storing items.
	 * @param id
	 *            The looting bag
	 * @return {@code true} if the interface was opened, {@code false}
	 *         otherwise.
	 */
	public boolean deposit(Player player, int id) {
		if (id != LOOTING_BAG.getId()) {
			return false;
		}
		sendAddItems();
		player.write(new SendSidebarInterfacePacket(3, 37343));
		return true;
	}

	/**
	 * Attempts to store an item to the container by the specified
	 * {@code amount}.
	 * 
	 * @param player
	 *            the player to store this item for.
	 * @param slot
	 *            the slot this item is stored from.
	 * @param amount
	 *            the amount that is being stored.
	 * @return {@code true} if an item is stored, {@code false} otherwise.
	 */
	public static boolean store(Player player, int slot, int amount) {
		Item item = player.getInventory().get(slot);

		if (item == null) {
			return false;
		}

		return true;
	}

	/**
	 * Attempts to withdraw an item from the container by the specified
	 * {@code amount}.
	 * 
	 * @param player
	 *            the player to withdraw this item for.
	 * @param slot
	 *            the slot this item is stored from.
	 * @param amount
	 *            the amount that is being withdrawed.
	 * @return {@code true} if an item is withdrawed, {@code false} otherwise.
	 */
	public static boolean withdraw(Player player, int slot, int amount) {
		Item item = player.getLootingBagContainer().get(slot);

		if (item == null) {
			return false;
		}

		return true;
	}

	private void showLootingBag() {

	}

	/**
	 * Sends the inventory items on the looting bag interface.
	 */
	private void sendAddItems() {
		if (!player.getInventory().playerHasItem(LOOTING_BAG)) {
			return;
		}
		final int START_ITEM_INTERFACE = 27342;
		for (int inventory_item = 0; inventory_item < 28; inventory_item++) {
			int id = 0;
			int amt = 0;

			if (inventory_item < player.playerInventory.length) {
				id = player.playerInventory[inventory_item];
				amt = player.getInventory().get(inventory_item).getAmount();
			}
			player.getActionSender().sendUpdateItem(START_ITEM_INTERFACE + inventory_item, id - 1, 0, amt);
		}
	}
}
