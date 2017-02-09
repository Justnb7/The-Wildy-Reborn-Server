package com.model.game.character.player.content.bounty_hunter;

/**
 * Holds the constant values for the {@link BountyHunter} minigame
 * 
 * @author Mobster
 *
 */
public class BountyHunterConstants {

	/**
	 * The key to the bounty hunter target attribute
	 */
	public static final String BOUNTY_TARGET = "BOUNTY_TARGET";
	
	/**
	 * The maximum level difference that a target can be to be considered a target
	 */
	public static final int MAXIMUM_LEVEL_DIFFERENCE = 10;
	
	/**
	 * The delay before the next target is assigned
	 */
	public static final int NEXT_TARGET_DELAY = 60;
	
	/**
	 * The delay before you can get a target upon logging in
	 */
	public static final long LOGIN_DELAY_WAIT = 10_000;
	
	/**
	 * The delay before you can get another target after killing somebody
	 */
	public static final long LAST_BOUNTY_KILL_DELAY = 15_000;
	
	/**
	 * The id for the bounty hunter interface
	 */
	public static final int BOUNTY_INTERFACE_ID = 23300;
	
	/**
	 * The id of the string for the wealth
	 */
	public static final int WEALTH_STRING_ID = 23305;
	
	/**
	 * The id of the string for the bounty hunter target name
	 */
	public static final int TARGET_NAME_STRING_ID = 23307;
	
	/**
	 * The id of the string for the level and combat
	 */
	public static final int LEVEL_COMBAT_STRING_ID = 23308;
	
	/**
	 * The id for the rogue current string
	 */
	public static final int ROGUE_CURRENT_STRING_ID = 23310;
	
	/**
	 * The id for the rogue record string
	 */
	public static final int ROGUE_RECORD_STRING_ID = 23311;
	
	/**
	 * The id for the hunter current string
	 */
	public static final int HUNTER_CURRENT_STRING_ID = 23312;
	
	/**
	 * The id for the hunter record string
	 */
	public static final int HUNTER_RECORD_STRING_ID = 23313;

	/**
	 * The amount to be considered very high wealth
	 */
	protected static final int V_HIGH_WEALTH = 10_000_000;

	/**
	 * The amount to be considered high wealth
	 */
	protected static final int HIGH_WEALTH = 1_000_000;

	/**
	 * The amount to be considered medium wealth
	 */
	protected static final int MEDIUM_WEALTH = 250_000;

	/**
	 * The amount to be considered low wealth
	 */
	protected static final int LOW_WEALTH = 50_000;

	/**
	 * The config id for very high wealth
	 */
	protected static final int V_HIGH_WEALTH_CONFIG = 881;

	/**
	 * The config id for high wealth
	 */
	protected static final int HIGH_WEALTH_CONFIG = 880;

	/**
	 * The config id for medium wealth
	 */
	protected static final int MEDIUM_WEALTH_CONFIG = 879;

	/**
	 * The config id for low wealth
	 */
	protected static final int LOW_WEALTH_CONFIG = 878;

	/**
	 * The configf id for very low wealth
	 */
	protected static final int V_LOW_WEALTH_CONFIG = 877;
	
	/**
	 * The default config for wealth
	 */
	protected static final int DEFAULT_CONFIG = 876;

	/**
	 * The key for the hunter current value
	 */
	public static final String HUNTER_CURRENT = "HUNTER_CURRENT";
	
	/**
	 * The key for the hunter record value
	 */
	public static final String HUNTER_RECORD = "HUNTER_RECORD";
	
	/**
	 * The key for the rogue current value
	 */
	public static final String ROGUE_CURRENT = "ROGUE_CURRENT";
	
	/**
	 * The key for the rogue record value
	 */
	public static final String ROGUE_RECORD = "ROGUE_RECORD";

}