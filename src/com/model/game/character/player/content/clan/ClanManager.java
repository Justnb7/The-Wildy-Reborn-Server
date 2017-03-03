package com.model.game.character.player.content.clan;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.RemoveClanMember;
import com.model.game.character.player.packets.out.SendEnterStringInterface;
import com.model.game.character.player.packets.out.SendInterface;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.player.packets.out.SendString;
import com.model.utility.NameUtils;
import com.model.utility.Utility;

/**
 * Handles the clan chat management.
 * 
 * @author Gabriel | Wolfs Darker
 *
 */
public class ClanManager extends ClanData {

	/**
	 * The clan chat list.
	 */
	private static Map<String, Clan> clansMap = new HashMap<String, Clan>();

	/**
	 * Returns the map containing all the clans.
	 * 
	 * @return
	 */
	public static Map<String, Clan> getClanList() {
		return clansMap;
	}

	/**
	 * Loads the clan informations.
	 */
	public static void init() {
		List<Clan> clan_list = ClanWriter.loadClanChats();

		if (clan_list == null || clan_list.isEmpty()) {
			return;
		}

		for (Clan clan : clan_list) {
			if (clan != null) {
				clan.getOnlineMembers().clear();
				clansMap.put(clan.getClanOwner().toLowerCase(), clan);
			}
		}
	}

	/**
	 * Opens the clan setup.
	 * 
	 * @param player
	 */
	public static void clanSetup(Player player) {

		Clan clan = clansMap.get(player.getName().toLowerCase());

		if (clan == null) {
			clan = new Clan(player);
			clansMap.put(player.getName().toLowerCase(), clan);
			clan.registerMember(player.getName(), ClanRank.OWNER);
			updateFriendsList(player);
			ClanWriter.saveClan(clan);
		}

		player.write(new SendString(clan.getClanName(), CLAN_SETUP_NAME));
		player.write(new SendString(clan.getSettings()[Clan.CAN_JOIN].toString(), CLAN_SETUP_JOIN));
		player.write(new SendString(clan.getSettings()[Clan.CAN_TALK].toString(), CLAN_SETUP_TALK));
		player.write(new SendString(clan.getSettings()[Clan.CAN_KICK].toString(), CLAN_SETUP_KICK));
		player.write(new SendString(clan.getSettings()[Clan.CAN_TOGGLE_LOOTSHARE].toString(), CLAN_SETUP_LOOTSHARE));

		for (int x = 0; x < 200; x++) {
			player.write(new SendString("", 58101 + x));
			player.write(new SendString("", 59301 + (x * 10)));
		}

		int interface_slot = 0;

		String name = "";
		ClanMember member = null;

		for (int i = 0; i < clan.getFriendList().size(); i++) {
			name = clan.getFriendList().get(i);
			if (name != null && name.length() > 0) {
				name = NameUtils.formatName(clan.getFriendList().get(i));
				member = clan.getRegisteredMembers().get(name);
				player.write(new SendString(name, 58101 + interface_slot));
				player.write(new SendString(member == null ? "Guest" : member.getRank().toStringRank(), 59301 + (interface_slot * 10)));
				interface_slot++;
			}
		}
		
		player.write(new SendInterface(48000));
	}

	/**
	 * Handles a edit clan action.
	 * 
	 * @param player
	 * @param action
	 * @param objects
	 */
	public static void editSettings(Player player, String action, Object... objects) {

		Clan clan = clansMap.get(player.getName().toLowerCase());

		if (clan == null) {
			player.write(new SendMessagePacket("You don't have permissions to edit this clan."));
			return;
		}

		if (player.getClanMembership() != null && player.getClanMembership().getRank() != ClanRank.OWNER) {
			player.write(new SendMessagePacket("You don't have permissions to edit this clan."));
			return;
		}

		if (objects.length > 0 && objects[0] instanceof SettingState) {
			int index = (int) objects[1];

			SettingState state = (SettingState) objects[0];

			if (state == null) {
				player.write(new SendMessagePacket("Error: Invalid setting."));
				return;
			}

			if (clan.getSettings()[index] == state) {
				return;
			}

			clan.setClanSettings(player, state, index);

			player.write(new SendString(clan.getClanName(), CLAN_SETUP_NAME));
			player.write(new SendString(clan.getSettings()[Clan.CAN_JOIN].toString(), CLAN_SETUP_JOIN));
			player.write(new SendString(clan.getSettings()[Clan.CAN_TALK].toString(), CLAN_SETUP_TALK));
			player.write(new SendString(clan.getSettings()[Clan.CAN_KICK].toString(), CLAN_SETUP_KICK));
			player.write(new SendString(clan.getSettings()[Clan.CAN_TOGGLE_LOOTSHARE].toString(), CLAN_SETUP_LOOTSHARE));
		}

		switch (action) {
		case "CHANGE_NAME":
			if (objects.length == 0) {
				player.write(new SendMessagePacket("Error: Invalid clan name."));
				return;
			}

			String name = (String) objects[0];

			if (name == null || name.length() == 0) {
				player.write(new SendMessagePacket("Error: Invalid clan name."));
				return;
			}

			name = name.replace("_", " ");

			clan.setClanName(player, NameUtils.formatName(name));
			player.write(new SendString(clan.getClanName(), CLAN_SETUP_NAME));
			break;
		case "EDIT_PLAYER":
			break;
		case "MEMBER_EDIT":

			int button = (int) objects[0];

			if (button <= 0) {
				return;
			}

			int[] info = getInfo(button);
			
			if (info[1] >= clan.getFriendList().size()) {
				return;
			}

			String member_name = clan.getFriendList().get(info[1]);

			if (member_name == null || member_name.length() == 0) {
				return;
			}

			ClanMember member = clan.getRegisteredMembers().get(NameUtils.formatName(member_name));

			ClanRank rank = null;

			if (info[0] == 0) {
				if (member != null && member.getRank() == ClanRank.BANNED) {
					rank = ClanRank.GUEST;
				} else {
					player.write(new SendMessagePacket("This user is not banned."));
					return;
				}
			} else if (info[0] == 1) {
				if (member != null && member.getRank() == ClanRank.BANNED) {
					player.write(new SendMessagePacket("This user is already banned."));
					return;
				}
				rank = ClanRank.BANNED;
			} else if (info[0] > 1) {
				rank = ClanRank.get(8 - info[0]);
			}

			if (rank == null) {
				return;
			}

			clan.registerMember(member_name, rank);

			Player m = World.getWorld().getPlayerByName(member_name);

			if (m != null && rank != ClanRank.BANNED) {
				m.setClanMembership(new ClanMember(member_name, clan.getClanOwner(), rank));
			}

			if (rank == ClanRank.BANNED) {
				clan.sendMessage(clan.getClanOwner(), " has banned " + member_name + ".", player.rights.getValue());
				if (m != null) {
					leaveClan(m, true);
				}
			} else if (rank != ClanRank.GUEST) {
				clan.sendMessage(clan.getClanOwner(), " has promoted " + NameUtils.formatName(member_name) + " to "
						+ rank.toString().toLowerCase() + ".", player.rights.getValue());
			}
			player.write(new SendString(NameUtils.formatName(member_name), 58101 + info[1]));
			player.write(new SendString(NameUtils.formatName(rank.toString().toLowerCase()), 59301 + (info[1] * 10)));
			ClanWriter.saveClan(clan);
			clan.updateTab();
			break;
		}
	}

	/**
	 * Handles the clan join action.
	 * 
	 * @param player
	 * @param clan_owner
	 */
	public static void joinClan(Player player, String clan_owner) {

		if (player.getClanMembership() != null) {
			player.write(new SendMessagePacket("You're in a clan chat already."));
			return;
		}

		Clan clan = clansMap.get(clan_owner.toLowerCase());
		
		if (clan != null) {
			clan.join(player);
		} else {
			if (player.getName().equalsIgnoreCase(clan_owner)) {
				player.write(new SendMessagePacket("You must setup your clan chat before joining it!"));
				return;
			}
			player.write(new SendMessagePacket("This clan chat does not exist."));
			return;
		}
	}

	/**
	 * Removes the player from clan chat.
	 * 
	 * @param player
	 * @param logout
	 */
	public static void leaveClan(Player player, boolean logout) {

		Clan clan = player.getClanMembership() == null ? null : clansMap.get(player.getClanMembership().getClanOwner()
				.toLowerCase());

		player.write(new SendString("Talking in: Not in chat", TAB_CLAN_NAME));
		player.write(new SendString("Owner: None", TAB_OWNER_NAME));
		player.write(new SendString("r221", 18144));

		for (int x = 0; x < 100; x++) {
			player.write(new SendString("", 18144 + x));
		}
		
		player.write(new SendString("Join Chat", 18135));
		player.setClanMembership(null);

		if (clan == null) {
			return;
		}

		for (ClanMember member : clan.getOnlineMembers()) {
			if (member != null && member.getName() != null) {
				Player clan_member = World.getWorld().getPlayerByName(member.getName());
				if (clan_member != null) {
					clan_member.write(new RemoveClanMember(player.getName()));
				}
			}
		}

		clan.removeOnlineMember(player.getName());
		player.setClanMembership(null);

		if (!logout) {
			player.write(new SendMessagePacket("You left the clan chat."));
		}
	}

	/**
	 * Handles the actions that any member can do.
	 * 
	 * @param player
	 * @param action
	 * @param objects
	 */
	public static void memberActions(Player player, String action, Object... objects) {
		if (player.getClanMembership() == null) {
			player.write(new SendMessagePacket("You must be in a clan chat to do this."));
			return;
		}
		Clan clan = clansMap.get(player.getClanMembership().getClanOwner().toLowerCase());
		if (clan == null || objects.length == 0) {
			player.write(new SendMessagePacket("You must be in a clan chat to do this."));
			return;
		}
		if (action.equals("KICK_MEMBER")) {
			if(!player.rights.isStaff()) {
				player.write(new SendMessagePacket("You don't have permissions to edit this clan."));
				return;
			}
			int button_id = (int) objects[0];
			if (button_id <= 0 || (70224 - button_id) >= clan.getOnlineMembers().size()) {
				return;
			}

			ClanMember clan_member = clan.getOnlineMembers().get(button_id - 70224);
			if (clan_member == null) {
				return;
			}

			if (clan_member.getName().equalsIgnoreCase(player.getName())) {
				return;
			}

			Player member = World.getWorld().getPlayerByName(clan_member.getName());

			if (member == null) {
				return;
			}
			if(!member.getClanPunishment()) {
				member.setClanPunishment(true);
				clan.sendMessage(player.getName(), " Muted " + NameUtils.formatName(clan_member.getName()) + ".",
						player.rights.getValue());
			} else {
				member.setClanPunishment(false);
				clan.sendMessage(player.getName(), " Unmuted " + NameUtils.formatName(clan_member.getName()) + ".",
						player.rights.getValue());
			}
		} else if (action.equals("DIALOGUE")) {
			String message = (String) objects[0];
			if (message == null || message.length() == 0) {
				return;
			}

			if (player.getClanMembership().getRank() == ClanRank.GUEST
					&& clan.getSettings()[CAN_TALK] == SettingState.ANY_FRIENDS && !clan.isFriend(player.getName())) {
				player.write(new SendMessagePacket("You don't have the necessary rank to join this clan chat."));
				return;
			}
			
			if(player.getClanPunishment()) {
				player.write(new SendMessagePacket("You are muted in the clanchat"));
				return;
			}

			if (clan.getSettings()[CAN_TALK].getRankRequired().ordinal() > player.getClanMembership().getRank()
					.ordinal()) {
				player.write(new SendMessagePacket("You don't have the necessary rank to join this clan chat."));
				return;
			}
			List<String> invalids;
			invalids = Arrays.asList( "<col=", "<shad=", "req:", ":market:", "shad", "nigga", "slut", "fuck", "cunt", "dick", "bitch", "negro", "jew", "fuck", "bitch", "pussy", "nigger", "nigga", "faggot", "dick",
					"shit", "cock", "cunt", "asshole", "hitler", "niggers", "nigguh", "gay", "fag", "feg", "downie", "downsyndrome", "retard");
			for (String string : invalids) {
				if (message.contains(string)) {
					player.write(new SendMessagePacket("Your message was not sent as it contained an illegal character."));
					return;
				}
			}
			message = message.replace("/", "");
			clan.sendMessage("<col=000000>" + player.getName(), ": <col=810000>" + Utility.formatPlayerName(message) + "",
					player.rights.getValue());
		} else if (action.equals("LOOTSHARE_TOGGLE")) {

			if (clan.getSettings()[CAN_TOGGLE_LOOTSHARE].getRankRequired().ordinal() > player.getClanMembership()
					.getRank().ordinal()) {
				player.write(new SendMessagePacket("You don't have the necessary rank to edit this clan chat."));
				return;
			}

			clan.setLootShare(!clan.isLootSharing());
			clan.sendMessage(player.getName(), " " + (clan.isLootSharing() ? "activated" : "deactivated")
					+ " the lootshare.", player.rights.getValue());
			if (player.getName().equalsIgnoreCase(clan.getClanOwner())) {
				ClanWriter.saveClan(clan);
			}
		}
	}

	/**
	 * Updates the clan's friends list.
	 * 
	 * @param player
	 */
	public static void updateFriendsList(Player player) {
		Clan clan = clansMap.get(player.getName().toLowerCase());
		if (clan == null) {
			return;
		}
		clan.updateFriendList(player);
		if (player.openInterface == 48000) {
			ClanManager.clanSetup(player);
		}
		clan.updateTab();
	}

	/**
	 * Handles the button clicking.
	 * 
	 * @param player
	 * @param button_id
	 * @return
	 */
	public static boolean handleButtons(Player player, int button_id) {
		
		if (button_id >= 231165 && button_id <= 237187) {
			ClanManager.editSettings(player, "MEMBER_EDIT", button_id);
			return true;
		}

		if (button_id >= 230130 && button_id <= 230139) {
			ClanManager.editSettings(player, "CAN_JOIN",
					(button_id == 230130 ? SettingState.ANYONE : SettingState.get(button_id - 230130)), CAN_JOIN);
			return true;
		}

		if (button_id >= 230140 && button_id <= 230149) {
			ClanManager.editSettings(player, "CAN_TALK",
					(button_id == 230140 ? SettingState.ANYONE : SettingState.get(button_id - 230140)), CAN_TALK);
			return true;
		}

		if (button_id >= 230150 && button_id <= 230157) {
			ClanManager.editSettings(player, "CAN_KICK",
					(button_id == 230150 ? SettingState.ONLY_ME : SettingState.get(button_id - 230149)), CAN_KICK);
			return true;
		}

		if (button_id >= 230160 && button_id <= 230167) {
			ClanManager.editSettings(player, "CAN_TOGGLE_LOOTSHARE", (button_id == 230160 ? SettingState.ONLY_ME
					: SettingState.get(button_id - 230159)), CAN_TOGGLE_LOOTSHARE);
			return true;
		}

		if (button_id >= 70224 && button_id <= 70324) {
			ClanManager.memberActions(player, "KICK_MEMBER", button_id);
			return true;
		}

		switch (button_id) {
		case 230120:
			player.setStringReceiver(1);
			player.write(new SendEnterStringInterface());
			return true;
		case 70215:
			leaveClan(player, false);
			return true;
		case 70212:
			clanSetup(player);
			return true;
		case 71074:
			ClanManager.memberActions(player, "LOOTSHARE_TOGGLE", button_id);
			break;
		}
		return false;
	}
}
