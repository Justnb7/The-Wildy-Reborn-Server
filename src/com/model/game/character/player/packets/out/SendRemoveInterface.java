package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendRemoveInterface implements PacketEncoder {
	
	private final int OPCODE = 219;
	
	public SendRemoveInterface() {
		
	}

	@Override
	public void encode(Player player) {
        player.openInterface = -1;
        player.setBanking(false);
		player.setTrading(false);
		player.setShopping(false);
        player.dialogue().interrupt();
        if (player.getOutStream() != null && player != null) {
            player.getOutStream().writeFrame(OPCODE);
        }
	}

}
