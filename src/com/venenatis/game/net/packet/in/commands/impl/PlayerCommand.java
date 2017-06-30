package com.venenatis.game.net.packet.in.commands.impl;

import com.venenatis.game.content.trivia.TriviaBot;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.net.packet.in.commands.CommandParser;

/**
 * A list of commands accessible to all players disregarding rank.
 * 
 * @author Michael | Chex
 */
public class PlayerCommand implements Command {

	@Override
	public boolean handleCommand(Player player, CommandParser parser) throws Exception {

		if (player.isDueling() || player.getDuelArena().isInSession() && !Rights.isSuperStaff(player)) {
			player.getActionSender().sendMessage("You cannot use commands while dueling.");
			return true;
		}

		switch (parser.getCommand()) {

		/* TriviaBot */
		case "answer":
			if (parser.hasNext()) {
				String answer = "";
				while (parser.hasNext()) {
					answer += parser.nextString() + " ";
				}
				TriviaBot.answer(player, answer.trim());
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean meetsRequirements(Player player) {
		return true;
	}
}