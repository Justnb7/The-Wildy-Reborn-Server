package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class SendFrame107 implements PacketEncoder {
	
	 private final int OPCODE = 107;

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(OPCODE);
		}
	}

}
