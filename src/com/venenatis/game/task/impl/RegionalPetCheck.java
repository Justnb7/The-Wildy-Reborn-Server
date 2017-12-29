package com.venenatis.game.task.impl;

import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.pet.Follower;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;

/**
 * A regional pet check, for respawning pets out of distance
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 * @author <a href="http://www.rune-server.org/members/Shadowy/">Jak</a>
 */
public class RegionalPetCheck extends Task {

	/**
	 * The player who owns this pet
	 */
	private Player player;
	
	/**
	 * The pet the player has spawned
	 */
	private NPC pet;
	
	/**
	 * The constructor of the regional check.
	 * 
	 * @param player
	 *            The player which spawned the pet
	 * @param pet
	 *            The pet which is spawned
	 * @param login
	 *            Are we activating this check upon login?
	 */
	public RegionalPetCheck(Player player, NPC pet, boolean login){
		super(4);
		this.player = player;
		this.pet = pet;
	}

	@Override
	public void execute() {
		// Pet despawned or owner offline
		if (player.getIndex() < 1 || pet.getIndex() < 1) {
			stop();
			return;
		}
		
		int delta = player.getLocation().distance(pet.getLocation());

		if (delta >= 13 || delta <= -13) {
			World.getWorld().unregister(pet);
			
			// create new instance
			pet = new Follower(player, pet.getId());
			pet.setLocation(player.getLocation());
			player.setAttribute("pet", pet);
			World.getWorld().register(pet);
		}
	}

}