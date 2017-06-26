package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;

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
