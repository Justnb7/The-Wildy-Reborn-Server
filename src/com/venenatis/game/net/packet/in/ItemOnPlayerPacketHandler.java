package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;


public class ItemOnPlayerPacketHandler implements PacketType {
	@Override
	public void handle(Player player, int packetType, int packetSize) {
		if (player.getAttribute("busy") != null) {
			return;
		}
		
	}

}