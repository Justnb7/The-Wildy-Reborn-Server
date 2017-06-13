package hyperion.region;

import clipmap.Region;
import clipmap.Tile;
import com.model.game.World;
import com.model.game.character.Entity;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.object.GameObject;
import hyperion.Directions;
import hyperion.impl.PrimitivePathFinder;

import java.util.*;

/**
 * Manages the world regions.
 *
 * THIS IS FROM MACKS 88
 *
 * Manages entities in a region..
 *
 * @author Graham Edgecombe
 * @author Mack
 */
public class RegionManager {

	public static RegionManager get() {
		return World.getWorld().regions; // TODO rather than putting in world, allocate here to keep code self-referencing.. easier to port
	}

	/**
	 * The region size.
	 */
	public static final int REGION_SIZE = 32;

	/**
	 * The lower bound that splits the region in half.
	 */
	@SuppressWarnings("unused")
	private static final int LOWER_BOUND = REGION_SIZE / 2 - 1;

	/**
	 * The active (loaded) region map.
	 */
	private Map<RegionCoordinates, RegionStore> activeRegions = new HashMap<RegionCoordinates, RegionStore>();

	public static boolean blockedNorth(Tile loc, Entity entity) {
		return !PrimitivePathFinder.canMove(entity, loc, Directions.NormalDirection.NORTH, entity.size(), false);
	}

	public static boolean blockedEast(Tile loc, Entity entity) {
		return !PrimitivePathFinder.canMove(entity, loc, Directions.NormalDirection.EAST, entity.size(), false);
	}

	public static boolean blockedSouth(Tile loc, Entity entity) {
		return !PrimitivePathFinder.canMove(entity, loc, Directions.NormalDirection.SOUTH, entity.size(), false);
	}

	public static boolean blockedWest(Tile loc, Entity entity) {
		return !PrimitivePathFinder.canMove(entity, loc, Directions.NormalDirection.WEST, entity.size(), false);
	}

	public static boolean blockedNorthEast(Tile loc, Entity entity) {
		return !PrimitivePathFinder.canMove(entity, loc, Directions.NormalDirection.NORTH_EAST, entity.size(), false);
	}

	public static boolean blockedNorthWest(Tile loc, Entity entity) {
		return !PrimitivePathFinder.canMove(entity, loc, Directions.NormalDirection.NORTH_WEST, entity.size(), false);
	}

	public static boolean blockedSouthEast(Tile loc, Entity entity) {
		return !PrimitivePathFinder.canMove(entity, loc, Directions.NormalDirection.SOUTH_EAST, entity.size(), false);
	}

	public static boolean blockedSouthWest(Tile loc, Entity entity) {
		return !PrimitivePathFinder.canMove(entity, loc, Directions.NormalDirection.SOUTH_WEST, entity.size(), false);
	}

	/**
	 * Gets the local players around an entity.
	 *
	 * @param mob
	 *            The entity.
	 * @return The collection of local players.
	 */
	public Collection<Player> getLocalPlayers(Entity mob) {
		List<Player> localPlayers = new LinkedList<Player>();
		RegionStore[] regions = getSurroundingRegions(mob.getPosition());
		for (RegionStore region : regions) {
			for (Player player : region.getPlayers()) {
				if (player.getPosition().isWithinDistance(mob.getPosition())) {
					localPlayers.add(player);
				}
			}
		}
		return Collections.unmodifiableCollection(localPlayers);
	}

	/**
	 * Gets the local NPCs around an entity.
	 *
	 * @param mob
	 *            The entity.
	 * @return The collection of local NPCs.
	 */
	public Collection<NPC> getLocalNpcs(Entity mob) {
		List<NPC> npcs = new LinkedList<NPC>();
		RegionStore[] regions = getSurroundingRegions(mob.getPosition());
		for (RegionStore region : regions) {
			for (NPC npc : region.getNpcs()) {
				if (npc.getPosition().isWithinDistance(mob.getPosition())) {
					npcs.add(npc);
				}
			}
		}
		return Collections.unmodifiableCollection(npcs);
	}

	/**
	 * Gets a local game object.
	 *
	 * @param location
	 *            The object's location.
	 * @param id
	 *            The object's id.
	 * @return The <code>GameObject</code> or <code>null</code> if no game
	 *         object was found to be existent.
	 */
	public GameObject getGameObject(Tile location, int id) { // TODO
		RegionStore[] regions = getSurroundingRegions(location);
		for (RegionStore region : regions) {
			for (GameObject object : region.getGameObjects()) {
				if (object.getPosition().equals(location) && object.getId() == id) {
					return object;
				}
			}
		}
		return null;
	}

	/**
	 * Gets all object types that are not 'rangeable' in the sense that mobs
	 * cannot fire a projectile over this type.
	 * 
	 * @param location
	 * @return
	 */
	public GameObject getWallObject(Tile location) { // TODO
		RegionStore[] regions = getSurroundingRegions(location);
		for (RegionStore region : regions) {
			for (GameObject object : region.getGameObjects()) {
				if (object != null && object.getType() >= 0 && object.getType() <= 3) {
					return object;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the regions surrounding a location.
	 *
	 * @param location
	 *            The location.
	 * @return The regions surrounding the location.
	 */
	public RegionStore[] getSurroundingRegions(Tile location) {
		int regionX = location.getX() / REGION_SIZE;
		int regionY = location.getY() / REGION_SIZE;

		RegionStore[] surrounding = new RegionStore[9];
		surrounding[0] = getRegion(regionX, regionY);
		surrounding[1] = getRegion(regionX - 1, regionY - 1);
		surrounding[2] = getRegion(regionX + 1, regionY + 1);
		surrounding[3] = getRegion(regionX - 1, regionY);
		surrounding[4] = getRegion(regionX, regionY - 1);
		surrounding[5] = getRegion(regionX + 1, regionY);
		surrounding[6] = getRegion(regionX, regionY + 1);
		surrounding[7] = getRegion(regionX - 1, regionY + 1);
		surrounding[8] = getRegion(regionX + 1, regionY - 1);

		return surrounding;
	}

	/**
	 * Gets a region by location.
	 *
	 * @param location
	 *            The location.
	 * @return The region.
	 */
	public RegionStore getRegionByLocation(Tile location) {
		return getRegion(location.getX() / REGION_SIZE, location.getY() / REGION_SIZE);
	}

	/**
	 * Gets a region by its x and y coordinates.
	 *
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @return The region.
	 */
	public RegionStore getRegion(int x, int y) {
		RegionCoordinates key = new RegionCoordinates(x, y);
		if (activeRegions.containsKey(key)) {
			return activeRegions.get(key);
		} else {
			RegionStore region = new RegionStore(key);
			activeRegions.put(key, region);
			return region;
		}
	}


	/**
	 * 
	 * @param entity
	 * @param endX
	 * @param endY
	 * @param height
	 * @return
	 */
	public boolean canMove(Entity entity, int endX, int endY, int height) {
		return canMove(entity.getPosition().getX(), entity.getPosition().getY(), endX, endY, height, entity.size(),
				entity.getHeight());
	}

	/**
	 * Checks if the mob is able to move to the next tile.
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @param height
	 * @param xLength
	 * @param yLength
	 * @return
	 */
	public boolean canMove(int startX, int startY, int endX, int endY, int height, int xLength, int yLength) {
		int diffX = endX - startX;
		int diffY = endY - startY;
		int max = Math.max(Math.abs(diffX), Math.abs(diffY));
		for (int ii = 0; ii < max; ii++) {
			int currentX = endX - diffX;
			int currentY = endY - diffY;
			for (int i = 0; i < xLength; i++) {
				for (int i2 = 0; i2 < yLength; i2++) {
					if (diffX < 0 && diffY < 0) { // South west (north east
													// flag)
						if ((Region.getClippingMask((currentX + i) - 1, (currentY + i2) - 1, height) & 0x12c010e) != 0
								|| (Region.getClippingMask((currentX + i) - 1, currentY + i2, height) & 0x12c0108) != 0
								|| (Region.getClippingMask(currentX + i, (currentY + i2) - 1, height) & 0x12c0102) != 0)
							return false;
					} else if (diffX > 0 && diffY > 0) { // North east
						if ((Region.getClippingMask(currentX + i + 1, currentY + i2 + 1, height) & 0x12c01e0) != 0
								|| (Region.getClippingMask(currentX + i + 1, currentY + i2, height) & 0x12c0180) != 0
								|| (Region.getClippingMask(currentX + i, currentY + i2 + 1, height) & 0x12c0120) != 0)
							return false;
					} else if (diffX < 0 && diffY > 0) { // North west
						if ((Region.getClippingMask((currentX + i) - 1, currentY + i2 + 1, height) & 0x12c0138) != 0
								|| (Region.getClippingMask((currentX + i) - 1, currentY + i2, height) & 0x12c0108) != 0
								|| (Region.getClippingMask(currentX + i, currentY + i2 + 1, height) & 0x12c0120) != 0)
							return false;
					} else if (diffX > 0 && diffY < 0) { // South east
						if ((Region.getClippingMask(currentX + i + 1, (currentY + i2) - 1, height) & 0x12c0183) != 0
								|| (Region.getClippingMask(currentX + i + 1, currentY + i2, height) & 0x12c0180) != 0
								|| (Region.getClippingMask(currentX + i, (currentY + i2) - 1, height) & 0x12c0102) != 0)
							return false;
					} else if (diffX > 0 && diffY == 0) { // East
						if ((Region.getClippingMask(currentX + i + 1, currentY + i2, height) & 0x12c0180) != 0)
							return false;
					} else if (diffX < 0 && diffY == 0) { // West
						if ((Region.getClippingMask((currentX + i) - 1, currentY + i2, height) & 0x12c0108) != 0)
							return false;
					} else if (diffX == 0 && diffY > 0) { // North
						if ((Region.getClippingMask(currentX + i, currentY + i2 + 1, height) & 0x12c0120) != 0)
							return false;
					} else if (diffX == 0 && diffY < 0
							&& (Region.getClippingMask(currentX + i, (currentY + i2) - 1, height) & 0x12c0102) != 0) // South
						return false;
				}
			}

			if (diffX < 0)
				diffX++;
			else if (diffX > 0)
				diffX--;
			if (diffY < 0)
				diffY++;
			else if (diffY > 0)
				diffY--;
		}
		return true;
	}

}
