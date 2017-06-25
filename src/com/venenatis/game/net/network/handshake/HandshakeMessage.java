package com.venenatis.game.net.network.handshake;

/**
 * Represents a message received from the respective handshake.
 *
 * @author relex lawl
 */
public final class HandshakeMessage {

	/**
	 * The HandshakeMessage constructor.
	 * 
	 * @param id
	 *            The value received from the handshake.
	 */
	public HandshakeMessage(int id) {
		this.id = id;
	}

	/**
	 * The handshake's id to identify its use.
	 */
	private final int id;

	/**
	 * Gets the handshake's id.
	 * 
	 * @return The id received from handshake.
	 */
	public int getId() {
		return id;
	}
}
