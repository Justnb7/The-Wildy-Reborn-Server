package com.venenatis.game.model.container.impl.inventory;

import com.venenatis.game.model.container.Container;
import com.venenatis.game.model.container.impl.InterfaceConstants;
import com.venenatis.game.model.entity.player.Player;

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
		player.getActionSender().sendItemOnInterface(InterfaceConstants.INVENTORY_INTERFACE, toArray());
		player.getEquipment().calculateWeight();
	}

	@Override
	public void refresh(int... slots) {
		for (final int slot : slots) {
			player.getActionSender().sendItemOnInterfaceSlot(InterfaceConstants.INVENTORY_INTERFACE, stack[slot], slot);
			player.getEquipment().calculateWeight();
		}
	}
}