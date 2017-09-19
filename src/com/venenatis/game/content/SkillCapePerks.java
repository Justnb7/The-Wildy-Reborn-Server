package com.venenatis.game.content;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;

public enum SkillCapePerks {
		ATTACK(new int[][] { { 9747, 9748 } }),
		STRENGTH(new int[][] { { 9750, 9751 } }),
		DEFENCE(new int[][] { { 9753, 9754} }),
		RANGING(new int[][] { { 9756, 9757 } }),
		PRAYER(new int[][] { { 9759, 9760 } }),
		MAGIC(new int[][] { { 9762, 9763 } }),
		RUNECRAFTING(new int[][] { { 9765, 9766 } }),
		HITPOINTS(new int[][] { { 9768, 9769 } }),
		AGILITY(new int[][] { { 9771, 9772 } }),
		HERBLORE(new int[][] { { 9774, 9775 } }),
		THIEVING(new int[][] { { 9777, 9778 } }),
		CRAFTING(new int[][] { { 9780, 9781 } }),
		FLETCHING(new int[][] { { 9783, 9784 } }),
		SLAYER(new int[][] { { 9786, 9787 } }),
		MINING(new int[][] { { 9792, 9793 } }),
		SMITHING(new int[][] { { 9795, 9796 } }),
		FISHING(new int[][] { { 9798, 9799 } }),
		COOKING(new int[][] { { 9801, 9802 } }),
		FIREMAKING(new int[][] { { 9804, 9805 } }),
		WOODCUTTING(new int[][] { { 9807, 9808 } }),
		FARMING(new int[][] { { 9810, 9811 } }),
		HUNTER(new int[][] { { 9948, 9949} }),
		MAX_CAPE(new int[][] { { 13280 } }),
		ARDOUGNE_MAX_CAPE(new int[][] { { 20760 } }),
		FIRE_MAX_CAPE(new int[][] { { 13329 } }),
		AVAS_MAX_CAPE(new int[][] { { 13337 } }),
		SARADOMIN_MAX_CAPE(new int[][] { { 13331 } }),
		ZAMORAK_MAX_CAPE(new int[][] { { 13333 } }),
		GUTHIX_MAX_CAPE(new int[][] { { 13335 } 
	});
	
	public static final int[] MAX_CAPE_IDS = { 13280, 13329, 13337, 13331, 13333, 13335, 20760 };
		
	public static final SkillCapePerks[] MAX_CAPES = { MAX_CAPE, ARDOUGNE_MAX_CAPE, FIRE_MAX_CAPE, AVAS_MAX_CAPE, SARADOMIN_MAX_CAPE, ZAMORAK_MAX_CAPE, GUTHIX_MAX_CAPE };
	
	private int[][] skillcapes;

	SkillCapePerks(int[][] skillcapes) {
		this.skillcapes = skillcapes;
	}
	
	public int[][] getSkillcape() {
		return skillcapes;
	}
	
	static int MAX_CAPE_ID = 13280, MAX_CAPE_HOOD = 13281;
	
	/**
	 * Allows us to check wether or not a player is wearing one of the capes
	 * @param player
	 * @return
	 */
	public boolean isWearing(Player player) {
		for (int[] set : skillcapes) {
			for (int setItem : set) {
				if (player.getInventory().contains(setItem)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isWearingMaxCape(Player player) {
		if (MAX_CAPE.isWearing(player) || 
			FIRE_MAX_CAPE.isWearing(player) || 
			AVAS_MAX_CAPE.isWearing(player) || 
			SARADOMIN_MAX_CAPE.isWearing(player) || 
			ZAMORAK_MAX_CAPE.isWearing(player) || 
			ARDOUGNE_MAX_CAPE.isWearing(player) || 
			GUTHIX_MAX_CAPE.isWearing(player)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Mixing items with max cape
	 */
	public static void mixCape(Player player, String cape) {

		if (!player.getInventory().contains(MAX_CAPE_ID) || !player.getInventory().contains(MAX_CAPE_HOOD)) {
			player.getActionSender().sendMessage("You must have a max cape and hood in order to do this.");
			return;
		}

		player.getInventory().remove(MAX_CAPE_ID, 1);
		player.getInventory().remove(MAX_CAPE_HOOD, 1);
		switch (cape) {
		
		case "FIRE":
			if (!player.getInventory().contains(6570)) {
				player.getActionSender().sendMessage("You must have a firecape in order to do this.");
				return;
			}
			player.getInventory().remove(6570, 1);
			player.getInventory().add(13329, 1);
			player.getInventory().add(13330, 1);
			SimpleDialogues.sendItemStatement(player, 13329, "", "You've combined the fire cape and max cape.");
			break;
			
		case "SARADOMIN":
			if (!player.getInventory().contains(2412)) {
				player.getActionSender().sendMessage("You must have a saradomin cape in order to do this.");
				return;
			}
			player.getInventory().remove(2412, 1);
			player.getInventory().add(13331, 1);
			player.getInventory().add(13332, 1);
			SimpleDialogues.sendItemStatement(player, 13331, "", "You've combined the saradomin cape and max cape.");
			break;
			
		case "ZAMORAK":
			if (!player.getInventory().contains(2414)) {
				player.getActionSender().sendMessage("You must have a zamorak cape in order to do this.");
				return;
			}
			player.getInventory().remove(2414, 1);
			player.getInventory().add(13333, 1);
			player.getInventory().add(13334, 1);
			SimpleDialogues.sendItemStatement(player, 13333, "", "You've combined the zamorak cape and max cape.");
			break;
			
		case "GUTHIX":
			if (!player.getInventory().contains(2413)) {
				player.getActionSender().sendMessage("You must have a guthix in order to do this.");
				return;
			}
			player.getInventory().remove(2413, 1);
			player.getInventory().add(13335, 1);
			player.getInventory().add(13336, 1);
			SimpleDialogues.sendItemStatement(player, 13335, "", "You've combined the guthix cape and max cape.");
			break;
			
		case "AVAS":
			if (!player.getInventory().contains(10499)) {
				player.getActionSender().sendMessage("You must have a accumulator in order to do this.");
				return;
			}
			player.getInventory().remove(10499, 1);
			player.getInventory().add(13337, 1);
			player.getInventory().add(13338, 1);
			SimpleDialogues.sendItemStatement(player, 13337, "", "You've combined the accumulator and max cape.");
			break;
			
		case "ARDOUGNE":
			if (!player.getInventory().contains(13124)) {
				player.getActionSender().sendMessage("You must have an ardougne cloak(4) in order to do this.");
				return;
			}
			player.getInventory().remove(13124, 1);
			player.getInventory().add(20760, 1);
			player.getInventory().add(20764, 1);
			SimpleDialogues.sendItemStatement(player, 20760, "", "You've combined the cloak and max cape.");
			break;
		}
	}

}