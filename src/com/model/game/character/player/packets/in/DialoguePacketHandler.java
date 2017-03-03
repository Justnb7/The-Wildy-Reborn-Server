package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;

public class DialoguePacketHandler implements PacketType {

	@Override
	public void handle(Player player, int id, int size) {
		if (player.dialogue().isActive()) {
			if (player.dialogue().next()) {
				return;
			}
		}
	}

}
