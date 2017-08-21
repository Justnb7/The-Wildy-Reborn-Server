package com.venenatis.game.content.skills.thieving;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.npc.pet.Pets;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

/**
 * The thieving skill.
 * Credits to Jason for some of the comments.
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 * 
 */
public class Thieving {
	
	/**
	 * The managing player of this class
	 */
	private Player player;
	
	/**
	 * The last interaction that player made that is recorded in milliseconds
	 */
	private long lastInteraction;
	
	/**
	 * The constant delay that is required inbetween interactions
	 */
	private static final long INTERACTION_DELAY = 2_000L;
	
	/**
	 * The stealing animation
	 */
	private final Animation STEAL_ANIMATION = Animation.create(881);
	
	private final int EXP_MODIFIER = 2;
	
	/**
	 * Constructs a new {@link Thieving} object that manages interactions 
	 * between players and stalls, as well as players and non playable characters.
	 * 
	 * @param player	
	 *        the visible player of this class
	 */
	public Thieving(final Player player) {
		this.player = player;
	}
	
	/**
	 * When we pass all the checks, we can goahead and steal from the stall.
	 * @param stall
	 *        The stall were stealing from
	 * @param objectId
	 *        The stallId
	 */
	public void stealFromStall(Stalls stall, int objectId) {
		
		//We can only steal once per 2 seconds
		if (System.currentTimeMillis() - lastInteraction < INTERACTION_DELAY) {
			return;
		}
		
		// Level requirement check
		if (player.getSkills().getLevel(Skills.THIEVING) < stall.getRequirement()) {
			player.getActionSender().sendMessage("You need a thieving level of " + stall.getRequirement() + " to steal from this stall.");
			return;
		}
		
		//Check if we have enough inventory space
		if (player.getInventory().getFreeSlots() == 0) {
			player.getActionSender().sendMessage("You need at least one free slot to steal from this stall.");
			return;
		}
		
		//Play the animation
		player.playAnimation(STEAL_ANIMATION);
		
		//Reward the player
		Item lootReceived = Utility.randomElement(stall.getLoot());
		player.getInventory().add(new Item(lootReceived.getId(), lootReceived.getAmount()));
		
		//Finish, we receive experience and now we can set the delay.
		player.getSkills().addExperience(Skills.THIEVING, stall.getExperience() *EXP_MODIFIER);
		
		//After the experience drop we add the random pet chance
				pet(player);
		lastInteraction = System.currentTimeMillis();
		
	}
	
	/**
	 * Once we pass all the checks, we can goahead and pickpocket the npc.
	 * @param pickpocket
	 *        The npc were pickpocketing
	 * @param npc
	 *        The npcId
	 */
	public void pickpocket(Pickpocket pickpocket, NPC npc) {
		//face the npc
		player.face(player, new Location(npc.getX(), npc.getY()));
		
		//We can only pickpocket once per 2 seconds
		if (System.currentTimeMillis() - lastInteraction < INTERACTION_DELAY) {
			return;
		}
		
		//Level requirement check
		if (player.getSkills().getLevel(Skills.THIEVING) < pickpocket.getRequirement()) {
			player.getActionSender().sendMessage("You need a thieving level of " + pickpocket.getRequirement() + " to pickpocket this npc.");
			return;
		}
		
		//Check if we have enough inventory space
		if (player.getInventory().getFreeSlots() == 0) {
			player.getActionSender().sendMessage("You need at least one free slot to steal from this npc.");
			return;
		}
		
		//Play the animation
		player.playAnimation(STEAL_ANIMATION);
		
		//Reward the player
		Item lootReceived = Utility.randomElement(pickpocket.getLoot());
		player.getInventory().add(new Item(lootReceived.getId(), lootReceived.getAmount()));
		
		//Finish, we receive experience and now we can set the delay.
		player.getSkills().addExperience(Skills.THIEVING, pickpocket.getExperience());
		
		//After the experience drop we add the random pet chance
		pet(player);
		lastInteraction = System.currentTimeMillis();
	}
	
	private void pet(Player player) {
		int roll = Utility.random(1000);
		Pets rocky = Pets.ROCKY;
		Pet pet = new Pet(player, rocky.getNpc());
		if (player.alreadyHasPet(player, 20663) || player.getPet() == rocky.getNpc()) {
			return;
		}
		
		if (roll == 155) {//Drop pet when the roll drops on 155
			// Player already has a pet roaming
			if (player.getPet() > -1) {
				player.getInventory().addOrSentToBank(player, new Item(20663));
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rocky.", false);
			} else {
				player.setPet(rocky.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rocky.", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}
	}

}
