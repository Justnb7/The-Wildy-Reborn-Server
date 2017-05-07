package com.model.net.packet.out;

import com.model.game.character.player.Player;
import com.model.net.network.rsa.GameBuffer;
import com.model.net.packet.PacketEncoder;

public class SendKillFeedPacket implements PacketEncoder {

	private static final int OPCODE = 173;


	private final int weapon;
	
	private final String killer, victim;
	
	private final boolean poison;

	public SendKillFeedPacket(String killer, String victim, int weapon, boolean poison) {
		this.killer = killer;
		this.victim = victim;
		this.weapon = weapon;
		this.poison = poison;
	}

	@Override
	public void encode(Player player) {
		if (killer == null || killer.length() == 0 || victim == null || victim.length() == 0) {
			return;
		}
		GameBuffer stream = player.getOutStream();
		stream.putFrameVarShort(OPCODE);
		int offset = player.getOutStream().offset;
		stream.putRS2String(killer);
		stream.putRS2String(victim);
		stream.writeShort(weapon);
		stream.writeByte(poison ? 1 : 0);
		player.getOutStream().putFrameSizeShort(offset);
		player.flushOutStream();
	}

}