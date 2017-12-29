package com.venenatis.game.model.entity.player.clan;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.server.Server;

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
		
		case 109225:
			if (player.getClan() == null) {
				player.enterXInterfaceId = 6969;
			} else if (player.getClan().isFounder(player.getUsername())) {
				Server.getClanManager().getClan(player.getLastClanChat()).delete();
			} else {
				Server.getClanManager().getClan(player.getLastClanChat()).removeMember(player);
			}
			break;
			
		case 109228:
			player.enterXInterfaceId = 28300;
			break;
			
		case 111112:
			if (player.getClan() == null) {
				player.message("You need to be in a clan to do this.");
			} else if (!player.getClan().isFounder(player.getUsername()) && !player.getClan().isGeneral(player.getUsername())) {
				player.message("Only the owner of the clan can toggle lootshare.");
			} else {
				player.getClan().setLootShare(!player.getClan().isLootShare());
			}
			break;

		}

		return false;

	}

}
