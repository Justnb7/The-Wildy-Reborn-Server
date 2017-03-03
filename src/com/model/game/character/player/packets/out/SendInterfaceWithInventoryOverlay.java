package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendInterfaceWithInventoryOverlay implements PacketEncoder {

	private final int OPCODE = 248;
	
	private final int mainFrame, subFrame;
	
	public SendInterfaceWithInventoryOverlay(int mainFrame, int subFrame) {
		this.mainFrame = mainFrame;
		this.subFrame = subFrame;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
			player.getOutStream().writeFrame(OPCODE);
			player.getOutStream().writeWordA(mainFrame);
			player.getOutStream().writeShort(subFrame);
			player.openInterface = mainFrame;
		}
	}
	
}
