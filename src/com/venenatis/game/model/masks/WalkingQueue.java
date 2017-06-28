package com.venenatis.game.model.masks;

import com.venenatis.game.constants.WalkingConstants;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.controller.Controller;
import com.venenatis.game.task.impl.EnergyRestoreTick;
import com.venenatis.game.util.DirectionUtils;
import com.venenatis.game.world.World;

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
	 * 
	 * @param energy
	 *            The energy to set.
	 */
	public void setEnergy(double energy) {
		this.energy = energy;
		if (this.energy < 100) {
			if (player.getEnergyRestoreTick() == null) {
				EnergyRestoreTick energyRestoreTick = new EnergyRestoreTick(player);
				player.setEnergyRestoreTick(energyRestoreTick);
				World.getWorld().schedule(energyRestoreTick);
			}
		} else {
			if (player.getEnergyRestoreTick() != null) {
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
	 * Removes the first waypoint which is only used for calculating directions.
	 * This means walking begins at the correct time.
	 */
	public void finish() {
		waypoints.removeFirst();
	}

	/**
	 * Adds a single step to the walking queue, filling in the points to the
	 * previous point in the queue if necessary.
	 * 
	 * @param x
	 *            The local x coordinate.
	 * @param y
	 *            The local y coordinate.
	 */
	public void addStep(int x, int y) {
		/*
		 * The RuneScape client will not send all the points in the queue. It
		 * just sends places where the direction changes.
		 * 
		 * For instance, walking from a route like this:
		 * 
		 * <code> ***** * * ***** </code>
		 * 
		 * Only the places marked with X will be sent:
		 * 
		 * <code> X***X * * X***X </code>
		 * 
		 * This code will 'fill in' these points and then add them to the queue.
		 */

		/*
		 * We need to know the previous point to fill in the path.
		 */
		if (waypoints.size() == 0) {
			/*
			 * There is no last point, reset the queue to add the player's
			 * current location.
			 */
			reset();
		}

		/*
		 * We retrieve the previous point here.
		 */
		Point last = waypoints.peekLast();

		/*
		 * We now work out the difference between the points.
		 */
		int diffX = x - last.getX();
		int diffY = y - last.getY();

		/*
		 * And calculate the number of steps there is between the points.
		 */
		int max = Math.max(Math.abs(diffX), Math.abs(diffY));
		for (int i = 0; i < max; i++) {
			/*
			 * Keep lowering the differences until they reach 0 - when our route
			 * will be complete.
			 */
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

			/*
			 * Add this next step to the queue.
			 */
			addStepInternal(x - diffX, y - diffY);

		}
	}

	/**
	 * Adds a single step to the queue internally without counting gaps. This
	 * method is unsafe if used incorrectly so it is private to protect the
	 * queue.
	 * 
	 * @param x
	 *            The x coordinate of the step.
	 * @param y
	 *            The y coordinate of the step.
	 */
	public void addStepInternal(int x, int y) {
		/*
		 * Check if we are going to violate capacity restrictions.
		 */
		if (waypoints.size() >= WalkingConstants.MAXIMUM_SIZE) {
			/*
			 * If we are we'll just skip the point. The player won't get a
			 * complete route by large routes are not probable and are more
			 * likely sent by bots to crash servers.
			 */
			return;
		}

		/*
		 * We retrieve the previous point (this is to calculate the direction to
		 * move in).
		 */
		Point last = waypoints.peekLast();

		/*
		 * Now we work out the difference between these steps.
		 */
		int diffX = x - last.x;
		int diffY = y - last.y;

		/*
		 * And calculate the direction between them.
		 */
		int dir = DirectionUtils.direction(diffX, diffY);
		// Direction dir = Direction.direction(diffX, diffY);

		/*
		 * Check if we actually move anywhere.
		 */
		if (dir > -1) {
			/*
			 * We now have the information to add a point to the queue! We
			 * create the actual point object and add it.
			 */
			waypoints.add(new Point(x, y, dir));
		}
	}

	/**
	 * Process player movement
	 */
	public void processNextMovement() {
		/*
		 * Store the teleporting flag.
		 */
		boolean teleporting = player.hasTeleportTarget();

		/*
		 * The points which we are walking to.
		 */
		Point walkPoint = null, runPoint = null;

		/*
		 * Checks if the player is teleporting i.e. not walking.
		 */
		if (teleporting) {
			/*
			 * Reset the walking queue as it will no longer apply after the
			 * teleport.
			 */
			reset();

			/*
			 * Set the 'teleporting' flag which indicates the player is
			 * teleporting.
			 */
			player.setTeleporting(true);

			/*
			 * Sets the player's new location to be their target.
			 */
			player.setLocation(player.getTeleportTarget());

			/*
			 * Resets the teleport target.
			 */
			player.resetTeleportTarget();
			
			/*
			 * Update the controller after teleporting
			 */
			Controller controller = player.getController();
			if (controller != null) {
				controller.onStep(player);
			}
		} else {
			/*
			 * If the player isn't teleporting, they are walking (or standing
			 * still). We get the next direction of movement here.
			 */
			Location before = player.getLocation();
			walkPoint = getNextPoint();

			if (runToggled || runQueue) {
				runPoint = getNextPoint();
			}

			/*
			 * Now set the sprites.
			 */
			int walkDir = walkPoint == null ? -1 : walkPoint.dir;
			int runDir = runPoint == null ? -1 : runPoint.dir;
			player.getSprites().setSprites(walkDir, runDir);
			if (walkPoint != null)
				player.lastTile = before;
		}
		// Check for region changes.
		int deltaX = player.getX() - player.getLastKnownRegion().getRegionX() * 8;
		int deltaY = player.getY() - player.getLastKnownRegion().getRegionY() * 8;
		if (deltaX < 16 || deltaX >= 88 || deltaY < 16 || deltaY >= 88) {
			player.setMapRegionChanging(true);
		}
	}

	/**
	 * Gets the next point of movement.
	 * 
	 * @return The next point.
	 */
	private Point getNextPoint() {
		/*
		 * Take the next point from the queue.
		 */
		Point p = waypoints.poll();

		/*
		 * Checks if there are no more points.
		 */
		if (p == null || p.dir == -1) {
			/*
			 * Return <code>null</code> indicating no movement happened.
			 */
			return null;
		} else {
			/*
			 * Set the player's new location.
			 */
			int diffX = WalkingConstants.DIRECTION_DELTA_X[p.dir];
			int diffY = WalkingConstants.DIRECTION_DELTA_Y[p.dir];
			player.setLocation(player.getPosition().transform(diffX, diffY, 0));
			player.updateCoverage(player.getPosition().transform(diffX, diffY, 0));
			if (player.getController() != null) {
				player.getController().onStep(player);
			}
			/*
			 * And return the direction.
			 */
			return p;
		}
	}

	public boolean isMoving() {
		if (player.getSprites().getPrimarySprite() != -1 || player.getSprites().getSecondarySprite() != -1) {
			return true;
		}
		return false;
	}

	public void walkTo(int x, int y) {
		reset();
		addStep(player.getX() + x, player.getY() + y);
		finish();
	}

}
