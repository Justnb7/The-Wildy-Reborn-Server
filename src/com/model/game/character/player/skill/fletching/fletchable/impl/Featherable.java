package com.model.game.character.player.skill.fletching.fletchable.impl;

import com.model.game.character.player.skill.fletching.Fletching;
import com.model.game.character.player.skill.fletching.fletchable.Fletchable;
import com.model.game.character.player.skill.fletching.fletchable.FletchableItem;
import com.model.game.item.Item;

public enum Featherable implements Fletchable {
	
	HEADLESS_ARROWS(new Item(314, 15), new Item(52, 15), new FletchableItem(new Item(53, 15), 1, 1.0)),
	BRONZE_BOLT(new Item(314, 10), new Item(9375, 10), new FletchableItem(new Item(877, 10), 9, 0.5)),
	IRON_BOLT(new Item(314, 10), new Item(9377, 10), new FletchableItem(new Item(9140, 10), 39, 1.5)),
	STEEL_BOLT(new Item(314, 10), new Item(9378, 10), new FletchableItem(new Item(9141, 10), 46, 3.5)),
	MITHRIL_BOLT(new Item(314, 10), new Item(9379, 10), new FletchableItem(new Item(9142, 10), 54, 5.0)),
	ADAMANT_BOLT(new Item(314, 10), new Item(9380, 10), new FletchableItem(new Item(9143, 10), 61, 7.0)),
	RUNITE_BOLT(new Item(314, 10), new Item(9381, 10), new FletchableItem(new Item(9144, 10), 69, 10.0));

	private final Item use;
	private final Item with;
	private final FletchableItem[] items;

	private Featherable(Item use, Item with, FletchableItem... items) {
		this.use = use;
		this.with = with;
		this.items = items;
	}

	public static void declare() {
		for (Featherable featherable : values()) {
			Fletching.SINGLETON.addFletchable(featherable);
		}
	}

	@Override
	public int getAnimation() {
		return 65535;
	}

	@Override
	public Item getUse() {
		return use;
	}

	@Override
	public Item getWith() {
		return with;
	}

	@Override
	public FletchableItem[] getFletchableItems() {
		return items;
	}

	@Override
	public String getProductionMessage() {
		return null;
	}

	@Override
	public Item[] getIngediants() {
		return new Item[] { use, with };
	}
}