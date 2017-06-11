package com.model.game.character.pathfinder.region;

import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.object.GameObject;
import com.model.utility.cache.map.Tile;

import java.util.*;


/**
 * Represents a single region.
 * @author Graham Edgecombe
 *
 */
public class Region {
	
	public static final int NORTH_WEST_BLOCKED = 0x1;
	public static final int NORTH_BLOCKED = 0x2;
	public static final int NORTH_EAST_BLOCKED = 0x4;
	public static final int EAST_BLOCKED = 0x8;
	public static final int SOUTH_EAST_BLOCKED = 0x10;
	public static final int SOUTH_BLOCKED = 0x20;
	public static final int SOUTH_WEST_BLOCKED = 0x40;
	public static final int WEST_BLOCKED = 0x80;
	public static final int TILE_BLOCKED = 0x100;
	public static final int PROJECTILE_NORTH_WEST_BLOCKED = 0x200;
	public static final int PROJECTILE_NORTH_BLOCKED = 0x400;
	public static final int PROJECTILE_NORTH_EAST_BLOCKED = 0x800;
	public static final int PROJECTILE_EAST_BLOCKED = 0x1000;
	public static final int PROJECTILE_SOUTH_EAST_BLOCKED = 0x2000;
	public static final int PROJECTILE_SOUTH_BLOCKED = 0x4000;
	public static final int PROJECTILE_SOUTH_WEST_BLOCKED = 0x8000;
	public static final int PROJECTILE_WEST_BLOCKED = 0x10000;
	public static final int PROJECTILE_TILE_BLOCKED = 0x20000;
	public static final int UNKNOWN = 0x80000;
	public static final int BLOCKED_TILE = 0x200000;
	public static final int UNLOADED_TILE = 0x1000000;

	/**
	 * The region coordinates.
	 */
	private RegionCoordinates coordinate;
	
	/**
	 * A list of players in this region.
	 */
	private List<Player> players = new LinkedList<Player>();
	
	/**
	 * A list of NPCs in this region.
	 */
	private List<NPC> npcs = new LinkedList<NPC>();

	/**
	 * Creates a region.
	 * @param coordinate The coordinate.
	 */
	public Region(RegionCoordinates coordinate) {
		this.coordinate = coordinate;
	}
	
	/**
	 * Gets the region coordinates.
	 * @return The region coordinates.
	 */
	public RegionCoordinates getCoordinates() {
		return coordinate;
	}

	/**
	 * Gets the list of players.
	 * @return The list of players.
	 */
	public Collection<Player> getPlayers() {
		synchronized(this) {
			return Collections.unmodifiableCollection(new LinkedList<Player>(players));
		}
	}
	
	/**
	 * Gets the list of NPCs.
	 * @return The list of NPCs.
	 */
	public Collection<NPC> getNpcs() {
		synchronized(this) {
			return Collections.unmodifiableCollection(new LinkedList<NPC>(npcs));
		}
	}
	
	public static boolean reached(Tile position, int minimumX, int minimumY, int maximumX, int maximumY, int mask) {
		int z = position.getZ() % 4;
		int srcX = position.getX();
		int srcY = position.getY();
		if (srcX >= minimumX && srcX <= maximumX && srcY >= minimumY && srcY <= maximumY) {
			return true;
		}
		if (srcX == minimumX - 1 && srcY >= minimumY && srcY <= maximumY && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.EAST_BLOCKED) == 0 && (mask & Region.EAST_BLOCKED) == 0) {
			return true;
		}
		if (srcX == maximumX + 1 && srcY >= minimumY && srcY <= maximumY && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.WEST_BLOCKED) == 0 && (mask & Region.NORTH_BLOCKED) == 0) {
			return true;
		}
		if (srcY == minimumY - 1 && srcX >= minimumX && srcX <= maximumX && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.NORTH_BLOCKED) == 0 && (mask & Region.NORTH_EAST_BLOCKED) == 0) {
			return true;
		}
		if (srcY == maximumY + 1 && srcX >= minimumX && srcX <= maximumX && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.SOUTH_BLOCKED) == 0 && (mask & Region.NORTH_WEST_BLOCKED) == 0) {
			return true;
		}
		return false;
	}
	
	public static boolean method219(Tile position, int destX, int destY, int objectType, int rotation) {
		int srcX = position.getX();
		int srcY = position.getY();
		if (srcX == destX && srcY == destY) {
			return true;
		}
		int z = position.getZ() % 4;
		switch (objectType) {
			case 0: {
				switch (rotation) {
					case 0: {
						if (srcX == destX - 1 && srcY == destY) {
							return true;
						}
						if (srcX == destX && srcY == destY + 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.SOUTH_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						if (srcX == destX && srcY == destY - 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.NORTH_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						return false;
					}
					case 1: {
						if (srcX == destX && srcY == destY + 1) {
							return true;
						}
						if (srcX == destX - 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.EAST_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						if (srcX == destX + 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.WEST_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						return false;
					}
					case 2: {
						if (srcX == destX + 1 && srcY == destY) {
							return true;
						}
						if (srcX == destX && srcY == destY + 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.SOUTH_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						if (srcX == destX && srcY == destY - 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.NORTH_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						return false;
					}
					case 3: {
						if (srcX == destX && srcY == destY - 1) {
							return true;
						}
						if (srcX == destX - 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.EAST_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						if (srcX == destX + 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.WEST_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						return false;
					}
				}
				return false;
				// throw new AssertionError();
			}
			case 2: {
				switch (rotation) {
					case 0: {
						if (srcX == destX - 1 && srcY == destY) {
							return true;
						}
						if (srcX == destX && srcY == destY + 1) {
							return true;
						}
						if (srcX == destX + 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.WEST_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						if (srcX == destX && srcY == destY - 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.NORTH_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						return false;
					}
					case 1: {
						if (srcX == destX - 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.EAST_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						if (srcX == destX && srcY == destY + 1) {
							return true;
						}
						if (srcX == destX + 1 && srcY == destY) {
							return true;
						}
						if (srcX == destX && srcY == destY - 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.NORTH_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						return false;
					}
					case 2: {
						if (srcX == destX - 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.EAST_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						if (srcX == destX && srcY == destY + 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.SOUTH_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						if (srcX == destX + 1 && srcY == destY) {
							return true;
						}
						if (srcX == destX && srcY == destY - 1) {
							return true;
						}
						return false;
					}
					case 3: {
						if (srcX == destX - 1 && srcY == destY) {
							return true;
						}
						if (srcX == destX && srcY == destY + 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.SOUTH_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						if (srcX == destX + 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & (Region.WEST_BLOCKED | Region.TILE_BLOCKED)) == 0) {
							return true;
						}
						if (srcX == destX && srcY == destY - 1) {
							return true;
						}
						return false;
					}
				}
				return false;
				// throw new AssertionError();
			}
			case 9: {
				if (srcX == destX && srcY == destY + 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.SOUTH_BLOCKED) == 0) {
					return true;
				}
				if (srcX == destX && srcY == destY - 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.NORTH_BLOCKED) == 0) {
					return true;
				}
				if (srcX == destX - 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.EAST_BLOCKED) == 0) {
					return true;
				}
				if (srcX == destX + 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.WEST_BLOCKED) == 0) {
					return true;
				}
				return false;
			}
		}
		return false;
	}

	public static boolean method220(Tile position, int destX, int destY, int objectType, int rotation) {
		int srcX = position.getX();
		int srcY = position.getY();
		if (srcX == destX && srcY == destY) {
			return true;
		}
		int z = position.getZ() % 4;
		switch (objectType) {
			case 6:
			case 7: {
				if (objectType == 7) {
					rotation = (rotation + 2) & 3;
				}
				switch (rotation) {
					case 0: {
						if (srcX == destX + 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.WEST_BLOCKED) == 0) {
							return true;
						}
						if (srcX == destX && srcY == destY - 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.NORTH_BLOCKED) == 0) {
							return true;
						}
						return false;
					}
					case 1: {
						if (srcX == destX - 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.EAST_BLOCKED) == 0) {
							return true;
						}
						if (srcX == destX && srcY == destY - 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.NORTH_BLOCKED) == 0) {
							return true;
						}
						return false;
					}
					case 2: {
						if (srcX == destX - 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.EAST_BLOCKED) == 0) {
							return true;
						}
						if (srcX == destX && srcY == destY + 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.SOUTH_BLOCKED) == 0) {
							return true;
						}
						return false;
					}
					case 3: {
						if (srcX == destX + 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.WEST_BLOCKED) == 0) {
							return true;
						}
						if (srcX == destX && srcY == destY + 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.SOUTH_BLOCKED) == 0) {
							return true;
						}
						return false;
					}
				}
				return false;
				// throw new AssertionError();
			}
			case 8: {
				if (srcX == destX && srcY == destY + 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.SOUTH_BLOCKED) == 0) {
					return true;
				}
				if (srcX == destX && srcY == destY - 1 && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.NORTH_BLOCKED) == 0) {
					return true;
				}
				if (srcX == destX - 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.EAST_BLOCKED) == 0) {
					return true;
				}
				if (srcX == destX + 1 && srcY == destY && (RegionManager.get().getClippingMask(srcX, srcY, z) & Region.WEST_BLOCKED) == 0) {
					return true;
				}
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Adds a new player.
	 * @param player The player to add.
	 */
	public void addPlayer(Player player) {
		synchronized(this) {
			players.add(player);
		}
	}

	/**
	 * Removes an old player.
	 * @param player The player to remove.
	 */
	public void removePlayer(Player player) {
		synchronized(this) {
			players.remove(player);
		}
	}

	/**
	 * Adds a new NPC.
	 * @param npc The NPC to add.
	 */
	public void addNpc(NPC npc) {
		synchronized(this) {
			npcs.add(npc);
		}
	}

	/**
	 * Removes an old NPC.
	 * @param npc The NPC to remove.
	 */
	public void removeNpc(NPC npc) {
		synchronized(this) {
			npcs.remove(npc);
		}
	}
	
	@Override
	public String toString() {
		return "["+coordinate.getX()+":"+coordinate.getY()+"]";
	}
	
	/**
	 * Gets the regions surrounding a location.
	 * @return The regions surrounding the location.
	 */
	public Region[] getSurroundingRegions() {
		Region[] surrounding = new Region[9];
		surrounding[0] = this;
		surrounding[1] = RegionManager.get().getRegion(this.getCoordinates().getX() - 1, this.getCoordinates().getY() - 1);
		surrounding[2] = RegionManager.get().getRegion(this.getCoordinates().getX() + 1, this.getCoordinates().getY() + 1);
		surrounding[3] = RegionManager.get().getRegion(this.getCoordinates().getX() - 1, this.getCoordinates().getY());
		surrounding[4] = RegionManager.get().getRegion(this.getCoordinates().getX(), this.getCoordinates().getY() - 1);
		surrounding[5] = RegionManager.get().getRegion(this.getCoordinates().getX() + 1, this.getCoordinates().getY());
		surrounding[6] = RegionManager.get().getRegion(this.getCoordinates().getX(), this.getCoordinates().getY() + 1);
		surrounding[7] = RegionManager.get().getRegion(this.getCoordinates().getX() - 1, this.getCoordinates().getY() + 1);
		surrounding[8] = RegionManager.get().getRegion(this.getCoordinates().getX() + 1, this.getCoordinates().getY() - 1);

		
		return surrounding;
	}

	private List<GameObject> objects = new LinkedList<GameObject>();

	public Collection<GameObject> getGameObjects() {
		return objects;
	}

	public void addObject(GameObject obj) {
		objects.add(obj);
	}

	public void removeObject(GameObject obj) {
		objects.remove(obj);
	}

	public GameObject getGameObject(Tile location, int id) {
		for(Region r : getSurroundingRegions()) {
			for(GameObject obj : r.getGameObjects()) {
				if(obj.getPosition().equals(location) && obj.getId() == id) {
					return obj;
				}
			}
		}
		return null;
	}
}
