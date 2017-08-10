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
	private final static int START_ITEM_INTERFACE = 29908;
	
	/**
	 * The start of the inventory interface widget
	 */
	public final static int INVENTORY_INTERFACE = 29880;
	
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
		player.getActionSender().sendItemOnInterface(INVENTORY_INTERFACE, player.getInventory().toArray());
		refresh();
		player.getActionSender().sendMessage("Sending rune pouch widget.");
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
		if (player.getInterfaceState().getCurrentInterface() != RunePouchContainer.INTERFACE) {
			return;
		}
		if (id == 12791) {
			player.getActionSender().sendMessage("Don't be silly.");
			return;
		}
		RuneType runeType = RuneType.forId(id);
		if (runeType == null || runeType.equals(RuneType.NONE)) {
			player.getActionSender().sendMessage("You can only place runes inside your rune pouch.");
			return;
		}
		int containerSize = player.getRunePouch().getSize();
		Item rune = player.getInventory().get(slot);
		boolean containsRune = player.getRunePouch().contains(id);
		if (rune == null) {
			return;
		}
		if (rune.getId() != id) {
			return;
		}
		int existing_count = player.getRunePouch().getAmount(rune.getId());
		if (existing_count + amount > RunePouchContainer.MAX_COUNT) {
			player.getActionSender().sendMessage("Your pouch cannot carry anymore of this rune.");
			return;
		}
		
		/*
		if (containerSize >= RunePouchContainer.SIZE && !containsRune) {
			player.getActionSender().sendMessage("Your pouch cannot hold anymore runes.");
			return;
		}
		try {
			int transferAmount = player.getInventory().getAmount(id);
			if (transferAmount >= amount) {
				transferAmount = amount;
			} else if (transferAmount == 0) {
				return;
			}
			//if (player.getRunePouch().add(new Item(rune.getId(), transferAmount))) {
				player.getInventory().remove(new Item(rune.getId(), transferAmount));
			//}
		} finally {
			refresh();
		}*/
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
			int transferAmount = player.getRunePouch().getAmount(id);
			if (transferAmount >= amount) {
				transferAmount = amount;
			} else if (transferAmount == 0) {
				return;
			}
			player.getRunePouch().remove(new Item(rune.getId(), transferAmount));
			/*if (player.getInventory().add(rune.getId(), transferAmount)) {
				player.getRunePouch().remove(new Item(rune.getId(), transferAmount));
			}*/
		} finally {
			refresh();
		}
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
				player.getActionSender().sendUpdateItem(START_ITEM_INTERFACE + slot, -1, 0, 0);
				continue;
			}
			int amt = this.get(slot).amount;
			player.getActionSender().sendUpdateItem(START_ITEM_INTERFACE + slot, id, 0, amt);
		}

		// Custom method sending the runeIds to the client
		sendCounts(this.player);

	}

	@Override
	public void refresh(int... slots) {
		
	}
}
