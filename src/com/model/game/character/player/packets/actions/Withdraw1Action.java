package com.model.game.character.player.packets.actions;

import java.util.Objects;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.Trading;
import com.model.game.character.player.content.multiplayer.MultiplayerSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.crafting.jewelry.JewelleryMaking;
import com.model.game.character.player.skill.smithing.Smithing;
import com.model.game.item.GameItem;
import com.model.game.item.Item;
import com.model.game.item.container.impl.RunePouchContainer;
import com.model.game.shop.Shop;

/**
 * Remove Item
 */
public class Withdraw1Action implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int interfaceId = player.getInStream().readUnsignedWordA();
		int removeSlot = player.getInStream().readUnsignedWordA();
		int removeId = player.getInStream().readUnsignedWordA();
		
		if (player.getBankPin().requiresUnlock()) {
			player.getBankPin().open(2);
			return;
		}
		
		switch (interfaceId) {
		
		case 41710:
			RunePouchContainer.withdraw(player, removeSlot, 1);
			break;
		case 7423:
			player.getItems().addToBank(removeId, 1, false);
			player.getItems().resetItems(7423);
			break;
			
		case 1688:
			player.getItems().removeEquipment(removeId, removeSlot);
			break;

		case 5064:
			if (player.isBanking) {
				player.getItems().addToBank(removeId, 1, true);
			} else {
				RunePouchContainer.store(player, removeSlot, 1);
			}
			break;
			
		case 5382:
        	if(player.getBank().getBankSearch().isSearching()) {
        		player.getBank().getBankSearch().removeItem(removeId, 1);
        		return;
        	}
            player.getItems().removeFromBank(removeId, 1, true);
			break;
			
		case 3900:
			if (player.getOpenShop().equals("Skillcape Shop")) {
				player.write(new SendMessagePacket("All items in this shop cost 99K coins."));
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).sendPurchasePrice(player, new Item(removeId));
			break;
			
		case 3823:
			if (player.getOpenShop().equals("Skillcape Shop")) {
				player.write(new SendMessagePacket("Items cannot be sold to this shop."));
				return;
			} else if (player.getOpenShop().equals("Death Store")) {
				player.write(new SendMessagePacket("You cannot sell items to this store!"));
				return;
			}
			Shop.SHOPS.get(player.getOpenShop()).sendSellingPrice(player, new Item(removeId));
			break;
			
		case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.nonNull(session)) {
				session.addItem(player, new GameItem(removeId, 1));
			} else {
				Trading.tradeItem(player, removeId, 1, removeSlot);
			}
			break;

		case 3415:
			Trading.takeItem(player, removeId, 1, removeSlot);
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(player, removeSlot, new GameItem(removeId, 1));
			}
			break;
			
		case 4233:
			JewelleryMaking.mouldItem(player,  removeId, 1);
			break;
			
		case 4239:
			JewelleryMaking.mouldItem(player,  removeId, 1);
			break;
			
		case 4245:
			JewelleryMaking.mouldItem(player,  removeId, 1);
			break;
			
		case 1119:
		case 1120:
		case 1121:
		case 1122:
		case 1123:
			Smithing.readInput(player, player.getSkills().getLevel(Skills.SMITHING), Integer.toString(removeId), 1);
			break;

		}
	}

}
