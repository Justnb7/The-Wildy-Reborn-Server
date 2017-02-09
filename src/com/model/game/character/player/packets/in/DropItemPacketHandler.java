package com.model.game.character.player.packets.in;

import com.model.game.Constants;
import com.model.game.character.combat.Combat;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.account_type.Account;
import com.model.game.character.player.content.Trading;
import com.model.game.character.player.content.bounty_hunter.BountyHunter;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.packets.encode.impl.SendSoundPacket;
import com.model.game.item.Item;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * Drop Item
 */
public class DropItemPacketHandler implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int itemId = player.getInStream().readUnsignedWordA();
		player.getInStream().readUnsignedByte();
		player.getInStream().readUnsignedByte();
		int slot = player.getInStream().readUnsignedWordA();
		
		if (player.isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0 || player.teleporting) {
			return;
		}
		
		if (player.getPets().isPetItem(itemId)) {
			player.getPets().spawnPet(player, itemId, false);
			return;
		}

		if (player.underAttackBy != 0 && (ItemDefinition.forId(itemId).getShopValue()* .75) > 1000) {
			player.write(new SendMessagePacket("You can't drop items worth over 1,000 gold in combat."));
			return;
		}
		if (Trading.isTrading(player)) {
        	Trading.decline(player);
        }
		if (!player.getItems().playerHasItem(itemId, 1, slot)) {
			return;
		}
		if (player.getBankPin().requiresUnlock()) {
			player.getBankPin().open(2);
			return;
		}

		if(player.getAccount().getType().equals(Account.IRON_MAN_TYPE)) {
			player.droppedItem = itemId;
			player.getItems().deleteItem(itemId, slot, player.playerItemsN[slot]);
			player.write(new SendMessagePacket("Your " + ItemDefinition.forId(itemId).getName() + " vanishes as you drop it on the ground."));
			return;
		}
		
		if(!ItemDefinition.forId(itemId).isTradable()) {
			player.getPA().destroyItem(itemId);
			player.setDestroyItem(itemId);
			return;
		}
		
		boolean droppable = true;
		for (int i : Constants.UNDROPABLE) {
			if (i == itemId) {
				droppable = false;
			}
		}
		if (player.playerItemsN[slot] != 0 && itemId != -1 && player.playerItems[slot] == itemId + 1) {
			if (droppable) {
				if (player.underAttackBy > 0) {
					if (ItemDefinition.forId(itemId).getShopValue() > 1000) {
						player.write(new SendMessagePacket("You can't drop items worth over 1,000 gold in combat."));
						return;
					}
				}
				boolean dropOnGround = true;
				boolean deleteFromInventory = true;
				
				if(itemId == 10729) {
					return;
				}
				
				player.write(new SendSoundPacket(376, 1, 0));
	
				if (dropOnGround) {
					GroundItemHandler.createGroundItem(new GroundItem(new Item(itemId, player.playerItemsN[slot]), player.getX(), player.getY(), player.getZ(), player));
				}
				if (deleteFromInventory) {
					player.getItems().deleteItem(itemId, slot, player.playerItemsN[slot]);
				}
				Combat.resetCombat(player);
				BountyHunter.determineWealth(player);
				//player.write(new SendMessagePacket(player.getName()+" has dropped a "+player.getItems().getItemName(itemId)+"."));
			} else {
				player.write(new SendMessagePacket(player.getItems().getItemName(itemId)+" is a undropable item there for this item could not be dropped."));
			}
		}
	}
}
