package com.venenatis.game.content.skills.runecrafting;

import java.util.HashMap;
import java.util.Map;

/**
 * The runecrafting data
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public enum Talisman {

	AIR_TALISMAN(1438, 5527, 1, 14897, 556, 5, false),
	MIND_TALISMAN(1448, 5529, 2, 14898, 558, 5.5, false),
	WATER_TALISMAN(1444, 5531, 5, 14899, 555, 6, false),
	EARTH_TALISMAN(1440, 5535, 9, 14900, 557, 6.5, false),
	FIRE_TALISMAN(1442, 5537, 14, 14901, 554, 7, false),
	BODY_TALISMAN(1446, 5533, 20, 14902, 559, 7.5, false),
	COSMIC_TALISMAN(1454, 5539, 27, 14903, 564, 8, true),
	CHAOS_TALISMAN(1452, 5543, 35, 14904, 562, 8.5, true),
	NATURE_TALISMAN(1462, 5541, 44, 14905, 561, 9, true),
	LAW_TALISMAN(1458, 5545, 128, 2459, 14906, 9.5, true),
	DEATH_TALISMAN(1456, 5547, 0, -1, 14907, 10, true);

	/**
	 * The id of the Talisman
	 */
	private final int id;
	
	/**
	 * The id of the Tiara
	 */
	private final int tiaraid;
	
	/**
	 * The level required to use the talisman
	 */
	private final int level;
	
	/**
	 * The id of the runecrafting altar
	 */
	private final int altarId;
	
	/**
	 * The rune reward
	 */
	private final int runeReward;
	
	/**
	 * The amount of expierence we receive
	 */
	private final double experience;
	
	/**
	 * Can we use pure essence only
	 */
	private boolean pureEssOnly;
	
	/**
	 * A map of all talismans
	 */
	private static Map<Short, Talisman> talismans = new HashMap<Short, Talisman>();
	
	/**
	 * Gets a talisman by an item id.
	 *
	 * @param item The item id.
	 * @return The talisman.
	 */
	public static Talisman forId(int item) {
		return talismans.get(item);
	}
	
	static{
		for(Talisman talisman : Talisman.values()){
			talismans.put((short) talisman.id, talisman);
		}
	}

	/**
	 * The Talisman creation
	 * @param id
	 * @param tiaraid
	 * @param level
	 * @param alterId
	 * @param rewardId
	 * @param rewardExp
	 * @param requirePureEss
	 */
	private Talisman(int id, int tiaraid, int level, int alterId, int rewardId, double rewardExp, boolean requirePureEss){
		this.id = id;
		this.tiaraid = tiaraid;
		this.level = level;
		this.altarId = alterId;
		this.runeReward = rewardId;
		this.experience = rewardExp;
		this.pureEssOnly = requirePureEss;
	}
	
	/**
	 * Gets the altar
	 * @param id
	 *        The altar
	 */
	public static boolean altar(int id){
		for(Talisman talisman: talismans.values()){
			if(talisman.getAlterId() == id){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the altar by talisman
	 * @param id
	 *        The talismanId
	 */
	public static Talisman getTalismanByAltar(int id){
		for(Talisman talisman: talismans.values()){
			if(talisman.getAlterId() == id){
				return talisman;
			}
		}
		return null;
	}

	/**
	 * Gets the talisman id
	 *
	 * @return id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Gets the tiaria id
	 * @return tiaraid
	 */
	public int getTiaraId(){
		return tiaraid;
	}

	/**
	 * Gets the required level.
	 *
	 * @return level
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Gets the altar
	 * @return altarId
	 */
	public int getAlterId(){
		return altarId;
	}
	
	/**
	 * Which rune do we receive
	 * @return runeReward
	 */
	public int getRuneReward(){
		return runeReward;
	}
	
	/**
	 * The amount of expierence we receive for crafting runes
	 * @return
	 */
	public double getExperience(){
		return experience;
	}
	
	/**
	 * Certain altars require pure essence only
	 * @return
	 */
	public boolean pureEssOnly(){
		return pureEssOnly;
	}
}