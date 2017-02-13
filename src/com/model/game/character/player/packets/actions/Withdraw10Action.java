package com.model.game.character.player.packets.actions;

import java.util.Objects;

import com.model.Server;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.multiplayer.MultiplayerSession;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionFinalizeType;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionStage;
import com.model.game.character.player.content.multiplayer.MultiplayerSessionType;
import com.model.game.character.player.content.multiplayer.duel.DuelSession;
import com.model.game.character.player.content.trade.Trading;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.item.GameItem;
import com.model.game.item.Item;
import com.model.game.item.container.impl.RunePouchContainer;
import com.model.game.shop.Shop;

/**
 * Bank 10 Items
 */
public class Withdraw10Action implements PacketType {

    @Override
    public void processPacket(Player player, int packetType, int packetSize) {
        int interfaceId = player.getInStream().readUnsignedWordBigEndian();
        int removeId = player.getInStream().readUnsignedWordA();
        int removeSlot = player.getInStream().readUnsignedWordA();
        
        switch (interfaceId) {
        
		case 41710:
			RunePouchContainer.withdraw(player, removeSlot, 10);
		break;
        case 1688:
			player.getPA().useOperate(removeId);
		break;

        case 3900:
        	if (Trading.isTrading(player)) {
                Trading.decline(player);
            }
            if (player.getOpenShop().equals("Skillcape Shop")) {
                Shop.skillBuy(player, removeId);
                return;
            }
            Shop.SHOPS.get(player.getOpenShop()).purchase(player, new Item(removeId, 5));//<5?
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
            Shop.SHOPS.get(player.getOpenShop()).sell(player, new Item(removeId, 5), removeSlot);
            break;
            
        case 5064:
        	if (Trading.isTrading(player)) {
                Trading.decline(player);
                return;
            }
            if (player.isBanking) {
                player.getItems().addToBank(removeId, 10, true);
            } else {
            	RunePouchContainer.store(player, removeSlot, 10);
            }
            DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
				player.write(new SendMessagePacket("You have declined the duel."));
				duelSession.getOther(player).write(new SendMessagePacket("The challenger has declined the duel."));
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
            break;

        case 5382:
        	if (Trading.isTrading(player)) {
                Trading.decline(player);
            }
        	if(player.getBank().getBankSearch().isSearching()) {
        		player.getBank().getBankSearch().removeItem(removeId, 10);
        		return;
        	}
			player.getItems().removeFromBank(removeId, 10, true);
			break;

		case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.nonNull(session)) {
				session.addItem(player, new GameItem(removeId, 10));
			} else {
				Trading.tradeItem(player, removeId, 10, removeSlot);
			}
			break;

		case 3415:
			Trading.takeItem(player, removeId, 10, removeSlot);
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(player);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(player, removeSlot, new GameItem(removeId, 10));
				
			}
			break;
			
        }
    }

}
