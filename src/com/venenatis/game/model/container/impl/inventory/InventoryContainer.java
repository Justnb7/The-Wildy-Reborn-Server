package com.venenatis.game.model.container.impl.inventory;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.container.Container;
import com.venenatis.game.model.container.impl.InterfaceConstants;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.ground_item.GroundItemHandler;

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

	public void addOrCreateGroundItem(Item item) {
		if (getFreeSlots() > 0) {
			add(new Item(item.getId(), item.getAmount()));
		} else if ((item.getAmount() > 1) && (!item.isStackable())) {
			for (int i = 0; i < item.getAmount(); i++)
				GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId(), item.getAmount()), player.getX(), player.getY(), player.getZ(), player));
			player.getActionSender().sendMessage("Inventory full item placed underneath you.");
		} else {
			GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId(), item.getAmount()), player.getX(), player.getY(), player.getZ(), player));
			player.getActionSender().sendMessage("Inventory full item placed underneath you.");
		}
	}
}