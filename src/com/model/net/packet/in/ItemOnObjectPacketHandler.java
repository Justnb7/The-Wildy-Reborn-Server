package com.model.net.packet.in;

import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.net.packet.PacketType;

public class ItemOnObjectPacketHandler implements PacketType {
	

	@Override@SuppressWarnings("unused")
	public void handle(final Player player, int packetType, int packetSize) {
		
		int interfaceType = player.getInStream().readUnsignedWord();
		final int objectId = player.getInStream().readSignedWordBigEndian();
		final int objectY = player.getInStream().readSignedWordBigEndianA();
		final int slot = player.getInStream().readUnsignedWord();
		final int objectX = player.getInStream().readSignedWordBigEndianA();
		final int itemId = player.getInStream().readUnsignedWord();
		
		Item item = player.getInventory().get(slot);

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
