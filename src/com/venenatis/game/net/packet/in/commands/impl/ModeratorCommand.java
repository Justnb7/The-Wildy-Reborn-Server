package com.venenatis.game.net.packet.in.commands.impl;

import com.venenatis.game.content.teleportation.Teleport.TeleportTypes;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
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
			
		}
		return false;
	}

	@Override
	public boolean meetsRequirements(Player player) {
		return Rights.isStaffMember(player);
	}
}