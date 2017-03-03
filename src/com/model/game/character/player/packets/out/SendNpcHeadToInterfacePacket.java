package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendNpcHeadToInterfacePacket implements PacketEncoder {

	private final int OPCODE = 75;

	private final int mainFrame, subFrame;

	public SendNpcHeadToInterfacePacket(int mainFrame, int subFrame) {
		this.mainFrame = mainFrame;
		this.subFrame = subFrame;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(OPCODE);
			player.getOutStream().writeWordBigEndianA(mainFrame);
			player.getOutStream().writeWordBigEndianA(subFrame);
		}
	}

}
