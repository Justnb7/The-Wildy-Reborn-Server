package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;
import com.model.utility.Utility;

public class SendPM implements PacketEncoder {
	
	private final int OPCODE = 196;
	
	private final long name;
	private final int rights, messageSize;
	private final byte[] chatMessage;
	
	public SendPM(long name, int rights, byte[] chatMessage, int messageSize) {
		this.name = name;
		this.rights = rights;
		this.chatMessage = chatMessage;
		this.messageSize = messageSize;
	}

	@Override
	public void encode(Player player) {
		if (player == null)
            return;
        if (player.getOutStream() != null) {
            player.getOutStream().putFrameVarByte(OPCODE);
            int offset = player.getOutStream().offset;
            player.getOutStream().putLong(name);
            player.getOutStream().putInt(player.lastChatId++);
            player.getOutStream().writeByte(rights);
            player.getOutStream().writeBytes(chatMessage, messageSize, 0);
            player.getOutStream().putFrameSizeByte(offset);
            Utility.textUnpack(chatMessage, messageSize);
            Utility.longToPlayerName(name);
        }
	}

}
