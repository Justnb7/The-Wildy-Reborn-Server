package com.venenatis.game.net.packet;

import com.venenatis.game.model.entity.player.Player;

/**
 * The interface that allows any implementing {@Packet}s. The ability to be
 * intercepted as an incoming {@link Packet}.
 * 
 * @author SeVen
 */
public interface IncomingPacketListener {
	
	 /**
     * Handles the packet that has just been received.
     * 
     * @param player
     *            The player receiving this packet.
     * 
     * @param packet
     *            The packet that has been received.
     * @param size
     *            The packet size.    
     */
	public void handle(Player player, int packet, int size);
}
