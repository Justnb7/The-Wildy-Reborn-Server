package com.venenatis.game.net.packet.in.commands.impl;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.content.activity.minigames.Minigame;
import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.content.skills.slayer.SlayerTaskManagement;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.definitions.NPCDefinitions;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.NPCHandler;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.entity.player.clan.ClanManager;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.net.packet.ActionSender.MinimapState;
import com.venenatis.game.net.packet.in.commands.Command;
import com.venenatis.game.net.packet.in.commands.CommandParser;
import com.venenatis.game.util.parser.impl.EquipmentDefinitionParser;
import com.venenatis.game.util.parser.impl.ItemDefinitionParser;
import com.venenatis.game.util.parser.impl.NPCDefinitionParser;
import com.venenatis.game.util.parser.impl.ShopParser;
import com.venenatis.game.util.parser.impl.WeaponDefinitionParser;
import com.venenatis.game.world.World;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.ground_item.GroundItemHandler;
import com.venenatis.game.world.pathfinder.clipmap.Region;
import com.venenatis.game.world.shop.ShopManager;
import com.venenatis.server.Server;

/**
 * A list of commands only accessible to the owner.
 * 
 * @author Michael | Chex
 */
public class OwnerCommand implements Command {

	@Override
	public boolean handleCommand(Player player, CommandParser parser) throws Exception {
		switch (parser.getCommand()) {
		
		case "setinfection":
			int infection = parser.nextInt();
			if (infection == 0)
				player.debug("None");
			else
			player.debug(infection == 1 ? "Poison" : "Venom");
			player.setInfection(infection);
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		return true;
		
		case "lolxd":
			for (int i = 0; i < 20; i++) {
				SlayerTaskManagement.vannakaTask(player);
			}
			return true;
			
		case "lolxd1":
			SlayerTaskManagement.vannakaTask(player);
			return true;
		
		case "stamina":
			int t = parser.nextInt();
			if (t == 1) {
				player.setStamina(1);
			} else if (t == 0) {
				player.setStamina(0);
			}
			player.debug("setting stamina config. Type: "+t);
			return true;
		
		case "opensi":
    		player.getSlayerInterface().open(player);
    		break;
		
		/* Mass Banner */
		case "massbanner":
			String message = "";
			if (parser.hasNext()) {
				message = parser.nextString();
			}
			if (message.length() != 0) {
				for (Player players : World.getWorld().getPlayers()) {
					if (players != null) {
						players.getActionSender().sendBanner("Venenatis Notification", message, 0xFA960A);
					}
				}
			}
			return true;
		
		case "sg":
			player.getActionSender().stillGfx(369, player.getX() -1, player.getY(), player.getZ(), 0);
			return true;
			
		case "sp":
			player.getActionSender().sendProjectile(player.getCentreLocation(), player.getLocation().transform(3, 3), 551, 45, 50, 70, 43, 35, 0, 10, 48);
			player.getActionSender().stillGfx(157, player.getX() -3, player.getY() -3, player.getZ(), 0);
			return true;
		
		case "sgfx":
			final int still_gfx = parser.nextInt();
			player.getActionSender().stillGfx(still_gfx, player.getX() -1, player.getY(), player.getZ(), 0);
			return true;
		
		case "removep":
			player.getSkills().setLevel(Skills.PRAYER, 1);
			return true;
		
		case "pos":
		case "mypos":
		case "coords":
			player.message(player.getLocation().toString());
			return true;
		
		case "unlock":
			int type = parser.nextInt();
			if (type == 0) {
				player.setPreserveUnlocked(true);
			} else if (type == 1) {
				player.setRigourUnlocked(true);
			} else if (type == 2) {
				player.setAuguryUnlocked(true);
			}
			player.getActionSender().sendConfig(709, player.isPreserveUnlocked() ? 1 : 0);
			player.getActionSender().sendConfig(711, player.isRigourUnlocked() ? 1 : 0);
			player.getActionSender().sendConfig(713, player.isAuguryUnlocked() ? 1 : 0);
			return true;
		
		case "showclipmap":
			for (int x = player.getX() - 4; x < player.getX()+4; x++)
				for (int y = player.getY() - 4; y < player.getY()+4; y++)
					if (Region.getClippingMask(x, y, player.getZ()) != 0)
						player.getActionSender().sendGroundItem(new GroundItem(new Item(229, 1), Location.create(x, y, player.getZ()), player));
			return true;
		
		case "bank":
    		player.getBank().open();
    		return true;
		
		case "drop":
			GroundItem groundItem = new GroundItem(new Item(4151), player.getLocation(), player);
			if (!GroundItemHandler.register(groundItem)) {
				return false;
			}
			player.getActionSender().sendGroundItem(groundItem);
			return true;
		
		case "spec":
		case "special":
			player.setSpecialAmount(100);
    		player.getWeaponInterface().sendSpecialBar(player.getEquipment().get(EquipmentConstants.WEAPON_SLOT));
    		player.getWeaponInterface().refreshSpecialAttack();
			return true;
		
		case "npc":
			final int npc = parser.nextInt();
			Location spawnLocation = new Location(player.getX(), player.getY() - 1, player.getZ());

			if (npc > 0) {
				NPC spawn = new NPC(npc, spawnLocation, 0);
				spawn.setLocation(spawnLocation);
				World.getWorld().register(spawn);
				if (parser.hasNext()) {
					int hp = parser.nextInt();
					spawn.setHitpoints(hp);
				}
			}
			return true;
			
		case "setstat":
    		try {
    			final int stat = parser.nextInt();
    			final int level = parser.nextInt();
				player.getSkills().setExperience(stat, player.getSkills().getXPForLevel(level) + 1);
				player.getSkills().setLevel(stat, level);
				player.getActionSender().sendMessage(Skills.SKILL_NAME[stat] + " level is now " +level+ ".");	
    		} catch(Exception e) {
				e.printStackTrace();
				player.getActionSender().sendMessage("Syntax is ::lvl [skill] [lvl].");				

			}
    		player.setCombatLevel(player.getSkills().getCombatLevel());
    		break;
			
		case "item":
			if (parser.hasNext()) {
				final int item = parser.nextInt();
				int amount = 1;

				if (parser.hasNext()) {
					amount = Integer.parseInt(parser.nextString().toLowerCase().replaceAll("k", "000").replaceAll("m", "000000").replaceAll("b", "000000000"));
				}

				player.getInventory().add(item, amount);
				return true;
			}
			return true;
		
		case "master":
			for(int skill = 0; skill < Skills.SKILL_COUNT; skill++) {
				player.getSkills().setExperience(skill, player.getSkills().getXPForLevel(99) + 1);
				player.getSkills().setLevel(skill, 99);
			}
			return true;
			
			case "infhp":
				boolean v = player.hasAttribute("infhp");
				player.setAttribute("infhp", !v);
				player.message("now: "+!v);
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

		case "tstate":
			player.getActionSender().sendMessage(player.getTradeSession().toString());
			return true;

		case "mapstate":
			if (parser.hasNext()) {
				int state = parser.nextInt();

				switch (state) {
				case 0:
					player.getActionSender().sendMinimapState(MinimapState.NORMAL);
					break;

				case 1:
					player.getActionSender().sendMinimapState(MinimapState.UNCLICKABLE);
					break;

				case 2:
					player.getActionSender().sendMinimapState(MinimapState.HIDDEN);
					break;
				}

				return true;
			}
			return false;

		case "duel":
			player.setTeleportTarget(new Location(3363, 3275));
			return true;

		case "69":
			player.getActionSender().sendString("Welcome to", 15257);
			player.getActionSender().sendString("Your ip: " + 1, 15258);
			player.getActionSender().sendString("More stuff here", 15259);
			player.getActionSender().sendString("Please register at our forum!", 15260);
			player.getActionSender().sendString("Loads of information and MORE!", 15261);
			player.getActionSender().sendString("Make easy money from thieving stalls", 15262);
			player.getActionSender().sendString("CLICK HERE TO PLAY", 15263);
			player.getActionSender().sendString("Don't forget to secure your bank, set a bank pin!", 15270);
			player.getActionSender().sendInterface(15244);
			player.getActionSender().sendInterfaceWithInventoryOverlay(15244, 15767);
			return true;

		case "h":
			if (parser.hasNext()) {
				int h = parser.nextInt();
				player.setLocation(new Location(player.getLocation().getX(), player.getLocation().getY(), h));
				player.setTeleporting(true);
				return true;
			}
			return false;

		case "debug":
			if (parser.hasNext()) {
				switch (parser.nextString()) {
				case "server":
					Server.SERVER_DEBUG = !Server.SERVER_DEBUG;
					return true;
				}
			}
			player.setDebugMode(player.inDebugMode() ? false : true);
			player.getActionSender().sendMessage(String.format("[debug= %s]", player.inDebugMode() ? "enabled" : "disabled"));
			return true;

		case "hide":
			player.setVisible(false);
			for (Player other : player.getLocalPlayers()) {
				if (other == null) {
					continue;
				}

				if (other.getLocalPlayers().contains(player)) {
					other.getLocalPlayers().remove(player);
				}
			}
			return true;

		case "show":
			player.setVisible(true);
			return true;

		case "dumpbank": {
			int[] amounts = player.getBank().getTabAmounts();
			Item[] items = player.getBank().toTrimmedArray();

			String data = " ";

			for (int am : amounts) {
				data += am + ", ";
			}

			System.out.println("\tprivate int[] tabAmounts = {" + data + "};");
			System.out.println("");

			data = "\n\t\t";
			int c = 0;
			for (Item it : items) {
				data += "new Item(" + it.getId() + ", " + it.getAmount() + "), ";
				if (++c % 5 == 0) {
					data += "\n\t\t";
				}
			}

			data += "\n\t";

			System.out.println("\tprivate Item[] bankItems = {" + data + "};");
		}
			return true;

		case "minigame":
			String username = parser.toString().toLowerCase().replaceAll("minigame ", "");

			if (World.getWorld().getPlayerByName(username).isPresent()) {
				Player user = World.getWorld().getPlayerByName(username).get();

				if (MinigameHandler.search(user).isPresent()) {
					Minigame m = MinigameHandler.search(user).get();

					player.getActionSender().sendMessage(m.toString());
				} else {
					player.getActionSender().sendMessage("This player is currently not in a minigame.");
				}
			}
			return true;

		case "conf":
			if (parser.hasNext(2)) {
				int id = parser.nextInt();
				int state = parser.nextInt();
				player.getActionSender().sendConfig(id, state);
				player.debug("config: "+id+ " state: "+state);
				return true;
			}
			return false;
			
		case "ts":
			if(parser.hasNext()) {
				String text = parser.nextString();
				int interfaceId = parser.nextInt();
				player.getActionSender().sendString(text, interfaceId);
			}
			return true;

		case "song":
			if (parser.hasNext()) {
				int id = parser.nextInt();
				player.getActionSender().sendSong(id);
			}
			return true;

		case "winterface":
			if (parser.hasNext()) {
				int id = parser.nextInt();
				player.getActionSender().sendWalkableInterface(id);
			}
			return true;

		case "sendtoggle":
			if (parser.hasNext((2))) {
				int config = parser.nextInt();

				int value = parser.nextInt();

				player.getActionSender().sendToggle(config, value);
			}
			return true;

		case "update":
			if (parser.hasNext()) {
				final int seconds = parser.nextInt();
				World.updateSeconds = seconds;
			}
			World.updateAnnounced = false;
			World.updateRunning = true;
			World.updateStartTime = System.currentTimeMillis();
			return true;

		case "pnpc":
			if (parser.hasNext()) {
				final int npcId = parser.nextInt();

				if (npcId >= 0) {
					final NPCDefinitions def = NPCDefinitions.get(npcId);

					if (def == null) {
						player.getActionSender().sendMessage("This mob does not exist!");
						return true;
					}

					player.setPnpc(npcId);
					player.setPlayerTransformed(true);
					player.getActionSender().sendMessage(String.format("You have turned into %s (ID: %s).", def.getName(), npcId));
					player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				} else {
					player.setPnpc(-1);
					player.getActionSender().sendMessage("You have reset your appearance.");
					player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				}
			}
			return true;

		case "shop":
			if (parser.hasNext()) {
				final int id = parser.nextInt();
				ShopManager.open(player, id);
				player.getActionSender().sendMessage("Opened shop: " + id);
			}
			return true;

		case "sound":
			if (parser.hasNext()) {
				player.getActionSender().sendSound(parser.nextInt(), 0, 0);
				return true;
			}
			player.setEnableSound(player.isEnableSound() ? false : true);
			player.getActionSender().sendMessage(String.format("You have %s sound effects.", player.isEnableSound() ? "enabled" : "disabled"));
			return true;

		case "int":
		case "interface":
			if (parser.hasNext()) {
				final int id = parser.nextInt();
				player.getActionSender().sendInterface(id);
				player.getActionSender().sendMessage("Opening interface: " + id);
			}
			return true;

		case "anim":
		case "animation":
			if (parser.hasNext()) {
				final int animation = parser.nextInt();
				player.playAnimation(Animation.create(animation));
				player.getActionSender().sendMessage("Starting animation: " + animation);
			}
			return true;

		case "gfx":
			if (parser.hasNext()) {
				final int graphic = parser.nextInt();
				player.playGraphics(Graphic.create(graphic));
				player.getActionSender().sendMessage("Starting gfx: " + graphic);
			}
			return true;

		case "dumpinv":
			for (final Item item : player.getInventory().toNonNullArray()) {
				if (item.getAmount() > 1) {
					System.out.printf("new Item(%s, %s), ", item.getId(), item.getAmount());
				} else {
					System.out.printf("new Item(%s), ", item.getId());
				}
			}
			System.out.println();
			return true;

		case "dumpinv1":
			int i = 0;
			for (final Item item : player.getInventory().toNonNullArray()) {
				if (i++ % 4 == 0) {
					System.out.println();
				}
				System.out.printf("%s, ", item.getId());
			}
			System.out.println();
			return true;

		case "dumpinv2":
			for (final Item item : player.getInventory().toNonNullArray()) {
				System.out.println("	new WeightedChance < Item > (WeightedChance.COMMON, new Item(" + item.getId() + ", " + item.getAmount() + ")), //" + item.getName());
			}
			System.out.println();
			return true;

		case "dumpinv3":
			int amt = 0;
			for (final Item item : player.getInventory().toNonNullArray()) {
				System.out.print("\"" + item.getName().toLowerCase() + "\", ");
				amt++;
				if (amt == 7) {
					System.out.println();
					amt = 0;
				}
			}
			return true;

		case "dumpinv4":
			for (final Item item : player.getInventory().toNonNullArray()) {
				System.out.println("         {");
				System.out.println("            \"id\": " + item.getId() + ",");
				System.out.println("            \"amount\":1000");
				System.out.println("         },");
			}
			System.out.println();
			return true;

		case "dumpequip":
			for (final Item item : player.getEquipment().toNonNullArray()) {
				if (item.getAmount() > 1) {
					System.out.printf("new Item(%s, %s), ", item.getId(), item.getAmount());
				} else {
					System.out.printf("new Item(%s), ", item.getId());
				}
			}
			System.out.println();
			return true;

		case "move":
		case "tele":
			if (parser.hasNext(2)) {
				final int x = parser.nextInt();
				final int y = parser.nextInt();
				int z = 0;

				if (parser.hasNext()) {
					z = parser.nextInt();
				}

				Location location = new Location(x, y, z);
				player.setTeleportTarget(location);
				player.getActionSender().sendMessage("You moved to: " + player.getLocation() + ".");
				return true;
			}
			return false;
			
		case "object":
			if (parser.hasNext()) {
				final int object = parser.nextInt();
				player.getActionSender().sendObject(object, player.getX(), player.getY(), player.getZ(), 0, 10);
				player.getActionSender().sendMessage("Spawned object: " + object);
			}
			return true;

		case "reload":
			if (parser.hasNext()) {
				final String input = parser.nextString();

				switch (input) {
				
				case "spawns":
					for (NPC n : World.getWorld().getNPCs()) {
						if (n != null) {
							World.getWorld().unregister(n);
						}
					}
					NPCHandler.loadAutoSpawn("./data/text_files/npc_spawns.txt");
					player.getActionSender().sendMessage("Succesfully reloaded the spawns");
					return true;
					
				case "npcs":
					player.getActionSender().sendMessage("Succesfully reloaded npcdefinitions");
					new NPCDefinitionParser().run();
					return true;

				case "item":
				case "items":
					new ItemDefinitionParser().run();
					player.getActionSender().sendMessage("Successfully reloaded item definitions.");
					return true;

				case "equipment":
					new EquipmentDefinitionParser().run();
					player.getActionSender().sendMessage("Successfully reloaded equipment definitions.");
					return true;

				case "weapon":
				case "weapons":
					new WeaponDefinitionParser().run();
					player.getActionSender().sendMessage("Successfully reloaded weapon definitions.");
					return true;

				case "shop":
				case "shops":
				case "store":
					new ShopParser().run();
					player.getActionSender().sendMessage("Successfully reloaded shops.");
					return true;

				default:
					player.getActionSender().sendMessage("'::reload " + input + "' does not exist.");
					return true;
				}
			}
			return false;
		}

		return false;

	}

	@Override
	public boolean meetsRequirements(Player player) {
		return player.getRights().isOwner(player);
	}
}