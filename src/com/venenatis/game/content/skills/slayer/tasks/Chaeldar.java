package com.venenatis.game.content.skills.slayer.tasks;

import com.venenatis.game.model.entity.player.Player;

public enum Chaeldar implements Task {
	
	 ABYSSAL_DEMON(415, 85, 12, 0),
	 ABERRANT_SPECTRE(3, 1, 8, 0),
	 BLACK_DEMON(1432, 1, 10, 0),
	 HELLHOUND(135, 1, 9, 0),
	 CAVE_KRAKEN(492, 87, 12, 0),
	 STEEL_DRAGON(274, 1, 9, 0),
	 IRON_DRAGON(272, 1, 12, 0),

	 BANSHEE(414, 15, 5, 0),
	 BASILISK(417, 40, 7, 0),
	 BLUE_DRAGON(268, 1, 8, 0),
	 BLOODVELD(484, 50, 8, 0),
	 BRONZE_DRAGON(270, 1, 11, 0),
	 CAVE_HORROR(3209, 58,10,0), 
	 CAVE_CRAWLERS(406, 10, 5, 0),
	 CAVE_SLIMES(480, 17, 6, 0),
	 COCKATRICE(419, 25, 6, 0),
	 DAGANNOTH(140,1,11,0),
	 FIRE_GIANT(2075, 1, 12, 0),
	 GARGOYLE(412, 75, 11, 0),

	 //kalphite

	 
	 KURASK(410, 70, 12, 0),
	 PYREFIEND(435, 30, 6, 0),
	 NECHRYAEL(11, 80, 12, 0),
	 TUROTH(432, 55, 10, 0),
	 DUST_DEVIL(423, 65, 9, 0);


	
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
	
	Chaeldar(int id, int slayerRequirement, int weight, int percentage) {
		this.id = id;
		this.slayerRequirement = slayerRequirement;
		this.weight = weight;
		this.percentage = percentage;
	}
	
	public int getId() {
		return id;
	}
	
	public int getSlayerReq() {
		return slayerRequirement;
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
	
	public int getPercentage(){
		return percentage;
	}
	public static int getStreak(Player player) {
		
		if(player.getSlayerStreak() % 1000 == 0) {
			return 50;
		} else 	if(player.getSlayerStreak() % 250 == 0) {
			return 150;
		} else if(player.getSlayerStreak() % 100 == 0) {
			return 250;
		} else if(player.getSlayerStreak() % 50 == 0) {
			return 350;
		}else 	if(player.getSlayerStreak() % 10 == 0) {
				return 500;		
		}
		return 0;
	}
	static {
		for (Chaeldar t : Chaeldar.values()) {
			total += t.getWeight();
			}
		}
}