package com.model.game.item.equipment;

import com.model.game.character.combat.combat_data.CombatAnimation;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.utility.json.definitions.ItemDefinition;
import com.model.utility.json.definitions.WeaponAnimation;


public class Equipment {
	
	/**
	 * The helmet slot.
	 */
	public static final int SLOT_HELM = 0;

	/**
	 * The cape slot.
	 */
	public static final int SLOT_CAPE = 1;

	/**
	 * The amulet slot.
	 */
	public static final int SLOT_AMULET = 2;

	/**
	 * The weapon slot.
	 */
	public static final int SLOT_WEAPON = 3;

	/**
	 * The chest slot.
	 */
	public static final int SLOT_CHEST = 4;

	/**
	 * The shield slot.
	 */
	public static final int SLOT_SHIELD = 5;

	/**
	 * The bottoms slot.
	 */
	public static final int SLOT_BOTTOMS = 7;

	/**
	 * The gloves slot.
	 */
	public static final int SLOT_GLOVES = 9;

	/**
	 * The boots slot.
	 */
	public static final int SLOT_BOOTS = 10;

	/**
	 * The rings slot.
	 */
	public static final int SLOT_RING = 12;

	/**
	 * The arrows slot.
	 */
	public static final int SLOT_ARROWS = 13;
	
	public int getRingId() {
		return SLOT_RING;
	}
	
	public int getHelmetId() {
		return SLOT_HELM;
	}
	
	public int getGlovesId() {
		return SLOT_GLOVES;
	}
	
	public int getCapeId() {
		return SLOT_CAPE;
	}
	
	public int getChestId() {
		return SLOT_CHEST;
	}
	
	public int getLegsId() {
		return SLOT_BOTTOMS;
	}
	
	public int getShieldId() {
		return SLOT_SHIELD;
	}
	
	public int getQuiverId() {
		return SLOT_ARROWS;
	}
	
	public int getBootsId() {
		return SLOT_BOOTS;
	}
	
	public int getAmuletId() {
		return SLOT_AMULET;
	}
	
	public int getWeaponId() {
		return SLOT_WEAPON;
	}
	
	/**
	 * Update the players equipment
	 * @param player
	 */
	public void updateEquipment(Player player) {
		player.getWeaponInterface().sendWeapon(player.playerEquipment[player.getEquipment().getWeaponId()], player.getItems().getItemName(player.playerEquipment[player.getEquipment().getWeaponId()]));
		player.getItems().setEquipment(player.playerEquipment[player.getEquipment().getWeaponId()], player.playerEquipmentN[player.getEquipment().getWeaponId()], player.getEquipment().getWeaponId());
		WeaponAnimation.execute(player, new Item(player.playerEquipment[player.getEquipment().getWeaponId()]));
		player.getItems().resetBonus();
		player.getItems().getBonus();
		player.getItems().writeBonus();
		for (int equip = 0; equip < player.playerEquipment.length; equip++) {
			player.getItems().setEquipment(player.playerEquipment[equip], player.playerEquipmentN[equip], equip);
		}
		player.getWeaponInterface().sendSpecialBar(player.playerEquipment[player.getEquipment().getWeaponId()]);
	}
	
	public final int[] VENEMOUS_WEPS = {12926, 12904, 12899, 12904};
	public final int[] VENEMOUS_HELMS = {13197, 13199, 12931};
	
	public boolean canInfect(Player player){
		for(int i : VENEMOUS_WEPS){
			if(player.playerEquipment[getWeaponId()] == i){
				return true;
			}
		}
		for(int i : VENEMOUS_HELMS){
			if(player.playerEquipment[getHelmetId()] == i){
				return true;
			}
		}
		return false;
	}
	
	public static final byte[] IS_METAL = new byte[25000];


	private static final String[] METAL_KEYWORDS = { "platebody", "chestplate", "platelegs", "chainbody", "chainskirt" };

	public static void setMetalEquipment() {
		for (int i = 0; i < 25000; i++) {
			ItemDefinition def = ItemDefinition.forId(i);
			if (def != null && def.getName() != null) {
				for (String s : METAL_KEYWORDS) {
					if (def.getName().toLowerCase().contains(s.toLowerCase())) {
						IS_METAL[i] = 1;
					}
				}
			}
		}
	}


	public static boolean isWearingMetal(Player player) {
		int[] equip = player.playerEquipment;

		if (equip[4] > 0) {
			if (equip[4] > 0 && IS_METAL[equip[4]] == 1) {
				return true;
			}
		}
		if (equip[7] > 0) {
			if (equip[7] > 0 && IS_METAL[equip[7]] == 1) {
				return true;
			}
		}

		if (equip[5] > 0) {
			if (equip[5] > 0 && IS_METAL[equip[5]] == 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the player is wearing full void.
	 * 
	 * @param player
	 * @return
	 */
	public static boolean wearingFullVoid(Player player, int type) {
		int complete = 0;

		if (type < 0) {
			return false;
		}

		boolean helmet = player.playerEquipment[0] == (type == 0 ? 11665 : type == 1 ? 11663 : 11664);

		boolean hasGloves = player.playerEquipment[9] == 8842;

		boolean hasDeflector = player.playerEquipment[5] == 19712;

		boolean hasLegs = player.playerEquipment[7] == 8840 || player.playerEquipment[7] == 13073;

		boolean hasPlate = player.playerEquipment[4] == 8839 || player.playerEquipment[4] == 13072;

		if (helmet) {
			complete++;
		}

		if (hasGloves) {
			complete++;
		}

		if ((hasDeflector && ((hasPlate && !hasLegs) || (!hasPlate && hasLegs))) || hasPlate && hasLegs) {
			complete++;
		}

		return complete >= 3;
	}
	
	public boolean usingCrystalBow(Player player) {
		return player.playerEquipment[3] == 4222;
	}
	
	public boolean wearingBlowpipe(Player player) {
		return player.playerEquipment[3] == 12926;
	}
	
	public boolean wearingBallista(Player player) {
		return player.playerEquipment[3] == 19481;
	}
	
	public boolean isCrossbow(Player player) {
		switch(player.playerEquipment[getWeaponId()]) {
		case 11785:
		case 9185:
		case 18357:
		case 21012:
			return true;
		}
		return false;
	}
	
	public boolean isThrowingWeapon(Player player) {
		switch (player.playerEquipment[getWeaponId()]) {
		// Javalins
		case 825:
		case 826:
		case 827:
		case 828:
		case 829:
		case 830:
			// Chinchompas
		case 10033:
		case 10034:
			// Thrownaxe
		case 800:
		case 801:
		case 802:
		case 803:
		case 804:
		case 805:
		case 6522:
			// Darts
		case 806:
		case 807:
		case 808:
		case 809:
		case 810:
		case 811:
			// Knives
		case 863:
		case 864:
		case 865:
		case 866:
		case 867:
		case 868:
		case 869:
			return true;
		}
		return false;
	}

	public boolean isBow(Player player) {
		switch (player.playerEquipment[getWeaponId()]) {
		case 839:
		case 841:
		case 843:
		case 845:
		case 847:
		case 849:
		case 851:
		case 853:
		case 855:
		case 857:
		case 859:
		case 861:
		case 4222:
		case 9705:
		case 12424:
		case 11235:
		case 6724:
		case 4734:
		case 20997:
			return true;
		}
		return false;
	}
	
	public boolean isArrow(Player player) {
		switch (player.playerEquipment[getQuiverId()]) {
		case 882:
		case 884:
		case 886:
		case 888:
		case 890:
		case 892:
		case 11212:
		case 78:
		case 4740:
			return true;
		}
		return false;
	}
	
	public boolean isBolt(Player player) {
		switch (player.playerEquipment[getQuiverId()]) {
		case 9140: 
		case 9141:
		case 4142: 
		case 9143: 
		case 9144: 
		case 9240: 
		case 9241: 
		case 9242: 
		case 9243:
		case 9244: 
		case 9245: 
		case 9706:
		return true;
		}
		
		return false;
	}
	
	public boolean usingRange(Player player) {
		if(wearingBlowpipe(player) || usingCrystalBow(player) || isCrossbow(player) || isThrowingWeapon(player) || isBow(player) || wearingBallista(player))
			return true;
		return false;
	}

}