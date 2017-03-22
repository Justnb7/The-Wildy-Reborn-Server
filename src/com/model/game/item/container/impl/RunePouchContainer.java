package com.model.game.item.container.impl;

import com.google.common.collect.ImmutableSet;
import com.model.game.character.player.Player;
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
	public static boolean open(Player player, int id) {
		if(id != 12791) {
			return false;
		}


		player.getItems().resetItems(5064);
		player.getActionSender().sendInterfaceWithInventoryOverlay(41700, 5063);
		player.getRunePouchContainer().refresh(player, 41710);
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

		if (item == null) {
			return false;
		}
		
		if(ItemDefinition.forId(item.getId()).isStackable() && amount > player.playerItemsN[slot]) {
			amount = player.playerItemsN[slot];
		} else if (amount > player.getItems().getItemAmount(item.getId())) {
			amount = player.getItems().getItemAmount(item.getId());
		}
		amount = Math.min(16000, amount);
		
		if(player.getRunePouchContainer().add(new Item(item.getId(), amount))) {
			player.getItems().remove(new Item(item.getId(), amount));
			player.getRunePouchContainer().refresh(player, 41710);
			player.getItems().resetItems(5064);
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
	public static boolean withdraw(Player player, int slot, int amount) {
		Item item = player.getRunePouchContainer().get(slot);

		if(item == null) {
			return false;
		}

		if(ItemDefinition.forId(item.getId()).isStackable() && amount > player.getRunePouchContainer().amount(item.getId())) {
			amount = player.playerItemsN[slot];
		} else if (amount > player.getRunePouchContainer().amount(item.getId())) {
			amount = player.getRunePouchContainer().amount(item.getId());
		}

		if(player.getRunePouchContainer().remove(new Item(item.getId(), amount))) {
			player.getItems().addItem(new Item(item.getId(), amount));
			player.getItems().resetItems(5064);
			player.getRunePouchContainer().refresh(player, 41710);
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
			player.getActionSender().sendMessage("Don't be silly.");
		}
		
		if(this.size() == this.capacity() && !this.spaceFor(item)) {
			player.getActionSender().sendMessage("Your rune pouch is currently full.");
			return false;
		}

		return canAdd;
	}
}
