package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.out.SendMessagePacket;

public class ItemOnObjectPacketHandler implements PacketType {
	

	@Override
	public void handle(final Player player, int packetType, int packetSize) {
		player.getInStream().readUnsignedWord();
		player.objectId = player.getInStream().readSignedWordBigEndian();
		player.objectY = player.getInStream().readSignedWordBigEndianA();
		player.getInStream().readUnsignedWord();
		player.objectX = player.getInStream().readSignedWordBigEndianA();
		player.objectDistance = 3;
		player.objectDistance = player.objectDistance < 1 ? 1 : player.objectDistance;
		player.itemUsedOn = player.getInStream().readUnsignedWord();
		player.turnPlayerTo(player.objectX, player.objectY);
		if (player.getBankPin().requiresUnlock()) {
			player.setBanking(false);
			player.getBankPin().open(2);
			return;
		}
		if (!player.getItems().playerHasItem(player.itemUsedOn, 1)) {
			return;
		}
		if (player.inDebugMode()) {
			player.write(new SendMessagePacket("You used a : ("+player.getItems().getItemName(player.itemUsedOn)+") on object: "+player.objectId+"."));
		}
		
		
		switch (player.itemUsedOn) {
			
		default:
			player.clickObjectType = 4;
			break;
		
		}
	}

}
