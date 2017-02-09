package com.model;

/**
 * Represents the servers current state
 * 
 * @author Mobster
 *
 */
public enum ServerState {

	/**
	 * The server has been started but not yet loaded
	 */
	STARTED,

	/**
	 * The server is loading but not yet loaded
	 */
	LOADING,

	/**
	 * The server has been successfully loaded
	 */
	LOADED

}
