package com.venenatis.game.net.packet.in;

import com.venenatis.game.content.option_menu.TeleportMenuHandler;
import com.venenatis.game.content.staff_control_panel.StaffControlPanel;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.IncomingPacketListener;

/**
 * Handles selected options for the OptionMenu Interface.
 * 
 * @author Lennard
 *
 */
public class OptionMenuPacketHandler implements IncomingPacketListener {

	@Override
	public void handle(Player player, int id, int size) {
		final int childId = player.getInStream().readUnsignedShort();
		final int identifier = player.getInStream().readUnsignedByte();
		final int optionType = player.getInStream().readUnsignedByte();

		switch (childId) {

		case TeleportMenuHandler.TELEPORT_MENU_CHILD_ID:

			switch (optionType) {

			// Teleport
			case 0:
				player.getTeleportMenuHandler().handleTeleportClick(identifier);
				break;

			// Add favorite
			case 1:
				player.getTeleportMenuHandler().addFavoriteTeleport(identifier);
				break;

			default:
				System.out.println("TeleportMenu Unhandled Option Type!");
				break;

			}

			break;
			
		case TeleportMenuHandler.FAVORITE_MENU_CHILD_ID:
			switch (optionType) {

			// Teleport
			case 0:
				player.getTeleportMenuHandler().handleTeleportClick(identifier);
				break;

			// Remove favorite
			case 1:
				player.getTeleportMenuHandler().removeFavoriteTeleport(identifier);
				break;
			
			// Move up favorite
			case 2:
				player.getTeleportMenuHandler().moveUpFavoriteTeleport(identifier);
				break;
			
			//Move down favorite
			case 3:
				player.getTeleportMenuHandler().moveDownFavoriteTeleport(identifier);
				break;

			default:
				System.out.println("FavoriteMenu Unhandled Option Type!");
				break;

			}
			break;
			
		case StaffControlPanel.STAFF_PANEL_OPTION_MENU_IDENTIFIER:
			player.getStaffControlPanel().handleOptionMenuClick(identifier, optionType);
			break;

		default:
			System.out.println("OptionMenu[childId: " + childId + " ,identifier: " + identifier + ", optionType: " + optionType + "] ");
			break;
		}

	}

}