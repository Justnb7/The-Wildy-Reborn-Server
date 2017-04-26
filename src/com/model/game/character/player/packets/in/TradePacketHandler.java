package com.model.game.character.player.packets.in;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.game.item.container.impl.Trade;


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
