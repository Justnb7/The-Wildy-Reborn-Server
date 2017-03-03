package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendChatBoxInterface implements PacketEncoder {

	private final int OPCODE = 164;

	private final int frame;

	public SendChatBoxInterface(int frame) {
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
