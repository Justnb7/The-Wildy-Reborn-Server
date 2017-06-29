package com.venenatis.game.net.packet.in.commands.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.entity.player.save.PlayerSerialization;
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
			//player.getMagic().teleport(new Location(3159, 3485, 2), TeleportTypes.SPELL_BOOK, true);
			return true;

		/* Save */
		case "save":
			PlayerSerialization.save(player);
			player.getActionSender().sendMessage("Your account has been successfully saved.");
			return true;

		/* Saveall */
		case "saveall":
			for (Player players : World.getWorld().getPlayers()) {
				if (players != null) {
					PlayerSerialization.save(players);
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
		}
		return false;
	}

	@Override
	public boolean meetsRequirements(Player player) {
		return Rights.isStaffMember(player);
	}
}