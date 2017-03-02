package com.model.game.character.player.packets.actions;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.game.item.ground.GroundItemHandler;
import com.model.utility.Utility;

public class ItemOnGroundItem implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		player.getInStream().readSignedWord();
		int itemUsed = player.getInStream().readSignedWordA();
		int groundItem = player.getInStream().readUnsignedWord();
		int gItemY = player.getInStream().readSignedWordA();
		int itemUsedSlot = player.getInStream().readSignedWordBigEndianA();
		int gItemX = player.getInStream().readUnsignedWord();
		if (!player.getItems().playerHasItem(itemUsed, 1, itemUsedSlot) || GroundItemHandler.get(groundItem, gItemX, gItemY, player.getX()) == null) {
			return;
		}
		if (player.getBankPin().requiresUnlock()) {
			player.isBanking = false;
			player.getBankPin().open(2);
			return;
		}

		switch (itemUsed) {

		default:
			if (player.in_debug_mode())
				Utility.println("ItemUsed " + itemUsed + " on Ground Item " + groundItem);
			break;
		}
	}

}
