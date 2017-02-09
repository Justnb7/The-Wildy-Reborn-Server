package com.model.game.character.player.content.clan;

import com.model.utility.NameUtils;

/**
 * Stores all the clan chat data.
 * 
 * @author Gabriel | Wolfs Darker
 *
 */
public class ClanData {

	/**
	 * Clan's name interface ID.
	 */
	public final static int CLAN_SETUP_NAME = 48061;

	/**
	 * Clan's who can join interface ID.
	 */
	public final static int CLAN_SETUP_JOIN = 48063;

	/**
	 * Clan's who can talk interface ID.
	 */
	public final static int CLAN_SETUP_TALK = 48065;

	/**
	 * Clan's who can kick interface ID.
	 */
	public final static int CLAN_SETUP_KICK = 48067;

	/**
	 * Clan's who can toggle lootshare interface ID.
	 */
	public final static int CLAN_SETUP_LOOTSHARE = 48069;

	/**
	 * Clan's talking in interface ID.
	 */
	public final static int TAB_CLAN_NAME = 18139;

	/**
	 * Clan's owner name interface ID.
	 */
	public static final int TAB_OWNER_NAME = 18140;

	/**
	 * Settings index for who can join.
	 */
	public static final int CAN_JOIN = 0;

	/**
	 * Settings index for who can talk.
	 */
	public static final int CAN_TALK = 1;

	/**
	 * Settings index for who can kick.
	 */
	public static final int CAN_KICK = 2;

	/**
	 * Settings index for who can toggle lootshare.
	 */
	public static final int CAN_TOGGLE_LOOTSHARE = 3;

	/**
	 * Handles the clan chat setting's state.
	 * 
	 * @author Gabriel | Wolfs Darker
	 *
	 */
	public enum SettingState {
		ANYONE("Anyone", ClanRank.GUEST, 1),
		ANY_FRIENDS("Any friends", ClanRank.GUEST, 2),
		RECRUIT("Recruit+", ClanRank.RECRUIT, 3),
		CORPORAL("Corporeal+", ClanRank.CORPORAL, 4),
		SERGEANT("Sergeant+", ClanRank.SERGEANT, 5),
		LIEUTENANT("Lieutenant+", ClanRank.LIEUTENANT, 6),
		CAPTAIN("Captain+", ClanRank.CAPTAIN, 7),
		GENERAL("General+", ClanRank.GENERAL, 8),
		ONLY_ME("Only me", ClanRank.OWNER, 9);

		private String to_string;

		private ClanRank rank_required;

		private int id;

		private SettingState(String to_string, ClanRank rank_required, int id) {
			this.to_string = to_string;
			this.rank_required = rank_required;
			this.id = id;
		}

		@Override
		public String toString() {
			return to_string;
		}

		public ClanRank getRankRequired() {
			return rank_required;
		}

		public int toInteger() {
			return id;
		}

		/**
		 * Returns a state by its index.
		 * 
		 * @param index
		 * @return the state
		 */
		public static SettingState get(int id) {
			for (SettingState state : SettingState.values()) {
				if (id == state.toInteger()) {
					return state;
				}
			}
			return null;
		}

	}

	/**
	 * Handle the clan chat icons.
	 * 
	 * @author Gabriel | Wolfs Darker
	 *
	 */
	public enum ClanRank {

		GUEST(-1), RECRUIT(0), CORPORAL(1), SERGEANT(2), LIEUTENANT(3), CAPTAIN(4), GENERAL(5), OWNER(6), BANNED(-1);

		private int icon_id;

		private ClanRank(int icon_id) {
			this.icon_id = icon_id;
		}

		public int getIconId() {
			return icon_id;
		}

		public String toStringRank() {
			return NameUtils.formatName(this.toString().toLowerCase());
		}

		/**
		 * Returns a rank by its index.
		 * 
		 * @param index
		 * @return the rank
		 */
		public static ClanRank get(int index) {
			for (ClanRank rank : ClanRank.values()) {
				if (index == rank.ordinal()) {
					return rank;
				}
			}
			return null;
		}
	}

	/**
	 * Returns the player's data by the button id.
	 * 
	 * @param button_id
	 * @return the player's index in the list and the action.
	 */
	public static int[] getInfo(int button_id) {
		int[] data = new int[2];

		String dif = (button_id - 231165) + "";

		data[0] = Integer.parseInt(dif.substring(dif.length() - 1));
		data[1] = (Integer.parseInt(dif) - data[0]) / 10;
		return data;
	}

}
