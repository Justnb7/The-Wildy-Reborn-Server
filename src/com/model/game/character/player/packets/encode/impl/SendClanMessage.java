package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class SendClanMessage implements PacketEncoder {
	
    private final int OPCODE = 217;
	
    private final String member, message, clan;
    
    private final int rights;
    
	public SendClanMessage(String member, String message, String clan, int rights) {
		this.member = member;
		this.message = message;
		this.clan = clan;
		this.rights = rights;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
            player.getOutStream().putFrameVarShort(OPCODE);
            int offset = player.getOutStream().offset;
            player.getOutStream().putRS2String(member);
            player.getOutStream().putRS2String(message);
            player.getOutStream().putRS2String(clan);
            player.getOutStream().writeShort(rights);
            player.getOutStream().putFrameSizeShort(offset);
        }
	}
}
