package com.model.net.packet.in;

import com.model.game.character.player.Player;
import com.model.net.packet.PacketType;

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
        
        if (packetType == PART1) {
            player.getOutStream().writeFrame(27);
        }

    }
}
