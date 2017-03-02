package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;

/**
 * Slient Packet
 **/
public class DefaultPacketHandler implements PacketType {

	@Override
	public void handle(Player c, int packetType, int packetSize) {

	}
}
