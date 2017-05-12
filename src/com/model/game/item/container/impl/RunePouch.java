package com.model.game.item.container.impl;

import com.google.common.collect.ImmutableSet;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.Container;
import com.model.game.item.container.ItemContainerPolicy;
import com.model.net.packet.out.SendInterfacePacket;

/**
 * The class which represents functionality for the rune pouch container.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 * @author <a href="http://www.rune-server.org/members/Shadowy/">Jak</a>
 */
public final class RunePouch extends Container {
	
	/**
	 * The rune pouch
	 */
	public final Item RUNE_POUCH = new Item(12791);
	
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
	 * Constructs a new {@link RunePouch}.
	 */
	public RunePouch(Player player) {
		super(ItemContainerPolicy.STACK_ALWAYS, 3);
		this.player = player;
	}

	@Override
	public boolean contains(Item item) {
		return player.getInventory().playerHasItem(RUNE_POUCH) && super.contains(item);
	}

	/**
	 * Checks if the underlying player has a rune pouch in his inventory and
	 * that the pouch consists of atleast 1 or more runes.
	 * 
	 * @return {@code true} if the player does, {@code false} otherwise.
	 */
	public boolean hasPouch() {
		return player.getInventory().playerHasItem(RUNE_POUCH) && this.size() > 0;
	}
	
	/**
	 * Opens the rune pouch interface.
	 * 
	 * @param player
	 *            the player to open this for.
	 * @param id
	 *            the item id that was clicked.
	 * @return {@code true} if the interface was opened, {@code false}
	 *         otherwise.
	 */
	public boolean open(Player player, int id) {
		if(id != RUNE_POUCH.getId()) {
			return false;
		}
		updatePouch();
		player.write(new SendInterfacePacket(29875));
		return true;
	}
	

	/**
	 * Attempts to store an item to the container by the specified
	 * {@code amount}.
	 * 
	 * @param id
	 *            the item that is being stored.
	 * @param amount
	 *            the amount that is being stored.
	 * @param slot
	 *            the slot this item is stored from.
	 * @return {@code true} if an item is stored, {@code false} otherwise.
	 */
	public void addItem(int id, int amount, int slot) {
		
		int containerSize = player.getRunePouch().size();
		Item rune = player.getInventory().getSlot(slot);
		boolean containsRune = player.getRunePouch().contains(id);
		if (rune == null) {
			return;
		}
		if (rune.getId() != id) {
			return;
		}
		int existing_count = player.getRunePouch().getCount(rune.getId());
		if (existing_count + amount > 16_000) {
			player.getActionSender().sendMessage("Your pouch cannot carry anymore of this rune.");
			return;
		}
		if (containerSize >= 3 && !containsRune) {
			player.getActionSender().sendMessage("Your pouch cannot hold anymore runes.");
			return;
		}

		try {
			int transferAmount = player.getInventory().getCount(id);
			if (transferAmount >= amount) {
				transferAmount = amount;
			} else if (transferAmount == 0) {
				return;
			}
			if (player.getRunePouch().add(new Item(rune.getId(), transferAmount), -1)) {
				player.getInventory().remove(new Item(rune.getId(), transferAmount));
			}

		} finally {

			updatePouch();

		}
	}

	/**
	 * Removes an item from the pouch
	 * @param id
	 * @param amount
	 * @param slot
	 */
	public void removeItem(int id, int amount, int slot) {
		Item rune = player.getRunePouch().get(slot);
		
		try {
			if (rune == null || rune.getId() != id) {
				return;
			}
			int transferAmount = player.getRunePouch().getCount(id);
			if (transferAmount >= amount) {
				transferAmount = amount;
			} else if (transferAmount == 0) {
				return;
			}
			
			if (player.getInventory().add(new Item(rune.getId(), transferAmount))) {
				player.getRunePouch().remove(new Item(rune.getId(), transferAmount));
			}
		} finally {
			updatePouch();
		}
	}
	

	/**
	 * The runes which can be added to this container.
	 */
	private static final ImmutableSet<Integer> RUNES = ImmutableSet.of(554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 9075);

	@Override
	public boolean canAdd(Item item, int slot) {
		boolean canAdd = RUNES.stream().filter(rune -> rune == item.getId()).findAny().isPresent();

		if(!canAdd) {
			player.getActionSender().sendMessage("Don't be silly.");
		}
		
		if(this.size() == this.capacity() && !this.spaceFor(item)) {
			player.getActionSender().sendMessage("Your rune pouch is currently full.");
			return false;
		}
		return canAdd;
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
		Item i1 = sendToClient.getRunePouch().get(0);
		Item i2 = sendToClient.getRunePouch().get(1);
		Item i3 = sendToClient.getRunePouch().get(2);
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
	 * Updates the inventory widget of the rune pouch interface.
	 */
	private void sendInventoryItems() {
		if (!player.getInventory().playerHasItem(RUNE_POUCH)) {
			return;
		}
		//Sent the items on the interface
		player.getActionSender().sendUpdateItems(START_INVENTORY_INTERFACE, player.getInventory().container());
	}
	
	/**
	 * Update the contents of the rune pouch interface
	 */
	private void updatePouch() {
		//player.getActionSender().sendUpdateItem(START_ITEM_INTERFACE + 1, -1, 1, 0);
		
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
