package com.model.game.item.container.impl;

import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.Container;
import com.model.game.item.container.ItemContainerPolicy;

/**
 * The container that manages the inventory for a player.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class InventoryContainer extends Container {
	
	/**
	 * The size of the inventory container.
	 */
	public static final int SIZE = 28;
	
	/**
	 * Inventory interface id.
	 */
	public static final int INTERFACE = 3214;

    /**
     * The player who's inventory is being managed.
     */
    private final Player player;

    /**
     * Create a new {@link InventoryContainer}.
     *
     * @param player
     *            the player who's inventory is being managed.
     */
    public InventoryContainer(Player player) {
        super(ItemContainerPolicy.NORMAL, 28);
        this.player = player;
    }

    /**
     * Refreshes the contents of this inventory container to the interface.
     */
    public void refresh() {
        refresh(player, 3214);
    }

    @Override
    public boolean add(Item item, int slot) {
        if (!Item.valid(item))
            return false;
        if (!contains(item.getId()) && !item.getDefinition().isStackable()) {
            if (freeSlot() == -1) {
                player.getActionSender().sendMessage("You don't have enough space in your inventory!");
                return false;
            }
        }
        boolean val = super.add(item, slot);
        refresh();
        return val;
    }

    @Override
    public boolean add(Item item) {
        return add(item, -1);
    }

    @Override
    public boolean remove(Item item, int slot) {
        boolean val = super.remove(item, slot);
        refresh();
        return val;
    }

    @Override
    public boolean remove(Item item) {
        return remove(item, -1);
    }

}
