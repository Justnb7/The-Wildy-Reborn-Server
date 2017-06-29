package com.venenatis.game.model.container.impl.trade;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.container.impl.InterfaceConstants;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.entity.player.save.PlayerSerialization;
import com.venenatis.game.net.packet.ActionSender.MinimapState;
import com.venenatis.game.util.Stopwatch;
import com.venenatis.game.util.StringUtils;
import com.venenatis.game.util.Utility;

/**
 * Represents a session between two {@link Player}s exchanging {@link Item}s.
 * 
 * @author Seven
 */
public final class TradeSession {

	/**
	 * The default stage of this trade session.
	 */
	private TradeStage stage = TradeStage.REQUEST;

	/**
	 * The timer used to prevent players from spamming buttons in trade.
	 */
	private Stopwatch timer = new Stopwatch();

	/**
	 * The player who started this trade.
	 */
	private final Player player;

	/**
	 * The other player in the trade.
	 */
	private Optional<Player> other = Optional.empty();

	/**
	 * Creates a new {@link TradeSession}.
	 *
	 * @param player
	 *            The player who started this trade.
	 */
	public TradeSession(Player player) {
		this.player = player;
	}

	/**
	 * The enumerated types who's elements represent the stages of a trade
	 * session.
	 */
	public enum TradeStage {

		/**
		 * The stage before the session between two players is created.
		 */
		REQUEST,

		/**
		 * The stage when two players are able to offer items.
		 */
		OFFER,

		/**
		 * The stage when two players initially accept each others offers.
		 */
		FIRST_ACCEPT,

		/**
		 * The stage that validates both offers.
		 */
		FINAL_ACCEPT;
	}

	/**
	 * Sends a trade request to another player.
	 * 
	 * @param other
	 *            The other player to send the request to.
	 */
	public void requestTrade(Player player, Player other) {			
		player.setTradeRequest(true);

		if (player.isTrading()) {
			player.getActionSender().sendMessage("You are already trading someone.");
			return;
		}
		
		if (other.getTradeSession().isTrading()) {
			player.getActionSender().sendMessage("That player is currently already trading someone.");
			return;
		}

		if (validRequest(player, other)) {
			// fill the optional
			this.other = Optional.of(other);
			setTradeStage(TradeStage.OFFER);
			other.getTradeSession().setTradeStage(TradeStage.OFFER);
			other.getTradeSession().setOther(Optional.of(player));
			player.getTradeContainer().setOther(this.other);
			other.getTradeContainer().setOther(Optional.of(this.player));
			// bring up the trade interface
			execute(TradeStage.OFFER);
			other.getTradeSession().execute(TradeStage.OFFER);
			other.face(other, player.getLocation());
			player.getActionSender().sendMinimapState(MinimapState.UNCLICKABLE);
			other.getActionSender().sendMinimapState(MinimapState.UNCLICKABLE);
			return;
		}
		player.getActionSender().sendMessage("Sending trade offer...");
		other.getActionSender().sendMessage(Utility.formatName(player.getUsername()) + ":tradereq:");
	}

	/**
	 * Determines if this request is valid.
	 *
	 * @param player
	 *            The player requesting the trade.
	 *
	 * @param other
	 *            The other player that is being interacted with.
	 *
	 * @return {@code true} If this trade is valid. Otherwise {@code false}.
	 */
	private boolean validRequest(Player player, Player other) {
		return player.getOtherPlayerTradeIndex() == other.getIndex() && other.getOtherPlayerTradeIndex() == player.getIndex() && other.isTradeRequest();
	}

	/**
	 * Executes the different stages throughout trade.
	 * 
	 * @param stage
	 *            The stage to execute.
	 */
	public void execute(TradeStage stage) {
		if (!other.isPresent()) {
			declineTrade(true);
			return;
		}

		switch (stage) {

		case REQUEST:
			break;

		case OFFER:
			int remaining = other.get().getInventory().getFreeSlots();
			player.getActionSender().sendItemOnInterface(3322, player.getInventory().toArray());
			player.getActionSender().sendString(String.format("Trading with: %s %s ", Rights.getStringForRights(other.get()), Utility.formatName(other.get().getUsername()), remaining), 33002);
			player.getActionSender().sendString(Utility.formatName(other.get().getUsername()), 33003);
			player.getActionSender().sendString("inventory spaces", 33005);
			player.getActionSender().sendString("<col=ffffff> Absolutely nothing!", 33018);
			player.getActionSender().sendString("", 33029);
			player.getActionSender().sendString("", 33030);
			player.getActionSender().sendInterfaceWithInventoryOverlay(InterfaceConstants.FIRST_TRADE_SCREEN, 3321);
			player.getActionSender().sendItemOnInterface(InterfaceConstants.PLAYER_TRADE_CONTAINER, new Item[] {});
			player.getActionSender().sendItemOnInterface(InterfaceConstants.OTHER_TRADE_CONTAINER, new Item[] {});
			break;

		case FIRST_ACCEPT:
			player.getActionSender().sendItemOnInterface(InterfaceConstants.INVENTORY_INTERFACE, player.getInventory().toArray());
			player.getActionSender().sendString("<col=65535>Are you sure you want to make this trade?", 33202);
			player.getActionSender().sendString("<col=65535>Trading With:", 33207);
			player.getActionSender().sendString(String.format("%s <col=65535>%s", Rights.getStringForRights(other.get()), Utility.formatName(other.get().getUsername())), 33208);
			player.getActionSender().sendString(StringUtils.getItemNames(player.getTradeContainer().toArray()), 33221);
			player.getActionSender().sendString(StringUtils.getItemNames(other.get().getTradeContainer().toArray()), 33251);
			player.getActionSender().sendInterfaceWithInventoryOverlay(InterfaceConstants.SECOND_TRADE_SCREEN, 3213);
			break;

		case FINAL_ACCEPT:
			if (isValidTrade()) {
				if (other.get().getTradeContainer().getTakenSlots() > 0) {
					player.getInventory().add(other.get().getTradeContainer().toTrimmedArray());
				}
				if (player.getTradeContainer().getTakenSlots() > 0) {
					other.get().getInventory().add(player.getTradeContainer().toTrimmedArray());
				}				
				player.getActionSender().sendMessage("You have accepted the trade.");
				other.get().getActionSender().sendMessage("The other player has accepted the trade.");
				
				// reset all the variables after trade was successful
				declineTrade(false);
			} else {
				declineTrade(true);
			}
			break;

		default:
			throw new IllegalArgumentException("Invalid trade stage! " + stage.name());
		}

	}

	public void onButtonClick(int button) {
		if (timer.elapsed(TimeUnit.SECONDS) > 0) {
			timer.reset();

			switch (button) {

			case 128255:
				player.getTradeContainer().offerItems(player.getInventory().toTrimmedArray());
				break;

			case 129002:
				player.getTradeContainer().removeOfferedItems(player.getTradeContainer().toTrimmedArray());
				break;

			case 128244:
			case 129188:
				player.getTradeSession().declineTrade(true);
				break;

			case 128241:
				if (player.getInventory().getFreeSlots() < other.get().getTradeContainer().getTakenSlots()) {
					player.getActionSender().sendMessage("You need more inventory space to accept this.");
					other.get().getActionSender().sendMessage("The other player needs more inventory space.");
					return;
				}

				stage = TradeStage.FIRST_ACCEPT;
				other.get().getActionSender().sendString("<col=ffffff>Other player has accepted.", 33029);
				player.getActionSender().sendString("<col=ffffff>Waiting for other player...", 33029);
				if (other.get().getTradeSession().getTradeStage() == TradeStage.FIRST_ACCEPT) {
					execute(stage);
					other.ifPresent($it -> $it.getTradeSession().execute(stage));
				}
				break;

			case 129185:
				if (player.getInventory().getFreeSlots() < other.get().getTradeContainer().getTakenSlots()) {
					player.getActionSender().sendMessage("You need more inventory space to accept this.");
					other.get().getActionSender().sendMessage("The other player needs more inventory space.");
					return;
				}

				stage = TradeStage.FINAL_ACCEPT;
				other.get().getActionSender().sendString("<col=ffffff>Other player has accepted.", 33202);
				player.getActionSender().sendString("<col=ffffff>Waiting for other player...", 33202);
				if (other.get().getTradeSession().getTradeStage() == stage) {
					execute(stage);
				}
				break;
			}
		}
	}

	/**
	 * Resets the trade and prepares for another trade.
	 * 
	 * @param declined
	 *            The flag that denotes a player declined the trade.
	 */
	public void declineTrade(boolean declined) {
		// give the players items back to them because they haven't traded yet,
		// and there items are in the container.
		if (!other.isPresent() || (stage != TradeStage.FINAL_ACCEPT && other.get().getTradeSession().getTradeStage() != TradeStage.FINAL_ACCEPT) || (stage == TradeStage.FINAL_ACCEPT && other.get().getTradeSession().getTradeStage() != TradeStage.FINAL_ACCEPT) || (other.get().getTradeSession().getTradeStage() == TradeStage.FINAL_ACCEPT && stage != TradeStage.FINAL_ACCEPT)) {

			// give this players items back
			if (player.getInventory().getFreeSlots() >= player.getTradeContainer().getTakenSlots() && !player.getTradeContainer().isEmpty()) {
				player.getInventory().add(player.getTradeContainer().toTrimmedArray());
			}

			// if the other person exists give them their items back too
			if (other.isPresent()) {
				if (other.get().getInventory().getFreeSlots() >= other.get().getTradeContainer().getTakenSlots() && !other.get().getTradeContainer().isEmpty()) {
					other.get().getInventory().add(other.get().getTradeContainer().toTrimmedArray());
				}
			}

		}

		// tell the users the trade was declined.
		if (declined) {
			player.getActionSender().sendMessage("You have declined the trade.");
			other.ifPresent($it -> $it.getActionSender().sendMessage("The other player has declined the trade!"));
		}
		
		player.getTradeContainer().clear(true);
		other.ifPresent($it -> $it.getTradeContainer().clear(true));
		
		PlayerSerialization.save(player);
		other.ifPresent($it -> PlayerSerialization.save($it));

		player.getActionSender().removeAllInterfaces();
		other.ifPresent($it -> $it.getActionSender().removeAllInterfaces());

		player.getActionSender().sendMinimapState(MinimapState.NORMAL);
		other.ifPresent($it -> $it.getActionSender().sendMinimapState(MinimapState.NORMAL));
		
		player.setTradeRequest(false);
		other.ifPresent($it -> $it.setTradeRequest(false));

		stage = TradeStage.REQUEST;
		other.ifPresent($it -> $it.getTradeSession().setTradeStage(TradeStage.REQUEST));			
		
		other.ifPresent($it -> $it.getTradeSession().setOther(Optional.empty()));
		other = Optional.empty();
		
	}

	/**
	 * The enumerated types that represent the state this trade is in.
	 * 
	 * @return The state this trade is in.
	 */
	public TradeStage getTradeStage() {
		return stage;
	}

	/**
	 * @param tradeStage
	 *            The state this trade is in.
	 */
	public void setTradeStage(TradeStage tradeStage) {
		this.stage = tradeStage;
	}

	/**
	 * Determines if a {@link Player} is in a trade session.
	 * 
	 * @return {@code true} If the player is in a trade session. {@code false}
	 *         otherwise.
	 */
	public boolean isTrading() {
		return stage != TradeStage.REQUEST;
	}

	/**
	 * Determines if this trade is valid.
	 * 
	 * {@code true} if this trade is valid. {@code false} otherwise.
	 */
	private boolean isValidTrade() {
		return player.isRegistered() && player != null && (other.isPresent() && other.get().isRegistered()) && getTradeStage() == other.get().getTradeSession().getTradeStage();
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param other
	 *            the other to set
	 */
	public void setOther(Optional<Player> other) {
		this.other = other;
	}

	/**
	 * @return the other
	 */
	public Optional<Player> getOther() {
		return other;
	}

	@Override
	public String toString() {
		return String.format("[TRADE] - Player: %s [STATE=%s] Other: %s [STATE=%s]", player.getUsername(), stage.name(), other.isPresent() ? other.get().getUsername() : "No one", other.isPresent() ? other.get().getTradeSession().getTradeStage().name() : "None");

	}

}