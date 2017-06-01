package com.model.net.packet.in;

import com.model.game.character.player.Player;
import com.model.net.packet.PacketType;

/**
 * A packet sent when the player enters a custom amount for banking etc.
 * @author Patrick van Elderen and original PI creators
 *
 */
public class EnterAmountPacketHandler implements PacketType {
	@Override
	public void handle(Player player, int packetType, int packetSize) {
		int amount = player.getInStream().readDWord();
		
		if (amount <= 0) {
			amount = 0;
		}
		if (player.getArea().inWild()) {
			return;
		}
		if (player.dialogue().isActive()) {
			if (player.dialogue().input(amount)) {
				return;
			}
		}
	}
}