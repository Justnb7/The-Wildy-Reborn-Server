package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendFrame185 implements PacketEncoder {
	
	private final int OPCODE = 185;
	
	private final int frame;
	
	public SendFrame185(int frame) {
		this.frame = frame;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(OPCODE);
			player.getOutStream().writeWordBigEndianA(frame);
		}
	}

}
