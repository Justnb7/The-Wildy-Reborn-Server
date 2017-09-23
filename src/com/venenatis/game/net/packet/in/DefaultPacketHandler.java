package com.venenatis.game.net.packet.in;

import java.util.logging.Logger;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.IncomingPacketListener;

/**
 * Reports information about unhandled packets.
 * @author Patrick van Elderen
 *
 */
public class DefaultPacketHandler implements IncomingPacketListener {
	
	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(DefaultPacketHandler.class.getName());

	@Override
	public void handle(Player player, int id, int size) {
		//logger.info("Packet : [opcode=" + id + " length=" + size + "]");
	}
}
