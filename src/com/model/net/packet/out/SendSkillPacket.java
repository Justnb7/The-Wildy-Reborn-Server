package com.model.net.packet.out;

import com.model.game.character.player.Player;
import com.model.net.packet.PacketEncoder;

public class SendSkillPacket implements PacketEncoder {
	
	private final int OPCODE = 134;
	
	private final int skill;

	public SendSkillPacket(int skill) {
		this.skill = skill;
	}

	@Override
	public void encode(Player player) {
		if (player.getOutStream() != null) {
            player.getOutStream().writeFrame(OPCODE);
            player.getOutStream().writeByte((byte) skill);
            player.getOutStream().writeDWord_v1((int) player.getSkills().getExperience(skill));
            player.getOutStream().writeByte((byte) player.getSkills().getLevel(skill));
            //System.out.printf("skill - %s exp %s lvl %s %n", skill, player.getSkills().getExperience(skill), player.getSkills().getLevel(skill));
        }
	}

}
