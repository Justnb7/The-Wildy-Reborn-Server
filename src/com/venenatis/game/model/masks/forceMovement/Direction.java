package com.venenatis.game.model.masks.forceMovement;

import com.venenatis.game.location.Location;
import com.venenatis.game.util.Utility;

/**
 * Represents the enumerated directions an entity can walk or face.
 * 
 * @author SeVen
 */
public enum Direction {

	NORTH(0, 1, 4),

	NORTH_EAST(1, 1, 2),

	EAST(1, 0, 1),

	SOUTH_EAST(1, -1, 7),

	SOUTH(0, -1, 6),

	SOUTH_WEST(-1, -1, 5),

	WEST(-1, 0, 3),

	NORTH_WEST(-1, 1, 0),

	NONE(0, 0, -1);

	private final int directionX;

	private final int directionY;

	private final int id;

	/**
	 * Creates a {@link Direction}.
	 * 
	 * @param directionX
	 *            The value that represents a direction.
	 */
	Direction(int directionX, int directionY, int id) {
		this.directionX = directionX;
		this.directionY = directionY;
		this.id = id;
	}

	/**
	 * Gets the direction between two locations.
	 * 
	 * @param location
	 *            The location that will be the viewpoint.
	 * 
	 * @param other
	 *            The other location to get the direction of.
	 * 
	 * @return The direction of the other location.
	 */
	public static Direction getDirection(Location location, Location other) {

		final int deltaX = other.getX() - location.getX();
		final int deltaY = other.getY() - location.getY();
		
		if (deltaY >= 1) {
			if (deltaX >= 1) {
				return NORTH_EAST;
			} else if (deltaX == 0) {
				return NORTH;
			} else if (deltaX <= -1) {
				return NORTH_WEST;
			}
		} else if (deltaY == 0) {
			if (deltaX >= 1) {
				return Direction.EAST;
			} else if (deltaX == 0) {
				return Direction.NONE;
			} else if (deltaX <= -1) {
				return Direction.WEST;
			}
		} else if (deltaY <= -1) {
			if (deltaX >= 1) {
				return SOUTH_EAST;
			} else if (deltaX == 0) {
				return SOUTH;
			} else if (deltaX <= -1) {
				return SOUTH_WEST;
			}
		}

		return Direction.NONE;

	}

	public static Direction getRandomDirection() {
		int random = Utility.random(4);
		switch(random) {
			case 1:
				return NORTH;

			case 2:
				return EAST;

			case 3:
				return SOUTH;

			case 4:
				return WEST;

			default:
				return SOUTH;
		}
	}

	/**
	 * Gets the direction between two locations, ignoring corners.
	 * 
	 * @param location
	 *            The location that will be the viewpoint.
	 * 
	 * @param other
	 *            The other location to get the direction of.
	 * 
	 * @return The direction of the other location.
	 */
	public static Direction getManhattanDirection(Location location, Location other) {

		final int deltaX = other.getX() - location.getX();
		final int deltaY = other.getY() - location.getY();

		if (deltaY >= 1) {
			if (deltaX == 0) {
				return NORTH;
			}
		} else if (deltaY == 0) {
			if (deltaX >= 1) {
				return Direction.EAST;
			} else if (deltaX == 0) {
				return Direction.NONE;
			} else if (deltaX <= -1) {
				return Direction.WEST;
			}
		} else if (deltaY <= -1) {
			if (deltaX == 0) {
				return SOUTH;
			}
		}

		return Direction.NONE;

	}

	/**
	 * Gets the opposite direction of the given direction.
	 * 
	 * @param direction
	 */
	public static Direction getOppositeDirection(Direction direction) {
		switch (direction) {

		case EAST:
			return WEST;

		case NORTH:
			return SOUTH;

		case NORTH_EAST:
			return SOUTH_WEST;

		case NORTH_WEST:
			return SOUTH_EAST;

		case SOUTH:
			return NORTH;

		case SOUTH_EAST:
			return NORTH_WEST;

		case SOUTH_WEST:
			return NORTH_EAST;

		case WEST:
			return EAST;

		default:
			return NONE;

		}
	}

	public static Direction getDanceFollowDirection(Direction direction) {
			switch (direction) {
				case NORTH:
					return SOUTH_EAST;

				case EAST:
					return SOUTH_WEST;

				case SOUTH:
					return NORTH_WEST;

				case WEST:
					return NORTH_EAST;

				default:
					System.out.println("UNHANDLED DIRECTION: " + direction.name());
					return direction;
			}
	}


	/**
	 * Gets the orientation for door.
	 *
	 * @param direction
	 *            The direction of this object.
	 *
	 * @return The orientation.
	 */
	public static int getDoorOrientation(Direction direction) {
		switch (direction) {

			case WEST:
				return 0;

			case NORTH:
				return 1;

			case EAST:
				return 2;

			case SOUTH:
				return 3;

			default:
				return 3;
		}
	}
	
	/**
	 * Gets the Manhattan direction of an orientation. Manhattan direction does not support diagnal directions, so the orientation must be between [0, 3] inclusive.
	 * @param orientation
	 * @return
	 */
	public static Direction ofManhattan(int orientation) {
		
		assert(orientation >= 0 && orientation <= 3);
		
		switch (orientation) {
		
		case 0:
			return EAST;
			
		case 1:
			return SOUTH;
			
		case 2:
			return WEST;
			
		case 3:
			return NORTH;
		
		}
		
		return NONE;
	}
	
	public static Direction of(int orientation) {
		for (Direction direction : values()) {
			if (direction.id == orientation) {
				return direction;
			}
		}
		
		return null;
	}
	
	public int getManhattanOrientation() {
		switch (this) {
		case NORTH_WEST:
			return 0;
			
		case NORTH:
			return 1;
			
		case NORTH_EAST:
			return 2;
			
		case WEST:
			return 3;
			
		case EAST:
			return 4;
			
		case SOUTH_WEST:
			return 5;

		case SOUTH:
			return 6;
			
		case SOUTH_EAST:
			return 7;
			
		default:
			return -1;
		}
	}

	/**
	 * @return the directionX
	 */
	public int getDirectionX() {
		return directionX;
	}

	/**
	 * @return the directionY
	 */
	public int getDirectionY() {
		return directionY;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	public enum FacingDirection {
		NORTH(Direction.NORTH), SOUTH(Direction.SOUTH), EAST(Direction.EAST), WEST(Direction.WEST);

		private final Direction direction;

		FacingDirection(Direction direction) {
			this.direction = direction;
		}

		public Direction getDirection() {
			return direction;
		}
	}

}