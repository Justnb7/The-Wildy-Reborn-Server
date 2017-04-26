package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;

public class WithdrawAllButOneAction implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		int interfaceId = player.getInStream().readSignedWordBigEndianA();
		@SuppressWarnings("unused")
		int itemId = player.getInStream().readSignedWordBigEndianA();
		player.getInStream().readSignedWordBigEndian();
		switch (interfaceId) {
		case 5382:
			//bank interface
			break;
		}
	}

}
