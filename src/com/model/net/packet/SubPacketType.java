package com.model.net.packet;

import com.model.game.character.player.Player;

public interface SubPacketType {

	public void processSubPacket(Player c, int packetType, int packetSize);
}