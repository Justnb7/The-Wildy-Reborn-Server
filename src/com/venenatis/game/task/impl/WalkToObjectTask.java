package com.venenatis.game.task.impl;

import com.venenatis.game.content.clicking.objects.ObjectInteraction;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;

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
	}

	@Override
	public void execute() {
		if (!player.isActive()) {
			this.stop();
			return;
		}
		
		
		final GameObject obj = RegionStoreManager.get().getGameObject(loc, object);
		
		//Safety
		if (obj == null) {
			System.err.println("Non existant obj "+object+"@"+loc+" (tree despawned or cheat client)");
			stop();
			return;
		}
		
		if (player.getLocation().isNextTo(loc) || loc.equals(player.getLocation())
				|| (object == 10777 && player.getLocation().equals(new Location(3194, 3416, 1)))
				|| (object == 10355 && player.getLocation().equals(new Location(3265, 3166, 3)))
				|| (object == 23134 && player.getLocation().equals(new Location(2474, 3426, 0)))
				|| (object == 20210 && player.getLocation().equals(new Location(2552, 3561, 0)))
				|| (object == 23131 && player.getLocation().equals(new Location(2551, 3554, 0)))
				|| (object == 20211 && player.getLocation().equals(new Location(2539, 3546, 0)))) {
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
			player.message("sleep on task");
		}
	}

}
