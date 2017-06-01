package com.model.utility.json.loader;

import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.model.game.item.container.impl.shop.Currency;
import com.model.game.item.container.impl.shop.Shop;
import com.model.game.item.container.impl.shop.ShopItem;
import com.model.utility.json.JsonLoader;

/**
 * Parses throug the shops files and creates in-game shop objects for the game
 * on startup.
 *
 * @author SeVen
 */
public class ShopParser extends JsonLoader {

	public ShopParser() {
		super("./data/json/shops.json");
	}

	@Override
	 public void load(JsonObject reader, Gson builder) {
		final int id = reader.get("id").getAsInt();
		
		final String name = Objects.requireNonNull(reader.get("name").getAsString());
		
		final Currency currency = builder.fromJson(reader.get("currency"), Currency.class);
		
		final boolean restock = reader.get("restock").getAsBoolean();
		
		final boolean canSellItems = reader.get("canSellItems").getAsBoolean();
		
		final int scroll = reader.get("scroll").getAsInt();
		
		final ShopItem[] items = builder.fromJson(reader.get("items"), ShopItem[].class);
		
		Shop.SHOPS.add(new Shop(id, currency, name, items, restock, canSellItems, scroll));
	}

}