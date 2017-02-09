package com.model.game.character.player.packets.encode.impl;

import java.util.ArrayList;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class SendParallellInterfaceVisibility implements PacketEncoder {
	
    private final int OPCODE = 209;
    
    private final int interfaceId;
    
    private final boolean visible;
    
    ArrayList<Integer> walkableInterfaceList = new ArrayList<Integer>();
	
	public SendParallellInterfaceVisibility(int interfaceId, boolean visible) {
		this.interfaceId = interfaceId;
		this.visible = visible;
	}

	@Override
	public void encode(Player player) {
		if (player != null && player.getOutStream() != null) {
			if (visible) {
				if (walkableInterfaceList.contains(interfaceId)) {
					return;
				} else {
					walkableInterfaceList.add(interfaceId);
				}
			} else {
				if (!walkableInterfaceList.contains(interfaceId)) {
					return;
				}
			}
			player.getOutStream().writeFrame(OPCODE);
			player.getOutStream().writeWordBigEndianA(interfaceId);
			player.getOutStream().writeWordBigEndianA(visible ? 1 : 0);
			player.flushOutStream();
		}
	}

}
