package com.venenatis.game.constants;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.player.Player;

/**
 * Class consisting of only equipment constants.
 * 
 * @author Daniel
 */
public final class EquipmentConstants {

	public static final int HELM_SLOT = 0;
	public static final int CAPE_SLOT = 1;
	public static final int NECKLACE_SLOT = 2;
	public static final int WEAPON_SLOT = 3;
	public static final int TORSO_SLOT = 4;
	public static final int SHIELD_SLOT = 5;
	public static final int LEGS_SLOT = 7;
	public static final int GLOVES_SLOT = 9;
	public static final int BOOTS_SLOT = 10;
	public static final int RING_SLOT = 12;
	public static final int AMMO_SLOT = 13;

	public static final int STAB = 0;
	public static final int SLASH = 1;
	public static final int CRUSH = 2;
	public static final int MAGIC = 3;
	public static final int RANGED = 4;
	public static final int STAB_DEFENSE = 5;
	public static final int SLASH_DEFENSE = 6;
	public static final int CRUSH_DEFENSE = 7;
	public static final int MAGIC_DEFENSE = 8;
	public static final int RANGED_DEFENSE = 9;
	public static final int STRENGTH = 10;
	public static final int RANGED_STRENGTH = 11;
	public static final int MAGIC_STRENGTH = 12;
	public static final int PRAYER = 13;

	public static final int getTextIdForInterface(int interfaceId) {
		switch (interfaceId) {
			case 4705:
				return 4708;
			case 2423:
				return 2426;
			case 7762:
				return 7765;
			case 12290:
				return 12293;
			case 1698:
				return 1701;
			case 2276:
				return 2279;
			case 1764:
				return 1767;
			case 328:
				return 355;
			case 4446:
				return 4449;
			case 4679:
				return 4682;
			case 425:
				return 428;
			case 3796:
				return 3799;
			case 8460:
				return 8463;
			case 5570:
				return 5573;
		}
		return 5857;
	}

	private static final boolean[] HAS_BODY = new boolean[Constants.ITEM_LIMIT];

	private static final boolean[] HAS_HEAD = new boolean[Constants.ITEM_LIMIT];

	private static final boolean[] HAS_JAW = new boolean[Constants.ITEM_LIMIT];

	public static void declare() {
		for (int index = 0; index < Constants.ITEM_LIMIT; index++) {
			HAS_BODY[index] = true;
			HAS_HEAD[index] = true;
			HAS_JAW[index] = true;
		}

		final String[] remove_head = {
				"helm", "coif", "sallet", "hood", "hat"
		};

		final String[] remove_body = {
				"blouse", "chestplate", "top", "shirt", "brassard",
				"torso", "hauberk", "platebody", "robe", "splitbark body", "jacket"
		};

		final String[] has_jaw = {
				"mask", "med helm", "coif", "hood", "neitiznot", "armadyl helmet",
				"berserker helm", "archer helm", "farseer helm", "warrior helm",
				"void", "bandana", "bearhead", "dharok's helm", "hat"
		};

		for (final ItemDefinition def : ItemDefinition.DEFINITIONS) {
			if (def != null) {
				final String itemName = def.getName().trim().toLowerCase();
				for (final String name : remove_head) {
					if (itemName.contains(name) && !itemName.equals("robin hood hat")) {
						HAS_HEAD[def.getId()] = false;
						HAS_JAW[def.getId()] = false;
					}
				}

				for (final String name : remove_body) {
					if (itemName.contains(name) && !itemName.equals("angler top") && !itemName.contains("Farmer's shirt")) {
						HAS_BODY[def.getId()] = false;
					}
				}

				for (final String name : has_jaw) {
					if (itemName.contains(name)) {
						HAS_JAW[def.getId()] = true;
					}
				}
				if (itemName.contains("full ")) {
					HAS_JAW[def.getId()] = false;
					HAS_HEAD[def.getId()] = true;
				}
			}
		}
	}

	public static boolean hasBody(int id) {
		return HAS_BODY[id];
	}

	public static boolean hasHead(int id) {
		return HAS_HEAD[id];
	}

	public static boolean hasJaw(int id) {
		return HAS_JAW[id];
	}

	/**
	 * Checks if a player owns a god cape.
	 */
	public static boolean hasGodCape(Player player) {
		final boolean inventory = player.getInventory().containsAny(2412, 2413, 2414);
		final boolean bank = player.getBank().containsAny(2412, 2413, 2414);
		final boolean equipment = player.getEquipment().containsAny(2412, 2413, 2414);
		return inventory || bank || equipment;
	}

	/**
	 * Checks if player is wearing an Anti-Fire Shield
	 */
	public static boolean isWearingAntiFire(Player player) {
		return player.getEquipment().containsAny(1540, 11283, 11284);
	}

	/**
	 * Checks if player is wearing a Dragonfire Shield
	 */
	public static boolean isWearingDFS(Player player) {
		return player.getEquipment().contains(11283);
	}

	/**
	 * Checks if player is wearing an Anti-Fire Shield
	 */
	public static boolean isWearingObbyWeapon(Player player) {
		return player.getEquipment().containsAny(6523, 6522, 6528, 6525, 6527, 6526);
	}

	/**
	 * Checks to see if player is wearing full Dharoks
	 */
	public static boolean isWearingDharoks(Player player) {
		final boolean weapon = player.getEquipment().containsAny(4718, 4886, 4887, 4888, 4889);
		final boolean helm = player.getEquipment().containsAny(4716, 4880, 4881, 4882, 4883);
		final boolean torso = player.getEquipment().containsAny(4720, 4892, 4893, 4894, 4895);
		final boolean legs = player.getEquipment().containsAny(4722, 4898, 4899, 4900, 4901);
		return weapon && helm && torso && legs;
	}

	/**
	 * Checks to see if player is wearing full Mage void
	 */
	public static boolean isWearingFullVoidMage(Player player) {
		return player.getEquipment().contains(new Item(11663), new Item(8839), new Item(8840), new Item(8842));
	}

	/**
	 * Checks to see if player is wearing full Melee void
	 */
	public static boolean isWearingFullVoidMelee(Player player) {
		return player.getEquipment().contains(new Item(11665), new Item(8839), new Item(8840), new Item(8842));
	}

	/**
	 * Checks to see if player is wearing full Range void
	 */
	public static boolean isWearingFullVoidRange(Player player) {
		return player.getEquipment().contains(new Item(11664), new Item(8839), new Item(8840), new Item(8842));
	}

	/**
	 * Checks to see if player is wearing full Guthans
	 */
	public static boolean isWearingGuthans(Player player) {
		final boolean weapon = player.getEquipment().containsAny(4726, 4910, 4911, 4912, 4913);
		final boolean helm = player.getEquipment().containsAny(4724, 4904, 4905, 4906, 4907);
		final boolean chest = player.getEquipment().containsAny(4728, 4916, 4917, 4918, 4919);
		final boolean legs = player.getEquipment().containsAny(4730, 4922, 4923, 4924, 4925);
		return weapon && helm && chest && legs;
	}

	/**
	 * Checks to see if player is wearing full Veracs
	 */
	public static boolean isWearingVeracs(Player player) {
		final boolean weapon = player.getEquipment().containsAny(4755, 4982, 4983, 4984, 4985);
		final boolean helm = player.getEquipment().containsAny(4753, 4976, 4977, 4978, 4979);
		final boolean chest = player.getEquipment().containsAny(4757, 4988, 4989, 4990, 4991);
		final boolean legs = player.getEquipment().containsAny(4759, 4994, 4995, 4996, 4997);
		return weapon && helm && chest && legs;
	}

	public static boolean usingCrystalBow(Player player) {
		return player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) != null && player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId() == 4222;
	}

	public static boolean wearingBlowpipe(Player player) {
		return player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) != null && player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId() == 12926;
	}

	public static boolean wearingBallista(Player player) {
		return player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) != null && player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId() == 19481;
	}

	public static boolean isCrossbow(Player player) {
		Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		if (weapon == null) return false;
		switch (weapon.getId()) {
			case 11785:
			case 9185:
			case 18357:
			case 21012:
				return true;
		}
		return false;
	}

	public static boolean isThrowingWeapon(Player player) {
		Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		if (weapon == null) return false;
		switch (weapon.getId()) {
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

	public static boolean isBow(Player player) {
		Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		if (weapon == null) return false;
		switch (weapon.getId()) {
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

	public static boolean isArrow(Player player) {
		Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		if (weapon == null) return false;
		switch (weapon.getId()) {
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

	public static boolean isBolt(Player player) {
		Item weapon = player.getEquipment().get(EquipmentConstants.AMMO_SLOT);
		if (weapon == null) return false;
		switch (weapon.getId()) {
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

	public static boolean usingRange(Player player) {
		if (wearingBlowpipe(player) || usingCrystalBow(player) || isCrossbow(player) || isThrowingWeapon(player) || isBow(player) || wearingBallista(player))
			return true;
		return false;
	}

	public final Item[] VENEMOUS_WEPS = {new Item(12926), new Item(12904), new Item(12899), new Item(12904)};
	public final Item[] VENEMOUS_HELMS = {new Item(13197), new Item(13199), new Item(12931)};

	public static boolean isWearingSpear(Player player) {

		String weapon = player.getEquipment().get(WEAPON_SLOT).getName().toLowerCase();
		if (weapon.contains("spear") || weapon.contains("hasta"))
			return true;
		return false;
	}

	public boolean canInfect(Player player) {
		for (Item i : VENEMOUS_WEPS) {
			if (player.getEquipment().get(EquipmentConstants.WEAPON_SLOT) == i) {
				return true;
			}
		}
		for (Item i : VENEMOUS_HELMS) {
			if (player.getEquipment().get(EquipmentConstants.HELM_SLOT) == i) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks to see if player is wearing full Torags
	 */
	public static boolean isWearingTorags(Player player) {
		final boolean weapon = player.getEquipment().containsAny(4747, 4958, 4959, 4960, 4961);
		final boolean helm = player.getEquipment().containsAny(4745, 4952, 4953, 4954, 4955);
		final boolean chest = player.getEquipment().containsAny(4749, 4964, 4965, 4966, 4967);
		final boolean legs = player.getEquipment().containsAny(4751, 4970, 4971, 4972, 4973);
		return weapon && helm && chest && legs;
	}
}