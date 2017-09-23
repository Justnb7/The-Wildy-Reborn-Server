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
		super(1, false);
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
		
		//The distance that is required to activate to object
		int distance_required = 1;
		
		final GameObject obj = RegionStoreManager.get().getGameObject(loc, object);
		
		//Safety
		if (obj == null) {
			System.err.println("Non existant obj "+object+"@"+loc+" (tree despawned or cheat client)");
			stop();
			return;
		}
		
		switch (obj.getId()) {
		case 10355:
		case 23131:
			distance_required = 3;//I set 3 tiles
			break;
		case 10777:
		case 23134://Two tiles
			distance_required = 2;
			break;
		}
		
		//Exactly some objects have different activation tiles like 3+ i tried to work a better way as you can see above but for some reason it doesnt work
		//Any idea if im doing it right?

		//We added the check here its an AND statement i tried || aswell
		if (loc.isWithinDistance(loc, distance_required) || player.getLocation().isNextTo(loc) || loc.equals(player.getLocation())/*
				|| (object == 10777 && player.getLocation().equals(new Location(3194, 3416, 1)))
				|| (object == 10355 && player.getLocation().equals(new Location(3265, 3166, 3)))*/) {
			// yeah so like did above with al kharid hardcode u need to hardcode that too
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
