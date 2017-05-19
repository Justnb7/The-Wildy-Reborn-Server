package com.model.game.item.container.impl;

import java.util.Arrays;

import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.Container;
import com.model.game.item.container.ItemContainerPolicy;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * The container that manages the inventory for a player.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class Inventory extends Container {
	
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
     * Create a new {@link Inventory}.
     *
     * @param player
     *            the player who's inventory is being managed.
     */
    public Inventory(Player player) {
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
                player.getActionSender().sendMessage("You don't have enough inventory space to hold that item.");
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

    public boolean alreadyHasItem(int id) {
		return playerHasItem(id);
	}

	public boolean playerOwnsAnyItems(int... ids) {
		return Arrays.stream(ids).anyMatch(this::alreadyHasItem);
	}
	
	public void addOrCreateGroundItem(Item item) {
		if (remaining() > 0) {
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
