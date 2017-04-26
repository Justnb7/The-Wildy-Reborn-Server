package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.PacketType;
import com.model.game.item.container.impl.Inventory;

/**
 * Switch item packet handler.
 * @author Patrick van Elderen
 *
 */
public class SwitchItemPacketHandler implements PacketType {

	@Override
	public void handle(Player player, int id, int size) {
		int interfaceId = player.getInStream().readUnsignedWordBigEndianA();
		int fromSlot = player.getInStream().readUnsignedWordBigEndianA();
		int toSlot = player.getInStream().readUnsignedWordBigEndian();
		
		if (Trading.isTrading(player)) {
        	Trading.decline(player);
        }
		
		switch(interfaceId) {
		case Inventory.INTERFACE:
			if(fromSlot >= 0 && fromSlot < Inventory.SIZE && toSlot >= 0 && toSlot < Inventory.SIZE && toSlot != fromSlot) {
				player.getInventory().swap(fromSlot, toSlot);
			}
			break;
		}
		
		//Stop active skilling tasks
		player.stopSkillTask();
		
		//player.getItems().swap(fromSlot, toSlot, interfaceId, insertMode);
	}
}