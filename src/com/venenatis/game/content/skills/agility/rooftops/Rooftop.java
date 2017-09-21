package com.venenatis.game.content.skills.agility.rooftops;

import java.util.Random;

import com.venenatis.game.content.skills.agility.rooftops.impl.*;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.ground_item.GroundItemHandler;
import com.venenatis.game.world.object.GameObject;

/**
 * The class which represents functionality for the all the rooftop courses.
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van
 *         Elderen</a>
 *
 */
public class Rooftop {
	
	
	/**
	 * The random number generator
	 */
	private final static Random random = new Random();
	
	/**
	 * Mark of grace
	 */
	private final static Item MARK_OF_GRACE = new Item(11849);
	
	/**
	 * Execute a single rooftop course based on object clicks.
	 * 
	 * @param player
	 *            The player attempting to perform a course
	 * @param object
	 *            The object being clicked
	 */
	public static boolean execute(Player player, GameObject object) {
		if(DraynorRooftop.start(player, object)) {
			return true;
		}
		if(AlKharidRooftop.start(player, object)) {
			return true;
		}
		if(VarrockRooftop.start(player, object)) {
			return true;
		}
		return false;
	}
	
	public static void marks_of_grace(Player player, Location location) {
		//Safety check
		if(player == null) {
			return;
		}
		
		//We can't receive any marks.
		if(!can_receive_marks(player)) {
			return;
		}
		
		//We have a 20% chance of receiving a mark of grace
		if(random.nextInt(10) <= 2) {
			GroundItemHandler.createGroundItem(new GroundItem(MARK_OF_GRACE, location, player));
			player.debug("spawned marks of grace @"+location.toString());
		}
	}
	
	private final static boolean can_receive_marks(Player player) {
		//We can't receive marks of grace when we're logged out, teleporting or dead.
		if (!player.isActive() || player.getTeleportAction().isTeleporting() || player.getCombatState().isDead()) {
			return false;
		}
		return true;
	}

}