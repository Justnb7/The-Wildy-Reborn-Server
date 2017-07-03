package com.venenatis.game.net.packet.in.commands.impl;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.teleportation.Teleport.SpellBookTypes;
import com.venenatis.game.content.teleportation.Teleport.TeleportTypes;
import com.venenatis.game.content.trivia.TriviaBot;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.net.packet.in.commands.CommandParser;
import com.venenatis.game.net.packet.in.commands.Yell;
import com.venenatis.game.world.World;

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
		
		/* Home Teleport */
		case "home":
			player.getTeleportAction().teleport(Constants.RESPAWN_PLAYER_LOCATION, TeleportTypes.SPELL_BOOK, false);
			return true;

		/* Online Players */
		case "players":
			player.getActionSender().sendMessage("<col=255>There are currently " + World.getWorld().getPlayerCount() + " players online!");
			return true;
		
		/* Vengeance Runes */
		case "veng":
		case "venge":
		case "vengeance":
		case "vengerune":
		case "vengerunes":
			if (!Rights.isIron(player)) {
				player.getInventory().add(new Item(557, 1000));
				player.getInventory().add(new Item(560, 1000));
				player.getInventory().add(new Item(9075, 1000));
				player.setSpellBook(SpellBookTypes.LUNARS);
			}
			return true;

		/* Barrage Runes */
		case "barrage":
		case "barragerune":
		case "barragerunes":
			if (!Rights.isIron(player)) {
				player.getInventory().add(new Item(555, 1000));
				player.getInventory().add(new Item(560, 1000));
				player.getInventory().add(new Item(565, 1000));
				player.setSpellBook(SpellBookTypes.ANCIENTS);
				player.getActionSender().sendSidebarInterface(6, 12855);
			}
			return true;
		
		/* Yell */
		case "yell":
			if (parser.hasNext()) {
				try {
					String message = parser.nextString();
					while (parser.hasNext()) {
						message += " " + parser.nextString();
					}
					Yell.yell(player, message.trim());
				} catch (final Exception e) {
					player.getActionSender().sendMessage("Invalid yell format, syntax: -messsage");
				}
			}
			return true;

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