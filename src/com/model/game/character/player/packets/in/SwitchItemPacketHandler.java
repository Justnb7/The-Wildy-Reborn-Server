package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.PacketType;

/**
 * Switch item packet handler.
 * @author Patrick van Elderen
 *
 */
public class SwitchItemPacketHandler implements PacketType {

	@Override
	public void handle(Player player, int id, int size) {
		int interfaceId = player.getInStream().readUnsignedWordBigEndianA();
		boolean insertMode = player.getInStream().readSignedByteC() == 1;
		int fromSlot = player.getInStream().readUnsignedWordBigEndianA();
		int toSlot = player.getInStream().readUnsignedWordBigEndian();
		
		if (Trading.isTrading(player)) {
        	Trading.decline(player);
        }
		
		if (player.isBusy()) {
			return;
		}
		
		//Stop active skilling tasks
		player.stopSkillTask();
		
		player.getItems().swap(fromSlot, toSlot, interfaceId, insertMode);
	}
}