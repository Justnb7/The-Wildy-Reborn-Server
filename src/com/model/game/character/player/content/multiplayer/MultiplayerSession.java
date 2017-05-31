package com.model.game.character.player.content.multiplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.character.player.serialize.PlayerSerialization;
import com.model.game.item.GameItem;
import com.model.game.item.Item;



public abstract class MultiplayerSession implements MultiplayerSessionItemDistribution, MultiplayerSessionLog {
	
	/**
	 * Represents the total number of players allowed in a single session.
	 */
	public static final int PLAYER_LIMIT = 2;

	/**
	 * A list containing just two players. This list will be maintained to
	 * ensure that no more than two players ever exist within the trade so that
	 * no mischief or glitching is occuring.
	 */
	protected List<Player> players = new ArrayList<>(2);

	/**
	 * A map of each trader and the items they have offered during the trade.
	 */
	protected Map<Player, List<GameItem>> items = new HashMap<>();

	/**
	 * A map containing a list of game items the player has before the trade
	 * starts
	 */
	protected Map<Player, List<GameItem>> presetItems = new HashMap<>();

	/**
	 * The stage this current session is on. See
	 * {@link ab.model.trade.MultiplayerSessionStage} for more information on
	 * stages.
	 */
	protected MultiplayerSessionStage stage = new MultiplayerSessionStage(
			MultiplayerSessionStage.REQUEST);

	/**
	 * The type of session
	 */
	protected MultiplayerSessionType type;

	/**
	 * Constructs a new session for two players
	 * 
	 * @param players
	 */
	public MultiplayerSession(List<Player> players, MultiplayerSessionType type) {
		this.players = players;
		this.type = type;
		players.stream().forEach(
				player -> items.put(player, new ArrayList<GameItem>()));
	}

	public abstract void accept(Player player, Player recipient, int stage);

	public abstract boolean itemAddable(Player player, GameItem item);

	public abstract boolean itemRemovable(Player player, GameItem item);

	public abstract void updateMainComponent();

	public abstract void updateOfferComponents();

	/**
	 * Adds a {@code GameItem} to the list of items that a player has offered
	 * 
	 * @param player
	 *            The player in the duel offering the item or items
	 * @param item
	 *            The game item the player is offering
	 */
	public void addItem(Player player, GameItem item) {
		int id = item.id;
		int amount = item.amount;
		boolean listContainsItem = items.get(player).stream()
				.anyMatch(i -> i.id == id);
		if (Objects.isNull(player) || Objects.isNull(item)) {
			return;
		}
		
		if (player.getAccount().getType().stakingPermitted()) {
			player.getActionSender().sendMessage("You're restricted to stake because of your account type.");
			return;
		}
		
		/*if (receiver.getAccount().getType().stakingPermitted()) {
			player.message("You're restricted to stake because of your account type.");
			return;
		}*/
		
		if (!player.getInventory().contains(id)) {
			return;
		}
		if (!Server.getMultiplayerSessionListener().inAnySession(player)) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		if (Server.getMultiplayerSessionListener()
				.containsSessionInconsistancies(player)) {
			return;
		}
		if (!itemAddable(player, item)) {
			return;
		}
		int inventoryAmount = player.getInventory().getAmount(id);
		if (amount > inventoryAmount) {
			amount = inventoryAmount;
		}
		if (amount < 0 || id < 0) {
			return;
		}
		if (!player.getInventory().contains(id, amount)) {
			return;
		}
		if (!presetListContains(player, id, amount)) {
			player.getActionSender().sendMessage("You cannot offer an item that you didn't have before the session started.");
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		if (!item.stackable && getItems(player).size() + amount >= 28) {
			amount = 28 - getItems(player).size();
		}
		if (getItems(player).size() >= 28) {
			if (!containsItem(player, id) || containsItem(player, id)
					&& !item.stackable) {
				player.getActionSender().sendMessage("You have already offered the maximum possible items.");
				return;
			}
		}
		if (item.stackable && listContainsItem) {
			for (GameItem i : items.get(player)) {
				if (i.id == id) {
					long total = ((long) i.amount + (long) amount);
					if (total > Integer.MAX_VALUE) {
						items.get(player).remove(i);
						player.getInventory().remove(new Item(id, Integer.MAX_VALUE - i.amount));
						items.get(player).add(new GameItem(id, Integer.MAX_VALUE));
					} else {
						items.get(player).remove(i);
						items.get(player).add(new GameItem(id, i.amount + amount));
						player.getInventory().remove(new Item(id, amount));
					}
					break;
				}
			}
		} else if (item.stackable && !listContainsItem) {
			items.get(player).add(new GameItem(id, amount));
			player.getInventory().remove(new Item(id, amount));
		} else {
			while (amount-- > 0) {
				items.get(player).add(new GameItem(id, 1));
				player.getInventory().remove(new Item(id, 1));
			}
		}
		stage.setAttachment(null);
		updateOfferComponents();
	}

	/**
	 * Removes a {@code GameItem} from the list of items that a player has
	 * offered
	 * 
	 * @param player
	 *            The player in the trade removing the items
	 * @param item
	 *            The game item the player is removing
	 */
	public void removeItem(Player player, int slot, GameItem item) {
		/*int id = item.id;
		int amount = item.amount;
		if (Objects.isNull(player) || Objects.isNull(item)) {
			return;
		}
		if (Server.getMultiplayerSessionListener()
				.containsSessionInconsistancies(player)) {
			return;
		}
		if (!itemRemovable(player, item)) {
			return;
		}
		List<GameItem> items = this.items.get(player);
		int freeSlots = player.getInventory().getFreeSlots();
		if (!items.stream().anyMatch(i -> i.id == id)) {
			player.getActionSender().sendMessage("Tried to remove item that does not exist in list.");
			return;
		}
		for (GameItem gameItem : items) {
			if (gameItem.id == id) {
				if (!item.stackable) {
					if (amount > freeSlots) {
						amount = freeSlots;
					}
				}
				break;
			}
		}
		if (amount < 0 || id < 0) {
			return;
		}
		item = new GameItem(id, amount);
		if (item.stackable) {
			for (int i = 0; i < items.size(); i++) {
				if (items.get(i).id == id) {
					long total = ((long) amount + (long) player.getInventory().getAmount(id));
					if (total > Integer.MAX_VALUE) {
						amount = Integer.MAX_VALUE - player.getInventory().getAmount(id);
					}
					if (amount > items.get(i).amount) {
						amount = items.get(i).amount;
					}
					if (!presetListContains(player, id, amount)
							|| inventoryContainsIllegalItem(player,
									new GameItem(id, amount))) {
						items.remove(i);
						player.getActionSender().sendMessage("You tried to remove an item from the screen that you did not have before");
						player.getActionSender().sendMessage("the session started, the item has been deleted because of this.");
						finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
						return;
					}
					if (!containsItem(player, id, amount)) {
						player.getActionSender().sendMessage("Tried to remove item that does not exist in list.");
						return;
					}
					if (items.get(i).amount - amount > 0) {
						if (player.getInventory().add(new Item(id, amount))) {
							int previousAmount = items.get(i).amount;
							items.set(i, new GameItem(id, previousAmount
									- amount));
						}
					} else {
						if (player.getInventory().add(new Item(id, amount))) {
							items.remove(i);
						}
					}
					break;
				}
			}
		} else {
			if (amount > getItemAmount(player, id)) {
				amount = getItemAmount(player, id);
			}
			if (!presetListContains(player, id, amount)
					|| inventoryContainsIllegalItem(player, item)) {
				this.items.get(player).remove(slot);
				player.getActionSender().sendMessage("You tried to remove an item from the trade screen that you did not have before");
				player.getActionSender().sendMessage("the trade started, the item has been deleted because of this.");
				finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			if (!containsItem(player, id, amount)) {
				player.getActionSender().sendMessage("Tried to remove item that does not exist in list.");
				return;
			}
			while (amount-- > 0) {
				if (player.getInventory().add(new Item(id, 1))) {
					for (GameItem i : items) {
						if (i.id == id) {
							items.remove(i);
							break;
						}
					}
				} else {
					break;
				}
			}
		}
		stage.setAttachment(null);
		updateOfferComponents();*/
	}

	public void accept(Player player, int stageId) {
		if (player.getArea().inWild()) {
			return;
		}
		if (!Server.getMultiplayerSessionListener().inAnySession(player)) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		if (stage.getStage() != stageId) {
			
			return;
		}
		if (Server.getMultiplayerSessionListener()
				.containsSessionInconsistancies(player)) {
			return;
		}
		if (players.stream().anyMatch(Objects::isNull)) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		Player recipient = getOther(player);
		boolean illegalItems = false;
		for (Player p : players) {
			Iterator<GameItem> itemList = items.get(p).iterator();
			while (itemList.hasNext()) {
				GameItem item = itemList.next();
				if (!presetListContains(p, item.id, item.amount)
						|| inventoryContainsIllegalItem(p, item)) {
					itemList.remove();
					illegalItems = true;
				}
			}
		}
		if (illegalItems) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		accept(player, recipient, stageId);
	}

	public void refreshItemContainer(Player player, Player other,
			int containerId) {
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(containerId);
		int len = items.get(other).size();
		int current = 0;
		player.getOutStream().writeWord(len);
		if (len > 0) {
			for (GameItem item : items.get(other)) {
				if (item.amount > 254) {
					player.getOutStream().writeByte(255);
					player.getOutStream().writeDWord_v2(item.amount);
				} else {
					player.getOutStream().writeByte(item.amount);
				}
				player.getOutStream().writeWordBigEndianA(item.id + 1);
				current++;
			}
		}
		if (current < 28) {
			for (int i = current; i < 28; i++) {
				player.getOutStream().writeByte(1);
				player.getOutStream().writeWordBigEndianA(-1);
			}
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}

	/**
	 * Declines the trade, the premise for receiving items is depicted by the
	 * situation
	 * 
	 * @param type
	 *            the type of declining protocol
	 */
	public void finish(MultiplayerSessionFinalizeType type) {
		if (stage.getStage() == MultiplayerSessionStage.FINALIZE) {
			throw new IllegalStateException("Attempted to finish session after already being finished once before.");
		}
		stage.setStage(MultiplayerSessionStage.FINALIZE);
		for (Player p : players) {
			Iterator<GameItem> itemList = items.get(p).iterator();
			while (itemList.hasNext()) {
				GameItem item = itemList.next();
				if (!presetListContains(p, item.id, item.amount)
						|| inventoryContainsIllegalItem(p, item)) {
					itemList.remove();
				}
			}
		}
		logSession(type);
		switch (type) {
		case GIVE_ITEMS:
			give();
			break;

		case WITHDRAW_ITEMS:
			withdraw();
			break;

		case DISPOSE_ITEMS:
			dispose();
			break;
		}
		for (Player player : players) {
			if (Objects.isNull(players)) {
				continue;
			}
			if (this.type.equals(MultiplayerSessionType.DUEL)) {
				if (type.equals(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS)) {
					player.getActionSender().removeAllInterfaces();
				}
			}
			//PlayerSerialization.saveGame(player);
			PlayerSerialization.save(player);
		}
		Server.getMultiplayerSessionListener().remove(this);
	}

	/**
	 * Populates the map of preset items from each of the players inventory.
	 */
	public void populatePresetItems() {
		/*for (Player player : players) {
			List<GameItem> realItems = new ArrayList<>();
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				int itemId = player.getInventory().getId(i) - 1;
				int amount = player.getInventory().get(i).getAmount();
				if (itemId > 0 && amount > 0) {
					realItems.add(new GameItem(itemId, amount));
				}
			}
			for (int i = 0; i < player.getEquipment().size(); i++) {
				int itemId = player.playerEquipment[i];
				int amount = player.playerEquipmentN[i];
				if (itemId > 0 && amount > 0) {
					realItems.add(new GameItem(itemId, amount));
				}
			}
			presetItems.put(player, realItems);
		}*/
	}

	/**
	 * Determines if an item exists in the list of game items for a specific
	 * player.
	 * 
	 * @param player
	 *            the player
	 * @param itemId
	 *            the item id
	 * @param amount
	 *            the amount of the item
	 * @return true if the player has the item before the trade
	 */
	protected boolean presetListContains(Player player, int itemId, int amount) {
		if (!new GameItem(itemId, amount).stackable) {
			Optional<GameItem> op = presetItems.get(player).stream()
					.filter(i -> i.id == itemId).findFirst();
			return op.isPresent();
		}
		Optional<GameItem> op = presetItems.get(player).stream()
				.filter(i -> i.id == itemId && i.amount >= amount).findFirst();
		return op.isPresent();
	}

	/**
	 * Determines if the offered item list contains items that the preset list
	 * doesn't contain.
	 * 
	 * @param player
	 *            the player
	 * @return true if the offered items contains an item that the preset doesnt
	 *         have
	 */
	protected boolean inventoryContainsIllegalItem(Player player, GameItem i) {
		long amount;
		if (!i.stackable) {
			amount = presetItems.get(player).stream()
					.filter(item -> item.id == i.id).count();
		} else {
			Optional<GameItem> op = presetItems.get(player).stream()
					.filter(item -> item.id == i.id).findAny();
			if (!op.isPresent()) {
				return true;
			}
			amount = op.get().amount;
		}
		return player.getInventory().getAmount(i.id)
				+ this.getItemAmount(player, i.id) > amount;
	}

	/**
	 * Returns a list of players that are in this session. This list should be
	 * modified from outside the class. This is to prevent the creation of any
	 * technical game bugs.
	 * 
	 * @return
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * Returns the current stage the trade is in, by default it's request.
	 * 
	 * @return the trade stage
	 */
	public MultiplayerSessionStage getStage() {
		return stage;
	}

	/**
	 * Returns the other player in the trade.
	 * 
	 * @param player
	 *            not the player we're trying to retrieve
	 * @return the player
	 */
	public Player getOther(Player player) {
		return players.stream().filter(p -> !Objects.equals(player, p))
				.findAny().get();
	}

	/**
	 * Determines if the item list for the specified player has a certain item,
	 * and item amount.
	 * 
	 * @param player
	 *            the player
	 * @param itemId
	 *            the item we're looking for
	 * @param itemAmount
	 *            the amount of the item we're looking for
	 * @return true if it contains the item, false if not.
	 */
	protected boolean containsItem(Player player, int itemId, int itemAmount) {
		if (!items.containsKey(player))
			return false;
		GameItem item = new GameItem(itemId, itemAmount);
		if (!item.stackable) {
			return items.get(player).stream().filter(i -> i.id == item.id)
					.count() >= itemAmount;
		}
		for (GameItem i : items.get(player)) {
			if (i.id == itemId && i.amount >= itemAmount) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the item list contains atleast one instance of this item
	 * 
	 * @param player
	 *            the player
	 * @param itemId
	 *            the item we're looking for
	 * @return true if it contains the item, false if not.
	 */
	protected boolean containsItem(Player player, int itemId) {
		return containsItem(player, itemId, 1);
	}

	/**
	 * Determines how much of a certain item we have in the session
	 * 
	 * @param player
	 *            the player
	 * @param itemId
	 *            the item
	 * @return
	 */
	protected int getItemAmount(Player player, int itemId) {
		int amount = 0;
		for (GameItem item : items.get(player)) {
			if (item.id == itemId) {
				amount += item.amount;
			}
		}
		return amount;
	}

	/**
	 * Returns a GameItem object if any of the items offered by the player exist
	 * in the recipients inventory, and if the combined total amount is greater
	 * than the maximum value of an Integer type.
	 * 
	 * @param player
	 *            the player with the offered items were checking
	 * @return a GameItem object if the pre-explained conditions are met
	 */
	protected GameItem getOverlappedItem(Player player) {
		Player recipient = getOther(player);
		for (GameItem playerItem : items.get(player)) {
			if (!playerItem.stackable) {
				continue;
			}
			if (!recipient.getInventory().contains(playerItem.id)) {
				continue;
			}
			long amount = ((long) playerItem.amount + (long) recipient.getInventory().getAmount(playerItem.id));
			if (amount > Integer.MAX_VALUE) {
				return new GameItem(playerItem.id, recipient.getInventory().getAmount(playerItem.id));
			}
		}
		return null;
	}

	/**
	 * Returns the list of items offered by the player
	 * 
	 * @param player
	 *            the player
	 * @return a list of items
	 */
	protected List<GameItem> getItems(Player player) {
		if (Objects.isNull(items.get(player)))
			return null;
		return items.get(player);
	}

}
