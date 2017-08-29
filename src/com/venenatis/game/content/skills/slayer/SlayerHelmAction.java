package com.venenatis.game.content.skills.slayer;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;

/**
 * The class which represents functionality for the slayer helmet creation.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class SlayerHelmAction {

	private final static Item[] items = { new Item(4168), new Item(4164), new Item(4166), new Item(4551), new Item(4155), new Item(8901) };


	/**
	 * Slayer helmet creation
	 * 
	 * @param player
	 *            The player attempting to create the slayer helmet
	 * @param used
	 *            The items used
	 * @param with
	 *            The items used {@linkplain with} with parameter {@linkplain used}
	 */
    public static boolean handleItemOnItem(Player player, Item used, Item with) {
        
    	//Checks if you're using to correct items on echater
    	if (!isRequirement(used) || !isRequirement(with)) {
            return false;
        }
        
        //You need to unlock the feature first
        if (!player.getSlayerInterface().getUnlocks().containsKey(91115)) {
            player.getActionSender().sendMessage("You have not yet unlocked this feature, Unlock it by speaking with your Slayer master.");
            return false;
        }
        
        //The items required to make a slayer helmet
        for (Item requirement : items) {
            if (!player.getInventory().contains(requirement.getId())) {
                player.getActionSender().sendMessage("You need a nosepeg, facemask, earmuffs, spiny helmet, Slayer gem and a black mask in your inventory in order to construct a Slayer helm.");
                return false;
            }
        }
        
        //You need a crafting level of 55 in order to make slayer helmets
        if (player.getSkills().getLevelForExperience(Skills.CRAFTING) < 55) {
            player.getActionSender().sendMessage("You need a crafting level of 55 to complete this action.");
            return false;
        }
        
        //Removes all the required items
        for (Item requirement : items) {
            player.getInventory().remove(requirement);
        }
        
        //Awards the player with the slayer helmet
        player.getInventory().add(new Item(11864, 1));
        player.getActionSender().sendMessage("You combine the items into a Slayer helm.");
        return true;
    }

	/**
	 * Check if we have all the required items
	 * 
	 * @param item
	 *            The required items
	 */
    public static boolean isRequirement(Item item) {
        for (Item requirements : items) {
            if (item.getId() == requirements.getId()) {
                return true;
            }
        }
        return false;
    }
    
}
