package com.model.game.character.player.packets.in;

import java.util.logging.Logger;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;

/**
 * Reports information about unhandled packets.
 * @author Patrick van Elderen
 *
 */
public class DefaultPacketHandler implements PacketType {
	
	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(DefaultPacketHandler.class.getName());

	@Override
	public void handle(Player player, int id, int size) {
		logger.info("Packet : [opcode=" + id + " length=" + size + "]");
	}
}
