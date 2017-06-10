package com.model.utility.parser.impl;

import java.util.Objects;

import com.google.gson.JsonObject;
import com.model.game.item.container.impl.shop.Currency;
import com.model.game.item.container.impl.shop.Shop;
import com.model.game.item.container.impl.shop.ShopItem;
import com.model.utility.parser.GsonParser;

/**
 * Parses throug the shops files and creates in-game shop objects for the game
 * on startup.
 *
 * @author SeVen
 */
public class ShopParser extends GsonParser {

	public ShopParser() {
		super("def/shop/shops");
	}

	@Override
	protected void parse(JsonObject data) {
		final int id = data.get("id").getAsInt();
		
		final String name = Objects.requireNonNull(data.get("name").getAsString());
		
		final Currency currency = builder.fromJson(data.get("currency"), Currency.class);
		
		final boolean restock = data.get("restock").getAsBoolean();
		
		final boolean canSellItems = data.get("canSellItems").getAsBoolean();
		
		final int scroll = data.get("scroll").getAsInt();
		
		final ShopItem[] items = builder.fromJson(data.get("items"), ShopItem[].class);
		
		Shop.SHOPS.add(new Shop(id, currency, name, items, restock, canSellItems, scroll));
	}

}