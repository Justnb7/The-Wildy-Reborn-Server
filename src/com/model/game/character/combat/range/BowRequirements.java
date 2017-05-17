package com.model.game.character.combat.range;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the requirements for bows
 * 
 * @author Arithium
 * 
 */
public enum BowRequirements {
	SHORTBOW(841, 1), LONGBOW(839, 1),

	OAK_SHORTBOW(843, 10), OAK_LONGBOW(845, 10),

	WILLOW_SHORTBOW(849, 20), WILLOW_LONGBOW(847, 20),

	MAPLE_SHORTBOW(853, 30), MAPLE_LONGBOW(851, 30),

	YEW_SHORTBOW(857, 40), YEW_LONGBOW(855, 40),

	MAGIC_SHORTBOW(861, 50), MAGIC_LONGBOW(859, 50),
	
	KARIL_CROSSBOW(4734, 70);

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