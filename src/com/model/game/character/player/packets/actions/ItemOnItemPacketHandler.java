package com.model.game.character.player.packets.actions;

import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.game.character.player.content.PotionCombinating;
import com.model.game.character.player.content.rewards.CrystalChest;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.impl.Firemaking;
import com.model.game.item.GameItem;
import com.model.game.item.Item;
import com.model.utility.json.definitions.ItemDefinition;

public class ItemOnItemPacketHandler implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int usedWithSlot = player.getInStream().readUnsignedWord();
		int usedItemSlot = player.getInStream().readUnsignedWordA();
		
		if (player.getBankPin().requiresUnlock()) {
			player.isBanking = false;
			player.getBankPin().open(2);
			return;
		}
		
		if (usedWithSlot >= player.playerItems.length || usedWithSlot < 0 || usedItemSlot >= player.playerItems.length || usedItemSlot < 0) {
			return;
		}
		
		int usedItem = player.playerItems[usedWithSlot] - 1;
		int withItem = player.playerItems[usedItemSlot] - 1;
		
		GameItem gameItemUsed = new GameItem(usedItem, player.playerItemsN[usedItemSlot], usedItemSlot);
		GameItem gameItemUsedWith = new GameItem(withItem, player.playerItemsN[usedItemSlot], usedWithSlot);

		if (!player.getItems().playerHasItem(withItem, 1) || !player.getItems().playerHasItem(usedItem, 1)) {
			return;
		}
		
		player.getSkilling().stop();
		
		if (player.rights == Rights.ADMINISTRATOR) {
			if(usedItem == 5733 || withItem == 5733) {
				int amount = player.getItems().checkAmount(withItem);
				player.getItems().remove(new Item(withItem, amount));
				player.message("Whee... "+ItemDefinition.forId(withItem).getName()+" All gone!");
			}
		}
		
		if (CrystalChest.createKey(player)) {
			return;
		}
		
		if (PotionCombinating.get().isPotion(gameItemUsed) && PotionCombinating.get().isPotion(gameItemUsedWith)) {
			if (PotionCombinating.get().matches(gameItemUsed, gameItemUsedWith)) {
				PotionCombinating.get().mix(player, gameItemUsed, gameItemUsedWith);
			} else {
				player.write(new SendMessagePacket("You cannot combine two potions of different types."));
			}
			return;
		}
		
		if (Firemaking.playerLogs(usedItem, withItem)) {
			Firemaking.grabData(player, usedItem, withItem);
			return;
		}
		
		if (usedItem == 227 || withItem == 227) {
			int primary = usedItem == 227 ? withItem : usedItem;
			player.getHerblore().mix(primary);
			return;
		}

		if (usedItem == 227 || withItem == 227) {
			int primary = usedItem == 227 ? withItem : usedItem;
			player.getHerblore().mix(primary);
			return;
		}
		
		if (usedItem == 11810 && withItem == 11798 || usedItem == 11798 && withItem == 11810) {
			player.getItems().remove(new Item(11810, 1));
			player.getItems().remove(new Item(11798, 1));
			player.getItems().addItem(new Item(11802, 1));
			return;
		}
		
		if (usedItem == 11812 && withItem == 11798 || usedItem == 11798 && withItem == 11812) {
			player.getItems().remove(new Item(11812, 1));
			player.getItems().remove(new Item(11798, 1));
			player.getItems().addItem(new Item(11804, 1));
			return;
		}
		
		if (usedItem == 11814 && withItem == 11798 || usedItem == 11798 && withItem == 11814) {
			player.getItems().remove(new Item(11814, 1));
			player.getItems().remove(new Item(11798, 1));
			player.getItems().addItem(new Item(11806, 1));
			return;
		}
		
		if (usedItem == 11816 && withItem == 11798 || usedItem == 11798 && withItem == 11816) {
			player.getItems().remove(new Item(11816, 1));
			player.getItems().remove(new Item(11798, 1));
			player.getItems().addItem(new Item(11808, 1));
			return;
		}
		
		if (usedItem == 11800 && withItem == 11818 || usedItem == 11818 && withItem == 11800) {
			player.getItems().remove(new Item(11818, 1));
			player.getItems().remove(new Item(11800, 1));
			player.getItems().addItem(new Item(11798, 1));
			return;
		}
		
		if (usedItem == 11820 && withItem == 11796 || usedItem == 11796 && withItem == 11820) {
			player.getItems().remove(new Item(11796, 1));
			player.getItems().remove(new Item(11820, 1));
			player.getItems().addItem(new Item(11798, 1));
			return;
		}
		
		if (usedItem == 11822 && withItem == 11794 || usedItem == 11794 && withItem == 11822) {
			player.getItems().remove(new Item(11794, 1));
			player.getItems().remove(new Item(11822, 1));
			player.getItems().addItem(new Item(11798, 1));
			return;
		}
		
		if (usedItem == 11818 && withItem == 11820 || usedItem == 11820 && withItem == 11818) {
			player.getItems().remove(new Item(11818, 1));
			player.getItems().remove(new Item(11820, 1));
			player.getItems().addItem(new Item(11794, 1));
			return;
		}
		
		if (usedItem == 11822 && withItem == 11818 || usedItem == 11818 && withItem == 11822) {
			player.getItems().remove(new Item(11818, 1));
			player.getItems().remove(new Item(11822, 1));
			player.getItems().addItem(new Item(11796, 1));
			return;
		}
		
		if (usedItem == 11820 && withItem == 11822 || usedItem == 11822 && withItem == 11820) {
			player.getItems().remove(new Item(11820, 1));
			player.getItems().remove(new Item(11822, 1));
			player.getItems().addItem(new Item(11800, 1));
			return;
		}
	}

}