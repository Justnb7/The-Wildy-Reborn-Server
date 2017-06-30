package com.venenatis.game.net.network.login;

import com.venenatis.game.net.network.rsa.ISAACRandomGen;
import com.venenatis.game.net.network.session.LoginSession;
import com.venenatis.game.net.network.session.Session;

/**
 * Represents a 'Login Credential' for a player who is attempting to login
 * 
 * @author Mobster
 *
 */
public class LoginCredential {

	/**
	 * The username of the player
	 */
	private final String username;

	/**
	 * The password of the player
	 */
	private final String password;
	
	/**
	 * The identity of the session
	 */
	private final String identity;
	
	/**
	 * The mac address of the session
	 */
	private final String macAddress;

	/**
	 * The packet encryptor of theplayers {@link Session}
	 */
	private final ISAACRandomGen encryptor;

	/**
	 * The packet decryptor of the players {@link Session}
	 */
	private final ISAACRandomGen decryptor;

	/**
	 * The players client hash
	 */
	private final long clientHash;

	/**
	 * The current version the player is connected too
	 */
	private final int version;

	/**
	 * Constructs a new {@link LoginSession} which is sent when a player
	 * attempts to login to the server
	 * 
	 * @param username
	 *            The username of the player
	 * @param password
	 *            The password of the player
	 * @param encryptor
	 *            Encrypts the outgoing packets for the player
	 * @param decryptor
	 *            Decrypts the incoming packets for the player
	 * @param clientHash
	 *            The clients hash the player is connected from
	 * @param version
	 *            The version of the client the player is connecting from
	 */
	public LoginCredential(String username, String password, String identity, String macAddress, ISAACRandomGen encryptor, ISAACRandomGen decryptor, long clientHash, int version) {
		this.username = username;
		this.password = password;
		this.identity = identity;
		this.macAddress = macAddress;
		this.encryptor = encryptor;
		this.decryptor = decryptor;
		this.clientHash = clientHash;
		this.version = version;
	}

	/**
	 * Gets the players username
	 * 
	 * @return The players username
	 */
	public String getName() {
		return username;
	}

	/**
	 * Gets the players password
	 * 
	 * @return The players password
	 */
	public String getPassword() {
		return password;
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * Gets the encyptor for outgoing packets
	 * 
	 * @return The encryptor for outgoing packets
	 */
	public ISAACRandomGen getEncryptor() {
		return encryptor;
	}

	/**
	 * Gets the decryptor for incoming packets
	 * 
	 * @return The decryptor for incoming packets
	 */
	public ISAACRandomGen getDecryptor() {
		return decryptor;
	}

	/**
	 * Gets the client hash for the players client
	 * 
	 * @return The client hash for the players client
	 */
	public long getClientHash() {
		return clientHash;
	}

	/**
	 * Gets the version of the players client
	 * 
	 * @return The version of the players client
	 */
	public int getVersion() {
		return version;
	}

}
