package com.model.game.character.player.content.clan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.AddClanMember;
import com.model.game.character.player.packets.encode.impl.SendClanMessage;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.packets.encode.impl.SendString;
import com.model.game.item.Item;
import com.model.utility.NameUtils;

/**
 * Handles the clan chat and it's variables.
 * 
 * @author Gabriel | Wolfs Darker
 *
 */
public class Clan extends ClanData {

	/**
	 * The clan's name.
	 */
	private String clan_name;

	/**
	 * The clan's owner name.
	 */
	private String clan_owner;

	/**
	 * The lootshare state.
	 */
	private boolean lootshare;

	/**
	 * The owner's friend list.
	 */
	private List<String> friends_list = new CopyOnWriteArrayList<String>();

	/**
	 * The online members.
	 */
	private List<ClanMember> online_members = new CopyOnWriteArrayList<ClanMember>();

	/**
	 * The registered members.
	 */
	private Map<String, ClanMember> registered_members = new HashMap<String, ClanMember>();

	/**
	 * The clan settings.
	 */
	private SettingState[] settings = new SettingState[4];

	public Clan(Player owner) {

		this.clan_name = owner.getName();
		this.clan_owner = owner.getName();
		settings[CAN_JOIN] = SettingState.ANYONE;
		settings[CAN_TALK] = SettingState.ANYONE;
		settings[CAN_KICK] = SettingState.ONLY_ME;
		settings[CAN_TOGGLE_LOOTSHARE] = SettingState.ONLY_ME;

		ClanWriter.saveClan(this);
	}

	public Clan(String clan_owner, String clanName, int canJoin, int canTalk, int canKick, int canToggleLootshare) {
		this.clan_name = clanName;
		this.clan_owner = clan_owner;
		settings[CAN_JOIN] = SettingState.get(canJoin);
		settings[CAN_TALK] = SettingState.get(canTalk);
		settings[CAN_KICK] = SettingState.get(canKick);
		settings[CAN_TOGGLE_LOOTSHARE] = SettingState.get(canToggleLootshare);
	}

	/**
	 * Returns the clan's name.
	 * 
	 * @return the name
	 */
	public String getClanName() {
		return clan_name;
	}

	/**
	 * Returns the clan's owner name.
	 * 
	 * @return the name
	 */
	public String getClanOwner() {
		return clan_owner;
	}

	/**
	 * Returns if the lootshare is active or not.
	 * 
	 * @return if is active
	 */
	public boolean isLootSharing() {
		return lootshare;
	}

	/**
	 * Returns the owner's friends list.
	 * 
	 * @return the list
	 */
	public List<String> getFriendList() {
		return friends_list;
	}

	/**
	 * Return the list of online members.
	 * 
	 * @return the members
	 */
	public List<ClanMember> getOnlineMembers() {
		return online_members;
	}

	/**
	 * Returns the list of registered members.
	 * 
	 * @return the members
	 */
	public Map<String, ClanMember> getRegisteredMembers() {
		return registered_members;
	}

	/**
	 * Returns the clan settings.
	 * 
	 * @return the settings
	 */
	public SettingState[] getSettings() {
		return settings;
	}

	/**
	 * Checks if the player isa friend of the owner.
	 * 
	 * @param member
	 * @return
	 */
	public boolean isFriend(String member) {
		return friends_list.contains(member.toLowerCase());
	}

	/**
	 * Sets the lootshare state.
	 * 
	 * @param b
	 */
	public void setLootShare(boolean b) {
		this.lootshare = b;
	}

	/**
	 * Handles the player joining into the clanc hat.
	 * 
	 * @param player
	 */
	public void join(Player player) {

		ClanMember member = registered_members.get(player.getName());

		if (member == null) {
			member = new ClanMember(player.getName(), clan_owner);
		}

		if (member.getRank() == ClanRank.BANNED) {
			player.write(new SendMessagePacket("You have been banned from this clan chat."));
			return;
		}

		if (member.getRank() == ClanRank.GUEST && settings[CAN_JOIN] == SettingState.ANY_FRIENDS
				&& !isFriend(player.getName())) {
			player.write(new SendMessagePacket("You don't have the necessary rank to join this clan chat."));
			return;
		}

		if (settings[CAN_JOIN].getRankRequired().ordinal() > member.getRank().ordinal()) {
			player.write(new SendMessagePacket("You don't have the necessary rank to join this clan chat."));
			return;
		}

		player.setClanMembership(member);
		
		for (ClanMember m : online_members) {
			if (m != null && (m == member || member.getName().equalsIgnoreCase(m.getName()))) {
				online_members.remove(m);
			}
		}
		
		online_members.add(member);
		
		if (member.getRank() == ClanRank.OWNER) {
			updateFriendList(player);
		}
		
		updateTab();
		player.write(new SendString("Leave Chat", 18135));
	}

	/**
	 * Registers a member's rank.
	 * 
	 * @param player
	 * @param rank
	 * @param save
	 */
	public void registerMember(String player, ClanRank rank) {
		if (rank == ClanRank.GUEST) {
			registered_members.remove(NameUtils.formatName(player));
		} else {
			registered_members.put(NameUtils.formatName(player), new ClanMember(NameUtils.formatName(player),
					clan_owner, rank));
		}
	}

	/**
	 * Sends a message to the whole clan.
	 * 
	 * @param member_name
	 * @param message
	 */
	public void sendMessage(String member_name, String message, int crown_id) {
		for (int i = 0; i < online_members.size(); i++) {
			ClanMember member = online_members.get(i);
			if (member != null && member.getName() != null) {
				Player player = World.getWorld().getPlayerByName(member.getName());
				if (player != null) {
					player.write(new SendClanMessage(member_name, message, clan_name, crown_id));
				}
			}
		}
	}

	/**
	 * Sends a message of the player's loot to the clan.
	 * 
	 * @param member_name
	 * @param item
	 * @param mob_name
	 * @param crown_id
	 */
	public void sendLootShareMessage(String member_name, Item item, String mob_name, int crown_id) {

		String message = "";

		if (item.getAmount() == 1) {
			message = " received: " + item.getDefinition().getName() + " (" + mob_name + ")";
		} else {
			message = " received: " + NameUtils.formatInt(item.getAmount()) + " x " + item.getDefinition().getName()
					+ " (" + mob_name + ")";
		}

		sendMessage(member_name, message, crown_id);
	}

	/**
	 * Sets the clan chat name and informs everyone.
	 * 
	 * @param player
	 * @param name
	 */
	public void setClanName(Player player, String name) {
		this.clan_name = name;
		sendMessage(clan_owner, " has changed the clan name to '" + clan_name + "'.", player.rights.getValue());
		updateTab();
		ClanWriter.saveClan(this);
	}

	/**
	 * Sets the clan chat settings.
	 * 
	 * @param player
	 * @param state
	 * @param index
	 * @param message
	 */
	public void setClanSettings(Player player, SettingState state, int index) {
		settings[index] = state;
		sendMessage(clan_owner, " has changed the clan chat settings.", player.rights.getValue());
		ClanWriter.saveClan(this);
		updateTab();
	}

	/**
	 * Updates the clan chat tab.
	 */
	public void updateTab() {

		ClanMember member = null;
		Player player = null;
		String name = null;

		for (int i = 0; i < online_members.size(); i++) {
			member = online_members.get(i);
			if (member != null && member.getName() != null) {
				player = World.getWorld().getPlayerByName(member.getName());

				if (player != null && player.getClanMembership() != null
						&& player.getClanMembership().getClanOwner().equalsIgnoreCase(clan_owner)) {
					
					player.write(new SendString("Talking in: " + clan_name, TAB_CLAN_NAME));
					player.write(new SendString("Owner: " + clan_owner, TAB_OWNER_NAME));
					
					for (int x = 0; x < 100; x++) {
						
						if (x < online_members.size() && online_members.get(x) != null) {
							name = online_members.get(x).getName();
							player.write(new SendString(("" + getClanIcon(name) + (NameUtils.formatName(name))), 18144 + x));
							player.write(new AddClanMember(name));
						} else {
							player.write(new SendString("", 18144 + x));
						}
						
					}
				} else {
					online_members.remove(i);
				}
			} else {
				online_members.remove(i);
			}
		}
	}

	/**
	 * Returns the player's name with the crown.
	 * 
	 * @param name
	 * @param rank
	 * @return
	 */
	public String getClanIcon(String member) {
		ClanMember ranked_member = registered_members.get(NameUtils.formatName(member));
		return ranked_member == null ? (isFriend(member) ? "<clan=8/>" : "") : ("<clan="
				+ ranked_member.getRank().getIconId() + "/>");
	}

	/**
	 * Updates the clan chat friend list.
	 * 
	 * @param owner
	 */
	public void updateFriendList(Player owner) {
		String friend = null;
		friends_list.clear();
		for (long f : owner.getFAI().getFriendsList()) {
			friend = NameUtils.longToName(f);
			if (friend != null && friend.length() > 0) {
				friends_list.add(friend);
			}
		}
		ClanWriter.saveClan(this);
	}

	/**
	 * Removes a member from the online list.
	 * 
	 * @param player
	 */
	public void removeOnlineMember(String player) {
		ClanMember member = null;
		for (int i = 0; i < online_members.size(); i++) {
			member = online_members.get(i);
			if (member != null && member.getName().equalsIgnoreCase(player)) {
				online_members.remove(i);
			}
		}
		updateTab();
	}

}
