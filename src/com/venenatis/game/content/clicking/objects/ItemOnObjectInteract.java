package com.venenatis.game.content.clicking.objects;

import com.venenatis.game.cache.definitions.AnyRevObjectDefinition;
import com.venenatis.game.content.ArmourSets;
import com.venenatis.game.content.skills.cooking.Cookables;
import com.venenatis.game.content.skills.cooking.Cooking;
import com.venenatis.game.content.skills.farming.Farming;
import com.venenatis.game.content.skills.smithing.SmithingConstants;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;

public class ItemOnObjectInteract {
	
	public static void handle(Player player, int obj, Location loc, Item item) {
		final GameObject gameObject = RegionStoreManager.get().getGameObject(loc, obj);
		AnyRevObjectDefinition def = AnyRevObjectDefinition.get(obj);
		if (!player.getInventory().contains(item.getId()))
			return;
		
		if (def.getName().toLowerCase().contains("altar") && def.getActions()[0].toLowerCase().contains("pray")) {
			player.getSkills().getPrayer().bonesOnAltar(item);
			return;
		}
		
		if (Farming.prepareCrop(player, item.getId(), obj, loc.getX(), loc.getY())) {
			return;
		}
		
		switch (def.getName().toLowerCase()) {
		
		case "anvil":
			if (SmithingConstants.useBarOnAnvil(player, obj, item.getId())) {
				return;
			}
			break;
			
		case "range":
		case "cooking range":
		case "stove":
		case "fire":
			if (Cookables.isCookable(item.getId())) {
				Cooking.showInterface(player, gameObject, item);
			}
			/*if (Cookables.isCookable(item.getId())) {
				Cooking.attemptCooking(player, item.getId(), obj);
				return;
			}*/
			break;
			
		/* Bank */
		case "bank":
		case "Bank":
		case "bank booth":
		case "booth":
		case "bank chest":
			if (ArmourSets.isSet(player, item.getId())) {
				ArmourSets.openSet(player, item.getId());
				return;
			}
			
			if (!item.isNoted()) {
				return;
			}
			
			int amount = item.getAmount();
			
			int space = player.getInventory().getFreeSlots();
			
			if (space == 0) {
				return;
			}
			
			if (space > amount)
				amount = space;
			
			player.getInventory().remove(item.getId(), space);
			
			int unnoted = item.unnoted().getId();
			
			player.getInventory().add(new Item(unnoted, space), true);
			break;
		
		}
		
		switch (obj) {
			
		default:
			player.debug(String.format("Player At Object id: %d with Item id: %s%n",obj, item));
			break;
		}
	}

}