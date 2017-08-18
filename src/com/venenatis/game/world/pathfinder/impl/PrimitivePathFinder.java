package com.venenatis.game.world.pathfinder.impl;

import java.awt.Point;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.world.pathfinder.BasicPoint;
import com.venenatis.game.world.pathfinder.Directions;
import com.venenatis.game.world.pathfinder.PathFinder;
import com.venenatis.game.world.pathfinder.PathState;
import com.venenatis.game.world.pathfinder.TileControl;
import com.venenatis.game.world.pathfinder.clipmap.Region;

public class PrimitivePathFinder implements PathFinder {
	
	public static PrimitivePathFinder INSTANCE = new PrimitivePathFinder();
	
	public static Point getNextStep(Entity mob, Location source, int toX, int toY, int height, int xLength, int yLength) {
    	int baseX = source.localX_hyperion(), baseY = source.localY_hyperion();
        int moveX = 0;
        int moveY = 0;
        if (baseX - toX > 0) {
            moveX--;
        } else if (baseX - toX < 0) {
            moveX++;
        }
        if (baseY - toY > 0) {
            moveY--;
        } else if (baseY - toY < 0) {
            moveY++;
        }
        if (canMove(mob, source, baseX, baseY, baseX + moveX, baseY + moveY, height, xLength, yLength)) {
            return new Point(baseX + moveX, baseY + moveY);
        } else if (moveX != 0 && canMove(mob, source, baseX, baseY, baseX + moveX, baseY, height, xLength, yLength)) {
            return new Point(baseX + moveX, baseY);
        } else if (moveY != 0 && canMove(mob, source, baseX, baseY, baseX, baseY + moveY, height, xLength, yLength)) {
            return new Point(baseX, baseY + moveY);
        }
        return null;
    }
    
    public static boolean canMove(Entity mob, Location base, int startX, int startY, int endX, int endY, int height, int xLength, int yLength) {
		int zX = (base.regionX_hyperion() - 6) << 3;
		int zY = (base.regionY_hyperion() - 6) << 3;
		
    	Location RSTile = Location.create(zX, zY, base.getZ());
        int diffX = endX - startX;
        int diffY = endY - startY;
        int max = Math.max(Math.abs(diffX), Math.abs(diffY));
        for (int ii = 0; ii < max; ii++) {
            int currentX = RSTile.getX() + (endX - diffX);
            int currentY = RSTile.getY() + (endY - diffY);
            for (int i = 0; i < xLength; i++) {
                for (int i2 = 0; i2 < yLength; i2++) {
                    if (diffX < 0 && diffY < 0) {
                        if ((Region.getClippingMask(currentX + i - 1, currentY + i2 - 1, height) & 0x128010e) != 0 || (Region.getClippingMask(currentX + i - 1, currentY + i2, height) & 0x1280108) != 0 || (Region.getClippingMask(currentX + i, currentY + i2 - 1, height) & 0x1280102) != 0) {
                            return false;
                        }
                    } else if (diffX > 0 && diffY > 0) {
                        if ((Region.getClippingMask(currentX + i + 1, currentY + i2 + 1, height) & 0x12801e0) != 0 || (Region.getClippingMask(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0 || (Region.getClippingMask(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0) {
                            return false;
                        }
                    } else if (diffX < 0 && diffY > 0) {
                        if ((Region.getClippingMask(currentX + i - 1, currentY + i2 + 1, height) & 0x1280138) != 0 || (Region.getClippingMask(currentX + i - 1, currentY + i2, height) & 0x1280108) != 0 || (Region.getClippingMask(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0) {
                            return false;
                        }
                    } else if (diffX > 0 && diffY < 0) {
                        if ((Region.getClippingMask(currentX + i + 1, currentY + i2 - 1, height) & 0x1280183) != 0 || (Region.getClippingMask(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0 || (Region.getClippingMask(currentX + i, currentY + i2 - 1, height) & 0x1280102) != 0) {
                            return false;
                        }
                    } else if (diffX > 0 && diffY == 0) {
                        if ((Region.getClippingMask(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0) {
                            return false;
                        }
                    } else if (diffX < 0 && diffY == 0) {
                        if ((Region.getClippingMask(currentX + i - 1, currentY + i2, height) & 0x1280108) != 0) {
                            return false;
                        }
                    } else if (diffX == 0 && diffY > 0) {
                        if ((Region.getClippingMask(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0) {
                            return false;
                        }
                    } else if (diffX == 0 && diffY < 0) {
                        if ((Region.getClippingMask(currentX + i, currentY + i2 - 1, height) & 0x1280102) != 0) {
                            return false;
                        }
                    }
                }
            }
            if (diffX < 0) {
                diffX++;
            } else if (diffX > 0) {
                diffX--;
            }
            if (diffY < 0) {
                diffY++;
            } else if (diffY > 0) {
                diffY--;
            }
        }
        return true;
    }
	
	public static boolean canMove(Entity mob, Location source, Directions.NormalDirection dir) {
		return canMove(mob, source, dir, 1, false);
	}

	public static boolean canMove(Entity mob, Location source, Directions.NormalDirection dir, boolean npcCheck) {
		return canMove(mob, source, dir, 1, npcCheck);
	}

	public static boolean canMove(Entity mob, Location source, Directions.NormalDirection dir, int size, boolean npcCheck) {
		return canMove(mob, source.getX(), source.getY(), dir, size, npcCheck ? 0x1 : 0);
	}
	
	public static boolean canMove(Entity mob, int x, int y, Directions.NormalDirection dir, int size, int checkType) {
		final int z = mob.getLocation().getZ();
		final int npc_height = z;
		boolean checkingNPCs = (checkType == 1);
		if (dir == null) {
			return true;
		}
		switch (dir) {
		case WEST:
			for (int k = y; k < y + size; k++) {
				if (checkingNPCs && TileControl.getSingleton().locationOccupied(x - 1, k, npc_height, 1))
					return false;
				if ((Region.getClippingMask(x - 1, k, z) & 0x1280108) != 0)
					return false;
			}
			break;
		case EAST:
			for (int k = y; k < y + size; k++) {
				if (checkingNPCs && TileControl.getSingleton().locationOccupied(x + size, k, npc_height, 1))
					return false;
				if ((Region.getClippingMask(x + size, k, z) & 0x1280180) != 0)
					return false;
			}
			break;
		case SOUTH:
			for (int i = x; i < x + size; i++) {
				if (checkingNPCs && TileControl.getSingleton().locationOccupied(i, y - 1, npc_height, 1))
					return false;
				if ((Region.getClippingMask(i, y - 1, z) & 0x1280102) != 0)
					return false;
			}
			break;
		case NORTH:
			for (int i = x; i < x + size; i++) {
				if (checkingNPCs && TileControl.getSingleton().locationOccupied(i, y + size, npc_height, 1))
					return false;
				if ((Region.getClippingMask(i, y + size, z) & 0x1280120) != 0)
					return false;
			}
			break;
		case SOUTH_WEST:
			for (int i = x; i < x + size; i++) {
				int s = Region.getClippingMask(i, y - 1, z);
				int w = Region.getClippingMask(i - 1, y, z);
				int sw = Region.getClippingMask(i - 1, y - 1, z);
				if (checkingNPCs && TileControl.getSingleton().locationOccupied(i - 1, y - 1, npc_height, 1))
					return false;
				if ((sw & 0x128010e) != 0 || (s & 0x1280102) != 0 || (w & 0x1280108) != 0)
					return false;
			}
			for (int k = y; k < y + size; k++) {
				int s = Region.getClippingMask(x, k - 1, z);
				int w = Region.getClippingMask(x - 1, k, z);
				int sw = Region.getClippingMask(x - 1, k - 1, z);
				if (checkingNPCs && TileControl.getSingleton().locationOccupied(x - 1, k - 1, npc_height, 1))
					return false;
				if ((sw & 0x128010e) != 0 || (s & 0x1280102) != 0 || (w & 0x1280108) != 0)
					return false;
			}
			break;
		case SOUTH_EAST:
			for (int i = x; i < x + size; i++) {
				int s = Region.getClippingMask(i, y - 1, z);
				int e = Region.getClippingMask(i + 1, y, z);
				int se = Region.getClippingMask(i + 1, y - 1, z);
				if (checkingNPCs && TileControl.getSingleton().locationOccupied(i + 1, y - 1, npc_height, 1))
					return false;
				if ((se & 0x1280183) != 0 || (s & 0x1280102) != 0 || (e & 0x1280180) != 0)
					return false;
			}
			for (int k = y; k < y + size; k++) {
				int s = Region.getClippingMask(x + size - 1, k - 1, z);
				int e = Region.getClippingMask(x + size, k, z);
				int se = Region.getClippingMask(x + size, k - 1, z);
				if (checkingNPCs && TileControl.getSingleton().locationOccupied(x + 1, k - 1, npc_height, 1))
					return false;
				if ((se & 0x1280183) != 0 || (s & 0x1280102) != 0 || (e & 0x1280180) != 0)
					return false;
			}
			break;
		case NORTH_WEST:
			for (int i = x; i < x + size; i++) {
				int n = Region.getClippingMask(i, y + size, z);
				int w = Region.getClippingMask(i - 1, y + size - 1, z);
				int nw = Region.getClippingMask(i - 1, y + size, z);
				if (checkingNPCs && TileControl.getSingleton().locationOccupied(i - 1, y + size, npc_height, 1))
					return false;
				if ((nw & 0x1280138) != 0 || (n & 0x1280102) != 0 || (w & 0x1280108) != 0)
					return false;
			}
			for (int k = y; k < y + size; k++) {
				int n = Region.getClippingMask(x, y, z);
				int w = Region.getClippingMask(x - 1, y, z);
				int nw = Region.getClippingMask(x - 1, y + 1, z);
				if (checkingNPCs && TileControl.getSingleton().locationOccupied(x - 1, y + 1, npc_height, 1))
					return false;
				if ((nw & 0x1280138) != 0 || (n & 0x1280102) != 0 || (w & 0x1280108) != 0)
					return false;
			}
			break;
		case NORTH_EAST:
			for (int i = x; i < x + size; i++) {
				int n = Region.getClippingMask(i, y + size, z);
				int e = Region.getClippingMask(i + 1, y + size - 1, z);
				int ne = Region.getClippingMask(i + 1, y + size, z);
				if (checkingNPCs && TileControl.getSingleton().locationOccupied(i + 1, y + size, npc_height, 1))
					return false;
				if ((ne & 0x12801e0) != 0 || (n & 0x1280120) != 0 || (e & 0x1280180) != 0)
					return false;
			}
			for (int k = y; k < y + size; k++) {
				int n = Region.getClippingMask(x + size - 1, k + 1, z);
				int e = Region.getClippingMask(x + size, k, z);
				int ne = Region.getClippingMask(x + size, k + 1, z);
				if (checkingNPCs && TileControl.getSingleton().locationOccupied(x + size, k + 1, npc_height, 1))
					return false;
				if ((ne & 0x12801e0) != 0 || (n & 0x1280120) != 0 || (e & 0x1280180) != 0)
					return false;
			}
			break;
		}
		return true;
	}

	@Override
	public PathState findPath(Entity mob, Entity target, Location base, int srcX,
                              int srcY, int dstX, int dstY, int radius, boolean running, boolean ignoreLastStep,
                              boolean moveNear) {
		return findPath(mob, base, srcX, srcY, dstX, dstY, radius, running, ignoreLastStep, moveNear, false);
	}
	
	/**
	 * Finds a path
	 * @param mob
	 * @param base
	 * @param srcX
	 * @param srcY
	 * @param dstX
	 * @param dstY
	 * @param radius
	 * @param running
	 * @param ignoreLastStep
	 * @param moveNear
	 * @param nullOnFail
	 * @return
	 */
	public PathState findPath(Entity mob, Location base, int srcX, int srcY, int dstX, int dstY, int radius, boolean running, boolean ignoreLastStep, boolean moveNear, boolean nullOnFail) {
		if (srcX < 0 || srcY < 0 || srcX >= 104 || srcY >= 104 || dstX < 0 || dstY < 0 || srcX >= 104 || srcY >= 104) {
			return null;
		}
		if (srcX == dstX && srcY == dstY) {
			return null;
		}
		
		int zX = (base.getRegionX() - 6) << 3;
		int zY = (base.getRegionY() - 6) << 3;

		Location location = Location.create(zX, zY, base.getZ());
		Location current = Location.create(location.getX() + srcX, location.getY() + srcY, location.getZ());
		Location end = Location.create(location.getX() + dstX, location.getY() + dstY, location.getZ());
		PathState state = new PathState();
		while (current != end) {
			Directions.NormalDirection nextDirection = current.direction(end);
			if (nextDirection != null && canMove(mob, current, nextDirection, mob.size(), false)) {
				current = current.transform(Directions.DIRECTION_DELTA_X[nextDirection.intValue()], Directions.DIRECTION_DELTA_Y[nextDirection.intValue()], 0);
				state.getPoints().add(BasicPoint.create(current.getX(), current.getY(), current.getZ()));
			} else {
				break;
			}
		}
		return state;
	}

}
