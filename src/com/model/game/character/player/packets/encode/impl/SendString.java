package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class SendString implements PacketEncoder {
	
	/**
	 * The opcode for the string to send
	 */
	private static final int OPCODE = 126;

	/**
	 * The message
	 */
	private final String message;
	
	/**
	 * The interface frame
	 */
	private final int interfaceId;

	/**
	 * 
	 * @param message
	 *            The message sent to the interface
	 * @param interfaceId
	 *            The interface frame
	 */
	public SendString(String message, int interfaceId) {
		this.message = message;
		this.interfaceId = interfaceId;
	}

	@Override
	public void encode(Player player) {
		if (!player.checkPacket126Update(message, interfaceId) && interfaceId != 56306 && interfaceId != 39507) {
			return;
		}
		if (player.getOutStream() != null) {
			player.getOutStream().putFrameVarShort(OPCODE);
			int offset = player.getOutStream().offset;
			player.getOutStream().putRS2String(message == null ? "" : message);
			player.getOutStream().writeWordA(interfaceId);
			player.getOutStream().putFrameSizeShort(offset);
		}
		player.flushOutStream();
	}
	

}
