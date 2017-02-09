package com.model.game.character.player.skill.slayer.tasks;

public enum Duradel implements Task {
	
	KING_BLACK_DRAGON(239, 1), 
	CHAOS_ELEMENTAL(2054, 1), 
	DAGANNOTH_SUPREME(2265, 1), 
	DAGGANOTH_REX(2267, 1), 
	DAGGANOTH_PRIME(2266, 1), 
	VENENATIS(6610, 1), 
	VETION(6611, 1), 
	KRIL_TSUTSAROTH(3129, 1), 
	KREE_ARRA(3162, 1), 
	COMMANDER_ZILYANA(2205, 1), 
	GENERAL_GRAARDOR(2215, 1), 
	SCORPIA(6615, 1), 
	CALLISTO(6609, 1);
	
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
	
	Duradel(int id, int slayerRequirement) {
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