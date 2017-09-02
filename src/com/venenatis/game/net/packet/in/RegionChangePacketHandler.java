package com.venenatis.game.net.packet.in;

import com.venenatis.game.content.sounds_and_music.MusicData;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.world.ground_item.GroundItemHandler;
import com.venenatis.server.Server;

/**
 * Change Regions
 */
public class RegionChangePacketHandler implements PacketType {
	@Override
	public final void handle(Player player, int packetType, int packetSize) {
		GroundItemHandler.reloadGroundItems(player);
		player.getAllotment().updateAllotmentsStates();
		Server.getGlobalObjects().updateRegionObjects(player);
		if (player.isEnableMusic()) {
			MusicData.playMusic(player);
		}
		player.aggressionTolerance.reset();
	}
}
