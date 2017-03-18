package com.model.game.location;

import java.util.ArrayList;
import java.util.List;

import com.model.game.character.Entity;
import com.model.utility.Utility;

/**
 * A position point on the map.
 * 
 * @author blakeman8192
 * @author lare96
 */
public class Position {

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
	public Position(int x, int y) {
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
	public Position(int x, int y, int z) {
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}

	/**
	 * Creates a new position with default values (0) for the coordinates.
	 */
	public Position() {

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
	public static Position create(int x, int y, int z) {
		return new Position(x, y, z);
	}

	/**
	 * Checks if this position is in the specified location.
	 * 
	 * @param other
	 *            the location to check for.
	 * @return true if this location was in the specified location.
	 */
	public boolean inLocation(Location other) {
		return this.x > other.getSouthWest().getX() && this.x < other.getNorthEast().getX() && this.y > other.getSouthWest().getY() && this.y < other.getNorthEast().getY() ? true : false;
	}

	/**
	 * Checks if this position is in the specified location (inclusive).
	 * 
	 * @param other
	 *            the location to check for.
	 * @return true if this location was in the specified location.
	 */
	public boolean inLocationInclusive(Location other) {
		return this.x >= other.getSouthWest().getX() && this.x <= other.getNorthEast().getX() && this.y >= other.getSouthWest().getY() && this.y <= other.getNorthEast().getY() ? true : false;
	}

	/**
	 * Sets this position as the other position. <b>Please use this method
	 * instead of entity.setPosition(other)</b> because of reference conflicts
	 * (if the other position gets modified, so will the entity's).
	 * 
	 * @param other
	 *            the other position.
	 */
	public Position setAs(Position other) {
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
	public Position move(int amountX, int amountY) {
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
	public Position move(int amountX, int amountY, int amountZ) {
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
		if (other instanceof Position) {
			Position p = (Position) other;
			return x == p.x && y == p.y && z == p.z;
		}
		return false;
	}

	@Override
	public Position clone() {
		return new Position(x, y, z);
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
	 * Gets the X coordinate of the region containing this Position.
	 * 
	 * @return the region X coordinate.
	 */
	public int getRegionX() {
		return (x >> 3) - 6;
	}

	/**
	 * Gets the Y coordinate of the region containing this Position.
	 * 
	 * @return the region Y coordinate.
	 */
	public int getRegionY() {
		return (y >> 3) - 6;
	}

	/**
	 * Gets the local X coordinate relative to the base Position.
	 * 
	 * @param base
	 *            the base Position.
	 * @return the local X coordinate.
	 */
	public int getLocalX(Position base) {
		return x - 8 * base.getRegionX();
	}

	/**
	 * Gets the local Y coordinate relative to the base Position.
	 * 
	 * @param base
	 *            the base Position.
	 * @return the local Y coordinate.
	 */
	public int getLocalY(Position base) {
		return y - 8 * base.getRegionY();
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
	public boolean isViewableFrom(Position other) {
		if (this.getZ() != other.getZ())
			return false;

		Position p = Utility.delta(this, other);
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
			return distanceToPoint(victim.getPosition());
		}
		int lowestDistance = 100;
		List<Position> myTiles = entityTiles(attacker);
		List<Position> theirTiles = entityTiles(victim);
		for (Position myTile : myTiles) {
			for (Position theirTile : theirTiles) {
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
			return distance(victim.getPosition()) <= distance;
		}
		List<Position> myTiles = entityTiles(attacker);
		List<Position> theirTiles = entityTiles(victim);
		for (Position myTile : myTiles) {
			for (Position theirTile : theirTiles) {
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
	public boolean isWithinDistance(Position location, int distance) {
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
	public List<Position> entityTiles(Entity entity) {
		List<Position> myTiles = new ArrayList<Position>();
		myTiles.add(entity.getPosition());
		if (entity.getWidth() > 1) {
			for (int i = 1; i < entity.getWidth(); i++) {
				myTiles.add(Position.create(entity.getPosition().getX() + i,
						entity.getPosition().getY(), entity.getPosition().getZ()));
			}
		}
		if (entity.getHeight() > 1) {
			for (int i = 1; i < entity.getHeight(); i++) {
				myTiles.add(Position.create(entity.getPosition().getX(),
						entity.getPosition().getY() + i, entity.getPosition().getZ()));
			}
		}
		int myHighestVal = (entity.getWidth() > entity.getHeight() ? entity.getWidth() : entity.getHeight());
		if (myHighestVal > 1) {
			for (int i = 1; i < myHighestVal; i++) {
				myTiles.add(Position.create(entity.getPosition().getX() + i,
						entity.getPosition().getY() + i, entity.getPosition().getZ()));
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
	public boolean withinDistance(Position position, int distance) {
		if (this.getZ() != position.getZ())
			return false;

		return Math.abs(position.getX() - this.getX()) <= distance && Math.abs(position.getY() - this.getY()) <= distance;
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
	public int distance(Position other) {
		int deltaX = other.x - x, deltaY = other.y - y;
		double dis = Math.sqrt(Math.pow(deltaX, 2D) + Math.pow(deltaY, 2D));
		if (dis > 1.0 && dis < 2)
			return 2;
		return (int) dis;
	}
	
	/**
	 * Gets the distance to a location.
	 *
	 * @param other The location.
	 * @return The distance from the other location.
	 */
	public int distanceToPoint(Position other) {
		int absX = x;
		int absY = y;
		int pointX = other.getX();
		int pointY = other.getY();
		return (int) Math.sqrt(Math.pow(absX - pointX, 2) + Math.pow(absY - pointY, 2));
	}
	
	public static double getDistance(Position p, Position p2) {
		return Math.sqrt((p2.getX() - p.getX()) * (p2.getX() - p.getX()) + (p2.getY() - p.getY()) * (p2.getY() - p.getY()));
	}
	
	/**
	 * Gets the distance between this position and another position. Only X and
	 * Y are considered (i.e. 2 dimensions).
	 *
	 * @param other The other position.
	 * @return The distance.
	 */
	public int getDistance(Position other) {
		int deltaX = x - other.x;
		int deltaY = y - other.y;
		return (int) Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY));
	}
}