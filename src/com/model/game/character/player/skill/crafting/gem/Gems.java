package com.model.game.character.player.skill.crafting.gem;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum Gems {

	UNCUT_DIAMOND(1617, 1601, 43, 890, 107),
	UNCUT_RUBY(1619, 1603, 34, 887, 85),
	UNCUT_EMERALD(1621, 1605, 27, 889, 67),
	UNCUT_SAPPHIRE(1623, 1607, 1, 888, 50),
	UNCUT_OPAL(1625, 1609, 1, 891, 15),
	UNCUT_JADE(1627, 1611, 13, 892, 20),
	UNCUT_RED_TOPAZ(1629, 1613, 16, 892, 25),
	UNCUT_DRAGONSTONE(1631, 1615, 55, 890, 137),
	UNCUT_ONYX(6571, 6573, 67, 2717, 167);
	
	/**
	 * The uncut id
	 */
	private final int uncutId;
	/**
	 * The cut id
	 */
	private final int cutId; 
	/**
	 * The level required
	 */
	private final int levelRequired;
	/**
	 * The animation
	 */
	private final int animation;
	/**
	 * The experience
	 */
	private final double experience;

	Gems(int uncut, int cut, int level, int anim, double exp) {
		uncutId = uncut;
		cutId = cut;
		levelRequired = level;
		animation = anim;
		experience = exp;
	}

	private static final Set<Gems> GEMS = Collections.unmodifiableSet(EnumSet.allOf(Gems.class));

	public int getUncutId() {
		return uncutId;
	}

	public int getCutId() {
		return cutId;
	}

	public int getLevel() {
		return levelRequired;
	}

	public int getAnimation() {
		return animation;
	}

	public double getExp() {
		return experience;
	}
	
	public static Gems forId(int id) {
		return GEMS.stream().filter(gems -> gems.uncutId == id).findFirst().orElse(null);
	}
}