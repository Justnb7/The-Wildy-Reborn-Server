package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendScreenFade implements PacketEncoder {
	
	private final int OPCODE = 9;
	
	private final String text;
	
	private final int state, seconds;
	
	public SendScreenFade(String text, int state, int seconds) {
		this.text = text;
		this.state = state;
		this.seconds = seconds;
	}

	@Override
	/**
	 * Sends some information to the client about screen fading. 
	 * @param text		the text that will be displayed in the center of the screen
	 * @param state		the state should be either 0, -1, or 1. 
	 * @param seconds	the amount of time in seconds it takes for the fade
	 * to transition.
	 * <p>
	 * If the state is -1 then the screen fades from black to transparent.
	 * When the state is +1 the screen fades from transparent to black. If 
	 * the state is 0 all drawing is stopped.
	 */
	public void encode(Player player) {
		if (player == null || player.getOutStream() == null) {
			return;
		}
		if (seconds < 1 && state != 0) {
			throw new IllegalArgumentException("The amount of seconds cannot be less than one.");
		}
		player.getOutStream().putFrameVarShort(OPCODE);
		int offset = player.getOutStream().offset;
		player.getOutStream().putRS2String(text);
		player.getOutStream().writeByte(state);
		player.getOutStream().writeByte(seconds);
		player.getOutStream().putFrameSizeShort(offset);
	}

}
