package com.venenatis.game.content.skills.runecrafting;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.location.Location;

/**
 * This Enum holds all talisman teleporting data.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public enum RiftTeles {

	AIR(25378, Location.create(2984, 3290, 0), Location.create(2841, 4829, 0)),
	MIND(25379, Location.create(2983, 3516, 0), Location.create(2793, 4828, 0)),
	WATER(25376, Location.create(3187, 3166, 0), Location.create(2726, 4832, 0)),
	EARTH(24972, Location.create(3304, 3475, 0), Location.create(2656, 4829, 0)),
	FIRE(24971, Location.create(3315, 3254, 0), Location.create(2576, 4845, 0)),
	BODY(24973, Location.create(3051, 3447, 0), Location.create(2521, 4834, 0)),
	COSMIC(24974, Location.create(2407, 4379, 0), Location.create(2122, 4833, 0)),
	CHAOS(24976, Location.create(3058, 3591, 0), Location.create(2281, 4837, 0)),
	NATURE(24975, Location.create(2869, 3021, 0), Location.create(2400, 4835, 0)),
	LAW(25034, Location.create(2858, 3379, 0), Location.create(2464, 4818, 0)),
	DEATH(25035, Location.create(3087, 3496, 0), Location.create(2208, 4830, 0));
	
	/**
	 * The id of the talisman
	 */
	private final int enterRiftId;
	
	/**
	 * The outside location of the altar
	 */
	private final Location outsideLocation;
	
	/**
	 * The inside location of the altar
	 */
	private final Location insideLocation;
	
	
	/**
	 * Gets a talisman by an item id.
	 *
	 * @param item The item id.
	 * @return The talisman.
	 */
	public static RiftTeles forId(int item) {
		return rifts.get(item);
	}
	
	public static boolean riftIds(int id) {
		for(RiftTeles altar : rifts.values()) {
			if(altar.getRiftId() == id) {
				return true;
			}
		}
		return false;
	}
	/**
	 * The talisman constructor
	 * 
	 * @param id
	 *            The talisman
	 * @param outsideLocation
	 *            The outside coordinates of the altar
	 * @param insideLocation
	 *            The inside coordinates of the altar
	 */
	private RiftTeles(int id, Location outsideLocation, Location insideLocation){
		this.enterRiftId = id;
		this.outsideLocation = outsideLocation;
		this.insideLocation = insideLocation;
	}

	/**
	 * Gets the id.
	 *
	 * @return The id.
	 */
	public int getRiftId() {
		return enterRiftId;
	}

	/**
	 * Gets the outside location of the alter
	 * 
	 * @return the location
	 */
	public Location getOutsideLocation() {
		return outsideLocation;
	}
	/**
	 * Gets the inside location of the alter
	 * 
	 * @return the location
	 */
	public Location getInsideLocation() {
		return insideLocation;
	}
	
	/**
	 * A map of all talismans
	 */
	private static Map<Integer, RiftTeles> rifts = new HashMap<Integer, RiftTeles>();
	
	static{
		for(RiftTeles rift : RiftTeles.values()){
			rifts.put(rift.getRiftId(), rift);
		}
	}
}