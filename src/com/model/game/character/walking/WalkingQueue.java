package com.model.game.character.walking;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.item.ground.GroundItemHandler;
import com.model.game.location.Location;
import com.model.task.impl.EnergyRestoreTick;

import java.util.LinkedList;

/**
 * <p>
 * A <code>WalkingQueue</code> stores steps the client needs to walk and allows
 * this queue of steps to be modified.
 * </p>
 * 
 * <p>
 * The class will also process these steps when {@link #processNextMovement()}
 * is called. This should be called once per server cycle.
 * </p>
 * 
 * @author Graham Edgecombe
 * @author Patrick van Elderen
 * @author Jak
 * 
 */
public class WalkingQueue {

	
	/**
	 * The player.
	 */
	private final Player player;
	
	/**
	 * Creates the <code>WalkingQueue</code> for the specified
	 * <code>Player</code>.
	 * 
	 * @param player
	 *            The player whose walking queue this is.
	 */
	public WalkingQueue(Player player) {
		this.player = player;
	}
	
	/**
	 * The queue of waypoints.
	 */
	private LinkedList<Point> waypoints = new LinkedList<Point>();
	
	/**
	 * Is the next path an automatic run path
	 */
	private boolean runQueue = false;
	
	/**
	 * Sets the run queue flag.
	 * 
	 * @param runQueue
	 *            The run queue flag.
	 */
	public void setRunningQueue(boolean runQueue) {
		this.runQueue = runQueue;
	}
	
	/**
	 * Gets the running queue flag.
	 * 
	 * @return The running queue flag.
	 */
	public boolean isRunningQueue() {
		return runQueue;
	}
	
	/**
	 * Run toggle (button in client).
	 */
	private boolean runToggled = false;
	
	/**
	 * Sets the run toggled flag.
	 * 
	 * @param runToggled
	 *            The run toggled flag.
	 */
	public void setRunningToggled(boolean runToggled) {
		this.runToggled = runToggled;
	}
	
	/**
	 * Gets the run toggled flag.
	 * 
	 * @return The run toggled flag.
	 */
	public boolean isRunningToggled() {
		return runToggled;
	}
	
	/**
	 * Checks if any running flag is set.
	 * 
	 * @return <code>true</code. if so, <code>false</code> if not.
	 */
	public boolean isRunning() {
		return runToggled || runQueue;
	}
	
	/**
	 * The entity's energy to run.
	 */
	private double energy = 100;
	
	/**
	 * A method that increases our run energy.
	 * @param energy
	 *            The energy to set.
	 */
	public void setEnergy(double energy) {
		this.energy = energy;
		if(this.energy < 100) {
			if(player.getEnergyRestoreTick() == null) {
				EnergyRestoreTick energyRestoreTick = new EnergyRestoreTick(player);
				player.setEnergyRestoreTick(energyRestoreTick);
				World.getWorld().schedule(energyRestoreTick);				
			}
		} else {
			if(player.getEnergyRestoreTick() != null) {
				player.getEnergyRestoreTick().stop();
				player.setEnergyRestoreTick(null);
			}
		}
	}
	
	/**
	 * @return The energy.
	 */
	public double getEnergy() {
		return energy;
	}
	
	/**
	 * Walking directions
	 */
	private int walkingDirection;
	private int runningDirection;
	
	/**
	 * Represents a single point in the queue.
	 * 
	 * @author Graham Edgecombe
	 * 
	 */
	public static class Point {

		/**
		 * The x-coordinate.
		 */
		private final int x;

		/**
		 * The y-coordinate.
		 */
		private final int y;

		/**
		 * The direction to walk to this point.
		 */
		private final int dir;

		/**
		 * Creates a point.
		 * 
		 * @param x
		 *            X coord.
		 * @param y
		 *            Y coord.
		 * @param dir
		 *            Direction to walk to this point.
		 */
		public Point(int x, int y, int dir) {
			this.x = x;
			this.y = y;
			this.dir = dir;
		}
		
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
	}
	
	/**
	 * Resets the walking queue so it contains no more steps.
	 */
	public void reset() {
		runQueue = false;
		waypoints.clear();
		// Set the base point as this Location.
		waypoints.add(new Point(player.getX(), player.getY(), -1));
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

					if (relX >= 2 << 3 && relX < 11 << 3 && relY >= 2 << 3 && relY < 11 << 3) { // didnt actually tele that far out of current region
						player.setMapRegionChanging(false);
					}
				}
				
				boolean zChange = false;
				if (player.teleHeight != -1 && (player.teleHeight != player.getZ())) {
					zChange = true;
					player.setMapRegionChanging(true);
				}
				
				if (player.isMapRegionChanging()) {
					player.mapRegionX = (player.teleportToX >> 3) - 6;
					player.mapRegionY = (player.teleportToY >> 3) - 6;
					player.setLastKnownRegion(player.getLocation());
				}
				player.teleLocalX = (player.teleportToX - (player.mapRegionX << 3));
				player.teleLocalY = (player.teleportToY - (player.mapRegionY << 3));
				System.out.println("teleport new telepos "+player.teleLocalX+","+player.teleLocalY+" from "+player.mapRegionX+","+player.mapRegionY);

				/*
				 * Set our current Location since we've just teleported
				 */
				player.setLocation(Location.create(player.teleportToX, player.teleportToY, zChange ? player.teleHeight : player.getZ()));
				player.updateCoverage(player.getPosition());
				player.lastTile = new Location(player.getX(), player.getY()+1, player.getZ());
				reset();
				player.teleportToX = player.teleportToY = player.teleHeight = -1; // reset
				
				
				/*player.currentX = (player.teleportToX - (player.mapRegionX << 3));
				player.currentY = (player.teleportToY - (player.mapRegionY << 3));
				player.absX = (short) player.teleportToX;
				player.absY = (short) player.teleportToY;
				player.heightLevel = player.teleHeight != -1 ? player.teleHeight : player.heightLevel;
				player.setLocation(new Location(player.absX, player.absY, player.heightLevel));
				player.lastTile = new Location(player.absX, player.absY+1, player.heightLevel);*/
				
				
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
			if((runToggled || runQueue) && player.getWalkingQueue().getEnergy() > 0) {
				runPoint = waypoints.poll();
			}
			
			if (walkPoint != null && walkPoint.dir != -1) {
				if (canMove(walkPoint.dir) || player.isForcedMovement()) {
					move(walkPoint.dir);
				}
			}

			if (runPoint != null && runPoint.dir != -1) {
				if (canMove(runPoint.dir) || player.isForcedMovement()) {
					move(runPoint.dir);
				}
			}

			// Check for region changes.
			int deltaX = player.getX() - player.getMapRegionX() * 8;
			int deltaY = player.getY() - player.getMapRegionY() * 8;
			if (deltaX < 16 || deltaX >= 88 || deltaY < 16 || deltaY > 88) {
				player.mapRegionX = (player.getX() >> 3) - 6;
				player.mapRegionY = (player.getY() >> 3) - 6;
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
		// for updating
		if (getWalkingDirection() == -1) {
			setWalkingDirection(dir);
		} else if (getRunningDirection() == -1) {
			setRunningDirection(dir);
		} else {
			throw new IllegalArgumentException("Tried to set a THIRD walking direction!");
		}
		player.lastTile = new Location(player.getX(), player.getY(), player.getZ());
		player.teleLocalX += WalkingConstants.DIR[dir][0];
		player.teleLocalY += WalkingConstants.DIR[dir][1];
		System.out.println("moved dir "+dir+" new telepos "+player.teleLocalX+","+player.teleLocalY);
		// update position
		player.setLocation(player.getLocation().transform(WalkingConstants.DIR[dir][0], WalkingConstants.DIR[dir][1]));
		player.updateCoverage(player.getPosition());
		player.updateWalkEntities();
	}

	public boolean isMoving() {
		 return getWalkingDirection() != -1 || getRunningDirection() != -1;
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
	
	public void walkTo(int x, int y) {
        reset();
        addToPath(new Location(player.getX() + x, player.getY() + y, player.getZ()));
        finish();
    }
	
}
