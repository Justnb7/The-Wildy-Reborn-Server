package com.model.net.packet;

import com.model.game.character.player.Player;


public interface PacketEncoder {

	/**
	 * Encodes the outgoing packet to send to the client
	 * 
	 * @param player
	 *            The {@link Player} sending the outgoing packet
	 */
	public void encode(Player player);

}
