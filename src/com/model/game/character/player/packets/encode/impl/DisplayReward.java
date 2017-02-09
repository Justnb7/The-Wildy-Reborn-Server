package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class DisplayReward implements PacketEncoder {
	
    private final int OPCODE = 53;
    
    private final int first_item, first_amount, second_item, second_amount, third_item, third_amount;
	
	public DisplayReward(int first_item, int first_amount, int second_item, int second_amount, int third_item, int third_amount) {
		this.first_item = first_item;
		this.first_amount = first_amount;
		this.second_item = second_item;
		this.second_amount = second_amount;
		this.third_item = third_item;
		this.third_amount = third_amount;
	}

	@Override
	public void encode(Player player) {
		int[] items = { first_item, second_item, third_item };
		int[] amounts = { first_amount, second_amount, third_amount };
		player.outStream.putFrameVarShort(OPCODE);
		int offset = player.getOutStream().offset;
		player.outStream.writeShort(6963);
		player.outStream.writeShort(items.length);
		for (int i = 0; i < items.length; i++) {
			if (player.playerItemsN[i] > 254) {
				player.outStream.writeByte(255);
				player.outStream.writeDWord_v2(amounts[i]);
			} else {
				player.outStream.writeByte(amounts[i]);
			}
			if (items[i] > 0) {
				player.outStream.writeWordBigEndianA(items[i] + 1);
			} else {
				player.outStream.writeWordBigEndianA(0);
			}
		}
		player.outStream.putFrameSizeShort(offset);
		player.flushOutStream();
		player.getItems().addItem(first_item, first_amount);
		player.getItems().addItem(second_item, second_amount);
		player.getItems().addItem(third_item, third_amount);
		player.write(new SendInterface(3322));
	}
}
