package com.model.net.packet;

import com.model.game.character.player.Player;

public interface SubPacketType {

	public void processSubPacket(Player player, int packetType, int packetSize);
}