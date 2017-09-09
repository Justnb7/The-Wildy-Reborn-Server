package com.venenatis.game.world.ground_item;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;

/**
 * An instance of a ground item (items shown on the floor when they are
 * dropped).
 * 
 * @author Patrick van Elderen
 *
 */
public final class GroundItem {

	public enum State {
		SEEN_BY_OWNER,
		SEEN_BY_EVERYONE, 
		HIDDEN;
	}

	/**
	 * The ground item
	 */
	private Item item;

	/**
	 * The location of the ground item
	 */
	private Location location;

	/**
	 * The owner of the ground item
	 */
	private final Player player;

	/**
	 * Checks if the ground item was already removed
	 */
	private boolean removed;

	/**
	 * The amount of ticks before the type changes to PUBLIC
	 */
	private int timer;

	/**
	 * The current state of the ground item
	 */
	private State state = State.SEEN_BY_OWNER;

	/**
	 * Constructs a new ground item object
	 * 
	 * @param item
	 *            The ground item
	 * @param location
	 *            The location of the ground item
	 * @param owner
	 *            The player that owns the ground item
	 */
	public GroundItem(Item item, Location location, Player owner) {
		this.item = item;
		this.setLocation(location);
		this.player = owner;
	}

	/**
	 * Gets the associated item.
	 * 
	 * @return the associated item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * Sets the ground items item
	 * 
	 * @param item
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * Returns the items location
	 * 
	 * @return the location of the ground item
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Sets the location of the ground item
	 * 
	 * @param location
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	public long getOwnerHash() {
		return player == null ? -1 : player.usernameHash;
	}

	/**
	 * Checks if the item was already removed
	 * 
	 * @return removed
	 */
	public boolean isRemoved() {
		return removed;
	}

	/**
	 * Activates or disables ground items
	 * 
	 * @param removed
	 */
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	/**
	 * Decreases ground item timer by one.
	 */
	public int decreaseTimer() {
		return timer--;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	/**
	 * Gets the ground item timer.
	 * 
	 * @return the ground item timer
	 */
	public int getTimer() {
		return timer;
	}

	public State getState() {
		return state;
	}

	/**
	 * Sets the current ground item state
	 * 
	 * @param state
	 *            The current state
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Gets the item owner's username.
	 * 
	 * @return the droppers username
	 */
	public Player getPlayer() {
		return player;
	}

	@Override
	public String toString() {
		return "GroundItem [item=" + item + ", owner=" + player + ", removed=" + removed + ", timer=" + timer
				+ ", state=" + state + ", location="+getLocation()+"]";
	}

}