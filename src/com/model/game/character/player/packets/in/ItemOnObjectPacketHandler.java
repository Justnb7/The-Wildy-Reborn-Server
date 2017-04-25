package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.game.item.Item;

public class ItemOnObjectPacketHandler implements PacketType {
	

	@Override@SuppressWarnings("unused")
	public void handle(final Player player, int packetType, int packetSize) {
		
		int interfaceType = player.getInStream().readUnsignedWord();
		final int objectId = player.getInStream().readSignedWordBigEndian();
		final int objectY = player.getInStream().readSignedWordBigEndianA();
		final int slot = player.getInStream().readUnsignedWord();
		final int objectX = player.getInStream().readSignedWordBigEndianA();
		final int itemId = player.getInStream().readUnsignedWord();
		
		Item item = player.getItems().getItemFromSlot(slot);

		int distanceRequired = 1;
		
		if (!player.getInventory().playerHasItem(item.getId(), 1)) {
			return;
		}
		
		switch (item.getId()) {
		
		default:
			break;
		
		}
		
	}

}
