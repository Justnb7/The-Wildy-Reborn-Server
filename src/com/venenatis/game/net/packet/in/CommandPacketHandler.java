package com.venenatis.game.net.packet.in;

import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.entity.player.clan.ClanManager;
import com.venenatis.game.net.packet.PacketType;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.net.packet.in.commands.CommandParser;
import com.venenatis.game.net.packet.in.commands.impl.*;

/**
 * Commands
 */
public class CommandPacketHandler implements PacketType {
	
	private static final Command[] COMMANDS = new Command[] { new PlayerCommand(), new ModeratorCommand(), new OwnerCommand() };

    @Override
    public void handle(Player player, int packetType, int packetSize) {
    	final String input = player.getInStream().readString().trim().toLowerCase();
		CommandParser parser = CommandParser.create(input);
		
		if (!player.getController().canCommand()) {
			return;
		}

		if (parser.getCommand().startsWith("/")) {

			if (player.getClan() == null) {
				player.getActionSender().sendMessage("You can only do this while in a clan chat.");
				return;
			}

			parser = CommandParser.create(" " + parser.toString().substring(1));

			if (parser.hasNext()) {

				String message = "";

				while (parser.hasNext()) {
					message += parser.nextString() + " ";
				}

				if (message.contains("<img") || message.contains("<col")) {
					player.getActionSender().sendMessage("Those symbols have been disabled.");
					return;
				}

				ClanManager.message(player, message);
				return;
			}
		}

		if ((player.getDuelArena().isDueling() || player.getDuelArena().isInSession()) && !player.getRights().isOwner(player)) {
			player.getActionSender().sendMessage("You cannot use commands while dueling.");
			return;
		}

		if (!parser.getCommand().startsWith("yell")) {
			if (!MinigameHandler.execute(player, true, $it -> $it.canUseCommands(player))) {
				return;
			}
		}
		
		boolean success = false;

		try {
			for (final Command command : COMMANDS) {
				if (command.meetsRequirements(player)) {
					if (command.handleCommand(player, parser)) {
						success = true;
						return;
					}
				}
			}
		} catch (final Exception e) {
			if (player.getRights() == Rights.OWNER) {
				player.message("Exception: "+e.getMessage());
				e.printStackTrace();
			}
		}

		if (!success) {
			parser = CommandParser.create(input);
			String command = parser.getCommand();

			if (parser.hasNext()) {
				command += " { ";

				while (parser.hasNext()) {
					command += parser.nextString() + " ";
				}

				command = command.trim();
				command += " }";
			}

			player.getActionSender().sendMessage("The command ::" + command + " is invalid.");
		}
	}
    
    
    
    
    
}