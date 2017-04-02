package com.model.game.item.container.impl;

import com.google.common.collect.ImmutableSet;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.SendInterfacePacket;
import com.model.game.item.Item;
import com.model.game.item.container.Container;
import com.model.game.item.container.ItemContainerPolicy;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * The class which represents functionality for the rune pouch container.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class RunePouchContainer extends Container {
	
	/**
	 * The rune pouch
	 */
	public final Item RUNE_POUCH_ID = new Item(12791);
	
	/**
	 * The start of the item group widget
	 */
	private final static int START_ITEM_INTERFACE = 29908;
	
	/**
	 * The start of the inventory interface widget
	 */
	private final int START_INVENTORY_INTERFACE = 29880;
	
	/**
	 * The size of the container
	 */
	public static final int SIZE = 3;

	/**
	 * The player this container is for.
	 */
	private final Player player;

	/**
	 * Constructs a new {@link RunePouchContainer}.
	 */
	public RunePouchContainer(Player player) {
		super(ItemContainerPolicy.STACK_ALWAYS, 3);
		this.player = player;
	}

	@Override
	public boolean contains(Item item) {
		return player.getItems().playerHasItem(12791) && super.contains(item);
	}

	/**
	 * Checks if the underlying player has a rune pouch in his inventory
	 * and that the pouch consists of atleast 1 or more runes.
	 * @return {@code true} if the player does, {@code false} otherwise.
	 */
	public boolean hasPouch() {
		return player.getItems().playerHasItem(12791) && this.size() > 0;
	}
	
	/**
	 * Opens the rune pouch interface.
	 * @param player		the player to open this for.
	 * @param id			the item id that was clicked.
	 * @return {@code true} if the interface was opened, {@code false} otherwise.
	 */
	public boolean open(Player player, int id) {
		if(id != 12791) {
			return false;
		}
		updatePouch();
		player.write(new SendInterfacePacket(29875));
		return true;
	}

	/**
	 * Attempts to store an item to the container by the specified {@code amount}.
	 * @param player	the player to store this item for.
	 * @param slot		the slot this item is stored from.
	 * @param amount	the amount that is being stored.
	 * @return {@code true} if an item is stored, {@code false} otherwise.
	 */
	public boolean store(Player player, int id, int amount) {
		Item item = player.getItems().getItemFromSlot(player.getItems().getItemSlot(id));
		

		//getInventory is a container method i never was able to fix the invneoty container
		//so it wont work i guess kk
		if (item == null) {
			return false;
		}
		
		if (amount > player.getItems().getItemAmount(item.getId())) {
			amount = player.getItems().getItemAmount(item.getId());
		}
		amount = Math.min(16000, amount);
		
		if(player.getRunePouchContainer().add(new Item(item.getId(), amount))) {
			player.getItems().remove(new Item(item.getId(), amount));
			player.getActionSender().sendUpdateItem(START_ITEM_INTERFACE + player.getRunePouchContainer().searchSlot(id), item.getId(), 0, amount);
			updatePouch();
			RunePouchContainer.sendCounts(player);
		}
		return true;
	}

	private static void sendCounts(Player player2) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("#");
		Item i1 = player2.getRunePouchContainer().get(0);
		Item i2 = player2.getRunePouchContainer().get(1);
		Item i3 = player2.getRunePouchContainer().get(2);
		sb.append(i1 == null ? "0" : ""+i1.id);
		sb.append(":");
		sb.append(i1 == null ? "0" : ""+i1.amount);
		sb.append("-");
		sb.append(i2 == null ? "0" : ""+i2.id);
		sb.append(":");
		sb.append(i2 == null ? "0" : ""+i2.amount);
		sb.append("-");
		sb.append(i3 == null ? "0" : ""+i3.id);
		sb.append(":");
		sb.append(i3 == null ? "0" : ""+i3.amount);
		sb.append("$");

		player2.getActionSender().sendString(sb.toString(), 49999); 
	}

	/**
	 * Attempts to withdraw an item from the container by the specified {@code amount}.
	 * @param player	the player to withdraw this item for.
	 * @param slot		the slot this item is stored from.
	 * @param amount	the amount that is being withdrawed.
	 * @return {@code true} if an item is withdrawed, {@code false} otherwise.
	 */
	public boolean withdraw(Player player, int id, int amount) {
		Item item = player.getRunePouchContainer().get(player.getRunePouchContainer().searchSlot(id));
		if(item == null) {
			return false;
		}

		if (amount > player.getRunePouchContainer().amount(item.getId())) {
			amount = player.getRunePouchContainer().amount(item.getId());
		}

		if(player.getRunePouchContainer().remove(new Item(item.getId(), amount))) {
			player.getItems().addItem(new Item(item.getId(), amount));
			player.getRunePouchContainer().refresh(player, START_ITEM_INTERFACE);
			updatePouch();
		}
		return true;
	}

	/**
	 * The runes which can be added to this container.
	 */
	private static final ImmutableSet<Integer> RUNES = ImmutableSet.of(554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 9075);

	@Override
	public boolean canAdd(Item item, int slot) {
		player.debug("update?");
		boolean canAdd = RUNES.stream().filter(rune -> rune == item.getId()).findAny().isPresent();

		if(!canAdd) {
			player.getActionSender().sendMessage("Don't be silly. "+item.getId());
			return false;
		}
		
		if(this.size() == this.capacity() && !this.spaceFor(item)) {
			player.getActionSender().sendMessage("Your rune pouch is currently full.");
			return false;
		}

		return canAdd;
	}
	
	public boolean handleRunePouch(Player player, int id, int amount, int interfaceId) {
		if (interfaceId >= START_ITEM_INTERFACE && (interfaceId <= START_ITEM_INTERFACE + 2)) {
			withdraw(player, id, amount);
			player.debug("withdrawing");
			return true;
		} else if (interfaceId >= START_INVENTORY_INTERFACE && (interfaceId <= START_INVENTORY_INTERFACE + 27)) {
			store(player, id, amount);
			player.debug("adding runes to pouch");
			return true;
		}
		return false;
	}
	
	private void sendInventoryItems() {
		if (!player.getItems().playerHasItem(RUNE_POUCH_ID)) {
			return;
		}
		for (int item = 0; item < 28; item++) {
			int id = 0;
			int amt = 0;

			if (item < player.playerItems.length) {
				id = player.playerItems[item];
				amt = player.playerItemsN[item];
			}
			player.getActionSender().sendUpdateItem(START_INVENTORY_INTERFACE + item, id - 1, 0, amt);
		}
	}
	
	private void updatePouch() {
		sendInventoryItems();
		player.getRunePouchContainer().refresh(player, START_ITEM_INTERFACE);
	}
}
