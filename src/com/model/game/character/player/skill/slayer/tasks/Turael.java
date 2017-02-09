package com.model.game.character.player.skill.slayer.tasks;

public enum Turael implements Task {
	 Banshees(414, 15),
	 BAT(2827, 1),
	 BIRDS(2692, 1),
	 BEAR(3423, 1),
	 CAVE_BUGS(481, 7),
	 CAVE_CRAWLERS(406, 10),
	 CAVE_SLIMES(480, 17),
	 COWS(2805, 1),
	 CRAWLING_HANDS(448, 5),
	 DWARVES(291, 1),
	 GHOSTS(85, 1),
	 GOBLINS(2245, 1),
	 KALPHITES(955, 1);

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
	
	Turael(int id, int slayerRequirement) {
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