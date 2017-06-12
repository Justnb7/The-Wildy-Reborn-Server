package com.model.utility.cache.map;

import com.model.game.character.Entity;
import com.model.game.character.pathfinder.Directions;
import com.model.game.character.pathfinder.impl.PrimitivePathFinder;
import com.model.game.character.pathfinder.region.RegionManager;
import com.model.game.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Tile {

	private int[] pointer = new int[3];

	public Tile(int x, int y, int z) {
		this.pointer[0] = x;
		this.pointer[1] = y;
		this.pointer[2] = z;
	}

	public int[] getTile() {
		return pointer;
	}

	public int getTileX() {
		return pointer[0];
	}

	public int getTileY() {
		return pointer[1];
	}

	public int getTileHeight() {
		return pointer[2];
	}

    public int getX() {
        return getTileX();
    }

	public int getY() {
		return getTileY();
	}

	public static Tile create(int x, int y, int z) {
		return new Tile(x, y, z);
	}

	public int getZ() {
		return getTileHeight();
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
					if ((RegionManager.get().getClippingMask(getX() - 1, k, getTileHeight()) & 0x1280108) != 0)
						return false;
				}
				break;
			case EAST:
				for (int k = getY(); k < getY() + size; k++) {
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(getX() + size, k, npcHeight, 1))
					return false;*/
					if ((RegionManager.get().getClippingMask(getX() + size, k, getTileHeight()) & 0x1280180) != 0)
						return false;
				}
				break;
			case SOUTH:
				for (int i = getX(); i < getX() + size; i++) {
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(i, y - 1, npcHeight, 1))
					return false;*/
					if ((RegionManager.get().getClippingMask(i, getY() - 1, getTileHeight()) & 0x1280102) != 0)
						return false;
				}
				break;
			case NORTH:
				for (int i = getX(); i < getX() + size; i++) {
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(i, getY() + size, npcHeight, 1))
					return false;*/
					if ((RegionManager.get().getClippingMask(i, getY() + size, getTileHeight()) & 0x1280120) != 0)
						return false;
				}
				break;
			case SOUTH_WEST:
				for (int i = getX(); i < getX() + size; i++) {
					int s = RegionManager.get().getClippingMask(i, getY() - 1, getTileHeight());
					int w = RegionManager.get().getClippingMask(i - 1, getY(), getTileHeight());
					int sw = RegionManager.get().getClippingMask(i - 1, getY() - 1, getTileHeight());
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(i - 1, getY() - 1, npcHeight, 1))
					return false;*/
					if ((sw & 0x128010e) != 0 || (s & 0x1280102) != 0 || (w & 0x1280108) != 0)
						return false;
				}
				for (int k = getY(); k < getY() + size; k++) {
					int s = RegionManager.get().getClippingMask(getX(), k - 1, getTileHeight());
					int w = RegionManager.get().getClippingMask(getX() - 1, k, getTileHeight());
					int sw = RegionManager.get().getClippingMask(getX() - 1, k - 1, getTileHeight());
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(getX() - 1, k - 1, npcHeight, 1))
					return false;*/
					if ((sw & 0x128010e) != 0 || (s & 0x1280102) != 0 || (w & 0x1280108) != 0)
						return false;
				}
				break;
			case SOUTH_EAST:
				for (int i = getX(); i < getX() + size; i++) {
					int s = RegionManager.get().getClippingMask(i, getY() - 1, getTileHeight());
					int e = RegionManager.get().getClippingMask(i + 1, getY(), getTileHeight());
					int se = RegionManager.get().getClippingMask(i + 1, getY() - 1, getTileHeight());
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(i + 1, getY() - 1, npcHeight, 1))
					return false;*/
					if ((se & 0x1280183) != 0 || (s & 0x1280102) != 0 || (e & 0x1280180) != 0)
						return false;
				}
				for (int k = getY(); k < getY() + size; k++) {
					int s = RegionManager.get().getClippingMask(getX() + size - 1, k - 1, getTileHeight());
					int e = RegionManager.get().getClippingMask(getX() + size, k, getTileHeight());
					int se = RegionManager.get().getClippingMask(getX() + size, k - 1, getTileHeight());
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(getX() + 1, k - 1, npcHeight, 1))
					return false;*/
					if ((se & 0x1280183) != 0 || (s & 0x1280102) != 0 || (e & 0x1280180) != 0)
						return false;
				}
				break;
			case NORTH_WEST:
				for (int i = getX(); i < getX() + size; i++) {
					int n = RegionManager.get().getClippingMask(i, getY() + size, getTileHeight());
					int w = RegionManager.get().getClippingMask(i - 1, getY() + size - 1, getTileHeight());
					int nw = RegionManager.get().getClippingMask(i - 1, getY() + size, getTileHeight());
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(i - 1, getY() + size, npcHeight, 1))
					return false;*/
					if ((nw & 0x1280138) != 0 || (n & 0x1280102) != 0 || (w & 0x1280108) != 0)
						return false;
				}
				for (int k = getY(); k < getY() + size; k++) {
					int n = RegionManager.get().getClippingMask(getX(), getY(), getTileHeight());
					int w = RegionManager.get().getClippingMask(getX() - 1, getY(), getTileHeight());
					int nw = RegionManager.get().getClippingMask(getX() - 1, getY() + 1, getTileHeight());
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(getX() - 1, getY() + 1, npcHeight, 1))
					return false;*/
					if ((nw & 0x1280138) != 0 || (n & 0x1280102) != 0 || (w & 0x1280108) != 0)
						return false;
				}
				break;
			case NORTH_EAST:
				for (int i = getX(); i < getX() + size; i++) {
					int n = RegionManager.get().getClippingMask(i, getY() + size, getTileHeight());
					int e = RegionManager.get().getClippingMask(i + 1, getY() + size - 1, getTileHeight());
					int ne = RegionManager.get().getClippingMask(i + 1, getY() + size, getTileHeight());
				/*if (checkingNPCs && TileControl.getSingleton().locationOccupied(i + 1, getY() + size, npcHeight, 1))
					return false;*/
					if ((ne & 0x12801e0) != 0 || (n & 0x1280120) != 0 || (e & 0x1280180) != 0)
						return false;
				}
				for (int k = getY(); k < getY() + size; k++) {
					int n = RegionManager.get().getClippingMask(getX() + size - 1, k + 1, getTileHeight());
					int e = RegionManager.get().getClippingMask(getX() + size, k, getTileHeight());
					int ne = RegionManager.get().getClippingMask(getX() + size, k + 1, getTileHeight());
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

	@Override
	public Tile clone() {
		return new Tile(getX(), getY(), getTileHeight());
	}
	public Tile transform(int diffX, int diffY, int diffZ) {
		return Tile.create(getX() + diffX, getY() + diffY, getTileHeight() + diffZ);
	}

	public Tile transform(int diffX, int diffY) {
		return new Tile(getTileHeight(), getX() + diffX, getY() + diffY);
	}

	@Override
	public int hashCode() {
		return getTileHeight() << 30 | getX() << 15 | getY();
	}

	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Tile)) {
			return false;
		}
		Tile loc = (Tile) other;
		return loc.getX() == getX() && loc.getY() == getY() && loc.getTileHeight() == getTileHeight();
	}

	public boolean matches(int x, int y) {
		return this.getX() == x && this.getY() == y;
	}

	public boolean matches(Tile other) {
		return this.getX() == other.getX() && this.getY() == other.getY() && this.getTileHeight() == other.getTileHeight();
	}

	public boolean isNextTo(Tile other) {
		if(getTileHeight() != other.getTileHeight()) {
			return false;
		}
		/*int deltagetX() = Math.abs(other.getX() - x), deltaY = Math.abs(other.y - y);
		return deltagetX() <= 1 && deltaY <= 1;*/
		return (getX() == other.getX() && getY() != other.getY()
				|| getX() != other.getX() && getY() == other.getY()
				|| getX() == other.getX() && getY() == other.getY());
	}

	public int getLocalX() {
		return getLocalX(this);
	}

	public int getLocalY() {
		return getLocalY(this);
	}

	public int getLocalX(Tile l) {
		return getX() - 8 * (l.getRegionX() - 6);
	}

	public int getLocalY(Tile l) {
		return getY() - 8 * (l.getRegionY() - 6);
	}

	public int getRegionX() {
		return (getX() >> 3);
	}

	public int getRegionY() {
		return (getY() >> 3);
	}

	public Directions.NormalDirection direction(Tile next) {
		return Directions.directionFor(this, next);
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
		if(getTileHeight() != other.getTileHeight()) {
			return false;
		}
		int deltaX = other.getX() - getX(), deltaY = other.getY() - getY();
		return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
	}

	public boolean isWithinDistance(Entity attacker, Entity victim, int distance) {
		if(attacker.getWidth() == 1 && attacker.getHeight() == 1 &&
				victim.getWidth() == 1 && victim.getHeight() == 1 && distance == 1) {
			return distanceToPoint(victim.getPosition()) <= distance;
		}
		List<Tile> myTiles = entityTiles(attacker);
		List<Tile> theirTiles = entityTiles(victim);
		for(Tile myTile : myTiles) {
			for(Tile theirTile : theirTiles) {
				if(myTile.isWithinDistance(theirTile, distance)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isWithinDistance(int width, int height, Tile otherLocation, int otherWidth, int otherHeight, int distance) {
		Tile myClosestTile = this.closestTileOf(otherLocation, width, height);
		Tile theirClosestTile = otherLocation.closestTileOf(this, otherWidth, otherHeight);

		return myClosestTile.distanceToPoint(theirClosestTile) <= distance;
	}

	public Tile closestTileOf(Tile from, int width, int height) {
		if(width < 2 && height < 2) {
			return this;
		}
		Tile location = null;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				Tile loc = Tile.create(getX() + x, this.getY() + y, this.getTileHeight());
				if(location == null || loc.distanceToPoint(from) < location.distanceToPoint(from)) {
					location = loc;
				}
			}
		}
		return location;
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

	public List<Tile> entityTiles(Entity entity) {
		List<Tile> myTiles = new ArrayList<Tile>();
		myTiles.add(entity.getPosition());
		if(entity.getWidth() > 1) {
			for(int i = 1; i < entity.getWidth(); i++) {
				myTiles.add(Tile.create(entity.getPosition().getX() + i,
						entity.getPosition().getY(), entity.getPosition().getZ()));
			}
		}
		if(entity.getHeight() > 1) {
			for(int i = 1; i < entity.getHeight(); i++) {
				myTiles.add(Tile.create(entity.getPosition().getX(),
						entity.getPosition().getY() + i, entity.getPosition().getZ()));
			}
		}
		int myHighestVal = (entity.getWidth() > entity.getHeight() ? entity.getWidth() : entity.getHeight());
		if(myHighestVal > 1) {
			for(int i = 1; i < myHighestVal; i++) {
				myTiles.add(Tile.create(entity.getPosition().getX() + i,
						entity.getPosition().getY() + i, entity.getPosition().getZ()));
			}
		}
		return myTiles;
	}

	public int distanceToPoint(Tile other) {
		int pointX = other.getX();
		int pointY = other.getY();
		return (int) Math.sqrt(Math.pow(getX() - pointX, 2) + Math.pow(getY() - pointY, 2));
	}

	public Location toLocation() {
		return Location.create(getX(), getY(), getTileHeight());
	}
}