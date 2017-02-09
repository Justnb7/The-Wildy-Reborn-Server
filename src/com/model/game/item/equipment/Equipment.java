package com.model.game.item.equipment;

import com.model.game.character.player.Player;


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
	
	/**
	 * Checks if the player is wearing full dharok armour.
	 * 
	 * @param p
	 * @return
	 */
	public static boolean fullDharok(Player player) {
		return player.playerEquipment[player.getEquipment().getWeaponId()] == 4718 && player.playerEquipment[player.getEquipment().getHelmetId()] == 4716 && player.playerEquipment[player.getEquipment().getChestId()] == 4720 && player.playerEquipment[player.getEquipment().getLegsId()] == 4722;
	}
	
	public boolean wearingBlowpipe(Player player) {
		return player.playerEquipment[player.getEquipment().getWeaponId()] == 12926;
	}

	/**
	 * Obisidian Weapons.
	 */
	public static final int[] obsidianWeapons = { 746, 747, 6523, 6525, 6526, 6527, 6528 };

	/**
	 * Checks if the player has obsidian equipment to apply the effect.
	 * 
	 * @param player
	 * @return
	 */
	public static boolean hasObsidianEffect(Player player) {
		if (player.playerEquipment[3] <= 0 || player.playerEquipment[2] <= 0 || player.playerEquipment[2] != 11128) {
			return false;
		}
		int weapon_id = player.playerEquipment[3];

		for (int weapon : obsidianWeapons) {
			if (weapon_id == weapon) {
				return true;
			}
		}
		return false;
	}

	public static boolean wearingAnguish(Player player) {
		return player.playerEquipment[player.getEquipment().getWeaponId()] == 19547;
	}

}