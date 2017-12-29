package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.clan.Clan;
import com.venenatis.game.model.entity.player.clan.ClanRank;
import com.venenatis.game.net.packet.IncomingPacketListener;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.server.Server;

public class StringInputClanPacketHandler implements IncomingPacketListener {

	@Override
	public void handle(Player player, int packet, int size) {
		String text = player.getInStream().readString();
		int index = text.indexOf(",");
		int id = Integer.parseInt(text.substring(0, index));
		String string = text.substring(index + 1);
		switch (id) {
		case 1:
			if (string.length() == 0) {
				break;
			} else if (string.length() > 15) {
				string = string.substring(0, 15);
			}
			Clan clan = player.getClan();
			if (clan == null) {
				Server.getClanManager().create(player);
				clan = player.getClan();
			}
			if (clan != null) {
				if (clan.getFounder().equalsIgnoreCase(player.getUsername()) || clan.isGeneral(player.getUsername())) {
					clan.setTitle(string);
					clan.save();
				} else {
					player.message("Only the owner of the clan chat can do this.");
				}
			}
			break;
		case 2:
			if (string.length() == 0) {
				break;
			} else if (string.length() > 12) {
				string = string.substring(0, 12);
			}
			if (string.equalsIgnoreCase(player.getUsername())) {
				break;
			}
			clan = player.getClan();
			if (clan.getRank(player.getUsername()) <= ClanRank.LEADER.getRankIndex()) {
				player.message("You don't have permissions to promote someone in this clan chat.");
				break;
			}
			if (World.getWorld().lookupPlayerByName(string) == null) {
				player.message("You can't promote an offline player.");
				break;
			}
			if (clan.isRanked(string)) {
				player.message("This player is already ranked.");
				break;
			}
			if (clan.isBanned(string)) {
				player.message("You cannot give a banned member a rank.");
				break;
			}
			if (clan != null) {
				clan.setRank(Utility.formatName(string), 1);
			}
			break;
		case 3:
			if (string.length() == 0) {
				break;
			} else if (string.length() > 12) {
				string = string.substring(0, 12);
			}
			if (string.equalsIgnoreCase(player.getUsername())) {
				break;
			}
			clan = player.getClan();
			if (!clan.canBan(player.getUsername())) {
				player.message("You don't have permission to ban a member of this clan chat channel.");
				break;
			}
			if (World.getWorld().lookupPlayerByName(Utility.formatName(string)) == null) {
				player.message("You can't ban an offline player.");
				break;
			}
			if (clan.getRank(player.getUsername()) <= clan.getRank(string)) {
				player.message("You can't ban someone ranked the same or higher than you.");
				break;
			}
			if (clan != null) {
				clan.banMember(Utility.formatName(string));
			}
			break;
		default:
			System.out.println("Received string: identifier=" + id + ", string=" + string);
			break;
		}
	}
}