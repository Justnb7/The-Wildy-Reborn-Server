package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.IncomingPacketListener;
import com.venenatis.game.util.Utility;

public class InputDialogueStringPacketHandler implements IncomingPacketListener {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		long value = player.getInStream().readQWord();

		if (value < 0) {
			// prevent invalid packets
			value = 0;
		}

		String stringValue = Utility.longToPlayerName2(value);
		
		if (player.getInputString() != null) {
			player.getInputString().input(stringValue);
			player.setInputString(null);
		}
	}
}