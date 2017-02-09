package com.model.game.character.player.packets.in;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.music.MusicData;
import com.model.game.character.player.packets.PacketType;
import com.model.game.item.ground.GroundItemHandler;

/**
 * Change Regions
 */
public class RegionChangePacketHandler implements PacketType {
	@Override
	public final void processPacket(Player player, int packetType, int packetSize) {
		GroundItemHandler.reloadGroundItems(player);
		Server.getGlobalObjects().updateRegionObjects(player);
		if (player.skullTimer > 0) {
			player.isSkulled = true;
			player.skullIcon = 0;
			player.getPA().requestUpdates();
		}
		if (player.isEnableMusic()) {
			MusicData.playMusic(player);
		}
		player.aggressionTolerance.reset();
	}
}
