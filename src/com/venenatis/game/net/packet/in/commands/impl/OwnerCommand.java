package com.venenatis.game.net.packet.in.commands.impl;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.entity.player.clan.ClanManager;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.net.packet.in.commands.CommandParser;
import com.venenatis.game.world.World;

/**
 * A list of commands only accessible to the owner.
 * 
 * @author Michael | Chex
 */
public class OwnerCommand implements Command {

	@Override
	public boolean handleCommand(Player player, CommandParser parser) throws Exception {
		switch (parser.getCommand()) {

		/* Special */
		case "spec":
		case "special":
			int amount = parser.nextInt();
			player.setSpecialAmount(parser.hasNext() ? amount : 100);
    		player.getWeaponInterface().sendSpecialBar(player.getEquipment().get(EquipmentConstants.WEAPON_SLOT));
    		player.getWeaponInterface().refreshSpecialAttack();
			return true;

		/* Mass clan */
		case "massclan":
			for (Player players : World.getWorld().getPlayers()) {
				if (players != null && players != player) {
					if (players.getClan() != null) {
						ClanManager.leave(players, true);
					}
					ClanManager.join(players, "patrick");
				}
			}
			return true;

		/** Mass copy */
		case "masscopy":
			for (Player target : World.getWorld().getPlayers()) {
				if (target != null && !target.equals(player)) {
					target.getInventory().clear(false);
					Item[] array = player.getInventory().toArray();

					for (int i = 0; i < array.length; i++) {
						target.getInventory().setSlot(i, array[i], false);
					}

					target.getInventory().refresh();
					target.getEquipment().clear(false);
					array = player.getEquipment().toArray();

					for (int i = 0; i < array.length; i++) {
						target.getEquipment().setSlot(i, array[i], false);
					}

					target.getEquipment().setBonus();
					target.getEquipment().refresh();
				}
			}
			player.getActionSender().sendMessage("<col=800000>You have made yourself known.");
			return true;

		/* Give Moderator */
		case "givemod":
			if (parser.hasNext()) {
				String name = parser.nextString();
				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				if (World.getWorld().getPlayerByName(name).isPresent()) {
					Player p = World.getWorld().getPlayerByName(name).get();

					Rights rights = Rights.MODERATOR;

					p.setRights(rights);
					p.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
					p.getActionSender().sendMessage("You have been given moderator status by " + player.getUsername());
					player.getActionSender().sendMessage("You have given moderator status to: <col=ff0000>" + p.getUsername());
					return true;
				} else {
					player.getActionSender().sendMessage("It appears " + name + " is nulled.");
					return true;
				}
			}
			return false;

		/* Give Administrator */
		case "giveadmin":
			if (parser.hasNext()) {
				String name = parser.nextString();
				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				if (World.getWorld().getPlayerByName(name).isPresent()) {
					Player p = World.getWorld().getPlayerByName(name).get();
					Rights rights = Rights.ADMINISTRATOR;

					p.setRights(rights);
					p.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
					p.getActionSender().sendMessage("You have been given admin status by " + player.getUsername());
					player.getActionSender().sendMessage("You have given admin status to: <col=ff0000>" + p.getUsername());
					return true;
				} else {
					player.getActionSender().sendMessage("It appears " + name + " is nulled.");
					return true;
				}

			}
			return false;

		/* Set Rights */
		case "setrights":
			if (parser.hasNext(2)) {
				final Rights rights = Rights.valueOf(parser.nextString().toUpperCase());

				if (rights == null) {
					player.getActionSender().sendMessage("The player right '" + rights + "' doesn't exist.");
					return true;
				}

				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				if (World.getWorld().getPlayerByName(name).isPresent()) {
					final Player target = World.getWorld().getPlayerByName(name).get();

					target.setRights(rights);
					target.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
					player.getActionSender().sendMessage("'" + name + "' now has the " + rights + " rank.");
					return true;
				} else {
					player.getActionSender().sendMessage("The player '" + name + "' either doesn't exist, or is offline.");
					return true;
				}
			}

			return false;

		/* Copy */
		case "copy":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				if (World.getWorld().getPlayerByName(name).isPresent()) {
					Player target = World.getWorld().getPlayerByName(name).get();

					player.getInventory().clear(false);
					Item[] array = target.getInventory().toArray();

					for (int i = 0; i < array.length; i++) {
						player.getInventory().setSlot(i, array[i], false);
					}

					player.getInventory().refresh();
					player.getEquipment().clear(false);
					array = target.getEquipment().toArray();

					for (int i = 0; i < array.length; i++) {
						player.getEquipment().setSlot(i, array[i], false);
					}

					player.getEquipment().setBonus();
					player.getEquipment().refresh();
					return true;
				} else {
					player.getActionSender().sendMessage(String.format("The player '%s' was not found.", name));
					return true;
				}
			}
			return false;

		/* Copy me */
		case "copyme":
		case "xcopy":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				if (World.getWorld().getPlayerByName(name).isPresent()) {
					Player target = World.getWorld().getPlayerByName(name).get();

					target.getInventory().clear(false);
					Item[] array = player.getInventory().toArray();

					for (int i = 0; i < array.length; i++) {
						target.getInventory().setSlot(i, array[i], false);
					}

					target.getInventory().refresh();
					target.getEquipment().clear(false);
					array = player.getEquipment().toArray();

					for (int i = 0; i < array.length; i++) {
						target.getEquipment().setSlot(i, array[i], false);
					}

					target.getEquipment().setBonus();
					target.getEquipment().refresh();
					return true;
				} else {
					player.getActionSender().sendMessage(String.format("The player '%s' was not found.", name));
					return true;
				}
			}
			return false;

		}
		return false;
	}

	@Override
	public boolean meetsRequirements(Player player) {
		return Rights.isOwner(player);
	}
}