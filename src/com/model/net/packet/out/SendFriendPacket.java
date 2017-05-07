package com.model.net.packet.out;

import com.model.game.character.player.Player;
import com.model.net.packet.PacketEncoder;

public class SendFriendPacket implements PacketEncoder {
	
    private final int OPCODE = 50;
    
    private final long username;
    
    private final int world;
	
	public SendFriendPacket(long username, int world) {
		this.username = username;
		this.world = world;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
            player.getOutStream().writeFrame(OPCODE);
            player.getOutStream().putLong(username);
            player.getOutStream().writeByte(world);
        }
	}
}
