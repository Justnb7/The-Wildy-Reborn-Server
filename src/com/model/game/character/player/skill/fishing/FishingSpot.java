package com.model.game.character.player.skill.fishing;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum FishingSpot {
	
	
	SHRIMPS_AND_ANCHOVIES(1521, 317, 321, 1, 303, 621, false, true, 180, 1, 4, 8000),
	TROUT_AND_SALMON(1521, 335, 331, 20, 307, 622, true, true, 460, 2, 4, 7000),
	MACKREL(1520, 353, 335, 16, 305, 620, false, false, 360, 1, 4, 7000),
	LOBSTER(1519, 377, -1, 40, 301, 619, false, false, 1180, 1, 6, 5000),
	KARAMBWAN(635, 3142, -1, 65, 303, 620, false, false, 105, 1, 9, 4500),
	MANTA(1526, 389, -1, 81, 303, 620, false, false, 1235, 2, 8, 4000),
	TUNA_SWORDFISH(1519, 359, 371, 50, 311, 618,false, true, 760, 2, 5, 4500),
	SHARK(1520, 383, 383, 76, 311, 618, false, false, 1620, 2, 7, 3000);
	
	/**
	 * The fishable spot ID
	 */
	private final int spot;
	
	/**
	 * The ID of the fish
	 */
	private final int fishId;
	
	/**
	 * The ID of the second fish
	 */
	private final int secondFishId;
	
	/**
	 * The level required to use the fishing spot
	 */
	private final int levelRequired;
	
	/**
	 * The tool required to fish
	 */
	private final int toolRequired;
	
	/**
	 * The animation ID
	 */
	private final int animationId;
	/**
	 * The secondary ingredient check
	 */
	private final boolean baitRequired;
	
	/**
	 * Does the fishing spot have more than one type of fish?
	 */
	private final boolean secondFishAvailable;
	
	/**
	 * The experience received from fishing
	 */
	private final double experience;
	
	/**
	 * The option ID
	 */
	private final int option;
	
	/**
	 * The timer required to fish
	 */
	private final int timer;
	
	/**
	 * The chance of receiving a pet
	 */
	private final int petChance;

	FishingSpot(int spotId, int fishId, int secondFishId, int levelRequired, int toolRequired, int animationId, boolean baitRequired, boolean secondFishAvailable, double experience, int option, int timer, int petChance) {
		this.spot = spotId;
		this.fishId = fishId;
		this.secondFishId = secondFishId;
		this.levelRequired = levelRequired;
		this.animationId = animationId;
		this.toolRequired = toolRequired;
		this.baitRequired = baitRequired;
		this.secondFishAvailable = secondFishAvailable;
		this.experience = experience;
		this.option = option;
		this.timer = timer;
		this.petChance = petChance;
	}
	
	private static final Set<FishingSpot> FISH = Collections.unmodifiableSet(EnumSet.allOf(FishingSpot.class));

	public int getSpotId() {
		return spot;
	}

	public int getFishId() {
		return fishId;
	}

	public int getSecondFishId() {
		return secondFishId;
	}
	public int getLevelRequired() {
		return levelRequired;
	}

	public int getAnimationId() {
		return animationId;
	}

	public int getToolId() {
		return toolRequired;
	}

	public boolean isBaitRequired() {
		return baitRequired;
	}

	public boolean isSecondFishAvailable() {
		return secondFishAvailable;
	}

	public double getExperience() {
		return experience;
	}
	
	public int getOption() {
		return option;
	}
	
	public int getTimer() {
		return timer;
	}
	
	public int getPetChance() { 
		return petChance; 
	}
	
	/**
	 * 
	 * @param id 
	 * @param option
	 * @return the id and option 
	 */
	public static FishingSpot forId(int id, int option) {
		return FISH.stream().filter(fish -> fish.spot == id && fish.option == option).findFirst().orElse(null);
	}
	/**
	 * 
	 * @param spot
	 * @return true if the spot matches a defined spot id
	 */
	public static boolean fishingNPC(int spot) {
		return FISH.stream().anyMatch(fish -> fish.spot == spot);
	}
	

}