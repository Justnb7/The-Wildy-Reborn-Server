package com.model.game.item;

import com.model.game.character.player.Player;
import com.model.game.character.player.content.PotionCombinating;
import com.model.game.character.player.content.rewards.CrystalChest;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.impl.Cooking;
import com.model.game.character.player.skill.impl.Cooking.Cookables;
import com.model.game.character.player.skill.impl.Firemaking;
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
