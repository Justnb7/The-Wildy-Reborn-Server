package com.model.game.shop;

import com.model.game.character.player.Player;

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
        player.getItems().deleteItem(id, amount);
    }

    @Override
    public void recieveCurrency(Player player, int amount) {
        player.getItems().addItem(id, amount);
    }

    @Override
    public int currencyAmount(Player player) {
        return player.getItems().getItemAmount(id);
    }

    @Override
    public boolean canRecieveCurrency(Player player) {
        return player.getItems().playerHasItem(id);
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