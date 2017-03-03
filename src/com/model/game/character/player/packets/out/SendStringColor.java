package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

/**
 * Changes the color of a string on an interface
 *
 * @author Arithium
 *
 */
public class SendStringColor implements PacketEncoder {

	private final int color;

	private final int stringId;

	/**
	 * Changes a strings color
	 *
	 * @param stringId
	 * @param color
	 */
	public SendStringColor(int stringId, int color) {
		this.stringId = stringId;
		this.color = color;
	}

	@Override
	public void encode(Player client) {
		client.getOutStream().writeFrame(122);
		client.getOutStream().writeWordBigEndianA(stringId);
		client.getOutStream().writeWordBigEndianA(color);	
	}

}
