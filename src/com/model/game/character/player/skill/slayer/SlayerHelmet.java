package com.model.game.character.player.skill.slayer;

import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.item.Item;

/**
 * 
 * @author Josh' <http://www.rune-server.org/members/josh%27/>
 *
 */

public class SlayerHelmet {
	
	private static Item[] HELMET_PARTS = { new Item(4166), new Item(4168), new Item(4164), new Item(4551), new Item(8921) };
	
	public static boolean hasRequirements(Player player) {
		if (player.getSkills().getLevelForExperience(Skills.CRAFTING) < 55) {
			player.write(new SendMessagePacket("You need a Crafting level of 55 to make a slayer helmet."));
			return false;
		}
		Item items[] = { new Item(4166), new Item(4164), new Item(4168), new Item(8921), new Item(4551) };
		for (Item item : items) {
			if (!player.getItems().playerHasItems(item)) {
				player.write(new SendMessagePacket("You don't have the required items to make a slayer helmet."));
				return false;
			}
		}
		return true;
	}
	
	public static void create(Player player) {
		if (!hasRequirements(player))
			return;
		for (Item item : HELMET_PARTS) {
			 player.getItems().deleteItems(item);
		}
		player.getItems().addItem(11864, 1);
		player.write(new SendMessagePacket("You combine all of the parts to create a slayer helmet."));
		return;
	}

	
	public static boolean handleItemOnItem(Player player, Item usedOn, Item usedWith) {
		if ((usedOn.getId() == 4166 || usedOn.getId() == 4168 ||
			usedOn.getId() == 4164 || usedOn.getId() == 4551 && usedWith.getId() == 8921) ||
			(usedWith.getId() == 4166 || usedWith.getId() == 4168 ||
			usedWith.getId() == 4164 || usedWith.getId() == 4551 && usedOn.getId() == 8921)) {
			create(player);	
			return true;
		}
		return false;
	}
	
	public static void disassemble(Player player) {
	}
	

}
