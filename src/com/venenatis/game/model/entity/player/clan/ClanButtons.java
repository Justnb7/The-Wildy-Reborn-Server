package com.venenatis.game.model.entity.player.clan;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.input.InputString;

/**
 * Handles the buttons for clan chat.
 * 
 * @author Daniel
 *
 */
public class ClanButtons {

	/**
	 * Handles clicking the buttons on interface.
	 * 
	 * @param player
	 * @param button
	 * @return
	 */
	public static boolean handle(Player player, int button) {
		player.debug("button: "+button);
		switch (button) {

		/* Joining */
		case 132014:
			if (player.getClan() != null) {
				player.getActionSender().sendMessage("You can't join a clan chat channel while in one!");
				return true;
			}
			player.getActionSender().sendInput((InputString) input -> ClanManager.join(player, input));
			return true;

		/* Leaving */
		case 132017:
			ClanManager.leave(player, false);
			return true;

		/* Clan Management */
		case 132020:
			ClanManager.manage(player);
			return true;

		/* Name Set */
		case 184148:
			if (player.getClan() == null) {
				return true;
			}
			player.getActionSender().sendInput((InputString) input -> ClanManager.changeName(player, input));
			return true;

		/* Lootshare */
		case 186205:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setLootshare(player.getClan().isLootshare());
			break;

		/* System Message */
		case -17713:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setLocked(player.getClan().getLocked());
			break;

		/* Who Can Enter */
		case 184151:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.ANYONE);
			break;
			
		case -17529:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.FRIEND);
			break;
			
		case -17530:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.RECRUIT);
			break;
			
		case -17531:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.CORPORAL);
			break;
			
		case -17532:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.SERGEANT);
			break;
			
		case -17533:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.LIEUTENANT);
			break;
			
		case -17534:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.CAPTAIN);
			break;
			
		case -17535:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.GENERAL);
			break;
			
		case -17536:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.LEADER);
			break;

		/* Who Can Talk */
		case 184154:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.ANYONE);
			break;
			
		case -17519:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.FRIEND);
			break;
			
		case -17520:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.RECRUIT);
			break;
			
		case -17521:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.CORPORAL);
			break;
			
		case -17522:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.SERGEANT);
			break;
			
		case -17523:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.LIEUTENANT);
			break;
			
		case -17524:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.CAPTAIN);
			break;
			
		case -17525:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.GENERAL);
			break;
			
		case -17526:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.LEADER);
			break;

		/* Who Can Manage */
		case 184157:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setManagable(ClanRank.LEADER);
			break;
			
		case -17510:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setManagable(ClanRank.RECRUIT);
			break;
			
		case -17511:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setManagable(ClanRank.CORPORAL);
			break;
			
		case -17512:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setManagable(ClanRank.SERGEANT);
			break;
			
		case -17513:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setManagable(ClanRank.LIEUTENANT);
			break;
			
		case -17514:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setManagable(ClanRank.CAPTAIN);
			break;
			
		case -17515:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setManagable(ClanRank.GENERAL);
			break;

		/* Promote */
		case -17696:
			if (player.getClan() == null) {
				return true;
			}
			ClanManager.promote(player, ClanRank.ANYONE);
			break;
			
		case -17697:
			if (player.getClan() == null) {
				return true;
			}
			ClanManager.promote(player, ClanRank.RECRUIT);
			break;
			
		case -17698:
			if (player.getClan() == null) {
				return true;
			}
			ClanManager.promote(player, ClanRank.CORPORAL);
			break;
			
		case -17699:
			if (player.getClan() == null) {
				return true;
			}
			ClanManager.promote(player, ClanRank.SERGEANT);
			break;
			
		case -17700:
			if (player.getClan() == null) {
				return true;
			}
			ClanManager.promote(player, ClanRank.LIEUTENANT);
			break;
			
		case -17701:
			if (player.getClan() == null) {
				return true;
			}
			ClanManager.promote(player, ClanRank.CAPTAIN);
			break;
			
		case -17702:
			if (player.getClan() == null) {
				return true;
			}
			ClanManager.promote(player, ClanRank.GENERAL);
			break;

		}

		return false;

	}

}
