package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class CreatePlayerHint implements PacketEncoder {
	
    private final int OPCODE = 254;
    
    private final int type, id;
	
	public CreatePlayerHint(int type, int id) {
		this.type = type;
		this.id = id;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(OPCODE);
			player.getOutStream().writeByte(type);
			player.getOutStream().writeShort(id);
			player.getOutStream().write3Byte(0);
		}
	}
}