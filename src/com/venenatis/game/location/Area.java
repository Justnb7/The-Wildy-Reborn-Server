package com.venenatis.game.location;

import java.util.Arrays;

import com.venenatis.game.constants.GameConstants;
import com.venenatis.game.model.entity.Entity;

/**
 * Resembles an area or region of coordinates.
 * 
 * @author SeVen
 */
public abstract class Area {

	/**
	 * Determines if a specified location is within an area.
	 * 
	 * @param location
	 *            The location to check.
	 * 
	 * @return {@code true} If the specified location is within an area.
	 *         {@code false} otherwise.
	 */
	public abstract boolean inArea(Location location);
	
	public abstract Location getRandomLocation();
    
    public static boolean inGnomeCourse(Entity entity) {
    	return inArea(entity, new Location(2469, 3414), new Location(2490, 3440)) && entity.getLocation().getZ() == 0;
    }
    
    public static boolean inBarbarianCourse(Entity entity) {
    	return inArea(entity, new Location(2530, 3543), new Location(2553, 3556)) && entity.getLocation().getZ() == 0;
    }
    
    public static boolean inWildernessCourse(Entity entity) {
    	return inArea(entity, new Location(2992, 3931), new Location(3007, 3961)) && entity.getLocation().getZ() == 0;
    }
	
	public static boolean inArea(Entity entity, Location bottomLeft, Location topRight) {
		return (entity.getLocation().getX() >= bottomLeft.getX()) && (entity.getLocation().getX() <= topRight.getX()) && (entity.getLocation().getY() >= bottomLeft.getY()) && (entity.getLocation().getY() <= topRight.getY());
	}

	/**
	 * Determines if an entity is in all of these areas.
	 * are
	 * @param entity
	 *            The entity to check.
	 *
	 * @param area
	 *            The areas to check.
	 * 
	 * @return {@code true} If an entity is in all of these areas. {@code false}
	 *         otherwise.
	 */
	public static boolean inAllArea(Entity entity, Area... area) {
		return Arrays.stream(area).allMatch(a -> a.inArea(entity.getLocation()));
	}

	/**
	 * Determines if an entity is in any of the areas.
	 * 
	 * @param entity
	 *            The entity to check.
	 *
	 * @param area
	 *            The areas to check.
	 * 
	 * @return {@code true} If an entity is in any of these areas. {@code false}
	 *         otherwise.
	 */
	public static boolean inAnyArea(Entity entity, Area... area) {
		return Arrays.stream(area).anyMatch(a -> a.inArea(entity.getLocation()));
	}

	/**
	 * Determines if an entity is in the duel arena lobby.
	 * 
	 * @param entity
	 *            The entity to check.
	 * 
	 * @return {@code true} If this entity is in the duel arena. {@code false}
	 *         otherwise.
	 */
	public static boolean inDuelArena(Entity entity) {
		return GameConstants.DUEL_ARENA.stream().anyMatch($it -> $it.inArea(entity.getLocation()));
	}

	/**
	 * Determines if an entity is in the barrows minigame.
	 * 
	 * @param entity
	 *            The entity to check.
	 * 
	 * @return {@code true} If this entity is in the barrows minigame.
	 *         {@code false} otherwise.
	 */
	public static boolean inBarrows(Entity entity) {
		return GameConstants.BARROWS_MINIGAME.stream().anyMatch($it -> $it.inArea(entity.getLocation()));
	}

	/**
	 * Determines if an entity is in a multi-combat zone.
	 * 
	 * @param entity
	 *            The entity to check.
	 * 
	 * @return {@code true} If this entity is in a multi-combat zone.
	 *         {@code false} otherwise.
	 */
	public static boolean inMultiCombatZone(Entity entity) {
		return GameConstants.MULTI_COMBAT_ZONES.stream().anyMatch($it -> $it.inArea(entity.getLocation()) || inGodwars(entity));
	}

	/**
	 * Determines if an entity is in the wilderness.
	 * 
	 * @param entity
	 *            The entity to check.
	 * 
	 * @return {@code true} If this entity is in the wilderness. {@code false}
	 *         otherwise.
	 */
	public static boolean inWilderness(Entity entity) {
		return GameConstants.WILDERNESS != null 
				&& GameConstants.WILDERNESS.stream().anyMatch($it -> $it != null && $it.inArea(entity.getLocation()));
	}
	
	public static boolean inF2P(Entity entity) {
		return GameConstants.F2P_ARENA.stream().anyMatch($it -> $it.inArea(entity.getLocation()));
	}

	public static boolean inGodwars(Entity entity) {
		return inArea(entity, new Location(2816, 5243, 2), new Location(2960, 5400, 2));
	}

}