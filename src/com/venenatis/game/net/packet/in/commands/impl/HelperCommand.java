package com.venenatis.game.net.packet.in.commands.impl;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.help.HelpDatabase;
import com.venenatis.game.content.teleportation.Teleport.TeleportTypes;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.net.packet.in.commands.CommandParser;
import com.venenatis.game.world.World;
import com.venenatis.server.GameEngine;

public class HelperCommand implements Command {
	
	@Override
	public boolean handleCommand(Player player, CommandParser parser) throws Exception {
		switch (parser.getCommand()) {
		
		case "yellmute":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				if (World.getWorld().getPlayerByName(name).isPresent()) {
					final Player target = World.getWorld().getPlayerByName(name).get();
					player.getActionSender().sendMessage("You've yell muted "+name+".");
					target.getActionSender().sendMessage("You have been yell muted by "+player.getUsername()+".");
					target.setYellMuted(true);
					return true;
				} else {
					player.getActionSender().sendMessage("The player '" + name + "' either doesn't exist, or is offline.");
					return false;
				}
			}
			return true;
			
		case "unyellmute":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				if (World.getWorld().getPlayerByName(name).isPresent()) {
					final Player target = World.getWorld().getPlayerByName(name).get();
					player.getActionSender().sendMessage("You've lifted "+name+"'s yell ban.");
					target.getActionSender().sendMessage("You're yell punishment has been lifted by "+player.getUsername()+".");
					target.setYellMuted(false);
					return true;
				} else {
					player.getActionSender().sendMessage("The player '" + name + "' either doesn't exist, or is offline.");
					return false;
				}
			}
			return true;
		
		case "helpdb":
			HelpDatabase.getDatabase().openDatabase(player);
			return true;
		
		case "jail":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				if (World.getWorld().getPlayerByName(name).isPresent()) {
					final Player target = World.getWorld().getPlayerByName(name).get();
					target.setTeleportTarget(new Location(3015, 3194, 0));
					player.getActionSender().sendMessage("You've jailed "+name+".");
					target.getActionSender().sendMessage("You have been jailed by "+player.getUsername()+".");
					target.setJailed(true);
					return true;
				} else {
					player.getActionSender().sendMessage("The player '" + name + "' either doesn't exist, or is offline.");
					return false;
				}
			}
			return true;
			
		case "unjail":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				if (World.getWorld().getPlayerByName(name).isPresent()) {
					final Player target = World.getWorld().getPlayerByName(name).get();
					target.setTeleportTarget(Constants.RESPAWN_PLAYER_LOCATION);
					player.getActionSender().sendMessage("You have unjailed "+name+".");
					target.getActionSender().sendMessage("You have been unjailed by "+player.getUsername()+".");
					target.setJailed(false);
					return true;
				} else {
					player.getActionSender().sendMessage("The player '" + name + "' either doesn't exist, or is offline.");
					return false;
				}
			}
			return true;


		/* Staffzone */
		case "staffzone":
			player.getTeleportAction().teleport(new Location(3159, 3485, 2), TeleportTypes.SPELL_BOOK, true);
			return true;

		/* Save */
		case "save":
			GameEngine.loginMgr.requestSave(player);
			player.getActionSender().sendMessage("Submitted save request.");
			return true;

		/* Saveall */
		case "saveall":
			for (Player p2 : World.getWorld().getPlayers()) {
				if (p2 != null) {
					GameEngine.loginMgr.requestSave(p2);
					player.getActionSender().sendMessage("Submitted save requests for everybody online.");
				}
			}
			return true;

		/* Teleport to player */
		case "teletome":
		case "t2m":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				if (World.getWorld().getPlayerByName(name).isPresent()) {
					final Player target = World.getWorld().getPlayerByName(name).get();
					target.setTeleportTarget(player.getLocation());
					return true;
				} else {
					player.getActionSender().sendMessage("The player '" + name + "' either doesn't exist, or is offline.");
					return false;
				}
			}
			return true;

		/* Teleport to player */
		case "teleto":
		case "t2":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				if (World.getWorld().getPlayerByName(name).isPresent()) {

					final Player target = World.getWorld().getPlayerByName(name).get();
					player.setTeleportTarget(target.getLocation());
					return true;
				} else {
					player.getActionSender().sendMessage("The player '" + name + "' either doesn't exist, or is offline.");
					return false;
				}

			}
			return false;
			
		/* Kick */
		case "kick":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				name = name.replaceAll("_", " ");

				final String playerName = name;

				World.getWorld().kickPlayer(p -> p.getUsername().equalsIgnoreCase(playerName.trim()));
				player.getActionSender().sendMessage("You have kicked " + playerName + "!");
				return true;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean meetsRequirements(Player player) {
		return player.getRights().isHelper(player);
	}
}