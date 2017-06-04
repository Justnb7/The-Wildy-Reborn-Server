package com.model.game.item.container.impl.trade;

import java.util.Optional;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.Container;
import com.model.game.item.container.InterfaceConstants;
import com.model.game.item.container.impl.trade.TradeSession.TradeStage;
import com.model.task.impl.TradeNotificationTask;
import com.model.utility.Utility;

/**
 * The container responsible for trading.
 * 
 * @author Seven | Daniel
 *
 */
public class TradeContainer extends Container {

	/**
	 * The player that owns this container.
	 */
	private final Player player;
	
	/**
	 * The other player this player is trading with.
	 */
	private Optional<Player> other = Optional.empty();

	/**
	 * Creates a new {@link TradeContainer}.
	 * 
	 * @param player
	 *            The player that owns this container.
	 */
	public TradeContainer(Player player) {
		super(28, ContainerType.DEFAULT);
		this.player = player;
	}

	/**
	 * Allows a player to offer their items to the trade.
	 * 
	 * @param item
	 *            The item to offer.
	 */
	public void offerItem(Item item, int limit) {
		
		if (!valid()) {
			return;
		}
		
		if (item.getAmount() > limit) {
			item.setAmount(limit);
		}

		if (item.getAmount() <= 0) {
			return;
		}

		if (!item.isTradeable()) {
			player.getActionSender().sendMessage("This item is not tradeable.");
			return;
		}

		add(item);
		player.getInventory().remove(item);
		refresh();
	}

	/**
	 * Allows a player to offer multiple items at once.
	 * 
	 * @param items
	 *            The items to offer.
	 */
	public void offerItems(Item[] items) {
		
		if (!valid()) {
			return;
		}
		
		for (Item item : items) {

			if (item != null) {
				if (!item.isTradeable()) {
					player.getActionSender().sendMessage("This item is not tradeable.");
					continue;
				}
			}
			add(item, false);
			player.getInventory().remove(item, false);
		}
		refresh();
	}

	/**
	 * Allows a player remove their current offer from the trade.
	 * 
	 * @param item
	 *            The items to remove.
	 */
	public void removeOffer(Item item, int limit) {
		
		if (!valid()) {
			return;
		}		
		
		if (item.getAmount() > limit) {
			item.setAmount(limit);
		}

		if (item.getAmount() <= 0) {
			return;
		}

		remove(item);
		player.getInventory().add(item);
		World.getWorld().schedule(new TradeNotificationTask(player.getTradeSession().getOther().get()));
		refresh();
	}

	/**
	 * Allows a user to remove multiple items currently in the trade container.
	 * 
	 * @param items
	 *            The items to remove.
	 */
	public void removeOfferedItems(Item[] items) {		
		if (!valid()) {
			return;
		}
		
		if (items != null) {
			for (Item item : items) {
				remove(item, false);
				player.getInventory().add(item, false);
			}		
			refresh();
		}
	}

	@Override
	public void onFillContainer() {
		player.getActionSender().sendMessage("You can't add anymore items!");
		return;
	}

	@Override
	public void refresh() {
		
		if (!other.isPresent()) {
			return;
		}

		player.getTradeSession().setTradeStage(TradeStage.OFFER);
		other.get().getTradeSession().setTradeStage(TradeStage.OFFER);

		// updating inventory for this player
		player.getActionSender().sendItemOnInterface(3322, player.getInventory().toArray());

		// updating this player offered items
		player.getActionSender().sendItemOnInterface(InterfaceConstants.PLAYER_TRADE_CONTAINER, stack);

		// update other player offered items
		player.getActionSender().sendItemOnInterface(InterfaceConstants.OTHER_TRADE_CONTAINER, other.get().getTradeContainer().toArray());

		// update other player with our items
		other.get().getActionSender().sendItemOnInterface(InterfaceConstants.OTHER_TRADE_CONTAINER, toArray());

		// update text on interface
		int remaining = other.get().getInventory().getFreeSlots();
		player.getActionSender().sendString(Utility.formatPlayerName(other.get().getName()), 33003);
		player.getActionSender().sendString("has " + remaining + " free", 33004);
		other.get().getActionSender().sendString("has " + player.getInventory().getFreeSlots() + " free", 33004);
		player.getActionSender().sendString("inventory spaces.", 33005);
		player.getActionSender().sendString(formatWealth(calculateWealthDifference(toArray(), other.get().getTradeContainer().toArray())), 33018);
		other.get().getActionSender().sendString(other.get().getTradeContainer().formatWealth(other.get().getTradeContainer().calculateWealthDifference(other.get().getTradeContainer().toArray(), toArray())), 33018);
		player.getActionSender().sendString(calculateWealth(toArray()), 33019);
		player.getActionSender().sendString(other.get().getTradeContainer().calculateWealth(other.get().getTradeContainer().toArray()), 33020);
		other.get().getActionSender().sendString(other.get().getTradeContainer().calculateWealth(other.get().getTradeContainer().toArray()), 33019);
		other.get().getActionSender().sendString(calculateWealth(toArray()), 33020);
		player.getActionSender().sendString("", 33029);
		other.get().getActionSender().sendString("", 33029);

		player.getInventory().refresh();
	}

	private String calculateWealth(Item[] items) {

		String wealth = "Nothing";

		int itemAmount = 0;

		for (Item item : items) {
			if (item == null) {
				continue;
			}
			itemAmount += item.getValue() * item.getAmount();
			wealth = Utility.formatValue(itemAmount);
		}

		return wealth;
	}

	private String formatWealth(int value) {

		if (value > 0) {
			return "<col=ff0000>" + Utility.formatValue(value);
		} else if (value < 0) {
			return "<col=65280>" + Utility.formatValue(value);
		}

		return "<col=ffffff>Absolutely nothing!";
	}

	private int calculateWealthDifference(Item[] items, Item[] otherItems) {
		int amount = 0;
		int otherAmount = 0;
		
		for (Item item : items) {
			if (item == null) {
				continue;
			}
			amount += item.getValue() * item.getAmount();

		}

		for (Item item : otherItems) {
			if (item == null) {
				continue;
			}
			otherAmount += item.getValue() * item.getAmount();
		}
		return amount - otherAmount;
	}

	@Override
	public void refresh(int... slots) {		
		for (final int slot : slots) {
			player.getActionSender().sendItemOnInterfaceSlot(InterfaceConstants.PLAYER_TRADE_CONTAINER, stack[slot], slot);
			player.getActionSender().sendItemOnInterfaceSlot(InterfaceConstants.OTHER_TRADE_CONTAINER, player.getTradeSession().getOther().get().getTradeContainer().stack[slot], slot);
			player.getInventory().refresh();
		}
	}
	
	private boolean valid() {
		if (!player.getTradeSession().getOther().isPresent()) {
			//LoggerUtils.logCheatAttempt("tradeAttempt.txt", "trade offer", "other player does not exist", player);
			return false;
		}
		
		if (player.getInterfaceState().getCurrentInterface() != InterfaceConstants.FIRST_TRADE_SCREEN) {
			//LoggerUtils.logCheatAttempt("tradeAttempt.txt", "trade offer", "first trade screen does not exist", player);
			return false;
		}
		
		if (player.getTradeSession().getTradeStage() != TradeStage.OFFER && player.getTradeSession().getTradeStage() != TradeStage.FIRST_ACCEPT) {
			//LoggerUtils.logCheatAttempt("tradeAttempt.txt", "trade offer", "player trade stage is not offer or first_accept", player);
			return false;
		}
		
		if (other.isPresent()) {
			if (other.get().getTradeSession().getTradeStage() != TradeStage.OFFER && other.get().getTradeSession().getTradeStage() != TradeStage.FIRST_ACCEPT) {
				//LoggerUtils.logCheatAttempt("tradeAttempt.txt", "trade offer", "other player trade stage is not offer or first_accept", player);
				return false;
			}
		}
		return true;
	}

	/**
	 * @return the other
	 */
	public Optional<Player> getOther() {
		return this.other;
	}

	/**
	 * @param other the other to set
	 */
	public void setOther(Optional<Player> other) {
		this.other = other;
	}	
	
}