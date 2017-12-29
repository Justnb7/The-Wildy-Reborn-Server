package com.venenatis.game.model.entity.player.clan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.server.Server;

public class Clan {

	public Player player;
	
	/**
	 * The ranks privileges require (joining, talking, kicking, banning).
	 */
	public int joinable = ClanRank.ANYONE.getRankIndex();
	public int talkable = ClanRank.ANYONE.getRankIndex();
	public int kickable = ClanRank.GENERAL.getRankIndex();
	public int managable = ClanRank.LEADER.getRankIndex();
	

	/**
	 * Adds a member to the clan.
	 * 
	 * @param player
	 */
	public void addMember(Player player) {
		player.message("Attempting to join channel...");
		if (activeMembers.size() >= 100) {
			player.setClan(null);
			resetInterface(player);
			player.message("This clan chat is full.");
			return;
		}
		if (isBanned(player.getUsername())) {
			player.setClan(null);
			resetInterface(player);
			player.message("@or2@You are currently banned from this clan.");
			return;
		}
		if (joinable > ClanRank.ANYONE.getRankIndex() && !isFounder(player.getUsername()) && !isGeneral(player.getUsername())) {
			if (getRank(player.getUsername()) < joinable || 
					(joinable == ClanRank.FRIEND.getRankIndex() && World.getWorld().getPlayerByName(getFounder()) != null && !World.getWorld().lookupPlayerByName(getFounder()).isFriend(player))) {
				if (joinable == ClanRank.FRIEND.getRankIndex()) {
					if (World.getWorld().getPlayerByName(getFounder()) == null || !World.getWorld().lookupPlayerByName(getFounder()).isFriend(player)) {
						player.setClan(null);
						resetInterface(player);
						if (World.getWorld().getPlayerByName(getFounder()) == null) {
							player.message("You can't join this chat while the owner is offline.");
						} else {
							player.message("Only friends of " + getFounder() + " may join this chat.");
						}
						return;
					}
				} else {
					player.setClan(null);
					resetInterface(player);
					player.message("Only " + getRankTitle(joinable) + "s+ may join this chat.");
					return;
				}
			}
		}
		player.setClan(this);
		player.lastClanChat = getFounder();
		activeMembers.add(player.getUsername());
		player.getActionSender().sendString("Talking in: <col=FFFF75>" + getTitle() + "</col>", 28139);
		player.getActionSender().sendString("Owner: <col=FFFFFF>" + Utility.formatName(getFounder()) + "</col>", 28140);
		player.message("Now talking in clan channel @or2@" + getTitle() + "@bla@");
		player.message("To talk, start each line of chat with the / symbol.");
		updateMembers();
	}

	/**
	 * Removes the player from the clan.
	 * 
	 * @param player
	 */
	public void removeMember(Player player) {
		List<String> remove = new ArrayList<>(1);
		for (String member : activeMembers) {
			if (Objects.isNull(member)) {
				continue;
			}
			if (member.equalsIgnoreCase(player.getUsername())) {
				player.setClan(null);
				resetInterface(player);
				remove.add(member);
			}
		}
		activeMembers.removeAll(remove);
		updateMembers();
	}

	/**
	 * Removes the player from the clan.
	 * 
	 * @param player
	 */
	private void removeMember(String name) {
		List<String> remove = new ArrayList<>(1);
		for (String member : activeMembers) {
			if (Objects.isNull(member)) {
				continue;
			}
			if (member.equalsIgnoreCase(name)) {
				Player player = World.getWorld().lookupPlayerByName(name);
				player.setClan(null);
				resetInterface(player);
				remove.add(member);
			}
		}
		activeMembers.removeAll(remove);
		updateMembers();
	}

	/**
	 * Updates the members on the interface for the player.
	 * 
	 * @param player
	 */
	public void updateInterface(Player player) {
		player.getActionSender().sendString(isFounder(player.getUsername()) ? "Delete Clan" : "Leave Chat", 28129);
		player.getActionSender().sendString(isFounder(player.getUsername()) ? "Delete Clan" : "Leave Chat", 28135);
		player.getActionSender().sendString("Talking in: @or2@" + getTitle(), 28139);
		player.getActionSender().sendString(getTitle(), 28306);
		player.getActionSender().sendString("Owner: @or2@" + Utility.formatName(getFounder()), 28140);
		player.getActionSender().sendString(getRankTitle(joinable), 28309);
		player.getActionSender().sendString(getRankTitle(talkable), 28312);
		player.getActionSender().sendString(getRankTitle(kickable), 28315);
		player.getActionSender().sendString(getRankTitle(managable), 28318);
		player.getActionSender().sendString(isLootShare() ? "Lootshare enabled" : "Lootshare disabled", 28529);
		player.getActionSender().sendConfig(28528, isLootShare() ? 1 : 0);
		
		Collections.sort(activeMembers);
		String[] aMembers = new String[activeMembers.size()];
		for (int index = 0; index < activeMembers.size(); index++) {
			aMembers[index] = "<clan=" + getRank(activeMembers.get(index)) + ">" + Utility.formatName(activeMembers.get(index));
		}
		player.getActionSender().sendStrings(28144, 28244, aMembers);
		
		Collections.sort(rankedMembers);
		String[] rMembers = new String[rankedMembers.size()];
		for (int index = 0; index < rankedMembers.size(); index++) {
			rMembers[index] = "<clan=" + getRank(rankedMembers.get(index)) + ">" + Utility.formatName(rankedMembers.get(index));
		}
		player.getActionSender().sendStrings(28323, 28423, rMembers);
		
		Collections.sort(bannedMembers);
		player.getActionSender().sendStrings(28425, 28525, bannedMembers);
	}

	/**
	 * Updates the interface for all members.
	 */
	public void updateMembers() {
		for (Player player : World.getWorld().getPlayers()) {
			if (Objects.nonNull(activeMembers) && Objects.nonNull(player)) {
				if (activeMembers.contains(player.getUsername())) {
					updateInterface(player);
				}
			}
		}
	}

	/**
	 * Resets the clan interface.
	 * 
	 * @param player
	 */
	public static void resetInterface(Player player) {
		player.getActionSender().sendString("Join Chat", 28135);
		player.getActionSender().sendString("Talking in: Not in chat", 28139);
		player.getActionSender().sendString("Owner: None", 28140);
		player.getActionSender().sendString("Chat Disabled", 28306);
		player.getActionSender().emptyStrings(28144, 28244);
	}

	/**
	 * Sends a message to the clan.
	 * 
	 * @param player
	 * @param message
	 */
	public void sendChat(Player paramClient, String paramString) {
		if (System.currentTimeMillis() - paramClient.lastClanTalk < 600) {
			paramClient.message("You can only send one message per game tick.");
			return;
		}
		if (getRank(paramClient.getUsername()) < this.talkable) {
			paramClient.message("Only " + getRankTitle(this.talkable) + "s+ may talk in this chat.");
			return;
		}
		if (paramClient.isMuted()) {
			paramClient.message("You are muted and cannot talk in this chat.");
			return;
		}

		for (String member : activeMembers) {
			Player player = World.getWorld().lookupPlayerByName(member);
			if (player == null) {
				continue;
			}
			player.message("@bla@[@blu@" + getTitle() + "@bla@] <clan=" + getRank(paramClient.getUsername()) + ">" + "@bla@" + Utility.optimizeText(paramClient.getUsername()) + ": @dre@" + Character.toUpperCase(paramString.charAt(0)) + paramString.substring(1) + ":clan:");
		}
		paramClient.lastClanTalk = System.currentTimeMillis();
	}

	/**
	 * Sets the rank for the specified name.
	 * 
	 * @param name
	 * @param rank
	 */
	public void setRank(String name, int rank) {
		if (rank > ClanRank.GENERAL.getRankIndex()) {
			rank = ClanRank.GENERAL.getRankIndex();
		}
		if (rankedMembers.contains(name)) {
			ranks.set(rankedMembers.indexOf(name), rank);
		} else if (!isGeneral(name)) {
			rankedMembers.add(name);
			ranks.add(rank);
		}
		save();
	}

	/**
	 * Demotes the specified name.
	 * 
	 * @param name
	 */
	public void demote(String name) {
		if (!rankedMembers.contains(name)) {
			return;
		}
		int index = rankedMembers.indexOf(name);
		rankedMembers.remove(index);
		ranks.remove(index);
		save();
	}

	/**
	 * Gets the rank of the specified name.
	 * 
	 * @param name
	 * @return
	 */
	public int getRank(String name) {
		name = Utility.formatName(name);
		if (isGeneral(name)) {
			return ClanRank.GENERAL.getRankIndex();
		}
		if (isFounder(name)) {
			return ClanRank.LEADER.getRankIndex();
		}
		if (rankedMembers.contains(name)) {
			return ranks.get(rankedMembers.indexOf(name));
		}
		return -1;
	}

	/**
	 * Can they kick?
	 * 
	 * @param name
	 * @return
	 */
	public boolean canKick(String name) {
		if (isFounder(name)) {
			return true;
		}
		if (getRank(name) == ClanRank.GENERAL.getRankIndex()) {
			return true;
		}
		if (getRank(name) >= kickable) {
			return true;
		}
		return false;
	}

	/**
	 * Can they ban?
	 * 
	 * @param name
	 * @return
	 */
	public boolean canBan(String name) {
		if (isFounder(name)) {
			return true;
		}
		if (isGeneral(name)) {
			return true;
		}
		if (getRank(name) >= managable) {
			return true;
		}
		return false;
	}

	/**
	 * Returns whether or not the specified name is the founder.
	 * 
	 * @param name
	 * @return
	 */
	public boolean isFounder(String name) {
		if (getFounder().equalsIgnoreCase(name)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns whether the specified name is an General.
	 * 
	 * @param name
	 *            the player
	 * @return true if they are a mod+
	 */
	public boolean isGeneral(String name) {
		if (World.getWorld().getPlayerByName(name) == null) {
			return false;
		}
		return Rights.isWithin(World.getWorld().lookupPlayerByName(name).getRights(), Rights.MODERATOR);
	}

	/**
	 * Returns whether or not the specified name is a ranked user.
	 * 
	 * @param name
	 * @return
	 */
	public boolean isRanked(String name) {
		name = Utility.formatName(name);
		if (isGeneral(name)) {
			return true;
		}
		if (rankedMembers.contains(name)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns whether or not the specified name is banned.
	 * 
	 * @param name
	 * @return
	 */
	public boolean isBanned(String name) {
		name = Utility.formatName(name);
		if (bannedMembers.contains(name)) {
			return true;
		}
		return false;
	}

	/**
	 * Kicks the name from the clan chat.
	 * 
	 * @param name
	 */
	public void kickMember(String name) {
		if (!activeMembers.contains(name)) {
			return;
		}
		if (name.equalsIgnoreCase(getFounder())) {
			return;
		}
		if (isGeneral(name)) {
			return;
		}
		Player otherPlayer = World.getWorld().lookupPlayerByName(name);
		removeMember(name);
		if (otherPlayer != null) {
			otherPlayer.message("You have been kicked from the clan chat.");
		}
	}

	/**
	 * Bans the name from entering the clan chat.
	 * 
	 * @param name
	 */
	public void banMember(String name) {
		name = Utility.formatName(name);
		if (bannedMembers.contains(name)) {
			return;
		}
		if (name.equalsIgnoreCase(getFounder())) {
			return;
		}
		if (isGeneral(name)) {
			return;
		}
		if (isRanked(name)) {
			int index = rankedMembers.indexOf(name);
			rankedMembers.remove(index);
			ranks.remove(index);
		}
		removeMember(name);
		bannedMembers.add(name);
		save();
		Player otherPlayer = World.getWorld().lookupPlayerByName(name);
		if (otherPlayer != null) {
			otherPlayer.message("You have been banned from the clan chat.");
		}
	}

	/**
	 * Unbans the name from the clan chat.
	 * 
	 * @param name
	 */
	public void unbanMember(String name) {
		name = Utility.formatName(name);
		if (bannedMembers.contains(name)) {
			bannedMembers.remove(name);
			save();
		}
	}
	
	public void unbanMember(int id) {
		Collections.sort(bannedMembers);
		bannedMembers.remove(id);
		save();
	}
	
	/**
	 * Gets the ranked player at the given index
	 * 
	 * @param index
	 */
	
	public String getRankedMemberAtIndex(int index) {
		Collections.sort(rankedMembers);
		return rankedMembers.get(index);
	}
	
	/**
	 * Gets the active player at the given index
	 * 
	 * @param index
	 */
	
	public String getActiveMemberAtIndex(int index) {
		Collections.sort(activeMembers);
		return activeMembers.get(index);
	}

	/**
	 * Saves the clan.
	 */
	public void save() {
		Server.getClanManager().save(this);
		updateMembers();
	}

	/**
	 * Deletes the clan.
	 */
	public void delete() {

		activeMembers.forEach((username) -> {
			final Player player = World.getWorld().lookupPlayerByName(username);
			if (player != null) {
				player.setClan(null);
				resetInterface(player);
				if (!isFounder(player.getUsername())) {
					player.message("The clan you were in has been deleted.");
				}
			}
		});
		Server.getClanManager().delete(this);
	}

	/**
	 * Creates a new clan for the specified player.
	 * 
	 * @param player
	 */
	public Clan(Player player) {
		setTitle(player.getUsername() + "");
		setFounder(player.getUsername().toLowerCase());
	}

	/**
	 * Creates a new clan for the specified title and founder.
	 * 
	 * @param title
	 * @param founder
	 */
	public Clan(String title, String founder) {
		setTitle(title);
		setFounder(founder);
	}

	/**
	 * Gets the founder of the clan.
	 * 
	 * @return
	 */
	public String getFounder() {
		return founder;
	}

	/**
	 * Sets the founder.
	 * 
	 * @param founder
	 */
	public void setFounder(String founder) {
		this.founder = founder;
	}

	/**
	 * Gets the title of the clan.
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title
	 * @return
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The title of the clan.
	 */
	public String title;

	/**
	 * The founder of the clan.
	 */
	public String founder;

	/**
	 * The active clan members.
	 */
	public LinkedList<String> activeMembers = new LinkedList<String>();

	/**
	 * The banned members.
	 */
	public LinkedList<String> bannedMembers = new LinkedList<String>();

	/**
	 * The ranked clan members.
	 */
	public LinkedList<String> rankedMembers = new LinkedList<String>();

	/**
	 * The clan member ranks.
	 */
	public LinkedList<Integer> ranks = new LinkedList<Integer>();

	/**
	 * Gets the rank title as a string.
	 * 
	 * @param rank
	 * @return
	 */
	public String getRankTitle(int rank) {
		switch (rank) {
		case -1:
			return "Anyone";
		case 0:
			return "Friend";
		case 1:
			return "Recruit";
		case 2:
			return "Corporal";
		case 3:
			return "Sergeant";
		case 4:
			return "Lieutenant";
		case 5:
			return "Captain";
		case 6:
			return "General";
		case 7:
			return "Owner";
		}
		return "";
	}

	/**
	 * Sets the minimum rank that can join.
	 * 
	 * @param rank
	 */
	public void setRankCanJoin(int rank) {
		joinable = rank;
		save();
	}

	/**
	 * Sets the minimum rank that can talk.
	 * 
	 * @param rank
	 */
	public void setRankCanTalk(int rank) {
		talkable = rank;
		save();
	}

	/**
	 * Sets the minimum rank that can kick.
	 * 
	 * @param rank
	 */
	public void setRankCanKick(int rank) {
		kickable = rank;
		save();
	}

	/**
	 * Sets the minimum rank that can ban.
	 * 
	 * @param rank
	 */
	public void setRankCanBan(int rank) {
		managable = rank;
		save();
	}

	public void handleLootShare(Player player, int item, int amount) {
	}

	public boolean isLootShare() {
		return lootShare;
	}

	public void setLootShare(boolean lootShare) {
		this.lootShare = lootShare;
		save();
	}

	public Integer getLootSharePoints(Player player) {
		if (lootSharePoints.get(player.getUsername().toLowerCase()) != null) {
			return lootSharePoints.get(player.getUsername().toLowerCase());
		}
		return 0;
	}

	public void setLootSharePoints(Player player, int amount) {
		lootSharePoints.put(player.getUsername().toLowerCase(), amount);
	}
	
	/**
	 * Lootshare variables
	 */
	public boolean lootShare;
	public Map<String, Integer> lootSharePoints = new HashMap<String, Integer>();

}