package com.venenatis.game.model.container.impl.rune_pouch;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.container.Container;
import com.venenatis.game.model.entity.player.Player;

/**
 * The class which represents functionality for the rune pouch container.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 * @author <a href="http://www.rune-server.org/members/Shadowy/">Jak</a>
 */
public final class RunePouchContainer extends Container {
	
	/**
	 * The rune pouch
	 */
	public final Item RUNE_POUCH = new Item(12791);
	
	/**
	 * The interface id of the container
	 */
	private static final int INTERFACE = 29875;
	
	/**
	 * The start of the item group widget
	 */
	public final static int RUNE_POUCH_CONTAINER = 29908;
	
	/**
	 * The start of the inventory interface widget
	 */
	public final static int INVNTORY_CONTAINER = 29880;
	
	/**
	 * The size of the container
	 */
	public static final int SIZE = 3;
	
	/**
	 * The absolute max number of runes per stack
	 */
	public static final int MAX_COUNT = 16000;

	/**
	 * The player this container is for.
	 */
	private final Player player;

	/**
	 * Constructs a new {@link RunePouchContainer}.
	 */
	public RunePouchContainer(Player player) {
		super(SIZE, ContainerType.ALWAYS_STACK);
		this.player = player;
	}
	
	/**
	 * Checks if the player owns a rune pouch already
	 * @return
	 */
	public boolean ownsPouch() {
		return (player.getBank().contains(12791) || (player.getInventory().contains(12791)));
	}

	/**
	 * Checks if the underlying player has a rune pouch in his inventory and
	 * that the pouch consists of atleast 1 or more runes.
	 * 
	 * @return {@code true} if the player does, {@code false} otherwise.
	 */
	public boolean hasPouch() {
		return player.getInventory().contains(RUNE_POUCH) && this.getSize() > 0;
	}
	
	/**
	 * Gets the amount for the specified slot in the container
	 * @param id
	 * @return
	 */
	public int getAmountForSlot(int id) {
		for (int i = 0; i < RunePouchContainer.SIZE; i++) {
			if (player.getRunePouch().get(i).getId() == id) {
				return player.getRunePouch().get(i).getAmount();
			}
		}
		return 0;
	}
	
	/**
	 * Sends all the respective data for the rune pouch
	 */
	public void display() {
		player.getActionSender().sendInterface(INTERFACE);
		player.getActionSender().sendItemOnInterface(INVNTORY_CONTAINER, player.getInventory().toArray());
		refresh();
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
		//We can't add runes to the container when if we are not actually opening the rune pouch
		if (player.getInterfaceState().getCurrentInterface() != RunePouchContainer.INTERFACE) {
			return;
		}
		
		//We can't store a rune pouch in a rune pouch
		if (id == 12791) {
			player.getActionSender().sendMessage("Don't be silly.");
			return;
		}
		
		//The rune we want to store
		RuneType runeType = RuneType.forId(id);
		
		//We can only store runes in the rune pouch
		if (runeType == null || runeType.equals(RuneType.NONE)) {
			player.getActionSender().sendMessage("You can only place runes inside your rune pouch.");
			return;
		}
		
		//The size of the rune pouch container
		int containerSize = player.getRunePouch().getSize();
		
		Item rune = player.getInventory().get(slot);
		
		//Checks if we have this rune already stored
		boolean containsRune = player.getRunePouch().contains(id);
		
		//If the rune is null do nothing
		if (rune == null) {
			return;
		}
		
		//If we're trying to fake the rune do nothing
		if (rune.getId() != id) {
			return;
		}
		
		//amount = Math.min(16000, amount);
		
		//Check the amount in the rune pouch we can't go over 16,000
		int existing_count = player.getRunePouch().getAmount(rune.getId());
		if (existing_count + amount > RunePouchContainer.MAX_COUNT) {
			player.getActionSender().sendMessage("Your pouch cannot carry anymore of this rune.");
			return;
		}
		
		//Check if all three slots are filled, or have a maximum amount of runes stored
		if (containerSize > RunePouchContainer.SIZE && !containsRune) {
			player.getActionSender().sendMessage("Your pouch cannot hold anymore runes.");
			return;
		}
		
		//The amount we want to add
		int transferAmount = player.getInventory().getAmount(id);
		
		//We can't add more runes then we actually have
		if (transferAmount >= amount) {
			transferAmount = amount;
		} else if (transferAmount == 0) {
			return;
		}
		
		//Add the runes to the container
		player.getRunePouch().add(new Item(rune.getId(), transferAmount));
		
		//Remove the runes from the inventory
		player.getInventory().remove(new Item(rune.getId(), transferAmount), true);
		
		//Refresh the containers
		refresh();
	}

	/**
	 * Removes an item from the pouch
	 * @param id
	 * @param amount
	 * @param slot
	 */
	public void removeItem(int id, int amount, int slot) {
		// We can't remove runes from the container when if we are not actually in the rune pouch interface
		if (player.getInterfaceState().getCurrentInterface() != RunePouchContainer.INTERFACE) {
			return;
		}
		
		Item rune = player.getRunePouch().get(slot);
		//player.debug(String.format("removing from slot %d%n", slot));

		// If the rune is null or when people try to fake runes we do nothing
		if (rune == null || rune.getId() != id) {
			return;
		}

		// The amount we want to remove from the pouch
		int transferAmount = player.getRunePouch().getAmount(id);

		// We can't remove more runes then we actually have stored
		if (transferAmount >= amount) {
			transferAmount = amount;
		} else if (transferAmount == 0) {
			return;
		}

		// Remove runes from the pouch
		player.getRunePouch().remove(new Item(rune.getId(), transferAmount));

		// Add the runes in the players inventory
		player.getInventory().add(new Item(rune.getId(), transferAmount));

		// Refresh the containers
		refresh();
	}
	
	public enum RuneType {
		NONE(-1),
		AIR(556),
		WATER(555),
		EARTH(557),
		FIRE(554),
		MIND(558),
		CHAOS(562),
		DEATH(560),
		BLOOD(565),
		COSMIC(564),
		NATURE(561),
		LAW(563),
		BODY(559),
		SOUL(566),
		ASTRAL(9075),
		MIST(324),
		MUD(4698),
		DUST(4696),
		LAVA(4699),
		STEAM(4694),
		SMOKE(4697);
		
		private int id;
		
		RuneType(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
		
		/**
		 * 
		 * @param rune
		 * @return
		 */
		public static RuneType forId(int rune) {
			for (RuneType type : RuneType.values()) {
				if (type.getId() == rune) {
					return type;
				}
			}
			return null;
		}
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

	@Override
	public void refresh() {

		// The rune pouch item group
		for (int slot = 0; slot < 3; slot++) {
			int id = this.getId(slot);
			if (id == -1) {
				player.getActionSender().sendUpdateItem(RUNE_POUCH_CONTAINER + slot, -1, 0, 0);
				continue;
			}
			int amt = this.get(slot).amount;
			player.getActionSender().sendUpdateItem(RUNE_POUCH_CONTAINER + slot, id, 0, amt);
		}
		
		//Update the inventory container
		player.getActionSender().sendItemOnInterface(INVNTORY_CONTAINER, player.getInventory().toArray());
		player.getInventory().refresh();

		// Custom method sending the runeIds to the client
		sendCounts(this.player);
	}

	@Override
	public void refresh(int... slots) {
		
	}
}
