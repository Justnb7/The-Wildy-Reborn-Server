package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class SendUpdateItemsAlt implements PacketEncoder {
	
    private final int OPCODE = 34;
    
    private final int interfaceId, id, amount, slot;
	
	public SendUpdateItemsAlt(int interfaceId, int id, int amount, int slot) {
		this.interfaceId = interfaceId;
		this.id = id;
		this.amount = amount;
		this.slot = slot;
	}

	@Override
	public void encode(Player player) {
		player.outStream.putFrameVarShort(OPCODE);
        int offset = player.getOutStream().offset;
        player.outStream.writeShort(interfaceId);
        player.outStream.writeByte(slot);
        if (id == 0) {
        	player.outStream.writeShort(0);
        	player.outStream.writeByte(0);
		} else {
			player.outStream.writeShort(id + 1);
			if (amount > 254) {
				player.outStream.writeByte(255);
				player.outStream.putInt(amount);
			} else {
				player.outStream.writeByte(amount);
			}
		}
        player.outStream.putFrameSizeShort(offset);
	}
}
