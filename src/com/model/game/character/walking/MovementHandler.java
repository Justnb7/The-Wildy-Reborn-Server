package com.model.game.character.walking;

import com.model.game.character.combat.Combat;
import com.model.game.character.player.Player;
import com.model.game.item.ground.GroundItemHandler;
import com.model.game.location.Location;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author blakeman8192
 */
public class MovementHandler {
	public static final boolean USE_WALKING_QUEUE = true;

	/**
	 * Walking directions
	 */
	public static final int[][] DIR = { { -1, 1 }, { 0, 1 }, { 1, 1 }, { -1, 0 }, { 1, 0 }, { -1, -1 }, { 0, -1 }, { 1, -1 } };
	/**
	 * Difference in X coordinates for directions array.
	 */
	public static final byte[] DIRECTION_DELTA_X = new byte[] { -1, 0, 1, -1, 1, -1, 0, 1 };

	/**
	 * Difference in Y coordinates for directions array.
	 */
	public static final byte[] DIRECTION_DELTA_Y = new byte[] { 1, 1, 1, 0, 0, -1, -1, -1 };
	/**
	 * The player.
	 */
	private final Player player;
	/**
	 * Waypoints.
	 */
	private final Deque<Point> waypoints = new LinkedList<Point>();
	/**
	 * Is the next path an automatic run path
	 */
	private boolean runPath = false;
	/**
	 * Walking directions
	 */
	private int walkingDirection;
	private int runningDirection;

	public boolean followPath = false;

	private boolean forcedMovement;

	/**
	 * Creates a new MovementHandler.
	 *
	 * @param player
	 *            the Player
	 */
	public MovementHandler(Player player) {
		this.player = player;
	}

	/**
	 * Process player movement
	 */
	public void process() {
		try {
			player.setMapRegionChanging(false);
			player.setTeleporting(false);
			setWalkingDirection(-1);
			setRunningDirection(-1);

			if (player.teleportToX != -1 && player.teleportToY != -1) {
				player.setMapRegionChanging(true);
				if (player.mapRegionX != -1 && player.mapRegionY != -1) {
					int relX = player.teleportToX - (player.mapRegionX << 3), relY = player.teleportToY - (player.mapRegionY << 3);

					if (relX >= 2 << 3 && relX < 11 << 3 && relY >= 2 << 3 && relY < 11 << 3) {
						player.setMapRegionChanging(false);
					}
				}
				
				boolean zChange = false;
				if (player.teleHeight != -1 && (player.teleHeight != player.heightLevel)) {
					zChange = true;
					player.setMapRegionChanging(true);
				}
				
				if (player.isMapRegionChanging()) {
					player.mapRegionX = (player.teleportToX >> 3) - 6;
					player.mapRegionY = (player.teleportToY >> 3) - 6;
				}
				
				/*
				 * Set our current Location since we've just teleported
				 */
				player.currentX = (player.teleportToX - (player.mapRegionX << 3));
				player.currentY = (player.teleportToY - (player.mapRegionY << 3));
				player.absX = (short) player.teleportToX;
				player.absY = (short) player.teleportToY;
				player.heightLevel = player.teleHeight != -1 ? player.teleHeight : player.heightLevel;
				player.setLocation(new Location(player.absX, player.absY, player.heightLevel));
				player.lastTile = new Location(player.absX, player.absY+1, player.heightLevel);
				reset();
				player.teleportToX = player.teleportToY = player.teleHeight = -1;
				player.setTeleporting(true);
				player.updateWalkEntities();
				/*
				 * Check if we've moved and the height level doesn't match, if so reload ground items and objects
				 */
				if (zChange) {
						//ObjectManager.loadObjects(player);
						//player.reloadItems(player);
						GroundItemHandler.reloadGroundItems(player);
				}
				
				return;
			}

			if (player.frozen()) {
				reset();
				return;
			}

			Point walkPoint = waypoints.poll();
			Point runPoint = null;

			// Handle the movement.
			if (player.isRunning() || isRunPath()) {
				runPoint = waypoints.poll();
			}
			
			if (walkPoint != null && walkPoint.getDirection() != -1) {
				if (canMove(walkPoint.getDirection()) || isForcedMovement()) {
					move(walkPoint.getDirection());
				}
			}

			if (runPoint != null && runPoint.getDirection() != -1) {
				if (canMove(runPoint.getDirection()) || isForcedMovement()) {
					move(runPoint.getDirection());
				}
			}

			// Check for region changes.
			int deltaX = player.absX - player.getMapRegionX() * 8;
			int deltaY = player.absY - player.getMapRegionY() * 8;
			if (deltaX < 16 || deltaX >= 88 || deltaY < 16 || deltaY > 88) {
				player.mapRegionX = (player.absX >> 3) - 6;
				player.mapRegionY = (player.absY >> 3) - 6;
				player.setMapRegionChanging(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean canMove(int dir) {
		if (!player.getController().canMove(player)) {
			return false;
		}
		return true;
	}

	public void move(int dir) {
		if (getWalkingDirection() == -1) {
			setWalkingDirection(dir);
		} else if (getRunningDirection() == -1) {
			setRunningDirection(dir);
		} else {
			throw new IllegalArgumentException("Tried to set a THIRD walking direction!");
		}

		player.lastTile = new Location(player.absX, player.absY, player.heightLevel);
		player.currentX += DIR[dir][0];
		player.currentY += DIR[dir][1];
		player.absX += DIR[dir][0];
		player.absY += DIR[dir][1];
		player.setLocation(new Location(player.absX, player.absY, player.heightLevel));
		player.updateWalkEntities();
	}

	public void setLocation(final int x, final int y, final int plane) {
		Combat.resetCombat(player);
		reset();
		player.teleportToX = x;
		player.teleportToY = y;
		player.heightLevel = (short) plane;
		player.setLocation(new Location(x, y, plane));
	}

	public boolean isMoving() {
		 return getWalkingDirection() != -1 || getRunningDirection() != -1;
	}

	public void resetWalkingQueue() {
		reset();
	}

	public void stopMovement() {
		reset();
	}

	/**
	 * Resets the walking queue.
	 */
	public void reset() {
		followPath = false;
		setRunPath(false);
		waypoints.clear();

		// Set the base point as this Location.
		waypoints.add(new Point(player.absX, player.absY, -1));
	}

	/**
	 * Finishes the current path.
	 */
	public void finish() {
		waypoints.removeFirst();
	}

	/**
	 * Adds a Location to the path.
	 *
	 * @param Location
	 *            the Location
	 */
	public void addToPath(Location Location) {
		if (waypoints.size() == 0) {
			reset();
		}

		Point last = waypoints.peekLast();
		int deltaX = Location.getX() - last.getX();
		int deltaY = Location.getY() - last.getY();
		int max = Math.max(Math.abs(deltaX), Math.abs(deltaY));

		for (int i = 0; i < max; i++) {
			if (deltaX < 0) {
				deltaX++;
			} else if (deltaX > 0) {
				deltaX--;
			}
			if (deltaY < 0) {
				deltaY++;
			} else if (deltaY > 0) {
				deltaY--;
			}

			addStep(Location.getX() - deltaX, Location.getY() - deltaY);
		}
	}

	/**
	 * Adds a step.
	 *
	 * @param x
	 *            the X coordinate
	 * @param y
	 *            the Y coordinate
	 */
	private void addStep(int x, int y) {
		if (waypoints.size() >= 100) {
			return;
		}
		if (waypoints.size() == 0) {
			reset();
		}
		Point last = waypoints.peekLast();
		int deltaX = x - last.getX();
		int deltaY = y - last.getY();
		int direction = direction(deltaX, deltaY);
		if (direction > -1) {
			waypoints.add(new Point(x, y, direction));
		}
	}

	/**
	 * Get a direction by the coordinate modifiers (must be at least -1 and at
	 * most 1)
	 *
	 * @param dx
	 *            the x coordinate modifier
	 * @param dy
	 *            the y coordinate modifier
	 * @return the walking direction (denoted as -1 for none or 0-8 for the
	 *         player update paket direction index)
	 */
	public static int direction(int dx, int dy) {
		if (dx < 0) {
			if (dy < 0) {
				return 5;
			} else if (dy > 0) {
				return 0;
			} else {
				return 3;
			}
		} else if (dx > 0) {
			if (dy < 0) {
				return 7;
			} else if (dy > 0) {
				return 2;
			} else {
				return 4;
			}
		} else {
			if (dy < 0) {
				return 6;
			} else if (dy > 0) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	/**
	 * Toggles running for the current path only.
	 *
	 * @param runPath
	 *            the flag
	 */
	public void setRunPath(boolean runPath) {
		this.runPath = runPath;
	}

	/**
	 * Gets whether or not we're running for the current path.
	 *
	 * @return running
	 */
	public boolean isRunPath() {
		return runPath;
	}

	/**
	 * @return the walkingDirection
	 */
	public int getWalkingDirection() {
		return walkingDirection;
	}

	/**
	 * @param walkingDirection
	 *            the walkingDirection to set
	 */
	public void setWalkingDirection(int walkingDirection) {
		this.walkingDirection = walkingDirection;
	}

	/**
	 * @return the runningDirection
	 */
	public int getRunningDirection() {
		return runningDirection;
	}

	/**
	 * @param runningDirection
	 *            the runningDirection to set
	 */
	public void setRunningDirection(int runningDirection) {
		this.runningDirection = runningDirection;
	}

	/**
	 * An internal Location type class with support for direction.
	 *
	 * @author blakeman8192
	 */
	private class Point extends Location {

		/**
		 * the walking direction
		 */
		private int direction;

		/**
		 * Creates a new Point.
		 *
		 * @param x
		 *            the X coordinate
		 * @param y
		 *            the Y coordinate
		 * @param direction
		 *            the direction to this point
		 */
		public Point(int x, int y, int direction) {
			super(x, y, player.getZ());
			setDirection(direction);
		}

		/**
		 * Sets the direction.
		 *
		 * @param direction
		 *            the direction
		 */
		public void setDirection(int direction) {
			this.direction = direction;
		}

		/**
		 * Gets the direction.
		 *
		 * @return the direction
		 */
		public int getDirection() {
			return direction;
		}

	}

	public boolean isForcedMovement() {
		return forcedMovement;
	}

	public void setForcedMovement(boolean b) {
		this.forcedMovement = b;
	}
	
	public void walkTo(int x, int y) {
        reset();
        addToPath(new Location(player.getX() + x, player.getY() + y, player.getZ()));
        finish();
    }
	
}
