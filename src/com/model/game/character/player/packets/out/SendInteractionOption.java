package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendInteractionOption implements PacketEncoder {
	
    private final int OPCODE = 104;
	
    private final String option;
    private final int slot;
    private final boolean top;
    
	public SendInteractionOption(String option, int slot, boolean top) {
		this.option = option;
		this.slot = slot;
		this.top = top;
	}

	/**
	 * Sends the player an option.
	 * 
	 * @param slot
	 *            The slot to place the option in the menu.
	 * @param top
	 *            Flag which indicates the item should be placed at the top.
	 * @return The action sender instance, for chaining.
	 */
	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().putFrameVarByte(OPCODE);
			int offset = player.getOutStream().offset;
			player.getOutStream().writeByte((byte) -slot);
			player.getOutStream().putByteA(top ? (byte) 0 : (byte) 1);
			player.getOutStream().putRS2String(option);
			player.getOutStream().putFrameSizeByte(offset);
			player.flushOutStream();
		}
	}
}
