package com.venenatis.game.net.packet.in;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.text.WordUtils;

import com.venenatis.game.content.help.HelpDatabase;
import com.venenatis.game.content.help.HelpRequest;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.IncomingPacketListener;
import com.venenatis.game.world.World;

public class InputFieldPacketHandler implements IncomingPacketListener {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		
		final int component = player.inStream.readDWord();
		final String text = player.inStream.readString();
		
		if (component < 0 || text == null || text.length() < 0) {
			return;
		}

		player.debug(String.format("[InputFieldPacketListener] Component: %d | Text: %s%n",component, text));
		
		switch (component) {
		
		case 42306:
			player.getStaffControlPanel().handleInputField(text);
			break;
		
		case 42152:
			player.getTeleportMenuHandler().handleTeleportInput(text);
			break;
		
		case 59527:
			if (text.length() < 25) {
				player.getActionSender().sendMessage("Your help request must contain 25 characters for the description.");
				return;
			}
			List<Player> staff = World.getWorld().getPlayers().stream().filter(Objects::nonNull).filter(p -> p.getRights().isStaffMember(player)).collect(Collectors.toList());
			if (HelpDatabase.getDatabase().requestable(player)) {
				HelpDatabase.getDatabase().add(new HelpRequest(player.getUsername(), player.getHostAddress(), text));
				if (staff.size() > 0) {
					World.getWorld().sendMessage("[HelpDB] " + WordUtils.capitalize(player.getUsername()) + "" + " is requesting help, type ::helpdb to view their request.", staff);
					player.getActionSender().sendMessage("You request has been sent, please wait as a staff member gets back to you.");
				} else {
					player.getActionSender().sendMessage("There are no staff online to help you at this time, please be patient.");
				}
			}
			player.getActionSender().removeAllInterfaces();
			break;
		
		/* Preset */
		case 57034:
			player.getPresets().setTitle(player.getPresets().getViewing(), text);
		break;
	
			default:
				break;
		}
	}

}
