package com.venenatis.game.content.skills.agility.course;

import com.venenatis.game.content.skills.agility.course.impl.*;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.npc.pet.Follower;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;

public class Course {
	
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
	 * Execute a agility course based on object clicks.
	 * 
	 * @param player
	 *            The player attempting to perform a course
	 * @param object
	 *            The object being clicked
	 */
	public static boolean execute(Player player, GameObject object) {
		if(GnomeCourse.start(player, object)) {
			pet(player);
			return true;
		}
		if(BarbarianCourse.start(player, object)) {
			pet(player);
			return true;
		}
		return false;
	}

}
