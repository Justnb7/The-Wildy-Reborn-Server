package com.venenatis.game.location;

import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.pathfinder.Directions;
import com.venenatis.game.world.pathfinder.ProjectilePathFinder.Direction;
import com.venenatis.game.world.pathfinder.clipmap.Region;
import com.venenatis.game.world.pathfinder.impl.PrimitivePathFinder;

import java.util.ArrayList;
import java.util.List;

/**
 * A location point on the map.
 * 
 * @author blakeman8192
 * @author lare96
 */
public class Location {

	/** The x coordinate. */
	private int x;

	/** The y coordinate. */
	private int y;

	/** The z coordinate. */
	private int z;

	/**
	 * Creates a new location with the specified coordinates. The Z coordinate
	 * is set to 0.
	 * 
	 * @param x
	 *            the X coordinate.
	 * @param y
	 *            the Y coordinate.
	 */
	public Location(int x, int y) {
		this(x, y, 0);
	}

	/**
	 * Creates a new location with the specified coordinates.
	 * 
	 * @param x
	 *            the X coordinate.
	 * @param y
	 *            the Y coordinate.
	 * @param z
	 *            the Z coordinate.
	 */
	public Location(int x, int y, int z) {
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}

	/**
	 * Creates a new location with default values (0) for the coordinates.
	 */
	public Location() {

	}
	
	/**
	 * Creates a location.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @return The location.
	 */
	public static Location create(int x, int y) {
		return new Location(x, y, 0);
	}
	
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
	 * Sets this location as the other location. <b>Please use this method
	 * instead of entity.setLocation(other)</b> because of reference conflicts
	 * (if the other location gets modified, so will the entity's).
	 * 
	 * @param other
	 *            the other location.
	 */
	public Location setAs(Location other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		return this;
	}

	/**
	 * Moves the location.
	 * 
	 * @param amountX
	 *            the amount of X coordinates.
	 * @param amountY
	 *            the amount of Y coordinates.
	 * @return this location.
	 */
	public Location move(int amountX, int amountY) {
		setX(getX() + amountX);
		setY(getY() + amountY);
		return this;
	}

	/**
	 * Moves the location.
	 * 
	 * @param amountX
	 *            the amount of X coordinates.
	 * @param amountY
	 *            the amount of Y coordinates.
	 * @param amountZ
	 *            the amount of Z coordinates.
	 * @return this location.
	 */
	public Location move(int amountX, int amountY, int amountZ) {
		setX(getX() + amountX);
		setY(getY() + amountY);
		setZ(getZ() + amountZ);
		return this;
	}

	@Override
	public String toString() {
		return "Location(" + x + ", " + y + ", " + z + ")";
	}

	@Override
	public boolean equals(java.lang.Object other) {
		if (other instanceof Location) {
			Location p = (Location) other;
			return x == p.x && y == p.y && z == p.z;
		}
		return false;
	}

	@Override
	public Location clone() {
		return new Location(x, y, z);
	}

	/**
	 * Sets the X coordinate.
	 * 
	 * @param x
	 *            the X coordinate.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets the X coordinate.
	 * 
	 * @return the X coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the Y coordinate.
	 * 
	 * @param y
	 *            the Y coordinate.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Gets the Y coordinate.
	 * 
	 * @return the Y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the Z coordinate.
	 * 
	 * @param z
	 *            the Z coordinate.
	 */
	public void setZ(int z) {
		this.z = z;
	}

	/**
	 * Gets the Z coordinate.
	 * 
	 * @return the Z coordinate.
	 */
	public int getZ() {
		return z;
	}
	/**
	 * Gets the X coordinate of the region containing this location.
	 * 
	 * @return the region X coordinate.
	 */
	public int getRegionX() {
		return (x >> 3) - 6;
	}

	/**
	 * Gets the Y coordinate of the region containing this location.
	 * 
	 * @return the region Y coordinate.
	 */
	public int getRegionY() {
		return (y >> 3) - 6;
	}

	/**
	 * Gets the local X coordinate relative to the base location.
	 * 
	 * @param base
	 *            the base location.
	 * @return the local X coordinate.
	 */
	public int getLocalX(Location base) {
		return x - 8 * base.getRegionX();
	}

	/**
	 * Gets the local Y coordinate relative to the base location.
	 * 
	 * @param base
	 *            the base location.
	 * @return the local Y coordinate.
	 */
	public int getLocalY(Location base) {
		return y - 8 * base.getRegionY();
	}

	/**
	 * Gets the local X coordinate relative to this location.
	 * 
	 * @return the local X coordinate.
	 */
	public int getLocalX() {
		return getLocalX(this);
	}

	/**
	 * Gets the local Y coordinate relative to this location.
	 * 
	 * @return the local Y coordinate.
	 */
	public int getLocalY() {
		return getLocalY(this);
	}


	// because hyperion path finder for some reason requires these different methods!! diff to 317
	public int localX_hyperion() {
		return localX_formatted_hyperion(this);
	}
	public int localX_formatted_hyperion(Location base) {
		return x - 8 * (base.regionX_hyperion()-6);
	}
	public int localY_hyperion() {
		return localY_formatted_hyperion(this);
	}
	public int localY_formatted_hyperion(Location base) {
		return y - 8 * (base.regionY_hyperion()-6);
	}
	public int regionX_hyperion() {
		return (x >> 3);
	}
	public int regionY_hyperion() {
		return (y >> 3);
	}

	/**
	 * Gets the X map region chunk relative to this location.
	 * 
	 * @return the X region chunk.
	 */
	public int getChunkX() {
		return (x >> 6);
	}

	/**
	 * Gets the Y map region chunk relative to this location.
	 * 
	 * @return the Y region chunk.
	 */
	public int getChunkY() {
		return (y >> 6);
	}

	/**
	 * Gets the region id relative to this location.
	 * 
	 * @return the region id.
	 */
	public int getRegionId() {
		return ((getChunkX() << 8) + getChunkY());
	}

	/**
	 * Checks if this location is viewable from the other location.
	 * 
	 * @param other
	 *            the other location.
	 * @return true if it is viewable, false otherwise.
	 */
	public boolean isViewableFrom(Location other) {
		if (this.getZ() != other.getZ())
			return false;

		Location p = Utility.delta(this, other);
		return p.x <= 14 && p.x >= -15 && p.y <= 14 && p.y >= -15;
	}
	
	/**
	 * Checks if a coordinate is within range of another.
	 *
	 * @return <code>true</code> if the location is in range,
	 * <code>false</code> if not.
	 */
	public int distanceToEntity(Entity attacker, Entity victim) {
		if (attacker.getWidth() == 1 && attacker.yLength() == 1 && victim.getWidth() == 1
				&& victim.yLength() == 1) {
			return distanceToPoint(victim.getLocation());
		}
		int lowestDistance = 100;
		List<Location> myTiles = entityTiles(attacker);
		List<Location> theirTiles = entityTiles(victim);
		for (Location myTile : myTiles) {
			for (Location theirTile : theirTiles) {
				int dist = myTile.distanceToPoint(theirTile);
				if (dist <= lowestDistance) {
					lowestDistance = dist;
				}
			}
		}
		return lowestDistance;
	}
	
	/**
	 * Checks if a coordinate is within range of another.
	 *
	 * @return <code>true</code> if the location is in range,
	 * <code>false</code> if not.
	 */
	public boolean isWithinDistance(Entity attacker, Entity victim, int distance) {
		if (attacker.getWidth() == 1 && attacker.yLength() == 1 &&
				victim.getWidth() == 1 && victim.yLength() == 1 && distance == 1) {
			return distance(victim.getLocation()) <= distance;
		}
		List<Location> myTiles = entityTiles(attacker);
		List<Location> theirTiles = entityTiles(victim);
		for (Location myTile : myTiles) {
			for (Location theirTile : theirTiles) {
				if (myTile.isWithinDistance(theirTile, distance)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Checks if a coordinate is within range of another.
	 *
	 * @return <code>true</code> if the location is in range,
	 * <code>false</code> if not.
	 */
	public boolean isWithinDistance(Location location, int distance) {
		int objectX = location.getX();
		int objectY = location.getY();
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((objectX + i) == x && ((objectY + j) == y || (objectY - j) == y || objectY == y)) {
					return true;
				} else if ((objectX - i) == x && ((objectY + j) == y || (objectY - j) == y || objectY == y)) {
					return true;
				} else if (objectX == x && ((objectY + j) == y || (objectY - j) == y || objectY == y)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * The list of tiles this entity occupies.
	 *
	 * @param entity The entity.
	 * @return The list of tiles this entity occupies.
	 */
	public List<Location> entityTiles(Entity entity) {
		List<Location> myTiles = new ArrayList<Location>();
		myTiles.add(entity.getLocation());
		if (entity.getWidth() > 1) {
			for (int i = 1; i < entity.getWidth(); i++) {
				myTiles.add(Location.create(entity.getLocation().getX() + i,
						entity.getLocation().getY(), entity.getLocation().getZ()));
			}
		}
		if (entity.yLength() > 1) {
			for (int i = 1; i < entity.yLength(); i++) {
				myTiles.add(Location.create(entity.getLocation().getX(),
						entity.getLocation().getY() + i, entity.getLocation().getZ()));
			}
		}
		int myHighestVal = (entity.getWidth() > entity.yLength() ? entity.getWidth() : entity.yLength());
		if (myHighestVal > 1) {
			for (int i = 1; i < myHighestVal; i++) {
				myTiles.add(Location.create(entity.getLocation().getX() + i,
						entity.getLocation().getY() + i, entity.getLocation().getZ()));
			}
		}
		return myTiles;
	}

	/**
	 * Checks if this location is within distance of another location.
	 * 
	 * @param location
	 *            the location to check the distance for.
	 * @param distance
	 *            the distance to check.
	 * @return true if this location is within the distance of another location.
	 */
	public boolean withinDistance(Location location, int distance) {
		if (this.getZ() != location.getZ())
			return false;

		return Math.abs(location.getX() - this.getX()) <= distance && Math.abs(location.getY() - this.getY()) <= distance;
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
		//was originally the below one yh its still iffy erm doesnbt matter that much
		// rooftops is odd though can u sho wme again
		
		// this is just inaccurate alltogether i cant remember the proper solution, probably should be checking if NESW instead of distance
		
		int deltaX = other.x - x, deltaY = other.y - y;
          return deltaX <= 2 && deltaX >= -3 && deltaY <= 2 && deltaY >= -3;
          //When i was using the below it works but then it opens banks like 3 tiles away
          //if you know what i mean
		//return deltaX <= 5 && deltaX >= -5 && deltaY <= 5 && deltaY >= -5;
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
	
	public static double getDistance(Location p, Location p2) {
		return Math.sqrt((p2.getX() - p.getX()) * (p2.getX() - p.getX()) + (p2.getY() - p.getY()) * (p2.getY() - p.getY()));
	}
	
	/**
	 * Gets the distance between this location and another location. Only X and
	 * Y are considered (i.e. 2 dimensions).
	 *
	 * @param other The other location.
	 * @return The distance.
	 */
	public int getDistance(Location other) {
		int deltaX = x - other.x;
		int deltaY = y - other.y;
		return (int) Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY));
	}


	public static boolean canMove(Entity mob, Directions.NormalDirection dir, int size, boolean npcCheck) {
		return PrimitivePathFinder.canMove(mob, mob.getLocation(), dir, size, npcCheck);
	}
	public boolean canMove(Directions.NormalDirection dir, int size, boolean checkType) {
		/*final int npcHeight = z;
		boolean checkingNPCs = checkType;*/
		if (dir == null) {
			return true;
		}
		switch (dir) {
			case WEST:
				for (int k = getY(); k < getY() + size; k++) {
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(getX() - 1, k, npcHeight, 1))
					return false;*/
					if ((Region.getClippingMask(getX() - 1, k, getZ()) & 0x1280108) != 0)
						return false;
				}
				break;
			case EAST:
				for (int k = getY(); k < getY() + size; k++) {
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(getX() + size, k, npcHeight, 1))
					return false;*/
					if ((Region.getClippingMask(getX() + size, k, getZ()) & 0x1280180) != 0)
						return false;
				}
				break;
			case SOUTH:
				for (int i = getX(); i < getX() + size; i++) {
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(i, y - 1, npcHeight, 1))
					return false;*/
					if ((Region.getClippingMask(i, getY() - 1, getZ()) & 0x1280102) != 0)
						return false;
				}
				break;
			case NORTH:
				for (int i = getX(); i < getX() + size; i++) {
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(i, getY() + size, npcHeight, 1))
					return false;*/
					if ((Region.getClippingMask(i, getY() + size, getZ()) & 0x1280120) != 0)
						return false;
				}
				break;
			case SOUTH_WEST:
				for (int i = getX(); i < getX() + size; i++) {
					int s = Region.getClippingMask(i, getY() - 1, getZ());
					int w = Region.getClippingMask(i - 1, getY(), getZ());
					int sw = Region.getClippingMask(i - 1, getY() - 1, getZ());
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(i - 1, getY() - 1, npcHeight, 1))
					return false;*/
					if ((sw & 0x128010e) != 0 || (s & 0x1280102) != 0 || (w & 0x1280108) != 0)
						return false;
				}
				for (int k = getY(); k < getY() + size; k++) {
					int s = Region.getClippingMask(getX(), k - 1, getZ());
					int w = Region.getClippingMask(getX() - 1, k, getZ());
					int sw = Region.getClippingMask(getX() - 1, k - 1, getZ());
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(getX() - 1, k - 1, npcHeight, 1))
					return false;*/
					if ((sw & 0x128010e) != 0 || (s & 0x1280102) != 0 || (w & 0x1280108) != 0)
						return false;
				}
				break;
			case SOUTH_EAST:
				for (int i = getX(); i < getX() + size; i++) {
					int s = Region.getClippingMask(i, getY() - 1, getZ());
					int e = Region.getClippingMask(i + 1, getY(), getZ());
					int se = Region.getClippingMask(i + 1, getY() - 1, getZ());
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(i + 1, getY() - 1, npcHeight, 1))
					return false;*/
					if ((se & 0x1280183) != 0 || (s & 0x1280102) != 0 || (e & 0x1280180) != 0)
						return false;
				}
				for (int k = getY(); k < getY() + size; k++) {
					int s = Region.getClippingMask(getX() + size - 1, k - 1, getZ());
					int e = Region.getClippingMask(getX() + size, k, getZ());
					int se = Region.getClippingMask(getX() + size, k - 1, getZ());
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(getX() + 1, k - 1, npcHeight, 1))
					return false;*/
					if ((se & 0x1280183) != 0 || (s & 0x1280102) != 0 || (e & 0x1280180) != 0)
						return false;
				}
				break;
			case NORTH_WEST:
				for (int i = getX(); i < getX() + size; i++) {
					int n = Region.getClippingMask(i, getY() + size, getZ());
					int w = Region.getClippingMask(i - 1, getY() + size - 1, getZ());
					int nw = Region.getClippingMask(i - 1, getY() + size, getZ());
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(i - 1, getY() + size, npcHeight, 1))
					return false;*/
					if ((nw & 0x1280138) != 0 || (n & 0x1280102) != 0 || (w & 0x1280108) != 0)
						return false;
				}
				for (int k = getY(); k < getY() + size; k++) {
					int n = Region.getClippingMask(getX(), getY(), getZ());
					int w = Region.getClippingMask(getX() - 1, getY(), getZ());
					int nw = Region.getClippingMask(getX() - 1, getY() + 1, getZ());
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(getX() - 1, getY() + 1, npcHeight, 1))
					return false;*/
					if ((nw & 0x1280138) != 0 || (n & 0x1280102) != 0 || (w & 0x1280108) != 0)
						return false;
				}
				break;
			case NORTH_EAST:
				for (int i = getX(); i < getX() + size; i++) {
					int n = Region.getClippingMask(i, getY() + size, getZ());
					int e = Region.getClippingMask(i + 1, getY() + size - 1, getZ());
					int ne = Region.getClippingMask(i + 1, getY() + size, getZ());
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(i + 1, getY() + size, npcHeight, 1))
					return false;*/
					if ((ne & 0x12801e0) != 0 || (n & 0x1280120) != 0 || (e & 0x1280180) != 0)
						return false;
				}
				for (int k = getY(); k < getY() + size; k++) {
					int n = Region.getClippingMask(getX() + size - 1, k + 1, getZ());
					int e = Region.getClippingMask(getX() + size, k, getZ());
					int ne = Region.getClippingMask(getX() + size, k + 1, getZ());
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(getX() + size, k + 1, npcHeight, 1))
					return false;*/
					if ((ne & 0x12801e0) != 0 || (n & 0x1280120) != 0 || (e & 0x1280180) != 0)
						return false;
				}
				break;
		}
		return true;
	}
	public static boolean isDiagonal(Entity source, Entity target) {
		Location l = source.getLocation();
		Location l2 = target.getLocation();
		return l.isDiagonal(l2);
	}

	public boolean isDiagonal(Location location) {
		if (getSouthEast().equals(location)) {
			return true;
		}
		if (getSouthWest().equals(location)) {
			return true;
		}
		if (getNorthEast().equals(location)) {
			return true;
		}
		if (getNorthWest().equals(location)) {
			return true;
		}
		return false;
	}

	public boolean isNextTo(Location location) {
		if (this.getSouth().equals(location)) {
			return true;
		}
		if (this.getNorth().equals(location)) {
			return true;
		}
		if (this.getWest().equals(location)) {
			return true;
		}
		if (this.getEast().equals(location)) {
			return true;
		}
		return false;
	}

	public Location getNorth() {
		return transform(0, 1, 0);
	}

	public Location getSouth() {
		return transform(0, -1, 0);
	}

	public Location getEast() {
		return transform(-1, 0, 0);
	}

	public Location getNorthEast() {
		return transform(1, 1, 0);
	}

	public Location getSouthEast() {
		return transform(1, -1, 0);
	}

	public Location getWest() {
		return transform(1, 0, 0);
	}

	public Location getNorthWest() {
		return transform(-1, 1, 0);
	}

	public Location getSouthWest() {
		return transform(-1, -1, 0);
	}

	public boolean right(Location t) {
		return getX() > t.getX();
	}

	public boolean left(Location t) {
		return getX() < t.getX();
	}

	public boolean above(Location t) {
		return getY() > t.getY();
	}

	public boolean under(Location t) {
		return getY() < t.getY();
	}

	public Location transform(int diffX, int diffY, int diffZ) {
		return create(getX() + diffX, getY() + diffY, getZ() + diffZ);
	}

	public Location transform(int diffX, int diffY) {
		return create(getX() + diffX, getY() + diffY, z);
	}

	@Override
	public int hashCode() {
		return getZ() << 30 | getX() << 15 | getY();
	}

	public boolean matches(int x, int y) {
		return this.getX() == x && this.getY() == y;
	}

	public boolean matches(Location other) {
		return this.getX() == other.getX() && this.getY() == other.getY() && this.getZ() == other.getZ();
	}

	
	public Directions.NormalDirection direction(Location next) {
		return Directions.directionFor(this, next);
	}

	public static boolean standingOn(Entity mob, Entity other) {
		int firstSize = mob.size();
		int secondSize = other.size();
		int x = mob.getLocation().getX();
		int y = mob.getLocation().getY();
		int vx = other.getLocation().getX();
		int vy = other.getLocation().getY();
		for (int i = x; i < x + firstSize; i++) {
			for (int j = y; j < y + firstSize; j++) {
				if (i >= vx && i < secondSize + vx && j >= vy && j < secondSize + vy) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isWithinDistance(Location other) {
		if(getZ() != other.getZ()) {
			return false;
		}
		int deltaX = other.getX() - getX(), deltaY = other.getY() - getY();
		return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
	}

	public boolean isWithinDistance(int width, int height, Location otherLocation, int otherWidth, int otherHeight, int distance) {
		Location myClosestTile = this.closestTileOf(otherLocation, width, height);
		Location theirClosestTile = otherLocation.closestTileOf(this, otherWidth, otherHeight);

		return myClosestTile.distanceToPoint(theirClosestTile) <= distance;
	}

	public Location closestTileOf(Location from, int width, int height) {
		if(width < 2 && height < 2) {
			return this;
		}
		Location location = null;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				Location loc = create(getX() + x, this.getY() + y, this.getZ());
				if(location == null || loc.distanceToPoint(from) < location.distanceToPoint(from)) {
					location = loc;
				}
			}
		}
		return location;
	}

	public int distanceToPoint(Location other) {
		int pointX = other.getX();
		int pointY = other.getY();
		return (int) Math.sqrt(Math.pow(getX() - pointX, 2) + Math.pow(getY() - pointY, 2));
	}

	public Location add(Direction direction) {
		return new Location(x + direction.getStepX(), y + direction.getStepY(), z);
	}

	public Location getDelta(Location location) {
		return transform(getX() - location.getX(), getY() - location.getY(), 0);
	}

	/**
	 * Gets the Euclidean (straight-line) distance between two {@link Location}
	 * s.
	 *
	 * @return The distance in tiles between the two locations.
	 */
	public static int getManhattanDistance(Location first, Location second) {
		final int dx = Math.abs(second.getX() - first.getX());
		final int dy = Math.abs(second.getY() - first.getY());
		return dx + dy;
	}

	/**
	 * The player is in the king black dragon area
	 * 
	 * @return
	 */
	public boolean inBossEvent() {
		return x >= 3108 && x <= 3118 && y >= 3955 && y <= 3962 ||
			   x >= 3030 && x <= 3051 && y >= 3529 && y <= 3546 ||
			   x >= 3348 && x <= 3385 && y >= 3634 && y <= 3663;
	}

	/**
	 * Gets the closest unblocked tile from a position, or if there is none it just returns the original position.
	 */
	public Location closestFreeTileOrSelf(Location from, int width, int height) {
		for (int x = -width; x <= width; x++) {
			for (int y = -height; y <= height; y++) {
				Location loc = Location.create(this.x + x, this.y + y, this.z);
				if (!Region.isPassable(loc.getX(), loc.getY(), loc.getZ())) {
					continue;
				}
				return loc;
			}
		}
		return from;
	}
}