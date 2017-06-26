package com.venenatis.game.content.activity.minigames.impl.duelarena;

import java.util.Optional;

import com.venenatis.game.content.activity.minigames.impl.duelarena.DuelArena.DuelStage;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.container.Container;
import com.venenatis.game.model.container.impl.InterfaceConstants;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.impl.DuelNotificationTask;
import com.venenatis.game.world.World;

/**
 * The {@link Container} implementation that holds all staked items for the
 * dueling minigame.
 * 
 * @author SeVen
 */
public class DuelContainer extends Container {

	/**
	 * The player that owns this container.
	 */
	private final Player player;
	
	/**
	 * The other player being interacted with.
	 */
	private Optional<Player> other = Optional.empty();

	/**
	 * Creates a new {@link TradeContainer}.
	 * 
	 * @param player
	 *            The player that owns this container.
	 */
	public DuelContainer(Player player) {
		super(28, ContainerType.DEFAULT);
		this.player = player;
	}

	/**
	 * Allows a player to stake an item.
	 * 
	 * @param item
	 *            The item to offer.
	 */
	public void offerItem(Item item, int amount) {		
		if (!valid()){
			return;
		}
		
		if (item.getAmount() > amount) {
			item.setAmount(amount);
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
		System.out.println("offering item");
		
		if (!valid()){
			return;
		}
		
		for (Item item : items) {

			if (item != null) {
				if (!item.isTradeable()) {
					player.getActionSender().sendMessage("You cannot stake this item.");
					continue;
				}
			}
			add(item, false);
			player.getInventory().remove(item, false);
		}
		refresh();
	}

	/**
	 * Allows a player remove their current offer from the stake.
	 * 
	 * @param item
	 *            The items to remove.
	 */
	public void removeOffer(Item item, int limit) {
		if (!valid()){
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
		World.getWorld().schedule(new DuelNotificationTask(other.get()));
		refresh();
	}

	/**
	 * Allows a user to remove multiple items currently in the duel container.
	 * 
	 * @param items
	 *            The items to remove.
	 */
	public void removeOfferedItems(Item[] items) {
		if (!valid()){
			return;
		}
		
		for (Item item : items) {
			remove(item, false);
			player.getInventory().add(item, false);
		}
		refresh();
	}

	@Override
	public void refresh() {
		if (!other.isPresent()) {
			return;
		}

		// a player modified this container so reset accepted.
		player.getDuelArena().setAccepted(false);

		// a player modified this container so reset stage for other player
		// as well.
		other.get().getDuelArena().setAccepted(false);

		// updating inventory for this player
		player.getActionSender().sendItemOnInterface(3322, player.getInventory().toArray());

		// updating this player offered items
		player.getActionSender().sendItemOnInterface(InterfaceConstants.PLAYER_STAKE_CONTAINER, stack);

		// update other player offered items
		player.getActionSender().sendItemOnInterface(InterfaceConstants.OTHER_STAKE_CONTAINER, other.get().getDuelContainer().toArray());

		// update other player with our items
		other.get().getActionSender().sendItemOnInterface(InterfaceConstants.OTHER_STAKE_CONTAINER, toArray());

		// This player modified the container so notify this player
		player.getActionSender().sendString("", 31009);

		// This player modified the container so notify other player
		other.get().getActionSender().sendString("", 31009);

		player.getInventory().refresh();
	}

	@Override
	public void refresh(int... slots) {
		for (final int slot : slots) {
			player.getActionSender().sendItemOnInterfaceSlot(InterfaceConstants.PLAYER_STAKE_CONTAINER, stack[slot], slot);
			player.getActionSender().sendItemOnInterfaceSlot(InterfaceConstants.OTHER_STAKE_CONTAINER, player.getDuelArena().getOther().get().getDuelContainer().stack[slot], slot);
			player.getInventory().refresh();
		}
	}
	
	private boolean valid() {
		if (!player.getDuelArena().getOther().isPresent()) {
			System.out.println("1");
			LoggerUtils.logCheatAttempt("duelAttempt.txt", "duel offer", "other player does not exist", player);
			return false;
		}
		
		if (player.getInterfaceState().getCurrentInterface() != InterfaceConstants.FIRST_DUEL_SCREEN) {
			System.out.println("2");
			LoggerUtils.logCheatAttempt("duelAttempt.txt", "duel offer", "first duel screen does not exist", player);
			return false;
		}
		
		if (player.getDuelArena().getStage() != DuelStage.FIRST_SCREEN) {
			System.out.println("3");
			LoggerUtils.logCheatAttempt("duelAttempt.txt", "duel offer", "player duel stage is not first_accept", player);
			return false;
		}
		
		if (other.isPresent()) {
			System.out.println("9");
			if (other.get().getDuelArena().getStage() != DuelStage.FIRST_SCREEN) {
				LoggerUtils.logCheatAttempt("duelAttempt.txt", "duel offer", "other player duel stage is not first_accept", player);
				return false;
			}
			
			if (other.get().getInterfaceState().getCurrentInterface() != InterfaceConstants.FIRST_DUEL_SCREEN) {
				System.out.println("7");
				LoggerUtils.logCheatAttempt("duelAttempt.txt", "duel offer", "first duel screen does not exist", player);
				return false;
			}
		}
		return true;
	}

	public Optional<Player> getOther() {
		return this.other;
	}

	public void setOther(Optional<Player> other) {
		this.other = other;
	}

}