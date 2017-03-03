package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

/**
 * Sends a link to the client to open up a url
 * 
 * @author Mobster
 *
 */
public class SendLink implements PacketEncoder {

	/**
	 * The opcode for the link to send
	 */
	private static final int OPCODE = 127;

	/**
	 * The link to the url to open up
	 */
	private final String link;

	/**
	 * Sends a link to the client to open a url
	 * 
	 * @param link
	 *            The link to the url to open up
	 */
	public SendLink(String link) {
		this.link = link;
	}

	@Override
	public void encode(Player player) {
		player.getOutStream().putFrameVarShort(OPCODE);
		int offset = player.getOutStream().offset;
		player.getOutStream().putRS2String(link);
		player.getOutStream().putFrameSizeShort(offset);
		player.flushOutStream();
	}

}
