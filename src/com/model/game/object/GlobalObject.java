package com.model.game.object;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import com.model.game.World;
import com.model.game.character.player.Player;
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
public class GlobalObject {
	
	private int id;
	
	private int x;
	
	private int y;
	
	private int height;
	
	private int face;
	
	private int ticksRemaining;
	
	private int restoreId;
	
	private int type;
	
	/**
	 * The location.
	 */
	private Location location;
	
	/**
	 * Creates the game object.
	 * @param location The location.
	 * @param type The type.
	 * @param rotation The rotation.
	 */
	public GlobalObject(Location location, int face, int type) {
		this.location = location;
		this.type = type;
		this.face = face;
	}
	
	public GlobalObject(int id, int x, int y, int height) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.height = height;
	}
	
	public GlobalObject(int id, int x, int y, int height, int face) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.height = height;
		this.face = face;
	}
	
	public GlobalObject(int id, int x, int y, int height, int face, int type) {
		this(id, x, y, height, face);
		this.type = type;
	}
	
	public GlobalObject(int id, int x, int y, int height, int face, int type, int ticksRemaining) {
		this(id, x, y, height, face, type);
		this.ticksRemaining = ticksRemaining;
	}
	
	public GlobalObject(int id, int x, int y, int height, int face, int type, int ticksRemaining, int restoreId) {
		this(id, x, y, height, face, type, ticksRemaining);
		this.restoreId = restoreId;
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
	 * Gets the location.
	 * @return The location.
	 */
	public Location getLocation() {
		return location;
	}
	
    public static CopyOnWriteArrayList<Objects> globalObjects = new CopyOnWriteArrayList<Objects>();
	
	public static Objects fireExists(int id, int objectX, int objectY, int objectHeight) {
		for (Objects o : globalObjects) {
			if (o.objectId == id && o.getObjectX() == objectX && o.getObjectY() == objectY && o.getObjectHeight() == objectHeight) {
				return o;
			}
		}
		return null;
	}
	
	public static void createAnObject(Player c, int id, int x, int y) {
		Objects OBJECT = new Objects(id, x, y, 0, 0, 10, 0);
		if (id == -1) {
			removeObject(OBJECT);
		} else {
			addObject(OBJECT);
		}
		placeObject(OBJECT);
	}
	
	/**
	 * Adds object to list
	 */
	public static void addObject(Objects object) {
		globalObjects.add(object);
	}

	/**
	 * Removes object from list
	 */
	public static void removeObject(Objects object) {
		globalObjects.remove(object);
	}
	
	/**
	 * Creates the object for anyone who is within 60 squares of the object
	 */
	public static void placeObject(Objects o) {
		ArrayList<Objects> toremove = new ArrayList<Objects>();
		for (Objects s : globalObjects) {
			if (s.getObjectX() == o.getObjectX() && s.getObjectY() == o.getObjectY()) {
				toremove.add(s);
			}
		}
		for (Objects s : toremove) {
			if (globalObjects.contains(s)) {
				globalObjects.remove(s);
			}
		}
		globalObjects.add(o);
		for (Player player : World.getWorld().getPlayers()) {
			if (player == null) {
				continue;
			}
			if (player.heightLevel == o.getObjectHeight() && o.objectTicks == 0) {
				if (player.distanceToPoint(o.getObjectX(), o.getObjectY()) <= 60) {
					player.getPA().object(o.getObjectId(), o.getObjectX(), o.getObjectY(), o.getObjectFace(), o.getObjectType());
				}
			}
		}
	}

}
