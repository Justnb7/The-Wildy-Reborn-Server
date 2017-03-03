package com.model.game.character.player.packets.out;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.PacketEncoder;
import com.model.net.network.rsa.GameBuffer;

public class SendExperienceCounter implements PacketEncoder {

	private static final int OPCODE = 127;

	private final int skill;

	private final int experience;

	public SendExperienceCounter(int skill, int experience) {
		this.skill = skill;
		this.experience = experience;
	}
	
	@Override
	public void encode(Player player) {
		GameBuffer stream = player.getOutStream();
    	stream.writeFrame(OPCODE);
    	stream.writeByte(skill);
    	stream.putInt(experience);
    	stream.putInt(player.getSkills().getExpCounter());
    	//System.out.println("skill: "+skill+ " exp given "+experience);
    	player.flushOutStream();
	}

}
