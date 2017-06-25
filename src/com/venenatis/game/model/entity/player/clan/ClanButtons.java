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
		switch (button) {
		
		case 184163:
			player.getActionSender().removeAllInterfaces();
			return true;

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
		case 186207:
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
			
		case 187135:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.FRIEND);
			break;
			
		case 187134:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.RECRUIT);
			break;
			
		case 187133:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.CORPORAL);
			break;
			
		case 187132:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.SERGEANT);
			break;
			
		case 187131:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.LIEUTENANT);
			break;
			
		case 187130:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.CAPTAIN);
			break;
			
		case 187129:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setJoinable(ClanRank.GENERAL);
			break;
			
		case 187128:
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
			
		case 187145:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.FRIEND);
			break;
			
		case 187144:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.RECRUIT);
			break;
			
		case 187143:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.CORPORAL);
			break;
			
		case 187142:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.SERGEANT);
			break;
			
		case 187141:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.LIEUTENANT);
			break;
			
		case 187140:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.CAPTAIN);
			break;
			
		case 187139:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setTalkable(ClanRank.GENERAL);
			break;
			
		case 187138:
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
			
		case 187154:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setManagable(ClanRank.RECRUIT);
			break;
			
		case 187153:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setManagable(ClanRank.CORPORAL);
			break;
			
		case 187152:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setManagable(ClanRank.SERGEANT);
			break;
			
		case 187151:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setManagable(ClanRank.LIEUTENANT);
			break;
			
		case 187150:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setManagable(ClanRank.CAPTAIN);
			break;
			
		case 187149:
			if (player.getClan() == null) {
				return true;
			}
			player.getClan().setManagable(ClanRank.GENERAL);
			break;

		/* Promote */
		case 186224:
			if (player.getClan() == null) {
				return true;
			}
			ClanManager.promote(player, ClanRank.ANYONE);
			break;
			
		case 186223:
			if (player.getClan() == null) {
				return true;
			}
			ClanManager.promote(player, ClanRank.RECRUIT);
			break;
			
		case 186222:
			if (player.getClan() == null) {
				return true;
			}
			ClanManager.promote(player, ClanRank.CORPORAL);
			break;
			
		case 186221:
			if (player.getClan() == null) {
				return true;
			}
			ClanManager.promote(player, ClanRank.SERGEANT);
			break;
			
		case 186220:
			if (player.getClan() == null) {
				return true;
			}
			ClanManager.promote(player, ClanRank.LIEUTENANT);
			break;
			
		case 186219:
			if (player.getClan() == null) {
				return true;
			}
			ClanManager.promote(player, ClanRank.CAPTAIN);
			break;
			
		case 186218:
			if (player.getClan() == null) {
				return true;
			}
			ClanManager.promote(player, ClanRank.GENERAL);
			break;

		}

		return false;

	}

}
