package com.venenatis.game.content.clicking.objects;

import com.venenatis.game.cache.definitions.AnyRevObjectDefinition;
import com.venenatis.game.content.skills.cooking.Cookables;
import com.venenatis.game.content.skills.cooking.Cooking;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;

public class ItemOnObjectInteract {
	
	public static void handle(Player player, int obj, Location loc, Item item) {
		AnyRevObjectDefinition def = AnyRevObjectDefinition.get(obj);
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
				Cooking.attemptCooking(player, item.getId(), obj);
				return;
			}
			break;
		}
		
		switch (obj) {
			
		default:
			if (player.inDebugMode())
				Utility.println("Player At Object id: " + obj + " with Item id: " + item);
			break;
		}
	}

}