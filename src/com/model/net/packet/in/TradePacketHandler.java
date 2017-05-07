package com.model.net.packet.in;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.item.container.impl.Trade;
import com.model.net.packet.PacketType;


public class TradePacketHandler implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		
		int index = player.getInStream().readSignedWordBigEndian();
		Player other = (Player) World.getWorld().getPlayers().get(index);
		
		if (other == null) {
			return;
		}
		Trade.requestTrade(player, other);
	}

}
