package com.model.game.item.container.impl.shop;

import java.util.ArrayList;
import java.util.List;

import com.model.game.definitions.ItemDefinition;
import com.model.game.item.container.Container;

public class Shop extends Container {
	
	public static final List<Shop> SHOPS = new ArrayList<Shop>(ShopConstants.MAXIMUM_SHOPS);
	
	private final int id;
	
	private final Currency currency;
	
	private final String name;
	
	private final boolean restock;
	
	private final boolean canSell;
	
	private final int scroll;
	
	private final int[] defaultStockAmounts;

	public Shop(int id, Currency currency, String name, ShopItem[] stock, boolean restock, boolean canSell, int scroll) {
		super(ShopConstants.CAPACITY, true, ContainerType.ALWAYS_STACK);
		this.id = id;
		this.name = name;
		this.scroll = scroll;
		this.restock = restock;
		this.canSell = canSell;
		this.currency = currency;
		defaultStockAmounts = new int[stock.length];

		for (int i = 0; i < stock.length; i++) {
			ShopItem item = (ShopItem) stock[i].copy();
			if (item.getValue() <= 0) {
				final ItemDefinition def = ItemDefinition.get(item.getId());

				if (def != null) {
					item.setValue(def.getValue());
				}
			}
			stack[i] = item;
			defaultStockAmounts[i] = stock[i].getAmount();
		}
	}
	
	@Override
	public void refresh() {
	}
	
	@Override
	public void refresh(int... slots) {
	}

	public boolean canSell() {
		return canSell;
	}

	public Currency getCurrency() {
		return currency;
	}

	public int[] getDefaultStockAmounts() {
		return defaultStockAmounts;
	}

	public String getName() {
		return name;
	}

	public int getShopId() {
		return id;
	}

	public boolean restock() {
		return restock;
	}

	public int getScroll() {
		return scroll;
	}

}