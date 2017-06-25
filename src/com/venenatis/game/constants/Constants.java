package com.venenatis.game.constants;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;

public class Constants {
	
	private static String[] RIGHTS_TO_STRING = { "<col=666666>Player</col>", "<col=F7F2E0>Moderator</col>", "<col=F7FE2E><shad=222222>Administrator</shad></col>","<col=0B610B>Donator</col>", "<col=DF013A>Super Donator</col>", "", "<col=FF6600>Elite Donator<shad=669999>", "<col=CC9933><shad=222222>Extreme Donator</col>"};
	
	private static String EXTREME_DONATOR() {
		return "<col=FFC200><shad=222222>Extreme Donator</col>";
	}
	
	private static String ELITE_DONATOR() {
		return "<col=FFC200><shad=222222>Elite Donator</col>";
	}
	
	private static String SUPER_DONATOR() {
		return "<col=FFC200>Sponsor Donator</col>";
	}
	
	private static String DONATOR() {
		return "<col=FFC200><shad=222222>Donator</col>";
	}
	
	public static String rank(Player player, int rights) {
		if (player.getRights().isExtremeDonator() && !player.getRights().isStaff()) {
			return EXTREME_DONATOR();
		}
		if(player.getRights().isEliteDonator() &&  !player.getRights().isStaff()) {
			return ELITE_DONATOR();
		}
		if(player.getRights().isSuperDonator() &&  !player.getRights().isStaff()) {
			return SUPER_DONATOR();
		}
		if(player.getRights().isDonator() &&  !player.getRights().isStaff()) {
			return DONATOR();
		}
		switch (rights) {
		case 0:
			return RIGHTS_TO_STRING[0];
		case 1:
			return RIGHTS_TO_STRING[1];
		case 2:
			return RIGHTS_TO_STRING[2];
		case 3:
			return RIGHTS_TO_STRING[3];
		case 4:
			return RIGHTS_TO_STRING[4];
		case 5:
			return RIGHTS_TO_STRING[5];
		case 6:
			return RIGHTS_TO_STRING[6];
		}
		return Integer.toString(player.getRights().getValue());
	}
	
	/**
	 * Spawnable items
	 */
	public static final String[] SPAWNABLES = { "Helm of neitiznot", "Prayer potion(4)", "Prayer potion(3)", "Prayer potion(2)",
			"Prayer potion(1)", "Super restore(4)", "Super restore(3)", "Super restore(2)", "Super restore(1)",
			"Magic potion(4)", "Magic potion(3)", "Magic potion(2)", "Magic potion(1)", "Super attack(4)",
			"Super attack(3)", "Super attack(2)", "Super attack(1)", "Super strength(4)", "Super strength(3)",
			"Super strength(2)", "Super strength(1)", "Super defence(4)", "Super defence(3)", "Super defence(2)",
			"Super defence(1)", "Ranging potion(4)", "Ranging potion(3)", "Ranging potion(2)", "Ranging potion(1)",
			"Saradomin brew(4)", "Saradomin brew(3)", "Saradomin brew(2)", "Saradomin brew(1)", "Black d'hide vamb",
			"Black d'hide chaps", "Blue d'hide body", "Shark", "Anglerfish", "Manta ray", "Fire rune", "Water rune",
			"Air rune", "Earth rune", "Mind rune", "Body rune", "Death rune", "Nature rune", "Chaos rune", "Law rune",
			"Cosmic rune", "Blood rune", "Soul rune", "Astral rune", "Rune full helm", "Rune platebody", "Rune platelegs",
			"Rune kiteshield", "Rune boots", "Climbing boots", "Dragon dagger", "Dragon dagger(p++)", "Dragon mace",
			"Dragon scimitar", "Dragon longsword", "Amulet of glory", "Amulet of glory(1)", "Amulet of glory(2)",
			"Amulet of glory(3)", "Amulet of glory(4)", "Amulet of strength", "Mystic hat", "Mystic robe top",
			"Mystic robe bottom", "Mystic gloves", "Mystic boots", "Rune boots", "Rune arrow", "Iron scimitar",
			"Ring of recoil", "Magic shortbow", "Rune crossbow", "Diamong bolts (e)", "Ava's accumulator",
			"Initiate sallet", "Initiate hauberk", "Initiate cuisse", "Granite shield", "Rune plateskirt" };
	
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
	public static final int SKILL_MODIFIER = 125;
	
	/**
	 * Strings that can not be used in a username
	 */
	public static final String BAD_USERNAMES[] = { "m o d", "a d m i n", "mod", "admin", "moderator", "administrator", "owner", "m0d", "adm1n", "0wner", "retard", "Nigga", "nigger", "n1gger", "n1gg3r", "nigg3r", "n1gga", "cock", "faggot", "fag", "anus", "arse", "fuck", "bastard", "bitch", "cunt", "chode", "damn", "dick", "faggit", "gay", "homo", "jizz", "lesbian", "negro", "pussy", "penis", "queef", "twat", "titty", "whore", "b1tch" };

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
	 * Duel respawn X coordinate
	 */
	public static final int DUELING_RESPAWN_X = 3362;
	
	/**
	 * Duel respawn Y coordinate
	 */
	public static final int DUELING_RESPAWN_Y = 3263;
	
	/**
	 * Add random 5 tiles to the current coordinate
	 */
	public static final int RANDOM_DUELING_RESPAWN = 5; 

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

	public static final int TOTAL_MOBS = 7730;

	/**
	 * The rate in milliseconds in which the game thread processes logic.
	 */
	public static final int CYLCE_RATE = 600;

}
