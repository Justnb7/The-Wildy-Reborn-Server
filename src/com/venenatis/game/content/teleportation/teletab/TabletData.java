package com.venenatis.game.content.teleportation.teletab;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;

/**
 * The teleportation data for tele tabs
 * @author Patrick van Elderen
 *
 */
public enum TabletData {
	
	LUMBRIDGE(new Item(8008), new Location(3222, 3218, 0));
	
	/**
	 * The teletab id
	 */
	private final Item tablet;
	
	/**
	 * The location to teleport to
	 */
	private final Location location;
	
	TabletData(final Item item, final Location location) {
		this.tablet = item;
		this.location = location;
	}

	public Item getTablet() {
		return tablet;
	}

	public Location getLocation() {
		return location;
	}

}
