package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;
import com.model.game.item.Item;

public class SendItemOnInterfaceSlot implements PacketEncoder {

	private final int OPCODE = 34;
	
	private final int interfaceId;

	private final Item item;

	private final int slot;

	public SendItemOnInterfaceSlot(int interfaceId, Item item, int slot) {
		this.interfaceId = interfaceId;
		this.item = item;
		this.slot = slot;
	}

	@Override
	public void encode(Player player) {
		player.outStream.putFrameVarShort(OPCODE);
        int offset = player.getOutStream().offset;
        player.outStream.writeShort(interfaceId);
        player.outStream.writeByte(slot);
        if (item != null) {
        	player.outStream.writeShort(0);
        	player.outStream.writeByte(0);
		} else {
			player.outStream.writeShort(item.getId() + 1);
			if (item.getAmount() > 254) {
				player.outStream.writeByte(255);
				player.outStream.putInt(item.getAmount());
			} else {
				player.outStream.writeByte(item.getAmount());
			}
		}
		player.outStream.putFrameSizeShort(offset);
	}

}
