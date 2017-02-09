package com.model.game.character.player.skill.crafting.jewelry;

import com.model.game.Constants;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendClearScreen;
import com.model.game.character.player.packets.encode.impl.SendActionInterface;
import com.model.game.character.player.packets.encode.impl.SendString;
import com.model.game.character.player.packets.encode.impl.SendInterface;
import com.model.game.character.player.packets.encode.impl.SendInterfaceModel;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.crafting.CraftingData;

/**
 * 
 * @author Patrick van Elderen
 * @date 1-4-2016 00:46 AM
 * @credits to the creator of the data being used
 */

public class JewelleryMaking extends CraftingData {
	
	/**
	 * Rings data
	 */

	private static final int[][] RINGS = { { 1635, -1, 5, 15 }, { 1637, 1607, 20, 40 }, { 1639, 1605, 27, 55 }, { 1641, 1603, 34, 70 }, { 1643, 1601, 43, 85 }, { 1645, 1615, 55, 100 }, { 6575, 6573, 67, 115 }};

	/**
	 * Necklaces data
	 */
	private static int[][] NECKLACES = { { 1654, -1, 6, 20 }, { 1656, 1607, 22, 55 }, { 1658, 1605, 29, 60 }, { 1660, 1603, 40, 75 }, { 1662, 1601, 56, 90 }, { 1664, 1615, 72, 105 }, { 6577, 6573, 82, 120 }};
	
	/**
	 * Amulets data
	 */
	private static int[][] AMULETS = { { 1673, -1, 8, 30 }, { 1675, 1607, 24, 65 }, { 1677, 1605, 31, 70 }, { 1679, 1603, 50, 85 }, { 1681, 1601, 70, 100 }, { 1683, 1615, 80, 150 }, { 6579, 6573, 90, 165 }};

	/**
	 * Interface ids
	 */
	private static final int[][] MOULD_INTERFACE_IDS = {
	/**
	 * Rings 
	 */
	{1635, 1637, 1639, 1641, 1643, 1645, 6575},
	
	/** 
	 * Necklaces
	 */
	
	{1654, 1656, 1658, 1660, 1662, 1664, 6577},
	
	/**
	 * Aamulets
	 */
	
	{1673, 1675, 1677, 1679, 1681, 1683, 6579 }};

	/**
	 * Opens the mould interface
	 * @param player
	 */
	public static void mouldInterface(Player player) {
		player.write(new SendInterface(4161));
		/**
		 * Rings
		 */
		if (player.getItems().playerHasItem(1592, 1)) {
			for (int i = 0; i < MOULD_INTERFACE_IDS[0].length; i++) {
				player.write(new SendActionInterface(MOULD_INTERFACE_IDS[0][i], i, 4233, 1));
			}
			player.write(new SendActionInterface(1643, 4, 4233, 1));
			player.write(new SendString("", 4230));
			player.write(new SendInterfaceModel(4229, 0, -1));
		} else {
			player.write(new SendInterfaceModel(4229, 120, 1592));
			for (int i = 0; i < MOULD_INTERFACE_IDS[0].length; i++) {
				player.write(new SendActionInterface(-1, i, 4233, 1));
			}
			player.write(new SendString(
					"You need a ring mould to craft rings.", 4230));
		}
		/**
		 * Necklaces
		 */
		if (player.getItems().playerHasItem(1597, 1)) {
			for (int i = 0; i < MOULD_INTERFACE_IDS[1].length; i++) {
				player.write(new SendActionInterface(MOULD_INTERFACE_IDS[1][i], i, 4239, 1));
			}
			player.write(new SendActionInterface(1662, 4, 4239, 1));
			player.write(new SendInterfaceModel(4235, 0, -1));
			player.write(new SendString("", 4236));
		} else {
			player.write(new SendInterfaceModel(4235, 120, 1597));
			player.write(new SendString("You need a necklace mould to craft necklaces", 4236));
			for (int i = 0; i < MOULD_INTERFACE_IDS[1].length; i++) {
				player.write(new SendActionInterface(-1, i, 4239, 1));
			}
		}
		/**
		 * Amulets
		 */
		if (player.getItems().playerHasItem(1595, 1)) {
			for (int i = 0; i < MOULD_INTERFACE_IDS[2].length; i++) {
				player.write(new SendActionInterface(MOULD_INTERFACE_IDS[2][i], i, 4245, 1));
			}
			player.write(new SendActionInterface(1681, 4, 4245, 1));
			player.write(new SendInterfaceModel(4241, 0, -1));
			player.write(new SendString("", 4242));
		} else {
			player.write(new SendInterfaceModel(4241, 120, 1595));
			player.write(new SendString("You need a amulet mould to craft necklaces", 4242));
			for (int i = 0; i < MOULD_INTERFACE_IDS[2].length; i++) {
				player.write(new SendActionInterface(-1, i, 4245, 1));
			}
		}
	}

	/**
	 * Mould items
	 * @param player
	 * @param item
	 * @param amount
	 */
	public static void mouldItem(Player player, int item, int amount) {
		int done = 0;

		final int GOLD_BAR = 2357;

		boolean isRing = false;
		boolean isNeck = false;
		boolean isAmulet = false;
		int gem = 0;
		int itemAdd = -1;
		int xp = 0;
		int lvl = 1;
		for (int i = 0; i < 7; i++) {
			if (item == RINGS[i][0]) {
				isRing = true;
				itemAdd = RINGS[i][0];
				gem = RINGS[i][1];
				lvl = RINGS[i][2];
				xp = RINGS[i][3];
				break;
			}
			if (item == NECKLACES[i][0]) {
				isNeck = true;
				itemAdd = NECKLACES[i][0];
				gem = NECKLACES[i][1];
				lvl = NECKLACES[i][2];
				xp = NECKLACES[i][3];
				break;
			}
			if (item == AMULETS[i][0]) {
				isAmulet = true;
				itemAdd = AMULETS[i][0];
				gem = AMULETS[i][1];
				lvl = AMULETS[i][2];
				xp = AMULETS[i][3];
				break;
			}
		}
		if (!isRing && !isNeck && !isAmulet) {
			return;
		}
		if (player.getSkills().getLevel(Skills.CRAFTING) >= lvl) {
			if (player.getItems().getItemName(itemAdd).toLowerCase().contains("gold") && !player.getItems().playerHasItem(GOLD_BAR, 1) || !player.getItems().playerHasItem(GOLD_BAR, 1)) {
				player.write(new SendMessagePacket("You need a Gold bar to make this."));
				return;
			} else if (!player.getItems().playerHasItem(gem, 1) && player.getItems().playerHasItem(GOLD_BAR, 1)) {
				player.write(new SendMessagePacket(getRequiredMessage(player.getItems().getItemName(gem))));
				return;
			}
			player.write(new SendClearScreen());
			while ((done < amount) && (player.getItems().getItemName(gem).toLowerCase().contains("unarmed") && player.getItems().playerHasItem(GOLD_BAR, 1) || player.getItems().playerHasItem(gem, 1) && player.getItems().playerHasItem(GOLD_BAR, 1))) {
				player.getItems().deleteItem(gem, 1);
				player.getItems().deleteItem(GOLD_BAR, 1);
				player.getItems().addItem(itemAdd, 1);
				player.getSkills().addExperience(Skills.CRAFTING, xp * Constants.SKILL_MODIFIER);
				done++;
			}
			if (done == 1) {
				player.getItems();
				player.write(new SendMessagePacket("You craft the gold and gem together to form a "
						+ player.getItems().getItemName(itemAdd) + "."));
			} else if (done > 1) {
				player.getItems();
				player.write(new SendMessagePacket("You craft the gold and gem together to form " + done + " " + player.getItems().getItemName(itemAdd) + "'s."));
			}
		} else {
			player.write(new SendMessagePacket("You need a Crafting level of " + lvl + " to craft this."));
			return;
		}
	}

	/**
	 * Sends message with needed requirements
	 * @param item
	 * @return
	 */
	public static String getRequiredMessage(String item) {
		if (item.startsWith("A") || item.startsWith("E") || item.startsWith("I") || item.startsWith("O") || item.startsWith("U")) {
			return "You need a Gold bar and an " + item + " to make this.";
		} else {
			return "You need a Gold bar and a " + item + " to make this.";
		}
	}

	/**
	 * Method used to string amulets
	 * @param player
	 * @param itemUsed
	 * @param usedWith
	 */
	public static void stringAmulet(Player player, final int itemUsed,
			final int usedWith) {
		final int amuletId = (itemUsed == 1759 ? usedWith : itemUsed);
		for (final amuletData a : amuletData.values()) {
			if (amuletId == a.getAmuletId()) {
				player.getItems().deleteItem(1759, 1);
				player.getItems().deleteItem(amuletId, 1);
				player.getItems().addItem(a.getProduct(), 1);
				player.getSkills().addExperience(Skills.CRAFTING, Constants.SKILL_MODIFIER);
			}
		}
	}

	/**
	 * All amulet data being stored in a enumeration
	 */
	public static enum amuletData {
		GOLD(1673, 1692),
		SAPPHIRE(1675, 1694),
		EMERALD(1677, 1696), 
		RUBY(1679, 1698), 
		DIAMOND(1681, 1700), 
		DRAGONSTONE(1683, 1702), 
		ONYX(6579, 6581);

		/**
		 * Represents the amulet id
		 */
		private int amuletId;
		
		/**
		 * Represents the finished product (A.K.A) "Amulet"
		 */
		private int product;

		/**
		 * Data being stored
		 * @param amuletId
		 * @param product
		 */
		private amuletData(final int amuletId, final int product) {
			this.amuletId = amuletId;
			this.product = product;
		}

		/**
		 * Defines the amulet id
		 * @return
		 */
		public int getAmuletId() {
			return amuletId;
		}

		/**
		 * Defines the final amulet
		 * @return
		 */
		public int getProduct() {
			return product;
		}
	}
}