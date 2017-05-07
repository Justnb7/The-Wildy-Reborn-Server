package com.model.net.packet.out;

import com.model.game.character.player.Player;
import com.model.net.packet.PacketEncoder;

public class SendInterfacePacket implements PacketEncoder {

	
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
	public SendInterfacePacket(int interfaceId) {
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
