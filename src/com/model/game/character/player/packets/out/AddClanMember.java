package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class AddClanMember implements PacketEncoder {
	
    private final int OPCODE = 216;
    
    private final String username;
	
	public AddClanMember(String username) {
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
