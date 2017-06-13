package clipmap;

import cache.definitions.AnyRevObjectDefinition;
import com.model.game.location.Location;
import com.model.game.object.GameObject;
import cache.definitions.r317.ObjectDefinition317;

/**
 * Contains CLIPPING ONLY
 */
public class Region {

    public static final int REGION_SIZE = 128;

    public static final int MAX_MAP_X = 16383, MAX_MAP_Y = 16383;

    private static Region[][] mapcache = new Region[(MAX_MAP_X + 1) / REGION_SIZE][(MAX_MAP_Y + 1) / REGION_SIZE];

    public static Region forCoords(int x, int y) {
        int regionX = x >> 7, regionY = y >> 7;
        Region r = mapcache[regionX][regionY];
        if (r == null) {
            r = mapcache[regionX][regionY] = new Region(regionX, regionY, REGION_SIZE);
        }
        return r;
    }

    public static Region getRegion(int x, int y) {
        return forCoords(x, y);
    }

	public int id;
	public final int[][][] clips = new int[4][][];
	// other stuff related to this region but not specific to clipping. this class is strictly clipmap related only
	private final RegionEntities store = new RegionEntities();
	
    private int size;
    private int x;
    private int y;

    public Region(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public static void addClipping(int x, int y, int z, int shift) {
        Region map = forCoords(x, y);
        int localX = x - ((x >> 7) << 7);
        int localY = y - ((y >> 7) << 7);
        if (map.clips[z] == null) {
            map.clips[z] = new int[map.size][map.size];
        }
        map.clips[z][localX][localY] |= shift;
    }

    public static void setClippingMask(int x, int y, int z, int shift) {
        Region map = forCoords(x, y);
        int localX = x - ((x >> 7) << 7);
        int localY = y - ((y >> 7) << 7);
        if (map.clips[z] == null) {
            map.clips[z] = new int[map.size][map.size];
        }
        map.clips[z][localX][localY] = shift;
    }

    public static void removeClipping(int x, int y, int z, int shift) {
        Region map = forCoords(x, y);
        int localX = x - ((x >> 7) << 7);
        int localY = y - ((y >> 7) << 7);
        if (map.clips[z] == null) {
            map.clips[z] = new int[map.size][map.size];
        }
        map.clips[z][localX][localY] &= ~shift;
    }

    public static int getClippingMask(int absx, int absy, int z) {
        Region map = forCoords(absx, absy);
        if (map.clips[z] == null) {
            System.err.println("region not loaded "+absx+","+absy+","+z);
            return -1;
        }
        int localX = absx - ((absx >> 7) << 7);
        int localY = absy - ((absy >> 7) << 7);
        return map.clips[z][localX][localY];
    }


	public static void addObject(GameObject obj) {
        ProjectileClipping.addClipping(obj);

        AnyRevObjectDefinition def = AnyRevObjectDefinition.get(obj.getId());
        if (def == null) {
            return;
        }

        int xLength;
        int yLength;
        if (obj.getFace() != 1 && obj.getFace() != 3) {
            xLength = def.xLength();
            yLength = def.yLength();
        } else {
            xLength = def.yLength();
            yLength = def.xLength();
        }
        if (obj.getType() == 22) {
            if (def.hasActions() && def.roofclips()) {
                addClipping(obj.getX(), obj.getY(), obj.getHeight(), 0x200000);
            }
        } else if (obj.getType() >= 9) {
            if (def.clips()) {
                addClippingForSolidObject(obj.getX(), obj.getY(), obj.getHeight(), xLength, yLength, def.projectileClipped());
            }
        } else if (obj.getType() >= 0 && obj.getType() <= 3) {
            if (def.clips()) {
                addClippingForVariableObject(obj.getX(), obj.getY(), obj.getHeight(), obj.getType(), obj.getFace(), def.projectileClipped());
            }
        }
    }

    public static void removeClipping(GameObject obj) {
        ProjectileClipping.removeClipping(obj);
        AnyRevObjectDefinition def = AnyRevObjectDefinition.get(obj.getId());
        if (def == null) return;
        int xLength;
        int yLength;
        int x = obj.getPosition().getX();
        int y = obj.getPosition().getY();
        int height = obj.getPosition().getZ();
        if (obj.getFace() != 1 && obj.getFace() != 3) {
            xLength = def.xLength();
            yLength = def.yLength();
        } else {
            xLength = def.yLength();
            yLength = def.xLength();
        }
        if (obj.getType() == 22) {
            if (def.roofclips() && def.hasActions()) {
                removeClipping(x, y, height, 0x200000);
            }
        } else if (obj.getType() >= 9 && obj.getType() <= 11) {
            if (def.clips()) {
                removeClippingForSolidObject(x, y, height, xLength, yLength, def.projectileClipped());
            }
        } else if (obj.getType() >= 0 && obj.getType() <= 3) {
            if (def.clips()) {
                removeClippingForVariableObject(x, y, height, obj.getType(), obj.getFace(), def.projectileClipped());
            }
        }
    }

    private static void addClippingForSolidObject(int x, int y, int height, int xLength, int yLength, boolean solidflag) {
        int clipflag = 256;
        if (solidflag) {
            clipflag |= 0x20000;
        }
        for (int xpos = x; xpos < x + xLength; xpos++) {
            for (int ypos = y; ypos < y + yLength; ypos++) {
                addClipping(xpos, ypos, height, clipflag);
            }
        }
    }

    private static void removeClippingForSolidObject(int x, int y, int height, int xLength, int yLength, boolean solidflag) {
        int flipFlag = 256;
        if (solidflag) {
            flipFlag |= 0x20000;
        }
        for (int i = x; i < x + xLength; i++) {
            for (int i2 = y; i2 < y + yLength; i2++) {
                removeClipping(i, i2, height, flipFlag);
            }
        }
    }

    private static void addClippingForVariableObject(int x, int y, int z, int type, int direction, boolean flag) {
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
                    addClipping(x, y, z, 0x10000);
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
    }

    private static void removeClippingForVariableObject(int x, int y, int z, int type, int direction, boolean flag) {
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
    }

    public static boolean getClipping(int x, int y, int height, int moveTypeX, int moveTypeY) {
        try {
            if (height > 3)
                height = 0;
            int checkX = (x + moveTypeX);
            int checkY = (y + moveTypeY);
            if (moveTypeX == -1 && moveTypeY == 0)
                return (getClippingMask(x, y, height) & 0x1280108) == 0;
            else if (moveTypeX == 1 && moveTypeY == 0)
                return (getClippingMask(x, y, height) & 0x1280180) == 0;
            else if (moveTypeX == 0 && moveTypeY == -1)
                return (getClippingMask(x, y, height) & 0x1280102) == 0;
            else if (moveTypeX == 0 && moveTypeY == 1)
                return (getClippingMask(x, y, height) & 0x1280120) == 0;
            else if (moveTypeX == -1 && moveTypeY == -1)
                return ((getClippingMask(x, y, height) & 0x128010e) == 0
                        && (getClippingMask(checkX - 1, checkY, height) & 0x1280108) == 0
                        && (getClippingMask(checkX - 1, checkY, height) & 0x1280102) == 0);
            else if (moveTypeX == 1 && moveTypeY == -1)
                return ((getClippingMask(x, y, height) & 0x1280183) == 0
                        && (getClippingMask(checkX + 1, checkY, height) & 0x1280180) == 0
                        && (getClippingMask(checkX, checkY - 1, height) & 0x1280102) == 0);
            else if (moveTypeX == -1 && moveTypeY == 1)
                return ((getClippingMask(x, y, height) & 0x1280138) == 0
                        && (getClippingMask(checkX - 1, checkY, height) & 0x1280108) == 0
                        && (getClippingMask(checkX, checkY + 1, height) & 0x1280120) == 0);
            else if (moveTypeX == 1 && moveTypeY == 1)
                return ((getClippingMask(x, y, height) & 0x12801e0) == 0
                        && (getClippingMask(checkX + 1, checkY, height) & 0x1280180) == 0
                        && (getClippingMask(checkX, checkY + 1, height) & 0x1280120) == 0);
            else {

                return false;
            }
        } catch (Exception e) {

            return true;
        }
    }

    public static boolean blockedNorth(int x, int y, int z) {
        return (getClippingMask(x, y + 1, z) & 0x1280120) != 0 || getClippingMask(x, y + 1, z) == -1;
    }

    public static boolean blockedEast(int x, int y, int z) {
        return (getClippingMask(x + 1, y, z) & 0x1280180) != 0 || getClippingMask(x + 1, y, z) == -1;
    }

    public static boolean blockedSouth(int x, int y, int z) {
        return (getClippingMask(x, y - 1, z) & 0x1280102) != 0 || getClippingMask(x, y - 1, z) == -1;
    }

    public static boolean blockedWest(int x, int y, int z) {
        return (getClippingMask(x - 1, y, z) & 0x1280108) != 0 || getClippingMask(x - 1, y, z) == -1;
    }

    public static boolean blockedNorthEast(int x, int y, int z) {
        return (getClippingMask(x + 1, y + 1, z) & 0x12801e0) != 0 || getClippingMask(x + 1, y + 1, z) == -1;
    }

    public static boolean blockedNorthWest(int x, int y, int z) {
        return (getClippingMask(x - 1, y + 1, z) & 0x1280138) != 0 || getClippingMask(x - 1, y + 1, z) == -1;
    }

    public static boolean blockedSouthEast(int x, int y, int z) {
        return (getClippingMask(x + 1, y - 1, z) & 0x1280183) != 0 || getClippingMask(x + 1, y - 1, z) == -1;
    }

    public static boolean blockedSouthWest(int x, int y, int z) {
        return (getClippingMask(x - 1, y - 1, z) & 0x128010e) != 0 || getClippingMask(x - 1, y - 1, z) == -1;
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
                        if ((getClippingMask((currentX + i) - 1, (currentY + i2) - 1, height) & 0x128010e) != 0
                                || (getClippingMask((currentX + i) - 1, currentY + i2, height) & 0x1280108) != 0
                                || (getClippingMask(currentX + i, (currentY + i2) - 1, height) & 0x1280102) != 0)
                            return false;
                    } else if (diffX > 0 && diffY > 0) {
                        if ((getClippingMask(currentX + i + 1, currentY + i2 + 1, height) & 0x12801e0) != 0
                                || (getClippingMask(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0
                                || (getClippingMask(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0)
                            return false;
                    } else if (diffX < 0 && diffY > 0) {
                        if ((getClippingMask((currentX + i) - 1, currentY + i2 + 1, height) & 0x1280138) != 0
                                || (getClippingMask((currentX + i) - 1, currentY + i2, height) & 0x1280108) != 0
                                || (getClippingMask(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0)
                            return false;
                    } else if (diffX > 0 && diffY < 0) {
                        if ((getClippingMask(currentX + i + 1, (currentY + i2) - 1, height) & 0x1280183) != 0
                                || (getClippingMask(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0
                                || (getClippingMask(currentX + i, (currentY + i2) - 1, height) & 0x1280102) != 0)
                            return false;
                    } else if (diffX > 0 && diffY == 0) {
                        if ((getClippingMask(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0)
                            return false;
                    } else if (diffX < 0 && diffY == 0) {
                        if ((getClippingMask((currentX + i) - 1, currentY + i2, height) & 0x1280108) != 0)
                            return false;
                    } else if (diffX == 0 && diffY > 0) {
                        if ((getClippingMask(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0)
                            return false;
                    } else if (diffX == 0 && diffY < 0
                            && (getClippingMask(currentX + i, (currentY + i2) - 1, height) & 0x1280102) != 0)
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
        return (getClippingMask(x, y, z) & 0x20000) == 0;
    }

	public static boolean isBlocked(int x, int y, int z, Direction direction) {
        final int size = 0;
        switch (direction) {
        case NORTH:
            return (getClippingMask(x, y + size, z) & direction.getClipMask()) != 0;
        case EAST:
            return (getClippingMask(x + size, y, z) & direction.getClipMask()) != 0;
        case SOUTH:
            return (getClippingMask(x, y - size, z) & direction.getClipMask()) != 0;
        case WEST:
            return (getClippingMask(x - size, y, z) & direction.getClipMask()) != 0;
        case NORTH_EAST:
            return (getClippingMask(x + size, y + size, z) & direction.getClipMask()) != 0;
        case NORTH_WEST:
            return (getClippingMask(x - size, y + size, z) & direction.getClipMask()) != 0;
        case SOUTH_EAST:
            return (getClippingMask(x + size, y - size, z) & direction.getClipMask()) != 0;
        case SOUTH_WEST:
            return (getClippingMask(x - size, y - size, z) & direction.getClipMask()) != 0;
        default:
            return true;
        }
    }

    public static int getShootable(int x, int y, int z) {
        return ProjectileClipping.getClippingMask(x, y, z);
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