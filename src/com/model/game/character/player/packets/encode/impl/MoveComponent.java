package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class MoveComponent implements PacketEncoder {
	
	private final int OPCODE = 70;

	private final int x, y, componentId;

	public MoveComponent(int x, int y, int id) {
		this.x = x;
		this.y = y;
		this.componentId = id;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(OPCODE);
			player.getOutStream().writeShort(x);
			player.getOutStream().writeWordBigEndian(y);
			player.getOutStream().writeWordBigEndian(componentId);
		}
	}

}
