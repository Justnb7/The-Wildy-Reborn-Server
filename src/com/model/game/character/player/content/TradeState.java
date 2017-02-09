package com.model.game.character.player.content;

/**
 * Defines the different states of the trade
 *
 */
public enum TradeState {

	/**
	 * We are not in a trade
	 */
	NONE,

	/**
	 * We are in the trade screen
	 */
	TRADE_SCREEN,

	/**
	 * We have accepted the trade screen
	 */
	ACCEPTED_TRADE_SCREEN,

	/**
	 * We are on the confirm screen
	 */
	CONFIRM_SCREEN,

	/**
	 * We have accepted the confirm screen
	 */
	ACCEPTED_CONFIRM_SCREEN

}
