package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendMessagePacket implements PacketEncoder {
	
	/**
	 * The opcode for the message to send
	 */
	private static final int OPCODE = 253;

	/**
	 * The message
	 */
	private final String message;

	/**
	 * Sends a message to the client
	 * 
	 * @param message
	 *            The message to the client
	 */
	public SendMessagePacket(String message) {
		this.message = message;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			player.outStream.putFrameVarByte(OPCODE);
			int offset = player.getOutStream().offset;
			player.outStream.putRS2String(message);
			player.outStream.writeByte(0);
			player.outStream.putFrameSizeByte(offset);
		}
	}

}
