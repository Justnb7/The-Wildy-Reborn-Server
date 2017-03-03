package com.model.game.character.player.packets.in;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;

/**
 * @author JaydenD12/Jaydennn
 */

public class ItemOnPlayerPacketHandler implements PacketType {
	@Override
	public void handle(Player player, int packetType, int packetSize) {
		int playerId = player.inStream.readUnsignedWord();
		int itemId = player.playerItems[player.inStream.readSignedWordBigEndian()] - 1;
		if (playerId > World.getWorld().getPlayers().capacity()) {
			return;
		}
		player.walkingToObject = false;

		if (player.getBankPin().requiresUnlock()) {
			player.setBanking(false);
			player.getBankPin().open(2);
			return;
		}
		switch (itemId) {
		
		
		default:
			break;
		}
	}

}