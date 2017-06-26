package com.venenatis.game.net.packet;

import com.venenatis.game.model.entity.player.Player;

public interface SubPacketType {

	public void processSubPacket(Player player, int packetType, int packetSize);
}