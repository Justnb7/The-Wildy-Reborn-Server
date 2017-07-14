package com.venenatis.game.model.combat.range;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the requirements for bows
 * 
 * @author Arithium | Patrick van Elderen
 * 
 */
public enum BowRequirements {
	
	SHORTBOW(841, 1), LONGBOW(839, 1),
	BRONZE_CROSSBOW(9174, 1),
	OAK_SHORTBOW(843, 10), OAK_LONGBOW(845, 10),
	WILLOW_SHORTBOW(849, 20), WILLOW_LONGBOW(847, 20),
	IRON_CROSSBOW(9177, 26),
	MAPLE_SHORTBOW(853, 30), MAPLE_LONGBOW(851, 30),
	STEEL_CROSSBOW(9179, 31),
	YEW_SHORTBOW(857, 40), YEW_LONGBOW(855, 40),
	MITH_CROSSBOW(9181, 36),
	GREY_CHINCHOMPA(9976, 45),
	ADAMANT_CROSSBOW(9182, 46),
	MAGIC_SHORTBOW(861, 50), MAGIC_LONGBOW(859, 50),
	RED_CHINCHOMPA(9977, 55),
	DARK_BOW(11235, 60), GREEN_DARK_BOW(12765, 60), BLUE_DARK_BOW(12766, 60), YELLOW_DARK_BOW(12768, 60), WHITE_DARK_BOW(12768, 60),
	BLACK_CHINCHOMPA(11959, 65),
	RUNE_CROSSBOW(9185, 61),
	DRAGON_HUNTER_CROSSBOW(21012, 65),
	KARIL_CROSSBOW(4734, 70),
	NEW_CRYSTAL_BOW(4212, 70), CRYSTAL_BOW_FULL(4214, 70), CRYSTAL_BOW_I(11748, 70), CRYSTAL_BOW_FULL_I(11748, 70),
	ARMADYL_CROSSBOW(11785, 70),
	BLOWPIPE(12926, 75),
	HEAVY_BALLISTA(19481, 75),
	TWISTED_BOW(20997, 75);

	int bowId, levelRequired;

	private BowRequirements(int bowId, int levelRequired) {
		this.bowId = bowId;
		this.levelRequired = levelRequired;
	}

	private static Map<Integer, BowRequirements> bows = new HashMap<Integer, BowRequirements>();

	static {
		for (BowRequirements def : values()) {
			bows.put(def.getBowId(), def);
		}
	}

	public int getBowId() {
		return bowId;
	}

	public int getLevelRequired() {
		return levelRequired;
	}

	public static BowRequirements forId(int id) {
		return bows.get(id);
	}
}