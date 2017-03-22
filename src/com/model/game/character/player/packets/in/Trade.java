package com.model.game.character.player.packets.in;

import com.model.game.World;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.PacketType;

import java.util.Objects;

/**
 * Trading
 */
public class Trade implements PacketType {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		int tradeId = player.getInStream().readSignedWordBigEndian();
		Player requested = World.getWorld().getPlayers().get(tradeId);
		player.setFollowing(null);
		if (requested == null) {
			return;
		}
		if (!player.isActive()) {
			player.tradeStatus = 0;
		}
		if (tradeId == player.getIndex()) {
			return;
		}
		if (Boundary.isIn(player, Boundary.DUEL_ARENAS)) {
			player.getActionSender().sendMessage("You cannot trade whilst inside the duel arena.");
			return;
		}
		if (Objects.equals(requested, player)) {
			player.getActionSender().sendMessage("You cannot trade yourself.");
			return;
		}
		if (tradeId != player.getIndex()) {
			Trading.request(player, requested);
		}
	}

}
