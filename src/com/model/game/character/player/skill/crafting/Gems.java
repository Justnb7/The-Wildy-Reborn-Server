package com.model.game.character.player.skill.crafting;

import java.util.HashMap;
import java.util.Map;

import com.model.game.character.Animation;

/**
 * Our gem data for the crafting skill
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public enum Gems {
	
	OPAL(1625, 1609, 15.0, 1, Animation.create(890)),
	JADE(1627, 1611, 20, 13, Animation.create(890)),
	RED_TOPAZ(1629, 1613, 25, 16, Animation.create(892)),
	SAPPHIRE(1623, 1607, 50, 20, Animation.create(888)),
	EMERALD(1621, 1605, 67, 27, Animation.create(889)),
	RUBY(1619, 1603, 85, 34, Animation.create(887)),
	DIAMOND(1617, 1601, 107.5, 43, Animation.create(886)),
	DRAGONSTONE(1631, 1615, 137.5, 55, Animation.create(885)),
	ONYX(6571, 6573, 168, 67, Animation.create(2717));
	
	/**
	 * The uncutId
	 */
	private int uncut;
	
	/**
	 * The reward
	 */
	private int cut;
	
	/**
	 * Amount of experience
	 */
	private double experience;
	
	/**
	 * The level required to cut
	 */
	private int levelRequired;
	
	/**
	 * The animation while cutting
	 */
	private Animation cutAnimation;
	
	/**
	 * The gem constructor
	 * @param uncut
	 *        The uncut item
	 * @param cut
	 *        The cut item a.k.a the reward
	 * @param experience
	 *        Amount of expierence we receive for cutting gems
	 * @param levelRequired
	 *        Level required in order to cut the gem
	 * @param cutAnimation
	 *        The animation while cutting gems
	 */
	private Gems(int uncut, int cut, double experience, int levelRequired, Animation cutAnimation) {
		this.uncut = uncut;
		this.cut = cut;
		this.experience = experience;
		this.levelRequired = levelRequired;
		this.cutAnimation = cutAnimation;
	}
	
	/**
	 * All our gems stored in a map
	 */
	private static Map<Integer, Gems> gems = new HashMap<Integer, Gems>();

	/**
	 * Get them Gem by ID
	 * @param item
	 *        The gem
	 */
	public static Gems forId(int item) {
		return gems.get(item);
	}

	static {
		for (Gems gem : Gems.values()) {
			gems.put(gem.uncut, gem);
		}
	}
	
	/**
	 * Thhe required level to cut the gem
	 * @return levelRequired
	 */
	public int getRequiredLevel() {
		return levelRequired;
	}
	
	/**
	 * The reward we receive after cutting our uncut gem
	 * @return cut
	 */
	public int cutReward() {
		return cut;
	}
	
	/**
	 * The uncut gem
	 * @return uncut
	 */
	public int getUncutVersion() {
		return uncut;
	}
	
	/**
	 * Amount of expierence we receive
	 * @return experience
	 */
	public double getExperience() {
		return experience;
	}
	
	/**
	 * The cutting animation
	 * @return cutAnimation
	 */
	public Animation getAnimation() {
		return cutAnimation;
	}

}