package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.clan.ClanManager;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.world.World;
import com.venenatis.server.Server;

public class InputFieldPacketHandler implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		
		final int component = player.inStream.readDWord();
		final String text = player.inStream.readString();
		
		if (component < 0 || text == null || text.length() < 0) {
			return;
		}

		player.debug(String.format("[InputFieldPacketListener] Component: %d | Text: %s%n",component, text));
		
		switch (component) {
		
		/* Preset */
		case 57034:
			player.getPresets().setTitle(player.getPresets().getViewing(), text);
		break;
		
		/* Clan Chat */
		case 47828:
			ClanManager.kickMember(player, text);
			break;
			
		case 47830:
			if (World.getWorld().getPlayerByName(text).isPresent()) {
				Player other = World.getWorld().getPlayerByName(text).get();
				player.setClanPromote(other.getUsername());
			}
			break;
			
		case 47843:
			ClanManager.changeSlogan(player, text);
			break;
			
		case 47845:
			int amount = Integer.parseInt(text);
			ClanManager.setMemberLimit(player, amount);
			break;
		
		case 42521:
			Server.getDropManager().search(player, text);
			break;
	
			default:
				break;
		}
	}

}
