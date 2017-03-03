package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendWalkableInterfacePacket implements PacketEncoder {
	
    private final int OPCODE = 208;
    
    private final int interfaceId;
	
	public SendWalkableInterfacePacket(int interfaceId) {
		this.interfaceId = interfaceId;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(OPCODE);
			player.getOutStream().writeWordBigEndian_dup(interfaceId);
		}
	}
}
