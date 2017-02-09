package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class SendInterfaceConfig implements PacketEncoder {
	
	/**
	 * The opcode for the string to send
	 */
	private static final int OPCODE = 171;
	
	private final int interfaceId, state;

	public SendInterfaceConfig(int interfaceId, int state) {
		this.interfaceId = interfaceId;
		this.state = state;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
            player.getOutStream().writeFrame(OPCODE);
            player.getOutStream().writeByte(interfaceId);
            player.getOutStream().writeShort(state);
        }
	}

}
