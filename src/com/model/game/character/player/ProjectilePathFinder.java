package com.model.game.character.player;

import com.model.game.Constants;
import com.model.game.location.Location;
import com.model.utility.cache.map.Region;

/**
 * A pathfinder implementation used for checking projectile paths.
 * @author Nick Hartskeerl <apachenick@hotmail.com>
 */
public final class ProjectilePathFinder {
	
	/**
	 * A singleton of the projectile path finder.
	 */
	public static final ProjectilePathFinder SINGLETON = new ProjectilePathFinder();
	
	/**
	 * Is the interaction path clear from src to dest
	 * @param src
	 * 			start destination
	 * @param dest
	 * 			end destination
	 * @return
	 * 		if the interaction path is clear from src to dest
	 */
	public static boolean isInteractionPathClear(Location src, Location dest) {
		int x0 = src.getX();
		int y0 = src.getY();
		int z = src.getZ();
		int x1 = dest.getX();
		int y1 = dest.getY();
		
		int deltaX = x1 - x0;
		int deltaY = y1 - y0;

		double error = 0;
		final double deltaError = Math.abs(
				(deltaY) / (deltaX == 0
						? ((double) deltaY)
						: ((double) deltaX)));

		int x = x0;
		int y = y0;

		int pX = x;
		int pY = y;

		boolean incrX = x0 < x1;
		boolean incrY = y0 < y1;

		while (true) {
			if (x != x1) {
				x += (incrX ? 1 : -1);
			}

			if (y != y1) {
				error += deltaError;

				if (error >= 0.5) {
					y += (incrY ? 1 : -1);
					error -= 1;
				}
			}

			if (!canAttackOver(x, y, z, pX, pY)) {
				return false;
			}

			if (incrX && incrY
					&& x >= x1 && y >= y1) {
				break;
			} else if (!incrX && !incrY
					&& x <= x1 && y <= y1) {
				break;
			} else if (!incrX && incrY
					&& x <= x1 && y >= y1) {
				break;
			} else if (incrX && !incrY
					&& x >= x1 && y <= y1) {
				break;
			}

			pX = x;
			pY = y;
		}

		return true;
	}
	
	public static boolean isProjectilePathClear(Location src, Location dest) {
		int x0 = src.getX();
		int y0 = src.getY();
		int z = src.getZ();
		int x1 = dest.getX();
		int y1 = dest.getY();
		
		int deltaX = x1 - x0;
		int deltaY = y1 - y0;

		double error = 0;
		final double deltaError = Math.abs(
				(deltaY) / (deltaX == 0
						? ((double) deltaY)
						: ((double) deltaX)));

		int x = x0;
		int y = y0;

		int pX = x;
		int pY = y;

		boolean incrX = x0 < x1;
		boolean incrY = y0 < y1;

		while (true) {
			if (x != x1) {
				x += (incrX ? 1 : -1);
			}

			if (y != y1) {
				error += deltaError;

				if (error >= 0.5) {
					y += (incrY ? 1 : -1);
					error -= 1;
				}
			}

			if (!shootable(x, y, z, pX, pY)) {
				return false;
			}

			if (incrX && incrY
					&& x >= x1 && y >= y1) {
				break;
			} else if (!incrX && !incrY
					&& x <= x1 && y <= y1) {
				break;
			} else if (!incrX && incrY
					&& x <= x1 && y >= y1) {
				break;
			} else if (incrX && !incrY
					&& x >= x1 && y <= y1) {
				break;
			}

			pX = x;
			pY = y;
		}

		return true;
	}

	/**
	 * Can you attack over this Location
	 * @param x
	 * 			x coordinate
	 * @param y
	 * 			y coordinate
	 * @param z
	 * 			z coordinate
	 * @param px
	 * 			px coordinate
	 * @param py
	 * 			py coordinate
	 * @return
	 * 		if you can attack from x,y to px,py
	 */
	private static boolean canAttackOver(int x, int y, int z, int px, int py) {
		if (x == px && y == py) {
			return true;
		}

		int dir = getDirection(x, y, px, py);
		int dir2 = getDirection(px, py, x, y);

		if (dir == -1 || dir2 == -1) {
			return false;
		}

		return Region.canMove(new Location(x, y, z), dir) && Region.canMove(new Location(px, py, z), dir2);
	}
	
	/**
	 * Get the direction from src to dest
	 * @param srcX
	 * 			src x coordinate
	 * @param srcY
	 * 			src y coordinate
	 * @param destX
	 * 			dest x coordinate
	 * @param destY
	 * 			dest y coordinate
	 * @return
	 * 			the direction from src to dest
	 */
	public static int getDirection(int srcX, int srcY, int destX, int destY) {
		int deltaX = destX - srcX;
		int deltaY = destY - srcY;
		return getDirection(deltaX, deltaY);
	}
	
	/**
	 * Get the direction from src to dest
	 * @param deltaX
	 * 			the deltaX, absolute value must be lower than 2
	 * @param deltaY
	 * 			the deltaY, absolute value must be lower than 2
	 */
	public static int getDirection(int deltaX, int deltaY) {
		for (int i = 0; i < 8; i++) {
			if (Constants.DIRECTION_DELTA_X[i] == deltaX && Constants.DIRECTION_DELTA_Y[i] == deltaY) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Can you shootable over this Location
	 * @param x
	 * 			x coordinate
	 * @param y
	 * 			y coordinate
	 * @param z
	 * 			z coordinate
	 * @param px
	 * 			px coordinate
	 * @param py
	 * 			py coordinate
	 * @return
	 * 		if you can shoot from x,y to px,py
	 */
	private static boolean shootable(int x, int y, int z, int px, int py) {
		if (x == px && y == py) {
			return true;
		}

		int dir = getDirection(x, y, px, py);
		int dir2 = getDirection(px, py, x, y);


		if (dir == -1 || dir2 == -1) {
			return false;
		}

		return Region.canMove(new Location(x, y, z), dir) && Region.canMove(new Location(px, py, z), dir2) || Region.canShoot(new Location(x, y, z), dir) && Region.canShoot(new Location(px, py, z), dir2);
	}

	public static enum Direction {

		/**
		 * The north-west direction.
		 */
		NORTH_WEST(-1, 1, 7, 0x12c0108, 0x12c0120, 0x12c0138),
		
		/**
		 * The north direction.
		 */
		NORTH(0, 1, 0, 0x12c0120),
		
		/**
		 * The north-east direction.
		 */
		NORTH_EAST(1, 1, 4, 0x12c0180, 0x12c0120, 0x12c01e0),
		
		/**
		 * The west direction.
		 */
		WEST(-1, 0, 3, 0x12c0108),
		
		/**
		 * The east direction.
		 */
		EAST(1, 0, 1, 0x12c0180),
		
		/**
		 * The south-west direction.
		 */
		SOUTH_WEST(-1, -1, 6, 0x12c0108, 0x12c0102, 0x12c010e),
		
		/**
		 * The south direction.
		 */
		SOUTH(0, -1, 2, 0x12c0102),
		
		/**
		 * The south-east direction.
		 */
		SOUTH_EAST(1, -1, 5, 0x12c0180, 0x12c0102, 0x12c0183);
		
		/**
		 * The amounts of steps on the x-axis.
		 */
		private final int stepX;
		
		/**
		 * The amounts of steps on the y-axis.
		 */
		private final int stepY;
		
		/**
		 * The integer value.
		 */
		private final int value;
		
		/**
		 * The traversal flags.
		 */
		private int[] traversal;
		
		/**
		 * Constructs a new {@code Direction} {@code Object}.
		 * @param stepX The x-offset to move a step.
		 * @param stepY The y-offset to move a step.
		 * @param value The direction value.
		 * @param traversal The traversal flags.
		 */
		private Direction(int stepX, int stepY, int value, int...traversal) {
			this.stepX = stepX;
			this.stepY = stepY;
			this.value = value;
			this.setTraversal(traversal);
		}

		/**
		 * Gets the direction.
		 * @param rotation The int value.
		 * @return The direction.
		 */
		public static Direction get(int rotation) {
			for (Direction dir : Direction.values()) {
				if (dir.value == rotation) {
					return dir;
				}
			}
			throw new IllegalArgumentException("Invalid direction value - " + rotation);
		}

		/**
		 * Gets the direction.
		 * @param Location The start Location.
		 * @param l The end Location.
		 * @return The direction.
		 */
		public static Direction getDirection(Location Location, Location l) {
			return getDirection(l.getX() - Location.getX(), l.getY() - Location.getY());
		}
		
		/**
		 * Gets the direction for movement.
		 * @param diffX The difference between 2 x-coordinates.
		 * @param diffY The difference between 2 y-coordinates.
		 * @return The direction.
		 */
		public static Direction getDirection(int diffX, int diffY) {
			if (diffX < 0) {
				if (diffY < 0) {
					return SOUTH_WEST;
				} else if (diffY > 0) {
					return NORTH_WEST;
				}
				return WEST;
			} else if (diffX > 0) {
				if (diffY < 0) {
					return SOUTH_EAST;
				} else if (diffY > 0) {
					return NORTH_EAST;
				}
				return EAST;
			}
			if (diffY < 0) {
				return SOUTH;
			}
			return NORTH;
		}
		
		/**
		 * Gets the direction for the given walking flag.
		 * @param walkingFlag The walking flag.
		 * @param rotation The rotation.
		 * @return The direction, or null if the walk flag was 0.
		 */
		public static Direction forWalkFlag(int walkingFlag, int rotation) {
			if (rotation != 0) {
				walkingFlag = (walkingFlag << rotation & 0xf) + (walkingFlag >> 4 - rotation);
			}
			if (walkingFlag > 0) {
				if ((walkingFlag & 0x8) == 0) {
					return Direction.WEST;
				}
				if ((walkingFlag & 0x2) == 0) {
					return Direction.EAST;
				}
				if ((walkingFlag & 0x4) == 0) {
					return Direction.SOUTH;
				}
				if ((walkingFlag & 0x1) == 0) {
					return Direction.NORTH;
				}
			}
			return null;
		}
		
		/**
		 * Gets the opposite dir.
		 * @return the direction.
		 */
		public Direction getOpposite() {
			return Direction.get(toInteger() + 2 & 3);
		}
		
		/**
		 * Gets the most logical direction.
		 * @param Location The start Location.
		 * @param l The end Location.
		 * @return The most logical direction.
		 */
		public static Direction getLogicalDirection(Location Location, Location l) {
			int offsetX = Math.abs(l.getX() - Location.getX());
			int offsetY = Math.abs(l.getY() - Location.getY());
			if (offsetX > offsetY) {
				if (l.getX() > Location.getX()) {
					return Direction.EAST;
				} else {
					return Direction.WEST;
				}
			} else if (l.getY() < Location.getY()) {
				return Direction.SOUTH;
			}
			return Direction.NORTH;
		}

		/**
		 * Method used to go to clue the anme.
		 * @param direction the direction.
		 * @return the name.
		 */
		public String toName(Direction direction) {
			return direction.name().toLowerCase();
		}
		
		/**
		 * Method used to get the direction to an integer.
		 * @return the integer.
		 */
		public int toInteger() {
			return value;
		}

		/**
		 * Gets the stepX.
		 * @return The stepX.
		 */
		public int getStepX() {
			return stepX;
		}

		/**
		 * Gets the stepY.
		 * @return The stepY.
		 */
		public int getStepY() {
			return stepY;
		}

		/**
		 * Checks if traversal is permitted for this direction.
		 * @param l The Location.
		 * @return {@code True} if so.
		 */
		public boolean canMove(Location l) {
			int flag = Region.getClippingMask(l.getZ(), l.getX(), l.getY());
			for (int f : traversal) {
				if ((flag & f) != 0) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Gets the traversal.
		 * @return The traversal.
		 */
		public int[] getTraversal() {
			return traversal;
		}

		/**
		 * Sets the traversal.
		 * @param traversal The traversal to set.
		 */
		public void setTraversal(int[] traversal) {
			this.traversal = traversal;
		}
	}
	
}
