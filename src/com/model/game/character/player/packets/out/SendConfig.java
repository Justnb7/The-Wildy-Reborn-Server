package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendConfig implements PacketEncoder {
	
	/**
	 * The opcode for the string to send
	 */
	private static final int OPCODE = 36;
	
	private final int configId, state;

	public SendConfig(int configId, int state) {
		this.configId = configId;
		this.state = state;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null && player != null) {
			//System.out.println("Varp update will be opcode "+(state<128 ? OPCODE:87)+" based on state:"+state);
			if (state < 128) {
				player.getOutStream().writeFrame(OPCODE);
				player.getOutStream().writeWordBigEndian(configId);
				player.getOutStream().writeByte(state);
			} else {
				player.getOutStream().writeFrame(87);
				player.getOutStream().writeWordBigEndian_dup(configId);
				player.getOutStream().writeDWord_v1(state);
			}
		}
	}

}
