package com.venenatis.game.net.packet.in.commands.impl;

import java.util.Optional;

import com.venenatis.game.content.teleportation.Teleport.TeleportTypes;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Sanctions;
import com.venenatis.game.model.entity.player.save.PlayerSave;
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
     		
    	case "macban":
    		if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				name = name.replaceAll("_", " ");
				
				if (World.getWorld().getPlayerByName(name).isPresent()) {
					Optional<Player> optionalPlayer = World.getWorld().getOptionalPlayer(name);
					if (optionalPlayer.isPresent()) {
						Player c2 = optionalPlayer.get();
						if (c2.getRights().isBetween(1, 3)) {
							player.getActionSender().sendMessage("You cannot ban this player.");
							return false;
						}
						Sanctions.addNameToBanList(name);
						Sanctions.addNameToBanFile(name);
						player.getActionSender().sendMessage("You have MAC banned " + name + ".");
						World.getWorld().queueLogout(c2);
					}
				} else {
					player.getActionSender().sendMessage("Couldn't find player " + name + ".");
					return false;
				}
    		}
    		return true;
     		
    	case "ban":
    		if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				name = name.replaceAll("_", " ");
				
				if (World.getWorld().getPlayerByName(name).isPresent()) {
					Optional<Player> optionalPlayer = World.getWorld().getOptionalPlayer(name);
					if (optionalPlayer.isPresent()) {
						Player c2 = optionalPlayer.get();
						if (c2.getRights().isBetween(1, 3)) {
							player.getActionSender().sendMessage("You cannot ban this player.");
							return false;
						}
						Sanctions.addMacBan(c2.getMacAddress());
	 					player.getActionSender().sendMessage("[" + name + "] has been Banned");
						World.getWorld().queueLogout(c2);
					}
				} else {
					player.getActionSender().sendMessage("Couldn't find player " + name + ".");
					return false;
				}
    		}
    		return true;
    		
   	 case "banip":
   	 case "ipban":
   		if (parser.hasNext()) {
			String name = parser.nextString();

			while (parser.hasNext()) {
				name += " " + parser.nextString();
			}

			name = name.replaceAll("_", " ");
			
			if (World.getWorld().getPlayerByName(name).isPresent()) {
				Optional<Player> optionalPlayer = World.getWorld().getOptionalPlayer(name);
				if (optionalPlayer.isPresent()) {
					Player c2 = optionalPlayer.get();
					if (c2.getRights().isBetween(1, 3)) {
						player.getActionSender().sendMessage("You cannot ban this player.");
						return false;
					}
					Sanctions.addIpToBanList(c2.getHostAddress());
 					player.getActionSender().sendMessage("[" + name + "] has been IP Banned");
					World.getWorld().queueLogout(c2);
				}
			} else {
				player.getActionSender().sendMessage("Couldn't find player " + name + ".");
				return false;
			}
		}
   		 return true;
   		 
   	case "unmacban":
		
		if (parser.hasNext()) {
			String address = parser.nextString();

			while (parser.hasNext()) {
				address += " " + parser.nextString();
			}

			address = address.replaceAll("_", " ");
			if (address != null) {
				if (!Sanctions.isMacBanned(address)) {
					player.getActionSender().sendMessage("The address does not exist in the list, make sure it matches perfectly. A example 'Z8-12-F6-77-8G-D1'");
					return false;
				}
				Sanctions.removeMacBan(address);
				player.getActionSender().sendMessage("The mac ban on the address; " + address + " has been lifted.");
			}
		}
		 return true;
   		
			
    	case "unban":
    		
    		if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				name = name.replaceAll("_", " ");
				
				if (World.getWorld().getPlayerByName(name).isPresent()) {
					Sanctions.removeNameFromBanList(name);
				} else {
					player.getActionSender().sendMessage("Couldn't find player " + name + ".");
					return false;
				}
    		}
   		 return true;
   		 
		case "mute":
			if (parser.hasNext()) {
				String name = parser.nextString();

				while (parser.hasNext()) {
					name += " " + parser.nextString();
				}

				name = name.replaceAll("_", " ");
				
				if (World.getWorld().getPlayerByName(name).isPresent()) {
					Optional<Player> optionalPlayer = World.getWorld().getOptionalPlayer(name);
					if (optionalPlayer.isPresent()) {
						Player c2 = optionalPlayer.get();
						if (c2.getRights().isBetween(1, 3)) {
							player.getActionSender().sendMessage("You cannot mute this player.");
							return false;
						}
	 					c2.setMuted(true);
	 					player.getActionSender().sendMessage("You have muted " + name + ".");
					}
				} else {
					player.getActionSender().sendMessage("Couldn't find player " + name + ".");
					return false;
				}
			}
			return true;
   		 
   	 case "unmute":
   		if (parser.hasNext()) {
			String name = parser.nextString();

			while (parser.hasNext()) {
				name += " " + parser.nextString();
			}

			name = name.replaceAll("_", " ");
			
			if (World.getWorld().getPlayerByName(name).isPresent()) {
				Optional<Player> optionalPlayer = World.getWorld().getOptionalPlayer(name);
				if (optionalPlayer.isPresent()) {
					Player c2 = optionalPlayer.get();
 					c2.setMuted(false);
 					player.getActionSender().sendMessage("You have unmuted " + name + ".");
 					c2.getActionSender().sendMessage("You have been unmuted by " + player.getUsername() + ".");
				}
			} else {
				player.getActionSender().sendMessage("Couldn't find player " + name + ".");
				return false;
			}
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