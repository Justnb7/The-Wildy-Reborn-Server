package com.venenatis.game.net.packet;

import com.venenatis.game.model.entity.player.Player;

public interface PacketType {
	
	public void handle(Player player, int id, int size);
}
