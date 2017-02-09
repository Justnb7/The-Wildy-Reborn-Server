package com.model.game.item;

import com.model.game.character.player.Player;
import com.model.game.character.player.content.CrystalChest;
import com.model.game.character.player.content.PotionCombinating;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.crafting.gem.GemCutting;
import com.model.game.character.player.skill.crafting.jewelry.JewelleryMaking;
import com.model.game.character.player.skill.crafting.leather.LeatherMaking;
import com.model.game.character.player.skill.fletching.Fletching;
import com.model.game.character.player.skill.fletching.FletchingHandler;
import com.model.game.character.player.skill.fletching.FletchingHandler.Bolts;
import com.model.game.character.player.skill.impl.Cooking;
import com.model.game.character.player.skill.impl.Cooking.Cookables;
import com.model.game.character.player.skill.impl.Firemaking;
import com.model.game.character.player.skill.impl.Runecrafting;
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
		player.getFarming().patchObjectInteraction(object, item, objectX, objectY);
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
		
		if (item == 1438 && object == 2452) {
			Runecrafting.enterAirAltar(player);
		} else if (item == 1440 && object == 2455) {
			Runecrafting.enterEarthAltar(player);
		} else if (item == 1442 && object == 2456) {
			Runecrafting.enterFireAltar(player);
		} else if (item == 1444 && object == 2454) {
			Runecrafting.enterWaterAltar(player);
		} else if (item == 1446 && object == 2457) {
			Runecrafting.enterBodyAltar(player);
		} else if (item == 1448 && object == 2453) {
			Runecrafting.enterMindAltar(player);
		}
		switch (object) {
		
		case 2097:
			player.getSmithingInt().showSmithInterface(item);
			break;
			
		default:
			if (player.in_debug_mode())
				Utility.println("Player At Object id: " + object + " with Item id: " + item);
			break;
		}

	}

	/**
	 * Using items on items.
	 * 
	 * @param c
	 * @param itemUsed
	 * @param useWith
	 */
	public static void ItemonItem(final Player player, final int itemUsed, final int useWith, final int itemUsedSlot, final int usedWithSlot) {
		GameItem gameItemUsed = new GameItem(itemUsed, player.playerItemsN[itemUsedSlot], itemUsedSlot);
		GameItem gameItemUsedWith = new GameItem(useWith, player.playerItemsN[itemUsedSlot], usedWithSlot);
		
		Cooking.makeCake(player, itemUsed, useWith);
		
		CrystalChest.createKey(player);
		
		if (PotionCombinating.get().isPotion(gameItemUsed) && PotionCombinating.get().isPotion(gameItemUsedWith)) {
			if (PotionCombinating.get().matches(gameItemUsed, gameItemUsedWith)) {
				PotionCombinating.get().mix(player, gameItemUsed, gameItemUsedWith);
			} else {
				player.write(new SendMessagePacket("You cannot combine two potions of different types."));
			}
			return;
		}
		
		for (int ref : Fletching.refItems) {
			if (itemUsed == ref || useWith == ref) {
				if (player.playerIsWoodcutting) {
					player.playerIsWoodcutting = false;
					return;
				}
				FletchingHandler.appendType(player, itemUsed, useWith);

			}
		}
		
		for (final Bolts bolt : Bolts.values()) {
			if (itemUsed == bolt.getInput1() || useWith == bolt.getInput1()) {
				FletchingHandler.appendType(player, itemUsed, useWith);
				return;
			}
		}
		
		if (itemUsed == 1755 || useWith == 1755) {
			GemCutting.attemptGemCutting(player, itemUsed, useWith);
			return;
		}
		
		if (itemUsed == 1733 || useWith == 1733) {
			LeatherMaking.craftLeatherInterface(player, itemUsed, useWith);
		}
		
		if (itemUsed == 1759 || useWith == 1759) {
			JewelleryMaking.mouldItem(player, itemUsed, useWith);
		}
		
		if (Firemaking.playerLogs(itemUsed, useWith)) {
			Firemaking.grabData(player, itemUsed, useWith);
			return;
		}
		
		if (itemUsed == 227 || useWith == 227) {
			int primary = itemUsed == 227 ? useWith : itemUsed;
			player.getHerblore().mix(primary);
			return;
		}

		if (itemUsed == 227 || useWith == 227) {
			int primary = itemUsed == 227 ? useWith : itemUsed;
			player.getHerblore().mix(primary);
			return;
		}
		
		switch (itemUsed) {
		

		default:
			if (player.in_debug_mode())
				Utility.println("Player used Item id: " + itemUsed + " with Item id: " + useWith);
			break;
		}
	}

	/**
	 * Using items on NPCs.
	 * 
	 * @param c
	 * @param itemId
	 * @param npcId
	 * @param slot
	 */
	public static void ItemonNpc(Player c, int itemId, int npcId, int slot) {

		switch (itemId) {

		default:
			if (c.in_debug_mode())
				Utility.println("Player used Item id: " + itemId + " with Npc id: " + npcId + " With Slot : " + slot);
			break;
		}

	}

}
