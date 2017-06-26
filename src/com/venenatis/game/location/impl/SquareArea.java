package com.venenatis.game.location.impl;

import com.venenatis.game.location.Area;
import com.venenatis.game.location.Location;
import com.venenatis.game.util.Utility;

public final class SquareArea extends Area {

	/**
	 * The name of this area.
	 */
	private final String name;

	/**
	 * The south-west {@code X} corner of the square.
	 */
	private final int swX;

	/**
	 * The south-west {@code Y} corner of the square.
	 */
	private final int swY;

	/**
	 * The north-east {@code X} corner of the square.
	 */
	private final int neX;

	/**
	 * The north-east {@code Y} corner of the square.
	 */
	private final int neY;

	/**
	 * The {@code Z} level of the box.
	 */
	private final int height;

	/**
	 * Creates a new {@link SquareArea} with a default height of {@code 0}.
	 *
	 * @param swX
	 *            the south-west {@code X} corner of the square.
	 * @param swY
	 *            the south-west {@code Y} corner of the square.
	 * @param neX
	 *            the north-east {@code X} corner of the square.
	 * @param neY
	 *            the north-east {@code Y} corner of the square.
	 */
	public SquareArea(int swX, int swY, int neX, int neY) {
		this("Unknown", swX, swY, neX, neY, 0);
	}

	/**
	 * Creates a new {@link SquareArea} with a default height of {@code 0}.
	 * 
	 * @param name
	 *            The name of this area.
	 *
	 * @param swX
	 *            the south-west {@code X} corner of the square.
	 * @param swY
	 *            the south-west {@code Y} corner of the square.
	 * @param neX
	 *            the north-east {@code X} corner of the square.
	 * @param neY
	 *            the north-east {@code Y} corner of the square.
	 */
	public SquareArea(String name, int swX, int swY, int neX, int neY) {
		this(name, swX, swY, neX, neY, 0);
	}

	/**
	 * Creates a new {@link SquareArea}.
	 * 
	 * @param name
	 *            The name of this area.
	 *
	 * @param swX
	 *            the south-west {@code X} corner of the square.
	 * @param swY
	 *            the south-west {@code Y} corner of the square.
	 * @param neX
	 *            the north-east {@code X} corner of the square.
	 * @param neY
	 *            the north-east {@code Y} corner of the square.
	 * @param height
	 *            the {@code Height} level of the square.
	 */
	public SquareArea(String name, int swX, int swY, int neX, int neY, int height) {
		this.name = name;
		this.swX = swX;
		this.swY = swY;
		this.neX = neX;
		this.neY = neY;
		this.height = height;
	}

	/**
	 * @return The name of this area.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the neX
	 */
	public int getNeX() {
		return neX;
	}

	/**
	 * @return the neY
	 */
	public int getNeY() {
		return neY;
	}

	/**
	 * @return the swX
	 */
	public int getSwX() {
		return swX;
	}

	/**
	 * @return the swY
	 */
	public int getSwY() {
		return swY;
	}

	/**
	 * @return the z
	 */
	public int getZ() {
		return height;
	}

	@Override
	public boolean inArea(Location location) {
		return location.getX() >= swX && location.getX() <= neX && location.getY() >= swY && location.getY() <= neY;
	}

	@Override
	public Location getRandomLocation() {
		int deltaX = getNeX() - getSwX();
		int deltaY = getNeY() - getSwY();
		return new Location(getSwX() + Utility.random(deltaX), getSwY() + Utility.random(deltaY));
	}

}