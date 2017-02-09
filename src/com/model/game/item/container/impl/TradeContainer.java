package com.model.game.item.container.impl;

import com.model.game.character.player.Player;
import com.model.game.item.container.Container;
import com.model.game.item.container.ItemContainerPolicy;

/**
 * Handles the players trade container
 * 
 * @author Mobster
 *
 */
public class TradeContainer extends Container {

	/**
	 * The maximum amoutn of items that can be held in this container
	 */
	private static final int CAPACITY = 28;

	/**
	 * Constructs a new {@Link ItemContainer} for trading
	 * 
	 * @param policy
	 */
	public TradeContainer(Player player) {
		super(ItemContainerPolicy.NORMAL, CAPACITY);
		addListener(new TradeListener(player));
	}

}
