package com.model.net.packet;

import com.model.game.character.player.Player;

public interface PacketType {
	
	public void handle(Player player, int id, int size);
}
