package com.model.game.character.pathfinder.region;

import com.model.utility.cache.map.Tile;

public class ProjectileClipping {
	
	public static final int REGION_SIZE = 128;

	public static final int MAX_MAP_X = 16383, MAX_MAP_Y = 16383;

	private static ProjectileClipping[][] regions = new ProjectileClipping[(MAX_MAP_X + 1) / REGION_SIZE][(MAX_MAP_Y + 1) / REGION_SIZE];
	
	private static int[] rangables = {
		23265, 23266, 23263, 23273, 23262, 23272, 23261, 23264, 23267, 23268, 9374,
		17473,
	};
	
	public static int hash(int x, int y) {
		return x >> 7 << 8 | y >> 7;
	}

	public static void addClipping(int x, int y, int z, int shift) {
		ProjectileClipping region = forCoords(x, y);
		int localX = x - ((x >> 7) << 7);
		int localY = y - ((y >> 7) << 7);
		if (z > 3) {
			z = 0;
		}
		//add clipping support for different height areas
		if (region.clippingMasks[z] == null) {
			region.clippingMasks[z] = new int[region.size][region.size];
		}
		region.clipped = true;
		region.clippingMasks[z][localX][localY] |= shift;
	}

	public static void setClippingMask(int x, int y, int z, int shift) {
		ProjectileClipping region = forCoords(x, y);
		int localX = x - ((x >> 7) << 7);
		int localY = y - ((y >> 7) << 7);
		if (z > 3) {
			z = 0;
		}
		//add clipping check for different height area
		if (region.clippingMasks[z] == null) {
			region.clippingMasks[z] = new int[region.size][region.size];
		}
		region.clipped = true;
		region.clippingMasks[z][localX][localY] = shift;
	}

	public static void removeClipping(int x, int y, int z, int shift) {
		ProjectileClipping region = forCoords(x, y);
		int localX = x - ((x >> 7) << 7);
		int localY = y - ((y >> 7) << 7);
		if (z > 3) {
			z = 0;
		}
		//add clipping check for different height area
		if (region.clippingMasks[z] == null) {
			region.clippingMasks[z] = new int[region.size][region.size];
		}
		region.clippingMasks[z][localX][localY] &= ~shift;
	}

	public static ProjectileClipping forCoords(int x, int y) {
		int regionX = x >> 7, regionY = y >> 7;
		ProjectileClipping r = regions[regionX][regionY];
		if (r == null) {
			r = regions[regionX][regionY] = new ProjectileClipping(regionX, regionY, REGION_SIZE);
		}
		return r;
	}

	public static ProjectileClipping forLocation(Tile other) {
		return forCoords(other.getX(), other.getY());
	}
	
	public static int getClippingMask(Tile loc) {
		ProjectileClipping region = forCoords(loc.getX(), loc.getY());
		int z = loc.getZ();
		if (z > 3) {
			z = 0;
		}
		//add clipping check for different height area
		if (region.clippingMasks[z] == null || !region.clipped) {
			return -1;
		}
		int localX = loc.getX() - ((loc.getX() >> 7) << 7);
		int localY = loc.getY() - ((loc.getY() >> 7) << 7);
		return region.clippingMasks[z][localX][localY];
	}

	public static int getClippingMask(int absx, int absy, int z) {
		ProjectileClipping region = forCoords(absx, absy);
		if (z > 3) {
			z = 0;
		}
		//add clipping check for different height area
		if (region.clippingMasks[z] == null || !region.clipped) {
			return -1;
		}
		int localX = absx - ((absx >> 7) << 7);
		int localY = absy - ((absy >> 7) << 7);
		return region.clippingMasks[z][localX][localY];
	}
	
	public static boolean getClippingMask(int x, int y, int z, int moveTypeX, int moveTypeY, boolean debug) {
		try {
			if (z > 3) {
				z = 0;
			}
			//add clipping check for different height area
			if (moveTypeX != 0 && moveTypeY != 0) {
				if (moveTypeX == 1 && moveTypeY == 1) {
					return getClippingMask(x, y, z, 0, 1, debug) && getClippingMask(x, y, z, 1, 0, debug) && getClippingMask(x - 1, y - 1, z, 0, -1, debug) && getClippingMask(x - 1, y - 1, z, -1, 0, debug);
				} else if (moveTypeX == -1 && moveTypeY == -1) {
					return getClippingMask(x, y, z, 0, -1, debug) && getClippingMask(x, y, z, -1, 0, debug) && getClippingMask(x + 1, y + 1, z, 0, 1, debug) && getClippingMask(x + 1, y + 1, z, 1, 0, debug);
				} else if (moveTypeX == 1 && moveTypeY == -1) {
					return getClippingMask(x, y, z, 0, -1, debug) && getClippingMask(x, y, z, 1, 0, debug) && getClippingMask(x - 1, y + 1, z, 0, 1, debug) && getClippingMask(x - 1, y + 1, z, -1, 0, debug);
				} else if (moveTypeX == -1 && moveTypeY == 1) {
					return getClippingMask(x, y, z, 0, 1, debug) && getClippingMask(x, y, z, -1, 0, debug) && getClippingMask(x + 1, y - 1, z, 0, -1, debug) && getClippingMask(x + 1, y - 1, z, 1, 0, debug);
				} else {
					return false;
				}
			} else if (moveTypeX == -1 && moveTypeY == 0) {
				return (getClippingMask(x, y, z) & 0x42240000) == 0;
			} else if (moveTypeX == 1 && moveTypeY == 0) {
				return (getClippingMask(x, y, z) & 0x60240000) == 0;
			} else if (moveTypeX == 0 && moveTypeY == -1) {
				return (getClippingMask(x, y, z) & 0x40a40000) == 0;
			} else if (moveTypeX == 0 && moveTypeY == 1) {
				return (getClippingMask(x, y, z) & 0x48240000) == 0;
			} else {
				return false;
			}
		} catch (Exception e) {
			return true;
		}
	}

	public static ProjectileClipping getRegion(int x, int y, int z) {
		return forCoords(x, y);
	}
	
	private int[][][] clippingMasks = new int[4][][];
	private int size;
	private int x;
	private int y;

	private boolean clipped;

	public ProjectileClipping(int x, int y, int size) {
		this.x = x;
		this.y = y;
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		return x << 8 | y;
	}

	public void setClipped(boolean clipped) {
		this.clipped = clipped;
	}

	public boolean isClipped() {
		return clipped;
	}

	// TODO
	/*public static void addClipping(GameObject obj) {
		ObjectDefinition def = obj.getDefinition();
		if (def.getName().equalsIgnoreCase("tree stump") || def.getName().equalsIgnoreCase("anvil") || obj.getId() == 83) {
			return;
		}
		if (def.getId() == 23271) {
			return;
		}
		if (def.isRangeable()) {
			return;
		}
		int x = obj.getPosition().getX();
		int y = obj.getPosition().getY();
		int height = obj.getPosition().getZ();
		int xLength;
		int yLength;
		if (obj.getDirection() != 1 && obj.getDirection()  != 3) {
			xLength = def.getSizeX();
			yLength = def.getSizeY();
		} else {
			xLength = def.getSizeY();
			yLength = def.getSizeX();
		}
		int type = obj.getType();
		if (type == 22) {
			for (int i = 0 ; i < rangables.length; i++) {
				if (def.getId() == rangables[i]) {
					return;
				}
			}
			if (def.getClippingFlag() == 1) {
				addClipping(x, y, height, 0x200000);
			}
		} else if (type >= 9 && type <= 11) {
			if (def.getClippingFlag() != 0) {
				addClippingForSolidObject(x, y, height, xLength, yLength, def.isSolid(), def.isSolid());
			}
		} else if (type >= 0 && type <= 3) {
			if (def.getClippingFlag() != 0) {
				addClippingForVariableObject(x, y, height, type, obj.getDirection(), def.isSolid(), def.isSolid());
			}
		}
	}

	public static void removeClipping(GameObject obj) {
		ObjectDefinition def = obj.getDefinition();
		int xLength;
		int yLength;
		int x = obj.getPosition().getX();
		int y = obj.getPosition().getY();
		int height = obj.getPosition().getZ();
		if (obj.getDirection() != 1 && obj.getDirection() != 3) {
			xLength = def.getSizeX();
			yLength = def.getSizeY();
		} else {
			xLength = def.getSizeY();
			yLength = def.getSizeX();
		}
		if (obj.getType() == 22) {
			if (def.getClippingFlag() == 1) {
				removeClipping(x, y, height, 0x200000);
			}
		} else if (obj.getType() >= 9 && obj.getType() <= 11) {
			if (def.getClippingFlag() != 0) {
				removeClippingForSolidObject(x, y, height, xLength, yLength, def.isSolid(), def.isSolid());
			}
		} else if (obj.getType() >= 0 && obj.getType() <= 3) {
			if (def.getClippingFlag() != 0) {
				removeClippingForVariableObject(x, y, height, obj.getType(), obj.getDirection(), def.isSolid(), def.isSolid());
			}
		}
	}*/
	
	private static void addClippingForSolidObject(int x, int y, int height, int xLength, int yLength, boolean flag, boolean projectileBlocked) {
		int clipping = 256;
		if (flag) {
			clipping |= 131072;
		}
		if(projectileBlocked) {
			clipping |= 1073741824;
		}
		for (int i = x; i < x + xLength; i++) {
			for (int i2 = y; i2 < y + yLength; i2++) {
				addClipping(i, i2, height, clipping);
			}
		}
	}

	private static void removeClippingForSolidObject(int x, int y, int height, int xLength, int yLength, boolean flag, boolean projectileBlocked) {
		int clipping = 256;
		if (flag) {
			clipping |= 131072;
		}
		if(projectileBlocked) {
			clipping |= 1073741824;
		}
		for (int i = x; i < x + xLength; i++) {
			for (int i2 = y; i2 < y + yLength; i2++) {
				removeClipping(i, i2, height, clipping);
			}
		}
	}

	private static void addClippingForVariableObject(int x, int y, int z, int type, int direction, boolean flag, boolean projectileBlocked) {
		if (type == 0) {
			if (direction == 0) {
				addClipping(x, y, z, 128);
				addClipping(x - 1, y, z, 8);
			}
			if (direction == 1) {
				addClipping(x, y, z, 2);
				addClipping(x, y + 1, z, 32);
			}
			if (direction == 2) {
				addClipping(x, y, z, 8);
				addClipping(x + 1, y, z, 128);
			}
			if (direction == 3) {
				addClipping(x, y, z, 32);
				addClipping(x, y - 1, z, 2);
			}
		}
		if (type == 1 || type == 3) {
			if (direction == 0) {
				addClipping(x, y, z, 1);
				addClipping(x - 1, y + 1, z, 16);
			}
			if (direction == 1) {
				addClipping(x, y, z, 4);
				addClipping(x + 1, y + 1, z, 64);
			}
			if (direction == 2) {
				addClipping(x, y, z, 16);
				addClipping(x + 1, y - 1, z, 1);
			}
			if (direction == 3) {
				addClipping(x, y, z, 64);
				addClipping(x - 1, y - 1, z, 4);
			}
		}
		if (type == 2) {
			if (direction == 0) {
				addClipping(x, y, z, 130);
				addClipping(x - 1, y, z, 8);
				addClipping(x, y + 1, z, 32);
			}
			if (direction == 1) {
				addClipping(x, y, z, 10);
				addClipping(x, y + 1, z, 32);
				addClipping(x + 1, y, z, 128);
			}
			if (direction == 2) {
				addClipping(x, y, z, 40);
				addClipping(x + 1, y, z, 128);
				addClipping(x, y - 1, z, 2);
			}
			if (direction == 3) {
				addClipping(x, y, z, 160);
				addClipping(x, y - 1, z, 2);
				addClipping(x - 1, y, z, 8);
			}
		}
		if (flag) {
			if (type == 0) {
				if (direction == 0) {
					addClipping(x, y, z, 0x10000);
					addClipping(x - 1, y, z, 4096);
				}
				if (direction == 1) {
					addClipping(x, y, z, 1024);
					addClipping(x, y + 1, z, 16384);
				}
				if (direction == 2) {
					addClipping(x, y, z, 4096);
					addClipping(x + 1, y, z, 0x10000);
				}
				if (direction == 3) {
					addClipping(x, y, z, 16384);
					addClipping(x, y - 1, z, 1024);
				}
			}
			if (type == 1 || type == 3) {
				if (direction == 0) {
					addClipping(x, y, z, 512);
					addClipping(x - 1, y + 1, z, 8192);
				}
				if (direction == 1) {
					addClipping(x, y, z, 2048);
					addClipping(x + 1, y + 1, z, 32768);
				}
				if (direction == 2) {
					addClipping(x, y, z, 8192);
					addClipping(x + 1, y - 1, z, 512);
				}
				if (direction == 3) {
					addClipping(x, y, z, 32768);
					addClipping(x - 1, y - 1, z, 2048);
				}
			}
			if (type == 2) {
				if (direction == 0) {
					addClipping(x, y, z, 0x10400);
					addClipping(x - 1, y, z, 4096);
					addClipping(x, y + 1, z, 16384);
				}
				if (direction == 1) {
					addClipping(x, y, z, 5120);
					addClipping(x, y + 1, z, 16384);
					addClipping(x + 1, y, z, 0x10000);
				}
				if (direction == 2) {
					addClipping(x, y, z, 20480);
					addClipping(x + 1, y, z, 0x10000);
					addClipping(x, y - 1, z, 1024);
				}
				if (direction == 3) {
					addClipping(x, y, z, 0x14000);
					addClipping(x, y - 1, z, 1024);
					addClipping(x - 1, y, z, 4096);
				}
			}
		}
		if(projectileBlocked) {
			if (type == 0) {
				if (direction == 0) {
					addClipping(x, y, z, 0x20000000);
					addClipping(x - 1, y, z, 0x2000000);
				}
				if (direction == 1) {
					addClipping(x, y, z, 0x800000);
					addClipping(x, y + 1, z, 0x8000000);
				}
				if (direction == 2) {
					addClipping(x, y, z, 0x2000000);
					addClipping(x + 1, y, z, 0x20000000);
				}
				if (direction == 3) {
					addClipping(x, y, z, 0x8000000);
					addClipping(x, y - 1, z, 0x800000);
				}
			}
			if (type == 1 || type == 3) {
				if (direction == 0) {
					addClipping(x, y, z, 0x400000);
					addClipping(x - 1, y + 1, z, 0x4000000);
				}
				if (direction == 1) {
					addClipping(x, y, z, 0x1000000);
					addClipping(1 + x, 1 + y, z, 0x10000000);
				}
				if (direction == 2) {
					addClipping(x, y, z, 0x4000000);
					addClipping(x + 1, -1 + y, z, 0x400000);
				}
				if (direction == 3) {
					addClipping(x, y, z, 0x10000000);
					addClipping(-1 + x, y - 1, z, 0x1000000);
				}
			}
			if (type == 2) {
				if (direction == 0) {
					addClipping(x, y, z, 0x20800000);
					addClipping(-1 + x, y, z, 0x2000000);
					addClipping(x, 1 + y, z, 0x8000000);
				}
				if (direction == 1) {
					addClipping(x, y, z, 0x2800000);
					addClipping(x, 1 + y, z, 0x8000000);
					addClipping(x + 1, y, z, 0x20000000);
				}
				if (direction == 2) {
					addClipping(x, y, z, 0xa000000);
					addClipping(1 + x, y, z, 0x20000000);
					addClipping(x, y - 1, z, 0x800000);
				}
				if (direction == 3) {
					addClipping(x, y, z, 0x28000000);
					addClipping(x, y - 1, z, 0x800000);
					addClipping(-1 + x, y, z, 0x2000000);
				}
			}
        }
	}

	public static void removeClippingForVariableObject(int x, int y, int z, int type, int direction, boolean flag, boolean projectileBlocked) {
		if (type == 0) {
			if (direction == 0) {
				removeClipping(x, y, z, 128);
				removeClipping(x - 1, y, z, 8);
			}
			if (direction == 1) {
				removeClipping(x, y, z, 2);
				removeClipping(x, 1 + y, z, 32);
			}
			if (direction == 2) {
				removeClipping(x, y, z, 8);
				removeClipping(1 + x, y, z, 128);
			}
			if (direction == 3) {
				removeClipping(x, y, z, 32);
				removeClipping(x, y - 1, z, 2);
			}
		}
		if (type == 1 || type == 3) {
			if (direction == 0) {
				removeClipping(x, y, z, 1);
				removeClipping(x - 1, 1 + y, z, 16);
			}
			if (direction == 1) {
				removeClipping(x, y, z, 4);
				removeClipping(1 + x, y + 1, z, 64);
			}
			if (direction == 2) {
				removeClipping(x, y, z, 16);
				removeClipping(x + 1, -1 + y, z, 1);
			}
			if (direction == 3) {
				removeClipping(x, y, z, 64);
				removeClipping(-1 + x, -1 + y, z, 4);
			}
		}
		if (type == 2) {
			if (direction == 0) {
				removeClipping(x, y, z, 130);
				removeClipping(x - 1, y, z, 8);
				removeClipping(x, 1 + y, z, 32);
			}
			if (direction == 1) {
				removeClipping(x, y, z, 10);
				removeClipping(x, 1 + y, z, 32);
				removeClipping(1 + x, y, z, 128);
			}
			if (direction == 2) {
				removeClipping(x, y, z, 40);
				removeClipping(x + 1, y, z, 128);
				removeClipping(x, -1 + y, z, 2);
			}
			if (direction == 3) {
				removeClipping(x, y, z, 160);
				removeClipping(x, y - 1, z, 2);
				removeClipping(-1 + x, y, z, 8);
			}
		}
		if (flag) {
			if (type == 0) {
				if (direction == 0) {
					removeClipping(x, y, z, 0x10000);
					removeClipping(-1 + x, y, z, 4096);
				}
				if (direction == 1) {
					removeClipping(x, y, z, 1024);
					removeClipping(x, 1 + y, z, 16384);
				}
				if (direction == 2) {
					removeClipping(x, y, z, 4096);
					removeClipping(x + 1, y, z, 0x10000);
				}
				if (direction == 3) {
					removeClipping(x, y, z, 16384);
					removeClipping(x, y - 1, z, 1024);
				}
			}
			if (type == 1 || type == 3) {
				if (direction == 0) {
					removeClipping(x, y, z, 512);
					removeClipping(-1 + x, 1 + y, z, 8192);
				}
				if (direction == 1) {
					removeClipping(x, y, z, 2048);
					removeClipping(1 + x, 1 + y, z, 32768);
				}
				if (direction == 2) {
					removeClipping(x, y, z, 8192);
					removeClipping(x + 1, -1 + y, z, 512);
				}
				if (direction == 3) {
					removeClipping(x, y, z, 32768);
					removeClipping(x - 1, -1 + y, z, 2048);
				}
			}
			if (type == 2) {
				if (direction == 0) {
					removeClipping(x, y, z, 0x10400);
					removeClipping(-1 + x, y, z, 4096);
					removeClipping(x, y + 1, z, 16384);
				}
				if (direction == 1) {
					removeClipping(x, y, z, 5120);
					removeClipping(x, 1 + y, z, 16384);
					removeClipping(x + 1, y, z, 0x10000);
				}
				if (direction == 2) {
					removeClipping(x, y, z, 20480);
					removeClipping(1 + x, y, z, 0x10000);
					removeClipping(x, -1 + y, z, 1024);
				}
				if (direction == 3) {
					removeClipping(x, y, z, 0x14000);
					removeClipping(x, -1 + y, z, 1024);
					removeClipping(-1 + x, y, z, 4096);
				}
			}
		}
		if(projectileBlocked) {
			if (type == 0) {
				if (direction == 0) {
					removeClipping(x, y, z, 0x20000000);
					removeClipping(x - 1, y, z, 0x2000000);
				}
				if (direction == 1) {
					removeClipping(x, y, z, 0x800000);
					removeClipping(x, y + 1, z, 0x8000000);
				}
				if (direction == 2) {
					removeClipping(x, y, z, 0x2000000);
					removeClipping(x + 1, y, z, 0x20000000);
				}
				if (direction == 3) {
					removeClipping(x, y, z, 0x8000000);
					removeClipping(x, y - 1, z, 0x800000);
				}
			}
			if (type == 1 || type == 3) {
				if (direction == 0) {
					removeClipping(x, y, z, 0x400000);
					removeClipping(x - 1, y + 1, z, 0x4000000);
				}
				if (direction == 1) {
					removeClipping(x, y, z, 0x1000000);
					removeClipping(1 + x, 1 + y, z, 0x10000000);
				}
				if (direction == 2) {
					removeClipping(x, y, z, 0x4000000);
					removeClipping(x + 1, -1 + y, z, 0x400000);
				}
				if (direction == 3) {
					removeClipping(x, y, z, 0x10000000);
					removeClipping(-1 + x, y - 1, z, 0x1000000);
				}
			}
			if (type == 2) {
				if (direction == 0) {
					removeClipping(x, y, z, 0x20800000);
					removeClipping(-1 + x, y, z, 0x2000000);
					removeClipping(x, 1 + y, z, 0x8000000);
				}
				if (direction == 1) {
					removeClipping(x, y, z, 0x2800000);
					removeClipping(x, 1 + y, z, 0x8000000);
					removeClipping(x + 1, y, z, 0x20000000);
				}
				if (direction == 2) {
					removeClipping(x, y, z, 0xa000000);
					removeClipping(1 + x, y, z, 0x20000000);
					removeClipping(x, y - 1, z, 0x800000);
				}
				if (direction == 3) {
					removeClipping(x, y, z, 0x28000000);
					removeClipping(x, y - 1, z, 0x800000);
					removeClipping(-1 + x, y, z, 0x2000000);
				}
			}
        }
	}

	public int getClippingFlag(int localX, int localY, int height) {
		return clippingMasks[localX][localY][height];
	}

}
