package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.clan.ClanRank;
import com.venenatis.game.net.packet.IncomingPacketListener;
import com.venenatis.server.Server;

public class ActionButtonWithMultipleActionsPacketHandler implements IncomingPacketListener {

	@Override
	public void handle(Player player, int packet, int size) {

		final int interfaceId = player.getInStream().readUnsignedShort();
		final int slot = player.getInStream().readUnsignedByte();
		final int actionId = player.getInStream().readUnsignedByte();
		
		if (interfaceId >= 28323 && interfaceId <= 28423) {
			if (actionId == 0) {
				return;
			}
			if (player.getClan() == null || !Server.getClanManager().clanExists(player.getClan().getFounder())) {
				player.message("You aren't in a clan chat.");
				return;
			}
			if (player.getClan().getRank(player.getUsername()) <= ClanRank.LEADER.getRankIndex()) {
				player.message("You don't have permissions to ban someone in this clan chat.");
				return;
			}
			if (actionId == 1) {
				player.getClan().demote(player.getClan().getRankedMemberAtIndex(interfaceId - 28323));
				return;
			}
			player.getClan().setRank(player.getClan().getRankedMemberAtIndex(interfaceId - 28323), actionId - 1);
			return;
		} else if (interfaceId >= 28425 && interfaceId <= 28525) {
			if (actionId == 0) {
				return;
			}
			if (player.getClan() == null || !Server.getClanManager().clanExists(player.getClan().getFounder())) {
				player.message("You aren't in a clan chat.");
				return;
			}
			if (!player.getClan().canBan(player.getUsername())) {
				player.message("You don't have permission to unban someone in this clan chat.");
				return;
			}
			player.getClan().unbanMember(interfaceId - 28425);
			return;
		} else if (interfaceId >= 28144 && interfaceId <= 28244) {
			if (actionId == 0) {
				return;
			}
			if (player.getClan() == null || !Server.getClanManager().clanExists(player.getClan().getFounder())) {
				player.message("You aren't in a clan chat.");
				return;
			}
			if (actionId == 1) {
				if (!player.getClan().canKick(player.getUsername())) {
					player.message("You don't have permission to unban someone in this clan chat.");
					return;
				}
				player.getClan().kickMember(player.getClan().getActiveMemberAtIndex(interfaceId - 28144));
			} else if (actionId == 2) {
				if (!player.getClan().canBan(player.getUsername())) {
					player.message("You don't have permission to unban someone in this clan chat.");
					return;
				}
				player.getClan().banMember(player.getClan().getActiveMemberAtIndex(interfaceId - 28144));
			}
			return;
		}

		switch (interfaceId) {
			
		case 28304:
			if (actionId != 1) {
				return;
			}
			if (player.getClan().getRank(player.getUsername()) <= ClanRank.LEADER.getRankIndex()) {
				player.message("You don't have permissions to delete this clan chat.");
				return;
			}
			player.getClan().delete();
			
		case 28307:
			if (actionId == 0) {
				return;
			}
			if (player.getClan().getRank(player.getUsername()) <= ClanRank.LEADER.getRankIndex()) {
				player.message("You don't have permissions to change these settings in this clan chat.");
				return;
			}
			player.getClan().setRankCanJoin(actionId - 2);
			break;
			
		case 28310:
			if (actionId == 0) {
				return;
			}
			if (player.getClan().getRank(player.getUsername()) <= ClanRank.LEADER.getRankIndex()) {
				player.message("You don't have permissions to change these settings in this clan chat.");
				return;
			}
			player.getClan().setRankCanTalk(actionId - 2);
			break;
			
		case 28313:
			if (actionId == 0) {
				return;
			}
			if (player.getClan().getRank(player.getUsername()) <= ClanRank.LEADER.getRankIndex()) {
				player.message("You don't have permissions to change these settings in this clan chat.");
				return;
			}
			player.getClan().setRankCanKick(actionId);
			break;
			
		case 28316:
			if (actionId == 0) {
				return;
			}
			if (player.getClan().getRank(player.getUsername()) <= ClanRank.LEADER.getRankIndex()) {
				player.message("You don't have permissions to change these settings in this clan chat.");
				return;
			}
			player.getClan().setRankCanBan(actionId);
			break;
	
		default:
			System.out.println("[ActionButtonWithMultipleActions] Unhandled actions for interfaceId: " + interfaceId + ", actionId: " + actionId);
			break;

		}

	}

}