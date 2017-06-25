package com.venenatis.game.world.ground_item;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;


public final class GroundItem {

	public enum State {
		PRIVATE, GLOBAL
	}

	private Item item;
	private final Player owner;

	private boolean removed;
	private int timer;
	private boolean deathShop;
	private GroundItemType itemType = GroundItemType.PUBLIC;

	private State state = State.PRIVATE;

	public GroundItem(Item item, int x, int y, int z, Player owner) {
		this(item, new Location(x, y, z), owner);
	}

	public GroundItem(Item item, Location position, Player owner) {
		this.item = item;
		this.setLocation(position);
		this.owner = owner;
	}

	private Location position;
	
	public void setLocation(Location position) {
		this.position = position;
	}
	
	/**
	 * Returns the items position
	 * @return
	 */
	public Location getPosition() {
		return position;
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
	 * @param item
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	public long getOwnerHash() {
		return owner == null ? -1 : owner.usernameHash;
	}

	public boolean isRemoved() {
		return removed;
	}

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

	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Gets the item owner's username.
	 * 
	 * @return the droppers username
	 */
	public Player getOwner() {
		return owner;
	}

	public boolean deathShop() {
		return deathShop;
	}

	public void setDeathShop(boolean deathShop) {
		this.deathShop = deathShop;
	}

	public void setGroundItemType(GroundItemType type) {
		this.itemType = type;
	}

	public GroundItemType getType() {
		return itemType;
	}

	@Override
	public String toString() {
		return "GroundItem [item=" + item + ", owner=" + owner + ", removed=" + removed + ", timer=" + timer + ", state=" + state + "]";
	}

}