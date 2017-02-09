package com.model.game.item.equipment;

import com.model.game.character.player.Player;
import com.model.utility.json.definitions.ItemDefinition;

public final class EquipmentConstants {

	public static final byte[] IS_METAL = new byte[25000];


	private static final String[] METAL_KEYWORDS = { "platebody", "chestplate", "platelegs", "chainbody", "chainskirt" };

	public static void setMetalEquipment() {
		for (int i = 0; i < 25000; i++) {
			ItemDefinition def = ItemDefinition.forId(i);
			if (def != null && def.getName() != null) {
				for (String s : METAL_KEYWORDS) {
					if (def.getName().toLowerCase().contains(s.toLowerCase())) {
						EquipmentConstants.IS_METAL[i] = 1;
					}
				}
			}
		}
	}

	public static void sendSoundForEquipSlot(Player player, int slot, int item) {
		int sound = 326;

		if (slot == 10) {
			sound = 1343;
		}
		if (IS_METAL[item] == 1) {
			switch (slot) {
			case 0:
				sound = 1342;
				break;
			case 4:
				sound = 1346;
				break;
			case 7:
				sound = 1346;
				break;
			case 3:
				sound = 1344;
			case 1:
			case 2:
			case 5:
			case 6:
			}
		}
		if (sound > 0) {
			//player.write(new SendSoundPacket(sound, 10, 0));
		}
	}

	public static final int FEET = 10;
	public static final int HANDS = 9;
	public static final int HEAD = 0;
	public static final int LEGS = 7;
	public static final int AMULET = 2;
	public static final int QUIVER = 13;
	public static final int RING = 12;
	public static final int SHIELD = 5;
	public static final int WEAPON = 3;
	public static final int BACK = 1;
	public static final int CHEST = 4;

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

}