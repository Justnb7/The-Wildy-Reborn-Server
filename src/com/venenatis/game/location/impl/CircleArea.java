package com.venenatis.game.location.impl;

import com.venenatis.game.location.Area;
import com.venenatis.game.location.Location;

/**
 * The location type that models any area in a circle or oval shape.
 *
 * @author lare96 <http://github.com/lare96>
 */
public class CircleArea extends Area {

	/**
	 * The name of this area.
	 */
	private final String name;

	/**
	 * The center {@code X} coordinate.
	 */
	private final double x;

	/**
	 * The center {@code Y} coordinate.
	 */
	private final double y;

	/**
	 * The center {@code Height} coordinate.
	 */
	private final int height;

	/**
	 * The radius of this area.
	 */
	private final double radius;
	
	/**
	 * Creates a new {@link CircleArea}.
	 *
	 * @param x
	 *            the center {@code X} coordinate.
	 * @param y
	 *            the center {@code Y} coordinate.
	 * @param radius
	 *            the radius of this location from the center coordinates.
	 */
	public CircleArea(String name, double x, double y, double radius) {
		this(name, x, y, 0, radius);
	}

	/**
	 * Creates a new {@link CircleArea}.
	 *
	 * @param x
	 *            the center {@code X} coordinate.
	 * @param y
	 *            the center {@code Y} coordinate.
	 *
	 * @param height
	 *            the center {@code Height} coordinate.
	 *            
	 * @param radius
	 *            the radius of this location from the center coordinates.
	 */
	public CircleArea(double x, double y, int height, double radius) {
		this("", x, y, height, radius);
	}

	/**
	 * Creates a new {@link CircleArea}.
	 * 
	 * @param name
	 *            The name of this area.
	 *
	 * @param x
	 *            the center {@code X} coordinate.
	 * @param y
	 *            the center {@code Y} coordinate.
	 * @param height
	 *            the center {@code Height} coordinate.
	 * @param radius
	 *            the radius of this location from the center coordinates.
	 */
	public CircleArea(String name, double x, double y, int height, double radius) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.height = height;
		this.radius = radius;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the radius
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	@Override
	public boolean inArea(Location location) {
		if (location.getZ() != height) {
			return false;
		}		
		return Math.pow((location.getX() - x), 2) + Math.pow((location.getY() - y), 2) <= Math.pow(radius, 2);
	}

	@Override
	public Location getRandomLocation() {
		return null;
	}

}