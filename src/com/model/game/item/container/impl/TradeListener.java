package com.model.game.item.container.impl;

import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.Container;
import com.model.game.item.container.ItemContainerListener;

/**
 * A listener for the item container
 *
 */
public class TradeListener implements ItemContainerListener {

	/**
	 * The player who this listener is listening to changes for
	 */
	private final Player player;

	public TradeListener(Player player) {
		this.player = player;
	}

	@Override
	public void onAdd(Container container, Item item, boolean successful) {

	}

	@Override
	public void onRemove(Container container, Item item, boolean successful) {

	}

	public Player getPlayer() {
		return player;
	}

}
