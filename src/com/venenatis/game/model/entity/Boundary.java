package com.venenatis.game.model.entity;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import com.venenatis.game.world.World;


/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Mar 2, 2014
 */
public class Boundary {
	
	int minX, minY, highX, highY;
	int height;
	int min;
	int max;
	
	public Boundary(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	/**
	 * 
	 * @param minX The south-west x coordinate
	 * @param minY The south-west y coordinate
	 * @param highX The north-east x coordinate
	 * @param highY The north-east y coordinate
	 */
	public Boundary(int minX, int minY, int highX, int highY) {
		this.minX = minX;
		this.minY = minY;
		this.highX = highX;
		this.highY = highY;
	}
	
	/**
	 * 
	 * @param minX	 	The south-west x coordinate
	 * @param minY 		The south-west y coordinate
	 * @param highX 	The north-east x coordinate
	 * @param highY 	The north-east y coordinate
	 * @param height	The height of the boundary
	 */
	public Boundary(int minX, int minY, int highX, int highY, int height) {
		this.minX = minX;
		this.minY = minY;
		this.highX = highX;
		this.highY = highY;
		this.height = height;
	}
	
	public int getMinimumX() {
		return minX;
	}
	
	public int getMinimumY() {
		return minY;
	}
	
	public int getMaximumX() {
		return highX;
	}
	
	public int getMaximumY() {
		return highY;
	}
	
	/**
	 * Checks if value is within bounds
	 * @param value
	 * @returns true if value is greater or equal to the minimum bound and 
	 * smaller or equal to the maximum bound
	 */
	public boolean withinBounds(int value) {
		return value <= max && value >= min;
	}
	
	/**
	 * @param value
	 * @returns the specified value if the value is between min and max, 
	 * otherwise min
	 */
	public static int checkBounds(int value, Boundary b) {
		if(value < b.min || value > b.max)
			return b.min;
		return value;
	}
	
	/**
	 * 
	 * @param entity The entity object
	 * @param boundaries The array of Boundary objects
	 * @return
	 */
	public static boolean isIn(Entity entity, Boundary[] boundaries) {
		for(Boundary b : boundaries) {
			if (b.height > 0) {
				if (entity.getZ() != b.height) {
					return false;
				}
			}
			if (entity.getX() >= b.minX && entity.getX() <= b.highX && entity.getY() >= b.minY && entity.getY() <= b.highY) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param entity The player object
	 * @param boundaries The boundary object
	 * @return
	 */
	public static boolean isIn(Entity entity, Boundary boundaries) {
		if (boundaries.height > 0) {
			if (entity.getZ() != boundaries.height) {
				return false;
			}
		}
		return entity.getX() >= boundaries.minX && entity.getX() <= boundaries.highX 
				&& entity.getY() >= boundaries.minY && entity.getY() <= boundaries.highY;
	}
	
	public static boolean isInSameBoundary(Entity player1, Entity player2, Boundary[] boundaries) {
		Optional<Boundary> boundary1 = Arrays.asList(boundaries).stream().filter(b -> isIn(player1, b)).findFirst();
		Optional<Boundary> boundary2 = Arrays.asList(boundaries).stream().filter(b -> isIn(player2, b)).findFirst();
		if (!boundary1.isPresent() || !boundary2.isPresent()) {
			return false;
		}
		return Objects.equals(boundary1.get(), boundary2.get());
	}
	
	public static int entitiesInArea(Boundary boundary) {
		int i = 0;
		for(Entity player : World.getWorld().players)
			if(player != null)
				if(isIn(player, boundary))
					i++;
		return i;
	}
	
	public static final Boundary BANDOS_GODWARS = new Boundary(2864, 5351, 2876, 5369);
	public static final Boundary ARMADYL_GODWARS = new Boundary(2824, 5296, 2842, 5308);
	public static final Boundary ZAMORAK_GODWARS = new Boundary(2918, 5318, 2936, 5331);
	public static final Boundary SARADOMIN_GODWARS = new Boundary(2889, 5258, 2907, 5276);
	
	public static final Boundary[] GODWARS_BOSSROOMS = {
		BANDOS_GODWARS,
		ARMADYL_GODWARS,
		ZAMORAK_GODWARS,
		SARADOMIN_GODWARS
	};
	
	public static final Boundary RESOURCE_AREA = new Boundary(3174, 3924, 3196, 3944);
	public static final Boundary KBD_AREA = new Boundary(2251, 4675, 2296, 4719);
	public static final Boundary PEST_CONTROL_AREA = new Boundary(2650, 2635, 2675, 2655);
	public static final Boundary FIGHT_CAVE = new Boundary(2365, 5052, 2429, 5122);
	public static final Boundary KRAKEN = new Boundary(3680, 5795, 3705, 5825);
	public static final Boundary QUARANTINE = new Boundary(2441, 4760, 2481, 4795);
	public static final Boundary ZULRAH = new Boundary(2251, 3058, 2281, 3088);
	public static final Boundary SCORPIA_PIT = new Boundary(3219, 10331, 3247, 10352);
	public static final Boundary DARK_FORTRESS = new  Boundary(3020, 3623, 3038, 3641);
	
	private static final Boundary VARROCK_BANK = new  Boundary(3180, 3433, 3186, 3448);
	private static final Boundary CAMELOT = new  Boundary(2731, 3486, 2781, 3498);
	private static final Boundary MAGE_BANK = new  Boundary(2540, 3433, 3186, 3447);
	public static final Boundary EDGEVILLE = new Boundary(3069, 3464, 3129, 3518);
	
	
	public static final Boundary[] SAFE_AREAS = {
		EDGEVILLE,
		VARROCK_BANK,
		CAMELOT,
		DARK_FORTRESS,
		MAGE_BANK
	};
	
	public static final Boundary[] DUEL_ARENAS = new Boundary[] {
		new Boundary(3332, 3244, 3359, 3259),
		new Boundary(3364, 3244, 3389, 3259)
	};

}