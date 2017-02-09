package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class SendFrame35 implements PacketEncoder {
	
    private final int OPCODE = 35;
    
    private final int i1, i2, i3, i4;
	
	public SendFrame35(int i1, int i2, int i3, int i4) {
		this.i1 = i1;
		this.i2 = i2;
		this.i3 = i3;
		this.i4 = i4;
	}

	@Override
	public void encode(Player player) {
		player.getOutStream().writeFrame(OPCODE);
		player.getOutStream().writeByte(i1);
		player.getOutStream().writeByte(i2);
		player.getOutStream().writeByte(i3);
		player.getOutStream().writeByte(i4);
		player.updateRequired = true;
		player.setAppearanceUpdateRequired(true);
	}

}
