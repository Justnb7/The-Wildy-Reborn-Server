package com.model.net.packet.out;

import com.model.game.character.player.Player;
import com.model.net.packet.PacketEncoder;

/**
 * Represents a single outbound sound packet
 *
 * @author Arithium
 *
 */
public class SendSoundPacket implements PacketEncoder {

	/**
	 * The id of the sound
	 */
	private final int id;

	/**
	 * The type of the sound
	 */
	private final int type;

	/**
	 * The delay of the sound
	 */
	private final int delay;

	/**
	 * Constructs a new SendSoundPacket
	 *
	 * @param id
	 *            The id of the sound
	 * @param type
	 *            The type of the sound
	 * @param delay
	 *            The delay of the sound
	 */
	public SendSoundPacket(int id, int type, int delay) {
		this.id = id;
		this.delay = delay;
		if (type == 0) {
			this.type = 10;
		} else {
			this.type = type;
		}
	}

	@Override
	public void encode(Player player) {
		/*if (!player.isEnableSound()) {
		    return;
		}*/
		player.getOutStream().writeFrame(174);
		player.getOutStream().writeShort(id);
		player.getOutStream().writeByte(type);
		player.getOutStream().writeShort(delay);
	}

}