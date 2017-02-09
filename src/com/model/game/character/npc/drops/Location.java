package com.model.game.character.npc.drops;

public class Location 
{
	
	private int x;
	
	private int y;
	
	private int h;

	public Location(int x, int y, int h) {
		this.x = x;
		this.y = y;
		this.h = h;
	}

	/**
	 * @return the h
	 */
	public int getH() {
		return h;
	}

	/**
	 * @param h the h to set
	 */
	public void setH(int h) {
		this.h = h;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}

}
