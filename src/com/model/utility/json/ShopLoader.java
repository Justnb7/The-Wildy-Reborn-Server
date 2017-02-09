package com.model.utility.json;

import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.model.game.item.Item;
import com.model.game.shop.Currency;
import com.model.game.shop.Shop;

/**
 * The {@link JsonLoader} implementation that loads all shops.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class ShopLoader extends JsonLoader {

    /**
     * Creates a new {@link ShopLoader}.
     */
    public ShopLoader() {
        super("./Data/json/shops.json");
    }

    @Override
    public void load(JsonObject reader, Gson builder) {
        String name = Objects.requireNonNull(reader.get("name").getAsString());
        Item[] items = Objects.requireNonNull(builder.fromJson(reader.get("items").getAsJsonArray(), Item[].class));
        boolean restock = reader.get("restock").getAsBoolean();
        boolean sellItems = reader.get("can-sell-items").getAsBoolean();
        Currency currency = Objects.requireNonNull(Currency.valueOf(reader.get("currency").getAsString()));

      /*  for(Item item : items) {
        	System.out.println("id = " + item.getId() + ", amount = " + item.getAmount());
        }*/
        
        Shop shop = new Shop(name, items, restock, sellItems, currency);
        
        if (Shop.SHOPS.containsKey(name))
            Shop.SHOPS.clear();
        Shop.SHOPS.put(name, shop);
    }
}