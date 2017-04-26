package com.model.game.item.container.impl;

import com.model.game.Constants;
import com.model.game.character.combat.Combat;
import com.model.game.character.player.Player;
import com.model.game.character.player.RequestManager.RequestState;
import com.model.game.character.player.RequestManager.RequestType;
import com.model.game.item.Item;
import com.model.task.impl.DistancedActionTask;
import com.model.utility.NameUtils;

/**
 * Handles trading items between players
 * 
 * @author Arithium
 * 
 */
public class Trade {

	/**
	 * The size of the container
	 */
	public final static int SIZE = 28;

	/**
	 * The interface id for the players inventory
	 */
	public static final int PLAYER_INVENTORY_INTERFACE = 3322;

	/**
	 * The trade interface id
	 */
	public static final int TRADE_INVENTORY_INTERFACE = 3415;

	/**
	 * The status of the trade
	 * 
	 * @author Arithium
	 * 
	 */
	public static enum TradeStatus {
		TRADE_SCREEN, ACCEPTED_TRADE_SCREEN, CONFIRM_SCREEN, ACCEPTED_CONFIRM_SCREEN, NONE
	}

	/**
	 * Gets if a player is in a trade or not
	 * 
	 * @param player
	 *            The player to check is in a trade
	 * @return
	 */
	public static boolean inTrade(Player player) {
		return player.getTradeStatus() != TradeStatus.NONE;
	}

	/**
	 * Requests a trade from the other player
	 * 
	 * @param player
	 *            The player requesting the trade
	 * @param other
	 *            The requested player for the trade
	 */
	public static void requestTrade(final Player player, final Player other) {
		if (other.getRequestManager().getState() == RequestState.PARTICIPATING) {
			player.getActionSender().sendMessage("The other player is currently busy.");
			return;
		}
		
		Combat.resetCombat(player);
		
		if (player.distanceToPoint(other.getX(), other.getY()) < 2) {
			startRequest(player, other);
		} else {
			player.setDistancedTask(new DistancedActionTask() {

				@Override
				public void onReach() {
					startRequest(player, other);
					stop();
				}

				@Override
				public boolean reached() {
					return player.distanceToPoint(other.getX(), other.getY()) < 2;
				}

			});
		}
	}

	public static void startRequest(Player player, Player other) {
		Combat.resetCombat(player);
		if (other != null) {
			if (other.getRequestManager().getAcquaintance() == player && other.getRequestManager().getState() == RequestState.REQUESTED) {
				player.getRequestManager().setAcquaintance(other);
				player.getRequestManager().setRequestState(RequestState.REQUESTED);
				player.getRequestManager().setRequestType(RequestType.TRADE);
				openTrade(player, other);
				openTrade(other, player);
			} else {
				player.getRequestManager().setAcquaintance(other);
				player.getRequestManager().setRequestState(RequestState.REQUESTED);
				player.getRequestManager().setRequestType(RequestType.TRADE);
				player.getActionSender().sendMessage("Sending trade request...");
				other.getActionSender().sendMessage(NameUtils.formatName(player.getName()) + player.getRequestManager().getType().getMessage());
			}
		}
	}

	/**
	 * Opens a new trade for the player
	 * 
	 * @param player
	 *            The player opening the trade
	 * @param other
	 *            The other player accessing the trade
	 */
	public static void openTrade(Player player, Player other) {
		player.getRequestManager().setRequestState(RequestState.PARTICIPATING);
		player.setTradeStatus(TradeStatus.TRADE_SCREEN);
		player.getActionSender().sendString(NameUtils.formatName(other.getName()), 3451);
		player.getActionSender().sendString("Trading With: " + NameUtils.formatName(other.getName()) + " who has @gre@" + other.getInventory().remaining() + " free slots.", 3417);
		player.getActionSender().sendString("", 3431);
		player.getActionSender().sendString("Are you sure you want to make this trade?", 3535);
		resetItems(player, other);
		player.getActionSender().sendInterfaceWithInventoryOverlay(3323, 3321);
	}

	/**
	 * Declines the trade for the player, declines for the other player if the
	 * other player isn't null
	 * 
	 * @param player
	 */
	public static void declineTrade(Player player) {
		Player other = player.getRequestManager().getAcquaintance();
		player.setTradeStatus(TradeStatus.NONE);
		if (other != null) {
			if (inTrade(other)) {
				Trade.declineTrade(other);
				other.getActionSender().sendMessage("Other player has declined the trade.");
			}
		}
		for (Item item : player.getTrade().toArray()) {
			if (item != null) {
				player.getInventory().add(item);
			}
		}
		player.getRequestManager().setAcquaintance(null);
		player.getRequestManager().setRequestState(RequestState.NORMAL);
		player.getTrade().clear();
		player.getActionSender().sendRemoveInterfacePacket();
	}

	/**
	 * Offers an item into the trade container
	 * 
	 * @param player
	 *            The player offering an item into the trade container
	 * @param id
	 *            The id of the item
	 * @param slot
	 *            The slot of the item
	 * @param amount
	 *            The amount of the item
	 */
	public static void offerItem(Player player, int id, int slot, int amount) {
		Player other = player.getRequestManager().getAcquaintance();

		if (shouldStopTrade(player, other)) {
			return;
		}

		if (!player.getInventory().contains(id)) {
			return;
		}

		if (player.getTradeStatus() != TradeStatus.TRADE_SCREEN && player.getTradeStatus() != TradeStatus.ACCEPTED_TRADE_SCREEN) {
			return;
		}

		if (amount < 0) {
			return;
		}

		if (amount > Constants.MAX_ITEMS) {
			amount = Constants.MAX_ITEMS;
		}

		Item item = new Item(id, amount);

		if (item.getDefinition().isStackable() && amount > player.getInventory().get(slot).getAmount()) {
			amount = player.getInventory().get(slot).getAmount();
		} else {
			if (amount > player.getInventory().amount(id)) {
				amount = player.getInventory().amount(id);
			}
		}

		item.setAmount(amount);

		player.getTrade().add(item);

		if (!item.getDefinition().isStackable() && amount != 1) {
			player.getInventory().remove(item);
		} else {
			player.getInventory().remove(item, slot);
		}

		resetItems(player, other);
		player.getActionSender().sendString("", 3431);
		other.getActionSender().sendString("", 3431);
		player.setTradeStatus(TradeStatus.TRADE_SCREEN);
		other.setTradeStatus(TradeStatus.TRADE_SCREEN);
		other.getActionSender().sendString("Trading With: " + NameUtils.formatName(player.getName()) + " who has @gre@" + player.getInventory().remaining() + " free slots.", 3417);
	}

	/**
	 * Resets the items for both players
	 * 
	 * @param player
	 * @param other
	 */
	public static void resetItems(Player player, Player other) {
		if (other == null) {
			return;
		}
		player.getActionSender().sendItemsOnInterface(3415, player.getTrade());
		other.getActionSender().sendItemsOnInterface(3416, player.getTrade());
		player.getActionSender().sendItemsOnInterface(3322, player.getInventory());
	}

	/**
	 * Take an item from the trade container
	 * 
	 * @param player
	 *            The player taking an item from the trade container
	 * @param id
	 *            The id of the item
	 * @param slot
	 *            The slot of the item
	 * @param amount
	 *            The amount of the item
	 */
	public static void takeItem(Player player, int id, int slot, int amount) {
		Player other = player.getRequestManager().getAcquaintance();

		if (shouldStopTrade(player, other)) {
			return;
		}

		if (player.getTradeStatus() != TradeStatus.TRADE_SCREEN && player.getTradeStatus() != TradeStatus.ACCEPTED_TRADE_SCREEN) {
			return;
		}

		if (!player.getTrade().contains(id)) {
			return;
		}

		if (amount < 0) {
			return;
		}

		if (amount > Constants.MAX_ITEMS) {
			amount = Constants.MAX_ITEMS;
		}

		Item item = new Item(id, amount);

		if (item.getDefinition().isStackable() && amount > player.getTrade().get(slot).getAmount()) {
			amount = player.getTrade().get(slot).getAmount();
		} else {
			if (amount > player.getTrade().amount(id)) {
				amount = player.getTrade().amount(id);
			}
		}

		item.setAmount(amount);

		player.getInventory().add(item);

		if (!item.getDefinition().isStackable() && amount != 1) {
			player.getTrade().remove(item);
		} else {
			player.getTrade().remove(item, slot);
		}
		resetItems(player, other);
		player.getActionSender().sendString("", 3431);
		other.getActionSender().sendString("", 3431);
		player.setTradeStatus(TradeStatus.TRADE_SCREEN);
		other.setTradeStatus(TradeStatus.TRADE_SCREEN);
		other.getActionSender().sendString("Trading With: " + NameUtils.formatName(player.getName()) + " who has @gre@" + player.getInventory().remaining() + " free slots.", 3417);
	}

	/**
	 * Accepts the trade screen
	 * 
	 * @param player
	 *            The player accepting the trade screen
	 */
	private static void acceptTradeScreen(Player player) {
		Player other = player.getRequestManager().getAcquaintance();

		if (shouldStopTrade(player, other)) {
			return;
		}

		int freeSlots = other.getInventory().remaining();

		if (freeSlots < player.getTrade().size()) {
			player.getActionSender().sendString("Other player doesn't have enough space.", 3431);
			player.setTradeStatus(TradeStatus.TRADE_SCREEN);
			return;
		}

		if (other.getTradeStatus() != TradeStatus.ACCEPTED_TRADE_SCREEN) {
			player.getActionSender().sendString("Waiting for other player...", 3431);
			other.getActionSender().sendString("Other player has accepted...", 3431);
			player.setTradeStatus(TradeStatus.ACCEPTED_TRADE_SCREEN);
		} else {
			openConfirmScreen(player);
			openConfirmScreen(other);
		}
	}

	/**
	 * Opens the confirm screen
	 * 
	 * @param player
	 *            The player opening the confirm screen
	 */
	private static void openConfirmScreen(Player player) {
		Player other = player.getRequestManager().getAcquaintance();

		if (shouldStopTrade(player, other)) {
			return;
		}
		player.setTradeStatus(TradeStatus.CONFIRM_SCREEN);
		player.getActionSender().sendItemsOnInterface(3214, player.getInventory());
		String transferWealth = "Absolutely nothing!";
		String transferAmount = "";
		int count = 0;

		for (Item item : player.getTrade().toArray()) {
			if (item != null) {
				if (item.getId() > 0) {
					if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
						transferAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + item.getAmount() + ")";
					} else if (item.getAmount() >= 1000000) {
						transferAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + item.getAmount() + ")";
					} else {
						transferAmount = "" + item.getAmount();
					}
					if (count == 0) {
						transferWealth = item.getDefinition().getName();
					} else {
						transferWealth = transferWealth + "\\n" + item.getDefinition().getName();
					}
					if (item.getDefinition().isStackable()) {
						transferWealth = transferWealth + " x " + transferAmount;
					}
					count++;
				}
			}
		}

		player.getActionSender().sendString(transferWealth, 3557);
		transferWealth = "Absolutely nothing!";
		transferAmount = "";
		count = 0;

		for (Item item : other.getTrade().toArray()) {
			if (item != null) {
				if (item.getId() > 0) {
					if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
						transferAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + item.getAmount() + ")";
					} else if (item.getAmount() >= 1000000) {
						transferAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + item.getAmount() + ")";
					} else {
						transferAmount = "" + item.getAmount();
					}
					if (count == 0) {
						transferWealth = item.getDefinition().getName();
					} else {
						transferWealth = transferWealth + "\\n" + item.getDefinition().getName();
					}
					if (item.getDefinition().isStackable()) {
						transferWealth = transferWealth + " x " + transferAmount;
					}
					count++;
				}
			}
		}

		player.getActionSender().sendString(transferWealth, 3558);
		player.getActionSender().sendInterfaceWithInventoryOverlay(3443, 197);
	}

	/**
	 * Handles accepting the confirm screen
	 * 
	 * @param player
	 *            The player accepting the confirm screen
	 */
	private static void acceptConfirmScreen(Player player) {
		Player other = player.getRequestManager().getAcquaintance();

		if (shouldStopTrade(player, other)) {
			return;
		}

		if (other.getTradeStatus() == TradeStatus.ACCEPTED_CONFIRM_SCREEN) {
			completeTrade(player);
			completeTrade(other);
			reset(player);
			reset(other);
		} else {
			player.getActionSender().sendString("Waiting for other player to accept...", 3535);
			other.getActionSender().sendString("Other player has accepted...", 3535);
			player.setTradeStatus(TradeStatus.ACCEPTED_CONFIRM_SCREEN);
		}
	}

	/**
	 * Checks if it should stop the trade because a player isn't in the trade or
	 * is null
	 * 
	 * @param player
	 *            The player in the trade
	 * @param other
	 *            THe player this player is trading
	 * @return If the other player is in a trade
	 */
	private static boolean shouldStopTrade(Player player, Player other) {
		if (other == null || !inTrade(other)) {
			declineTrade(player);
			return true;
		}
		return false;
	}

	/**
	 * Completes the trade for the player by giving him the traded items
	 * 
	 * @param player
	 *            The player completing the trade
	 */
	private static void completeTrade(Player player) {
		Player other = player.getRequestManager().getAcquaintance();
		if (shouldStopTrade(player, other)) {
			return;
		}

		for (Item item : other.getTrade().toArray()) {
			if (item != null) {
				player.getInventory().add(item);
			}
		}
		other.getTrade().clear();
	}

	/**
	 * Resets the trade after completion
	 * 
	 * @param player
	 *            The player resetting the trade for
	 */
	private static void reset(Player player) {
		player.setTradeStatus(TradeStatus.NONE);
		player.getRequestManager().setAcquaintance(null);
		player.getRequestManager().setRequestState(RequestState.NORMAL);
		player.getTrade().clear();
		player.getActionSender().sendRemoveInterfacePacket();
	}

	/**
	 * Handles the trade buttons for accepting both trade screens
	 * 
	 * @param player
	 *            The player pressing the button
	 * @param button
	 *            The button being pressed
	 * @return If the button was a trade button
	 */
	public static boolean handleTradeButtons(Player player, int button) {
		switch (button) {
		case 13092:
			acceptTradeScreen(player);
			break;
		case 13218:
			acceptConfirmScreen(player);
			break;
		}
		return false;
	}

}