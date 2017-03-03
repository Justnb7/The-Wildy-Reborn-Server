package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;

public class SendSongPacket implements PacketEncoder {

	private final int songId;

	public SendSongPacket(int songId) {
		this.songId = songId;
	}

	@Override
	public void encode(Player player) {
		player.getOutStream().writeFrame(74);
		player.getOutStream().writeWordBigEndian(songId);
	}

}