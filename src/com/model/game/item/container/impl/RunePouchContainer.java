package com.model.game.item.container.impl;

import com.google.common.collect.ImmutableSet;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.SendInterfacePacket;
import com.model.game.item.Item;
import com.model.game.item.container.Container;
import com.model.game.item.container.ItemContainerPolicy;

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

		if (item == null) {
			return false;
		}
		
		if (amount > player.getItems().getItemAmount(item.getId())) {
			amount = player.getItems().getItemAmount(item.getId());
		}
		amount = Math.min(16000, amount);
		
		if(player.getRunePouchContainer().add(new Item(item.getId(), amount))) {
			player.getItems().remove(new Item(item.getId(), amount));
			updatePouch();
		}
		return true;
	}

	/**
	 * Sends the runeIds to the client in form of a string. This will make the
	 * spells light up.
	 * 
	 * @param sendToClient
	 *            The player that sends the string to the client.
	 */
	private static void sendCounts(Player sendToClient) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("#");
		Item i1 = sendToClient.getRunePouchContainer().get(0);
		Item i2 = sendToClient.getRunePouchContainer().get(1);
		Item i3 = sendToClient.getRunePouchContainer().get(2);
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

		sendToClient.getActionSender().sendString(sb.toString(), 49999);
	}

	/**
	 * Attempts to withdraw an item from the container by the specified {@code amount}.
	 * @param player	the player to withdraw this item for.
	 * @param slot		the slot this item is stored from.
	 * @param amount	the amount that is being withdrawed.
	 * @return {@code true} if an item is withdrawed, {@code false} otherwise.
	 */
	public boolean withdraw(Player player, int id, int amount) {
		Item item = player.getItems().getItemFromSlot(player.getItems().getItemSlot(id));
		
		if(item == null) {
			return false;
		}

		// Ensure you can only take out what is actually inside the RP.
		if (amount > player.getRunePouchContainer().amount(item.getId())) {
			amount = player.getRunePouchContainer().amount(item.getId());
		}

		if(player.getRunePouchContainer().remove(new Item(item.getId(), amount))) {
			player.getItems().addItem(new Item(item.getId(), amount));
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
		boolean canAdd = RUNES.stream().filter(rune -> rune == item.getId()).findAny().isPresent();

		if(!canAdd) {
			player.getActionSender().sendMessage("Don't be silly. "+item.getId());
		}
		
		if(this.size() == this.capacity() && !this.spaceFor(item)) {
			player.getActionSender().sendMessage("Your rune pouch is currently full.");
			return false;
		}
		return canAdd;
	}
	
	/**
	 * Sends the withdraw or store method when adding/removing runes.
	 * 
	 * @param player
	 *            The player storing or withdrawing runes.
	 * @param id
	 *            The runeId.
	 * @param amount
	 *            The amount we're storing.
	 * @param interfaceId
	 *            The widgetId
	 * @return
	 */
	public boolean handleRunePouch(Player player, int id, int amount, int interfaceId) {
		if (interfaceId >= START_ITEM_INTERFACE && (interfaceId <= START_ITEM_INTERFACE + 2)) {
			withdraw(player, id, amount);
			return true;
		} else if (interfaceId >= START_INVENTORY_INTERFACE && (interfaceId <= START_INVENTORY_INTERFACE + 27)) {
			store(player, id, amount);
			return true;
		}
		return false;
	}
	
	/**
	 * Updates the inventory widget of the rune pouch interface.
	 */
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
	
	/**
	 * Update the contents of the rune pouch interface
	 */
	private void updatePouch() {
		player.getActionSender().sendUpdateItem(START_ITEM_INTERFACE + 1, -1, 1, 0);
		
		//the rune pouch inventory group
		sendInventoryItems();

		//The rune pouch item group
		for (int slot = 0; slot < 3; slot++) {
			int id = this.getId(slot);
			if (id == -1) {
				player.getActionSender().sendUpdateItem(START_ITEM_INTERFACE + slot, -1, 0, 0);
				continue;
			}
			int amt = this.get(slot).amount;
			player.getActionSender().sendUpdateItem(START_ITEM_INTERFACE + slot, id, 0, amt);
		}
		
		//Custom method sending the runeIds to the client
		sendCounts(this.player);
	}
}
