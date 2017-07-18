package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.container.impl.InterfaceConstants;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;

/**
 * Switch item packet handler.
 * @author Patrick van Elderen
 *
 */
public class SwitchItemPacketHandler implements PacketType {

	@Override
	public void handle(Player player, int id, int size) {
		int interfaceId = player.getInStream().readUnsignedWordBigEndianA();
		final int inserting = player.getInStream().readSignedByteC();
		int fromSlot = player.getInStream().readUnsignedWordBigEndianA();
		int toSlot = player.getInStream().readUnsignedWordBigEndian();
		
		player.debug(String.format("SwitchItemPacketHandler: interfaceId %d - fromSlot %d - toSlot %d%n", interfaceId, fromSlot, toSlot));
		
		if (interfaceId < 0 || fromSlot < 0 || toSlot < 0)
            return;
		
		switch(interfaceId) {
			
		case InterfaceConstants.INVENTORY_INTERFACE:
		case InterfaceConstants.INVENTORY_STORE:
		    player.getInventory().swap(fromSlot, toSlot);
		    break;
		    
		case InterfaceConstants.WITHDRAW_BANK:
			if (inserting == 2) {
				player.getBank().itemToTab(fromSlot, toSlot, true);
			} else if (inserting == 1) {
				player.getBank().insert(fromSlot, toSlot);
				final int fromTab = player.getBank().getData(fromSlot, 0);
				final int toTab = player.getBank().getData(toSlot, 0);

				if (fromTab != toTab) {
					player.getBank().changeTabAmount(fromTab, -1, true);
					player.getBank().changeTabAmount(toTab, 1, true);
					player.getBank().refresh();
				}
			} else {
				player.getBank().swap(fromSlot, toSlot);
			}
			break;
			
		}
		
		//Stop active skilling tasks
		player.stopSkillTask();
		
	}
}