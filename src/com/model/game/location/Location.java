package com.model.game.location;

import com.model.game.character.Entity;
import com.model.utility.Utility;
import clipmap.Region;
import clipmap.Tile;
import hyperion.Directions;
import hyperion.impl.PrimitivePathFinder;

import java.util.ArrayList;
import java.util.List;

/**
 * A position point on the map.
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
	 * Creates a new Position with the specified coordinates. The Z coordinate
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
	 * Creates a new Position with the specified coordinates.
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
	 * Creates a new position with default values (0) for the coordinates.
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
	 * Sets this position as the other position. <b>Please use this method
	 * instead of entity.setPosition(other)</b> because of reference conflicts
	 * (if the other position gets modified, so will the entity's).
	 * 
	 * @param other
	 *            the other position.
	 */
	public Location setAs(Location other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		return this;
	}

	/**
	 * Moves the position.
	 * 
	 * @param amountX
	 *            the amount of X coordinates.
	 * @param amountY
	 *            the amount of Y coordinates.
	 * @return this position.
	 */
	public Location move(int amountX, int amountY) {
		setX(getX() + amountX);
		setY(getY() + amountY);
		return this;
	}

	/**
	 * Moves the position.
	 * 
	 * @param amountX
	 *            the amount of X coordinates.
	 * @param amountY
	 *            the amount of Y coordinates.
	 * @param amountZ
	 *            the amount of Z coordinates.
	 * @return this position.
	 */
	public Location move(int amountX, int amountY, int amountZ) {
		setX(getX() + amountX);
		setY(getY() + amountY);
		setZ(getZ() + amountZ);
		return this;
	}

	@Override
	public String toString() {
		return "Position(" + x + ", " + y + ", " + z + ")";
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

	static boolean osrsclipping = true;
	/**
	 * Gets the X coordinate of the region containing this Position.
	 * 
	 * @return the region X coordinate.
	 */
	public int getRegionX() {
		return osrsclipping ? (x>>3) : ((x >> 3) - 6);
	}

	/**
	 * Gets the Y coordinate of the region containing this Position.
	 * 
	 * @return the region Y coordinate.
	 */
	public int getRegionY() {
		return osrsclipping ? (y>>3) : ((y >> 3) - 6);
	}

	/**
	 * Gets the local X coordinate relative to the base Position.
	 * 
	 * @param base
	 *            the base Position.
	 * @return the local X coordinate.
	 */
	public int getLocalX(Location base) {
		return osrsclipping ? (x - 8 * (base.getRegionX()-6)) : (x - 8 * base.getRegionX());
	}

	/**
	 * Gets the local Y coordinate relative to the base Position.
	 * 
	 * @param base
	 *            the base Position.
	 * @return the local Y coordinate.
	 */
	public int getLocalY(Location base) {
		return osrsclipping ? (y - 8 * (base.getRegionY()-6)) : (y - 8 * base.getRegionY());
	}

	/**
	 * Gets the local X coordinate relative to this Position.
	 * 
	 * @return the local X coordinate.
	 */
	public int getLocalX() {
		return getLocalX(this);
	}

	/**
	 * Gets the local Y coordinate relative to this Position.
	 * 
	 * @return the local Y coordinate.
	 */
	public int getLocalY() {
		return getLocalY(this);
	}

	/**
	 * Gets the X map region chunk relative to this position.
	 * 
	 * @return the X region chunk.
	 */
	public int getChunkX() {
		return (x >> 6);
	}

	/**
	 * Gets the Y map region chunk relative to this position.
	 * 
	 * @return the Y region chunk.
	 */
	public int getChunkY() {
		return (y >> 6);
	}

	/**
	 * Gets the region id relative to this position.
	 * 
	 * @return the region id.
	 */
	public int getRegionId() {
		return ((getChunkX() << 8) + getChunkY());
	}

	/**
	 * Checks if this position is viewable from the other position.
	 * 
	 * @param other
	 *            the other position.
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
		if (attacker.getWidth() == 1 && attacker.getHeight() == 1 && victim.getWidth() == 1
				&& victim.getHeight() == 1) {
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
		if (attacker.getWidth() == 1 && attacker.getHeight() == 1 &&
				victim.getWidth() == 1 && victim.getHeight() == 1 && distance == 1) {
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
		if (entity.getHeight() > 1) {
			for (int i = 1; i < entity.getHeight(); i++) {
				myTiles.add(Location.create(entity.getLocation().getX(),
						entity.getLocation().getY() + i, entity.getLocation().getZ()));
			}
		}
		int myHighestVal = (entity.getWidth() > entity.getHeight() ? entity.getWidth() : entity.getHeight());
		if (myHighestVal > 1) {
			for (int i = 1; i < myHighestVal; i++) {
				myTiles.add(Location.create(entity.getLocation().getX() + i,
						entity.getLocation().getY() + i, entity.getLocation().getZ()));
			}
		}
		return myTiles;
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
	
	/**
	 * Checks if this location is within range of another.
	 * @param other The other location.
	 * @return <code>true</code> if the location is in range,
	 * <code>false</code> if not.
	 */
	public boolean isWithinDistance(Location other) {
		if(z != other.z) {
			return false;
		}
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
	}
	
	/**
	 * Gets the distance to a location.
	 *
	 * @param other The location.
	 * @return The distance from the other location.
	 */
	public int distanceToPoint(Location other) {
		int absX = x;
		int absY = y;
		int pointX = other.getX();
		int pointY = other.getY();
		return (int) Math.sqrt(Math.pow(absX - pointX, 2) + Math.pow(absY - pointY, 2));
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


	public static boolean canMove(Entity mob, Directions.NormalDirection dir, int size, boolean npcCheck) {
		return PrimitivePathFinder.canMove(mob, mob.getPosition(), dir, size, npcCheck);
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
		Tile l = source.getPosition();
		Tile l2 = target.getPosition();
		if (l.getSouthEast().equals(l2)) {
			return true;
		}
		if (l.getSouthWest().equals(l2)) {
			return true;
		}
		if (l.getNorthEast().equals(l2)) {
			return true;
		}
		if (l.getNorthWest().equals(l2)) {
			return true;
		}
		return false;
	}

	public Tile getNorth() {
		return transform(0, 1, 0);
	}

	public Tile getSouth() {
		return transform(0, -1, 0);
	}

	public Tile getEast() {
		return transform(-1, 0, 0);
	}

	public Tile getNorthEast() {
		return transform(1, 1, 0);
	}

	public Tile getSouthEast() {
		return transform(1, -1, 0);
	}

	public Tile getWest() {
		return transform(1, 0, 0);
	}

	public Tile getNorthWest() {
		return transform(-1, 1, 0);
	}

	public Tile getSouthWest() {
		return transform(-1, -1, 0);
	}

	public boolean right(Tile t) {
		return getX() > t.getX();
	}

	public boolean left(Tile t) {
		return getX() < t.getX();
	}

	public boolean above(Tile t) {
		return getY() > t.getY();
	}

	public boolean under(Tile t) {
		return getY() < t.getY();
	}

	public Tile transform(int diffX, int diffY, int diffZ) {
		return Tile.create(getX() + diffX, getY() + diffY, getZ() + diffZ);
	}

	public Tile transform(int diffX, int diffY) {
		return Tile.create(getZ(), getX() + diffX, getY() + diffY);
	}

	@Override
	public int hashCode() {
		return getZ() << 30 | getX() << 15 | getY();
	}

	public boolean matches(int x, int y) {
		return this.getX() == x && this.getY() == y;
	}

	public boolean matches(Tile other) {
		return this.getX() == other.getX() && this.getY() == other.getY() && this.getZ() == other.getZ();
	}

	public boolean isNextTo(Tile other) {
		if(getZ() != other.getZ()) {
			return false;
		}
		/*int deltagetX() = Math.abs(other.getX() - x), deltaY = Math.abs(other.y - y);
		return deltagetX() <= 1 && deltaY <= 1;*/
		return (getX() == other.getX() && getY() != other.getY()
				|| getX() != other.getX() && getY() == other.getY()
				|| getX() == other.getX() && getY() == other.getY());
	}
	public Directions.NormalDirection direction(Tile next) {
		return Directions.directionFor(Tile.create(this), next);
	}

	public static boolean standingOn(Entity mob, Entity other) {
		int firstSize = mob.size();
		int secondSize = other.size();
		int x = mob.getPosition().getX();
		int y = mob.getPosition().getY();
		int vx = other.getPosition().getX();
		int vy = other.getPosition().getY();
		for (int i = x; i < x + firstSize; i++) {
			for (int j = y; j < y + firstSize; j++) {
				if (i >= vx && i < secondSize + vx && j >= vy && j < secondSize + vy) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isWithinDistance(Tile other) {
		if(getZ() != other.getZ()) {
			return false;
		}
		int deltaX = other.getX() - getX(), deltaY = other.getY() - getY();
		return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
	}

	public boolean isWithinDistance(int width, int height, Tile otherLocation, int otherWidth, int otherHeight, int distance) {
		Tile myClosestTile = this.closestTileOf(otherLocation, width, height);
		Tile theirClosestTile = otherLocation.closestTileOf(Tile.create(this), otherWidth, otherHeight);

		return myClosestTile.distanceToPoint(theirClosestTile) <= distance;
	}

	public Tile closestTileOf(Tile from, int width, int height) {
		if(width < 2 && height < 2) {
			return Tile.create(this);
		}
		Location location = null;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				Location loc = Tile.create(getX() + x, this.getY() + y, this.getZ());
				if(location == null || loc.distanceToPoint(from) < location.distanceToPoint(from)) {
					location = loc;
				}
			}
		}
		return Tile.create(location);
	}

	public boolean isWithinDistance(Tile location, int distance) {
		int objectX = location.getX();
		int objectY = location.getY();
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((objectX + i) == getX() && ((objectY + j) == getY() || (objectY - j) == getY() || objectY == getY())) {
					return true;
				} else if ((objectX - i) == getX() && ((objectY + j) == getY() || (objectY - j) == getY() || objectY == getY())) {
					return true;
				} else if (objectX == getX() && ((objectY + j) == getY() || (objectY - j) == getY() || objectY == getY())) {
					return true;
				}
			}
		}
		return false;
	}

	public int distanceToPoint(Tile other) {
		int pointX = other.getX();
		int pointY = other.getY();
		return (int) Math.sqrt(Math.pow(getX() - pointX, 2) + Math.pow(getY() - pointY, 2));
	}
}