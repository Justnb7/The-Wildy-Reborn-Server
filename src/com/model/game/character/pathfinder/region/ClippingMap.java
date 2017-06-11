package com.model.game.character.pathfinder.region;

public class ClippingMap {

	public static final int MAP_SIZE = 32;

	private int[][][] flags = new int[4][MAP_SIZE][MAP_SIZE];

	private RegionCoordinates coords;

	public ClippingMap(RegionCoordinates coords) {
		this.coords = coords;
	}

	public int getFlag(int x, int y, int z) {
		return flags[z][x][y];
	}
	
	public static final int REGION_SIZE = 128;

	public static final int MAX_MAP_X = 16383, MAX_MAP_Y = 16383;

	
	public void addClipping(int x, int y, int z, int flag) {
		if (z < 0 || z >= flags.length) {
			return;
		}
		if (x < 0 || x >= MAP_SIZE || y < 0 || y >= MAP_SIZE) {
			int mapX = coords.getX(), mapY = coords.getY();
			if (x < 0) {
				mapX--;
				x = MAP_SIZE + x;
			} else if (x >= MAP_SIZE) {
				mapX++;
				x = x - MAP_SIZE;
			}
			if (y < 0) {
				mapY--;
				y = MAP_SIZE + y;
			} else if (y >= MAP_SIZE) {
				mapY++;
				y = y - MAP_SIZE;
			}
			RegionManager.get().getClippingMap(mapX, mapY, true).addClipping(x, y, z, flag);
		} else {
			flags[z][x][y] |= flag;
		}
	}

	private void removeClipping(int x, int y, int z, int flag) {
		if (x < 0 || x >= MAP_SIZE || y < 0 || y >= MAP_SIZE) {
			int mapX = coords.getX(), mapY = coords.getY();
			if (x < 0) {
				mapX--;
				x = MAP_SIZE - x;
			} else if (x >= MAP_SIZE) {
				mapX++;
				x = x - MAP_SIZE;
			}
			if (y < 0) {
				mapY--;
				y = MAP_SIZE - y;
			} else if (y >= MAP_SIZE) {
				mapY++;
				y = y - MAP_SIZE;
			}
			RegionManager.get().getClippingMap(mapX, mapY, true).removeClipping(x, y, z, flag);
		} else {
			flags[z][x][y] &= ~flag;
		}
	}

	// TODO
	/*public void addObject(GameObject obj) {
		if (obj == null) {
			return;
		}
		ObjectDefinition def = obj.getDefinition();
		if (def == null) {
			return;
		}
		int type = obj.getType();
		int x = obj.getPosition().getX(), y = obj.getPosition().getY(), z = obj.getPosition().getZ();

		x = x % MAP_SIZE;
		y = y % MAP_SIZE;

		int xLength = def.getSizeX(), yLength = def.getSizeY();
		int direction = obj.getDirection();
		if (type >= 0 && type <= 3) {
			if (def.getClippingFlag() != 0) {
				addClippingForVariableObject(x, y, z, type, direction, def.isSolid());
			}
		} else if (type == 9 || type == 10 || type == 11) {
			if (def.getClippingFlag() != 0)
				addClippingForSolidObject(x, y, z, xLength, yLength, direction, def.isSolid());
		} else if (type == 22) {
			if (def.getClippingFlag() == 1) {
				addClipping(x, y, z, 262144);
			}
		}
	}

	public void removeObject(GameObject obj) {
		if (obj == null) {
			return;
		}
		ObjectDefinition def = obj.getDefinition();
		if (def == null) {
			return;
		}
		int x = obj.getPosition().getX(), y = obj.getPosition().getY(), z = obj.getPosition().getZ();
		x = x % MAP_SIZE;
		y = y % MAP_SIZE;
		int xLength = def.getSizeX(), yLength = def.getSizeY();
		if (obj.getDirection() == 1 && obj.getDirection() == 3) {
			xLength = def.getSizeY();
			yLength = def.getSizeX();
		}
		if (obj.getType() == 22) {
			if (def.getClippingFlag() == 1) {
				removeClipping(x, y, z, 262144);
			}
		} else if (obj.getType() >= 9 && obj.getType() <= 11) {
			if (def.getClippingFlag() != 0) {
				removeClippingForSolidObject(x, y, z, xLength, yLength, def.isSolid());
			}
		} else if (obj.getType() >= 0 && obj.getType() <= 3) {
			if (def.getClippingFlag() != 0) {
				removeClippingForVariableObject(x, y, z, obj.getType(), obj.getDirection(), def.isSolid());
			}
		}
	}*/

	private void addClippingForSolidObject(int objectX, int objectY, int objectZ, int xSize, int ySize, int direction, boolean flag) {
		int clipping = 256;
		if (flag) {
			clipping += 131072;
		}

		if (direction == 1 || direction == 3) {
			int originaly = ySize;
			ySize = xSize;
			xSize = originaly;
		}

		for (int x = objectX; x < objectX + xSize; x++) {
			for (int y = objectY; y < objectY + ySize; y++) {
				addClipping(x, y, objectZ, clipping);
			}
		}
	}

	private void removeClippingForSolidObject(int objectX, int objectY, int objectZ, int xSize, int ySize, boolean flag) {
		int clipping = 256;
		if (flag) {
			clipping += 131072;
		}
		for (int x = objectX; x < objectX + xSize; x++) {
			for (int y = objectY; y < objectY + ySize; y++) {
				removeClipping(x, y, objectZ, clipping);
			}
		}
	}

	private void addClippingForVariableObject(int x, int y, int z, int type, int direction, boolean flag) {
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
					addClipping(x, y, z, 65536);
					addClipping(x - 1, y, z, 4096);
				}
				if (direction == 1) {
					addClipping(x, y, z, 1024);
					addClipping(x, y + 1, z, 16384);
				}
				if (direction == 2) {
					addClipping(x, y, z, 4096);
					addClipping(x + 1, y, z, 65536);
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
					addClipping(x, y, z, 66560);
					addClipping(x - 1, y, z, 4096);
					addClipping(x, y + 1, z, 16384);
				}
				if (direction == 1) {
					addClipping(x, y, z, 5120);
					addClipping(x, 1 + y, z, 16384);
					addClipping(1 + x, y, z, 65536);
				}
				if (direction == 2) {
					addClipping(x, y, z, 20480);
					addClipping(x + 1, y, z, 65536);
					addClipping(x, y - 1, z, 1024);
				}
				if (direction == 3) {
					addClipping(x, y, z, 81920);
					addClipping(x, y - 1, z, 1024);
					addClipping(x - 1, y, z, 4096);
				}
			}
		}
	}

	public void removeClippingForVariableObject(int x, int y, int z, int direction, int type, boolean flag) {
		if (type == 0) {
			if (direction == 0) {
				removeClipping(x, y, z, 128);
				removeClipping(x - 1, y, z, 8);
			}
			if (direction == 1) {
				removeClipping(x, y, z, 2);
				removeClipping(x, y + 1, z, 32);
			}
			if (direction == 2) {
				removeClipping(x, y, z, 8);
				removeClipping(x + 1, y, z, 128);
			}
			if (direction == 3) {
				removeClipping(x, y, z, 32);
				removeClipping(x, y - 1, z, 2);
			}
		}
		if (type == 1 || type == 3) {
			if (direction == 0) {
				removeClipping(x, y, z, 1);
				removeClipping(x - 1, y + 1, z, 16);
			}
			if (direction == 1) {
				removeClipping(x, y, z, 4);
				removeClipping(x + 1, y + 1, z, 64);
			}
			if (direction == 2) {
				removeClipping(x, y, z, 16);
				removeClipping(1 + x, y - 1, z, 1);
			}
			if (direction == 3) {
				removeClipping(x, y, z, 64);
				removeClipping(x - 1, y - 1, z, 4);
			}
		}
		if (type == 2) {
			if (direction == 0) {
				removeClipping(x, y, z, 130);
				removeClipping(x - 1, y, z, 8);
				removeClipping(x, y + 1, z, 32);
			}
			if (direction == 1) {
				removeClipping(x, y, z, 10);
				removeClipping(x, y + 1, z, 32);
				removeClipping(x + 1, y, z, 128);
			}
			if (direction == 2) {
				removeClipping(x, y, z, 40);
				removeClipping(x + 1, y, z, 128);
				removeClipping(x, y - 1, z, 2);
			}
			if (direction == 3) {
				removeClipping(x, y, z, 160);
				removeClipping(x, y - 1, z, 2);
				removeClipping(x - 1, y, z, 8);
			}
		}
		if (flag) {
			if (type == 0) {
				if (direction == 0) {
					removeClipping(x, y, z, 65536);
					removeClipping(x - 1, y, z, 4096);
				}
				if (direction == 1) {
					removeClipping(x, y, z, 1024);
					removeClipping(x, y + 1, z, 16384);
				}
				if (direction == 2) {
					removeClipping(x, y, z, 4096);
					removeClipping(x + 1, y, z, 65536);
				}
				if (direction == 3) {
					removeClipping(x, y, z, 16384);
					removeClipping(x, y - 1, z, 1024);
				}
			}
			if (type == 1 || type == 3) {
				if (direction == 0) {
					removeClipping(x, y, z, 512);
					removeClipping(x - 1, y + 1, z, 8192);
				}
				if (direction == 1) {
					removeClipping(x, y, z, 2048);
					removeClipping(x + 1, y + 1, z, 32768);
				}
				if (direction == 2) {
					removeClipping(x, y, z, 8192);
					removeClipping(1 + x, y - 1, z, 512);
				}
				if (direction == 3) {
					removeClipping(x, y, z, 32768);
					removeClipping(x - 1, y - 1, z, 2048);
				}
			}
			if (type == 2) {
				if (direction == 0) {
					removeClipping(x, y, z, 66560);
					removeClipping(x - 1, y, z, 4096);
					removeClipping(x, 1 + y, z, 16384);
				}
				if (direction == 1) {
					removeClipping(x, y, z, 5120);
					removeClipping(x, y + 1, z, 16384);
					removeClipping(x + 1, y, z, 65536);
				}
				if (direction == 2) {
					removeClipping(x, y, z, 20480);
					removeClipping(x + 1, y, z, 65536);
					removeClipping(x, y - 1, z, 1024);
				}
				if (direction == 3) {
					removeClipping(x, y, z, 81920);
					removeClipping(x, y - 1, z, 1024);
					removeClipping(x - 1, y, z, 4096);
				}
			}
		}
	}
}
