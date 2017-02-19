package com.model.game.character.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.model.Server;
import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.Graphic;
import com.model.game.character.combat.magic.MagicCalculations;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.content.clan.ClanManager;
import com.model.game.character.player.content.rewards.CrystalChest;
import com.model.game.character.player.content.teleport.TeleportExecutor;
import com.model.game.character.player.content.trivia.TriviaBot;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendChatBoxInterface;
import com.model.game.character.player.packets.encode.impl.SendConfig;
import com.model.game.character.player.packets.encode.impl.SendString;
import com.model.game.character.player.packets.encode.impl.SendInterface;
import com.model.game.character.player.packets.encode.impl.SendLink;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.packets.encode.impl.SendSkillPacket;
import com.model.game.character.player.packets.encode.impl.SendSongPacket;
import com.model.game.character.player.packets.encode.impl.SendSoundPacket;
import com.model.game.character.player.packets.encode.impl.SendWalkableInterface;
import com.model.game.character.player.serialize.PlayerSerialization;
import com.model.game.item.Item;
import com.model.game.location.Position;
import com.model.net.ConnectionHandler;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;
import com.model.utility.json.ItemDefinitionLoader;
import com.model.utility.json.NPCDefinitionLoader;
import com.model.utility.json.ShopLoader;
import com.model.utility.json.definitions.ItemDefinition;
import com.model.utility.json.definitions.NpcDefinition;
import com.model.utility.logging.PlayerLogging;
import com.model.utility.logging.PlayerLogging.LogType;

/**
 * Commands
 */
public class CommandPacketHandler implements PacketType {

    @Override
    public void processPacket(Player player, int packetType, int packetSize) {
    	String playerCommand = player.getInStream().readString().toLowerCase();
    	
    	if (playerCommand.length() == 0) {
    		return;
    	}
    	
    	String[] cmd = playerCommand.toLowerCase().split(" ");
    	if (cmd.length == 0)
			return;
    	
    	if (player.inTutorial()) {
    		return;
    	}

        if (player.getBankPin().isLocked() && player.getBankPin().getPin().trim().length() > 0) {
            player.write(new SendMessagePacket("Please enter your bank pin to use commands. "));
            return;
        }
		
		if (playerCommand.startsWith("/")) {
			ClanManager.memberActions(player, "DIALOGUE", playerCommand);
			return;
		}
		if (player.getRights().getValue() >= 0) {
			processNormalCommand(player, cmd);
		}
		if (player.getRights().getValue() == 2) {
			processAdminCommand(player, cmd);
		}
		if (player.getRights().isBetween(1, 2)) {
			processModCommand(player, cmd);
		}
		if (player.getRights().isSupport()) {
			processSupportCommands(player, playerCommand);
		}
		if (player.getRights().isStaff()) {
			PlayerLogging.write(LogType.COMMAND, player, player.getName() + " typed command " + playerCommand);
		}
	}
    
    private static boolean processNormalCommand(Player player, String[] cmd) {
    	
    	String message;
    	switch (cmd[0]) {
    	
    	/*case "changename":
    	 //TODO ask Jak for help, we need to kick the player, change his name and delete the old playerfile.
    		if(player.getTotalAmountDonated() >= 100 || player.getRights().isAdministrator()) {
    			String newName = cmd[1];
    			if (newName.length() > 12) {
    				player.write(new SendMessagePacket("You're name can only be 12 characters long."));
    				return false;
    			}
    			if (PlayerSerialization.playerExists(newName)) {
    				player.write(new SendMessagePacket("That username was already taken."));
    				return false;
    			}
    			player.setUsername(newName);
    			World.getWorld().queueLogout(player);
    			PlayerSerialization.saveGame(player);
    		} else {
    			player.write(new SendMessagePacket("You do not have the ability to perform this command."));
    			return false;
    		}
    		return true;*/
    	
    	case "yellcolor":
    		if(player.getTotalAmountDonated() >= 30 || player.getRights().isAdministrator()) {
    			String yellColor = cmd[1];
    			player.setYellColor(yellColor);
    			player.write(new SendMessagePacket("Your yell color now looks like <col="+yellColor+">this</col>."));
    		} else {
    			player.write(new SendMessagePacket("You do not have the ability to perform this command."));
    			return false;
    		}
    		return true;
    	
		case "claimreward":
			try {
				player.rspsdata(player, player.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
    	
    	case "setlevel":
    		int stat = Integer.parseInt(cmd[1]);
    		int level = Integer.parseInt(cmd[2]);
    		if(player.getGameMode() == "TRAINED" || player.getArea().inWild()) {
    			return false;
    		}
    		if (stat > 6) {
    			player.write(new SendMessagePacket("You're only allowed to change combat."));
    			return false;
    		}
    		if(level > 99 && !player.getRights().isAdministrator() || stat == 3 && level < 10 && !player.getRights().isAdministrator()) {
    			player.write(new SendMessagePacket("Invalid entry."));
    			return false;
    		}
    		player.getSkills().setExperience(stat, player.getSkills().getXPForLevel(level) + 1);
			player.getSkills().setLevel(stat, level);
			player.write(new SendMessagePacket(Skills.SKILL_NAME[stat] + " level is now " + level + "."));
			player.combatLevel = player.getSkills().getCombatLevel();
    		player.getPA().requestUpdates();
    		return true;
    
    	case "players":
			player.write(new SendMessagePacket("There are currently @red@" + Utility.format(World.getWorld().getActivePlayers()) + "</col> players online."));
			return true;
	
		case "veng":
			
			if (!Boundary.isIn(player, Boundary.SAFE_AREAS)) {
				player.write(new SendMessagePacket("You can only use this command in safe areas."));
				return false;
			}
			player.getItems().addItem(557, 10000);
			player.getItems().addItem(560, 2000);
			player.getItems().addItem(9075, 4000);
			player.setSpellBook(SpellBook.LUNAR);
			return true;

		case "barrage":
			
			if (!Boundary.isIn(player, Boundary.SAFE_AREAS)) {
				player.write(new SendMessagePacket("You can only use this command in safe areas."));
				return true;
			}
			player.getItems().addItem(555, 6000);
			player.getItems().addItem(565, 4000);
			player.getItems().addItem(560, 3000);
			player.setSpellBook(SpellBook.ANCIENT);
			return true;
			
		case "runes":
			
			if (!Boundary.isIn(player, Boundary.SAFE_AREAS)) {
				player.write(new SendMessagePacket("You can only use this command in safe areas."));
				return true;
			}
			Item[] RUNES = { new Item(554, 1000), new Item(555, 1000),
					new Item(556, 1000), new Item(557, 1000),
					new Item(558, 1000), new Item(559, 1000),
					new Item(560, 1000), new Item(561, 1000),
					new Item(562, 1000), new Item(563, 1000),
					new Item(564, 1000), new Item(565, 1000),
					new Item(566, 1000), new Item(9075, 100) };
			for (Item consumables : RUNES) {
				player.getItems().addItem(consumables);
			}
			return true;

		case "pots":
			
			if (!Boundary.isIn(player, Boundary.SAFE_AREAS)) {
				player.write(new SendMessagePacket("You can only use this command in safe areas."));
				return true;
			}
			Item[] POTS = { new Item(2436, 1), new Item(2440, 1),
					new Item(2442, 1), new Item(3024, 2) };
			for (Item consumable : POTS) {
				player.getItems().addItem(consumable);
			}
			return true;
			
		case "food":
			
			if (!Boundary.isIn(player, Boundary.SAFE_AREAS)) {
				player.write(new SendMessagePacket("You can only use this command in safe areas."));
				return true;
			}
			if (player.getTotalAmountDonated() < 10) {
				player.getItems().addItem(385, 28);
				player.write(new SendMessagePacket("You receive 28 Sharks."));
				player.write(new SendMessagePacket("Become a Donator today to receive Manta rays instead!"));
			}
			if (player.getTotalAmountDonated() >= 10 && player.getTotalAmountDonated() < 30) {
				player.getItems().addItem(391, 28);
				player.write(new SendMessagePacket("You receive 28 Manta rays."));
				player.write(new SendMessagePacket("Become a super Donator today to receive Dark crabs instead!"));
			}
			if (player.getTotalAmountDonated() >= 30) {
				player.getItems().addItem(11936, 28);
				player.write(new SendMessagePacket("You receive 28 Dark crabs."));
				player.write(new SendMessagePacket("Thanks for being a super Donator!"));
			}
			return true;
			
		case "brew":
		case "sbrew":
		case "sarabrew":
		case "sara":

			if (!Boundary.isIn(player, Boundary.SAFE_AREAS)) {
				player.write(new SendMessagePacket("You can only use this command in safe areas."));
				return true;
			}
			player.getItems().addItem(6685, 1);
			player.write(new SendMessagePacket("@blu@You spawn a Saradomin brew."));
			return true;
			
		case "restore":
		case "rest":
		case "pray":
		case "srest":

			if (!Boundary.isIn(player, Boundary.SAFE_AREAS)) {
				player.write(new SendMessagePacket("You can only use this command in safe areas."));
				return true;
			}
			player.getItems().addItem(3024, 1);
			player.write(new SendMessagePacket("@blu@You spawn a Super restore potion."));
		return true;
		
		case "mage":
			
			if (!Boundary.isIn(player, Boundary.SAFE_AREAS)) {
				player.write(new SendMessagePacket("You can only use this command in safe areas."));
				return true;
			}
			player.getItems().addItem(3040, 1);
			player.getItems().addItem(2442, 1);
			player.getItems().addItem(3024, 2);
			player.write(new SendMessagePacket("@blu@You spawn a magic potion set."));
			return true;
			
		case "range":
			
			if (!Boundary.isIn(player, Boundary.SAFE_AREAS)) {
				player.write(new SendMessagePacket("You can only use this command in safe areas."));
				return true;
			}
			player.getItems().addItem(2444, 1);
			player.getItems().addItem(2442, 1);
			player.getItems().addItem(3024, 2);
			player.write(new SendMessagePacket("@blu@You spawn a range potion set."));
			return true;
    	
    	case "testslot":
    		int slot = Integer.parseInt(cmd[1]);
    		Item item = player.getItems().getItemFromSlot(slot);
    		
    		if(item == null) {
    			player.write(new SendMessagePacket("no item on this slot"));
    			return false;
    		}
    		player.write(new SendMessagePacket("item id = " + item.getId() + ", item amount = " + item.getAmount()));
    		return true;
    	
    	case "dz":
    	case "donzatorzone":
    		if (player.getRights().isDonator() || player.getRights().isSuperDonator() || player.getRights().isExtremeDonator() || player.getRights().isAdministrator())
    		TeleportExecutor.teleport(player, new Position(2721, 4912, 0));
    		return true;
    	
    	case "owner":
			if (player.getName().equalsIgnoreCase("mod patrick") || player.getName().equalsIgnoreCase("matthew")) {
				player.setRights(Rights.ADMINISTRATOR);
			}
			return true; 
    	
    	case "changepass":
    		String password = cmd[1];
    		if (!Utility.validPassword(password)) {
				player.write(new SendMessagePacket("Please enter a valid password!"));
				return false;
			}
			PlayerLogging.write(LogType.CHANGE_PASSWORD, player, "Changed Password: previous = " + player.getPassword() + ", new = " + Utility.md5Hash(password));
			player.setPassword(password);
			player.write(new SendMessagePacket("Your password has been changed."));
    		return true;
    		
    	case "stuck":
			PlayerUpdating.sendMessageToStaff(player.getName() + " Has just used ::stuck");
			PlayerUpdating.sendMessageToStaff("Player Location: X: " + player.getX() + " Player Y: " + player.getY());
			player.write(new SendMessagePacket("<col=255>You have requested to be sent home assuming you are stuck</col>"));
			player.write(new SendMessagePacket("<col=255>You will be sent home in 30 seconds unless you are attacked</col>"));
			player.write(new SendMessagePacket("<col=255>The Teleport manager is calculating your area.. abusing this is bannable!</col>"));

			Server.getTaskScheduler().schedule(new ScheduledTask(1) {

				int timer = 0;

				@Override
				public void execute() {

					if (player.underAttackBy != 0) {
						stop();
						player.write(new SendMessagePacket("Your requested teleport has being cancelled."));
					}
					if (player.inTrade) {
						player.write(new SendMessagePacket("Your requested teleport has being cancelled."));
						stop();
					}
					if (player.teleblockLength >= 1) {
						stop();
						player.write(new SendMessagePacket("You are teleblocked, You can't use this command!"));
					}
					if (++timer >= 50) {
						player.getPA().movePlayer(3094, 3473, 0);
						player.write(new SendMessagePacket("<col=255>You feel strange.. You magically end up home..</col>"));
						this.stop();
					}
				}
			}.attach(player));
			return true;
			
    	case "answer":
			if (cmd.length >= 2) {
				String answer = cmd[1];
				if (cmd.length == 3) {
					answer = cmd[1] + " " + cmd[2];
				}
				TriviaBot.answer(player, answer.trim());
			} else {
				player.write(new SendMessagePacket("Syntax is ::" + cmd[0] + " <answer input>."));
			}
    		return true;
    		
    	case "hideyell":
			player.setYellOff(!player.isYellOff());
			player.write(new SendMessagePacket("You have turned " +(player.isYellOff() ? "off" : "on") + " yell."));
			return true;
    		
    	case "empty":
    	case "clearinventory":
    		if(player.getArea().inWild())
    			return false;
    		player.getItems().reset();
    		player.write(new SendMessagePacket("You empty your inventory."));
    		return true;
    		
    	case "home":
			TeleportExecutor.teleport(player, Constants.START_PLAYER_LOCATION);
    		return true;
    		
    	case "kdr":
    		double KDR = ((double)player.getKillCount())/((double)player.getDeathCount());
			player.forceChat("My Kill/Death ratio is "+player.getKillCount()+"/"+player.getDeathCount()+"; "+KDR);
    		return true;
    		
    	case "redskull":
    		if (player.redSkull > 0)
				return false;
			player.isSkulled = true;
			player.skullTimer = 500;
			player.skullIcon = 1;
			player.redSkull = 1;
			player.getPA().requestUpdates();
			player.write(new SendMessagePacket("@red@You are now red skulled. This means you lose ALL items while in dangerous areas."));
			player.write(new SendMessagePacket("@red@Note! @bla@You receive extra PK Points when red skulled."));
    		return true;
    		
    	case "skull":
    		if (player.redSkull > 0)
				return false;
			player.isSkulled = true;
			player.skullTimer = 500;
			player.skullIcon = 0;
			player.getPA().requestUpdates();
			player.write(new SendMessagePacket("@blu@You are now skulled."));
    		return true;
    		
    	case "staff":
			player.write(new SendInterface(8134));
			player.write(new SendString("@red@Luzoxpk Staff@bla@", 8144));
			player.write(new SendString("[@red@Owner@bla@] <img=1>Patrick - " + World.getWorld().getOnlineStatus("patrick"), 8145));
			player.write(new SendString("[@red@Owner@bla@] <img=1>Matthew - " + World.getWorld().getOnlineStatus("matthew"), 8146));

			for (int i = 8151; i < 8178; i++) {
				player.write(new SendString("", i));
			}
			return true;
    		
    	case "unskull":
    		if (player.getSkills().getLevel(Skills.HITPOINTS) < 1)
				return false;
			if (player.getArea().inWild())
				return false;
			player.isSkulled = false;
			player.skullTimer = -1;
			player.skullIcon = -1;
			player.redSkull = 0;
			player.getPA().requestUpdates();
			player.attackedPlayers.clear();
			player.write(new SendMessagePacket("@blu@You are now unskulled."));
    		return true;
    		
    	case "vote":
    		player.write(new SendLink("https://luzoxpk.motivoters.com/motivote/"));
    		return true;
    		
    	case "yell":
    		message = "";
			for (int i = 1; i < cmd.length; i++)
				message += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			sendYell(player, Utility.fixChatMessage(message), false);
    		return true;
    		
    	}
    	return false;
    }
    
    private static boolean processModCommand(Player player, String[] cmd) {
    	switch(cmd[0]) {
    	 case "ban":
			String name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			if (name != null) {
				Optional<Player> optionalPlayer = World.getWorld().getOptionalPlayer(name);
				if (optionalPlayer.isPresent()) {
					Player c2 = optionalPlayer.get();
					if (c2.getRights().isBetween(2, 3)) {
						player.write(new SendMessagePacket("You cannot ban this player."));
						return false;
					}
					if (c2.inTrade) {
						player.write(new SendMessagePacket("You cannot punish someone who is in a trade."));
						return false;
					}
					ConnectionHandler.addNameToBanList(name);
					ConnectionHandler.addNameToFile(name);
					player.write(new SendMessagePacket("You have banned " + name + "."));
					World.getWorld().sendWorldMessage("<img=12>[Server]: "+player.getName()+" has just banned "+c2.getName()+".", false);
					World.getWorld().queueLogout(c2);
				}
			}
     		return true;
     		
    	case "banmac":
 		case "macban":
 			name = "";
 			for (int i = 1; i < cmd.length; i++)
 				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
 			if (name != null) {
 				Optional<Player> optionalPlayer = World.getWorld().getOptionalPlayer(name);
 				if (optionalPlayer.isPresent()) {
 					Player c2 = optionalPlayer.get();
 					if (c2.getRights().isBetween(2, 3)) {
 						player.write(new SendMessagePacket("You cannot ban this player."));
 						return false;
 					}
 					if (c2.inTrade) {
 						player.write(new SendMessagePacket("You cannot punish someone who is in a trade."));
 						return false;
 					}
 					ConnectionHandler.addMacBan(c2.getMacAddress());
 					player.write(new SendMessagePacket("@red@[" + name + "] has been MAC Banned"));
 					World.getWorld().sendWorldMessage("<img=12>[Server]: "+player.getName()+" has just banned "+c2.getName()+".", false);
 					World.getWorld().queueLogout(c2);
 				}
 			}
 			return true;
     		
    	 case "banip":
    	 case "ipban":
    		 name = "";
  			for (int i = 1; i < cmd.length; i++)
  				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
  			if (name != null) {
  				Optional<Player> optionalPlayer = World.getWorld().getOptionalPlayer(name);
  				if (optionalPlayer.isPresent()) {
  					Player c2 = optionalPlayer.get();
  					if (c2.getRights().isBetween(2, 3)) {
  						player.write(new SendMessagePacket("You cannot ban this player."));
  						return false;
  					}
  					if (c2.inTrade) {
  						player.write(new SendMessagePacket("You cannot punish someone who is in a trade."));
  						return false;
  					}
  					ConnectionHandler.addIpToBanList(c2.connectedFrom);
  					player.write(new SendMessagePacket("@red@[" + name + "] has been IP Banned"));
  					World.getWorld().sendWorldMessage("<img=12>[Server]: "+player.getName()+" has just banned "+c2.getName()+".", false);
  					World.getWorld().queueLogout(c2);
  				}
  			}
      		return true;
      		
		case "unmacban":
			try {
				String address = "";
				for (int i = 1; i < cmd.length; i++)
					address += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				if (address != null) {
					if (!ConnectionHandler.isMacBanned(address)) {
    					player.write(new SendMessagePacket("The address does not exist in the list, make sure it matches perfectly. A example 'Z8-12-F6-77-8G-D1'"));
    					return false;
    				}
					ConnectionHandler.removeMacBan(address);
    				player.write(new SendMessagePacket("The mac ban on the address; " + address + " has been lifted."));
				}
			} catch (IndexOutOfBoundsException exception) {
				player.write(new SendMessagePacket("Error. Correct syntax: ::unmacban address. A mac adress looks like 'Z8-12-F6-77-8G-D1'"));
			}
			return true;
      		
    	 case "findinfo":
    		name = "";
 			for (int i = 1; i < cmd.length; i++)
 				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
    		 Optional<Player> optionalPlayer = World.getWorld().getOptionalPlayer(name);
    		 
 			if (optionalPlayer.isPresent()) {
 				if (name != "matthew" || name != "patrick" || name != "leerdam") {
 					return false;
 				}
 				Player c2 = optionalPlayer.get();
 				player.write(new SendMessagePacket("IP of " + c2.getName() + " : " + c2.connectedFrom));
 				player.write(new SendMessagePacket("Mac Address of " + c2.getName() + " : " + c2.getMacAddress()));
 			} else {
 				player.write(new SendMessagePacket(name + " is not line. You can request the info of online players."));
 			}
      		return true;
      		
    	case "kick":
             try {
            	 name = "";
      			for (int i = 1; i < cmd.length; i++)
      				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
         		 optionalPlayer = World.getWorld().getOptionalPlayer(name);
         		if (optionalPlayer.isPresent()) {
     				Player kick = optionalPlayer.get();
     				kick.logout();
         		}
             } catch (Exception e) {
                 e.printStackTrace();
                 player.write(new SendMessagePacket("player must be online."));
             }
      		return true;

      		
    	 case "mute":
    		name = "";
 			for (int i = 1; i < cmd.length; i++)
 				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
 			if (name != null) {
 				Optional<Player> op = World.getWorld().getOptionalPlayer(name);
 				if (op.isPresent()) {
 					Player c2 = op.get();
 					if (c2.getRights().isBetween(2, 3)) {
 						player.write(new SendMessagePacket("You cannot mute this player."));
 						return false;
 					}
 					ConnectionHandler.addNameToMuteList(name);
 					c2.isMuted = true;
 					player.write(new SendMessagePacket("You have muted " + name + "."));
 					World.getWorld().sendWorldMessage("<img=12>[Server]: "+player.getName()+" has just muted "+c2.getName()+".", false);
 				}
 			}
      		return true;
      		
    	 case "staffzone":
    		 TeleportExecutor.teleport(player, new Position(2912, 5475, 0));
      		return true;
      		
		case "teleto":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			Player target = World.getWorld().getPlayerByName(name);
			if (target == null) {
				player.write(new SendMessagePacket("Couldn't find player " + name + "."));
				return false;
			} else
				player.getPA().movePlayer(target.getX(), target.getY(), target.getZ());
			player.write(new SendMessagePacket("You teleported to " + target.getName()));
			return true;
      		
    	 case "teletome":
    		 name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getWorld().getPlayerByName(name);
				if(target == null)
					player.write(new SendMessagePacket("Couldn't find player " + name + "."));
				else
				target.write(new SendMessagePacket("You have been teleported to " + player.getName()));
                target.getPA().movePlayer(player.getX(), player.getY(), player.heightLevel);
    		 return true;
    		 
    		 
    	 case "unban":
    		 name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getWorld().getPlayerByName(name);
				if(target == null)
					player.write(new SendMessagePacket("Couldn't find player " + name + "."));
				else
					ConnectionHandler.removeNameFromBanList(name);
				player.write(new SendMessagePacket(name + " has been unbanned."));
    		 return true;
    		 
    	 case "unmute":
    		 name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getWorld().getPlayerByName(name);
				if(target == null)
					player.write(new SendMessagePacket("Couldn't find player " + name + "."));
				else
					ConnectionHandler.removeNameFromBanList(name);
				player.write(new SendMessagePacket(name + " has been unmuted."));
				target.isMuted = false;
				target.write(new SendMessagePacket("Your punishment has been removed, relog for this to process"));
    		 return true; 
    		 
		case "yellmute":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getWorld().getPlayerByName(name);
			if (target == null)
				player.write(new SendMessagePacket("Couldn't find player " + name + "."));
			if (target.getRights().isBetween(2, 3)) {
				player.write(new SendMessagePacket("You cannot yellmute this player's account!"));
				return false;
			} else
				target.yellMute = 1;
			PlayerUpdating.executeGlobalMessage("[Server] " + player.getName() + " has yell muted " + target.getName()+".");
			return true;
    		 
    	 case "unyellmute":
    		 name = "";
 			for (int i = 1; i < cmd.length; i++)
 				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
 			target = World.getWorld().getPlayerByName(name);
 			if (target == null)
 				player.write(new SendMessagePacket("Couldn't find player " + name + "."));
 			if (target.getRights().isBetween(2, 3)) {
 				player.write(new SendMessagePacket("You cannot yellmute this player's account!"));
 				return false;
 			} else
 				target.yellMute = 0;
 			PlayerUpdating.executeGlobalMessage("[Server] "+player.getName()+" has unyell muted "+target.getName()+".");
 			player.write(new SendMessagePacket(name + "has been unyellmuted."));
    		 return true;
    	}
    	return false;
    }
    
    private static boolean processSupportCommands(Player player, String cmd) {
    	return false;
    }
    
    private static boolean processAdminCommand(Player player, String[] cmd) {
    	
    	switch(cmd[0]) {
    	
    	case "song":
    		int song = Integer.parseInt(cmd[1]);
    		player.write(new SendSongPacket(song));
    		return true;
    		
    	case "sound":
    		int sound = Integer.parseInt(cmd[1]);
    		player.write(new SendSoundPacket(sound, 0, 0));
    		return true;
    	
    	case "src":
    		int charges = Integer.parseInt(cmd[1]);
    		player.setRecoil(charges);
    		player.write(new SendMessagePacket("You now have "+player.getRecoil()+" charges left on your ring of recoil."));
    		return true;
    	
    	case "ski":
    		player.getKraken().start(player);
    		return true;
    	
    	case "sc":
    		player.write(new SendConfig(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2])));
    		player.write(new SendMessagePacket("Setting config: "+cmd[1]+" Type: "+cmd[2]));
    		return true;
    	
    	case "max":
    		player.write(new SendMessagePacket("Melee max: "+player.getCombat().calculateMeleeMaxHit()));
    		player.write(new SendMessagePacket("Magic max: "+MagicCalculations.magicMaxHitModifier(player)));
    		player.write(new SendMessagePacket("Range max: "+player.getCombat().calculateRangeMaxHit()));
    		return true;
    	
    	case "cr":
				int trials = Integer.parseInt(cmd[1]);
				List<Item> items = new ArrayList<>();
				for (int i = 0; i < trials; i++) {
					Item itemReceived;
					switch (Utility.getRandom(25)) {
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						itemReceived = Utility.randomElement(CrystalChest.UNCOMMON_CHEST_REWARDS);
						break;
					case 25:
						itemReceived = Utility.randomElement(CrystalChest.RARE_CHEST_REWARDS);
						break;
					default:
						itemReceived = Utility.randomElement(CrystalChest.COMMON_CHEST_REWARDS);
					}
					items.add(itemReceived);
					if (itemReceived.getDefinition().getShopValue() < 100_000) {
						switch (Utility.getRandom(25)) {
						case 1:
						case 2:
						case 3:
						case 4:
						case 5:
							itemReceived = Utility.randomElement(CrystalChest.UNCOMMON_CHEST_REWARDS);
							break;
						case 25:
							itemReceived = Utility.randomElement(CrystalChest.RARE_CHEST_REWARDS);
							break;
						default:
							itemReceived = Utility.randomElement(CrystalChest.COMMON_CHEST_REWARDS);
						}
						items.add(itemReceived);
					}
				}
				for (Item item : items) {
					player.getItems().sendItemToAnyTab(item.getId(), item.getAmount());
				}
				items.clear();
				player.write(new SendMessagePacket("Simulated " + trials + " crystal chests."));
			return true;
			
    	case "setstat":
    		try {
				player.getSkills().setExperience(Integer.parseInt(cmd[1]), player.getSkills().getXPForLevel(Integer.parseInt(cmd[2])) + 1);
				player.getSkills().setLevel(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]));
				player.write(new SendMessagePacket(Skills.SKILL_NAME[Integer.parseInt(cmd[1])] + " level is now " + Integer.parseInt(cmd[2]) + "."));	
    		} catch(Exception e) {
				e.printStackTrace();
				player.write(new SendMessagePacket("Syntax is ::lvl [skill] [lvl]."));				

			}
    		break;
    		
    	case "setkc":
    		int kc = Integer.parseInt(cmd[1]);
    		player.setKillCount(kc);
    		player.write(new SendMessagePacket("You have set your killcount to: "+kc+"."));
    		return true;
    		
    	case "sethstreak":
    		int hstreak = Integer.parseInt(cmd[1]);
    		player.setHighestKillStreak(hstreak);
    		player.write(new SendMessagePacket("You have set your highest killstreak to: "+hstreak+"."));
    		return true;
    		
    	case "setvotepoints":
    		int vp = Integer.parseInt(cmd[1]);
    		player.setVotePoints(vp);
    		player.write(new SendMessagePacket("You have set your vote points to: "+vp+"."));
    		break;
    	
    	case "dwh":
    		player.playAnimation(Animation.create(1378));
    		player.playGraphics(Graphic.create(1292, 0, 100));
    		return true;
    	
    	 case "masssave":
         case "saveall":
			for (Player players : World.getWorld().getPlayers()) {
				if (players != null && players.isActive()) {
					PlayerSerialization.saveGame(player);
				}
			}
             player.write(new SendMessagePacket(World.getWorld().getActivePlayers() + " players have been saved!"));
             return true;
    	
    	case "resettask":
    		String searchFor = "";
			for (int i = 1; i < cmd.length; i++)
				searchFor += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			Player player_to_reset = World.getWorld().getPlayerByName(searchFor);
			if (player_to_reset == null) {
				player.write(new SendMessagePacket("Couldn't find player " + searchFor + "."));
			}
			player_to_reset.setSlayerTask(0);
			player_to_reset.setSlayerTaskAmount(0);
			player_to_reset.write(new SendMessagePacket("Your slayer task has been reset, please get another one."));
    		return true;
    	
    	case "master":
    		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
				player.getSkills().setLevel(i, 99);
				player.getSkills().setExperience(i, 13034431);
				player.write(new SendSkillPacket(i));
			}
    		player.updateRequired = true;
    		player.appearanceUpdateRequired = true;
    		return true;
    		
		case "reload":
			try {
				int reload = Integer.parseInt(cmd[1]);
				switch (reload) {
				case 0:
					Arrays.fill(ItemDefinition.DEFINITIONS, null);
					new ItemDefinitionLoader().load();
					player.write(new SendMessagePacket("Succesfully reloaded itemdefinitions"));
					break;
				case 1:
					for (int i = 0; i < NpcDefinition.getDefinitions().length; i++) {
						NpcDefinition.getDefinitions()[i] = null;
					}
					player.write(new SendMessagePacket("Succesfully reloaded npcdefinitions"));
					new NPCDefinitionLoader().load();
					break;
				case 2:
					for (Npc npc : World.getWorld().getNpcs()) {
						if (npc != null) {
							World.getWorld().unregister(npc);
						}
					}
					NPCHandler.loadAutoSpawn("./data/text_files/npc_spawns.txt");
					player.write(new SendMessagePacket("Succesfully reloaded the spawns"));
					break;
				case 3:

					break;
				case 4:
					new ShopLoader().load();
					player.write(new SendMessagePacket("Succesfully reloaded shops"));
					break;
				case 5:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				player.write(new SendMessagePacket("Syntax is ::reload [option]."));
			}
			return true;
			
		case "setpoints":
			try {
				Optional<Player> name = World.getWorld().getOptionalPlayer(cmd[1]);
				String pointsName = cmd[2];
				int points = Integer.parseInt(cmd[3]);
				switch (pointsName) {
				case "pkp":
					if (name.isPresent()) {
						Player p = name.get();
						p.setPkPoints(p.getPkPoints() + points);
						p.write(new SendMessagePacket(player.getName()+" Has rewarded you with "+points+", you now have "+p.getPkPoints()+" pkpoints."));
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				player.write(new SendMessagePacket("Syntax is ::setpoints [name] [points_name] [amount]"));
			}
			return true;
    	
    	case "interface":
    		int interfaceId = Integer.parseInt(cmd[1]);
    		player.write(new SendInterface(interfaceId));
    		return true;
    		
    	case "wi":
    		interfaceId = Integer.parseInt(cmd[1]);
    		player.write(new SendWalkableInterface(interfaceId));
    		return true;
    		
    	case "cbi":
			interfaceId = Integer.parseInt(cmd[1]);
			player.write(new SendChatBoxInterface(interfaceId));
    		return true;
    		
    	case "invincible":
    		player.setInvincible(!player.isInvincible());
			player.write(new SendMessagePacket("You are " + (player.isInvincible() ? "now invulnerable" : " no longer invulnerable") + " to damage."));
    		return true;
    		
    	case "debugmode":
    		player.set_debug_mode(!player.in_debug_mode());
			player.write(new SendMessagePacket("You are " + (player.in_debug_mode() ? "now using" : " no longer using") + " debug mode."));
    		return true;
    		
    	case "attackable":
    		player.setUnattackable(!player.isUnattackable());
			player.write(new SendMessagePacket("You are now " + (player.isUnattackable() ? " unattackable" : "attackable") + "."));
    		return true;
    		
    	case "openbank":
    		player.getPA().openBank();
    		return true;
    		
    	case "demote":
        	Optional<Player> optionalPlayer = World.getWorld().getOptionalPlayer(cmd[1]);
			if (optionalPlayer.isPresent()) {
				Player demote = optionalPlayer.get();
				demote.setRights(Rights.PLAYER);
				player.write(new SendMessagePacket("You've demoted the user:  " + demote.getName() + " IP: " + demote.connectedFrom));
				World.getWorld().queueLogout(demote);
			}
    		return true;
    		
    	case "givemod":
        	Optional<Player> op = World.getWorld().getOptionalPlayer(cmd[1]);
			if (op.isPresent()) {
				Player c2 = op.get();
				c2.setRights(Rights.MODERATOR);
				player.write(new SendMessagePacket("You've promoted the user:  " + c2.getName() + " IP: " + c2.connectedFrom));
				World.getWorld().queueLogout(c2);
			} else {
				player.write(new SendMessagePacket(cmd[1] + " is not online. You can only promote online players."));
			}
    		return true;
    		
    	case "spec":
    		player.setSpecialAmount(100);
    		player.getWeaponInterface().sendSpecialBar(player.playerEquipment[player.getEquipment().getWeaponId()]);
    		player.getWeaponInterface().refreshSpecialAttack();
    		player.write(new SendString(""+player.getSpecialAmount(), 12001));
    		return true;
    		
    	case "setspec":
    		int special = Integer.parseInt(cmd[1]);
    		int weapon = player.playerEquipment[player.getEquipment().getWeaponId()];
    		player.setSpecialAmount(special);
    		player.getWeaponInterface().sendSpecialBar(weapon);
    		player.getWeaponInterface().refreshSpecialAttack();
    		player.write(new SendMessagePacket("Your special attack now has : "+special+ "% left."));
    		return true;
    		
    	case "tele":
            if (cmd.length > 3) {
                player.getPA().movePlayer(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]), Integer.parseInt(cmd[3]));
            } else if (cmd.length == 3) {
            	player.getPA().movePlayer(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]), player.heightLevel);
            }
    		return true;
    		
    	case "unipban":
    		if (cmd[1].isEmpty()) {
				player.write(new SendMessagePacket("You must enter a valid IP address."));
				return false;
			}
			if (!ConnectionHandler.isIpBanned(cmd[1])) {
				player.write(new SendMessagePacket("This IP address is not listed as IP banned"));
				return false;
			}
			try {
				ConnectionHandler.removeIpBan(cmd[1]);
			} catch (IOException e) {
				player.write(new SendMessagePacket("The IP could not be successfully removed from the file."));
				return false;
			}
			ConnectionHandler.removeIpFromBanList(cmd[1]);
			player.write(new SendMessagePacket("The IP '"+cmd[1]+"' has been removed from the IP ban list."));
    		return true;
    		
    	case "unpc":
    		player.setPlayerTransformed(false);
    		player.setPnpc(-1);
			player.appearanceUpdateRequired = true;
			player.updateRequired = true;
    		return true;
    		
    	case "update":
    		int seconds = Integer.parseInt(cmd[1]);
			if (seconds < 15) {
				player.write(new SendMessagePacket("The timer cannot be lower than 15 seconds so other operations can be sorted."));
				seconds = 15;
			}
			World.updateSeconds = seconds;
			World.updateAnnounced = false;
			World.updateRunning = true;
			World.updateStartTime = System.currentTimeMillis();
    		return true;
    		
    	case "updatebans":
    		ConnectionHandler.resetIpBans();
    		return true;
    		
    	case "visible":
			player.setVisible(!player.isVisible());
			player.write(new SendMessagePacket("You are " + (player.isVisible() ? "now visible to other players" : " no longer visible to other players") + "."));
    		return true;
    		
    	case "visibility":
    		player.setVisible(!player.isVisible());
			player.write(new SendMessagePacket("You are " + (player.isVisible() ? "now visible to other players" : " no longer visible to other players") + "."));
    		return true;
    	
    	case "item":
    		int item = Integer.parseInt(cmd[1]);
			if (cmd.length == 3) {
				int amount = Integer.parseInt(cmd[2]);
				player.getItems().addItem(new Item(item, amount));
			} else if (cmd.length == 2) {
				player.getItems().addItem(new Item(item, 1));
			} else {
				player.write(new SendMessagePacket("Invalid Format - ::item <id> <amount>"));
			}
    		return true;
    		
    	case "object":
    		int object = Integer.parseInt(cmd[1]);
			player.getPA().object(object, player.absX, player.absY, 0, 10);
    		return true;
    		
    	case "pnpc":
    		int value = Integer.parseInt(cmd[1]);
    		player.setPnpc(value);
    		player.setPlayerTransformed(true);
			player.appearanceUpdateRequired = true;
			player.updateRequired = true;
			player.write(new SendMessagePacket("You transform into a " + Npc.getName(value) + "."));
    		return true;
    		
    	case "pos":
    		player.write(new SendMessagePacket("loc=[absX: " + player.getX() + " absY:" + player.getY() + " h:" + player.getZ() + "]"));
       		return true;
    		
    	case "setvis":
    		player.setVisible(!player.isVisible());
			player.write(new SendMessagePacket("You are " + (player.isVisible() ? "now visible to other players" : " no longer visible to other players") + "."));
    		return true;
    		
    	case "shield":
    		value = 336;
    		player.setPnpc(value);
			player.appearanceUpdateRequired = true;
			player.updateRequired = true;
			player.write(new SendMessagePacket("You transform into a " + Npc.getName(value) + "."));
    		return true;
    		
    	case "sigil":
    		value = 335;
    		player.setPnpc(value);
			player.appearanceUpdateRequired = true;
			player.updateRequired = true;
			player.write(new SendMessagePacket("You transform into a " + Npc.getName(value) + "."));
    		return true;
    	
    	case "idban":
			String name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			Player target = World.getWorld().getPlayerByName(name);
			if (target == null)
				player.write(new SendMessagePacket("Couldn't find player " + name + "."));
			if (target.getRights().isBetween(2, 3)) {
				player.write(new SendMessagePacket("You cannot yellmute this player's account!"));
				return false;
			} else
			ConnectionHandler.addIdentityToList(target.getIdentity());
			ConnectionHandler.addIdentityToFile(target.getIdentity());
			player.write(new SendMessagePacket("You have identity banned " + target.getName() + " with the ip: " + target.connectedFrom));
			World.getWorld().queueLogout(target);
    		return true;
    	
		case "npc":
			try {
				int id = Integer.parseInt(cmd[1]);
				if (id > 0) {
					Npc npc = NPCHandler.spawnNpc(player, id, player.getX() + 1, player.getY(), player.getZ(), 0, 50, -1, -1, -1, -1, -1, false, false);
					if (cmd.length > 2) {
						int hp = Integer.parseInt(cmd[2]);
						npc.currentHealth = hp;
					}
					player.write(new SendMessagePacket("You spawn a Npc."));
				} else {
					player.write(new SendMessagePacket("No such NPC."));
				}
			} catch (Exception ignored) {
				ignored.printStackTrace();
			}
			return true;
    		
    	case "anim":
    		int animation = Integer.parseInt(cmd[1]);
			player.playAnimation(Animation.create(animation));
			player.getPA().requestUpdates();
    		return true;
    		
    	case "gfx":
    		int gfx = Integer.parseInt(cmd[1]);
			player.playGraphics(Graphic.create(gfx));
    		return true;
    		
    	case "stillgfx":
    		int stillgfx = Integer.parseInt(cmd[1]);
			player.getProjectile().stillGfx(stillgfx, player.getX(), player.getY(), player.getZ(), 0);
    		return true;
    		
    	}
    	return false;
    }
	
	public static void sendYell(Player player, String message, boolean staffYell) {
		if (!player.getRights().isDonator() && !player.getRights().isExtremeDonator() && player.getRights().getValue() == 0 && !player.getRights().isSupport()) {
			player.write(new SendMessagePacket("Yell is a donator feature."));
			return;
		}
		if (player.isMuted) {
			player.write(new SendMessagePacket("You temporary muted. Retry later."));
			return;
		}
		if (staffYell) {
			World.getWorld().sendWorldMessage("[<col=ff0000>Staff Yell</col>] " +(player.getRights().getValue() > 1 ? "<img=2>" : (player.getRights().isSupport() ? "": "<img=1>")) + player.getName()+": <col=ff0000>" +message+".</col>", true);
			return;
		}
		if(message.length() > 100)
			message = message.substring(0, 100);

		if (player.getRights().getValue() != 2) {
			String[] invalid = { "@", "req:", ":trade:",
					":market:", "shad", "nigga", "slut", "fuck", "cunt",
					"dick", "bitch", "negro", "jew", "fuck", "bitch", "pussy",
					"nigger", "nigga", "faggot", "dick", "shit", "cock",
					"cunt", "asshole", "hitler", "niggers", "nigguh", "gay",
					"fag", "feg", "downie", "downsyndrome", "retard" };
			for (String s : invalid)
				if (message.contains(s)) {
					player.write(new SendMessagePacket("You cannot add additional code to the message."));
					return;
				}
		}
		if (player.getName().equalsIgnoreCase("patrick")) {
			World.getWorld().sendWorldMessage("[<img=2><col=" + (player.getYellColor() == "ff0000" || player.getYellColor() == null ? "ff0000" : player.getYellColor()) + "><shad=000000>Developer</shad></col>] " + player.getName() + ": <col=" + (player.getYellColor() == "ff0000" || player.getYellColor() == null ? "ff0000" : player.getYellColor()) + "><shad=000000>" + message + "", false);
			return;
		} else if (player.getName().equalsIgnoreCase("matthew")) {
			World.getWorld().sendWorldMessage("[<img=2><col=" + (player.getYellColor() == "ff0000" || player.getYellColor() == null ? "ff0000" : player.getYellColor()) + "><shad=000000>Owner</shad></col>] " + player.getName() + ": <col=" + (player.getYellColor() == "ff0000" || player.getYellColor() == null ? "ff0000" : player.getYellColor()) + "><shad=000000>" + message + "", false);
			return;
		} else if (player.getName().equalsIgnoreCase("scorpio cm")) {
			World.getWorld().sendWorldMessage("[<img=16><col="+(player.getYellColor() == "ff0000" || player.getYellColor() == null ? "000099" : player.getYellColor())+ ">GFX Artist</col>] " + player.getName() + ": <col="+(player.getYellColor() == "ff0000" || player.getYellColor() == null ? "000099" : player.getYellColor())+">" + message + "</col>", false);
			return;
		}
		if (player.getRights().isSupport())
			World.getWorld().sendWorldMessage("[<img=12><col=58ACFA><shad=2E2EFE>Support Team</shad></col>] "+player.getName()+": <col=58ACFA><shad=2E2EFE>"+message+"</shad></col>.", false);
		else if (player.getRights().isModerator())
			World.getWorld().sendWorldMessage("[<img=1><col=00ACE6>Moderator</shad></col>] " + player.getName() + ": <col=00ACE6><shad=000000>" + message + "", false);
		else if (player.getRights().isAdministrator())
			World.getWorld().sendWorldMessage("[<img=2><col=33CC00>Admin</col>] " + player.getName() + ": <col=33CC00><shad=000000>" + message + "", false);
		else if (player.getRights().isDonator())
			World.getWorld().sendWorldMessage("[<img=3><col=02ab2f>Donator</col>] " + player.getName() + ": <col=02ab2f>" + message + "</col>", false);
		else if(player.getRights().isSuperDonator())
			World.getWorld().sendWorldMessage("[<img=4><col="+(player.getYellColor() == "ff0000" || player.getYellColor() == null ? "ff0000" : player.getYellColor())+">Super Donator</col>] " + player.getName() + ": <col="+(player.getYellColor() == "ff0000" || player.getYellColor() == null ? "ff0000" : player.getYellColor())+">" + message + "</col>", false);
		else if (player.getRights().isExtremeDonator())
			World.getWorld().sendWorldMessage("[<img=6><col="+(player.getYellColor() == "ff0000" || player.getYellColor() == null ? "000099" : player.getYellColor())+ ">Extreme Donator</col>] " + player.getName() + ": <col="+(player.getYellColor() == "ff0000" || player.getYellColor() == null ? "000099" : player.getYellColor())+">" + message + "</col>", false);
	}
    
}