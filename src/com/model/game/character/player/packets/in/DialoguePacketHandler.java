package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;

/**
 * Dialogue
 **/
public class DialoguePacketHandler implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		if (player.dialogue().isActive()) {
			if (player.dialogue().next()) {
				return;
			}
		}
		if (player.nextChat > 0) {
			player.getDialogueHandler().sendDialogues(player.nextChat, player.talkingNpc);
		} else {
			player.getDialogueHandler().sendDialogues(0, -1);
		}
	}

}
