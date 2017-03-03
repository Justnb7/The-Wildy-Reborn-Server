package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SetChatOptions implements PacketEncoder {

	private final int OPCODE = 206;

	private final int publicChat, privateChat, tradeBlock;

	public SetChatOptions(int publicChat, int privateChat, int tradeBlock) {
		this.publicChat = publicChat;
		this.privateChat = privateChat;
		this.tradeBlock = tradeBlock;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(OPCODE);
			player.getOutStream().writeByte(publicChat);
			player.getOutStream().writeByte(privateChat);
			player.getOutStream().writeByte(tradeBlock);
		}
	}
}
