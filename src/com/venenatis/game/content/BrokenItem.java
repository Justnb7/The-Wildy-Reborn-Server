package com.venenatis.game.content;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.DialogueManager;

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

	//Original item value * this multiplier is the repair cost of all items.
	//Currently 3%
	private static final double REPAIR_COST_MULTIPLIER = 0.03;
	
	/**
	 * Gets the total cost of repairing a player's stuff.
	 * @param player
	 * @param deleteEmblems
	 * @return
	 */
	public static int getRepairCost(Player player) {
		int cost = 0;
		for(BrokenItem b : BrokenItem.values()) {
			final int amt = player.getInventory().getAmount(b.getBrokenItem());
			if(amt > 0) {
				cost += ((int)(ItemDefinition.get(b.getOriginalItem()).getValue() * REPAIR_COST_MULTIPLIER) * amt);	
			}
		}
		return cost;
	}

	/**
	 * Repairs all broken stuff for a player.
	 * @param player
	 */
	public static void repair(Player player) {
		boolean fixed = false;
		
		for(BrokenItem b : BrokenItem.values()) {
			final int amt = player.getInventory().getAmount(b.getBrokenItem());
			if(amt > 0) {
				final int cost = ((int)(ItemDefinition.get(b.getOriginalItem()).getValue() * REPAIR_COST_MULTIPLIER) * amt);
				if(player.getInventory().getAmount(995) >= cost) {
					player.getInventory().remove(995, cost);
					player.getInventory().remove(b.getBrokenItem(), amt);
					player.getInventory().add(b.getOriginalItem(), amt);
					fixed = true;
				} else {
					player.getActionSender().sendMessage("You could not afford fixing all your items.");
					break;
				}
			}
		}
		
		if(fixed) {
			DialogueManager.start(player, 21);
		} else {
			player.getActionSender().removeAllInterfaces();
		}
	}

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