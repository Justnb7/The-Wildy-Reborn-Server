package com.model.net.packet.out;

import com.model.game.character.player.Player;
import com.model.net.packet.PacketEncoder;

public class SendChatBoxInterfacePacket implements PacketEncoder {

	private final int OPCODE = 164;

	private final int frame;

	public SendChatBoxInterfacePacket(int frame) {
		this.frame = frame;
	}

	@Override
	public void encode(Player player) {
		player.stopSkillTask();
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(OPCODE);
			player.getOutStream().writeWordBigEndian_dup(frame);
		}
	}

}
