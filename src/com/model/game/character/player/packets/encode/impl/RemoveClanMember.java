package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class RemoveClanMember implements PacketEncoder {
	
    private final int OPCODE = 213;
    
    private final String username;
	
	public RemoveClanMember(String username) {
		this.username = username;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			player.getOutStream().putFrameVarByte(OPCODE);
			int offset = player.getOutStream().offset;
			player.getOutStream().putRS2String(username);
			player.getOutStream().putFrameSizeByte(offset);
		}
	}

}
