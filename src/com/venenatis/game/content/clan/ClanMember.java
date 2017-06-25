package com.venenatis.game.content.clan;

import com.venenatis.game.content.clan.ClanData.ClanRank;

/**
 * Handles the clan member and it's variables.
 * 
 * @author Gabriel | Wolfs darker
 *
 */
public class ClanMember {

	/**
	 * The clan's key.
	 */
	private String clan_owner;

	/**
	 * The player's name.
	 */
	private String player_name;

	/**
	 * The player's rank.
	 */
	private ClanRank player_rank;

	public ClanMember(String player_name, String clan_owner) {
		this.clan_owner = clan_owner;
		this.player_name = player_name;
		this.player_rank = ClanRank.GUEST;
	}

	public ClanMember(String player_name, String clan_owner, ClanRank rank) {
		this.clan_owner = clan_owner;
		this.player_name = player_name;
		this.player_rank = rank;
	}

	/**
	 * Returns the clan chat key.
	 * 
	 * @return the key
	 */
	public String getClanOwner() {
		return clan_owner;
	}

	/**
	 * Returns the player's name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return player_name;
	}

	/**
	 * Returns the player's rank.
	 * 
	 * @return the rank
	 */
	public ClanRank getRank() {
		return player_rank;
	}
}
