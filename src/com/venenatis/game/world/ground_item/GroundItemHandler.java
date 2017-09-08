package com.venenatis.game.world.ground_item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.Task.BreakType;
import com.venenatis.game.task.Task.StackType;
import com.venenatis.game.util.logging.PlayerLogging;
import com.venenatis.game.util.logging.PlayerLogging.LogType;
import com.venenatis.game.world.World;
import com.venenatis.game.world.ground_item.GroundItem.State;
import com.venenatis.server.GameEngine;
import com.venenatis.server.Server;

/**
 * A handler for a collection of {@link GroundItem}s
 * 
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public final class GroundItemHandler {

	/**
	 * A list containing all of the ground items
	 */
	private static final List<GroundItem> groundItems = new ArrayList<GroundItem>();
	
	/**
	 * Registers a new ground item.
	 * 
	 * @param groundItem
	 *            the ground item
	 */
	public static boolean register(GroundItem groundItem) {
		Item item = new Item(groundItem.getItem());
		if (find(groundItem.getLocation(), groundItem.getItem().getId()) != null) {
			if (item.isStackable()) {
				find(groundItem.getLocation(), groundItem.getItem().getId()).setItem(new Item(groundItem.getItem().getId(), find(groundItem.getLocation(), groundItem.getItem().getId()).getItem().getAmount() + groundItem.getItem().getAmount()));
				return false;
			}
		}
		groundItems.add(groundItem);
		return true;
	}
	
	/**
	 * Finds the ground item by a given location.
	 * 
	 * @param position
	 *            the item location
	 * 
	 * @param itemIndex
	 *            the item index
	 */
	public static GroundItem find(Location position, int itemIndex) {
		for (GroundItem groundItem : groundItems) {
			if (groundItem == null) {
				continue;
			}
			if (groundItem.getLocation().equals(position) && groundItem.getItem().getId() == itemIndex) {
				return groundItem;
			}
		}
		return null;
	}

	/**
	 * Checks if the ground item is actually on the clicked location.
	 * 
	 * @param id
	 *            The ground item being checked
	 * @param location
	 *            The location of the ground item
	 */
	public static Optional<GroundItem> get(int id, Location location) {
		return groundItems.stream().filter(item -> item.getItem().getId() == id && item.getLocation().getX() == location.getX() && item.getLocation().getY() == location.getY() && item.getLocation().getZ() == location.getZ()).findFirst();
	}

	/**
	 * Sends a remove ground item packet to all players.
	 * 
	 * @param groundItem
	 *            the ground item
	 */
	public static boolean sendRemoveGroundItem(GroundItem groundItem) {
		for (GroundItem other : groundItems) {
			if (groundItem.getItem().getId() == other.getItem().getId()
					&& groundItem.getLocation().getX() == other.getLocation().getX()
					&& groundItem.getLocation().getY() == other.getLocation().getY()
					&& groundItem.getLocation().getZ() == other.getLocation().getZ() && !other.isRemoved()
					&& groundItem.getItem().getAmount() == other.getItem().getAmount()) {
				other.setRemoved(true);
				removeRegionalItem(other);
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes all ground items when the player leaves the region
	 * 
	 * @param groundItem
	 *            the ground item
	 */
	private static void removeRegionalItem(GroundItem groundItem) {
		for (Player player : World.getWorld().getPlayers()) {
			if (player == null || player.distanceToPoint(groundItem.getLocation().getX(), groundItem.getLocation().getY()) > 64) {
				continue;
			}

			//We can go ahead and send the remove ground item packet
			player.getActionSender().sendRemoveGroundItem(groundItem);
		}
	}

	/**
	 * Add ground items for players when entering region.
	 * 
	 * @param groundItem
	 *            The ground item
	 */
	private static void addRegionalItem(GroundItem groundItem) {
		for (Player player : World.getWorld().getPlayers()) {
			if (player == null || player.getLocation().getZ() != groundItem.getLocation().getZ() || player.distanceToPoint(groundItem.getLocation().getX(), groundItem.getLocation().getY()) > 64) {
				continue;
			}

			//Skip iron man game modes, we can't pick up items anyways.
			/*if (player.getAccount().getType().alias().equals(Account.IRON_MAN_TYPE.alias()) || player.getAccount().getType().alias().equals(Account.ULTIMATE_IRON_MAN_TYPE.alias()) || player.getAccount().getType().alias().equals(Account.HARDCORE_IRON_MAN_TYPE.alias())) {
				continue;
			}*/

			// If we are globalizing an item, don't re-add it for the owner
			if (player.usernameHash != groundItem.getOwnerHash()) {

				//Don't add private items to the region yet, we only add public items
				if (groundItem.getState() == State.PRIVATE) {
					continue;
				}

				Item item = new Item(groundItem.getItem().getId());

				//If the item is a non-tradable item continue
				if (!item.isTradeable()) {
					continue;
				}

				//Checks if we're able to view the ground item
				if (player.distanceToPoint(groundItem.getLocation().getX(), groundItem.getLocation().getY()) <= 60 && player.getLocation().getZ() == player.getLocation().getZ()) {
					player.getActionSender().sendGroundItem(groundItem);
				}
			}
		}
	}
	
	/**
	 * The ground item task, removes the ticks
	 */
	public static void pulse() {
		long start = System.currentTimeMillis();
		Iterator<GroundItem> iterator = groundItems.iterator();
		while (iterator.hasNext()) {
			GroundItem item = iterator.next();

			if (item.isRemoved()) {
				iterator.remove();
				continue;
			}
			
			if (item.decreaseTimer() < 1) {
				if (item.getState() == State.PUBLIC) {
					item.setRemoved(true);
					iterator.remove();
					removeRegionalItem(item);
				}

				if (item.getState() == State.PRIVATE) {
					item.setState(State.PUBLIC);
					item.setTimer(200);
					addRegionalItem(item);
				}
			}
		}
		long end = (System.currentTimeMillis() - start);
		GameEngine.profile.items = end;
       // System.out.println("[GroundItemHandler] it took "+end+"ms for "+groundItems.size()+" gitems.");
	}

	public static boolean add(GroundItem groundItem) {
		return groundItems.add(groundItem);
	}

	public static int getItemAmount(GroundItem groundItem) {
		Item item = groundItem.getItem();
		for (GroundItem other : groundItems) {
			if (groundItem.getOwnerHash() == other.getOwnerHash() || other.getState() == (State.PUBLIC)) {
				if (groundItem.getItem().getId() == other.getItem().getId()
						&& groundItem.getLocation().getX() == other.getLocation().getX()
						&& groundItem.getLocation().getY() == other.getLocation().getY()
						&& groundItem.getLocation().getZ() == other.getLocation().getZ() && !other.isRemoved()) {
					if (item.isStackable()) {
						int existingCount = other.getItem().getAmount();
						long newCount = (long) existingCount + item.getAmount();
						if (newCount > Integer.MAX_VALUE) {
							return -1;
						}
						return (int) newCount;
					}
				}
			}
		}
		return 0;
	}

	public static void reloadGroundItems(Player player) {
		for (GroundItem groundItem : groundItems) {
			if (player.getLocation().getZ() != groundItem.getLocation().getZ() || player.distanceToPoint(groundItem.getLocation().getX(), groundItem.getLocation().getY()) > 60) {
				continue;
			}
			
			if (groundItem.getOwnerHash() != player.usernameHash) {
				if (!player.getAccount().getType().unownedDropsVisible() || (groundItem.getState() == State.PRIVATE)) {
					continue;
				}
			}
			
			Item item = new Item(groundItem.getItem().getId());
			
			if (item.isTradeable() || groundItem.getOwnerHash() == player.usernameHash) {
				
				if (groundItem.getState() == State.PUBLIC || groundItem.getOwnerHash() == player.usernameHash) {
					//System.out.println(player.getPosition() + " : " + groundItem.getPosition());
					player.getActionSender().sendRemoveGroundItem(groundItem);
					player.getActionSender().sendGroundItem(groundItem);
				}
			}
		}
	}

	public static boolean createGroundItem(GroundItem groundItem) {
		Player player = groundItem.getOwner();
		if (groundItem.getItem().getId() < 0) {
			return false;
		}
		if (player == null) {
			groundItem.setState(State.PUBLIC);
		}

		//PlayerLogging.write(LogType.DEATH_LOG, groundItem.getOwner(), "Items added to floor : " + groundItem.getItem().getId() + " Amount : "  + groundItem.getItem().getAmount());
		/*if (player != null && player.getAccount().getType().alias().equals(Account.IRON_MAN_TYPE.alias()) || player.getAccount().getType().alias().equals(Account.ULTIMATE_IRON_MAN_TYPE.alias()) || player.getAccount().getType().alias().equals(Account.HARDCORE_IRON_MAN_TYPE.alias())) {
			groundItem.setState(State.PRIVATE);
		}*/
		
		if (groundItem.getItem().getId() >= 2412 && groundItem.getItem().getId() <= 2414) {
			player.getActionSender().sendMessage("The cape vanishes as it touches the ground.");
			return false;
		}

		for (GroundItem other : groundItems) {
			if (groundItem.getItem().getId() == other.getItem().getId()
					&& groundItem.getLocation().getX() == other.getLocation().getX()
					&& groundItem.getLocation().getY() == other.getLocation().getY()
					&& groundItem.getLocation().getZ() == other.getLocation().getZ() && !other.isRemoved()) {
				if (other.getState() == State.PUBLIC || other.getOwnerHash() == player.usernameHash) {
					int existing = getItemAmount(groundItem);

					if (existing == -1) {
						if (player != null) {
							player.getActionSender().sendMessage("There is not enough room for your item on this tile.");
						}
						return false;
					}

					if (existing > 0) {
						other.getItem().setAmount(existing);
						other.setTimer(groundItem.getState() == State.PUBLIC ? 200 : 100);
						return true;
					}
				}
			}
		}

		if (add(groundItem)) {
			groundItem.setTimer(groundItem.getState() == State.PUBLIC ? 200 : 100);
			if (player != null) {
				player.getActionSender().sendGroundItem(groundItem);
			}
			if (groundItem.getState() == State.PUBLIC) {
				addRegionalItem(groundItem);
			}
		}

		return true;
	}

	public static void pickup(Player player, int id, Location position) {
		Optional<GroundItem> optionalGroundItem = get(id, position);
		if (!optionalGroundItem.isPresent()) {
			return;
		}

		GroundItem groundItem = optionalGroundItem.get();

		Server.getTaskScheduler().schedule(new Task(player, 1, true, StackType.STACK, BreakType.NEVER) {
			@Override
			public void execute() {
				if (!player.isActive()) {
					stop();
					return;
				}
				if (groundItem.getState() != State.PUBLIC && groundItem.getOwnerHash() != player.usernameHash) {
					stop();
					return;
				}
				if (groundItem.isRemoved()) {
					stop();
					return;
				}

				Item item = groundItem.getItem();

				if (player.getLocation().getX() == groundItem.getLocation().getX() && player.getLocation().getY() == groundItem.getLocation().getY()) {

					if (player.getInventory().getFreeSlots() == 0 && !(player.getInventory().contains(item.getId()) && item.isStackable())) {
						player.getActionSender().sendMessage("You do not have enough inventory space to pick that up.");
						stop();
						return;
					}
					sendRemoveGroundItem(groundItem);
					player.getInventory().add(item);
					player.getInventory().refresh();
					PlayerLogging.write(LogType.DEATH_LOG, groundItem.getOwner(), "Item Dropped: " + groundItem.getItem().getId() + " Items picked up by : " + player.getUsername() + " Amount:  " + groundItem.getItem().getAmount() );
					stop();
				}
			}
		});
	}

}