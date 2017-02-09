package com.model.game.item.container.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendSidebarInterface;
import com.model.game.item.Item;
import com.model.game.item.container.Container;
import com.model.game.item.container.ItemContainerPolicy;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * The class which represents functionality for the looting bag container.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class LootingBagContainer extends Container {

	/**
	 * The player this container is for.
	 */
	private final Player player;
	
	/**
	 * Constructs a new {@link RunePouchContainer}.
	 */
	public LootingBagContainer(Player player) {
		super(ItemContainerPolicy.NORMAL, 28);
		this.player = player;
	}
	
	@Override
	public boolean contains(Item item) {
		return player.getItems().playerHasItem(11941) && super.contains(item);
	}

	/**
	 * Checks if the underlying player has a looting bag in his inventory
	 * and that the bag consists of atleast 1 or more items.
	 * @return {@code true} if the player does, {@code false} otherwise.
	 */
	public boolean hasLootingBag() {
		return player.getItems().playerHasItem(11941) && this.size() > 0;
	}
	
	/**
	 * Opens the looting bag interface.
	 * @param player		the player to open this for.
	 * @param id			the item id that was clicked.
	 * @return {@code true} if the interface was opened, {@code false} otherwise.
	 */
	public static boolean open(Player player, int id) {
		if(id != 11941) {
			return false;
		}
		player.write(new SendSidebarInterface(3, 44000));
		//player.getPA().sendInterfaceWithInventoryOverlay(26700, 26800);
		return true;
	}
	
	/**
	 * Attempts to store an item to the container by the specified {@code amount}.
	 * @param player	the player to store this item for.
	 * @param slot		the slot this item is stored from.
	 * @param amount	the amount that is being stored.
	 * @return {@code true} if an item is stored, {@code false} otherwise.
	 */
	public static boolean store(Player player, int slot, int amount) {
		Item item = player.getItems().getItemFromSlot(slot);

		if(item == null) {
			return false;
		}

		if(ItemDefinition.forId(item.getId()).isStackable() && amount > player.playerItemsN[slot]) {
			amount = player.playerItemsN[slot];
		} else if (amount > player.getItems().getItemAmount(item.getId())) {
			amount = player.getItems().getItemAmount(item.getId());
		}

		if(player.getLootingBagContainer().add(new Item(item.getId(), amount))) {
			player.getItems().remove(new Item(item.getId(), amount));
			player.getItems().resetItems(5064);
			player.getLootingBagContainer().refresh(player, 26706);
		}
		return true;
	}
	
	/**
	 * Attempts to withdraw an item from the container by the specified {@code amount}.
	 * @param player	the player to withdraw this item for.
	 * @param slot		the slot this item is stored from.
	 * @param amount	the amount that is being withdrawed.
	 * @return {@code true} if an item is withdrawed, {@code false} otherwise.
	 */
	public static boolean withdraw(Player player, int slot, int amount) {
		Item item = player.getLootingBagContainer().get(slot);

		if(item == null) {
			return false;
		}

		if(ItemDefinition.forId(item.getId()).isStackable() && amount > player.getLootingBagContainer().amount(item.getId())) {
			amount = player.playerItemsN[slot];
		} else if (amount > player.getLootingBagContainer().amount(item.getId())) {
			amount = player.getLootingBagContainer().amount(item.getId());
		}

		if(player.getLootingBagContainer().remove(new Item(item.getId(), amount))) {
			player.getItems().addItem(new Item(item.getId(), amount));
			player.getItems().resetItems(5064);
			player.getLootingBagContainer().refresh(player, 26706);
		}
		return true;
	}
}
