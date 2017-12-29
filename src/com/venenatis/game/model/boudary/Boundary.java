package com.venenatis.game.model.boudary;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.Entity;

public class Boundary {

	/**
	 * The boundary name
	 */
	private String name;

	/**
	 * The bottom left location
	 */
	private Location bottomLeft;

	/**
	 * The top right location
	 */
	private Location topRight;

	/**
	 * No args constructor
	 */
	public Boundary() {

	}

	/**
	 * Sets the boundaries in the constructor
	 * 
	 * @param buttonLeft
	 *            The bottom left coordinates
	 * @param topRight
	 *            The top right coordinates
	 */
	public Boundary(String name, Location bottonLeft, Location topRight) {
		this.name = name;
		this.bottomLeft = bottonLeft;
		this.topRight = topRight;
	}

	/**
	 * Checks if the specified {@link Entity} is within this Boundary's
	 * dimensions.
	 * 
	 * @param entity
	 *            The Entity that is being checked.
	 * @return {@code true} if the Entity is within the boundary's dimensions,
	 *         {@code false} otherwise.
	 */
	public boolean isIn(Entity entity) {
		return isIn(entity.getLocation());
	}

	/**
	 * Checks if the specified {@link Location} is within this Boundary's
	 * dimensions.
	 * 
	 * @param location
	 *            The Location that is being checked.
	 * @return {@code true} if the Location is within the boundary's dimensions,
	 *         {@code false} otherwise.
	 */
	public boolean isIn(final Location location) {
		return location.getX() >= this.bottomLeft.getX() && location.getX() <= this.topRight.getX()
				&& location.getY() >= this.bottomLeft.getY() && location.getY() <= this.topRight.getY();
	}

	public static Boundary create(String name, Location bottomLeft, Location topRight) {
		return new Boundary(name, bottomLeft, topRight);
	}

	public static Boundary create(Location bottomLeft, Location topRight) {
		return create("", bottomLeft, topRight);
	}

	/**
	 * The boundary name
	 * 
	 * @return The boundary name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the button left location
	 * 
	 * @return the bottom left
	 */
	public Location getBottomLeft() {
		return bottomLeft;
	}

	/**
	 * Gets the top left location
	 * 
	 * @return the top right
	 */
	public Location getTopRight() {
		return topRight;
	}

	@Override
	public String toString() {
		return "[name=" + name + " bottomLeft" + bottomLeft + " topRight" + topRight + "]";
	}

}