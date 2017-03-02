package com.model.game.character.player.packets.actions;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;

public class SecondGroundOption implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		player.getInStream().readSignedWordBigEndian();
		player.getInStream().readUnsignedWord();
		player.getInStream().readSignedWordBigEndian();
		if (player.getBankPin().requiresUnlock()) {
			player.isBanking = false;
			player.getBankPin().open(2);
			return;
		}
	}
}
