package com.venenatis.game.content.skills.cooking;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Stores all our cooking data
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public enum Cookables {
	
	/**
	 * Meat
	 */
	RAW_MEAT(2132, 1, 200, 30, 2146, 2142, false),
	RAW_CHICKEN(2138, 1, 200, 30, 2144, 4291, false),
	RAW_RABBIT(3226, 1, 200, 30, 7222, 3228, false),
	RAW_CRAB_MEAT(7518, 21, 200, 100, 7520, 7521, false),
	
	/**
	 * Fish
	 */
	RAW_SHRIMP(317, 1, 34, 30, 7954, 315, false),
	RAW_KARAMBWANJI(3150, 1, 35, 10, 3148, 3151, false),
	RAW_SARDINE(327, 1, 38, 40, 323, 325, false),
	RAW_ANCHOVIES(321, 1, 34, 30, 323, 319, false),
	POISON_KARAMBWAN(3142, 1, 20, 80, 3148, 3151, false),
	RAW_HERRING(345, 5, 37, 50, 357, 347, false),
	RAW_MACKEREL(353, 10, 45, 60, 357, 355, false),
	RAW_TROUT(335, 15, 50, 70, 343, 333, false),
	RAW_COD(341, 18, 39, 75, 343, 339, false),
	RAW_PIKE(349, 20, 52, 80, 349, 351, false),
	RAW_SALMON(331, 25, 58, 90, 343, 329, false),
	RAW_SLIMY_EEL(3379, 28, 58, 95, 3383, 3381, false),
	RAW_TUNA(359, 30, 63, 100, 367, 361, false),
	RAW_KARAMBWAN(3142, 5, 94, 225, 3148, 3144, false),
	RAW_CAVE_EEL(5001, 38, 40, 115, 5006, 5003, false),
	RAW_LOBSTER(377, 40, 66, 120, 381, 379, false),
	RAW_BASS(363, 43, 80, 130, 367, 365, false),
	RAW_SWORDFISH(371, 45, 86, 140, 375, 373, false),
	RAW_LAVA_EEL(2148, 53, 53, 30, -1, 2149, false),
	RAW_MONKFISH(7944, 62, 90, 150, 7948, 7946, false),
	RAW_SHARK(383, 80, 100, 210, 387, 385, false),
	RAW_SEA_TURTLE(395, 82, 200, 212, 399, 397, false),
	RAW_ANGLERFISH(13439, 84, 200, 230, 13443, 13441, false),
	DARK_CRAB(11934, 90, 200, 215, 11938, 11936, false),
	RAW_MANTA_RAY(389, 91, 200, 216, 393, 391, false),
	
	/**
	 * Pies
	 */
	RAW_REDBERRY_PIE(2321, 10, 200, 78, 2329, 2325, true),
	RAW_MEAT_PIE(2319, 20, 200, 104, 2329, 2327, true),
	RAW_MUD_PIE(7168, 29, 200, 128, 2329, 7170, true),
	RAW_APPLE_PIE(2317, 30, 200, 130, 2329, 2323, true),
	RAW_GARDEN_PIE(7176, 34, 200, 138, 2329, 7178, true),
	RAW_FISH_PIE(7186, 47, 200, 164, 2329, 7188, true),
	RAW_ADMIRAL_PIE(7196, 70, 200, 210, 2329, 7198, true),
	RAW_WILD_PIE(7206, 85, 200, 240, 2329, 7208, true),
	RAW_SUMMER_PIE(7216, 95, 200, 260, 2329, 7218, true),
	
	/**
	 * Pizza
	 */
	PLAIN_PIZZA(2287, 35, 200, 143, 2305, 2289, true),
	
	/**
	 * Cake
	 */
	RAW_FISHCAKE(7529, 31, 200, 100, 7531, 7530, true),
	CAKE(1889, 40, 200, 180, 1903, 1891, true),

	/**
	 * Potato
	 */
	RAW_POTATO(1942, 7, 200, 15, 6699, 6701, false);
	
	/**
	 * The uncooked item
	 */
	private final int raw;
	
	/**
	 * The level required to cook the cookable
	 */
	private final int lvl;
	
	/**
	 * The level the cookable stops burning at
	 */
	private final int burningLvl;
	
	/**
	 * The experience you receive per succesful cook.
	 */
	private final int xp;
	
	/**
	 * The id of the burnt cookable.
	 */
	private final int burnt;
	
	/**
	 * The cooked cookable item id
	 */
	private final int cooked;
	
	/**
	 * Can only be used on fire
	 */
	private final boolean stoveOrRangeOnly;

	private Cookables(int raw, int lvl, int burningLvl, int exp,
			int burnt, int cooked, boolean stoveOrRangeOnly) {
		this.raw = raw;
		this.lvl = lvl;
		this.burningLvl = burningLvl;
		this.xp = exp;
		this.burnt = burnt;
		this.cooked = cooked;
		this.stoveOrRangeOnly = stoveOrRangeOnly;
	}

	/**
	 * An unmodifiable enum set
	 */
    private static final Set<Cookables> cook = Collections.unmodifiableSet(EnumSet.allOf(Cookables.class));

    public int getRawItem() {
		return raw;
	}

	public int getLvl() {
		return lvl;
	}

	public int getBurntId() {
		return burnt;
	}

	public int getProduct() {
		return cooked;
	}

	public int getXp() {
		return xp;
	}

	public int getBurningLvl() {
		return burningLvl;
	}

	public boolean isStoveOrRangeOnly() {
		return stoveOrRangeOnly;
	}

	/**
	 * Get the cookable by id
	 * @param id
	 * @return the uncooked cookable id
	 */
	public static Cookables forId(int id) {
		return cook.stream().filter(cooking -> cooking.raw == id).findAny().orElse(null);
	}

	public static boolean isCookable(int id) {
		return cook.stream().anyMatch(cooking -> cooking.raw == id);
	}
}
