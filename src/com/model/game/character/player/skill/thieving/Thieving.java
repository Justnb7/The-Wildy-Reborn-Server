package com.model.game.character.player.skill.thieving;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.item.Item;
import com.model.game.location.Position;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;

/**
 * The thieving skill.
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
	
	public void stealFromStall(Stalls stall, int objectId) {
		
		//We can only steal once per 2 seconds
		if (System.currentTimeMillis() - lastInteraction < INTERACTION_DELAY) {
			return;
		}
		
		// Level requirement check
		if (player.getSkills().getLevel(Skills.THIEVING) < stall.getRequirement()) {
			player.message("You need a thieving level of " + stall.getRequirement() + " to steal from this stall.");
			return;
		}
		
		//Check if we have enough inventory space
		if (player.getItems().freeSlots() == 0) {
			player.message("You need at least one free slot to steal from this stall.");
			return;
		}
		
		//Play the animation
		player.playAnimation(STEAL_ANIMATION);
		
		//Reward the player
		Item lootReceived = Utility.randomElement(stall.getLoot());
		player.getItems().addItem(lootReceived.getId(), lootReceived.getAmount());
		
		//Finish, we receive experience and now we can set the delay.
		player.getSkills().addExperience(Skills.THIEVING, stall.getExperience());
		lastInteraction = System.currentTimeMillis();
		
	}
	
	public void pickpocket(Pickpocket pickpocket, Npc npc) {
		//face the npc
		player.face(player, new Position(npc.getX(), npc.getY()));
		
		//We can only pickpocket once per 2 seconds
		if (System.currentTimeMillis() - lastInteraction < INTERACTION_DELAY) {
			return;
		}
		
		//Level requirement check
		if (player.getSkills().getLevel(Skills.THIEVING) < pickpocket.getRequirement()) {
			player.message("You need a thieving level of " + pickpocket.getRequirement() + " to pickpocket this npc.");
			return;
		}
		
		//Check if we have enough inventory space
		if (player.getItems().freeSlots() == 0) {
			player.message("You need at least one free slot to steal from this npc.");
			return;
		}
		
		//Play the animation
		player.playAnimation(STEAL_ANIMATION);
		
		//Reward the player
		Item lootReceived = Utility.randomElement(pickpocket.getLoot());
		player.getItems().addItem(lootReceived.getId(), lootReceived.getAmount());
		
		//Finish, we receive experience and now we can set the delay.
		player.getSkills().addExperience(Skills.THIEVING, pickpocket.getExperience());
		lastInteraction = System.currentTimeMillis();
	}

}
