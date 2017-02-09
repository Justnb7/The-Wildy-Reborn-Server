package com.model.game.character.player.packets.encode.impl;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.PacketEncoder;

public class SendTemporarySongPacket implements PacketEncoder {

	private final int songDelay;

	private final int songId;

	public SendTemporarySongPacket(int songId, int songDelay) {
		this.songId = songId;
		this.songDelay = songDelay;
	}

	@Override
	public void encode(Player player) {
		player.getOutStream().writeFrame(121);
		player.getOutStream().writeWordBigEndian(songId);
		player.getOutStream().writeWordBigEndian(songDelay);
	}

}