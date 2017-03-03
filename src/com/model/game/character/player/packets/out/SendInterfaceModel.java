package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendInterfaceModel implements PacketEncoder {
	
	/**
	 * The opcode for the string to send
	 */
	private static final int OPCODE = 246;
	
	private final int interfaceChild, zoom, itemId;

	public SendInterfaceModel(int interfaceChild, int zoom, int itemId) {
		this.interfaceChild = interfaceChild;
		this.zoom = zoom;
		this.itemId = itemId;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null && player != null) {
            player.getOutStream().writeFrame(OPCODE);
            player.getOutStream().writeWordBigEndian(interfaceChild);
            player.getOutStream().writeShort(zoom);
            player.getOutStream().writeShort(itemId);
        }
	}
}
