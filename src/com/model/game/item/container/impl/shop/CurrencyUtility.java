package com.model.game.item.container.impl.shop;

import com.model.game.character.player.Player;

public interface CurrencyUtility {

    public int addCurrency(Player player, int amount);

    public String getCurrencyName();

    public int removeCurrency(Player player, int amount, int minimum);
}