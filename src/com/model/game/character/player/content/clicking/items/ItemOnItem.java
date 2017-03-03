package com.model.game.character.player.content.clicking.items;

import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.game.character.player.content.PotionCombinating;
import com.model.game.character.player.content.rewards.CrystalChest;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.player.skill.impl.Firemaking;
import com.model.game.item.Item;
import com.model.utility.json.definitions.ItemDefinition;

public class ItemOnItem {
	
	/**
	 * Handles the action of using an item with another item.
	 * 
	 * @param player
	 *            The player performing this action.
	 * 
	 * @param used
	 *            The {@link Item} that is being initially selected.
	 * 
	 * @param usedWith
	 *            The {@link Item} that is being selected on.
	 */
	public static void handleAction(Player player, Item used, Item usedWith) {
		if (player.inDebugMode()) {
			System.out.println(String.format("[ItemOnItem] - itemUsed: %d usedWith: %d ", used.getId(), usedWith.getId()));
		}
		
		if (player.rights == Rights.ADMINISTRATOR) {
			if(used.getId() == 5733 || usedWith.getId() == 5733) {
				int amount = player.getItems().checkAmount(usedWith.getId());
				player.getItems().remove(new Item(usedWith.getId(), amount));
				player.message("Whee... "+ItemDefinition.forId(usedWith.getId()).getName()+" All gone!");
			}
		}
		
		if (PotionCombinating.get().isPotion(used) && PotionCombinating.get().isPotion(usedWith)) {
			if (PotionCombinating.get().matches(used, usedWith)) {
				PotionCombinating.get().mix(player, used, usedWith);
			} else {
				player.write(new SendMessagePacket("You cannot combine two potions of different types."));
			}
			return;
		}
		
		if (Firemaking.playerLogs(used.getId(), usedWith.getId())) {
			Firemaking.grabData(player, used.getId(), usedWith.getId());
			return;
		}
		
		if (CrystalChest.createKey(player)) {
			return;
		}
		
		if (used.getId() == 227 || usedWith.getId() == 227) {
			int primary = used.getId() == 227 ? usedWith.getId() : used.getId();
			player.getHerblore().mix(primary);
			return;
		}

		if (used.getId() == 227 || usedWith.getId() == 227) {
			int primary = used.getId() == 227 ? usedWith.getId() : used.getId();
			player.getHerblore().mix(primary);
			return;
		}
		
		if (used.getId() == 11810 && usedWith.getId() == 11798 || used.getId() == 11798 && usedWith.getId() == 11810) {
			player.getItems().remove(new Item(11810, 1));
			player.getItems().remove(new Item(11798, 1));
			player.getItems().addItem(new Item(11802, 1));
			return;
		}
		
		if (used.getId() == 11812 && usedWith.getId() == 11798 || used.getId() == 11798 && usedWith.getId() == 11812) {
			player.getItems().remove(new Item(11812, 1));
			player.getItems().remove(new Item(11798, 1));
			player.getItems().addItem(new Item(11804, 1));
			return;
		}
		
		if (used.getId() == 11814 && usedWith.getId() == 11798 || used.getId() == 11798 && usedWith.getId() == 11814) {
			player.getItems().remove(new Item(11814, 1));
			player.getItems().remove(new Item(11798, 1));
			player.getItems().addItem(new Item(11806, 1));
			return;
		}
		
		if (used.getId() == 11816 && usedWith.getId() == 11798 || used.getId() == 11798 && usedWith.getId() == 11816) {
			player.getItems().remove(new Item(11816, 1));
			player.getItems().remove(new Item(11798, 1));
			player.getItems().addItem(new Item(11808, 1));
			return;
		}
		
		if (used.getId() == 11800 && usedWith.getId() == 11818 || used.getId() == 11818 && usedWith.getId() == 11800) {
			player.getItems().remove(new Item(11818, 1));
			player.getItems().remove(new Item(11800, 1));
			player.getItems().addItem(new Item(11798, 1));
			return;
		}
		
		if (used.getId() == 11820 && usedWith.getId() == 11796 || used.getId() == 11796 && usedWith.getId() == 11820) {
			player.getItems().remove(new Item(11796, 1));
			player.getItems().remove(new Item(11820, 1));
			player.getItems().addItem(new Item(11798, 1));
			return;
		}
		
		if (used.getId() == 11822 && usedWith.getId() == 11794 || used.getId() == 11794 && usedWith.getId() == 11822) {
			player.getItems().remove(new Item(11794, 1));
			player.getItems().remove(new Item(11822, 1));
			player.getItems().addItem(new Item(11798, 1));
			return;
		}
		
		if (used.getId() == 11818 && usedWith.getId() == 11820 || used.getId() == 11820 && usedWith.getId() == 11818) {
			player.getItems().remove(new Item(11818, 1));
			player.getItems().remove(new Item(11820, 1));
			player.getItems().addItem(new Item(11794, 1));
			return;
		}
		
		if (used.getId() == 11822 && usedWith.getId() == 11818 || used.getId() == 11818 && usedWith.getId() == 11822) {
			player.getItems().remove(new Item(11818, 1));
			player.getItems().remove(new Item(11822, 1));
			player.getItems().addItem(new Item(11796, 1));
			return;
		}
		
		if (used.getId() == 11820 && usedWith.getId() == 11822 || used.getId() == 11822 && usedWith.getId() == 11820) {
			player.getItems().remove(new Item(11820, 1));
			player.getItems().remove(new Item(11822, 1));
			player.getItems().addItem(new Item(11800, 1));
			return;
		}
		
	}

}
