package com.model.game.location;

import com.model.game.character.Entity;

/**
 * Represents a single location in the game world.
 * 
 * @author Graham Edgecombe
 * @author Thomas Nappo
 */
public class Location {

	/**
	 * The x coordinate.
	 */
	private final int x;

	/**
	 * The y coordinate.
	 */
	private final int y;

	/**
	 * The z coordinate.
	 */
	private final int z;

	public int lastX, lastY;
	
	/**
	 * Creates a location.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @param z
	 *            The z coordinate.
	 * @return The location.
	 */
	public static Location create(int x, int y, int z) {
		return new Location(x, y, z);
	}

	/**
	 * Creates a location.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @return loc(x, y, 0)
	 */
	public static Location create(int x, int y) {
		return new Location(x, y, 0);
	}

	/**
	 * Creates a new {@link Location} with a default {@code height} of {@code 0}
	 * .
	 * 
	 * @param x
	 *            The x coordinate on a grid.
	 * @param y
	 *            The y coordinate on a grid.
	 */
	public Location(int x, int y) {
		this(x, y, 0);
	}

	/**
	 * Creates a new {@link Location}.
	 * 
	 * @param x
	 *            The x coordinate on a grid.
	 * @param y
	 *            The y coordinate on a grid.
	 * @param height
	 *            The height or plane.
	 */
	public Location(int x, int y, int height) {
		this.x = x;
		this.y = y;
		this.z = height;
	}

	/**
	 * Gets the absolute x coordinate.
	 * 
	 * @return The absolute x coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the absolute y coordinate.
	 * 
	 * @return The absolute y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the z coordinate, or height.
	 * 
	 * @return The z coordinate.
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Gets the local x coordinate relative to this region.
	 * 
	 * @return The local x coordinate relative to this region.
	 */
	public int getLocalX() {
		return getLocalX(this);
	}

	/**
	 * Gets the local y coordinate relative to this region.
	 * 
	 * @return The local y coordinate relative to this region.
	 */
	public int getLocalY() {
		return getLocalY(this);
	}

	/**
	 * Gets the local x coordinate relative to a specific region.
	 * 
	 * @param l
	 *            The region the coordinate will be relative to.
	 * @return The local x coordinate.
	 */
	public int getLocalX(Location l) {
		return x - 8 * l.getRegionX();
	}

	/**
	 * Gets the local y coordinate relative to a specific region.
	 * 
	 * @param l
	 *            The region the coordinate will be relative to.
	 * @return The local y coordinate.
	 */
	public int getLocalY(Location l) {
		return y - 8 * l.getRegionY();
	}

	/**
	 * Gets the region x coordinate.
	 * 
	 * @return The region x coordinate.
	 */
	public int getRegionX() {
		return (x >> 3) - 6;
	}

	/**
	 * Gets the region y coordinate.
	 * 
	 * @return The region y coordinate.
	 */
	public int getRegionY() {
		return (y >> 3) - 6;
	}

	/**
	 * Checks if a coordinate is within range of another.
	 * 
	 * @return <code>true</code> if the location is in range, <code>false</code>
	 *         if not.
	 */
	public boolean isWithinDistance(Entity attacker, Entity victim, int distance) {
		return distanceToPoint(victim.getLocation()) <= distance;
	}

	/**
	 * Gets the distance to a location.
	 * 
	 * @param other
	 *            The location.
	 * @return The distance from the other location.
	 */
	public int distanceToPoint(Location other) {
		int absX = x;
		int absY = y;
		int pointX = other.getX();
		int pointY = other.getY();
		return (int) Math.sqrt(Math.pow(absX - pointX, 2) + Math.pow(absY - pointY, 2));
	}

	/**
	 * Checks if this location is within range of another.
	 * 
	 * @param other
	 *            The other location.
	 * @return <code>true</code> if the location is in range, <code>false</code>
	 *         if not.
	 */
	public boolean isWithinDistance(Location other) {
		if (z != other.z) {
			return false;
		}
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
	}

	/**
	 * Checks if this location is within interaction range of another.
	 * 
	 * @param other
	 *            The other location.
	 * @return <code>true</code> if the location is in range, <code>false</code>
	 *         if not.
	 */
	public boolean isWithinInteractionDistance(Location other) {
		if (z != other.z) {
			return false;
		}
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 2 && deltaX >= -3 && deltaY <= 2 && deltaY >= -3;
	}

	@Override
	public int hashCode() {
		return z << 30 | x << 15 | y;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Location)) {
			return false;
		}
		Location loc = (Location) other;
		return loc.x == x && loc.y == y && loc.z == z;
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "," + z + "]";
	}

	/**
	 * Creates a new location based on this location.
	 * 
	 * @param diffX
	 *            X difference.
	 * @param diffY
	 *            Y difference.
	 * @param diffZ
	 *            Z difference.
	 * @return The new location.
	 */
	public Location transform(int diffX, int diffY, int diffZ) {
		return Location.create(x + diffX, y + diffY, z + diffZ);
	}

	/**
	 * Gets the closest spot from a list of locations.
	 * 
	 * @param steps
	 *            The list of steps.
	 * @param location
	 *            The location we want to be close to.
	 * @return The closest location.
	 */
	public static Location[] getClosestSpot(Location target, Location[][] steps) {
		Location[] closestStep = new Location[steps.length + 1];
		int index = 0;
		for (int i2 = 0; i2 < steps.length; i2++) {
			for (int i = 0; i < steps[i2].length; i++) {
				if (closestStep[index] == null || (getDistance(closestStep[index], target) > getDistance(steps[i2][i], target))) {
					closestStep[index] = steps[i2][i];
				}
			}
			index++;
		}
		return closestStep;
	}

	public static double getDistance(Location p, Location p2) {
		return Math.sqrt((p2.getX() - p.getX()) * (p2.getX() - p.getX()) + (p2.getY() - p.getY()) * (p2.getY() - p.getY()));
	}
	
	/**
	 * Gets the distance between this position and another position. Only X and
	 * Y are considered (i.e. 2 dimensions).
	 *
	 * @param other The other position.
	 * @return The distance.
	 */
	public int getDistance(Location other) {
		int deltaX = x - other.x;
		int deltaY = y - other.y;
		return (int) Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY));
	}

	/**
	 * Gets the closest spot from a list of locations.
	 * 
	 * @param steps
	 *            The list of steps.
	 * @param location
	 *            The location we want to be close to.
	 * @return The closest location.
	 */
	public Location getClosestSpot(Location target, Location[] steps) {
		Location closestStep = null;
		for (Location p : steps) {
			if (p == null) {
				break;
			}
			if (closestStep == null || (getDistance(closestStep, target) > getDistance(p, target))) {
				closestStep = p;
			}
		}
		return closestStep;
	}

	/**
	 * Checks if this location is within interaction range of another.
	 * 
	 * @param other
	 *            The other location.
	 * @return <code>true</code> if the location is in range, <code>false</code>
	 *         if not.
	 */
	public int distance(Location other) {
		int deltaX = other.x - x, deltaY = other.y - y;
		double dis = Math.sqrt(Math.pow(deltaX, 2D) + Math.pow(deltaY, 2D));
		if (dis > 1.0 && dis < 2)
			return 2;
		return (int) dis;
	}

	/**
	 * Checks if a specific location is within a specific radius.
	 * 
	 * @param rad
	 *            The radius.
	 * @return True if we're within distance/range, false if not.
	 */
	public boolean withinRange(Location p, int rad) {
		if (p == null) {
			return false;
		}
		int dX = Math.abs(x - p.x);
		int dY = Math.abs(y - p.y);
		return dX <= rad && dY <= rad;
	}

	/**
	 * Checks if a specific location is within a specific radius.
	 * 
	 * @param rad
	 *            The radius.
	 * @return True if we're within distance/range, false if not.
	 */
	public boolean withinRange(int x1, int y1, int rad) {
		int dX = Math.abs(x - x1);
		int dY = Math.abs(y - y1);
		return dX <= rad && dY <= rad;
	}

	/**
	 * Checks if the entity is in a multi area
	 * 
	 * @return
	 */
	public boolean inMulti() {
		if (((x >= 3136) && (x <= 3327) && (y >= 3519) && (y <= 3607)) || ((x >= 3190) && (x <= 3327) && (y >= 3648) && (y <= 3839)) || ((x >= 3200) && (x <= 3390) && (y >= 3840) && (y <= 3967)) || ((x >= 2992) && (x <= 3007) && (y >= 3912) && (y <= 3967)) || ((x >= 2946) && (x <= 2959) && (y >= 3816) && (y <= 3831)) || ((x >= 3008) && (x <= 3199) && (y >= 3856) && (y <= 3903)) || ((x >= 3008) && (x <= 3071) && (y >= 3600) && (y <= 3711)) || ((x >= 3072) && (x <= 3327) && (y >= 3608) && (y <= 3647)) || ((x >= 2624) && (x <= 2690) && (y >= 2550) && (y <= 2619)) || ((x >= 2371) && (x <= 2422) && (y >= 5062) && (y <= 5117)) || ((x >= 2896) && (x <= 2927) && (y >= 3595) && (y <= 3630)) || ((x >= 2892) && (x <= 2932) && (y >= 4435) && (y <= 4464)) || ((x >= 3131) && (x <= 3273) && (y >= 9520) && (y <= 9602)) || ((x >= 2850) && (x <= 2878) && (y >= 9825) && (y <= 9860)) || ((x >= 3460) && (x <= 3517) && (y >= 9475) && (y <= 9532)) || ((x >= 2971) && (x <= 2986) && (y >= 9512) && (y <= 9523)) || ((x >= 2256) && (x <= 2287) && (y >= 4680) && (y <= 4711)) || ((x >= 2538) && (x <= 2590) && (y >= 4932) && (y <= 4987)) || ((x >= 2690) && (x <= 2750) && (y >= 9410) && (y <= 9470)) || ((x >= 2501) && (x <= 2545) && (y >= 4623) && (y <= 4666)) || ((x >= 2820) && (x <= 2889) && (y >= 9484) && (y <= 9549)) || ((x >= 2515) && (x <= 2785) && (y >= 4770) && (y <= 4784)) || ((x >= 2411) && (x <= 2426) && (y >= 4673) && (y <= 4687))) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if an entity is in wild
	 * 
	 * @return
	 */
	public boolean inWild() {
		if (((x > 2941) && (x < 3392) && (y > 3519) && (y < 3966)) || ((x > 2941) && (x < 3392) && (y > 9918) && (y < 10366)) || ((x > 3652) && (x < 3666) && (y > 3529) && (y < 3545))) {
			return true;
		}

		return false;
	}

	/**
	 * Are the coordinates inside of the tile.
	 * 
	 * @param blockX
	 *            the tile src x
	 * @param blockY
	 *            the tile src y
	 * @param size
	 *            the tile size
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @return if the coordainte are inside of the tile.
	 */
	public static boolean isWithinBlock(int blockX, int blockY, int size, int x, int y) {
		return (x - blockX < size) && (x - blockX > -1) && (y - blockY < size) && (y - blockY > -1);
	}

	/**
	 * Checks if this position is within distance of another position.
	 * 
	 * @param position
	 *            the position to check the distance for.
	 * @param distance
	 *            the distance to check.
	 * @return true if this position is within the distance of another position.
	 */
	public boolean withinDistance(Location position, int distance) {
		if (this.getZ() != position.getZ())
			return false;

		return Math.abs(position.getX() - this.getX()) <= distance && Math.abs(position.getY() - this.getY()) <= distance;
	}
}