package com.model.game.item.ground;

import com.model.game.character.Entity;
import com.model.game.character.EntityType;
import com.model.game.character.Hit;
import com.model.game.character.player.ActionSender;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.location.Location;

public final class GroundItem extends Entity {

	public enum State {
		PRIVATE, GLOBAL
	}

	private final Item item;
	private final Player owner;

	private boolean removed;
	private int timer;
	private boolean deathShop;
	private GroundItemType itemType = GroundItemType.PUBLIC;

	private State state = State.PRIVATE;

	public GroundItem(Item item, int x, int y, int z, Player owner) {
		this(item, new Location(x, y, z), owner);
	}

	public GroundItem(Item item, Location location, Player owner) {
		super(EntityType.GROUND_ITEM);
		this.item = item;
		this.setLocation(location);
		this.owner = owner;
	}


	public Item getItem() {
		return item;
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

	public int decreaseTimer() {
		return timer--;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

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

	@Override
	public Hit decrementHP(Hit hit) {
		return null;
	}

	@Override
	public void process() {
		
	}

	@Override
	public ActionSender getActionSender() {
		return null;
	}

}