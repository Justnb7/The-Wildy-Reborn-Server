package com.model.game.character.pathfinder.impl;

import com.model.game.character.Entity;
import com.model.game.character.pathfinder.BasicPoint;
import com.model.game.character.pathfinder.PathFinder;
import com.model.game.character.pathfinder.PathState;
import com.model.utility.cache.map.Tile;

public class DefaultPathFinder implements PathFinder {
	
	private int[] queueX = new int[4096];
	private int[] queueY = new int[4096];
	private int[][] via = new int[104][104];
	private int[][] cost = new int[104][104];
	private int writePathPosition = 0;

	public DefaultPathFinder() {
	}

	public void check(Entity mob, int x, int y, int viaDir, int thisCost) {
		queueX[writePathPosition] = x;
		queueY[writePathPosition] = y; 
		via[x][y] = viaDir;
		cost[x][y] = thisCost;
		writePathPosition = writePathPosition + 1 & 0xfff;
	}

	@Override
	public PathState findPath(Entity mob, Entity target, Tile base, int srcX,
                              int srcY, int dstX, int dstY, int radius, boolean running, boolean ignoreLastStep,
                              boolean moveNear) {
		PathState state = new PathState();
		if (srcX < 0 || srcY < 0 || srcX >= 104 || srcY >= 104 || dstX < 0 || dstY < 0 || dstX >= 104 || dstY >= 104) {
			state.routeFailed();
			return state;
		}
		if (srcX == dstX && srcY == dstY) {
			return state;
		}
		int z = mob.getPosition().getZ();
	
		int zX = (base.getRegionX() - 6) << 3;
		int zY = (base.getRegionY() - 6) << 3;
		

		Tile location = Tile.create(zX, zY, base.getZ());
		boolean foundPath = false;
		for (int xx = 0; xx < 104; xx++) {
			for (int yy = 0; yy < 104; yy++) {
				via[xx][yy] = 0;
				cost[xx][yy] = 99999999;
			}
		}
		int curX = srcX;
		int curY = srcY;
		int attempts = 0;
		int readPosition = 0;
		check(mob, curX, curY, 99, 0);
		while (writePathPosition != readPosition) {
			curX = queueX[readPosition];
			curY = queueY[readPosition];
			readPosition = readPosition + 1 & 0xfff;
			if (curX == dstX && curY == dstY) {
				foundPath = true;
				break;
			}
			int absX = location.getX() + curX, absY = location.getY() + curY;
			int thisCost = cost[curX][curY] + 1;
			if (curY > 0 && via[curX][curY - 1] == 0 && (World.getWorld().getRegionManager().getClippingMask(absX, absY - 1, z) & 0x1280102) == 0) {
				check(mob, curX, curY - 1, SOUTH_FLAG, thisCost);
			}
			if (curX > 0 && via[curX - 1][curY] == 0 && (World.getWorld().getRegionManager().getClippingMask(absX - 1, absY, z) & 0x1280108) == 0) {
				check(mob, curX - 1, curY, WEST_FLAG, thisCost);
			}
			if (curY < 103 && via[curX][curY + 1] == 0 && (World.getWorld().getRegionManager().getClippingMask(absX, absY + 1, z) & 0x1280120) == 0) {
				check(mob, curX, curY + 1, NORTH_FLAG, thisCost);
			}
			if (curX < 103 && via[curX + 1][curY] == 0 && (World.getWorld().getRegionManager().getClippingMask(absX + 1, absY, z) & 0x1280180) == 0) {
				check(mob, curX + 1, curY, EAST_FLAG, thisCost);
			}
			if (curX > 0 && curY > 0 && via[curX - 1][curY - 1] == 0 && (World.getWorld().getRegionManager().getClippingMask(absX - 1, absY - 1, z) & 0x128010e) == 0 && (World.getWorld().getRegionManager().getClippingMask(absX - 1, absY, z) & 0x1280108) == 0 && (World.getWorld().getRegionManager().getClippingMask(absX, absY - 1, z) & 0x1280102) == 0) {
				check(mob, curX - 1, curY - 1, SOUTH_WEST_FLAG, thisCost);
			}
			if (curX > 0 && curY < 103 && via[curX - 1][curY + 1] == 0 && (World.getWorld().getRegionManager().getClippingMask(absX - 1, absY + 1, z) & 0x1280138) == 0 && (World.getWorld().getRegionManager().getClippingMask(absX - 1, absY, z) & 0x1280108) == 0 && (World.getWorld().getRegionManager().getClippingMask(absX, absY + 1, z) & 0x1280120) == 0) {
				check(mob, curX - 1, curY + 1, NORTH_WEST_FLAG, thisCost);
			}
			if (curX < 103 && curY > 0 && via[curX + 1][curY - 1] == 0 && (World.getWorld().getRegionManager().getClippingMask(absX + 1, absY - 1, z) & 0x1280183) == 0 && (World.getWorld().getRegionManager().getClippingMask(absX + 1, absY, z) & 0x1280180) == 0 && (World.getWorld().getRegionManager().getClippingMask(absX, absY - 1, z) & 0x1280102) == 0) {
				check(mob, curX + 1, curY - 1, SOUTH_EAST_FLAG, thisCost);
			}
			if (curX < 103 && curY < 103 && via[curX + 1][curY + 1] == 0 && (World.getWorld().getRegionManager().getClippingMask(absX + 1, absY + 1, z) & 0x12801e0) == 0 && (World.getWorld().getRegionManager().getClippingMask(absX + 1, absY, z) & 0x1280180) == 0 && (World.getWorld().getRegionManager().getClippingMask(absX, absY + 1, z) & 0x1280120) == 0) {
				check(mob, curX + 1, curY + 1, NORTH_EAST_FLAG, thisCost);
			}
		}
		if (!foundPath) {
			state.routeFailed();
			if (moveNear) {
				int fullCost = 1000;
				int thisCost = 100;
				int depth = 10;
				int xLength = mob.size();
				int yLength = mob.size();
				for (int x = dstX - depth; x <= dstX + depth; x++) {
					for (int y = dstY - depth; y <= dstY + depth; y++) {
						if (x >= 0 && y >= 0 && x < 104 && y < 104 && cost[x][y] < 100) {
							int diffX = 0;
							if (x < dstX)
								diffX = dstX - x;
							else if (x > dstX + xLength - 1)
								diffX = x - (dstX + xLength - 1);
							int diffY = 0;
							if (y < dstY)
								diffY = dstY - y;
							else if (y > dstY + yLength - 1)
								diffY = y - (dstY + yLength - 1);
							int totalCost = diffX * diffX + diffY * diffY;
							if (totalCost < fullCost || (totalCost == fullCost && (cost[x][y] < thisCost))) {
								fullCost = totalCost;
								thisCost = cost[x][y];
								curX = x;
								curY = y;
							}
						}
					}
				}
				if (fullCost == 1000)
					return state;
			}
		}
		readPosition = 0;
		queueX[readPosition] = curX;
		queueY[readPosition++] = curY;
		int l5;
		attempts = 0;
		for (int j5 = l5 = via[curX][curY]; curX != srcX || curY != srcY; j5 = via[curX][curY]) {
			if (attempts++ > queueX.length) {
				state.routeFailed();
				return state;
			}
			if (j5 != l5) {
				l5 = j5;
				queueX[readPosition] = curX;
				queueY[readPosition++] = curY;
			}
			if ((j5 & WEST_FLAG) != 0) {
				curX++;
			} else if ((j5 & EAST_FLAG) != 0) {
				curX--;
			}
			if ((j5 & SOUTH_FLAG) != 0) {
				curY++;
			} else if ((j5 & NORTH_FLAG) != 0) {
				curY--;
			}
		}
		int size = readPosition--;
		int absX = location.getX() + queueX[readPosition];//loc generated: 3096 3104. in 464: 3156
		int absY = location.getY() + queueY[readPosition];
		state.getPoints().add(BasicPoint.create(absX, absY, z));
		for (int i = 1; i < size; i++) {
			readPosition--;
			absX = location.getX() + queueX[readPosition];
			absY = location.getY() + queueY[readPosition];
			state.getPoints().add(BasicPoint.create(absX, absY, z));
		}
		return state;
	}

}
