package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class SendEnterStringInterface implements PacketEncoder {
	
    private final int OPCODE = 187;
	
	public SendEnterStringInterface() {
		
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
            player.getOutStream().writeFrame(OPCODE);
        }
	}

}
