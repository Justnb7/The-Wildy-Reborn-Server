package com.model.game.character.player.skill.crafting;

import com.model.game.character.player.skill.SkillHandler;

public class CraftingData extends SkillHandler {

	/**
	 * Stores all the data in a enumeration
	 */
	public static enum tanningData {

		SOFT_LEATHER(new int[][] { { 57225, 1 }, { 57217, 5 }, { 57201, 28 } }, 1739, 1741, 1, new int[] { 14777, 14785, 14769 }, "Soft leather"),
		HARD_LEATHER(new int[][] { { 57226, 100 }, { 57218, 5 }, { 57202, 28 } }, 1739, 1743, 125, new int[] { 14778, 14786, 14770 }, "Hard leather"),
		SNAKESKIN(new int[][] { { 57227, 1 }, { 57219, 5 }, { 57203, 28 } }, 6287, 6289, 150, new int[] { 14779, 14787, 14771 }, "Snakeskin"),
		SNAKESKIN2(new int[][] { { 57228, 1 }, { 57220, 5 }, { 57204, 28 } }, 6287, 6289, 150, new int[] { 14780, 14788, 14772 }, "Snakeskin"),
		GREEN_DRAGON_LEATHER(new int[][] { { 57229, 1 }, { 57221, 5 }, { 57205, 28 } }, 1753, 1745, 215, new int[] { 14781, 14789, 14773 }, "Green d'hide"),
		BLUE_DRAGON_LEATHER(new int[][] { { 57230, 1 }, { 57222, 5 }, { 57206, 28 } }, 1751, 2505, 245, new int[] { 14782, 14790, 14774 }, "Blue d'hide"),
		RED_DRAGON_LEATHER(new int[][] { { 57231, 1 }, { 57223, 5 }, { 57207, 28 } }, 1749, 2507, 300, new int[] { 14783, 14791, 14775 }, "Red d'hide"),
		BLACK_DRAGON_LEATHER(new int[][] { { 57232, 1 }, { 57224, 5 }, { 57208, 28 } }, 1747, 2509, 350, new int[] { 14784, 14792, 14776 }, "Black d'hide");

		/**
		 * Interface buttonId
		 */
		private int[][] buttonId;
		
		/**
		 * The hide item id
		 */
		private int hideId;
		
		/**
		 * The leather item id
		 */
		private int leatherId;
		
		/**
		 * Cost to tan the hide
		 */
		private int price;
		
		/**
		 * Interface frameId
		 */
		private int[] frameId;
		
		/**
		 * Item name
		 */
		private String name;

		/**
		 * All the data that is being called in the enum
		 * @param buttonId
		 * @param hideId
		 * @param leatherId
		 * @param price
		 * @param frameId
		 * @param name
		 */
		private tanningData(final int[][] buttonId, final int hideId, final int leatherId, final int price, final int[] frameId, final String name) {
			this.buttonId = buttonId;
			this.hideId = hideId;
			this.leatherId = leatherId;
			this.price = price;
			this.frameId = frameId;
			this.name = name;
		}

		/**
		 * Gets the button from the interface
		 * @param button
		 * @return
		 */
		public int getButtonId(final int button) {
			for (int i = 0; i < buttonId.length; i++) {
				if (button == buttonId[i][0]) {
					return buttonId[i][0];
				}
			}
			return -1;
		}

		/**
		 * Gets the amount of buttons
		 * @param button
		 * @return
		 */
		public int getAmount(final int button) {
			for (int i = 0; i < buttonId.length; i++) {
				if (button == buttonId[i][0]) {
					return buttonId[i][1];
				}
			}
			return -1;
		}

		/**
		 * Grabs the hide item id
		 * @return
		 */
		public int getHideId() {
			return hideId;
		}

		/**
		 * Grabs the leather item id
		 * @return
		 */
		public int getLeatherId() {
			return leatherId;
		}

		/**
		 * Price for tanning
		 * @return
		 */
		public int getPrice() {
			return price;
		}

		/**
		 * Frame
		 * @return
		 */
		public int getNameFrame() {
			return frameId[0];
		}

		/**
		 * Frame
		 * @return
		 */
		public int getCostFrame() {
			return frameId[1];
		}

		/**
		 * Frame
		 * @return
		 */
		public int getItemFrame() {
			return frameId[2];
		}

		/**
		 * Grabs the item name
		 * @return
		 */
		public String getName() {
			return name;
		}
	}

	/**
	 * Stores all the data in a enumeration
	 */
	public static enum leatherDialogueData {

		GREEN_LEATHER(1745, 1065, 1099, 1135),
		BLUE_LEATHER(2505, 2487, 2493, 2499),
		RED_LEATHER(2507, 2489, 2495, 2501),
		BLACK_LEATHER(2509, 2491, 2497, 2503);

		/**
		 * Leather id
		 */
		private int leather;
		
		/**
		 * D'hide vambrace id
		 */
		private int vambraces;
		
		/**
		 * D'hide chaps id
		 */
		private int chaps;
		
		/**
		 * D'hide body id
		 */
		private int body;
		

		/**
		 * Stored data
		 * @param leather
		 * @param vambraces
		 * @param chaps
		 * @param body
		 */
		private leatherDialogueData(final int leather, final int vambraces, final int chaps, final int body) {
			this.leather = leather;
			this.vambraces = vambraces;
			this.chaps = chaps;
			this.body = body;
		}

		/**
		 * Grabs the leather item id
		 * @return
		 */
		public int getLeather() {
			return leather;
		}

		/**
		 * Grabs the D'hide vambrace id
		 * @return
		 */
		public int getVambraces() {
			return vambraces;
		}

		/**
		 * Grabs the D'hide chaps id
		 * @return
		 */
		public int getChaps() {
			return chaps;
		}

		/**
		 * Grabs the D'hide body id
		 * @return
		 */
		public int getBody() {
			return body;
		}
	}

	/**
	 * Stores all the data in a enumeration
	 */
	public static enum leatherData {

		LEATHER_BODY(new int[][] { { 33187, 1 }, { 33186, 5 }, { 33185, 10 } }, 1741, 1129, 14, 25, 1),
		LEATHER_GLOVES(new int[][] { { 33190, 1 }, { 33189, 5 }, { 33188, 10 } }, 1741, 1059, 1, 13.8, 1),
		LEATHER_BOOTS(new int[][] { { 33193, 1 }, { 33192, 5 }, { 33191, 10 } }, 1741, 1061, 7, 16.25, 1),
		LEATHER_VAMBRACES(new int[][] { { 33196, 1 }, { 33195, 1 }, { 33194, 1 } }, 1741, 1063, 11, 22, 1),
		LEATHER_CHAPS(new int[][] { { 33199, 1 }, { 33198, 5 }, { 33197, 10 } }, 1741, 1095, 18, 27, 1),
		LEATHER_COIF(new int[][] { { 33202, 1 }, { 33201, 5 }, { 33200, 10 } }, 1741, 1169, 38, 37, 1),
		LEATHER_COWL(new int[][] { { 33205, 1 }, { 33204, 5 }, { 33203, 10 } }, 1741, 1167, 9, 18.5, 1),
		HARD_LEATHER_BODY(new int[][] { { 10239, 1 }, { 10238, 10 }, { 6212, 28 } }, 1743, 1131, 28, 35, 1),
		SNAKESKIN_BODY(new int[][] { { 34245, 1 }, { 34244, 5 }, { 34243, 10 }, { 34242, 28 } }, 6289, 6322, 53, 55, 15),
		SNAKESKIN_CHAPS(new int[][] { { 34249, 1 }, { 34248, 5 }, { 34247, 10 }, { 34246, 28 } }, 6289, 6324, 51, 50, 12),
		SNAKESKIN_BANDANA(new int[][] { { 34253, 1 }, { 34252, 5 }, { 34251, 10 }, { 34250, 28 } }, 6289, 6326, 48, 45, 5),
		SNAKESKIN_BOOTS(new int[][] { { 35001, 1 }, { 35000, 5 }, { 34255, 10 }, { 34254, 28 } }, 6289, 6328, 45, 30, 6),
		SNAKESKIN_VAMBRACES(new int[][] { { 35005, 1 }, { 35004, 5 }, { 35003, 10 }, { 35002, 28 } }, 6289, 6330, 47, 35, 8),
		GREEN_DHIDE_VAMBRACES(new int[][] { { 34185, 1 }, { 34184, 5 }, { 34183, 10 }, { 34182, 28 } }, 1745, 1065, 57, 62, 1),
		GREEN_DHIDE_BODY(new int[][] { { 34189, 1 }, { 34188, 5 }, { 34187, 10 }, { 34186, 28 } }, 1745, 1135, 63, 186, 3),
		GREEN_DHIDE_CHAPS(new int[][] { { 34193, 1 }, { 34192, 5 }, { 34191, 10 }, { 34190, 28 } }, 1745, 1099, 60, 124, 2),
		BLUE_DHIDE_VAMBRACES(new int[][] { { 34185, 1 }, { 34184, 5 }, { 34183, 10 }, { 34182, 28 } }, 2505, 2487, 66, 70, 1),
		BLUE_DHIDE_BODY(new int[][] { { 34189, 1 }, { 34188, 5 }, { 34187, 10 }, { 34186, 28 } }, 2505, 2499, 71, 210, 3),
		BLUE_DHIDE_CHAPS(new int[][] { { 34193, 1 }, { 34192, 5 }, { 34191, 10 }, { 34190, 28 } }, 2505, 2493, 68, 140, 2),
		RED_DHIDE_VAMBRACES(new int[][] { { 34185, 1 }, { 34184, 5 }, { 34183, 10 }, { 34182, 28 } }, 2507, 2489, 73, 78, 1),
		RED_DHIDE_BODY(new int[][] { { 34189, 1 }, { 34188, 5 }, { 34187, 10 }, { 34186, 28 } }, 2507, 2501, 77, 234, 3),
		RED_DHIDE_CHAPS(new int[][] { { 34193, 1 }, { 34192, 5 }, { 34191, 10 }, { 34190, 28 } }, 2507, 2495, 75, 156, 2),
		BLACK_DHIDE_VAMBRACES(new int[][] { { 34185, 1 }, { 34184, 5 }, { 34183, 10 }, { 34182, 28 } }, 2509, 2491, 79, 86, 1),
		BLACK_DHIDE_BODY(new int[][] { { 34189, 1 }, { 34188, 5 }, { 34187, 10 }, { 34186, 28 } }, 2509, 2503, 84, 258, 3),
		BLACK_DHIDE_CHAPS(new int[][] { { 34193, 1 }, { 34192, 5 }, { 34191, 10 }, { 34190, 28 } }, 2509, 2497, 82, 172, 2);

		/**
		 * Interface buttonId
		 */
		private int[][] buttonId;
		
		/**
		 * Leather item id
		 */
		private int leather;
		
		/**
		 * productId
		 */
		private int product;
		
		/**
		 * Level requirement
		 */
		private int level;
		
		/**
		 * Amount
		 */
		private int amount;
		
		/**
		 * Experience
		 */
		private double xp;

		/**
		 * Data stored
		 * @param buttonId
		 * @param leather
		 * @param product
		 * @param level
		 * @param xp
		 * @param amount
		 */
		private leatherData(final int[][] buttonId, final int leather, final int product, final int level, final double xp, final int amount) {
			this.buttonId = buttonId;
			this.leather = leather;
			this.product = product;
			this.level = level;
			this.xp = xp;
			this.amount = amount;
		}

		/**
		 * Grabs the button id
		 * @param button
		 * @return
		 */
		public int getButtonId(final int button) {
			for (int i = 0; i < buttonId.length; i++) {
				if (button == buttonId[i][0]) {
					return buttonId[i][0];
				}
			}
			return -1;
		}

		/**
		 * Amount of buttons
		 * @param button
		 * @return
		 */
		public int getAmount(final int button) {
			for (int i = 0; i < buttonId.length; i++) {
				if (button == buttonId[i][0]) {
					return buttonId[i][1];
				}
			}
			return -1;
		}

		/**
		 * Grabs the leather item
		 * @return
		 */
		public int getLeather() {
			return leather;
		}

		/**
		 * Gets the final product id
		 * @return
		 */
		public int getProduct() {
			return product;
		}

		/**
		 * Level requirement
		 * @return
		 */
		public int getLevel() {
			return level;
		}

		/**
		 * Expierence gain
		 * @return
		 */
		public double getXP() {
			return xp;
		}

		/**
		 * Grabs the amount of hides needed
		 * @return
		 */
		public int getHideAmount() {
			return amount;
		}
	}

	/**
	 * Stores all the data in a enumeration
	 */
	public static enum jewelryData {

		RINGS(new int[][] { { 2357, 1635, 5, 15 }, { 1607, 1637, 20, 40 }, { 1605, 1639, 27, 55 }, { 1603, 1641, 34, 70 }, { 1601, 1643, 43, 85 }, { 1615, 1645, 55, 100 }, { 6573, 6575, 67, 115 } }),
		NECKLACE(new int[][] { { 2357, 1654, 6, 20 }, { 1607, 1656, 22, 55 }, { 1605, 1658, 29, 60 }, { 1603, 1660, 40, 75 }, { 1601, 1662, 56, 90 }, { 1615, 1664, 72, 105 }, { 6573, 6577, 82, 120 } }),
		AMULETS(new int[][] { { 2357, 1673, 8, 30 },{ 1601, 1681, 70, 100 }, { 1607, 1675, 24, 65 }, { 1605, 1677, 31, 70 }, { 1603, 1679, 50, 85 }, { 1615, 1683, 80, 150 },  { 1615, 1683, 80, 150 }, { 6573, 6579, 90, 165 } });

		/**
		 * Grabs the item[] ids
		 */
		public int[][] item;

		private jewelryData(int[][] item) {
			this.item = item;
		}
	}
	
}