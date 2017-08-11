package com.venenatis.game.net.packet.in;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.PacketType;

/**
 * Sent when a player clicks somewhere on the game screen.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 */
public class ClickOnGameScreen implements PacketType {

	@Override
	public void handle(Player player, int id, int size) {
		if(player.hasAttribute("firemaking")) {
			player.getAttributes().put("firemaking", false);
			player.debug("as");
		}
		player.stopSkillTask();
	}

}
