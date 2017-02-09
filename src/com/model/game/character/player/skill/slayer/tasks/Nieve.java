package com.model.game.character.player.skill.slayer.tasks;

/**
 * The enum holds all Nieve's task data.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 */
public enum Nieve implements Task {
	NECHRYAEL(11, 80), 
	ABYSSAL_DEMON(415, 85), 
	BLACK_DRAGON(252, 1), 
	BLUE_DRAGON(265, 1), 
	DARK_BEAST(4005, 90), 
	CAVE_KRAKEN(493, 87), 
	KRAKEN(496, 87), 
	STEEL_DRAGON(274, 1), 
	IRON_DRAGON(272, 1), 
	BRONZE_DRAGON(270, 1), 
	BLACK_DEMON(2048, 1), 
	HELLHOUND(104, 1), 
	CAVE_HORROR(3209, 58), 
	BABY_BLUE_DRAGON(241, 1), 
	FIRE_GIANT(2075, 1), 
	GREATER_DEMON(2025, 1), 
	BASILISK(417, 40), 
	JELLY(437, 52), 
	TUROTH(432, 55), 
	INFERNAL_MAGE(443, 45), 
	BLOODVELD(484, 50), 
	DUST_DEVIL(423, 65),
	LESSER_DEMON(2005, 1);

	/**
	 * The slayer npc ID
	 */
	private final int id;
	/**
	 * The level requirement
	 */
	private final int slayerRequirement;

	/**
	 * The experience multiplier
	 */

	Nieve(int id, int slayerRequirement) {
		this.id = id;
		this.slayerRequirement = slayerRequirement;
	}

	public int getId() {
		return id;
	}

	public int getSlayerReq() {
		return slayerRequirement;
	}
}
