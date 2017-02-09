package com.model.game.character.npc.drops;

import com.model.game.item.GameItem;

public class Drop {

	private final GameItem item;
	private final int rarity;
	private final int random;

	public Drop(int itemId, int itemAmount, int rarity, int random) {
		this.item = new GameItem(itemId, itemAmount);
		this.rarity = rarity;
		this.random = random;
	}

	public GameItem getItem() {
		return item;
	}

	public int getRarity() {
		return rarity;
	}

	public int getRandom() {
		return random;
	}
	
}
