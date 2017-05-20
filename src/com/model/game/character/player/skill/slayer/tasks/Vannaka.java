package com.model.game.character.player.skill.slayer.tasks;

import com.model.game.character.player.Player;

public enum Vannaka implements Task {
	
	
	ABERRANT_SPECTRE(7,60,5,0),
	ABYSSAL_DEMON(415, 85,8,0), 
	ANKOU(7257,70,7,0),
	//AVIANSIE
	BANSHEE(414, 1, 6, 0),
	BASILISK(418, 1, 8, 0),
	BLUE_DRAGON(265, 1, 7, 0),
	BLOODVELD(484, 50,8,0), 
	BRONZE_DRAGON(270, 1, 7, 0),

	COCKATRICE(419, 25, 8, 0), 
	CRAWLING_HANDS(448, 5, 6, 0),
	DAGANNOTH(140,1, 7, 0),
	DUST_DEVIL(423, 65, 8 ,0),
	//earth warrior
	//elves
	FIRE_GIANT(2075, 1,7,0), 
	GARGOYLE(412, 75, 0, 5),
	GHOSTS(85, 1, 7, 0),
	GREEN_DRAGON(260, 1, 6, 0),
	

	HELLHOUND(135, 1,7,0), 
	HILL_GIANT(2098, 1, 7, 0),
	ICE_GIANT(2085, 1, 7, 0),
	ICE_WARRIOR(2841, 1, 7, 0),
	JELLY(437, 52, 8, 0),
	INFERNAL_MAGE(447, 45, 0, 8),
	LESSER_DEMON(2005, 1, 7, 0),
	MOSS_GIANT(891, 1, 7, 0),
	NECHRYAEL(11, 80, 5, 0),
	OGRE(136, 1, 7, 0),
	OTHER_WORLDY_BEING(2843, 1, 8, 0),
	PYREFIEND(435, 30, 8, 0),
	TUROTH(432, 55, 8, 0);
	
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
	
	Vannaka(int id, int slayerRequirement, int weight, int percentage) {
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
			return 100;
		} else 	if(player.getSlayerStreak() % 250 == 0) {
			return 70;
		} else if(player.getSlayerStreak() % 100 == 0) {
			return 50;
		} else if(player.getSlayerStreak() % 50 == 0) {
			return 15;
		}else 	if(player.getSlayerStreak() % 10 == 0) {
				return 5;		
		}
		return 0;
	}
	static {
		for (Vannaka t : Vannaka.values()) {
			total += t.getWeight();
			}
		}
}