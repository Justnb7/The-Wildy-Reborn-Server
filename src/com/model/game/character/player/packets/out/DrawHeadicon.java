package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class DrawHeadicon implements PacketEncoder {
	
	private final int OPCODE = 254;
	
	private final int i, j, k, l;
	
	public DrawHeadicon(int i, int j, int k, int l) {
		this.i = i;
		this.j = j;
		this.k = k;
		this.l = l;
	}

	@Override
	public void encode(Player player) {
		player.outStream.writeFrame(OPCODE);
		player.outStream.writeByte(i);

		if (i == 1 || i == 10) {
			player.outStream.writeShort(j);
			player.outStream.writeShort(k);
			player.outStream.writeByte(l);
		} else {
			player.outStream.writeShort(k);
			player.outStream.writeShort(l);
			player.outStream.writeByte(j);
		}
	}

}
