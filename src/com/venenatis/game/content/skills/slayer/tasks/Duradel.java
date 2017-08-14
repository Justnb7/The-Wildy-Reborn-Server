package com.venenatis.game.content.skills.slayer.tasks;

import com.venenatis.game.model.entity.player.Player;

public enum Duradel implements Task {
	
	ABERRANT_SPECTRE(7, 60, 7, 0),
	ABYSSAL_DEMON(415, 85, 12, 0), 
	ANKOU(7257, 70, 5, 0),
	//AVIANSIE
	BLACK_DEMON(1432, 1, 8, 0),
	BLACK_DRAGON(252, 1, 9, 0), 
	BLOODVELD(484, 50, 8, 0), 
	BLUE_DRAGON(268, 1, 4, 0), 
	CAVE_HORROR(3209, 58, 4, 0), 
	CAVE_KRAKEN(492, 87, 9, 0), 
	DAGANNOTH(970, 1, 9, 0),
	DARK_BEAST(4005, 90, 11, 0), 
	DUST_DEVIL(423, 65, 5, 0),
	//elves
	FIRE_GIANT(2075, 1, 7, 0), 
	
	GREATER_DEMON(2026, 1, 9, 0), 
	HELLHOUND(135, 1, 10, 0), 
	STEEL_DRAGON(274, 1, 7, 0), 
	IRON_DRAGON(272, 1, 5, 0), 
	
	//kalphite
	//lizardman
	MITHRIL_DRAGON(2919, 1, 10, 0),
	NECHRYAEL(11, 80, 9, 0), 
	RED_DRAGON(247, 1, 8, 0),
	WYVERN(465, 72, 7, 0),
	SMOKE_DEVIL(498, 93, 9, 0),
	TUROTH(432, 55, 3, 0),
	//waterfiend
	//suqah
	//troll
	//tzhaar
	KRAKEN(494, 87, 6, 0);
	
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
	
	Duradel(int id, int slayerRequirement, int weight, int percentage) {
		this.id = id;
		this.slayerRequirement = slayerRequirement;
		this.weight = weight;
		this.percentage = percentage;
	}

	public int getId() {
		return id;
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
	
	public int getSlayerReq() {
		return slayerRequirement;
	}
	public int getPercentage(){
		return percentage;
	}
	public static int getStreak(Player player) {
		if(player.getSlayerStreak() % 1000 == 0) {
			return 750;
		} else 	if(player.getSlayerStreak() % 250 == 0) {
			return 525;
		} else if(player.getSlayerStreak() % 100 == 0) {
			return 375;
		} else if(player.getSlayerStreak() % 50 == 0) {
			return 225;
		}else 	if(player.getSlayerStreak() % 10 == 0) {
				return 75;		
		}
		return 0;
	}
	static {
		for (Duradel t : Duradel.values()) {
			total += t.getWeight();
			}
		}
}