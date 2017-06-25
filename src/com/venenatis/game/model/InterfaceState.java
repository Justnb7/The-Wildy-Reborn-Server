package com.venenatis.game.model;

import com.venenatis.game.model.entity.player.Player;

/**
 * Contains information about the state of interfaces open in the client.
 * @author Graham Edgecombe
 *
 */
public class InterfaceState {
	
	/**
	 * The current open interface.
	 */
	private int currentInterface = -1;
	
	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * Creates the interface state.
	 */
	public InterfaceState(Player player) {
		this.player = player;
	}
	
	/**
	 * Checks if the specified interface is open.
	 * @param id The interface id.
	 * @return <code>true</code> if the interface is open, <code>false</code> if not.
	 */
	public boolean isInterfaceOpen(int id) {
		return currentInterface == id;
	}
	
	/**
	 * Gets the current open interface.
	 * @return The current open interface.
	 */
	public int getCurrentInterface() {
		return currentInterface;
	}
	
	/**
	 * Called when an interface is opened.
	 * @param id The interface.
	 */
	public void interfaceOpened(int id) {
		if(currentInterface != -1) {
			interfaceClosed();
		}
		currentInterface = id;
	}
	
	/**
	 * Called when an interface is closed.
	 */
	public void interfaceClosed() {
		currentInterface = -1;
		player.getActionQueue().clearRemovableActions();
		player.removeInterfaceAttributes();
	}
}