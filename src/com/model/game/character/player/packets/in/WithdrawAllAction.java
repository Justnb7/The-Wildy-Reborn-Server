package com.model.game.character.player.packets.in;

import java.util.Objects;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.multiplayer.MultiplayerSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.item.GameItem;
import com.model.game.item.Item;
import com.model.game.item.bank.BankItem;
import com.model.game.item.container.impl.RunePouchContainer;
import com.model.game.shop.Shop;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * Bank All Items
 */
public class WithdrawAllAction implements PacketType {

    @Override
    public void handle(Player player, int packetType, int packetSize) {
        int removeSlot = player.getInStream().readUnsignedWordA();
        int interfaceId = player.getInStream().readUnsignedWord();
        int removeId = player.getInStream().readUnsignedWordA();
        
        switch (interfaceId) {

        case 3900:
        	if (Trading.isTrading(player)) {
                Trading.decline(player);
            }
            if (player.getOpenShop().equals("Skillcape Shop")) {
                Shop.skillBuy(player, removeId);
                return;
            }
            Shop.SHOPS.get(player.getOpenShop()).purchase(player, new Item(removeId, 10));
            break;
            
        case 3823:
        	if (Trading.isTrading(player)) {
                Trading.decline(player);
            }
            if (player.getOpenShop().equals("Skillcape Shop")) {
                return;
            } else if (player.getOpenShop().equals("Death Store")) {
                player.write(new SendMessagePacket("You cannot sell items to this store!"));
                return;
            }
            Shop.SHOPS.get(player.getOpenShop()).sell(player, new Item(removeId, 10), removeSlot);
            break;

        case 41710:
        	RunePouchContainer.withdraw(player, removeSlot, player.getRunePouchContainer().amount(removeId));
        	break;
        	
        case 5064:
        	 if (Trading.isTrading(player)) {
                 Trading.decline(player);
                 return;
             }
			if (player.isBanking()) {
				player.getItems().addToBank(removeId, player.getItems().getItemAmount(removeId), true);
			} else {
				RunePouchContainer.store(player, removeSlot, player.getItems().getItemAmount(removeId));
			}
			break;
			
        case 5382:
            if (!player.isBanking()) {
                return;
            }
            if (player.getBank().getBankSearch().isSearching()) {
                player.getBank().getBankSearch().removeItem(removeId, player.getBank().getCurrentBankTab().getItemAmount(new BankItem(removeId + 1)));
                return;
            }
            player.getItems().removeFromBank(removeId, player.getBank().getCurrentBankTab().getItemAmount(new BankItem(removeId + 1)), true);
            break;
		

        case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.nonNull(session)) {
				session.addItem(player, new GameItem(removeId, player.getItems().getItemAmount(removeId)));
			} else {
				if (ItemDefinition.forId(removeId).isStackable()) {
					Trading.tradeItem(player, removeId, player.playerItemsN[removeSlot], removeSlot);
				} else {
					Trading.tradeItem(player, removeId, 28, removeSlot);
				}
			}
			break;

		case 3415:
			if (ItemDefinition.forId(removeId).isStackable()) {
				for (Item item : player.getTradeContainer().container()) {
					if (item != null && item.id == removeId) {
						Trading.takeItem(player, item.id, player.getTradeContainer().get(removeSlot).getAmount(), removeSlot);
					}
				}
			} else {
				Trading.takeItem(player, removeId, 28, removeSlot);

			}
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(player, removeSlot, new GameItem(removeId, Integer.MAX_VALUE));
			}
			break;

		case 7295:
			if (ItemDefinition.forId(removeId).isStackable()) {
				player.getItems().addToBank(player.playerItems[removeSlot],
						player.playerItemsN[removeSlot], false);
				player.getItems().resetItems(7423);
			} else {
				player.getItems().addToBank(player.playerItems[removeSlot],
				player.getItems().itemAmount(player.playerItems[removeSlot]), false);
				player.getItems().resetItems(7423);
			}
			break;

        }
    }

}
