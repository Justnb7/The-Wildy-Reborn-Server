package com.venenatis.game.constants;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;

public class Constants {
	
	/**
	 * Magic attack delay
	 */
	public static int MAGIC_ATTACK_DELAY = 5;
	
	/**
	 * Decides max incoming packets per cycle
	 */
	public static final int MAX_INCOMING_PACKETS_PER_CYCLE = 20;
	
	/**
	 * Bonus weekends
	 */
	public static final boolean PK_REWARDS = false;
	public static final boolean SLAYER_REWARDS = false;
	public static final boolean DOUBLE_EXPERIENCE = false;
	public static final boolean DOUBLE_DROPS = false;
	
	/**
	 * Prayer exp modifier
	 */
	public static final int PRAYER_EXP_MODIFIER = 10;
	
	/**
	 * Slayer exp modifier
	 */
	public static final int SLAYER_EXP_MODIFIER = 10;
	
	/**
	 * The exp modifier
	 */
	public static final int EXP_MODIFIER = 50;
	
	/**
	 * The experience multiplier given to non-combat stats
	 */
	public static final int SKILL_MODIFIER = 10;
	
	/**
	 * Strings that can not be used in a username
	 */
	public static final String BAD_USERNAMES[] = { "m o d", "a d m i n", "mod", "admin", "moderator", "administrator", "owner", "m0d", "adm1n", "0wner", "retard", "Nigga", "nigger", "n1gger", "n1gg3r", "nigg3r", "n1gga", "cock", "faggot", "fag", "anus", "arse", "fuck", "bastard", "bitch", "cunt", "chode", "damn", "dick", "faggit", "gay", "homo", "jizz", "lesbian", "negro", "pussy", "penis", "queef", "twat", "titty", "whore", "b1tch" };

	/**
	 * Strings that are classified as bad
	 */
	public static final String[] BAD_STRINGS = { "fag", "f4g", "faggot", "nigger", "fuck", "bitch", "whore", "slut", "gay", "lesbian", "scape", ".net", ".com", ".org", "vagina", "dick", "cock", "penis", "hoe", "soulsplit", "ikov", "retard", "cunt", };
	
	/**
	 * Players that can overwrite those username block.
	 */
	public static final String USERNAME_EXCEPTIONS[] = { 
		"patrick", "matthew"
	};
	
	/**
	 * The current version of the client. Used to notify player to update
	 * client.
	 */
	public static final int CLIENT_VERSION = 11;

	/**
	 * The name of the server
	 */
	public static final String SERVER_NAME = "Venenatis";
	
	/**
	 * The port in which this server is bound upon
	 */
	public static final int SERVER_PORT = 5555;
	
	/**
	 * the speed of world in ms
	 */
	public static final int WORLD_CYCLE_TIME = 600;
	
	/**
	 * The maximum amount of items the client can sent.
	 */
	public static final int ITEM_LIMIT = 25000;
	
	/**
	 * The maximum amount of items in a stack.
	 */
	public static final int MAX_ITEMS = Integer.MAX_VALUE;
	
	/**
	 * Maximum amount a player can hold
	 */
	public static final int MAXITEM_AMOUNT = Integer.MAX_VALUE;
	
	/**
	 * Starting location
	 */
	public static final Location START_PLAYER_LOCATION = new Location(3087, 3499, 0);
	
	/**
	 * Donator zone location
	 */
	public static final Location START_DZ_LOCATION = new Location(2721, 4912, 0);
	
	/**
	 * Respawn location
	 */
	public static final Location RESPAWN_PLAYER_LOCATION = new Location(3099, 3503, 0);
	
	/**
	 * The maximum time for a player skull with an extension in the length.
	 */
	public static final int EXTENDED_SKULL_TIMER = 2000;

	/**
	 * The amount of wealth the player must have in order to receive pk points
	 */
	public static final int PK_POINTS_WEALTH = 250_000;
	
	/**
	 * Determines the buffering size (10000)
	 */
	public static final int BUFFER_SIZE = 10000;

	/**
	 * The npc max cap.
	 */
	public static final int MAX_NPCS = 32000;
	
	/**
	 * Maximum players allowed in a single world.
	 */
	public static final int MAX_PLAYERS = 2000;
	
	/**
	 * Valid chacters that can be used in the friends chat
	 */
	public static final char VALID_CHARS[] = { '_', 'a', 'b', 'c', 'd', 'e',
			'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', '!', '@', '#', '$', '%', '^', '&', '*',
			'(', ')', '-', '+', '=', ':', ';', '.', '>', '<', ',', '"', '[',
			']', '|', '?', '/', '`' };

	/**
     * Messages chosen a random to be sent to a player that has killed another
     * player. {@code -victim-} is replaced with the player's name that was
     * killed, while {@code -killer-} is replaced with the killer's name.
     */
    public static final String[] DEATH_MESSAGES = { "You have just killed -victim-!", "You have completely slaughtered -victim-!",
            "I bet -victim- will think twice before messing with you again!",
            "Your killing style is impeccable, -victim- didn't stand a chance!" };

	/**
	 * The directory for the engine scripts.
	 */
	public static final String SCRIPTS_DIRECTORY = "./data/scripts/";
	
	/**
	 * Location of the data
	 */
	public static final String DATA_DIR = "./data/";

	public static final int TOTAL_MOBS = 7750+1000;//+1000 for custom npcs

	/**
	 * The rate in milliseconds in which the game thread processes logic.
	 */
	public static final int CYLCE_RATE = 600;

	/**
	 * Returns the amount of hitpoints extra will be health eating anglerfish
	 * 
	 * @param level
	 *            The players hitpoints level
	 */
	public static int getModification(int level) {
		if (level >= 20 && level <= 24) {
			return 4;
		}
		if (level >= 25 && level <= 29) {
			return 6;
		}
		if (level >= 30 && level <= 39) {
			return 7;
		}
		if (level >= 40 && level <= 49) {
			return 8;
		}
		if (level >= 50 && level <= 59) {
			return 11;
		}
		if (level >= 60 && level <= 69) {
			return 12;
		}
		if (level >= 70 && level <= 74) {
			return 13;
		}
		if (level >= 75 && level <= 79) {
			return 15;
		}
		if (level >= 80 && level <= 89) {
			return 16;
		}
		if (level >= 90 && level <= 92) {
			return 17;
		}
		if (level >= 93 && level <= 99) {
			return 22;
		}
		return 3;
	}

	public static boolean hasSerpHelm(Player player) {
		return player.getEquipment() != null && (player.getEquipment().contains(12931) || player.getEquipment().contains(13197) || player.getEquipment().contains(13199));
	}
	
	public static final int ATTACK_TAB = 0;
	public static final int SKILL_TAB = 1;
	public static final int QUEST_TAB = 2;
	public static final int INVENTORY_TAB = 3;
	public static final int EQUIPMENT_TAB = 4;
	public static final int PRAYER_TAB = 5;
	public static final int MAGIC_TAB = 6;
	public static final int FRIENDS_TAB = 8;
	public static final int IGNORE_TAB = 9;
	public static final int CLAN_TAB = 7;
	public static final int WRENCH_TAB = 11;
	public static final int EMOTE_TAB = 12;
	public static final int MUSIC_TAB = 13;
	public static final int LOGOUT_TAB = 10;
	
	public static String SPAWNABLES[] = { "Super strength", "Super attack", "Super defence", "Ranging potion", "Magic potion", "Super restore", "Saradomin brew", "Prayer potion", "Superantipoison", "Shark", "Manta ray", "Super combat", "Dark crabs", "Anglerfish" };

}
