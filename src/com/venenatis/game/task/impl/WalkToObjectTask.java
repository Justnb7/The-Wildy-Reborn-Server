package com.venenatis.game.task.impl;

import com.venenatis.game.content.clicking.objects.ObjectInteraction;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.pathfinder.PathState;
import com.venenatis.game.world.pathfinder.impl.DefaultPathFinder;
import com.venenatis.game.world.pathfinder.impl.ObjectPathFinder;
import com.venenatis.game.world.pathfinder.region.RegionStore;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;
import com.venenatis.server.Server;

/**
 * This task handles walking towards objects.
 * 
 * @author Patrick van Elderen
 * @date 24-5-2017
 *
 */
public class WalkToObjectTask extends Task {
	
	/**
	 * The location of the object
	 * 
	 */
	private final Location loc;
	
	/**
	 * The game object
	 */
	private final int object;

	/**
	 * The player interacting with the npc
	 */
	private final Player player;

	/**
	 * The object click action
	 */
	private final int clickAction;
	private PathState path = null;
	private int finalX, finalY;
	
	/**
	 * Create a new {@link WalkToObjectTask}.
	 * 
	 * @param location
	 *            the location of the object that we're interacting with.
	 * @param object
	 *            the object that we're interacting with.
	 * @param player
	 *            the player that is interacting with the npc.
	 * @param action
	 *            the click option, objects have 3 or 4 click actions
	 */
	public WalkToObjectTask(Player player, Location location, int object, int action) {
		super(1, true); // Needs to be instantly executed
		this.loc = location;
		this.object = object;
		this.player = player;
		this.clickAction = action;

		// do the path
		// Client isnt very happy with this shit so we have to hard call it
		if (object == 10357 && location.getX() == 3318 && location.getY() == 3166) {
			final GameObject obj = RegionStoreManager.get().getGameObject(new Location(location.getX(), location.getY(), player.getZ()), object);
			path = ObjectPathFinder.find(player, obj);
		}

		if (object == 10777 && location.getX() == 3191 && location.getY() == 3415) {
			final GameObject obj = RegionStoreManager.get().getGameObject(new Location(location.getX(), location.getY(), player.getZ()), object);
			path = ObjectPathFinder.find(player, obj);
		}

		if (object == 10355 && location.getX() == 3269 && location.getY() == 3166 && player.getZ() == 3) {
			path = player.doPath(new DefaultPathFinder(), null, 3265, 3166, false, true);
		}
		if (path == null) {
			final GameObject obj = Server.getGlobalObjects().customOrCache(object, loc);
			if (obj == null) {
				this.stop();
				return;
			}
			path = ObjectPathFinder.find(player, obj);
			// NOTE: you might have to add exceptions to a couple object ids here like agility obstacles
			if (!path.isRouteFound()) {
				player.getWalkingQueue().reset();
				player.getActionSender().sendMessage("I can't reach that!");
				this.stop();
			}
			// here is the path to method
		}
		if (path != null && path.getPoints().peekLast() != null) {
			finalX = path.getPoints().getLast().getX();
			finalY = path.getPoints().getLast().getY();
		} else {
			finalX = player.getLocation().getX();
			finalY = player.getLocation().getY();
		}
	}

	@Override
	public void execute() {
		if (!player.isActive()) {
			this.stop();
			return;
		}

		final GameObject obj = Server.getGlobalObjects().customOrCache(object, loc);
		
		//Safety
		if (obj == null) {
			System.err.println("Non existant obj "+object+" @ "+loc+" (tree despawned or cheat client)");
			stop();
			return;
		}
		
		if (RegionStore.reached(player.getLocation(), finalX, finalY, finalX, finalY, path.walkToData)) {
			// in distance. interact and stop cycle.
			switch (clickAction) {
			case 1:
				 ObjectInteraction.handleFirstClickAction(player, loc, object);
				break;
			case 2:
				 ObjectInteraction.handleSecondClickAction(player, loc, object);
				break;
			case 3:
				 ObjectInteraction.handleThirdClickAction(player, loc, object);
				break;
			}
			stop();
			// reached target. face coords.
			player.faceObject(obj);
			player.following().setFollowing(null);
		} else {
			// do nothing this cycle. try again next time this Task is executed.
			//player.message("sleep on task");
		}
	}

}