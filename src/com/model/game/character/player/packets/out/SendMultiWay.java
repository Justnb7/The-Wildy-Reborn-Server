package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendMultiWay implements PacketEncoder {
	
	private final int OPCODE = 61, icon;
	
	public SendMultiWay(int icon) {
		this.icon = icon;
	}

	@Override
	public void encode(Player player) {
		if (player != null) {
            player.outStream.writeFrame(OPCODE);
            player.outStream.writeByte(icon);
            player.updateRequired = true;
            player.appearanceUpdateRequired = true;
        }
	}

}
