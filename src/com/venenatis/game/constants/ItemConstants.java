package com.venenatis.game.constants;

import com.venenatis.game.content.EmotesManager;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.account.Account;
import com.venenatis.game.model.equipment.EquipmentRequirement;

public class ItemConstants {
	
	/**
	 * Spawnable items
	 */
	public static final String[] SPAWNABLES = { "Helm of neitiznot", "Prayer potion(4)", "Prayer potion(3)", "Prayer potion(2)",
			"Prayer potion(1)", "Super restore(4)", "Super restore(3)", "Super restore(2)", "Super restore(1)",
			"Magic potion(4)", "Magic potion(3)", "Magic potion(2)", "Magic potion(1)", "Super attack(4)",
			"Super attack(3)", "Super attack(2)", "Super attack(1)", "Super strength(4)", "Super strength(3)",
			"Super strength(2)", "Super strength(1)", "Super defence(4)", "Super defence(3)", "Super defence(2)",
			"Super defence(1)", "Ranging potion(4)", "Ranging potion(3)", "Ranging potion(2)", "Ranging potion(1)",
			"Saradomin brew(4)", "Saradomin brew(3)", "Saradomin brew(2)", "Saradomin brew(1)", "Black d'hide vamb",
			"Black d'hide chaps", "Blue d'hide body", "Shark", "Anglerfish", "Manta ray", "Fire rune", "Water rune",
			"Air rune", "Earth rune", "Mind rune", "Body rune", "Death rune", "Nature rune", "Chaos rune", "Law rune",
			"Cosmic rune", "Blood rune", "Soul rune", "Astral rune", "Rune full helm", "Rune platebody", "Rune platelegs",
			"Rune kiteshield", "Rune boots", "Climbing boots", "Dragon dagger", "Dragon dagger(p++)", "Dragon mace",
			"Dragon scimitar", "Dragon longsword", "Amulet of glory", "Amulet of glory(1)", "Amulet of glory(2)",
			"Amulet of glory(3)", "Amulet of glory(4)", "Amulet of strength", "Mystic hat", "Mystic robe top",
			"Mystic robe bottom", "Mystic gloves", "Mystic boots", "Rune boots", "Rune arrow", "Iron scimitar",
			"Ring of recoil", "Magic shortbow", "Rune crossbow", "Diamong bolts (e)", "Ava's accumulator",
			"Initiate sallet", "Initiate hauberk", "Initiate cuisse", "Granite shield", "Rune plateskirt" };
	
	public static int getDegradeItemWhenWear(int id) {
		// pvp armors
		if (id == 13958 || id == 13961 || id == 13964 || id == 13967 || id == 13970 || id == 13973 || id == 13858 || id == 13861 || id == 13864 || id == 13867 || id == 13870 || id == 13873 || id == 13876 || id == 13884 || id == 13887 || id == 13890 || id == 13893 || id == 13896 || id == 13899 || id == 13902 || id == 13905 || id == 13908 || id == 13911 || id == 13914 || id == 13917 || id == 13920 || id == 13923 || id == 13926 || id == 13929 || id == 13932 || id == 13935 || id == 13938 || id == 13941 || id == 13944 || id == 13947 || id == 13950 || id == 13958)
			return id + 2; // if you wear it it becomes corrupted
		return -1;
	}

	// return amt of charges
	public static int getItemDefaultCharges(int id) {
		// pvp armors
		if (id == 13910 || id == 13913 || id == 13916 || id == 13919 || id == 13922 || id == 13925 || id == 13928 || id == 13931 || id == 13934 || id == 13937 || id == 13940 || id == 13943 || id == 13946 || id == 13949 || id == 13952)
			return 1500;
		if (id == 13960 || id == 13963 || id == 13966 || id == 13969 || id == 13972 || id == 13975)
			return 3000;
		if (id == 13860 || id == 13863 || id == 13866 || id == 13869 || id == 13872 || id == 13875 || id == 13878 || id == 13886 || id == 13889 || id == 13892 || id == 13895 || id == 13898 || id == 13901 || id == 13904 || id == 13907 || id == 13960)
			return 6000; // 1hour
		// nex armors
		if (id == 20137 || id == 20141 || id == 20145 || id == 20149 || id == 20153 || id == 20157 || id == 20161 || id == 20165 || id == 20169 || id == 20173)
			return 60000;
		return -1;
	}

	// return what id it degrades to, -1 for disapear which is default so we
	// dont add -1
	public static int getItemDegrade(int id) {
		if (id == 11285) // DFS
			return 11283;
		// nex armors
		if (id == 20137 || id == 20141 || id == 20145 || id == 20149 || id == 20153 || id == 20157 || id == 20161 || id == 20165 || id == 20169 || id == 20173)
			return id + 1;
		return -1;
	}

	public static int getDegradeItemWhenCombating(int id) {
		// nex armors
		if (id == 20135 || id == 20139 || id == 20143 || id == 20147 || id == 20151 || id == 20155 || id == 20159 || id == 20163 || id == 20167 || id == 20171)
			return id + 2;
		return -1;
	}

	public static boolean itemDegradesWhileHit(int id) {
		if (id == 2550)
			return true;
		return false;
	}

	public static boolean itemDegradesWhileWearing(Item id) {
		String name = id.getName().toLowerCase();
		if (name.contains("c. dragon") || name.contains("corrupt dragon") || name.contains("vesta's") || name.contains("statius'") || name.contains("morrigan's") || name.contains("zuriel's"))
			return true;
		return false;
	}

	public static boolean itemDegradesWhileCombating(Item id) {
		String name = id.getName().toLowerCase();
		// nex armors
		if (name.contains("torva") || name.contains("pernix") || name.contains("virtux") || name.contains("zaryte"))
			return true;
		return false;
	}

	public static boolean canWear(Item item, Player player) {
		if(player.getRights().isOwner(player) && player.inDebugMode())
			return true;
		
		if (!EquipmentRequirement.canEquip(player, item.getId()))
            return false;
		
		/**
		 * Ironman armour
		 */
		if (item.getId() == 12810 || item.getId() == 12811 || item.getId() == 12812) {
			if (!player.getAccount().getType().equals(Account.IRON_MAN_TYPE)) {
				player.getActionSender().sendMessage("You need to be an regular Ironman to wear this armour.");
				return false;
			}
		}
		
		/**
		 * Ultimate ironman armour
		 */
		if (item.getId() == 12813 || item.getId() == 12814 || item.getId() == 12815) {
			if (!player.getAccount().getType().equals(Account.IRON_MAN_TYPE)) {
				player.getActionSender().sendMessage("You need to be an Ultimate Ironman to wear this armour.");
				return false;
			}
		}
		
		/**
		 * Hardcore ironman armour
		 */
		if (item.getId() == 20792 || item.getId() == 20793 || item.getId() == 20794) {
			if (!player.getAccount().getType().equals(Account.IRON_MAN_TYPE)) {
				player.getActionSender().sendMessage("You need to be an Hardcore Ironman to wear this armour.");
				return false;
			}
		}
		
        /**
         * Skill Capes
         */
        if (EmotesManager.doesntHaveLevelReq(player, item.getId())) {
			return false;
		}
        
		return true;
	}
	
	/**
	 * Check if we have a special item such as Korasi's sword
	 * @param item
	 *        The special item.
	 * @return
	 */
	public static boolean specialItem(int item) {
		switch(item) {
		case 19780:
			return true;
		}
		return false;
	}
	
	/**
	 * Items that are special and turn into dust on death.
	 * @param item
	 *        The item that turns to dust.
	 * @return
	 */
	public static int turnsToDust(int item) {
		switch(item) {
		case 19780:
			return 50;
		}
		return 0;
	}

}
