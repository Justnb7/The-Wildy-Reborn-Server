package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class SendScrollbar implements PacketEncoder {

	private final int OPCODE = 204;

private final int scrollbar, size;

	public SendScrollbar(int scrollbar, int size) {
		this.scrollbar = scrollbar;
		this.size = size;
	}
	
	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
            player.getOutStream().writeFrame(OPCODE);
            player.getOutStream().putInt(scrollbar);
            player.getOutStream().putInt(size);
        }
	}

}
