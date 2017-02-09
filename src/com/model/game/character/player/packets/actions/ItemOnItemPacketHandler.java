package com.model.game.character.player.packets.actions;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.game.item.UseItem;

public class ItemOnItemPacketHandler implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int usedWithSlot = player.getInStream().readUnsignedWord();
		int itemUsedSlot = player.getInStream().readUnsignedWordA();
		
		if(usedWithSlot >= player.playerItems.length || usedWithSlot < 0 || itemUsedSlot >= player.playerItems.length || itemUsedSlot < 0) {
			return;
		}
		int useWith = player.playerItems[usedWithSlot] - 1;
		int itemUsed = player.playerItems[itemUsedSlot] - 1;
		player.getSkilling().stop();
		if (!player.getItems().playerHasItem(useWith, 1) || !player.getItems().playerHasItem(itemUsed, 1)) {
			return;
		}
		
		if (player.getBankPin().requiresUnlock()) {
			player.isBanking = false;
			player.getBankPin().open(2);
			return;
		}
		UseItem.ItemonItem(player, itemUsed, useWith, itemUsedSlot, usedWithSlot);
	}

}