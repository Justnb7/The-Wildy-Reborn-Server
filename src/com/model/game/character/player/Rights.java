package com.model.game.character.player;

import java.util.Arrays;

import com.model.utility.Utility;

/**
 * The rights of a player determines their authority. Every right can be viewed
 * with a name and a value. The value is used to separate each right from one another.
 * 
 * @author Jason
 * @date January 22, 2015, 5:23:49 PM
 */
public enum Rights {
	PLAYER(0),
	MODERATOR(1),
	ADMINISTRATOR(2, MODERATOR),
	DONATOR(3),
	SUPER_DONATOR(4),
	ELITE_DONATOR(5),
	EXTREME_DONATOR(6),
	GOLD_DONATOR(7),
	SUPPORT_TEAM_RED(8),
	SUPPORT_TEAM_BLUE(9),
	PARTYHAT(10),
	COINS(11),
	PK_SKULL_ICON(12),
	SKULL_ICON(13),
	FIRE_CAPE(14),
	CASKET(15),
	GRAPHICS_ARTIST(16),
	VETERAN_BLUE(17),
	VETERAN_ORANGE(18),
	VETERAN_SILVER(19),
	VETERAN_GOLD(20),
	YOUTUBER(21),
	IRON_MAN(22),
	ULTIMATE_IRON_MAN(23),
	NEWS_ICON(25),
	CASH_STACK(26),
	SKULL_ICON_3(27),
	SLAYER_ICON(28),
	VOTE_ICON(29),
	ACHIEVEMENTS_ICON(30);
	
	/**
	 * The level of rights that define this
	 */
	private final int right;
	
	/**
	 * The right or rights inherited by this right
	 */
	private final Rights[] inherited;
	
	/**
	 * Creates a new right with a value to differentiate it between the others
	 * @param right			the right required
	 * @param inherited		the right or rights inherited with this level of right
	 */
	private Rights(int right, Rights... inherited) {
		this.right = right;
		this.inherited = inherited;
	}
	
	/**
	 * The rights of this enumeration
	 * @return	the rights
	 */
	public int getValue() {
		return right;
	}
	
	/**
	 * Determines if this level of rights inherited another level of rights
	 * @param rights	the level of rights we're looking to determine is inherited
	 * @return			{@code true} if the rights are inherited, otherwise {@code false}
	 */
	public boolean inherits(Rights rights) {
		return equals(rights) || Arrays.asList(inherited).contains(rights);
	}
	
	/**
	 * Determines if the player has rights equal to that of {@linkplain PLAYER}.
	 * @return	true if they are of type player
	 */
	public boolean isPlayer() {
		return equals(PLAYER);
	}
	
	/**
	 * Determines if the players rights equal that of {@linkplain MODERATOR}
	 * @return	true if they are of type moderator
	 */
	public boolean isModerator() {
		return equals(MODERATOR);
	}
	
	/**
	 * Determines if the players rights equal that of {@linkplain ADMINISTRATOR}
	 * @return	true if they are of type administrator
	 */
	public boolean isAdministrator() {
		return equals(ADMINISTRATOR);
	}
	
	/**
	 * Determines if the players rights equal that of {@linkplain DONATOR}
	 * @return	true if they are of type donator
	 */	
	public boolean isDonator() {
		return equals(DONATOR);
	}
	
	/**
	 * Determines if the players rights equal that of {@linkplain SUPER_DONATOR}
	 * @return	true if they are of type super donator
	 */	
	public boolean isSuperDonator() {
		return equals(SUPER_DONATOR);
	}
	
	/**
	 * Determines if the players rights equal that of {@linkplain EXTREME_DONATOR}
	 * @return	true if they are of type extreme donator
	 */	
	public boolean isExtremeDonator() {
		return equals(EXTREME_DONATOR);
	}
	
	/**
	 * Determines if the players rights equal that of {@linkplain IRONMAN}
	 * @return	true if they are of type respected ironmen
	 */	
	public boolean isIronMan() {
		return equals(IRON_MAN);
	}
	
	/**
	 * Determines if the players rights equal that of {@linkplain HARDCORE_IRONMAN}
	 * @return	true if they are of type Ultimate ironmen
	 */	
	public boolean isUltimateIron() {
		return equals(ULTIMATE_IRON_MAN);
	}
	
	/**
	 * Determines if the players rights equal that of {@linkplain SUPPORT}
	 * @return	true if they are of type helper
	 */	
	public boolean isSupport() {
		return equals(SUPPORT_TEAM_BLUE);
	}
	
	/**
	 * Determines if the players rights equal that of {@linkplain GRAPHICS_ARTIST}
	 * @return	true if they are of type gfx artist
	 */	
	public boolean isGFXArist() {
		return equals(GRAPHICS_ARTIST);
	}
	
	/**
	 * Determines if the players right equal that of {@link MODERATOR}, {@link ADMINISTRATOR},
	 * and {@link SUPPORT_TEAM}
	 * @return	true if they are any of the predefined types
	 */
	public boolean isStaff() {
		return isModerator() || isSupport() || isAdministrator();
	}
	
	/**
	 * Determines if the players rights are in-between two values.
	 * @param start	the lowest range
	 * @param end	the highest range
	 * @return		true if the rights are greater than the start and lower
	 * than the end value.
	 */
	public boolean isBetween(int start, int end) {
		if (start < 0 || end < 0 || start > end || start == end) {
			throw new IllegalStateException();
		}
		return right >= start && right <= end;
	}
	
	/**
	 * Returns a {@link Rights} object for the value.
	 * @param value	the right level
	 * @return	the rights object
	 */
	public static Rights get(int value) {
		return Arrays.asList(values()).stream().filter(element -> element.right == value).findFirst().orElse(PLAYER);
	}
	
	@Override
	public String toString() {
		return Utility.capitalize(name().toLowerCase());
	}

	/**
	 * Gets an integer representing this rights level.
	 * 
	 * @return An integer representing this rights level.
	 */
	public int toInteger() {
		return right;
	}
}