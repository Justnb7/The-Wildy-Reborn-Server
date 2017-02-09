
package com.model.game.character.player.packets.in;

import java.util.Objects;

import com.model.game.World;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.Trading;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;

/**
 * Trading
 */
public class Trade implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int tradeId = player.getInStream().readSignedWordBigEndian();
		Player requested = World.getWorld().getPlayers().get(tradeId);
		player.getPA().resetFollow();
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
			player.write(new SendMessagePacket("You cannot trade whilst inside the duel arena."));
			return;
		}
		if (Objects.equals(requested, player)) {
			player.write(new SendMessagePacket("You cannot trade yourself."));
			return;
		}
		if (tradeId != player.getIndex()) {
			Trading.request(player, requested);
		}
	}

}
