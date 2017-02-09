package com.model.game.character.player.packets;

import com.model.game.character.player.Player;

public interface PacketType {
	public void processPacket(Player c, int packetType, int packetSize);
}
