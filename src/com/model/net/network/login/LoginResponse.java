package com.model.net.network.login;


/**
 * Represents a simple LoginResponse object which consists of three values
 * received from the login protocol.
 *
 * @author relex lawl
 */
public final class LoginResponse {

	/**
	 * The LoginResponse constructor.
	 * 
	 * @param response
	 *            The login return opcode.
	 * @param rights
	 *            The player's privilege level.
	 * @param flagged
	 *            The flagged status, if {@code 1} the client will received
	 *            extra information related to mouse movements.
	 */
	public LoginResponse(int response, int rights, int flagged) {
		this.response = response;
		this.rights = rights;
		this.flagged = flagged;
	}
	
	/**
	 * The login opcode received.
	 */
	private final int response;

	/**
	 * The player's privilege level.
	 */
	private final int rights;

	/**
	 * Flag that checks if client will receive more information on mouse
	 * movement, etc. from player.
	 */
	private final int flagged;

	/**
	 * Gets the login return opcode.
	 * 
	 * @return The login response.
	 */
	public int getResponse() {
		return response;
	}

	/**
	 * Gets the player's privilege level.
	 * 
	 * @return The privilege level.
	 */
	public int getRights() {
		return rights;
	}

	/**
	 * Gets the flag status as an int.
	 * 
	 * @return The flag status.
	 */
	public int getFlagStatus() {
		return flagged;
	}

}
