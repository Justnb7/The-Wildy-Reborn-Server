package com.model.task.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.content.clicking.object.ObjectInteraction;
import com.model.game.location.Location;
import com.model.task.ScheduledTask;

/**
 * This task handles walking towards objects.
 * 
 * @author Patrick van Elderen
 * @date 24-5-2017
 *
 */
public class WalkToObjectTask extends ScheduledTask {
	
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
		
		if (player.getLocation().isWithinInteractionDistance(loc)) {
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
		} else {
			player.setDistancedTask(new DistancedActionTask() {
				@Override
				public void onReach() {
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
				}
				@Override
				public boolean reached() {
					stop();
					return player.distanceToPoint(loc.getX(), loc.getY()) < 2;
				}
			});
		}
		player.face(player, loc);
		stop();
	}

}
