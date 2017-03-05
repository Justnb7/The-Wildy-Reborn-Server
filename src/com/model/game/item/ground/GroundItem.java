package com.model.game.item.ground;

import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.location.Position;


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
		this(item, new Position(x, y, z), owner);
	}

	public GroundItem(Item item, Position position, Player owner) {
		this.item = item;
		this.setLocation(position);
		this.owner = owner;
	}

	private Position position;
	
	public void setLocation(Position position) {
		this.position = position;
	}
	
	/**
	 * Returns the items position
	 * @return
	 */
	public Position getPosition() {
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