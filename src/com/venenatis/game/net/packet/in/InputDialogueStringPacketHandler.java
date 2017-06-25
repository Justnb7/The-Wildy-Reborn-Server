package com.venenatis.game.net.packet.in;

import com.venenatis.game.content.clan.ClanManager;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.util.Utility;

public class InputDialogueStringPacketHandler implements PacketType {

	@Override
	public void handle(Player c, int packetType, int packetSize) {
		long value = c.getInStream().readQWord();

		if (value < 0) {
			// prevent invalid packets
			value = 0;
		}

		String stringValue = Utility.longToPlayerName2(value);
		
		if (c.dialogue().input(stringValue)) {
			return;
		}
		
		if(c.getStringReceiver() > 0) {
			if(c.getStringReceiver() == 1) {
				ClanManager.editSettings(c, "CHANGE_NAME", stringValue);
			}
			c.setStringReceiver(-1);
			return;
		}

		ClanManager.joinClan(c, stringValue);
	}
}