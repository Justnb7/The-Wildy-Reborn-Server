package com.model.game;

import com.model.game.location.Position;

public class Constants {
	
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
	
	/**
	 * Slayer exp modifier
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
	public static final String SERVER_NAME = "Luzoxpk";
	
	/**
	 * The port in which this server is bound upon
	 */
	public static final int SERVER_PORT = 43594;
	
	/**
	 * the speed of world in ms
	 */
	public static final int WORLD_CYCLE_TIME = 600;
	
	/**
	 * How far does the npc walk
	 */
	public static final int NPC_FOLLOW_DISTANCE = 15;
	
	/**
	 * Maximum amount of items supported by the server
	 */
	public static final int ITEM_LIMIT = 25_000;
	
	/**
	 * Maximum amount a player can hold
	 */
	public static final int MAXITEM_AMOUNT = Integer.MAX_VALUE;
	
	/**
	 * Starting location
	 */
	public static final Position START_PLAYER_LOCATION = new Position(3087, 3499, 0);
	
	/**
	 * Donator zone location
	 */
	public static final Position START_DZ_LOCATION = new Position(2721, 4912, 0);
	
	/**
	 * Respawn location
	 */
	public static final Position RESPAWN_PLAYER_LOCATION = new Position(3099, 3503, 0);
	
	/**
	 * The maximum time for a player skull with an extension in the length.
	 */
	public static final int EXTENDED_SKULL_TIMER = 2000;

	/**
	 * The amount of wealth the player must have in order to receive pk points
	 */
	public static final int PK_POINTS_WEALTH = 250_000;
	
	/**
	 * Difference in X coordinates for directions array.
	 */
	public static final byte[] DIRECTION_DELTA_X = new byte[] { -1, 0, 1, -1, 1, -1, 0, 1 };

	/**
	 * Difference in Y coordinates for directions array.
	 */
	public static final byte[] DIRECTION_DELTA_Y = new byte[] { 1, 1, 1, 0, 0, -1, -1, -1 };
	
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

}
