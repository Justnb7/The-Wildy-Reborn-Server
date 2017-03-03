package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.PacketType;

public class SwitchItemPacketHandler implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		int interfaceId = player.getInStream().readUnsignedWordBigEndianA();
		boolean insertMode = player.getInStream().readSignedByteC() == 1;
		int from = player.getInStream().readUnsignedWordBigEndianA();
		int to = player.getInStream().readUnsignedWordBigEndian();
		if (Trading.isTrading(player)) {
        	Trading.decline(player);
        }
		if (player.isBusy()) {
			return;
		}
		player.getItems().moveItems(from, to, interfaceId, insertMode);
	}
}