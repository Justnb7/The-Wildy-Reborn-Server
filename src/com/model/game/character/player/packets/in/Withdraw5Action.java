package com.model.game.character.player.packets.in;

import java.util.Objects;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.multiplayer.MultiplayerSession;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.PacketType;
import com.model.game.item.GameItem;
import com.model.game.item.Item;
import com.model.game.item.container.impl.RunePouchContainer;
import com.model.game.shop.Shop;

/**
 * Bank 5 Items
 */
public class Withdraw5Action implements PacketType {

    @Override
    public void handle(Player player, int packetType, int packetSize) {
        int interfaceId = player.getInStream().readSignedWordBigEndianA();
        int removeId = player.getInStream().readSignedWordBigEndianA();
        int removeSlot = player.getInStream().readSignedWordBigEndian();
        
        switch (interfaceId) {
            
		case -23826:
			player.getRunePouchContainer().withdraw(player, removeSlot, 5);
			break;
			
        case 1688:
            player.getPA().useOperate(removeId);
            break;
            
        case 3900:
            if (player.getOpenShop().equals("Skillcape Shop")) {
                Shop.skillBuy(player, removeId);
                return;
            }
            Shop.SHOPS.get(player.getOpenShop()).purchase(player, new Item(removeId, 1));
            break;
            
        case 3823:
            if (player.getOpenShop().equals("Skillcape Shop")) {
                return;
            } else if (player.getOpenShop().equals("Death Store")) {
                player.getActionSender().sendMessage("You cannot sell items to this store!");
                return;
            }
            Shop.SHOPS.get(player.getOpenShop()).sell(player, new Item(removeId, 5), removeSlot);
            break;
            
        case 5064:
			if (player.isBanking()) {
				player.getItems().addToBank(removeId, 5, true);
			}/* else {
				RunePouchContainer.store(player, removeSlot, 5);
			}*/
            break;
            
        case 5382:
        	if(player.getBank().getBankSearch().isSearching()) {
        		player.getBank().getBankSearch().removeItem(removeId, 5);
        		return;
        	}
			player.getItems().removeFromBank(removeId, 5, true);
            break;

        case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.nonNull(session)) {
				session.addItem(player, new GameItem(removeId, 5));
			} else {
				Trading.tradeItem(player, removeId, 5, removeSlot);
			}
			break;

		case 3415:
			Trading.takeItem(player, removeId, 5, removeSlot);
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(player, removeSlot, new GameItem(removeId, 5));
			}
			break;

        }
    }

}
