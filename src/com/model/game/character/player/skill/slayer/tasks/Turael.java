package com.model.game.character.player.skill.slayer.tasks;

public enum Turael implements Task {
	
	 ROCK_CRAB(100,1,10,0),
	 BANSHEE(414, 15,7,0),
	 BAT(2834, 1,8,0),
	 BIRDS(2692, 1,10,0),
	 BEAR(3423, 1,9,0),
	 CAVE_BUGS(481, 7,9,0),
	 CAVE_CRAWLERS(406, 10,15,0),
	 CAVE_SLIMES(480, 17,6,0),
	 COWS(2805, 1,8,0);
	
	
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
	
	
	Turael(int id, int slayerRequirement, int weight, int percentage) {
		this.id = id;
		this.slayerRequirement = slayerRequirement;
		this.weight = weight;
		this.percentage = percentage;
	}
	private int weight;
	
	private int percentage;
	
	public void setPercentage(int percentage){
		this.percentage = percentage;
	}
	
	public int getWeight(){
		return weight;
	}
	
	public static int getTotal() {
		return total;
	}
	
	public static int total;
	
	public int getId() {
		return id;
	}
	
	public int getPercentage(){
		return percentage;
	}
	
	public int getSlayerReq() {
		return slayerRequirement;
	}
	
	static {
	for (Turael t : Turael.values()) {
		total += t.getWeight();
		}
	}


	public int getStreak() {
		return 0;
	}
}