package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendFrame70 implements PacketEncoder {
	
	private final int OPCODE = 70;

	private final int i, o, id;

	public SendFrame70(int i, int o, int id) {
		this.i = i;
		this.o = o;
		this.id = id;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(OPCODE);
			player.getOutStream().writeShort(i);
			player.getOutStream().writeWordBigEndian(o);
			player.getOutStream().writeWordBigEndian(id);
		}
	}

}
