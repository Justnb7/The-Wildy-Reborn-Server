package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class SendClearScreen implements PacketEncoder {
	
	private final int OPCODE = 219;
	
	public SendClearScreen() {
		
	}

	@Override
	public void encode(Player player) {
        player.openInterface = -1;
        player.isBanking = false;
        player.usingGlory = false;
        player.dialogue().interrupt();
        if (player.getOutStream() != null && player != null) {
            player.getOutStream().writeFrame(OPCODE);
        }
	}

}
