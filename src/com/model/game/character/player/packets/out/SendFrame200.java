package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendFrame200 implements PacketEncoder {

	private final int OPCODE = 200;

	private final int mainFrame, subFrame;

	public SendFrame200(int mainFrame, int subFrame) {
		this.mainFrame = mainFrame;
		this.subFrame = subFrame;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(OPCODE);
			player.getOutStream().writeShort(mainFrame);
			player.getOutStream().writeShort(subFrame);
		}
	}

}
