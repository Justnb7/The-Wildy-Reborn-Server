package com.venenatis.game.content.skills.agility.rooftops;

import com.venenatis.game.content.skills.agility.rooftops.impl.AlKharidRooftop;
import com.venenatis.game.content.skills.agility.rooftops.impl.ArdougneRooftop;
import com.venenatis.game.content.skills.agility.rooftops.impl.DraynorRooftop;
import com.venenatis.game.content.skills.agility.rooftops.impl.SeersRooftop;
import com.venenatis.game.content.skills.agility.rooftops.impl.VarrockRooftop;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.npc.pet.Follower;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
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
	
	private static void pet(Player player) {
		Pet pets = Pet.GIANT_SQUIRREL;
		Follower pet = new Follower(player, pets.getNpc());
		
		if(player.alreadyHasPet(player, 20659) || player.getPet() == pets.getNpc()) {
			return;
		}
		
		int random = Utility.random(1500);
		if (random == 0) {
			if (player.getPet() > -1) {
				player.getInventory().addOrSentToBank(player, new Item(20659));
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Giant squirrel.", false);
			} else {
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Giant squirrel.", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}
	}
	
	/**
	 * The last interaction that player made that is recorded in milliseconds
	 */
	private static long last_mark_of_grace_drop;
	
	/**
	 * The constant delay that is required inbetween mark of grace drops
	 */
	private static final long DROP_DELAY = 3_000L;
	
	/**
	 * Mark of grace
	 */
	private final static Item MARK_OF_GRACE = new Item(11849);
	
	/**
	 * The locations of the marks of grace, in Seers village
	 */
	private static int[][] SEERS_COORDINATES = { 
			{ 2728, 3495, 3 }, 
			{ 2707, 3492, 2 }, 
			{ 2713, 3479, 2 },
			{ 2698, 3463, 2 } 
	};
	
	/**
	 * The locations of the marks of grace, in Varrock
	 */
	private static int[][] VARROCK_COORDINATES = { 
			{ 3219, 3418, 3 }, 
			{ 3202, 3417, 3 }, 
			{ 3195, 3416, 1 },
			{ 3196, 3404, 3 }, 
			{ 3193, 3393, 3 }, 
			{ 3205, 3403, 3 }, 
			{ 3218, 3395, 3 }, 
			{ 3240, 3411, 3 } 
	};
	
	private static int[][] ARDOUGNE_COORDINATES = { 
			{ 2671, 3303, 3 }, 
			{ 2663, 3318, 3 }, 
			{ 2655, 3318, 3 },
			{ 2653, 3312, 3 }, 
			{ 2651, 3307, 3 }, 
			{ 2653, 3302, 3 }, 
			{ 2656, 3297, 3 }, 
			{ 2668, 3297, 0 } 
	};
	
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
			pet(player);
			return true;
		}
		if(AlKharidRooftop.start(player, object)) {
			pet(player);
			return true;
		}
		if(VarrockRooftop.start(player, object)) {
			pet(player);
			return true;
		}
		if(SeersRooftop.start(player, object)) {
			pet(player);
			return true;
		}
		if(ArdougneRooftop.start(player, object)) {
			pet(player);
			return true;
		}
		return false;
	}
	
	public static void marks_of_grace(Player player, String location) {
		//Safety check
		if(player == null) {
			return;
		}
		
		//We can't receive any marks.
		if(!can_receive_marks(player)) {
			return;
		}
		
        int chance = 0;
		
		switch (location) {
		case "ARDOUGNE":
			chance = player.getSkills().getLevel(Skills.AGILITY) / 17;
			break;
			
		case "SEERS":
			chance = player.getSkills().getLevel(Skills.AGILITY) / 17;
			break;
			
		case "VARROCK":
			chance = player.getSkills().getLevel(Skills.AGILITY) / 17;
			break;
		}
		
		int index = Utility.random(location == "SEERS" ? SEERS_COORDINATES.length - 1 : location == "ARDOUGNE" ? ARDOUGNE_COORDINATES.length - 1 : VARROCK_COORDINATES.length - 1);
		int x = location == "SEERS" ? SEERS_COORDINATES[index][0] : location == "ARDOUGNE" ? ARDOUGNE_COORDINATES[index][0] : VARROCK_COORDINATES[index][0];
		int y = location == "SEERS" ? SEERS_COORDINATES[index][1] : location == "ARDOUGNE" ? ARDOUGNE_COORDINATES[index][1] : VARROCK_COORDINATES[index][1];
		int z = location == "SEERS" ? SEERS_COORDINATES[index][2] : location == "ARDOUGNE" ? ARDOUGNE_COORDINATES[index][2] : VARROCK_COORDINATES[index][2];
		
		if (Utility.random(chance) == 0) {
			if (System.currentTimeMillis() - last_mark_of_grace_drop < DROP_DELAY) {
				return;
			}
			GroundItemHandler.createGroundItem(new GroundItem(MARK_OF_GRACE, new Location(x, y, z), player));
			last_mark_of_grace_drop = System.currentTimeMillis();
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