package com.model.net.packet.in;

import com.model.game.character.player.Player;
import com.model.net.packet.PacketType;

/**
 * Sent when a player clicks somewhere on the game screen.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 */
public class ClickOnGameScreen implements PacketType {

	@Override
	public void handle(Player player, int id, int size) {
		//player.stopSkillTask();
	}

}
