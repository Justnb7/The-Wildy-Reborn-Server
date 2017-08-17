package com.venenatis.game.model.entity.player.clan;

import java.util.Arrays;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

/**
 * Class handles the management of clans.
 * 
 * @author Daniel
 * @author Michael
 * 
 */
public class ClanManager {

	public static Clan getClan(Player player) {
		String name = player.getUsername().toLowerCase().trim();
		
		Clan clan = ClanRepository.get(name);

		if (clan != null) {
			return ClanRepository.get(name);
		}

		clan = new Clan(name);
		
		clan.init();

		if (World.getWorld().getPlayerByName(name).isPresent()) {
			clan.add(new ClanMember(World.getWorld().getPlayerByName(name).get(), ClanRank.LEADER));
		}

		ClanRepository.add(clan);
		ClanRepository.save();
		
		player.getActionSender().sendMessage("You have successfully created your clan.");

		return clan;
	}

	public static void join(Player player, String name) {
		if (name == null || name.length() == 0 || player.getClan() != null) {
			return;
		}
		
		player.getActionSender().sendMessage("Attempting to join channel...");

		Clan clan = ClanRepository.get(name);

		if (clan == null) {
			player.getActionSender().sendMessage("This channel does not exist.");
			return;
		}
		
		ClanMember member = new ClanMember(player, ClanRank.ANYONE);
		
		clan.setRank(member);

		if (!clan.canJoin(member)) {
			player.getActionSender().sendMessage("You do not have sufficient permisson to join this channel!");
			return;
		}

		if (!clan.add(member)) {
			player.getActionSender().sendMessage("This channel is currently full!");
			return;
		}
		
		player.setClan(clan);
		player.setClanChat(clan.getOwner());

		player.getActionSender().sendString("</col>Talking in: <col=FFFF64><shad=0>" + Utility.formatName(clan.getName()), 33802);
		player.getActionSender().sendString("</col>Owner: <col=ffffff>" + Utility.formatName(clan.getOwner()), 33803);
		player.getActionSender().sendString("</col>Slogan: <col=ffffff>" + Utility.capitalizeSentence(clan.getSlogan()), 33816);

		player.getActionSender().sendMessage("Now talking in clan chat <col=FFFF64><shad=0>" + Utility.formatName(clan.getName()) + "</shad></col>.");
		player.getActionSender().sendMessage("To talk, start each line of chat with the / symbol.");

		update(clan);
	}

	public static void leave(Player player, boolean save) {
		Clan clan = player.getClan();

		if (clan == null) {
			player.getActionSender().sendMessage("You are not currently in a clan chat channel.");
			return;
		}

		player.getActionSender().sendString("</col>Talking in: <col=ffffff>None", 33802);
		player.getActionSender().sendString("</col>Owner: <col=ffffff>None", 33803);
		player.getActionSender().sendString("</col>Slogan: <col=ffffff>None", 33816);
		player.getActionSender().sendString("", 33815);

		player.getActionSender().sendMessage("You have left the clan chat channel.");

		for (int i = 0; i < 50; i++) {
			player.getActionSender().sendString("", 33821 + i);
		}

		if (save) {
			player.setSavedClan(player.getClan().getOwner());
		}

		clan.remove(clan.get(player.getUsername()));
		player.setClan(null);
		update(clan);
	}

	public static void message(Player player, String message) {
		Clan clan = player.getClan();

		if (clan == null) {
			player.getActionSender().sendMessage("You are not currently in a clan chat channel.");
			return;
		}

		ClanMember member = clan.get(player.getUsername());
		ClanRank rank = member.getRank();

		if (!clan.canTalk(member)) {
			player.getActionSender().sendMessage("You do not have sufficient permisson to talk in this channel!");
			return;
		}

		for (ClanMember other : clan.members()) {
			other.getPlayer().getActionSender().sendClanDetails(Utility.formatName(player.getUsername()), Utility.capitalizeSentence(message), Utility.formatName(clan.getName()), rank);
		}
	}

	public static void systemMessage(Clan clan, String message) {
		for (ClanMember member : clan.members()) {
			member.getPlayer().getActionSender().sendClanDetails(Utility.capitalizeSentence(message), Utility.formatName(clan.getName()));
		}
	}

	private static void update(Clan clan) {
		for (ClanMember member : clan.members()) {
			Player player = member.getPlayer();

			player.getActionSender().sendString("</col>Talking in: <col=FFFF64><shad=0>" + Utility.formatName(clan.getName()), 33802);
			player.getActionSender().sendString(clan.members().size() + "/" + clan.getMemberLimit(), 33815);
			
			int index = 0;
			for (int i = 0; i < 50 - index; i++) {
				player.getActionSender().sendString("", 33821 + i);
			}

			for (ClanMember other : clan.members()) {
				String name = Utility.formatName(other.getName());

				if (other.getRank() != ClanRank.ANYONE) {
					String rank = "<clan=" + other.getRank().getRankIndex() + "> ";
					player.getActionSender().sendString(rank.concat(name), 33821 + index++);
				} else {
					player.getActionSender().sendString(name, 33821 + index++);
				}
			}

		}
	}

	public static void changeName(Player player, String input) {
		if (Arrays.stream(Constants.BAD_STRINGS).anyMatch($it -> input.contains($it))) {
			player.getActionSender().sendMessage("That name is not permitted!");
			return;
		}

		Clan clan = player.getClan();
		
		if (clan == null) {
			return;
		}

		clan.setName(input);

		systemMessage(clan, Utility.formatName(player.getUsername()) + " has changed the clan name.");

		for (ClanMember member : clan.members()) {
			member.getPlayer().getActionSender().sendString("</col>Talking in: <col=FFFF64><shad=0>" + Utility.formatName(clan.getName()), 33802);
		}
		
		player.getActionSender().sendString(Utility.formatName(clan.getName()), 47814);

		ClanRepository.save();
	}

	public static void changeSlogan(Player player, String input) {
		boolean allowed = true;

		for (String bad : Constants.BAD_STRINGS) {
			if (input.contains(bad)) {
				allowed = false;
			}
		}

		if (!allowed) {
			player.getActionSender().sendMessage("Your slogan consisted of some words that were inappropriate!");
			return;
		}

		Clan clan = player.getClan();

		if (clan == null) {
			return;
		}

		clan.setSlogan(Utility.capitalizeSentence(input));

		systemMessage(clan, Utility.formatName(player.getUsername()) + " has changed the clan slogan.");

		for (ClanMember member : clan.members()) {
			member.getPlayer().getActionSender().sendString("</col>Slogan: <col=ffffff>" + Utility.capitalizeSentence(clan.getSlogan()), 33816);
		}

		ClanRepository.save();
	}

	public static void setMemberLimit(Player player, int amount) {
		if (amount > 99) {
			player.getActionSender().sendMessage("You can only have a maximum of 99 clan members.");
			return;
		}

		Clan clan = player.getClan();

		clan.setMemberLimit(amount);

		systemMessage(clan, Utility.formatName(player.getUsername()) + " has changed the clan member limit.");

		for (ClanMember member : clan.members()) {
			member.getPlayer().getActionSender().sendString(clan.members().size() + "/" + clan.getMemberLimit(), 33815);
		}

	}

	public static void manage(Player player) {
		if (player.getClan() == null) {
			getClan(player);
			return;
		}
		
		String name = player.getUsername().toLowerCase().trim();
		
		Clan clan = ClanRepository.get(name);
		
		ClanMember member = clan.get(player.getUsername());

		if (!clan.getOwner().equalsIgnoreCase(player.getUsername())) {
			if (!clan.canManage(member)) {
				player.getActionSender().sendMessage("You do not have sufficient permisson to manage this channel!");
				return;
			}
		}

		int index = 0;
		for (int i = index; i < 50; i++) {
			player.getActionSender().sendString("", 44001 + index);
			player.getActionSender().sendString("", 44801 + index);
		}

		for (ClanMember other : clan.members()) {
			player.getActionSender().sendString(Utility.formatName(other.getName()), 44001 + index);
			player.getActionSender().sendString(
					"<clan=" + other.getRank().getRankIndex() + ">" + other.getRank().getName(), 44801 + index);
			index++;
		}

		player.getActionSender().sendString(Utility.formatName(clan.getName()), 47814);

		player.getActionSender().sendInterface(40172);
	}

	public static void kickMember(Player player, String name) {
		Clan clan = player.getClan();

		if (clan == null) {
			player.getActionSender().sendMessage("You are not in a clan channel.");
			return;
		}

		if (World.getWorld().getPlayerByName(name).isPresent()) {
			Player victim = World.getWorld().getPlayerByName(name).get();

			ClanMember member = clan.get(player.getUsername());

			if (!clan.canKick(member)) {
				player.getActionSender().sendMessage("You do not have sufficient permisson to kick in this channel!");
				return;
			}

			if (!clan.contains(victim.getUsername())) {
				player.getActionSender().sendMessage(Utility.formatName(name) + " is not in this channel.");
				return;
			}

			leave(victim, false);
			victim.getActionSender().sendMessage(Utility.formatName(player.getUsername()) + " has kicked you from the clan channel.");
			player.getActionSender().sendMessage("You have successfully kicked " + Utility.formatName(name) + " from the clan channel.");
		}

	}

	public static void promote(Player player, ClanRank rank) {
		player.getActionSender().sendString(rank.getName(), 47841);

		Clan clan = player.getClan();

		if (clan == null) {
			clan = ClanRepository.get(player.getUsername());

			if (clan == null) {
				player.getActionSender().sendMessage("You are not in a clan channel.");
				return;
			}
		}

		if (player.getClanPromote() == null) {
			player.getActionSender().sendMessage("You have not chosen a member to promote!");
			return;
		}

		ClanMember other = clan.get(player.getClanPromote());

		if (other == null) {
			player.getActionSender().sendMessage("This player is not in this channel.");
			return;
		}

		if (!clan.contains(other.getName())) {
			player.getActionSender().sendMessage(Utility.formatName(other.getName()) + " is not in your clan channel.");
			return;
		}

		other.setRank(rank);
		clan.add(other);
		update(clan);
		manage(player);
		ClanRepository.save();
	}

}