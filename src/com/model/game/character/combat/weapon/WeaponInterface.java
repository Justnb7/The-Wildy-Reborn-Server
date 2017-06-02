package com.model.game.character.combat.weapon;

import java.util.HashMap;
import java.util.Map;

import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.container.impl.equipment.EquipmentConstants;

/**
 * The class which represents functionality for the weapons interface.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 * @date 1-7-2016
 */
public class WeaponInterface {

	private Player player;

	public WeaponInterface(Player player) {
		this.player = player;
	}

	public enum weaponInterface {
		GREATAXE("greataxe", 1698, 1701, 1701), 
		BATTLEAXE("battleaxe", 1698, 1701, 1701), 
		TWO_HANDED("2h", 4705, 4708, 4708), 
		PICKAXE("pickaxe", 5570, 5573, 5573), 
		TWISTED_BOW("Twisted bow", 1764, 1767, 1765),
		SHORT_BOW_UPPER("Shortbow", 1764, 1767, 1765), 
		SHORT_BOW("shortbow", 1764, 1767, 1765), 
		THIRD_AGE_BOW("3rd age bow", 1764, 1767, 1765), 
		LONG_BOW("longbow", 1764, 1767, 1765), 
		LONG_BOW_UPPER("Longbow", 1764, 1767, 1765), 
		SEERCULL("seercull", 1764, 1767, 1765), 
		CROSSBOW("crossbow", 1764, 1767, 1765), 
		BLOWPIPE("blowpipe", 4446, 4449, 4447), 
		DART("dart", 4446, 4449, 4447), 
		KNIFE("knife", 4446, 4449, 4447), 
		JAVELIN("javelin", 4446, 4449, 4447), 
		DARK_BOW("Dark bow", 1764, 1767, 1765), 
		BALLISTA("ballista", 1764, 1767, 1765), 
		KARIL_CROSSBOW("Karil", 1764, 1767, 1765), 
		STAFF("staff", 328, 355, 355),
		TRIDENT("trident", 328, 355, 355), 
		TRIDENT_UPPPER("Trident", 328, 355, 355), 
		STAFF_UPPER("Staff", 328, 355, 355), 
		TOKTZ_MEJ_TAL("Toktz-mej_tal", 328, 355, 355), 
		TOKTZ_XIL_UL("Toktz-xil-ul", 4446, 4449, 4447), 
		TZHAAR_KET_OM("Tzhaar-ket-om", 425, 428, 426), 
		TZHAAR_KET_EM("Tzhaar-ket-em", 425, 428, 426), 
		DAGGER("dagger", 2276, 2279, 2277), 
		HALBERD("halberd", 8460, 8463, 8461), 
		SCYTHE("Scythe", 776, 779, 777), 
		SPEAR("spear", 4679, 4682, 4680),
		MJOLNIR("mjolnir", 4679, 4682, 4680), 
		MACE("mace", 3796, 3799, 3797), 
		ANCHOR("anchor", 3796, 3799, 3797), 
		VERAC_FLAIL("flail", 3796, 3799, 3797), 
		MAUL("maul", 425, 428, 426), 
		CLAW("claw", 7762, 7765, 7763), 
		FLOWERS("Flowers", 425, 428, 426), 
		WARHAMMER("warhammer", 425, 428, 426), 
		BLUDGEON("bludgeon", 425, 428, 426), 
		SCIMITAR("scimitar", 2423, 2426, 2424), 
		LONGSWORD("longsword", 423, 2426, 2424), 
		SWORD("sword", 2276, 2279, 2277), 
		SABRE("sabre", 2423, 2426, 2424), 
		DARKLIGHT("Darklight", 2423, 2426, 2424), 
		SILVERLIGHT("Silverlight", 2423, 2426, 2424), 
		MACHETE("machete", 2423, 2426, 2424), 
		WAND("wand", 328, 355, 355), 
		ABYSSAL_WHIP("whip", 12290, 12293, 12291), 
		ABYSSAL_TENTACLE("tentacle", 12290, 12293, 12291), 
		MAGIC_SECATEURS("Magic secateurs", 2423, 2426, 2424), 
		TORAG_HAMMER("Torag", 425, 428, 426), 
		SARADOMIN_SWORD("Saradomin sword", 4705, 4708, 4708), 
		BLESSED_SARADOMIN_SWORD("Saradomin's blessed sword", 4705, 4708, 4708), 
		GODSWORD("godsword", 4705, 4708, 4708),
		KORASI("Korasi's sword", 2423, 2426, 2424);
		/*STAFF_OF_THE_DEATH("Staff of the dead", 28500, 28500, 331),
		TOXIC_STAFF_OF_THE_DEATH("Toxic staff of the dead", 28500, 28500, 331);*/

		private String weaponType;
		private int interfaceId;
		private int nameOnInterfaceId;
		private int itemLocationId;

		private weaponInterface(String weaponType, int interfaceId, int nameOnInterfaceId, int itemLocationId) {
			this.weaponType = weaponType;
			this.interfaceId = interfaceId;
			this.nameOnInterfaceId = nameOnInterfaceId;
			this.itemLocationId = itemLocationId;
		}

		public String getWeaponType() {
			return weaponType;
		}

		public int getInterface() {
			return interfaceId;
		}

		public int getNameOnInterfaceId() {
			return nameOnInterfaceId;
		}

		public int getItemLocation() {
			return itemLocationId;
		}
	}

	private static enum WeaponSpecials {
		
		CROSSBOW(new int[] { 4212, 11748, 12788, 861, 859, 11785, 11235, 12765, 12766, 12767, 12768, 12926, 19478, 19481 }, 7549, 7561),
		DAGGER_INTERFACE(new int[] { 1305, 1215, 1231, 5698, 5680, 13265, 13267, 12369, 13271 }, 7574, 7586),
		GODSWORD_INTERFACE(new int[] { 11802, 11804, 11806, 11808, 11838, 12809 }, 7699, 7711),
		WHIP_INTERFACE(new int[] { 4151, 12006, 12773 }, 12323, 12335),
		SCIMITAR_INTERFACE(new int[] { 4587, 19780 }, 7599, 7611),
		KORASI_SWORD_INTERFACE(new int[] { 19780 }, 7599, 7611),
		SPEAR_INTERFACE(new int[] { 1249 }, 7674, 7686),
		MAUL_INTERFACE(new int[] { 4153, 12848, 13902, 13576, 13263 }, 7474, 7486),
		AXE_INTERFACE(new int[] { 1377 }, 7499, 7511),
		HALBERD_INTERFACE(new int[] { 3204, 13091, 13092, 13081 }, 8493, 8505),
		MACE_INTERFACE(new int[] { 1434, 10887 }, 7624, 7636),
		CLAWS(new int[] { 13652 }, 7800, 7812),
		BLOWPIPE(new int[] { 12926 }, 7649, 7661);
		//STAFF_OF_THE_DEATH(new int[] { 11791, 12904 }, 28500, 28500);

		private int[] itemIds;
		private int configId, specialBarId;

		private WeaponSpecials(int[] itemIds, int configId, int specialBarId) {
			this.itemIds = itemIds;
			this.configId = configId;
			this.specialBarId = specialBarId;
		}

		public int getConfigId() {
			return configId;
		}

		public int getSpecialBarId() {
			return specialBarId;
		}

		private static Map<Integer, WeaponSpecials> weapons = new HashMap<Integer, WeaponSpecials>();

		static {
			for (WeaponSpecials spec : values()) {
				for (int i : spec.itemIds) {
					weapons.put(i, spec);
				}
			}
		}

		public static WeaponSpecials forId(int id) {
			return weapons.get(id);
		}
	}

	/**
	 * Sends the special bar interface
	 * 
	 * @param id
	 */
	public void sendSpecialBar(Item id) {
		if(id == null) {
			System.out.println("The player isn't wearing any items.");
			return;
		}
		WeaponSpecials spec = WeaponSpecials.forId(id.getId());
		if (spec == null) {
			player.getActionSender().sendInterfaceConfig(1, WeaponSpecials.DAGGER_INTERFACE.getConfigId());
			player.getActionSender().sendInterfaceConfig(1, WeaponSpecials.KORASI_SWORD_INTERFACE.getConfigId());
			player.getActionSender().sendInterfaceConfig(1, WeaponSpecials.WHIP_INTERFACE.getConfigId());
			player.getActionSender().sendInterfaceConfig(1, WeaponSpecials.SCIMITAR_INTERFACE.getConfigId());
			player.getActionSender().sendInterfaceConfig(1, WeaponSpecials.SPEAR_INTERFACE.getConfigId());
			player.getActionSender().sendInterfaceConfig(1, WeaponSpecials.MAUL_INTERFACE.getConfigId());
			player.getActionSender().sendInterfaceConfig(1, WeaponSpecials.AXE_INTERFACE.getConfigId());
			player.getActionSender().sendInterfaceConfig(1, WeaponSpecials.HALBERD_INTERFACE.getConfigId());
			player.getActionSender().sendInterfaceConfig(1, WeaponSpecials.MACE_INTERFACE.getConfigId());
			player.getActionSender().sendInterfaceConfig(1, WeaponSpecials.CROSSBOW.getConfigId());
			player.getActionSender().sendInterfaceConfig(1, WeaponSpecials.CLAWS.getConfigId());
			player.getActionSender().sendInterfaceConfig(1, WeaponSpecials.GODSWORD_INTERFACE.getConfigId());
			player.getActionSender().sendInterfaceConfig(1, WeaponSpecials.BLOWPIPE.getConfigId());
		} else {
			player.getActionSender().sendInterfaceConfig(0, spec.getConfigId());
			specialAmount(id, player.getSpecialAmount(), spec.getSpecialBarId());
		}
	}

	/**
	 * Specials bar filling amount
	 *
	 * @param weapon
	 *            Weapon's id
	 * @param specAmount
	 *            The spec's amount
	 * @param barId
	 *            The Bar's id
	 */
	public void specialAmount(Item weapon, int specAmount, int barId) {
		player.specBarId = barId;
		player.getActionSender().moveComponent(specAmount >= 100 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 90 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 80 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 70 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 60 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 50 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 40 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 30 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 20 ? 500 : 0, 0, (--barId));
		player.getActionSender().moveComponent(specAmount >= 10 ? 500 : 0, 0, (--barId));
		refreshSpecialAttack();
	}

	public void refreshSpecialAttack() {
		if (player.isUsingSpecial()) {
			player.getActionSender().sendString("@yel@ Special Attack ("+player.getSpecialAmount() * 1 +"%)", player.specBarId);
		} else {
			player.getActionSender().sendString("@bla@ Special Attack ("+player.getSpecialAmount() * 1 +"%)", player.specBarId);
		}
	}
	
	public void restoreWeaponAttributes() {
		refreshSpecialAttack();
		sendSpecialBar(player.getEquipment().get(EquipmentConstants.WEAPON_SLOT));
	}
}
