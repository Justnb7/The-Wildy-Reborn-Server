package com.venenatis.game.net.packet.in.commands.impl;

import com.venenatis.game.content.teleportation.Teleport.TeleportTypes;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.save.PlayerSave;
import com.venenatis.game.model.entity.player.save.PlayerSave.PlayerContainer;
import com.venenatis.game.model.entity.player.save.PlayerSave.Type;
import com.venenatis.game.model.entity.player.save.PlayerSaveUtility;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.net.packet.in.commands.CommandParser;
import com.venenatis.game.world.World;

/**
 * A list of commands accessible to all players with the moderator's rank or
 * greater.
 * 
 * @author Michael | Chex
 */
public class ModeratorCommand implements Command {

	@Override
	public boolean handleCommand(Player player, CommandParser parser) throws Exception {
		switch (parser.getCommand()) {

		/* Staffzone */
		case "staffzone":
			player.getTeleportAction().teleport(new Location(3159, 3485, 2), TeleportTypes.SPELL_BOOK, true);
			return true;

		/* Save */
		case "save":
			PlayerSave.save(player);
			player.getActionSender().sendMessage("Your account has been successfully saved.");
			return true;

		/* Saveall */
		case "saveall":
			for (Player players : World.getWorld().getPlayers()) {
				if (players != null) {
					PlayerSave.save(players);
					player.getActionSender().sendMessage("Your account has been saved.");
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
			
    	case "kickall":
            try {
            	if (World.getWorld().getActivePlayers() > 10) {
            		player.getActionSender().sendMessage("Are you on the LIVE game? shit nigga dont wanna forcekick everyone");
            		return true;
            	} else { 
	            	for (Player op : World.getWorld().getPlayers()) {
	            		if (op == null) continue;
	    				op.logout();
	            	}
            	}
            } catch (Exception e) {
                e.printStackTrace();
                player.getActionSender().sendMessage("player must be online.");
            }
     		return true;
			
			/* Mute */
		case "mute":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				name = name.replaceAll("_", " ");

				final String playerName = name;

				if (!World.getWorld().getPlayerByName(playerName).isPresent()) {
					if (!PlayerSaveUtility.exists(name)) {
						player.getActionSender().sendMessage("It appears " + playerName + " does not exist!");
						return true;
					}

					Player player2 = new Player(name);

					if (PlayerContainer.loadDetails(player2)) {
						player2.getSanctions().mute(60);
					}

					PlayerSave.save(player, Type.PLAYER_INFORMATION);
					return true;
				} else {

					Player victim = World.getWorld().getPlayerByName(playerName).get();

					victim.getSanctions().mute(2);
					victim.getActionSender().sendMessage("You have been muted by " + playerName + "!");
					player.getActionSender().sendMessage("You have muted " + playerName + "!");
				}
				return true;
			}
			return true;
			
			/* Un-mute */
		case "unmute":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				name = name.replaceAll("_", " ");

				final String playerName = name;

				if (!World.getWorld().getPlayerByName(playerName).isPresent()) {
					if (!PlayerSaveUtility.exists(name)) {
						player.getActionSender().sendMessage("It appears " + playerName + " does not exist!");
						return true;
					}

					Player player2 = new Player(name);

					if (PlayerContainer.loadDetails(player2)) {
						player2.getSanctions().unMute();
					}

					PlayerSave.save(player, Type.PLAYER_INFORMATION);
					return true;
				} else {
					Player victim = World.getWorld().getPlayerByName(playerName).get();

					victim.getSanctions().unMute();
					victim.getActionSender().sendMessage("You have been un-muted by " + playerName + "!");
					player.getActionSender().sendMessage("You have un-muted " + playerName + "!");
					return true;
				}
			}
			return true;

		/* Banned */
		case "ban":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				name = name.replaceAll("_", " ");

				final String playerName = name;

				boolean test = World.getWorld().getPlayerByRealName(playerName).isPresent();
				
				player.debug(""+test);
				
				if (!World.getWorld().getPlayerByRealName(playerName).isPresent()) {
					if (!PlayerSaveUtility.exists(name)) {
						player.getActionSender().sendMessage("It appears " + playerName + " does not exist!");
						return true;
					}

					Player player2 = new Player(name);

					if (PlayerContainer.loadDetails(player2)) {
						player2.getSanctions().ban(60);
					}

					PlayerSave.save(player, Type.PLAYER_INFORMATION);
					return true;
				} else {

					Player victim = World.getWorld().getPlayerByName(playerName).get();

					victim.getSanctions().ban(60);
					victim.getActionSender().sendMessage("You have been banned by " + playerName + "!");
					player.getActionSender().sendMessage("You have banned " + playerName + "!");
					World.getWorld().kickPlayer(p -> p.getUsername().equalsIgnoreCase(playerName.trim()));
				}
				return true;
			}
			return true;

		/* Un-Banned */
		case "unban":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				name = name.replaceAll("_", " ");

				final String playerName = name;

				if (!World.getWorld().getPlayerByName(playerName).isPresent()) {
					if (!PlayerSaveUtility.exists(name)) {
						player.getActionSender().sendMessage("It appears " + playerName + " does not exist!");
						return true;
					}

					Player player2 = new Player(name);

					if (PlayerContainer.loadDetails(player2)) {
						player2.getSanctions().unBan();
					}

					PlayerSave.save(player, Type.PLAYER_INFORMATION);
					return true;
				} else {
					Player victim = World.getWorld().getPlayerByName(playerName).get();

					victim.getSanctions().unBan();
					victim.getActionSender().sendMessage("You have been un-banned by " + playerName + "!");
					player.getActionSender().sendMessage("You have un-banned " + playerName + "!");
				}
				return true;
			}
			return true;
			
		}
		return false;
	}

	@Override
	public boolean meetsRequirements(Player player) {
		return player.getRights().isStaffMember(player);
	}
}