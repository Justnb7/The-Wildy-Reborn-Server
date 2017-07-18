package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.DialogueManager;
import com.venenatis.game.net.packet.PacketType;

/**
 * Represents a packet used for handling dialogues.
 * This specific packet currently handles the action
 * for clicking the "next" option during a dialogue.
 * 
 * @author Professor Oak
 */
public class DialoguePacketHandler implements PacketType {

	@Override
	public void handle(Player player, int id, int size) {
		
		if(player == null || player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
			return;
		}
		
		DialogueManager.next(player);
	}

}
