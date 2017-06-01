package com.model.game.item.container.container.impl;

import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.InterfaceConstants;
import com.model.game.item.container.container.Container;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.utility.json.definitions.ItemDefinition;

public class InventoryContainer extends Container {

	/**
	 * The size of our container
	 */
	public static final int SIZE = 28;
	
	private final Player player;

	public InventoryContainer(Player player) {
		super(SIZE, ContainerType.DEFAULT);
		this.player = player;
	}

	@Override
	public void onFillContainer() {
		player.getActionSender().sendMessage("Your inventory is full!");
	}

	@Override
	public void onMaxStack() {
		player.getActionSender().sendMessage("You do not have enough inventory space to carry that.");
	}

	@Override
	public void refresh() {
		player.getActionSender().sendUpdateItems(InterfaceConstants.INVENTORY_INTERFACE, toArray());
	}

	@Override
	public void refresh(int... slots) {
		for (final int slot : slots) {
			player.getActionSender().sendItemOnInterfaceSlot(InterfaceConstants.INVENTORY_INTERFACE, stack[slot], slot);
		}
	}

	public void addOrCreateGroundItem(Item item) {
		if (getFreeSlots() > 0) {
			add(new Item(item.getId(), item.getAmount()));
		} else if ((item.getAmount() > 1) && (!ItemDefinition.forId(item.getId()).isStackable())) {
			for (int i = 0; i < item.getAmount(); i++)
				GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId(), item.getAmount()), player.getX(), player.getY(), player.getZ(), player));
			player.getActionSender().sendMessage("Invntory full item placed underneath you.");
		} else {
			GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId(), item.getAmount()), player.getX(), player.getY(), player.getZ(), player));
			player.getActionSender().sendMessage("Invntory full item placed underneath you.");
		}
	}

	public boolean isTradeable(int itemId) {
		boolean tradable = ItemDefinition.forId(itemId).isTradeable();
		if (tradable)
			return true;
		return false;
	}
}