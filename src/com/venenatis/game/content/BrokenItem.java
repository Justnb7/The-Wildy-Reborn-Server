package com.venenatis.game.content;

import java.util.HashMap;
import java.util.Map;

public enum BrokenItem {
	
	FIRE_CAPE_BROKEN(6570, 6570/*20445*/),
	INFERNAL_CAPE(21295, 21295/*21287*/),
	/*DRAGON_DEFENDER_BROKEN(12954, 20463),
	FIGHTER_TORSO_BROKEN(10551, 20513),
	VOID_KNIGHT_TOP(8839, 20465),
	VOID_KNIGHT_ROBE(8840, 20469),
	VOID_KNIGHT_GLOVES(8842, 20475),
	VOID_KNIGHT_MAGE_HELM(11663, 20477),
	VOID_KNIGHT_RANGER_HELM(11664, 20479),
	VOID_KNIGHT_MELEE_HELM(11665, 20481),*/
	;

	BrokenItem(int originalItem, int brokenItem) {
		this.originalItem = originalItem;
		this.brokenItem = brokenItem;
	}

	public int getOriginalItem() {
		return originalItem;
	}

	public int getBrokenItem() {
		return brokenItem;
	}

	private final int originalItem;
	private final int brokenItem;

	private static Map<Integer, BrokenItem> brokenItems = new HashMap<Integer, BrokenItem>();

	public static BrokenItem get(int originalId) {
		return brokenItems.get(originalId);
	}

	static {
		for(BrokenItem brokenItem : BrokenItem.values()) {
			brokenItems.put(brokenItem.getOriginalItem(), brokenItem);
		}
	}
}