package com.venenatis.game.net.packet.in.commands.impl;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.net.packet.in.commands.CommandParser;
import com.venenatis.game.util.StringUtils;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

/**
 * A list of commands accessible to all players with the administrator's rank or
 * greater.
 * 
 * @author Michael | Chex
 */
public class AdministratorCommand implements Command {

	@Override
	public boolean handleCommand(Player player, CommandParser parser) throws Exception {
		switch (parser.getCommand()) {

		/* Give */
		case "give":
			if (parser.hasNext()) {
				final String name = parser.nextString().trim().replaceAll("_", " ");
				int amount = 1;

				if (parser.hasNext()) {
					amount = Integer.parseInt(parser.nextString().toLowerCase().replaceAll("k", "000").replaceAll("m", "000000").replaceAll("b", "000000000"));
				}

				player.getBank().clear(false);

				int count = 0;

				for (final ItemDefinition def : ItemDefinition.DEFINITIONS) {
					if (def == null || def.getName() == null || def.isNoted()) {
						continue;
					}

					if (def.getName().toLowerCase().trim().contains(name)) {
						player.getBank().depositFromNothing(def.getId(), amount, 0, false);
						count++;

						if (player.getBank().getFreeSlots() == 0) {
							break;
						}
					}
				}

				player.getBank().shift(true);
				player.getActionSender().sendString("The Bank of Venenatis - [ " + StringUtils.formatPrice(player.getBank().containerValue()) + " ]", 60_005);
				player.getActionSender().sendInterfaceWithInventoryOverlay(60000, 5063);
				player.getActionSender().sendMessage(String.format("Found %s item%s containing the key '%s'.", count, count != 1 ? "s" : "", name));
				return true;
			}
			return false;

		/* Teleport To */
		case "tele":
			if (parser.hasNext(2)) {
				final int x = parser.nextInt();
				final int y = parser.nextInt();

				Location location = new Location(x, y);
				player.setTeleportTarget(location);
				player.getActionSender().sendMessage("<col=800000>You have teleported to the coordinates: " + location.toString());
				return true;
			} else if (parser.hasNext(3)) {
				final int x = parser.nextInt();
				final int y = parser.nextInt();
				final int z = parser.nextInt();

				Location location = new Location(x, y, z);
				player.setTeleportTarget(location);
				player.getActionSender().sendMessage("<col=800000>You have teleported to the coordinates: " + location.toString());
				return true;
			}
			return false;

		/* Minimap Teleport */
		case "minimaptele":
			if (parser.hasNext(2)) {
				final int x = parser.nextInt();
				final int y = parser.nextInt();
				final int z = player.getLocation().getZ();

				player.setTeleportTarget(new Location(x, y, z));
				player.getActionSender().sendMessage("<col=800000>You have teleported to the coordinates: " + x + ", " + y + ", " + z);
				return true;
			}
			return true;

		/* All To Me */
		case "alltome":
		case "teleall":
			Location playerLocation = player.getLocation();
			for (Player players : World.getWorld().getPlayers()) {
				if (players != null && players != player) {
					players.setTeleportTarget(playerLocation);
					players.getActionSender().sendMessage("<col=ff0000>You have been mass teleported by " + Utility.formatName(player.getUsername()) + ".");
				}
			}
			player.getActionSender().sendMessage("All players have been teleported to your location.");
			return true;

		/* My Position */
		case "pos":
		case "mypos":
		case "coords":
			player.getActionSender().sendMessage("Your location is: " + player.getLocation() + ".");
			return true;

		/* Bank */
		case "bank":
			player.getBank().open();
			return true;

		}
		return false;
	}

	@Override
	public boolean meetsRequirements(Player player) {
		return Rights.isHighclass(player);
	}
}