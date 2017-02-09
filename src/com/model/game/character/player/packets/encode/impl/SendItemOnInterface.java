package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class SendItemOnInterface implements PacketEncoder {
	
    private final int OPCODE = 34;
    
    private final int frame, item, slot, amount;
	
	public SendItemOnInterface(int frame, int item, int slot, int amount) {
		this.frame = frame;
		this.item = item;
		this.slot = slot;
		this.amount = amount;
	}

	@Override
	public void encode(Player player) {
		player.outStream.putFrameVarShort(OPCODE);
        int offset = player.getOutStream().offset;
        player.outStream.writeShort(frame);
        player.outStream.writeByte(slot);
        player.outStream.writeShort(item + 1);
        player.outStream.writeByte(255);
        player.outStream.putInt(amount);
        player.outStream.putFrameSizeShort(offset);
	}
}
