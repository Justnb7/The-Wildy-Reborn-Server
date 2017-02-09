package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class DisableMap implements PacketEncoder {
	
    private final int OPCODE = 99;
    
    private final int state;
    
    public int mapStatus = 0;
	
	public DisableMap(int state) {
		this.state = state;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			if (mapStatus != state) {
				mapStatus = state;
				player.getOutStream().writeFrame(OPCODE);
				player.getOutStream().writeByte(state);
			}
		}
	}
}
