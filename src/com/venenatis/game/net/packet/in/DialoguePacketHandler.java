package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;

public class DialoguePacketHandler implements PacketType {

	@Override
	public void handle(Player player, int id, int size) {
		if (player.getDialogueManager().isActive()) {
			if (player.getDialogueManager().next()) {
				return;
			}
		}
	}

}