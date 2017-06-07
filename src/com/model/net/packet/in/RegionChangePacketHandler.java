package com.model.net.packet.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.content.music.MusicData;
import com.model.game.item.ground.GroundItemHandler;
import com.model.net.packet.PacketType;
import com.model.server.Server;

/**
 * Change Regions
 */
public class RegionChangePacketHandler implements PacketType {
	@Override
	public final void handle(Player player, int packetType, int packetSize) {
		GroundItemHandler.reloadGroundItems(player);
		Server.getGlobalObjects().updateRegionObjects(player);
		if (player.isEnableMusic()) {
			MusicData.playMusic(player);
		}
		player.aggressionTolerance.reset();
	}
}
