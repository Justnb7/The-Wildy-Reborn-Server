package com.venenatis.game.net.packet.in;

import com.venenatis.game.content.activity.minigames.impl.duelarena.DuelRule;
import com.venenatis.game.content.skills.runecrafting.Pouches;
import com.venenatis.game.content.skills.runecrafting.Pouches.Pouch;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.magic.Autocast;
import com.venenatis.game.model.container.impl.InterfaceConstants;
import com.venenatis.game.model.definitions.EquipmentDefinition;
import com.venenatis.game.model.definitions.EquipmentDefinition.EquipmentType;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.IncomingPacketListener;

/**
 * Handles the 'wield' option on items.
 * 
 * @author Patrick van Elderen
 * 
 */
public class WieldPacketHandler implements IncomingPacketListener {

	@Override
	public void handle(Player player, int packetType, int packetSize) {
		final int id = player.getInStream().readUnsignedWord();
		final int slot = player.getInStream().readUnsignedWordA();
		final int interfaceId = player.getInStream().readUnsignedWordA();
		
		player.debug(String.format("[WieldPacketHandler] [id= %d] [slot= %d] [interface %d]", id, slot, interfaceId));
		
		if (player.getCombatState().isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0 || player.getTeleportAction().isTeleporting()) {
			return;
		}
		
		if (player.getAttribute("busy") != null) {
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
			
			if (player.getDuelArena().isDueling()) {
				if (!player.getDuelArena().canEquip(equip)) {
					if (player.getDuelArena().getRules().get(DuelRule.WHIP_DDS)) {
						player.getActionSender().sendMessage("Only whips and dragon daggers are only allowed in this duel.");
					} else {
						player.getActionSender().sendMessage(equip.getType().name().toLowerCase() + "s are disabled in this duel.");
					}
					return;
				}
			}

			if (!player.getController().canEquip(id, slot)) {
				return;
			}
			
			if (equip.getType() == EquipmentType.NOT_WIELDABLE) {
				player.getActionSender().sendMessage("This item cannot be worn.");
				return;
			}

			player.getEquipment().wear(item, slot);
			
			//FIXME Ask Jak is this how OSRS does this too? Why not within the itemOption packets
			switch (item.getId()) {
			case 5509:
				Pouches.empty(player, Pouch.forId(id), id, 0);
				break;
			case 5510:
				Pouches.empty(player, Pouch.forId(id), id, 1);
				break;
			case 5512:
				Pouches.empty(player, Pouch.forId(id), id, 2);
				break;
			}
			
		}
		
		if (player.getEquipment().get(3) == null) {
			player.setAutocastId(-1);
			Autocast.resetAutocasting(player);
			Combat.resetCombat(player);
		}
	}

}
