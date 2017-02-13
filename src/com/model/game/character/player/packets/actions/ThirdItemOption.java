package com.model.game.character.player.packets.actions;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;

/**
 * Item Click 3 Or Alternative Item Option 1
 * 
 * @author Ryan / Lmctruck30
 * 
 *         Proper Streams
 */

public class ThirdItemOption implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		player.getInStream().readSignedWordBigEndianA();
		player.getInStream().readSignedWordA();
		int itemId = player.getInStream().readSignedWordA();
		if (player.in_debug_mode())
			player.write(new SendMessagePacket("Third item option clicked on a: (" + player.getItems().getItemName(itemId) + ")"));
		if (!player.getItems().playerHasItem(itemId))
			return;

		if (player.getBankPin().requiresUnlock()) {
			player.isBanking = false;
			player.getBankPin().open(2);
			return;
		}
		
		switch (itemId) {
			
		default:
			break;

		}

	}

}
