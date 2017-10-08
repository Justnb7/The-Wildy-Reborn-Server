package com.venenatis.game.world.object;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;
import com.venenatis.game.world.pathfinder.clipmap.Region;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;
import com.venenatis.server.GameEngine;


/**
 * 
 * @author Jason MacKeigan
 * @date Dec 18, 2014, 12:14:09 AM
 */
public class GlobalObjects {
	
	/**
	 * Used for spawning objects that cannot be inserted into the file
	 * 
	 * @param player the player
	 */
	private void loadCustomObjects(Player player) {
		player.farming().updateObjects();
		player.getActionSender().sendObject(27282, 3087, 3504, 0, 0, 10);
		player.getActionSender().sendObject(11338, 2751, 3512, 0, 0, 10);
		player.getActionSender().sendObject(11338, 2750, 3512, 0, 0, 10);
		player.getActionSender().sendObject(11338, 2751, 3512, 0, 0, 10);
		player.getActionSender().sendObject(11338, 2755, 3503, 0, 0, 10);
		player.getActionSender().sendObject(11338, 2754, 3503, 0, 0, 10);
		player.getActionSender().sendObject(11338, 2753, 3503, 0, 0, 10);
		player.getActionSender().sendObject(11338, 2760, 3503, 0, 0, 10);
		player.getActionSender().sendObject(11338, 2761, 3503, 0, 0, 10);
		player.getActionSender().sendObject(11338, 2762, 3503, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2543, 10143, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2545, 10145, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2545, 10141, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2750, 3509, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2759, 3513, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2750, 3510, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2756, 3508, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2759, 3507, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2761, 3509, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2761, 3511, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2758, 3513, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2757, 3513, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2755, 3511, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2755, 3509, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2757, 3507, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2757, 3499, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2758, 3499, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2758, 3498, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2757, 3498, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2763, 3498, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2763, 3500, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2762, 3499, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2753, 3498, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2753, 3499, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2753, 3500, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2757, 3504, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2758, 3504, 0, 0, 10);
		player.getActionSender().sendObject(-1, 2757, 3503, 0, 0, 0);
		player.getActionSender().sendObject(-1, 2758, 3503, 0, 0, 0);
		player.getActionSender().sendObject(-1, 3253, 3267, 0, 0, 0);
		player.getActionSender().sendObject(-1, 3253, 3266, 0, 0, 0);
	}
	
	/**
	 * A collection of all existing objects - note these are custom spawned objects NOT objects that exist by default in the game world such as grass etc
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
		Region.addClipping(object);
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
				&& o.getY() == y && o.getZ() == height).findFirst();
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
		objects.stream().filter(o -> o.getId() == id && o.getZ() == height).forEach(this::remove);
	}
	
	/**
	 * Removes a global object from the world based on object reference
	 * @param object	the global object
	 */
	public void remove(GameObject object) {
		updateObject(object, -1);
		remove.add(object);
		Region.removeClipping(object);
	}
	
	/*public void replaceObject(GameObject gameObject, GameObject replacementObject, int objectRespawnTimer) {
		remove(gameObject);
		add(replacementObject);
	}*/
	
	public void replaceObject(final GameObject original, final GameObject replacement, int cycles) {
		remove(original);
		if (replacement != null) {
			add(replacement);
		}
		if (cycles < 0)
			return;
		World.getWorld().schedule(new Task(cycles) {
			@Override
			public void execute() {
				if (replacement != null) {
					remove(replacement);
				}
				GameObject addOrig = new GameObject(original.getLocation(), original.getId(), original.getType(),
						original.getDirection());
				add(addOrig);
				stop();
			}
		});
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
		return objects.stream().anyMatch(object -> object.getId() == id && object.getX() == location.getX() && object.getY() == location.getY() && object.getZ() == location.getZ());
	}
	
	/**
	 * Determines if any object exists in the game world at the specified location
	 * @param x			the x location of the object
	 * @param y			the y location of the object
	 * @param height	the height location of the object
	 * @return			true if the object exists, otherwise false.
	 */
	public boolean anyExists(int x, int y, int height) {
		return objects.stream().anyMatch(object ->object.getX() == x && object.getY() == y && object.getZ() == height);
	}
	
	public GameObject get(int id, int x, int y, int height) {
		Optional<GameObject> obj = objects.stream().filter(object -> object.getId() == id && object.getX() == x
				&& object.getY() == y && object.getZ() == height).findFirst();
		return obj.orElse(null);
		
	}
	
	public GameObject customOrCache(int id, Location l) {
		GameObject spawned = get(id, l.getX(), l.getY(), l.getZ());
		if (spawned != null)
			return spawned;
		return RegionStoreManager.get().getGameObject(l, id);
	}
	
	/**
	 * All global objects have a unique value associated with them that is referred to as ticks remaining.
	 * Every six hundred milliseconds each object has their amount of ticks remaining reduced. Once an 
	 * object has zero ticks remaining the object is replaced with it's counterpart. If an object has a
	 * tick remaining value that is negative, the object is never removed unless indicated otherwise.
	 */
	public void pulse() {
		 long start = System.currentTimeMillis();
		if (objects.size() == 0) {
			return;
		}
		Queue<GameObject> updated = new LinkedList<>();
		GameObject object = null;
		objects.removeAll(remove);
		remove.clear();
		// note; gonna add a pointless amount of overhead removing and readding unless ticks were zero every game cylce LUL
		final int toCheck = objects.size();
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
		long end = (System.currentTimeMillis() - start);
		GameEngine.profile.objs = end;
        //System.out.println("[GlobalObjects] it took "+end+"ms for "+toCheck+" custom spawned objs.");
	}
	
	/**
	 * Updates a single global object with a new object id in the game world for every player within a region.
	 * @param object	the new global object
	 * @param objectId	the new object id
	 */
	public void updateObject(final GameObject object, final int objectId) {
		List<Player> players = World.getWorld().getPlayers().stream().filter(Objects::nonNull).filter(player ->
			player.distanceToPoint(object.getX(), object.getY()) <= 60 && player.getZ() == object.getZ()).collect(Collectors.toList());
		players.forEach(player -> player.getActionSender().sendObject(objectId, object.getX(), object.getY(), object.getZ(), object.getDirection(), object.getType()));
	}
	
	/**
	 * Updates all region objects for a specific player
	 * @param player	the player were updating all objects for
	 */
	public void updateRegionObjects(Player player) {
		objects.stream().filter(Objects::nonNull).filter(object -> player.distanceToPoint(
			object.getX(), object.getY()) <= 60 && object.getZ() == player.getZ()).forEach(object -> player.getActionSender().sendObject(
				object.getId(), object.getX(), object.getY(), object.getZ(), object.getDirection(), object.getType()));
		loadCustomObjects(player);
	}

}
