package com.venenatis.game.content.skills.crafting;

import com.venenatis.game.model.Item;

public interface Craftable {
	
	public String getName();
	
	public int getAnimation();
	
	public Item getUse();
	
	public Item getWith();
	
	public CraftableItem[] getCraftableItems();
	
	public Item[] getIngediants(int index);
	
	public String getProductionMessage();
}