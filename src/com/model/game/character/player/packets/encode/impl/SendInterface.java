package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class SendInterface implements PacketEncoder {

	
	/**
	 * The opcode
	 */
	private static final int OPCODE = 97;

	/**
	 * The interface ID
	 */
	private final int interfaceId;

	/**
	 * Sends a packet to the client in order to open an interface
	 * 
	 * @param interfaceId
	 *            The interface
	 */
	public SendInterface(int interfaceId) {
		this.interfaceId = interfaceId;
	}
	
	@Override
	public void encode(Player player) {
		if (player != null) {
        	player.stopSkillTask();
            if (player.getOutStream() != null) {
                player.getOutStream().writeFrame(OPCODE);
                player.getOutStream().writeShort(interfaceId);
                player.openInterface = interfaceId;
            }
        }
	}

}
