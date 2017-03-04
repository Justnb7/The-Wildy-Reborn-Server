package com.model.game.character.player.content.trade;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.model.Server;
import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.RequestManager.RequestState;
import com.model.game.character.player.RequestManager.RequestType;
import com.model.game.character.player.content.cluescrolls.ClueDifficulty;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionFinalizeType;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.packets.buttons.ActionButton;
import com.model.game.character.player.packets.buttons.ActionButtonEvent;
import com.model.game.character.player.packets.buttons.ActionButtonEventListener;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.item.Item;
import com.model.game.item.container.impl.TradeContainer;
import com.model.task.impl.DistancedActionTask;
import com.model.utility.Utility;
import com.model.utility.json.definitions.ItemDefinition;
import com.model.utility.logging.PlayerLogging;
import com.model.utility.logging.PlayerLogging.LogType;


/**
 * Handles all of the trading related needs
 * 
 * @author Mobster
 *
 */
public class Trading {

	/**
	 * Requests a trade from another {@link Player}
	 * 
	 * @param player
	 *            The {@link Player} requesting the trade
	 * @param target
	 *            The {@link Player} we are requesting a trade with
	 */
	public static void request(Player player, Player target) {

		if (player == null || target == null) {
			return;
		}

		if (player.distanceToPoint(target.getX(), target.getY()) < 2) {
			startRequest(player, target);
		} else {
			player.setDistancedTask(new DistancedActionTask() {

				@Override
				public void onReach() {
					startRequest(player, target);
					stop();
				}

				@Override
				public boolean reached() {
					return player.distanceToPoint(target.getX(), target.getY()) < 2;
				}

			});
		}

	}

	/**
	 * Starts the request once the player is close enough to the other
	 * 
	 * @param player
	 *            The {@link Player} requesting the trade
	 * @param target
	 *            The {@link Player} this player is requesting a trade with
	 */
	private static void startRequest(Player player, Player target) {
		if (!canRequest(player, target)) {
			return;
		}

		if (isTrading(player)) {
			decline(player);
		}

		player.getRequestManager().setRequestType(RequestType.TRADE);
		player.getRequestManager().setRequestState(RequestState.REQUESTED);
		player.getRequestManager().setAcquaintance(target);

		player.write(new SendMessagePacket("Sending trade request..."));
		target.write(new SendMessagePacket(player.getName() + RequestType.TRADE.getMessage()));

		/*
		 * The other person is trading us so we open the trade
		 */
		if (goodRequest(player, target)) {
			//ken fix, if we want to open the trading screen, we don't want the dueling session to exist anymore.
			DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && !(duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST)) {
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS); 
			}
			open(player, TradeState.TRADE_SCREEN);
			open(target, TradeState.TRADE_SCREEN);
		}
	}

	private static boolean goodRequest(Player player, Player target) {
		Player temp = target.getRequestManager().getAcquaintance();
		if (temp == null || (temp.usernameHash != player.usernameHash)) {
			// if their acquaintance isn't us or is null, dont allow the trade
			// to open
			return false;
		}
		return target.getRequestManager().getState() == RequestState.REQUESTED
				&& target.getRequestManager().getType() == RequestType.TRADE;
	}

	/**
	 * Checks if the {@link Player} can request a trade right now
	 * 
	 * @param player
	 *            The {@link Player} requesting the trade
	 * @param target
	 *            The {@link Player} we are requesting a trade with
	 * @return If we can request a trade right now
	 */
	public static boolean canRequest(Player player, Player target) {
		if (target == null) {
			return false;
		}
		if (player.underAttackBy > 0 || player.underAttackBy2 > 0 || target.underAttackBy > 0
				|| target.underAttackBy2 > 0) {
			return false;
		}
		
		if (player.absX == target.absX && player.absY == target.absY) {
			player.write(new SendMessagePacket("You cannot trade while standing on someone"));
			return false;
		}

		if (target.isBusy() || target.isPlayerTransformed() || target.tradeStatus > 0) {
			player.write(new SendMessagePacket("This player is currently busy."));
			return false;
		}

		if (!player.getController().canTrade(player)) {
			return false;
		}
		return true;
	}

	/**
	 * Opens a trade between 2 players
	 * 
	 * @param player
	 *            The {@link Player} To open the trade screen for
	 */
	public static void open(Player player, TradeState state) {

		Player target = player.getRequestManager().getAcquaintance();

		if (target == null) {
			decline(player);
			return;
		}

		if (state == TradeState.TRADE_SCREEN) {
			player.setTradeState(TradeState.TRADE_SCREEN);
			update(player, TradeState.TRADE_SCREEN);
			player.getItems().resetItems(3322);
			player.getActionSender().sendString("", 3431);
			target.getActionSender().sendString("" + formatPlayerName(player.getName()) + "", 3451);
			player.getActionSender().sendString("" + formatPlayerName(target.getName()) + "", 3451);
			target.getActionSender().sendString("Trading with: " + formatPlayerName(player.getName()) + "", 3417);
			player.getActionSender().sendString("Trading with: " + formatPlayerName(target.getName()) + "", 3417);
			player.getActionSender().sendString(formatPlayerName(target.getName()) + " has\\n "
					+ target.getTradeContainer().remaining() + " free\\n inventory slots.", 23505);
			target.getActionSender().sendString(formatPlayerName(player.getName()) + " has\\n "
					+ player.getTradeContainer().remaining() + " free\\n inventory slots.", 23505);
			player.getActionSender().sendInterfaceWithInventoryOverlay(3323, 3321);
		} else if (state == TradeState.CONFIRM_SCREEN) {
			player.setTradeState(TradeState.CONFIRM_SCREEN);
			update(player, TradeState.CONFIRM_SCREEN);
		}
	}

	/**
	 * Resets the trade for the player
	 * 
	 * @param player
	 *            The {@Link Player} to reset the trade for
	 */
	public static void reset(Player player) {
		player.setTradeState(TradeState.NONE);
		player.getRequestManager().setRequestType(null);
		player.getRequestManager().setAcquaintance(null);
		player.getActionSender().sendRemoveInterfacePacket();
		player.getTradeContainer().clear();
	}

	/**
	 * Formats the username to be displayed
	 * 
	 * @param str
	 *            The {@link String} to format
	 * @return A formatted string for the username
	 */
	private static String formatPlayerName(String str) {
		String str1 = Utility.ucFirst(str);
		str1.replace("_", " ");
		return str1;
	}

	/**
	 * Updates the trade screen for a specific {@link TradeState}
	 * 
	 * @param player
	 *            The {@link Player} to update the trade screen for
	 * @param state
	 *            The {@link TradeState} to update
	 */
	public static void update(Player player, TradeState state) {

		Player target = player.getRequestManager().getAcquaintance();

		if (target == null) {
			decline(player);
			return;
		}
		switch (state) {
		case TRADE_SCREEN:
			resetItems(player);
			resetItems(target);
			player.getItems().resetItems(3322);
			player.getActionSender().sendString("", 3431);
			target.getActionSender().sendString("", 3431);
			break;
		case ACCEPTED_TRADE_SCREEN:
			target.getActionSender().sendString("Other player has accepted", 3431);
			player.getActionSender().sendString("Waiting for other player...", 3431);
			break;
		case CONFIRM_SCREEN:
			writeConfirmScreen(player, target);
			writeConfirmScreen(target, player);
			break;
		case ACCEPTED_CONFIRM_SCREEN:
			target.getActionSender().sendString("Other player has accepted.", 3535);
			player.getActionSender().sendString("Waiting for other player...", 3535);
			break;
		default:
			throw new IllegalArgumentException("Invalid trade state. " + state);
		}
	}

	/**
	 * Writes the confirm screen for the {@link Player}
	 * 
	 * @param player
	 *            The {@link Player} to write the trade screen for
	 * @param target
	 *            The {@link Player} we are trading with
	 */
	private static void writeConfirmScreen(Player player, Player target) {
		target.getActionSender().sendString("Waiting for other player...", 3535);
		player.getActionSender().sendString("Waiting for other player...", 3535);
		player.getItems().resetItems(3214);
		String SendTrade = "Absolutely nothing!";
		String SendAmount;
		int Count = 0;
		for (Item item : player.getTradeContainer().container()) {
			if (item != null && item.id > 0) {
				if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
					SendAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + Utility.format(item.getAmount()) + ")";
				} else if (item.getAmount() >= 1000000) {
					SendAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@("
							+ Utility.format(item.getAmount()) + ")";
				} else {
					SendAmount = "" + Utility.format(item.getAmount());
				}
				// SendAmount = SendAmount;
				if (Count == 0) {
					SendTrade = player.getItems().getItemName(item.id);
				} else {
					SendTrade = SendTrade + "\\n" + player.getItems().getItemName(item.id);
				}
				if (ItemDefinition.forId(item.id).isStackable()) {
					SendTrade = SendTrade + " x " + SendAmount;
				}
				Count++;
			}
		}

		player.getActionSender().sendString(SendTrade, 3557);
		SendTrade = "Absolutely nothing!";
		SendAmount = "";
		Count = 0;

		for (Item item : target.getTradeContainer().container()) {
			if (item != null && item.id > 0) {
				if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
					SendAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + Utility.format(item.getAmount()) + ")";
				} else if (item.getAmount() >= 1000000) {
					SendAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@("
							+ Utility.format(item.getAmount()) + ")";
				} else {
					SendAmount = "" + Utility.format(item.getAmount());
				}
				// SendAmount = SendAmount;
				if (Count == 0) {
					SendTrade = player.getItems().getItemName(item.id);
				} else {
					SendTrade = SendTrade + "\\n" + player.getItems().getItemName(item.id);
				}
				if (ItemDefinition.forId(item.id).isStackable()) {
					SendTrade = SendTrade + " x " + SendAmount;
				}
				Count++;
			}
		}
		player.getActionSender().sendString(SendTrade, 3558);
		player.getActionSender().sendInterfaceWithInventoryOverlay(3443, 197);
	}

	/**
	 * offers an item to the trade
	 * 
	 * @param player
	 *            The {@link Player} offering an item in the trade
	 * @param id
	 *            The id of the item being traded
	 * @param amount
	 *            The amount of the item being traded
	 * @param slot
	 *            The slot of the item in the inventory
	 */
	public static void tradeItem(Player player, int id, int amount, int slot) {

		Player target = player.getRequestManager().getAcquaintance();

		if (target == null) {
			decline(player);
			return;
		}

		if (!cannotTrade(player, target, id)) {
			return;
		}

		if (!player.getItems().contains(id)) {
			return;
		}

		if ((player.playerItems[slot] - 1) != id) {
			return;
		}

		if (ItemDefinition.forId(id).isStackable() && amount > player.playerItemsN[slot]) {
			amount = player.playerItemsN[slot];
		} else if (amount > player.getItems().getItemAmount(id)) {
			amount = player.getItems().getItemAmount(id);
		}

		if (amount <= 0) {
			return;
		}

		final int existing = player.getTradeContainer().amount(id);

		long overflow = overflow(amount, existing);

		if (overflow > Integer.MAX_VALUE) {
			amount = Integer.MAX_VALUE - existing;
		}

		player.getItems().deleteItem(id, amount);

		player.getTradeContainer().add(new Item(id, amount));

		/*
		 * Reset our trade state
		 */
		player.setTradeState(TradeState.TRADE_SCREEN);
		target.setTradeState(TradeState.TRADE_SCREEN);

		/*
		 * Update the interface
		 */
		update(player, TradeState.TRADE_SCREEN);
	}

	/**
	 * Verifies if the player can trade the item
	 * 
	 * @param player
	 *            The {@link Player} trading the item
	 * @param id
	 *            The id of the {@link Item} the player is trading
	 * @return If the player can trade the item
	 */
	private static boolean cannotTrade(Player player, Player target, int id) {
		if (!ItemDefinition.forId(id).isTradeable() || ClueDifficulty.isClue(id)) {
			player.write(new SendMessagePacket("That item isn't tradeable."));
			return false;
		}
		return true;
	}

	/**
	 * Takes an item back from the trade
	 * 
	 * @param player
	 *            The {@link Player} taking an item from the trade
	 * @param id
	 *            id of the item being taken
	 * @param amount
	 *            The amount of the item being taken
	 * @param slot
	 *            The slot of the item on the trade screen
	 */
	public static void takeItem(Player player, int id, int amount, int slot) {
		Player target = player.getRequestManager().getAcquaintance();
		if (target == null) {
			decline(player);
			return;
		}
		if (!player.getTradeContainer().contains(id)) {
			return;
		}

		if (player.getTradeContainer().getId(slot) != id) {
			return;
		}

		if (player.getTradeState() != TradeState.TRADE_SCREEN
				&& player.getTradeState() != TradeState.ACCEPTED_TRADE_SCREEN) {
			return;
		}

		if (ItemDefinition.forId(id).isStackable() && amount > player.getTradeContainer().get(slot).getAmount()) {
			amount = player.getTradeContainer().get(slot).getAmount();
		} else if (amount > player.getTradeContainer().amount(id)) {
			amount = player.getTradeContainer().amount(id);
		}

		final int existing = player.playerItemsN[slot];

		long overflow = overflow(amount, existing);

		if (overflow > Integer.MAX_VALUE) {
			amount = Integer.MAX_VALUE - existing;
		}

		Item item = new Item(id, amount);
		TradeContainer container = player.getTradeContainer();
		
		if (player.getItems().isItemAddable(id, amount) && container.isRemovable(item, -1)) {
			container.remove(item);
			player.getItems().addItem(id, amount);
			player.setTradeState(TradeState.TRADE_SCREEN);
			target.setTradeState(TradeState.TRADE_SCREEN);
			update(player, TradeState.TRADE_SCREEN);
		}
	}

	/**
	 * Resets all of the traded items for the player
	 * 
	 * @param player
	 *            The [@link Player} To reset all of the traded items for
	 */
	private static void resetItems(Player player) {
		/*
		 * This players traded items
		 */
		player.getOutStream().putFrameVarShort(53);
		int offset = player.getOutStream().offset;
		player.getOutStream().writeShort(3415);
		int len = player.getTradeContainer().container().length;
		player.getOutStream().writeShort(len);
		for (Item item : player.getTradeContainer().container()) {
			int id = -1;
			int count = 0;
			if (item != null) {
				id = item.getId();
				count = item.getAmount();
			}
			if (count > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(count);
			} else {
				player.getOutStream().writeByte(count);
			}
			player.getOutStream().writeWordBigEndianA(id + 1);
		}
		player.getOutStream().putFrameSizeShort(offset);
		player.flushOutStream();

		Player target = player.getRequestManager().getAcquaintance();

		if (target == null) {
			decline(player);
			return;
		}
		/*
		 * The other players traded items
		 */
		player.getOutStream().putFrameVarShort(53);
		offset = player.getOutStream().offset;
		player.getOutStream().writeShort(3416);
		len = target.getTradeContainer().container().length;
		player.getOutStream().writeShort(len);
		for (Item item : target.getTradeContainer().container()) {

			int id = -1;
			int count = 0;

			if (item != null) {
				id = item.getId();
				count = item.getAmount();
			}

			if (count > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(count);
			} else {
				player.getOutStream().writeByte(count);
			}
			player.getOutStream().writeWordBigEndianA(id + 1);
		}
		player.getOutStream().putFrameSizeShort(offset);
		player.flushOutStream();
	}

	/**
	 * Declines the trade for the player and alerts the other player to decline
	 * their trade
	 * 
	 * @param player
	 *            The {@link Player} declining the trade
	 */
	public static void decline(Player player) {
		decline(player, true);
	}

	/**
	 * Declines the trade for the player
	 * 
	 * @param player
	 *            The {@link Player} to decline the trade for
	 */
	private static void decline(Player player, boolean tell) {

		for (Item item : player.getTradeContainer().container()) {
			if (item != null) {
				player.getItems().addItem(item);
			}
		}

		Player target = player.getRequestManager().getAcquaintance();

		if (tell && target != null) {
			decline(target, false);
		}

		reset(player);

		if (tell)
			player.write(new SendMessagePacket("The trade has been declined."));
		else
			player.write(new SendMessagePacket("The other player has declined the trade."));
	}

	/**
	 * Accepts the trade screen for the {@link Player}
	 * 
	 * @param player
	 *            The {@link Player} who has accepted the trade screen
	 */
	public static void acceptTradeScreen(Player player) {
		Player target = player.getRequestManager().getAcquaintance();
		if (target == null) {
			decline(player, false);
			return;
		}

		int playerSize = player.getTradeContainer().size();
		int targetSize = target.getTradeContainer().size();
		if (target.getItems().getFreeSlots() < playerSize) {
			player.write(new SendMessagePacket(target.getName() + " only has " + target.getItems().getFreeSlots()
					+ " free slots, please remove " + (playerSize - target.getItems().getFreeSlots()) + " items."));
			target.write(new SendMessagePacket(player.getName() + " has to remove " + (playerSize - target.getItems().getFreeSlots())
					+ " items or you could offer them " + (targetSize - target.getItems().getFreeSlots()) + " items."));
			player.getActionSender().sendString("Not enough inventory space...", 3431);
			target.getActionSender().sendString("Not enough inventory space...", 3431);
			return;
		}

		if (!isOverflow(player, target)) {
			return;
		}

		/*
		 * Set our trade state
		 */
		player.setTradeState(TradeState.ACCEPTED_TRADE_SCREEN);

		/*
		 * Update the interface for both players
		 */
		update(player, TradeState.ACCEPTED_TRADE_SCREEN);

		/*
		 * If the target has already accepted, open confirm screen for both
		 * players
		 */
		if (target.getTradeState() == TradeState.ACCEPTED_TRADE_SCREEN) {
			open(player, TradeState.CONFIRM_SCREEN);
			open(target, TradeState.CONFIRM_SCREEN);
		}
	}

	private static boolean isOverflow(Player player, Player target) {
		// TODO: prevent overflow when trading over items
		return true;
	}

	/**
	 * Accepts the confirm screen for the {@link Player}
	 * 
	 * @param player
	 *            The {@link Player} who has accepted the confirm screen
	 */
	public static void acceptConfirmScreen(Player player) {

		Player target = player.getRequestManager().getAcquaintance();
		if (target == null) {
			decline(player, false);
			return;
		}

		/*
		 * Set our trade state since we've accepted
		 */
		player.setTradeState(TradeState.ACCEPTED_CONFIRM_SCREEN);

		/*
		 * Update the interface for both players
		 */
		update(player, TradeState.ACCEPTED_CONFIRM_SCREEN);

		/*
		 * If our target has accepted, we can go ahead and finish the trade
		 */
		if (target.getTradeState() == TradeState.ACCEPTED_CONFIRM_SCREEN) {

			PlayerLogging.write(LogType.TRADE_LOG, player, "Receiver : " + player.getName() + " " + Arrays.toString(player.getTradeContainer().containerCopy()) + " Trader:" + target.getName() + " " + Arrays.toString(target.getTradeContainer().containerCopy()));
			PlayerLogging.write(LogType.TRADE_LOG, target, "Receiver : " + target.getName() + " " + Arrays.toString(target.getTradeContainer().containerCopy()) + " Trader:" + player.getName() + " " + Arrays.toString(player.getTradeContainer().containerCopy()));
			finish(player, target);
			finish(target, player);

			/*
			 * Reset the trade values since we no longer need this
			 */
			reset(player);
			reset(target);

		}
	}

	/**
	 * Prevents adding an overflow of items
	 * 
	 * @param val1
	 *            The The first value to add
	 * @param val2
	 *            The second value to add
	 * @return
	 */
	private static long overflow(long val1, long val2) {
		return val1 + val2;
	}

	/**
	 * Finishes the trade for the player
	 * 
	 * @param player
	 *            The {@link Player} To finish the trade for
	 */
	public static void finish(Player player, Player target) {

		player.setTradeState(TradeState.NONE);

		/*
		 * Add the other players trade container items
		 */
		for (Item item : target.getTradeContainer().container()) {
			if (item != null) {
				player.getItems().addItem(item);
			}
		}
	}

	/**
	 * Returns if the {@link Player} is in a trade
	 * 
	 * @param c
	 *            The {@link Player} to check if is in a trade
	 * @return If the {@link Player} is in a trade
	 */
	public static boolean isTrading(Player c) {
		return c.getTradeState() != TradeState.NONE;
	}

	/**
	 * Adds the action buttons for the trade system
	 */
	static {
		ActionButtonEventListener.submit(new ActionButtonEvent() {

			@Override
			public void onActionButtonClick(Player player, ActionButton button) {
				if (button.getId() == 13092) {
					Trading.acceptTradeScreen(player);
				} else if (button.getId() == 13218) {
					Trading.acceptConfirmScreen(player);
				} else if (button.getId() == 13094) {
					Trading.decline(player);
				} else if (button.getId() == 13220) {
					Player op = World.getWorld().getPlayers().get(player.tradeWith);
					if (player.isBusy()) {
						if (!op.acceptedTrade && !player.acceptedTrade) {
							Trading.decline(player);
						}
					}
				}
			}

			@Override
			public List<ActionButton> getButtons() {
				return Collections.unmodifiableList(Arrays.asList(new ActionButton(13092, this), new ActionButton(13218, this)));
			}

		});
	}

}
