package com.model.game.character.walking;

import com.model.game.character.player.Player;
import com.model.game.location.Location;
import com.model.utility.cache.map.Region;

import java.util.ArrayList;

public class PathFinder {

	private static final PathFinder pathfinder = new PathFinder();

	public static PathFinder getPathFinder() {
		return pathfinder;
	}

	public PathFinder() {
	}

	public void findRoute(Player player, int destX, int destY, boolean moveNear, int xLength, int yLength) {
		if (player.frozen())
			return;
		final int regionX = ((player.getX() >> 3) - 6) << 3;
		final int regionY = ((player.getY() >> 3) - 6) << 3;
		final int localX = player.getX() - 8 * ((player.getX() >> 3) - 6);
		final int localY = player.getY() - 8 * ((player.getY() >> 3) - 6);

		if (destX == localX && destY == localY && !moveNear) {
			player.debug("Pathfinder: Target non-close match");
			return;
		}

		final int waypointX = destX;
		final int waypointY = destY;

		destX = destX - (regionX << 3);
		destY = destY - (regionY << 3);

		int[][] via = new int[104][104];
		int[][] cost = new int[104][104];

		ArrayList<Integer> tileQueueX = new ArrayList<Integer>(9000);
		ArrayList<Integer> tileQueueY = new ArrayList<Integer>(9000);

		int curX = localX;
		int curY = localY;
		via[curX][curY] = 99;
		cost[curX][curY] = 1;
		int tail = 0;
		tileQueueX.add(curX);
		tileQueueY.add(curY);

		boolean foundPath = false;
		int pathLength = 4000;

		while (tail != tileQueueX.size() && tileQueueX.size() < pathLength) {

			curX = tileQueueX.get(tail);
			curY = tileQueueY.get(tail);

			int curAbsX = (regionX) + curX;
			int curAbsY = (regionY) + curY;

			if (curAbsX == waypointX && curAbsY == waypointY) {
				foundPath = true;
				break;
			}

			tail = (tail + 1) % pathLength;

			int thisCost = cost[curX][curY] + 1 + 1;
			if (curY > 0 && via[curX][curY - 1] == 0 && (Region.getClippingMask(curAbsX, curAbsY - 1, player.heightLevel) & 0x1280102) == 0) {
				tileQueueX.add(curX);
				tileQueueY.add(curY - 1);
				via[curX][curY - 1] = 1;
				cost[curX][curY - 1] = thisCost;
			}

			if (curX > 0 && via[curX - 1][curY] == 0 && (Region.getClippingMask(curAbsX - 1, curAbsY, player.heightLevel) & 0x1280108) == 0) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY);
				via[curX - 1][curY] = 2;
				cost[curX - 1][curY] = thisCost;
			}

			if (curY < 104 - 1 && via[curX][curY + 1] == 0 && (Region.getClippingMask(curAbsX, curAbsY + 1, player.heightLevel) & 0x1280120) == 0) {
				tileQueueX.add(curX);
				tileQueueY.add(curY + 1);
				via[curX][curY + 1] = 4;
				cost[curX][curY + 1] = thisCost;
			}

			if (curX < 104 - 1 && via[curX + 1][curY] == 0 && (Region.getClippingMask(curAbsX + 1, curAbsY, player.heightLevel) & 0x1280180) == 0) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY);
				via[curX + 1][curY] = 8;
				cost[curX + 1][curY] = thisCost;
			}

			if (curX > 0 && curY > 0 && via[curX - 1][curY - 1] == 0 && (Region.getClippingMask(curAbsX - 1, curAbsY - 1, player.heightLevel) & 0x128010e) == 0 && (Region.getClippingMask(curAbsX - 1, curAbsY, player.heightLevel) & 0x1280108) == 0 && (Region.getClippingMask(curAbsX, curAbsY - 1, player.heightLevel) & 0x1280102) == 0) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY - 1);
				via[curX - 1][curY - 1] = 3;
				cost[curX - 1][curY - 1] = thisCost;
			}

			if (curX > 0 && curY < 104 - 1 && via[curX - 1][curY + 1] == 0 && (Region.getClippingMask(curAbsX - 1, curAbsY + 1, player.heightLevel) & 0x1280138) == 0 && (Region.getClippingMask(curAbsX - 1, curAbsY, player.heightLevel) & 0x1280108) == 0 && (Region.getClippingMask(curAbsX, curAbsY + 1, player.heightLevel) & 0x1280120) == 0) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY + 1);
				via[curX - 1][curY + 1] = 6;
				cost[curX - 1][curY + 1] = thisCost;
			}

			if (curX < 104 - 1 && curY > 0 && via[curX + 1][curY - 1] == 0 && (Region.getClippingMask(curAbsX + 1, curAbsY - 1, player.heightLevel) & 0x1280183) == 0 && (Region.getClippingMask(curAbsX + 1, curAbsY, player.heightLevel) & 0x1280180) == 0 && (Region.getClippingMask(curAbsX, curAbsY - 1, player.heightLevel) & 0x1280102) == 0) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY - 1);
				via[curX + 1][curY - 1] = 9;
				cost[curX + 1][curY - 1] = thisCost;
			}

			if (curX < 104 - 1 && curY < 104 - 1 && via[curX + 1][curY + 1] == 0 && (Region.getClippingMask(curAbsX + 1, curAbsY + 1, player.heightLevel) & 0x12801e0) == 0 && (Region.getClippingMask(curAbsX + 1, curAbsY, player.heightLevel) & 0x1280180) == 0 && (Region.getClippingMask(curAbsX, curAbsY + 1, player.heightLevel) & 0x1280120) == 0) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY + 1);
				via[curX + 1][curY + 1] = 12;
				cost[curX + 1][curY + 1] = thisCost;
			}
		}

		if (!foundPath) {
			if (moveNear) {
				int i_223_ = 1000;
				int thisCost = 100 + 1;
				int i_225_ = 10;

				for (int x = destX - i_225_; x <= destX + i_225_; x++) {
					for (int y = destY - i_225_; y <= destY + i_225_; y++) {
						if (x >= 0 && y >= 0 && x < 104 && y < 104 && cost[x][y] < 100 && cost[x][y] != 0) {
							int i_228_ = 0;
							if (x < destX) {
								i_228_ = destX - x;
							} else if (x > destX + xLength - 1) {
								i_228_ = x - (destX + xLength - 1);
							}
							int i_229_ = 0;
							if (y < destY) {
								i_229_ = destY - y;
							} else if (y > destY + yLength - 1) {
								i_229_ = y - (destY + yLength - 1);
							}
							int i_230_ = i_228_ * i_228_ + i_229_ * i_229_;
							if (i_230_ < i_223_ || i_230_ == i_223_ && cost[x][y] < thisCost && cost[x][y] != 0) {
								i_223_ = i_230_;
								thisCost = cost[x][y];
								curX = x;
								curY = y;
							}
						}
					}
				}

				if (i_223_ == 1000) {
					player.debug("Pathfinder: Limit reached");
					return;
				}

			} else {
				player.debug("Pathfinder: No path possible");
				return;
			}
		}

		tail = 0;
		tileQueueX.set(tail, curX);
		tileQueueY.set(tail++, curY);
		int l5;

		for (int j5 = l5 = via[curX][curY]; curX != localX || curY != localY; j5 = via[curX][curY]) {
			if (j5 != l5) {
				l5 = j5;
				tileQueueX.set(tail, curX);
				tileQueueY.set(tail++, curY);
			}
			if ((j5 & 2) != 0) {
				curX++;
			} else if ((j5 & 8) != 0) {
				curX--;
			}
			if ((j5 & 1) != 0) {
				curY++;
			} else if ((j5 & 4) != 0) {
				curY--;
			}
		}

		player.getMovementHandler().reset();

		int size = tail--;
		int pathX = (regionX) + tileQueueX.get(tail);
		int pathY = (regionY) + tileQueueY.get(tail);

		player.getMovementHandler().addToPath(new Location(pathX, pathY, player.getZ()));
		for (int i = 1; i < size; i++) {
			tail--;
			pathX = (regionX) + tileQueueX.get(tail);
			pathY = (regionY) + tileQueueY.get(tail);
			player.getMovementHandler().addToPath(new Location(pathX, pathY, player.getZ()));
		}

		player.getMovementHandler().finish();
		player.debug("path found! moving");
	}
	
	public boolean findroute2(Location c, int destX, int destY, boolean moveNear, int xLength, int yLength) {
		final int regionX = ((c.getX() >> 3) - 6) << 3;
		final int regionY = ((c.getY() >> 3) - 6) << 3;
		final int localX = c.getX() - 8 * ((c.getX() >> 3) - 6);
		final int localY = c.getY() - 8 * ((c.getY() >> 3) - 6);

		if (destX == localX && destY == localY && !moveNear) {
			return true;
		}

		final int waypointX = destX;
		final int waypointY = destY;

		destX = destX - (regionX << 3);
		destY = destY - (regionY << 3);

		int[][] via = new int[104][104];
		int[][] cost = new int[104][104];

		ArrayList<Integer> tileQueueX = new ArrayList<Integer>(9000);
		ArrayList<Integer> tileQueueY = new ArrayList<Integer>(9000);

		int curX = localX;
		int curY = localY;
		via[curX][curY] = 99;
		cost[curX][curY] = 1;
		int tail = 0;
		tileQueueX.add(curX);
		tileQueueY.add(curY);

		boolean foundPath = false;
		int pathLength = 4000;

		while (tail != tileQueueX.size() && tileQueueX.size() < pathLength) {

			curX = tileQueueX.get(tail);
			curY = tileQueueY.get(tail);

			int curAbsX = (regionX) + curX;
			int curAbsY = (regionY) + curY;

			if (curAbsX == waypointX && curAbsY == waypointY) {
				foundPath = true;
				break;
			}

			tail = (tail + 1) % pathLength;

			int thisCost = cost[curX][curY] + 1 + 1;
			if (curY > 0 && via[curX][curY - 1] == 0 && (Region.getClippingMask(curAbsX, curAbsY - 1, c.getZ()) & 0x1280102) == 0) {
				tileQueueX.add(curX);
				tileQueueY.add(curY - 1);
				via[curX][curY - 1] = 1;
				cost[curX][curY - 1] = thisCost;
			}

			if (curX > 0 && via[curX - 1][curY] == 0 && (Region.getClippingMask(curAbsX - 1, curAbsY, c.getZ()) & 0x1280108) == 0) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY);
				via[curX - 1][curY] = 2;
				cost[curX - 1][curY] = thisCost;
			}

			if (curY < 104 - 1 && via[curX][curY + 1] == 0 && (Region.getClippingMask(curAbsX, curAbsY + 1, c.getZ()) & 0x1280120) == 0) {
				tileQueueX.add(curX);
				tileQueueY.add(curY + 1);
				via[curX][curY + 1] = 4;
				cost[curX][curY + 1] = thisCost;
			}

			if (curX < 104 - 1 && via[curX + 1][curY] == 0 && (Region.getClippingMask(curAbsX + 1, curAbsY, c.getZ()) & 0x1280180) == 0) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY);
				via[curX + 1][curY] = 8;
				cost[curX + 1][curY] = thisCost;
			}

			if (curX > 0 && curY > 0 && via[curX - 1][curY - 1] == 0 && (Region.getClippingMask(curAbsX - 1, curAbsY - 1, c.getZ()) & 0x128010e) == 0 && (Region.getClippingMask(curAbsX - 1, curAbsY, c.getZ()) & 0x1280108) == 0 && (Region.getClippingMask(curAbsX, curAbsY - 1, c.getZ()) & 0x1280102) == 0) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY - 1);
				via[curX - 1][curY - 1] = 3;
				cost[curX - 1][curY - 1] = thisCost;
			}

			if (curX > 0 && curY < 104 - 1 && via[curX - 1][curY + 1] == 0 && (Region.getClippingMask(curAbsX - 1, curAbsY + 1, c.getZ()) & 0x1280138) == 0 && (Region.getClippingMask(curAbsX - 1, curAbsY, c.getZ()) & 0x1280108) == 0 && (Region.getClippingMask(curAbsX, curAbsY + 1, c.getZ()) & 0x1280120) == 0) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY + 1);
				via[curX - 1][curY + 1] = 6;
				cost[curX - 1][curY + 1] = thisCost;
			}

			if (curX < 104 - 1 && curY > 0 && via[curX + 1][curY - 1] == 0 && (Region.getClippingMask(curAbsX + 1, curAbsY - 1, c.getZ()) & 0x1280183) == 0 && (Region.getClippingMask(curAbsX + 1, curAbsY, c.getZ()) & 0x1280180) == 0 && (Region.getClippingMask(curAbsX, curAbsY - 1, c.getZ()) & 0x1280102) == 0) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY - 1);
				via[curX + 1][curY - 1] = 9;
				cost[curX + 1][curY - 1] = thisCost;
			}

			if (curX < 104 - 1 && curY < 104 - 1 && via[curX + 1][curY + 1] == 0 && (Region.getClippingMask(curAbsX + 1, curAbsY + 1, c.getZ()) & 0x12801e0) == 0 && (Region.getClippingMask(curAbsX + 1, curAbsY, c.getZ()) & 0x1280180) == 0 && (Region.getClippingMask(curAbsX, curAbsY + 1, c.getZ()) & 0x1280120) == 0) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY + 1);
				via[curX + 1][curY + 1] = 12;
				cost[curX + 1][curY + 1] = thisCost;
			}
		}

		if (!foundPath) {
			if (moveNear) {
				int i_223_ = 1000;
				int thisCost = 100 + 1;
				int i_225_ = 10;

				for (int x = destX - i_225_; x <= destX + i_225_; x++) {
					for (int y = destY - i_225_; y <= destY + i_225_; y++) {
						if (x >= 0 && y >= 0 && x < 104 && y < 104 && cost[x][y] < 100 && cost[x][y] != 0) {
							int i_228_ = 0;
							if (x < destX) {
								i_228_ = destX - x;
							} else if (x > destX + xLength - 1) {
								i_228_ = x - (destX + xLength - 1);
							}
							int i_229_ = 0;
							if (y < destY) {
								i_229_ = destY - y;
							} else if (y > destY + yLength - 1) {
								i_229_ = y - (destY + yLength - 1);
							}
							int i_230_ = i_228_ * i_228_ + i_229_ * i_229_;
							if (i_230_ < i_223_ || i_230_ == i_223_ && cost[x][y] < thisCost && cost[x][y] != 0) {
								i_223_ = i_230_;
								thisCost = cost[x][y];
								curX = x;
								curY = y;
							}
						}
					}
				}

				if (i_223_ == 1000) {
					return false;
				}

			} else {
				return false;
			}
		}

		tail = 0;
		tileQueueX.set(tail, curX);
		tileQueueY.set(tail++, curY);
		int l5;

		for (int j5 = l5 = via[curX][curY]; curX != localX || curY != localY; j5 = via[curX][curY]) {
			if (j5 != l5) {
				l5 = j5;
				tileQueueX.set(tail, curX);
				tileQueueY.set(tail++, curY);
			}
			if ((j5 & 2) != 0) {
				curX++;
			} else if ((j5 & 8) != 0) {
				curX--;
			}
			if ((j5 & 1) != 0) {
				curY++;
			} else if ((j5 & 4) != 0) {
				curY--;
			}
		}

		return foundPath;
		
	}

}