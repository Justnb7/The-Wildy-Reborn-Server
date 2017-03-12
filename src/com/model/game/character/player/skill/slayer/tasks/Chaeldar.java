package com.model.game.character.player.skill.slayer.tasks;

public enum Chaeldar implements Task {
	
	 ABYSSAL_DEMON(415, 85),
	 ABERRANT_SPECTRE(3, 1),
	 BLACK_DEMON(1432, 1),
	 HELLHOUND(135, 1),
	 CAVE_KRAKEN(492, 87),
	 STEEL_DRAGON(274, 1),
	 IRON_DRAGON(272, 1),
	 ANKOU(2514, 1),
	 Banshees(414, 15),
	 BASILISK(417, 40),
	 BLUE_DRAGON(268, 1),
	 BLOODVELD(484, 50),
	 BRONZE_DRAGON(270, 1),
	 CAVE_BUGS(481, 7),
	 CAVE_CRAWLERS(406, 10),
	 CAVE_SLIMES(480, 17),
	 COCKATRICE(419, 25),
	 CRAWLING_HANDS(448, 5),
	 FIRE_GIANT(2075, 1),
	 GARGOYLE(412, 75),
	 GREEN_DRAGON(264, 1),
	 HILL_GIANT(2098, 1),
	 ICE_WARRIOR(2841, 1),
	 INFERNAL_MAGE(443, 45),
	 JELLY(437, 52),
	 KURASK(410, 70),
	 PYREFIEND(435, 30),
	 NECHRYAEL(11, 80),
	 TUROTH(432, 55),
	 DUST_DEVIL(423, 65),
	 LESSER_DEMON(2005, 1),
	 BLACK_DRAGON(252, 1);
	
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
	
	Chaeldar(int id, int slayerRequirement) {
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