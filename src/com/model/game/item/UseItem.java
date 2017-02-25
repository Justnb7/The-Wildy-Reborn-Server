package com.model.game.item;

import com.model.game.character.player.Player;
import com.model.game.character.player.skill.impl.Cooking;
import com.model.game.character.player.skill.impl.Cooking.Cookables;
import com.model.utility.Utility;
import com.model.utility.cache.ObjectDefinition;

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
	public static void ItemonObject(Player player, int object, int objectX, int objectY, int item) {
		ObjectDefinition def = ObjectDefinition.getObjectDef(object);
		if (!player.getItems().playerHasItem(item, 1))
			return;
		if (def.getName().toLowerCase().contains("altar") && def.actions[0].toLowerCase().contains("pray")) {
			player.getSkills().getPrayer().bonesOnAltar(item);
			return;
		}
		
		switch (def.name.toLowerCase()) {
			
		case "range":
		case "cooking range":
		case "stove":
		case "fire":
			if (Cookables.isCookable(item)) {
				Cooking.attemptCooking(player, item, object);
				return;
			}
			break;
		}
		
		switch (object) {
			
		default:
			if (player.in_debug_mode())
				Utility.println("Player At Object id: " + object + " with Item id: " + item);
			break;
		}

	}

}
