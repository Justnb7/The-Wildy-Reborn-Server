package com.venenatis.game.net.packet.in;

import com.venenatis.game.content.minigames.multiplayer.duel_arena.DuelArena;
import com.venenatis.game.content.minigames.multiplayer.duel_arena.DuelArena.DuelOptions;
import com.venenatis.game.content.minigames.multiplayer.duel_arena.DuelArena.DuelStage;
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
		final int id = player.getInStream().readUnsignedShort();
		final int slot = player.getInStream().readUnsignedWordA();
		final int interfaceId = player.getInStream().readUnsignedWordA();
		
		player.debug(String.format("[WieldPacketHandler] [id= %d] [slot= %d] [interface %d]", id, slot, interfaceId));
		//FIXME Ask Jak is this how OSRS does this too? Why not within the itemOption packets
		
		if (player.getCombatState().isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0 || player.getTeleportAction().isTeleporting()) {
			return;
		}
		
		if (player.getAttribute("busy") != null || player.getAttributes().get("duel_stage") != null && player.getAttributes().get("duel_stage") != DuelStage.FIGHTING_STAGE) {
			return;
		}
		
		switch (interfaceId) {
		case InterfaceConstants.INVENTORY_INTERFACE:
			final Item item = player.getInventory().get(slot);

			if (item == null || item.getId() != id) {
				player.sendDelayedMessage(0, "RETURNING");
				return;
			}
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
		
			final EquipmentDefinition equip = EquipmentDefinition.EQUIPMENT_DEFINITIONS.get(item.getId());

			if (equip == null) {
				return;
			}

			if (!player.getController().canEquip(id, slot)) {
				return;
			}
			
			if (equip.getType() == EquipmentType.NOT_WIELDABLE) {
				player.getActionSender().sendMessage("This item cannot be worn.");
				return;
			}
			
			if ((player.getDuelArena().getOptionActive()[DuelOptions.NO_WEAPON_SWITCH.getId()] && equip.getType() == EquipmentType.WEAPON) || ((player.getDuelArena().getOptionActive()[DuelOptions.FUN_WEAPONS.getId()] && equip.getType() == EquipmentType.WEAPON))) {
				player.getActionSender().sendMessage("The right to change weapon has been revoked during this stake.");
				return;
			}
				
			if (player.getAttributes().get("duel_stage") != null) {
				for (int index = 0; index < player.getDuelArena().getEquipmentRestricted().length; index++) {
					if (player.getDuelArena().getEquipmentRestricted()[index] && DuelArena.EquipmentSlots.get(index).getSlot() == equip.getType().getSlot()) {
						player.getActionSender().sendMessage("You can't wear this item during this stake.");
						return;
					}
				}
			}

			player.getEquipment().wear(item, slot);
			
			
			
		}
		
		if (player.getEquipment().get(3) == null) {
			player.setAutocastId(-1);
			Autocast.resetAutocasting(player);
			Combat.resetCombat(player);
		}
	}

}
