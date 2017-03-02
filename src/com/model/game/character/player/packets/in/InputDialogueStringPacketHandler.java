package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.content.clan.ClanManager;
import com.model.game.character.player.packets.PacketType;
import com.model.utility.Utility;

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