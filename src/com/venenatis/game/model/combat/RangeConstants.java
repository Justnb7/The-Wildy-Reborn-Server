package com.venenatis.game.model.combat;

import com.venenatis.game.model.masks.Graphic;

public class RangeConstants {
	
	/**
	 * An enum that represents a type of range bow.
	 * 
	 * @author Michael
	 *
	 */
	public enum BowType {
		LONGBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW }, new int[] { 10, 10, 10 }),

		SHORTBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW }, new int[] { 7, 7, 9 }),

		OAK_SHORTBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW }, new int[] { 7, 7, 9 }),

		OAK_LONGBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW }, new int[] { 10, 10, 10 }),

		WILLOW_LONGBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW }, new int[] { 10, 10, 10 }),

		WILLOW_SHORTBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW }, new int[] { 7, 7, 9 }),

		MAPLE_LONGBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW, ArrowType.MITHRIL_ARROW }, new int[] { 10, 10, 10 }),

		MAPLE_SHORTBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW, ArrowType.MITHRIL_ARROW }, new int[] { 7, 7, 9 }),

		YEW_LONGBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW, ArrowType.MITHRIL_ARROW, ArrowType.ADAMANT_ARROW }, new int[] { 10, 10, 10 }),

		YEW_SHORTBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW, ArrowType.MITHRIL_ARROW, ArrowType.ADAMANT_ARROW }, new int[] { 7, 7, 9 }),

		MAGIC_LONGBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW, ArrowType.MITHRIL_ARROW, ArrowType.ADAMANT_ARROW, ArrowType.RUNE_ARROW }, new int[] { 10, 10, 10 }),

		MAGIC_SHORTBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW, ArrowType.MITHRIL_ARROW, ArrowType.ADAMANT_ARROW, ArrowType.RUNE_ARROW }, new int[] { 7, 7, 9 }),

		CRYSTAL_BOW(new ArrowType[0], new int[] { 10, 10, 10 }),

		KARILS_XBOW(new ArrowType[] { ArrowType.BOLT_RACK }, new int[] { 7, 7, 9 }),

		DARK_BOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW, ArrowType.MITHRIL_ARROW, ArrowType.ADAMANT_ARROW, ArrowType.RUNE_ARROW, ArrowType.DRAGON_ARROW }, new int[] { 10, 10, 10 }),

		TWISTED_BOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW, ArrowType.MITHRIL_ARROW, ArrowType.ADAMANT_ARROW, ArrowType.RUNE_ARROW, ArrowType.DRAGON_ARROW }, new int[] { 10, 10, 10 }),
		
		BRONZE_CBOW(new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT }, new int[] { 7, 7, 9 }),

		IRON_CBOW(new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT }, new int[] { 7, 7, 9 }),

		STEEL_CBOW(new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT, ArrowType.STEEL_BOLT }, new int[] { 7, 7, 9 }),

		MITH_CBOW(new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT, ArrowType.STEEL_BOLT, ArrowType.MITHRIL_BOLT }, new int[] { 7, 7, 9 }),

		ADAMANT_CBOW(new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT, ArrowType.STEEL_BOLT, ArrowType.MITHRIL_BOLT, ArrowType.ADAMANT_BOLT }, new int[] { 7, 7, 9 }),

		RUNE_CBOW(new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT, ArrowType.STEEL_BOLT, ArrowType.MITHRIL_BOLT, ArrowType.ADAMANT_BOLT, ArrowType.RUNE_BOLT }, new int[] { 7, 7, 9 }),
		
		ARMADYL_CBOW(new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT, ArrowType.STEEL_BOLT, ArrowType.MITHRIL_BOLT, ArrowType.ADAMANT_BOLT, ArrowType.RUNE_BOLT }, new int[] { 7, 7, 9 }),
		
		CHAOTIC_CBOW(new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT, ArrowType.STEEL_BOLT, ArrowType.MITHRIL_BOLT, ArrowType.ADAMANT_BOLT, ArrowType.RUNE_BOLT }, new int[] { 7, 7, 9 }), 
		
		DRAGON_CBOW(new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT, ArrowType.STEEL_BOLT, ArrowType.MITHRIL_BOLT, ArrowType.ADAMANT_BOLT, ArrowType.RUNE_BOLT }, new int[] { 7, 7, 9 }), 
		
		;//end of enum

		/**
		 * The arrows this bow can use.
		 */
		private ArrowType[] validArrows;

		/**
		 * The distances required to be near the victim based on the mob's
		 * combat style.
		 */
		private int[] distances;

		private BowType(ArrowType[] validArrows, int[] distances) {
			this.validArrows = validArrows;
			this.distances = distances;
		}

		/**
		 * Gets the valid arrows this bow can use.
		 * 
		 * @return The valid arrows this bow can use.
		 */
		public ArrowType[] getValidArrows() {
			return validArrows;
		}

		/**
		 * Gets a valid arrow this bow can use by its index.
		 * 
		 * @param index
		 *            The arrow index.
		 * @return The valid arrow this bow can use by its index.
		 */
		public ArrowType getValidArrow(int index) {
			return validArrows[index];
		}

		/**
		 * Gets a distance required to be near the victim.
		 * 
		 * @param index
		 *            The combat style index.
		 * @return The distance required to be near the victim
		 */
		public int getDistance(int index) {
			return distances[index];
		}
	}
	

	/**
	 * An enum for all arrow types, this includes the drop rate percentage of
	 * this arrow (the higher quality the less likely it is to disappear).
	 * 
	 * @author Michael Bull
	 * @author Sir Sean
	 */
	public enum ArrowType {

		BRONZE_ARROW(0.75, Graphic.create(19, 0, 100), 10),

		IRON_ARROW(0.7, Graphic.create(18, 0, 100), 9),

		STEEL_ARROW(0.65, Graphic.create(20, 0, 100), 11),

		MITHRIL_ARROW(0.6, Graphic.create(21, 0, 100), 12),

		ADAMANT_ARROW(0.5, Graphic.create(22, 0, 100), 13),

		RUNE_ARROW(0.4, Graphic.create(24, 0, 100), 15),

		BOLT_RACK(1.1, null, 27),

		DRAGON_ARROW(0.3, Graphic.create(1111, 0, 100), 1115),

		BRONZE_BOLT(0.75, null, 27),

		IRON_BOLT(0.7, null, 27),

		STEEL_BOLT(0.65, null, 27),

		MITHRIL_BOLT(0.6, null, 27),

		ADAMANT_BOLT(0.5, null, 27),

		RUNE_BOLT(0.4, null, 27),

		CRYSTAL_ARROW(1.1, Graphic.create(250, 0, 100), 249);

		/**
		 * The percentage chance for the arrow to disappear once fired.
		 */
		private double dropRate;

		/**
		 * The pullback graphic.
		 */
		private Graphic pullback;

		/**
		 * The projectile id.
		 */
		private int projectile;

		/**
		 * 
		 * @param dropRate
		 * @param pullback
		 * @param projectile
		 */
		private ArrowType(double dropRate, Graphic pullback, int projectile) {
			this.dropRate = dropRate;
			this.pullback = pullback;
			this.projectile = projectile;
		}

		/**
		 * Gets the arrow's percentage chance to disappear once fired
		 * 
		 * @return The arrow's percentage chance to disappear once fired.
		 */
		public double getDropRate() {
			return dropRate;
		}

		/**
		 * Gets the arrow's pullback graphic.
		 * 
		 * @return The arrow's pullback graphic.
		 */
		public Graphic getPullbackGraphic() {
			return pullback;
		}

		/**
		 * Gets the arrow's projectile id.
		 * 
		 * @return The arrow's projectile id.
		 */
		public int getProjectileId() {
			return projectile;
		}
	}

	/**
	 * An enum that represents all range weapons, e.g. throwing knives and
	 * javelins.
	 * 
	 * @author Michael
	 *
	 */
	public enum RangeWeaponType {

		BRONZE_KNIFE(0.75, Graphic.create(219, 0, 100), 212, new int[] { 4, 4, 6 }),

		IRON_KNIFE(0.7, Graphic.create(220, 0, 100), 213, new int[] { 4, 4, 6 }),

		STEEL_KNIFE(0.65, Graphic.create(221, 0, 100), 214, new int[] { 4, 4, 6 }),

		MITHRIL_KNIFE(0.6, Graphic.create(223, 0, 100), 216, new int[] { 4, 4, 6 }),

		ADAMANT_KNIFE(0.5, Graphic.create(224, 0, 100), 217, new int[] { 4, 4, 6 }),

		RUNE_KNIFE(0.4, Graphic.create(225, 0, 100), 218, new int[] { 4, 4, 6 }),

		BLACK_KNIFE(0.6, Graphic.create(222, 0, 100), 215, new int[] { 4, 4, 6 }),

		BRONZE_DART(0.75, Graphic.create(1234, 0, 100), 226, new int[] { 3, 3, 5 }),

		IRON_DART(0.7, Graphic.create(1235, 0, 100), 227, new int[] { 3, 3, 5 }),

		BLACK_DART(0.6, Graphic.create(1238, 0, 100), 231, new int[] { 3, 3, 5 }),
		
		STEEL_DART(0.65, Graphic.create(1236, 0, 100), 228, new int[] { 3, 3, 5 }),

		MITHRIL_DART(0.6, Graphic.create(1237, 0, 100), 229, new int[] { 3, 3, 5 }),

		ADAMANT_DART(0.5, Graphic.create(1239, 0, 100), 230, new int[] { 3, 3, 5 }),

		RUNE_DART(0.4, Graphic.create(1240, 0, 100), 231, new int[] { 3, 3, 5 }),
		
		DRAGON_DART(0.4, Graphic.create(1123, 0, 100), 1122, new int[] { 3, 3, 5 }),

		BRONZE_THROWNAXE(0.75, Graphic.create(42, 0, 100), 36, new int[] { 4, 4, 6 }),

		IRON_THROWNAXE(0.7, Graphic.create(43, 0, 100), 35, new int[] { 4, 4, 6 }),

		STEEL_THROWNAXE(0.65, Graphic.create(44, 0, 100), 37, new int[] { 4, 4, 6 }),

		MITHRIL_THROWNAXE(0.6, Graphic.create(45, 0, 100), 38, new int[] { 4, 4, 6 }),

		ADAMANT_THROWNAXE(0.5, Graphic.create(46, 0, 100), 39, new int[] { 4, 4, 6 }),

		RUNE_THROWNAXE(0.4, Graphic.create(48, 0, 100), 41, new int[] { 4, 4, 6 }),
		
		DRAGON_THROWNAXE(0.4, Graphic.create(1317, 0, 100), 1317, new int[] { 4, 4, 6 }),

		BRONZE_JAVELIN(0.75, Graphic.create(206, 0, 100), 200, new int[] { 4, 4, 6 }),

		IRON_JAVELIN(0.7, Graphic.create(207, 0, 100), 201, new int[] { 4, 4, 6 }),

		STEEL_JAVELIN(0.65, Graphic.create(208, 0, 100), 202, new int[] { 4, 4, 6 }),

		MITHRIL_JAVELIN(0.6, Graphic.create(209, 0, 100), 203, new int[] { 4, 4, 6 }),

		ADAMANT_JAVELIN(0.5, Graphic.create(210, 0, 100), 204, new int[] { 4, 4, 6 }),

		RUNE_JAVELIN(0.4, Graphic.create(211, 0, 100), 205, new int[] { 4, 4, 6 }),
		
		DRAGON_JAVELIN(0.4, null, 1301, new int[] { 4, 4, 6 }),

		OBSIDIAN_RING(0.45, null, 442, new int[] { 7, 7, 9 }), ;

		/**
		 * The percentage chance for the arrow to disappear once fired.
		 */
		private double dropRate;

		/**
		 * The pullback graphic.
		 */
		private Graphic pullback;

		/**
		 * The projectile id.
		 */
		private int projectile;

		/**
		 * The distances required for each attack type.
		 */
		private int[] distances;

		/**
		 * 
		 * @param dropRate
		 * @param pullback
		 * @param projectile
		 * @param distances
		 */
		private RangeWeaponType(double dropRate, Graphic pullback, int projectile, int[] distances) {
			this.dropRate = dropRate;
			this.pullback = pullback;
			this.projectile = projectile;
			this.distances = distances;
		}

		/**
		 * @return the dropRate
		 */
		public double getDropRate() {
			return dropRate;
		}

		/**
		 * @return the pullback
		 */
		public Graphic getPullbackGraphic() {
			return pullback;
		}

		/**
		 * @return the projectile
		 */
		public int getProjectileId() {
			return projectile;
		}

		/**
		 * @return the distances
		 */
		public int[] getDistances() {
			return distances;
		}

		/**
		 * @return the distances
		 */
		public int getDistance(int index) {
			return distances[index];
		}
	}

}