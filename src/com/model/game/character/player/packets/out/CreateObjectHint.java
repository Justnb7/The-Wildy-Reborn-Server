package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class CreateObjectHint implements PacketEncoder {
	
    private final int OPCODE = 254;
    
    private final int x, y, height, pos;
	
	public CreateObjectHint(int x, int y, int height, int pos) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.pos = pos;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(OPCODE);
			player.getOutStream().writeByte(pos);
			player.getOutStream().writeShort(x);
			player.getOutStream().writeShort(y);
			player.getOutStream().writeByte(height);
		}
	}
}
