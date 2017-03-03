package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendFrame106 implements PacketEncoder {
	
	private final int OPCODE = 106;
	
	private final int sideIcon;
	
	public SendFrame106(int sideIcon) {
		this.sideIcon = sideIcon;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
            player.getOutStream().writeFrame(OPCODE);
            player.getOutStream().writeByteC(sideIcon);
            player.getPA().requestUpdates();
        }
	}

}
