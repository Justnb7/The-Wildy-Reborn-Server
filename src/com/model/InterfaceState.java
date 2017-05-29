package com.model;

import com.model.game.character.player.Player;
import com.model.game.item.container.impl.Bank;
import com.model.game.item.container.impl.Trade;

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
	 * The active enter amount interface.
	 */
	private int enterAmountInterfaceId = -1;
	
	/**
	 * The active enter amount id.
	 */
	private int enterAmountId;
	
	/**
	 * The active enter amount slot.
	 */
	private int enterAmountSlot;
	
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
		enterAmountInterfaceId = -1;
		player.getActionQueue().clearRemovableActions();
		player.removeInterfaceAttributes();
	}

	/**
	 * Called to open the enter amount interface.
	 * @param interfaceId The interface id.
	 * @param slot The slot.
	 * @param id The id.
	 */
	public void openEnterAmountInterface(int interfaceId, int slot, int id) {
		enterAmountInterfaceId = interfaceId;
		enterAmountSlot = slot;
		enterAmountId = id;
		player.getActionSender().sendEnterAmountInterface(id, null);
	}
	
	/**
	 * Checks if the enter amount interface is open.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isEnterAmountInterfaceOpen() {
		return enterAmountInterfaceId != -1;
	}

	/**
	 * Called when the enter amount interface is closed.
	 * @param amount The amount that was entered.
	 */
	public void closeEnterAmountInterface(int amount) {
		try {
			switch(enterAmountInterfaceId) {
			case Bank.PLAYER_INVENTORY_INTERFACE:
				player.getBank().depositFromInventory(enterAmountId, amount);
				break;
			case Bank.BANK_INVENTORY_INTERFACE:
				player.getBank().withdraw(enterAmountId, amount, true);
				break;
			case Trade.PLAYER_INVENTORY_INTERFACE:
				Trade.offerItem(player, enterAmountId, enterAmountSlot, amount);
				break;
			case Trade.TRADE_INVENTORY_INTERFACE:
				Trade.takeItem(player, enterAmountId, enterAmountSlot, amount);
				break;
			}
		} finally {
			enterAmountInterfaceId = -1;
		}
	}
}