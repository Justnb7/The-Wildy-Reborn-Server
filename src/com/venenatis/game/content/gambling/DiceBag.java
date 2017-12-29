package com.venenatis.game.content.gambling;

import java.util.Random;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.clan.ClanManager;

/**
 * This class represents functionality for the dice bag.
 * @author Patrick van Elderen
 *
 */
public class DiceBag {
	
	/**
	 * The last interaction that player made that is recorded in milliseconds
	 */
	private static long lastInteraction;
	
	/**
	 * The constant delay that is required inbetween interactions
	 */
	private static final long INTERACTION_DELAY = 2_000L;
	
	/**
	 * The dice bag
	 */
	private static Item DICE_BAG = new Item(22016);
	
	/**
	 * A random multiplier
	 */
	private static Random RANDOM = new Random();
	
	/**
	 * A method used to sent a dice message to the clan chat.
	 * 
	 * @param player
	 *            The player rolling the dice
	 */
	public static void rollDice(final Player player) {
		
		//Safety checks
		if(!player.getInventory().contains(DICE_BAG) || player == null) {
			return;
		}
		
		//We can roll up to 100
		int roll = RANDOM.nextInt(100);
		
		//The message thats being sent to the clan chat
		String message = "<img=25>[Gamble]: "+player.getUsername()+" just rolled a "+roll+" on the percentile dice.";
		
		//Checks if we're in a clan chat
		if (player.getClan() == null) {
			player.getActionSender().sendMessage("You have to join a clan chat in order to roll the dice.");
			return;
		} else {
			//Only allow the dice to be sent once per two seconds, otherwise SPAM!
			if (System.currentTimeMillis() - lastInteraction < INTERACTION_DELAY) {
				return;
			}
			//Sent the message to the clan chat
			//TODO
			//ClanManager.message(player, message);
			
			//Set the delay
			lastInteraction = System.currentTimeMillis();
		}
	}

}
