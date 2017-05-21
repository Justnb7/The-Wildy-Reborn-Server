package com.model.game.object;

import com.model.game.location.Location;

/**
 * A global object is a visual model that is viewed by all players within a region.
 * This class represents the identification value, x and y position, as well as the
 * height of the object. 
 * 
 * A key factor is the ticks remaining. The ticksRemaining variable represents how
 * many game ticks this object will remain visible for. If the value is negative
 * the object will remain indefinitly. On the flip side, if the value is positive then
 * every tick the total remaining will reduce by one until it hits zero.
 * 
 * @author Jason MacKeigan
 * @date Dec 17, 2014, 6:18:20 PM
 */
public class GameObject {
	
	private int id;
	
	private int x;
	
	private int y;
	
	private int height;
	
	private int face;
	
	private int ticksRemaining;
	
	private int restoreId;
	
	private int type;
	
	/**
	 * The maximum amount of health this object has (for trees).
	 */
	private int maxHealth = 0;

	/**
	 * The current health this object has (for trees).
	 */
	private int currentHealth = 0;
	
	/**
	 * The location.
	 */
	private Location position;
	
	/**
	 * Creates the game object.
	 * @param Location The position.
	 * @param type The type.
	 * @param rotation The rotation.
	 */
	public GameObject(int id, Location position, int type, int direction) {
		this.id = id;
		this.position = position;
		this.type = type;
		this.face = direction;
	}
	
	public GameObject(int id, int x, int y, int height) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.height = height;
	}
	
	public GameObject(int id, int x, int y, int height, int face) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.height = height;
		this.face = face;
	}
	
	public GameObject(int id, int x, int y, int height, int face, int type) {
		this(id, x, y, height, face);
		this.type = type;
	}
	
	public GameObject(int id, int x, int y, int height, int face, int type, int ticksRemaining) {
		this(id, x, y, height, face, type);
		this.ticksRemaining = ticksRemaining;
	}
	
	public GameObject(int id, int x, int y, int height, int face, int type, int ticksRemaining, int restoreId) {
		this(id, x, y, height, face, type, ticksRemaining);
		this.restoreId = restoreId;
	}

	public GameObject(Location location, int id, int type, int direction) {
		this.id = id;
		this.x = location.getX();
		this.y = location.getY();
		this.height = location.getZ();
		this.type = type;
		this.face = direction;
	}

	public void removeTick() {
		this.ticksRemaining--;
	}
	
	public int getObjectId() {
		return id;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getFace() {
		return face;
	}
	
	public int getTicksRemaining() {
		return ticksRemaining;
	}
	
	public int getRestoreId() {
		return restoreId;
	}
	
	public int getType() {
		return type;
	}
	
	/**
	 * Gets the position.
	 * @return The position.
	 */
	public Location getPosition() {
		return position;
	}

	
	/**
	 * @return the maxHealth
	 */
	public int getMaxHealth() {
		return maxHealth;
	}

	/**
	 * @param maxHealth the maxHealth to set
	 */
	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
		this.currentHealth = maxHealth;
	}

	/**
	 * @return the currentHealth
	 */
	public int getCurrentHealth() {
		return currentHealth;
	}

	/**
	 * @param currentHealth the currentHealth to set
	 */
	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}

	/**
	 * @param currentHealth the currentHealth to set
	 */
	public void decreaseCurrentHealth(int amount) {
		this.currentHealth -= amount;
	}

}
