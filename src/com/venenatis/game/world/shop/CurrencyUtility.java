package com.venenatis.game.world.shop;

import com.venenatis.game.model.entity.player.Player;

public interface CurrencyUtility {

    public int addCurrency(Player player, int amount);

    public String getCurrencyName();

    public int removeCurrency(Player player, int amount, int minimum);
}