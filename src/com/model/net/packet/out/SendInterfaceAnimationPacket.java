package com.model.net.packet.out;

import com.model.game.character.player.Player;
import com.model.net.packet.PacketEncoder;

public class SendInterfaceAnimationPacket implements PacketEncoder {

	private final int OPCODE = 200;

	private final int mainFrame, subFrame;

	public SendInterfaceAnimationPacket(int mainFrame, int subFrame) {
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
