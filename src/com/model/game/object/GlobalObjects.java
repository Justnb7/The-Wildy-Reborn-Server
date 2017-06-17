package com.model.game.object;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.location.Location;


/**
 * 
 * @author Jason MacKeigan
 * @date Dec 18, 2014, 12:14:09 AM
 */
public class GlobalObjects {
	
	/**
	 * A collection of all existing objects
	 */
	Queue<GameObject> objects = new LinkedList<>();
	
	/**
	 * A collection of all objects to be removed from the game
	 */
	Queue<GameObject> remove = new LinkedList<>();
	
	/**
	 * Adds a new global object to the game world
	 * @param object	the object being added
	 */
	public void add(GameObject object) {
		updateObject(object, object.getId());
		objects.add(object);
	}
	
	/**
	 * Removes a global object from the world. If the object is present in the game,
	 * we find the reference to that object and add it to the remove list. 
	 * @param id		the identification value of the object
	 * @param x			the x location of the object
	 * @param y			the y location of the object
	 * @param height	the height of the object 
	 */
	public void remove(int id, int x, int y, int height) {
		Optional<GameObject> existing = objects.stream().filter(o -> o.getId() == id && o.getX() == x 
				&& o.getY() == y && o.getHeight() == height).findFirst();
		if (!existing.isPresent()) {
			return;
		}
		remove(existing.get());
	}
	
	/**
	 * Attempts to remove any and all objects on a certain height that have the same object id.
	 * @param id		the id of the object
	 * @param height	the height the object must be on to be removed
	 */
	public void remove(int id, int height) {
		objects.stream().filter(o -> o.getId() == id && o.getHeight() == height).forEach(this::remove);
	}
	
	/**
	 * Removes a global object from the world based on object reference
	 * @param object	the global object
	 */
	public void remove(GameObject object) {
		if (!objects.contains(object)) {
			return;
		}
		updateObject(object, -1);
		remove.add(object);
	}
	
	public void replaceObject(GameObject gameObject, GameObject replacementObject, int objectRespawnTimer) {
		remove(gameObject);
		add(replacementObject);
	}
	
	/**
	 * Determines if an object exists in the game world
	 * @param id		the identification value of the object
	 * @param x			the x location of the object
	 * @param y			the y location of the object
	 * @param height	the height location of the object
	 * @return			true if the object exists, otherwise false.
	 */
	public boolean exists(int id, Location location) {
		return objects.stream().anyMatch(object -> object.getId() == id && object.getX() == location.getX() && object.getY() == location.getY() && object.getHeight() == location.getZ());
	}
	
	/**
	 * Determines if any object exists in the game world at the specified location
	 * @param x			the x location of the object
	 * @param y			the y location of the object
	 * @param height	the height location of the object
	 * @return			true if the object exists, otherwise false.
	 */
	public boolean anyExists(int x, int y, int height) {
		return objects.stream().anyMatch(object ->object.getX() == x && object.getY() == y && object.getHeight() == height);
	}
	
	public GameObject get(int id, int x, int y, int height) {
		Optional<GameObject> obj = objects.stream().filter(object -> object.getId() == id && object.getX() == x
				&& object.getY() == y && object.getHeight() == height).findFirst();
		return obj.orElse(null);
		
	}
	
	/**
	 * All global objects have a unique value associated with them that is referred to as ticks remaining.
	 * Every six hundred milliseconds each object has their amount of ticks remaining reduced. Once an 
	 * object has zero ticks remaining the object is replaced with it's counterpart. If an object has a
	 * tick remaining value that is negative, the object is never removed unless indicated otherwise.
	 */
	public void pulse() {
		if (objects.size() == 0) {
			return;
		}
		Queue<GameObject> updated = new LinkedList<>();
		GameObject object = null;
		objects.removeAll(remove);
		remove.clear();
		while ((object = objects.poll()) != null) {
			if (object.getTicksRemaining() < 0) {
				updated.add(object);
				continue;
			}
			object.removeTick();
			if (object.getTicksRemaining() == 0) {
				updateObject(object, object.getRestoreId());
			} else {
				updated.add(object);
			}
		}
		objects.addAll(updated);
	}
	
	/**
	 * Updates a single global object with a new object id in the game world for every player within a region.
	 * @param object	the new global object
	 * @param objectId	the new object id
	 */
	public void updateObject(final GameObject object, final int objectId) {
		List<Player> players = World.getWorld().getPlayers().stream().filter(Objects::nonNull).filter(player ->
			player.distanceToPoint(object.getX(), object.getY()) <= 60 && player.getZ() == object.getHeight()).collect(Collectors.toList());
		players.forEach(player -> player.getActionSender().sendObject(objectId, object.getX(), object.getY(), object.getHeight(), object.getFace(), object.getType()));
 	}
	
	/**
	 * Updates all region objects for a specific player
	 * @param player	the player were updating all objects for
	 */
	public void updateRegionObjects(Player player) {
		objects.stream().filter(Objects::nonNull).filter(object -> player.distanceToPoint(
			object.getX(), object.getY()) <= 60 && object.getHeight() == player.getZ()).forEach(object -> player.getActionSender().sendObject(
				object.getId(), object.getX(), object.getY(), object.getHeight(), object.getFace(), object.getType()));
	}

}
