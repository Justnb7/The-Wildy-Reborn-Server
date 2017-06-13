package com.model.utility.cache.map;

import clipmap.RegionEntities;
import com.model.game.location.Location;
import com.model.game.object.GameObject;
import com.model.utility.cache.Direction;
import com.model.utility.cache.ObjectDefinition;

import java.util.HashMap;
import java.util.Map;

public class Region {

	public static Map<Integer,Region> regions = new HashMap<>();
	public int id;
	public final int[][][] clips;
	public final int[][][] shootable;
	// game objects in the region by default in the cache. NOT custom spawns
	public final GameObject[][][] objects;
	// other stuff related to this region but not specific to clipping. this class is strictly clipmap related only
	private final RegionEntities store = new RegionEntities();

	public Region(int id) {
		this.id = id;
		clips = new int[4][][];
		shootable = new int[4][][];
		objects = new GameObject[4][64][64];
	}

	public static void addClipping(boolean before, int x, int y, int height, int shift) {
        int regionX = x >> 3;
        int regionY = y >> 3;
        int regionId = ((regionX / 8) << 8) + (regionY / 8);
        getRegion(x, y).addClip(x, y, height, shift);
    }

	private static void addClippingAlternate(boolean before, int shift, int x, int y, int height) {
        int regionX = x >> 3;
        int regionY = y >> 3;
        int regionId = ((regionX / 8) << 8) + (regionY / 8);
        getRegion(x, y).addClip(x, y, height, shift);
    }

	public static void addClippingForVariableObject(boolean before, int x, int y, int height, int type, int direction, boolean flag) {

        addProjectileClippingForVariableObject(before, x, y, height, type, direction, flag);

        if (type == 0) {
            if (direction == 0) {
                addClippingAlternate(before, 128, x, y, height);
                addClippingAlternate(before, 8, x - 1, y, height);
            }
            if (direction == 1) {
                addClippingAlternate(before, 2, x, y, height);
                addClippingAlternate(before, 32, x, y + 1, height);
            }
            if (direction == 2) {
                addClippingAlternate(before, 8, x, y, height);
                addClippingAlternate(before, 128, x + 1, y, height);
            }
            if (direction == 3) {
                addClippingAlternate(before, 32, x, y, height);
                addClippingAlternate(before, 2, x, y - 1, height);
            }
        }
        if (type == 1 || type == 3) {
            if (direction == 0) {
                addClippingAlternate(before, 1, x, y, height);
                addClippingAlternate(before, 16, x - 1, y + 1, height);
            }
            if (direction == 1) {
                addClippingAlternate(before, 4, x, y, height);
                addClippingAlternate(before, 64, x + 1, y + 1, height);
            }
            if (direction == 2) {
                addClippingAlternate(before, 16, x, y, height);
                addClippingAlternate(before, 1, x + 1, y - 1, height);
            }
            if (direction == 3) {
                addClippingAlternate(before, 64, x, y, height);
                addClippingAlternate(before, 4, x - 1, y - 1, height);
            }
        }
        if (type == 2) {
            if (direction == 0) {
                addClippingAlternate(before, 130, x, y, height);
                addClippingAlternate(before, 8, x - 1, y, height);
                addClippingAlternate(before, 32, x, y + 1, height);
            }
            if (direction == 1) {
                addClippingAlternate(before, 10, x, y, height);
                addClippingAlternate(before, 32, x, y + 1, height);
                addClippingAlternate(before, 128, x + 1, y, height);
            }
            if (direction == 2) {
                addClippingAlternate(before, 40, x, y, height);
                addClippingAlternate(before, 128, x + 1, y, height);
                addClippingAlternate(before, 2, x, y - 1, height);
            }
            if (direction == 3) {
                addClippingAlternate(before, 160, x, y, height);
                addClippingAlternate(before, 2, x, y - 1, height);
                addClippingAlternate(before, 8, x - 1, y, height);
            }
        }
    }

	public static void addProjectileClippingForVariableObject(boolean before, int x, int y, int height, int type, int direction, boolean flag) {
        if (flag) {
            if (type == 0) {
                if (direction == 0) {
                    addProjectileClipping(before, x, y, height, 128);
                    addProjectileClipping(before, x - 1, y, height, 8);
                }
                if (direction == 1) {
                    addProjectileClipping(before, x, y, height, 2);
                    addProjectileClipping(before, x, y + 1, height, 32);
                }
                if (direction == 2) {
                    addProjectileClipping(before, x, y, height, 8);
                    addProjectileClipping(before, x + 1, y, height, 128);
                }
                if (direction == 3) {
                    addProjectileClipping(before, x, y, height, 32);
                    addProjectileClipping(before, x, y - 1, height, 2);
                }
            }
            if (type == 1 || type == 3) {
                if (direction == 0) {
                    addProjectileClipping(before, x, y, height, 1);
                    addProjectileClipping(before, x - 1, y + 1, height, 16);
                }
                if (direction == 1) {
                    addProjectileClipping(before, x, y, height, 4);
                    addProjectileClipping(before, x + 1, y + 1, height, 64);
                }
                if (direction == 2) {
                    addProjectileClipping(before, x, y, height, 16);
                    addProjectileClipping(before, x + 1, y - 1, height, 1);
                }
                if (direction == 3) {
                    addProjectileClipping(before, x, y, height, 64);
                    addProjectileClipping(before, x - 1, y - 1, height, 4);
                }
            }
            if (type == 2) {
                if (direction == 0) {
                    addProjectileClipping(before, x, y, height, 130);
                    addProjectileClipping(before, x - 1, y, height, 8);
                    addProjectileClipping(before, x, y + 1, height, 32);
                }
                if (direction == 1) {
                    addProjectileClipping(before, x, y, height, 10);
                    addProjectileClipping(before, x, y + 1, height, 32);
                    addProjectileClipping(before, x + 1, y, height, 128);
                }
                if (direction == 2) {
                    addProjectileClipping(before, x, y, height, 40);
                    addProjectileClipping(before, x + 1, y, height, 128);
                    addProjectileClipping(before, x, y - 1, height, 2);
                }
                if (direction == 3) {
                    addProjectileClipping(before, x, y, height, 160);
                    addProjectileClipping(before, x, y - 1, height, 2);
                    addProjectileClipping(before, x - 1, y, height, 8);
                }
            }
        }
    }

	private static void addProjectileClipping(boolean before, int x, int y, int height, int flag) {
        int regionX = x >> 3;
        int regionY = y >> 3;
        int regionId = ((regionX / 8) << 8) + (regionY / 8);
        getRegion(x, y).addShootable(x, y, height, flag);
    }

	private static void addClippingForSolidObject(boolean before, int x, int y, int height, int xLength, int yLength, boolean flag) {
        int clipping = 256;
        for (int i = x; i < x + xLength; i++) {
            for (int i2 = y; i2 < y + yLength; i2++) {
                if (flag) {
                    addProjectileClipping(before, i, i2, height, clipping);
                }
                addClipping(before, i, i2, height, clipping);
            }
        }
    }

	public static void addObject(boolean beforeLoad, int objectId, int x, int y, int height, int type, int direction) {
        ObjectDefinition def = ObjectDefinition.get(objectId);

        if (def == null) {
            return;
        }

        int xLength;
        int yLength;
        if (direction != 1 && direction != 3) {
            xLength = def.xLength();
            yLength = def.yLength();
        } else {
            xLength = def.yLength();
            yLength = def.xLength();
        }

        if (type == 22) {
            if (def.hasActions() && def.aBoolean779) {
                addClipping(beforeLoad, x, y, height, 0x200000);
            }
        } else if (type >= 9) {
            if (def.aBoolean779) {
                addClippingForSolidObject(beforeLoad, x, y, height, xLength, yLength, def.aBoolean757);
            }
        } else if (type >= 0 && type <= 3) {
            if (def.aBoolean779) {
                addClippingForVariableObject(beforeLoad, x, y, height, type, direction, def.aBoolean757);
            }
        }
    }

	public static int getClipping(int x, int y, int height) {
        Region r = getRegion(x, y);
        if (r == null) {
            int regionX = x >> 3;
            int regionY = y >> 3;
            int regionId = ((regionX / 8) << 8) + (regionY / 8);
            System.err.println("Clipmap: region " + regionId + " has no clipmap! @ " + x + "," + y + "," + height);
        }
        return r == null ? 0 : r.getClip(x, y, height);
    }

	/**
     * Is the path blocked in this direction
     * @param x
     * 			the x coordinate
     * @param y
     * 			the y coordinate
     * @param z
     * 			the z coordinate
     * @return
     * 			if the path is blocked in this direction
     */
    public static boolean blockedNorth(int x, int y, int z) {
        return (getClipping(x, y + 1, z) & 0x1280120) != 0 || getClipping(x, y + 1, z) == -1;
    }

	/**
     * Is the path blocked in this direction
     * @param x
     * 			the x coordinate
     * @param y
     * 			the y coordinate
     * @param z
     * 			the z coordinate
     * @return
     * 			if the path is blocked in this direction
     */
    public static boolean blockedEast(int x, int y, int z) {
        return (getClipping(x + 1, y, z) & 0x1280180) != 0 || getClipping(x + 1, y, z) == -1;
    }

	/**
     * Is the path blocked in this direction
     * @param x
     * 			the x coordinate
     * @param y
     * 			the y coordinate
     * @param z
     * 			the z coordinate
     * @return
     * 			if the path is blocked in this direction
     */
    public static boolean blockedSouth(int x, int y, int z) {
        return (getClipping(x, y - 1, z) & 0x1280102) != 0 || getClipping(x, y - 1, z) == -1;
    }

	/**
     * Is the path blocked in this direction
     * @param x
     * 			the x coordinate
     * @param y
     * 			the y coordinate
     * @param z
     * 			the z coordinate
     * @return
     * 			if the path is blocked in this direction
     */
    public static boolean blockedWest(int x, int y, int z) {
        return (getClipping(x - 1, y, z) & 0x1280108) != 0 || getClipping(x - 1, y, z) == -1;
    }

	/**
     * Is the path blocked in this direction
     * @param x
     * 			the x coordinate
     * @param y
     * 			the y coordinate
     * @param z
     * 			the z coordinate
     * @return
     * 			if the path is blocked in this direction
     */
    public static boolean blockedNorthEast(int x, int y, int z) {
        return (getClipping(x + 1, y + 1, z) & 0x12801e0) != 0 || getClipping(x + 1, y + 1, z) == -1;
    }

	/**
     * Is the path blocked in this direction
     * @param x
     * 			the x coordinate
     * @param y
     * 			the y coordinate
     * @param z
     * 			the z coordinate
     * @return
     * 			if the path is blocked in this direction
     */
    public static boolean blockedNorthWest(int x, int y, int z) {
        return (getClipping(x - 1, y + 1, z) & 0x1280138) != 0 || getClipping(x - 1, y + 1, z) == -1;
    }

	/**
     * Is the path blocked in this direction
     * @param x
     * 			the x coordinate
     * @param y
     * 			the y coordinate
     * @param z
     * 			the z coordinate
     * @return
     * 			if the path is blocked in this direction
     */
    public static boolean blockedSouthEast(int x, int y, int z) {
        return (getClipping(x + 1, y - 1, z) & 0x1280183) != 0 || getClipping(x + 1, y - 1, z) == -1;
    }

	/**
     * Is the path blocked in this direction
     * @param x
     * 			the x coordinate
     * @param y
     * 			the y coordinate
     * @param z
     * 			the z coordinate
     * @return
     * 			if the path is blocked in this direction
     */
    public static boolean blockedSouthWest(int x, int y, int z) {
        return (getClipping(x - 1, y - 1, z) & 0x128010e) != 0 || getClipping(x - 1, y - 1, z) == -1;
    }

	public static boolean canMove(int startX, int startY, int endX, int endY, int height, int xLength, int yLength) {
        int diffX = endX - startX;
        int diffY = endY - startY;
        int max = Math.max(Math.abs(diffX), Math.abs(diffY));
        for (int ii = 0; ii < max; ii++) {
            int currentX = endX - diffX;
            int currentY = endY - diffY;
            for (int i = 0; i < xLength; i++) {
                for (int i2 = 0; i2 < yLength; i2++)
                    if (diffX < 0 && diffY < 0) {
                        if ((getClipping((currentX + i) - 1, (currentY + i2) - 1, height) & 0x128010e) != 0
                                || (getClipping((currentX + i) - 1, currentY + i2, height) & 0x1280108) != 0
                                || (getClipping(currentX + i, (currentY + i2) - 1, height) & 0x1280102) != 0)
                            return false;
                    } else if (diffX > 0 && diffY > 0) {
                        if ((getClipping(currentX + i + 1, currentY + i2 + 1, height) & 0x12801e0) != 0
                                || (getClipping(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0
                                || (getClipping(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0)
                            return false;
                    } else if (diffX < 0 && diffY > 0) {
                        if ((getClipping((currentX + i) - 1, currentY + i2 + 1, height) & 0x1280138) != 0
                                || (getClipping((currentX + i) - 1, currentY + i2, height) & 0x1280108) != 0
                                || (getClipping(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0)
                            return false;
                    } else if (diffX > 0 && diffY < 0) {
                        if ((getClipping(currentX + i + 1, (currentY + i2) - 1, height) & 0x1280183) != 0
                                || (getClipping(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0
                                || (getClipping(currentX + i, (currentY + i2) - 1, height) & 0x1280102) != 0)
                            return false;
                    } else if (diffX > 0 && diffY == 0) {
                        if ((getClipping(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0)
                            return false;
                    } else if (diffX < 0 && diffY == 0) {
                        if ((getClipping((currentX + i) - 1, currentY + i2, height) & 0x1280108) != 0)
                            return false;
                    } else if (diffX == 0 && diffY > 0) {
                        if ((getClipping(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0)
                            return false;
                    } else if (diffX == 0 && diffY < 0
                            && (getClipping(currentX + i, (currentY + i2) - 1, height) & 0x1280102) != 0)
                        return false;

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

	public static boolean blockedShot(int x, int y, int z) {
        return (getClipping(x, y, z) & 0x20000) == 0;
    }

	public static int getShootable(int x, int y, int z) {
        Region region = getRegion(x, y);
        if (region == null)
            return 0;
        int regionAbsX = (region.id >> 8) << 6;
        int regionAbsY = (region.id & 0xff) << 6;

        if (z > 3) {
            z = z % 4;
        }

        if (region.shootable[z] == null) {
            return 0;
        }

        return region.shootable[z][x - regionAbsX][y - regionAbsY];
    }

	public static boolean getClipping(int x, int y, int height, int moveTypeX, int moveTypeY) {
        try {
            if (height > 3)
                height = 0;
            int checkX = (x + moveTypeX);
            int checkY = (y + moveTypeY);
            if (moveTypeX == -1 && moveTypeY == 0)
                return (getClipping(x, y, height) & 0x1280108) == 0;
            else if (moveTypeX == 1 && moveTypeY == 0)
                return (getClipping(x, y, height) & 0x1280180) == 0;
            else if (moveTypeX == 0 && moveTypeY == -1)
                return (getClipping(x, y, height) & 0x1280102) == 0;
            else if (moveTypeX == 0 && moveTypeY == 1)
                return (getClipping(x, y, height) & 0x1280120) == 0;
            else if (moveTypeX == -1 && moveTypeY == -1)
                return ((getClipping(x, y, height) & 0x128010e) == 0
                        && (getClipping(checkX - 1, checkY, height) & 0x1280108) == 0
                        && (getClipping(checkX - 1, checkY, height) & 0x1280102) == 0);
            else if (moveTypeX == 1 && moveTypeY == -1)
                return ((getClipping(x, y, height) & 0x1280183) == 0
                        && (getClipping(checkX + 1, checkY, height) & 0x1280180) == 0
                        && (getClipping(checkX, checkY - 1, height) & 0x1280102) == 0);
            else if (moveTypeX == -1 && moveTypeY == 1)
                return ((getClipping(x, y, height) & 0x1280138) == 0
                        && (getClipping(checkX - 1, checkY, height) & 0x1280108) == 0
                        && (getClipping(checkX, checkY + 1, height) & 0x1280120) == 0);
            else if (moveTypeX == 1 && moveTypeY == 1)
                return ((getClipping(x, y, height) & 0x12801e0) == 0
                        && (getClipping(checkX + 1, checkY, height) & 0x1280180) == 0
                        && (getClipping(checkX, checkY + 1, height) & 0x1280120) == 0);
            else {

                return false;
            }
        } catch (Exception e) {

            return true;
        }
    }

	public static boolean isBlocked(int x, int y, int z, Direction direction) {
        final int size = 0;
        switch (direction) {
        case NORTH:
            return (getClipping(x, y + size, z) & direction.getClipMask()) != 0;
        case EAST:
            return (getClipping(x + size, y, z) & direction.getClipMask()) != 0;
        case SOUTH:
            return (getClipping(x, y - size, z) & direction.getClipMask()) != 0;
        case WEST:
            return (getClipping(x - size, y, z) & direction.getClipMask()) != 0;
        case NORTH_EAST:
            return (getClipping(x + size, y + size, z) & direction.getClipMask()) != 0;
        case NORTH_WEST:
            return (getClipping(x - size, y + size, z) & direction.getClipMask()) != 0;
        case SOUTH_EAST:
            return (getClipping(x + size, y - size, z) & direction.getClipMask()) != 0;
        case SOUTH_WEST:
            return (getClipping(x - size, y - size, z) & direction.getClipMask()) != 0;
        default:
            return true;
        }
    }

	public static boolean shotBlockedNorth(int x, int y, int z) {
        return (getShootable(x, y + 1, z) & 0x1280120) != 0;
    }

	public static boolean shotBlockedEast(int x, int y, int z) {
        return (getShootable(x + 1, y, z) & 0x1280180) != 0;
    }

	public static boolean shotBlockedSouth(int x, int y, int z) {
        return (getShootable(x, y - 1, z) & 0x1280102) != 0;
    }

	public static boolean shotBlockedWest(int x, int y, int z) {
        return (getShootable(x - 1, y, z) & 0x1280108) != 0;
    }

	public static boolean shotBlockedNorthEast(int x, int y, int z) {
        return (getShootable(x + 1, y + 1, z) & 0x12801e0) != 0;
    }

	public static boolean shotBlockedNorthWest(int x, int y, int z) {
        return (getShootable(x - 1, y + 1, z) & 0x1280138) != 0;
    }

	public static boolean shotBlockedSouthEast(int x, int y, int z) {
        return (getShootable(x + 1, y - 1, z) & 0x1280183) != 0;
    }

	public static boolean shotBlockedSouthWest(int x, int y, int z) {
        return (getShootable(x - 1, y - 1, z) & 0x128010e) != 0;
    }

	/**
     * Tells you if this direction is walkable.
     *
     * @param location
     * 			the current location
     * @param direction
     *            the direction.
     * @return if the direction is walkable.
     */
    public static boolean canMove(Location location, int direction) {
        int x = location.getX();
        int y = location.getY();
        int z = location.getZ();
        if (direction == 0) {
            return !blockedNorthWest(x, y, z) && !blockedNorth(x, y, z) && !blockedWest(x, y, z);
        } else if (direction == 1) {
            return !blockedNorth(x, y, z);
        } else if (direction == 2) {
            return !blockedNorthEast(x, y, z) && !blockedNorth(x, y, z) && !blockedEast(x, y, z);
        } else if (direction == 3) {
            return !blockedWest(x, y, z);
        } else if (direction == 4) {
            return !blockedEast(x, y, z);
        } else if (direction == 5) {
            return !blockedSouthWest(x, y, z) && !blockedSouth(x, y, z) && !blockedWest(x, y, z);
        } else if (direction == 6) {
            return !blockedSouth(x, y, z);
        } else if (direction == 7) {
            return !blockedSouthEast(x, y, z) && !blockedSouth(x, y, z) && !blockedEast(x, y, z);
        }
        return false;
    }

	public static boolean canShoot(Location location, int direction) {
        int x = location.getX();
        int y = location.getY();
        int z = location.getZ();
        if (direction == 0) {
            return !shotBlockedNorthWest(x, y, z) && !shotBlockedNorth(x, y, z)
                    && !shotBlockedWest(x, y, z);
        } else if (direction == 1) {
            return !shotBlockedNorth(x, y, z);
        } else if (direction == 2) {
            return !shotBlockedNorthEast(x, y, z) && !shotBlockedNorth(x, y, z)
                    && !shotBlockedEast(x, y, z);
        } else if (direction == 3) {
            return !shotBlockedWest(x, y, z);
        } else if (direction == 4) {
            return !shotBlockedEast(x, y, z);
        } else if (direction == 5) {
            return !shotBlockedSouthWest(x, y, z) && !shotBlockedSouth(x, y, z)
                    && !shotBlockedWest(x, y, z);
        } else if (direction == 6) {
            return !shotBlockedSouth(x, y, z);
        } else if (direction == 7) {
            return !shotBlockedSouthEast(x, y, z) && !shotBlockedSouth(x, y, z)
                    && !shotBlockedEast(x, y, z);
        }
        return false;
    }

	public void addClip(int x, int y, int height, int shift) {
		int regionAbsX = (id >> 8) << 6;
		int regionAbsY = (id & 0xff) << 6;

		if (clips[height] == null) {
			clips[height] = new int[64][64];

			for (int z = 0; z < 4; z++) {
				for (int x2 = 0; x < 64; x++) {
					for (int y2 = 0; y < 64; y++) {
						clips[z][x2][y2] = -1;
					}
				}
			}
		}

		if (clips[height][x - regionAbsX][y - regionAbsY] == -1) {
			clips[height][x - regionAbsX][y - regionAbsY] = 0;
		}

		clips[height][x - regionAbsX][y - regionAbsY] += shift;
	}

	public void addShootable(int x, int y, int z, int flag) {
		int regionAbsX = (id >> 8) << 6;
		int regionAbsY = (id & 0xff) << 6;

		if (shootable[z] == null) {
			shootable[z] = new int[64][64];
		}

		shootable[z][x - regionAbsX][y - regionAbsY] |= flag;
	}

	public int getClip(int x, int y, int z) {
		int regionAbsX = (id >> 8) << 6;
		int regionAbsY = (id & 0xff) << 6;
		if (clips[z] == null) {
			return 0;
		}
		return clips[z][x - regionAbsX][y - regionAbsY];
	}

	/**
	 * Fetches a region after they are sorted by id.
	 * @return the region.
	 */
	public static Region getRegion(int x, int y) {
		if (regions == null) {
			System.err.println("You haven't loading region clipping.");
			return null;
		}
		int regionX = x >> 3;
		int regionY = y >> 3;
		int regionId = (regionX / 8 << 8) + regionY / 8;
		Region r = regions.get(regionId);
		if (r == null) {
			r = new Region(regionId);
			regions.put(regionId, r);
		}
		return r;
	}

	/**
	 * Tells you if this direction is walkable.
	 * @return if the direction is walkable.
	 */
	public boolean canMove(int x, int y, int z, int direction) {
		if (direction == 0) {
			return !Region.blockedNorthWest(x, y, z) && !Region.blockedNorth(x, y, z) && !Region.blockedWest(x, y, z);
		} else if (direction == 1) {
			return !Region.blockedNorth(x, y, z);
		} else if (direction == 2) {
			return !Region.blockedNorthEast(x, y, z) && !Region.blockedNorth(x, y, z) && !Region.blockedEast(x, y, z);
		} else if (direction == 3) {
			return !Region.blockedWest(x, y, z);
		} else if (direction == 4) {
			return !Region.blockedEast(x, y, z);
		} else if (direction == 5) {
			return !Region.blockedSouthWest(x, y, z) && !Region.blockedSouth(x, y, z) && !Region.blockedWest(x, y, z);
		} else if (direction == 6) {
			return !Region.blockedSouth(x, y, z);
		} else if (direction == 7) {
			return !Region.blockedSouthEast(x, y, z) && !Region.blockedSouth(x, y, z) && !Region.blockedEast(x, y, z);
		}
		return false;
	}
	
	/**
	 * Tells you if this direction is shootable.
	 * @return if the direction is walkable.
	 */
	public boolean canShoot(int x, int y, int z, int direction) {
		if (direction == 0) {
			return !Region.shotBlockedWest(x, y, z) && !Region.shotBlockedNorth(x, y, z) && !Region.shotBlockedWest(x, y, z);
		} else if (direction == 1) {
			return !Region.shotBlockedNorth(x, y, z);
		} else if (direction == 2) {
			return !Region.shotBlockedNorthEast(x, y, z) && !Region.shotBlockedNorth(x, y, z) && !Region.shotBlockedEast(x, y, z);
		} else if (direction == 3) {
			return !Region.shotBlockedWest(x, y, z);
		} else if (direction == 4) {
			return !Region.shotBlockedEast(x, y, z);
		} else if (direction == 5) {
			return !Region.shotBlockedSouthWest(x, y, z) && !Region.shotBlockedSouth(x, y, z) && !Region.shotBlockedWest(x, y, z);
		} else if (direction == 6) {
			return !Region.shotBlockedSouth(x, y, z);
		} else if (direction == 7) {
			return !Region.shotBlockedSouthEast(x, y, z) && !Region.shotBlockedSouth(x, y, z) && !Region.shotBlockedEast(x, y, z);
		}
		return false;
	}

	public RegionEntities getStore() {
		return store;
	}
}