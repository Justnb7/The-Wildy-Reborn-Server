package com.model.game.item;

import cache.definitions.AnyRevObjectDefinition;
import com.model.game.character.player.Player;
import com.model.game.character.player.skill.cooking.Cookables;
import com.model.game.character.player.skill.cooking.Cooking;
import com.model.utility.Utility;
import cache.definitions.r317.ObjectDefinition317;

/**
 * @author Sanity
 * @author Ryan
 * @author Lmctruck30 Revised by Shawn Notes by Shawn
 */

public class UseItem {

	/**
	 * Using items on an object.
	 * 
	 * @param c
	 * @param object
	 * @param objectX
	 * @param objectY
	 * @param item
	 */
	public static void ItemonObject(Player player, int object, int objectX, int objectY, Item item) {
		AnyRevObjectDefinition def = AnyRevObjectDefinition.get(object);
		if (!player.getInventory().contains(item.getId()))
			return;
		if (def.getName().toLowerCase().contains("altar") && def.getActions()[0].toLowerCase().contains("pray")) {
			player.getSkills().getPrayer().bonesOnAltar(item);
			return;
		}
		
		switch (def.getName().toLowerCase()) {
			
		case "range":
		case "cooking range":
		case "stove":
		case "fire":
			if (Cookables.isCookable(item.getId())) {
				Cooking.attemptCooking(player, item.getId(), object);
				return;
			}
			break;
		}
		
		switch (object) {
			
		default:
			if (player.inDebugMode())
				Utility.println("Player At Object id: " + object + " with Item id: " + item);
			break;
		}

	}

}
