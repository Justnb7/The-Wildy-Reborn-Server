package com.model.game.character.player.packets.in;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketType;
import com.model.game.item.Item;
import com.model.game.shop.Shop;

public class BankX1 implements PacketType {

    public static final int PART1 = 135;
    public static final int PART2 = 208;
    public int XremoveSlot, XinterfaceID, XremoveID, Xamount;

    @Override
    public void handle(Player player, int packetType, int packetSize) {
    	
        if (packetType == 135) {
            player.xRemoveSlot = player.getInStream().readSignedWordBigEndian();
            player.xInterfaceId = player.getInStream().readUnsignedWordA();
            player.xRemoveId = player.getInStream().readSignedWordBigEndian();
        }
        
        if (player.xInterfaceId == 3823) {
            if (player.getOpenShop().equals("Death Store")) {
                player.getActionSender().sendMessage("You cannot sell items to this store!");
                return;
            }
            Shop.SHOPS.get(player.getOpenShop()).sell(player, new Item(player.xRemoveId, 100), player.xRemoveSlot);
            player.xRemoveSlot = 0;
            player.xInterfaceId = 0;
            player.xRemoveId = 0;
            return;
        }
        if (packetType == PART1) {
            player.getOutStream().writeFrame(27);
        }

    }
}
