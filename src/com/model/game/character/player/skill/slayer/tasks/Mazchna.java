package com.model.game.character.player.skill.slayer.tasks;

public enum Mazchna implements Task {
	
	 Banshees(414, 15),
	 BAT(2827, 1),
	 BIRDS(2692, 1),
	 BEAR(3423, 1),
	 CAVE_BUGS(481, 7),
	 CAVE_CRAWLERS(406, 10),
	 CAVE_SLIMES(480, 17),
	 COCKATRICE(419, 25),
	 CRAWLING_HANDS(448, 5),
	 GHOSTS(85, 1),
	 HILL_GIANT(2098, 1),
	 ICE_WARRIOR(2841, 1),
	 KALPHITES(955, 1),
	 PYREFIEND(433, 30),
	 ROCK_SLUG(421, 20);
	
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
	
	Mazchna(int id, int slayerRequirement) {
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