package com.venenatis.game.content.skills.slayer.tasks;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.updating.PlayerUpdating;
import com.venenatis.game.util.Utility;

/**
 * The enum holds all Nieve's task data.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 */
public enum Nieve implements Task {
	
	ABERRANT_SPECTRE(7, 60, 9, 0),
	BLACK_DEMON(1432, 1, 9, 0),
	ABYSSAL_DEMON(415, 85,9,0), 
	ANKOU(7257,70,5,0),
	//AVIANSIE
	BLACK_DRAGON(252, 1,6,0), 
	BLOODVELD(484, 50,9,0), 
	BLUE_DRAGON(268, 1,4,0), 
	CAVE_HORROR(3209, 58,5,0), 
	CAVE_KRAKEN(492, 87,6,0), 
	DAGANNOTH(140,1,8,0),
	DARK_BEAST(4005, 90,5,0), 
	DUST_DEVIL(423, 65,6,0),
	GREATER_DEMON(2026, 1,7,0), 
	HELLHOUND(135, 1,8,0), 
	STEEL_DRAGON(274, 1,5,0), 
	IRON_DRAGON(272, 1,5,0), 
	MITHRIL_DRAGON(2919, 1, 5, 0),
	RED_DRAGON(247, 1, 5, 0),
	WYVERN(465, 72, 5, 0),
	SMOKE_DEVIL(498, 93, 7, 0),
	TUROTH(432, 55,3,0), 
	NECHRYAEL(11, 80, 7, 0), 
	KRAKEN(494, 87, 6, 0), 
	FIRE_GIANT(2075, 1, 9, 0); 


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

	Nieve(int id, int slayerRequirement, int weight, int percentage) {
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
			PlayerUpdating.executeGlobalMessage("<shad=000000><col=FF5E00>News: " + Utility.formatName(player.getUsername()) + " has just completed " + player.getSlayerStreak() + "x Slayer tasks in a row!");
			return 600;
		} else 	if(player.getSlayerStreak() % 250 == 0) {
			PlayerUpdating.executeGlobalMessage("<shad=000000><col=FF5E00>News: " + Utility.formatName(player.getUsername()) + " has just completed " + player.getSlayerStreak() + "x Slayer tasks in a row!");
			return 420;
		} else if(player.getSlayerStreak() % 100 == 0) {
			return 300;
		} else if(player.getSlayerStreak() % 50 == 0) {
			return 180;
		}else 	if(player.getSlayerStreak() % 10 == 0) {
				return 60;		
		}
		return 0;
	}
	static {
		for (Nieve t : Nieve.values()) {
			total += t.getWeight();
			}
		}
}