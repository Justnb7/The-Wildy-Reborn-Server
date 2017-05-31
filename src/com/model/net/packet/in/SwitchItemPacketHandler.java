package com.model.net.packet.in;

import com.model.game.character.player.Player;
import com.model.game.item.container.InterfaceConstants;
import com.model.game.item.container.impl.Bank;
import com.model.net.packet.PacketType;

/**
 * Switch item packet handler.
 * @author Patrick van Elderen
 *
 */
public class SwitchItemPacketHandler implements PacketType {

	@Override
	public void handle(Player player, int id, int size) {
		int interfaceId = player.getInStream().readUnsignedWordBigEndianA();
		@SuppressWarnings("unused")
		boolean insertMode = player.getInStream().readSignedByteC() == 1;
		int fromSlot = player.getInStream().readUnsignedWordBigEndianA();
		int toSlot = player.getInStream().readUnsignedWordBigEndian();
		
		if (player.inDebugMode()) {
			System.out.printf("SwitchItemPacketHandler: interfaceId %d - fromSlot %d - toSlot %d%n", interfaceId, fromSlot, toSlot);
		}
		
		if (interfaceId < 0 || fromSlot < 0 || toSlot < 0)
            return;
		
		switch(interfaceId) {
			
		case InterfaceConstants.INVENTORY_INTERFACE:
		case InterfaceConstants.INVENTORY_STORE:
		    player.getInventory().swap(fromSlot, toSlot);
		    break;
			
		case Bank.BANK_INVENTORY_INTERFACE:
			if(fromSlot >= 0 && fromSlot < Bank.SIZE && toSlot >= 0 && toSlot < Bank.SIZE && toSlot != fromSlot) {
				if(player.isInsertItem()) {
					player.getBank().swap(fromSlot, toSlot);
				} else {
					player.getBank().transfer(fromSlot, toSlot);
				}
			}
			break;
		}
		
		//Stop active skilling tasks
		player.stopSkillTask();
		
	}
}