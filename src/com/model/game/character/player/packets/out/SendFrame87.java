package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendFrame87 implements PacketEncoder {

	private final int OPCODE = 87;

	private final int id, state;

	public SendFrame87(int id, int state) {
		this.id = id;
		this.state = state;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(OPCODE);
			player.getOutStream().writeWordBigEndian_dup(id);
			player.getOutStream().writeDWord_v1(state);
		}
	}

}
