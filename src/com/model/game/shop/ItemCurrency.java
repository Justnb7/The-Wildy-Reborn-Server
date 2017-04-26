package com.model.game.shop;

import com.model.game.character.player.Player;
import com.model.game.item.Item;

/**
 * The currency that provides basic functionality for all tangible currencies.
 * It is recommended that this be used rather than {@link GeneralCurrency}.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class ItemCurrency implements GeneralCurrency {

    /**
     * The item identification for this currency.
     */
    private final int id;

    /**
     * Creates a new {@link ItemCurrency}.
     *
     * @param id
     *         the item identification for this currency.
     */
    public ItemCurrency(int id) {
        this.id = id;
    }

    @Override
    public void takeCurrency(Player player, int amount) {
        player.getInventory().remove(new Item(id, amount));
    }

    @Override
    public void recieveCurrency(Player player, int amount) {
        player.getInventory().add(new Item(id, amount));
    }

    @Override
    public int currencyAmount(Player player) {
        return player.getInventory().amount(id);
    }

    @Override
    public boolean canRecieveCurrency(Player player) {
        return player.getInventory().playerHasItem(id);
    }

    /**
     * Gets the item identification for this currency.
     *
     * @return the item identification.
     */
    public int getId() {
        return id;
    }
}