package com.venenatis.game.content.skills.runecrafting;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.model.entity.npc.pet.Pet;

/**
 * This Enum holds all runecrafting data.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public enum Altar {
	
	AIR(14897, 14841, 1438, 5527, 1, 556, 5, 11, false, 100_000, Pet.RIFT_GUARDIAN_AIR),
	MIND(14898, 14842, 1448, 5529, 2, 558, 5.5, 14, false, 100_000, Pet.RIFT_GUARDIAN_MIND),
	WATER(14899, 14843, 1444, 5531, 5, 555, 6, 19, false, 100_000, Pet.RIFT_GUARDIAN_WATER),
	EARTH(14900, 14844, 1440, 5535, 9, 557, 6.5, 26, false, 100_000, Pet.RIFT_GUARDIAN_EARTH),
	FIRE(14901, 14845, 1442, 5537, 14, 554, 7, 35, false, 100_000, Pet.RIFT_GUARDIAN_FIRE),
	BODY(14902, 14846, 1446, 5533, 20, 559, 7.5, 46, false, 100_000, Pet.RIFT_GUARDIAN_BODY),
	COSMIC(14903, 14847, 1454, 5539, 27, 564, 8, 59, true, 100_000, Pet.RIFT_GUARDIAN_COSMIC),
	CHAOS(14906, 14893, 1452, 5543, 35, 562, 8.5, 74, true, 100_000, Pet.RIFT_GUARDIAN_CHAOS),
	ASTRAL(14911, -1, -1, 9106, 40, 9075, 8.7, 82, true, 100_000, Pet.RIFT_GUARDIAN_ASTRAL),
	NATURE(14905, 14892, 1462, 5541, 44, 561, 9, 91, true, 100_000, Pet.RIFT_GUARDIAN_NATURE),
	LAW(14904, 14848, 1458, 5545, 54, 563, 9.5, 95, true, 100_000, Pet.RIFT_GUARDIAN_LAW),
	DEATH(14907, 14894, 1456, 5547, 65, 560, 10, 99, true, 100_000, Pet.RIFT_GUARDIAN_DEATH),
	BLOOD(27978, -1, 1450, 5549, 77, 565, 10.5, 1, true, 50_000, Pet.RIFT_GUARDIAN_BLOOD),
	SOUL(27980, -1, 1450, 5549, 77, 565, 10.5, 1, true, 50_000, Pet.RIFT_GUARDIAN_SOUL);
	
	/**
	 * The object id used for this altar
	 */
	private int altar;
	
	/**
	 * The object id of the exit object
	 */
	private int exitObjectId;
	
	/**
	 * The talisman item id
	 */
	private int talismanId;
	
	/**
	 * The tiarra item id
	 */
	private int tiarraId;
	
	/**
	 * The level required
	 */
	private int levelRequired;
	
	/**
	 * The experience received per essence
	 */
	private double experience;
	
	/**
	 * The rune
	 */
	private int rune;
	
	private int multiplier;
	
	/**
	 * Can we only use pure essence?
	 */
	private boolean pureEssenceOnly;
	
	/**
	 * The drop rate example 1/1000
	 */
	private int baseChance;
	
	/**
	 * The actual pet
	 */
	private Pet pet;
	
	/**
	 * A map of all altars
	 */
	private static Map<Integer, Altar> altars = new HashMap<Integer, Altar>();
	
	static{
		for(Altar altar : Altar.values()){
			altars.put(altar.getId(), altar);
		}
	}

	/**
	 * The altar constructor
	 * 
	 * @param altar
	 *            The altar id
	 * @param exitObject
	 *            The exit object id
	 * @param talisman
	 *            The talisman required
	 * @param tiarraId
	 *            Or the tiarra required
	 * @param level
	 *            The level requirement
	 * @param rune
	 *            The rune we're crafting
	 * @param experience
	 *            The experience we gain
	 * @param multiplier
	 *            The rune multiplier
	 * @param requirePureEss
	 *            Is pure essence required in order to make this rune
	 * @param rate
	 *            The drop rate of the pet
	 * @param petDrop
	 *            The pet id
	 */
	private Altar(int altar, int exitObject, int talisman, int tiarraId, int level, int rune, double experience, int multiplier, boolean requirePureEss, int rate, Pet petDrop) {
		this.altar = altar;
		this.exitObjectId = exitObject;
		this.talismanId = talisman;
		this.tiarraId = tiarraId;
		this.levelRequired = level;
		this.experience = experience;
		this.rune = rune;
		this.multiplier = multiplier;
		this.pureEssenceOnly = requirePureEss;
		this.baseChance = rate;
		this.pet = petDrop;
	}
	
	/**
	 * Grab the altars by object id
	 * 
	 * @param id
	 *            The altar
	 * @return
	 */
	public static boolean alterIds(int id) {
		for(Altar altar : altars.values()) {
			if(altar.getAlterId() == id) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Grab a single altar by id
	 * 
	 * @param id
	 *            The object to check
	 * @return
	 */
	public static Altar getAltar(int id) {
		for(Altar a: altars.values()) {
			if(a.getAlterId() == id) {
				return a;
			}
		}
		return null;
	}
	
	/**
	 * Gets the altar by id
	 * 
	 * @return the alter Id
	 */
	public int getAlterId() {
		return altar;
	}

	/**
	 * Gets the exit object
	 * 
	 * @return the object id.
	 */
	public int getExitObject() {
		return exitObjectId;
	}

	/**
	 * Gets the talisman id.
	 *
	 * @return The id.
	 */
	public int getId() {
		return talismanId;
	}

	/**
	 * Gets the tiara id
	 * 
	 * @return The id.
	 */
	public int getTiaraId() {
		return tiarraId;
	}

	/**
	 * Gets the required level.
	 *
	 * @return The required level.
	 */
	public int getLevelRequired() {
		return levelRequired;
	}

	/**
	 * The rune we're crafting
	 * 
	 * @return The rune Id.
	 */
	public int getRune() {
		return rune;
	}

	/**
	 * The experience we receive per rune
	 * 
	 * @return the Experience.
	 */
	public double getExperience() {
		return experience;
	}
	
	public int getDoubleRunesLevel() {
		return multiplier;
	}

	/**
	 * Can we only use pure essence here?
	 * 
	 * @return True or false
	 */
	public boolean isPureEssenceOnly() {
		return pureEssenceOnly;
	}

	/**
	 * Returns the drop rate example 1/1000
	 * 
	 * @return The drop rate.
	 */
	public int getBaseChance() {
		return baseChance;
	}

	/**
	 * The pet we receive
	 * 
	 * @return the pet Id.
	 */
	public Pet petDrop() {
		return pet;
	}

}