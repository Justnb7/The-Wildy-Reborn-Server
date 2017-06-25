package com.venenatis.game.world.shop;

import com.venenatis.game.model.Item;

public class ShopItem extends Item {

	private int value;
	
	public ShopItem(int id, int amount, int value) {
		super(id, amount);
		this.value = value;
	}
	
	public ShopItem(int id, int value) {
		super(id);
		this.value = value;
	}
	
	@Override
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	@Override
	public Item copy() {
		return new ShopItem(getId(), getAmount(), getValue());
	}
	
	@Override
	public String toString() {
		return String.format("ShopItem[name=%s, id=%s, amount=%s, value=%s]", getName(), getId(), getAmount(), value);
	}
	
}