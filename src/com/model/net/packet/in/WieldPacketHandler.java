package com.model.net.packet.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.Rights;
import com.model.game.character.player.Skills;
import com.model.game.definitions.EquipmentDefinition;
import com.model.game.definitions.EquipmentDefinition.EquipmentType;
import com.model.game.item.Item;
import com.model.game.item.container.InterfaceConstants;
import com.model.net.packet.PacketType;
import com.model.net.packet.SubPacketType;

/**
 * Handles the 'wield' option on items.
 * 
 * @author Patrick van Elderen
 * 
 */
public class WieldPacketHandler implements SubPacketType {

	@Override
	public void processSubPacket(Player player, int packetType, int packetSize) {
		final int id = player.getInStream().readUnsignedWord();
		final int slot = player.getInStream().readUnsignedWordA();
		final int interfaceId = player.getInStream().readUnsignedWordA();
		
		if (player.getRights().equals(Rights.ADMINISTRATOR) && player.inDebugMode()) {
			player.debug(String.format("[WieldPacketHandler] [id= %d] [slot= %d] [interface %d]", id, slot, interfaceId));
		}
		
		if (player.isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0 || player.isTeleporting()) {
			return;
		}
		
		switch (interfaceId) {

		case InterfaceConstants.INVENTORY_INTERFACE:
			final Item item = player.getInventory().get(slot);

			if (item == null || item.getId() != id) {
				return;
			}

			final EquipmentDefinition equip = EquipmentDefinition.EQUIPMENT_DEFINITIONS.get(item.getId());

			if (equip == null) {
				return;
			}

			if (!player.getController().canEquip(player, id, slot)) {
				return;
			}
			
			if (equip.getType() == EquipmentType.NOT_WIELDABLE) {
				player.getActionSender().sendMessage("This item cannot be worn.");
				return;
			}

			player.getEquipment().wear(item, slot);
		}
	}

}
