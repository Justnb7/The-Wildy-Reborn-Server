package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;
import com.model.net.network.rsa.GameBuffer;

public class SendChangeSprite implements PacketEncoder {
	
	private final int OPCODE = 7;
	
	private final int componentId;
	
	private final byte index;
	
	public SendChangeSprite(int componentId, byte index) {
		this.componentId = componentId;
		this.index = index;
	}

	@Override
	/**
	 * Changes the main displaying sprite on an interface. The index represents
	 * the location of the new sprite in the index of the sprite array.
	 * 
	 * @param componentId	the interface
	 * @param index			the index in the array
	 */
	public void encode(Player player) {
		if (player == null || player.getOutStream() == null) {
			return;
		}
		GameBuffer stream = player.getOutStream();
		stream.writeFrame(OPCODE);
		stream.putInt(componentId);
		stream.writeByte(index);
		player.flushOutStream();
	}

}
