package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class SendActionInterface implements PacketEncoder {

	private final int OPCODE = 34;
	
	private final int id, slot, column, amount;
	
	public SendActionInterface(int id, int slot, int column, int amount) {
		this.id = id;
		this.slot = slot;
		this.column = column;
		this.amount = amount;
	}
	
	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			player.outStream.putFrameVarShort(OPCODE); // init item to smith screen
			int offset = player.getOutStream().offset;
			player.outStream.writeShort(column); // Column Across Smith Screen
			player.outStream.writeByte(4); // Total Rows?
			player.outStream.putInt(slot); // Row Down The Smith Screen
			player.outStream.writeShort(id + 1); // item
			player.outStream.writeByte(amount); // how many there are?
			player.outStream.putFrameSizeShort(offset);
		}
	}

}
