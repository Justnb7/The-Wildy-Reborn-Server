package com.model.game.character.player;
import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.npc.NPC;
import com.model.game.character.npc.pet.Pet;
import com.model.game.character.player.content.BrimhavenVines;
import com.model.game.character.player.content.WildernessDitch;
import com.model.game.character.player.content.rewards.CrystalChest;
import com.model.game.character.player.content.rewards.ShinyChest;
import com.model.game.character.player.content.teleport.Obelisks;
import com.model.game.character.player.content.teleport.Teleport;
import com.model.game.character.player.content.teleport.Teleport.TeleportType;
import com.model.game.character.player.content.teleport.TeleportExecutor;
import com.model.game.character.player.minigames.pest_control.PestControl;
import com.model.game.character.player.packets.out.SendInterfacePacket;
import com.model.game.character.player.packets.out.SendSidebarInterfacePacket;
import com.model.game.character.player.skill.agility.Shortcut;
import com.model.game.character.player.skill.fishing.Fishing;
import com.model.game.character.player.skill.fishing.FishingSpot;
import com.model.game.character.player.skill.runecrafting.Runecrafting;
import com.model.game.character.player.skill.thieving.Pickpocket;
import com.model.game.character.player.skill.thieving.Stalls;
import com.model.game.character.player.skill.woodcutting.Tree;
import com.model.game.character.player.skill.woodcutting.Woodcutting;
import com.model.game.location.Position;
import com.model.game.shop.Shop;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;
import com.model.utility.cache.ObjectDefinition;

public class ActionHandler {

	private Player player;

	public ActionHandler(Player player) {
		this.player = player;
	}

	public void firstClickObject(int id, int x, int y) {

		ObjectDefinition def = ObjectDefinition.getObjectDef(id);
		final Position loc = Position.create(x, y, player.heightLevel);
		if (player.inDebugMode()) {
			player.getActionSender().sendMessage("[Debug] First click object - ObjectId: [@red@" + id + "@bla@] objectX:[@red@" + x + "@bla@]@bla@] objectY:[@red@" + y + "@bla@]");
		}

		player.clickObjectType = 0;
		player.face(player, new Position(x, y));

		player.setFollowing(null);

		player.getMining().mine(id, new Position(x, y, player.heightLevel));
		
		Tree tree = Tree.forObject(id);
		if (tree != null) {
			Woodcutting.getInstance().chop(player, id, x, y);
			return;
		}
		
		Obelisks.get().activate(player, id);
		
		if (Runecrafting.handleObject(player, id)) {
			return;
		}
		
		if (id >= 21731 && id <= 21737 || id == 12987 || id == 12986) {
			BrimhavenVines.handleBrimhavenVines(player, id);
			return;
		}
		
		if (def.name == null || def.name.length() == 0) {
			return;
		}
		if (def.getName().toLowerCase().contains("altar") && def.actions[0].toLowerCase().contains("pray")) {
			player.getSkills().getPrayer().prayAltar(loc);
			return;
		}
		switch (def.name.toLowerCase()) {
		
		case "open chest":
			if (player.getItems().playerHasItem(85)) {
				ShinyChest.searchChest(player, x, y);
				return;
			} else if(player.getItems().playerHasItem(989)) {
				CrystalChest.searchChest(player, x, y);
				return;
			} else {
				player.getActionSender().sendMessage("You need a key to open this chest.");
			}
			break;
		
		case "magic chest":

			break;
		
		case "ladder":
			//KBD ladder
			if (player.getArea().inWild() && player.getX() == 3069 && player.getY() == 10255) {
				player.playAnimation(Animation.create(828));
				player.getMovementHandler().setForcedMovement(true);
				Server.getTaskScheduler().schedule(new ScheduledTask(2) {
					@Override
					public void execute() {
						player.getMovementHandler().setForcedMovement(false);
						player.getPA().movePlayer(new Position(3017, 3850, 0));
						this.stop();
					}
				});
			}
			if (player.getArea().inWild() && player.getX() == 3017 && player.getY() == 3850) {
				player.playAnimation(Animation.create(828));
				player.getMovementHandler().setForcedMovement(true);
				Server.getTaskScheduler().schedule(new ScheduledTask(2) {
					@Override
					public void execute() {
						player.getMovementHandler().setForcedMovement(false);
						player.getPA().movePlayer(new Position(3069, 10255, 0));
						this.stop();
					}
				});
			}
			break;
		
		case "bank":
		case "bank booth":
			player.getPA().openBank();
			break;
			
		case "crevice":
			if (player.getY() == 9797) {
				player.getKraken().start(player);
			} else if (player.getY() == 5798) {
				player.getPA().movePlayer(new Position(2486, 9797, 0));
			} else if (player.getX() == 2444) {
				player.getPA().movePlayer(new Position(2430, 3424, 0));
			}
			break;

		case "cave":
			if (player.getX() == 2430) {
				player.getPA().movePlayer(new Position(2444, 9825, 0));
			}
			break;
			
		case "passage":
			if (player.getX() == 2970) {
				player.getPA().movePlayer(new Position(2974, 4384, 2));
			} else if (player.getX() == 2974) {
				player.getPA().movePlayer(new Position(2970, 4384, 2));
			}
			break;
			
		case "lever":
			if (player.getX() == 3153)
			TeleportExecutor.executeLeverTeleport(player, new Teleport(new Position(3090, 3475, player.getZ()), TeleportType.LEVER));
			break;
		}

		switch (id) {
		
		/**
		 * Entering the Fight Caves.
		 */
		case 11833:
			if (Boundary.entitiesInArea(Boundary.FIGHT_CAVE) >= 50) {
				player.getActionSender().sendMessage("There are too many people using the fight caves at the moment. Please try again later");
				return;
			}
			player.getFightCave().enterFightCaves();
			break;

		case 11834:
			if (Boundary.isIn(player, Boundary.FIGHT_CAVE)) {
				player.getFightCave().exitCave(1);
				return;
			}
			break;
		
		/**
		 * Pest control
		 */
		case 14315:
			PestControl.addToLobby(player);
			break;

		case 14314:
			PestControl.removeFromLobby(player);
			break;
		
		/**
		 * Warriors guild
		 */
		case 24306:
		case 24309:
			if (player.getHeight() == 2) {
				player.getWarriorsGuild().handleDoor();
				return;
			}
			if (player.heightLevel == 0) {
				if (player.absX == 2855 || player.absX == 2854) {
					if (player.absY == 3546)
						player.getPA().movePlayer(player.absX, player.absY - 1, 0);
					else if (player.absY == 3545)
						player.getPA().movePlayer(player.absX, player.absY + 1, 0);
					player.face(player, new Position(x, y));
				}
			}
			break;
		
		case 26502:
			if(player.getY() == 5294) {
				player.getPA().movePlayer(new Position(2839, 5295, 2));
			} else if(player.getY() == 5295 || player.getY() == 5296) {
				player.getPA().movePlayer(new Position(2839, 5294, 2));
			}
			break;
		
		case 26503:
			if(player.getX() == 2862) {
				player.getPA().movePlayer(new Position(2863, 5354, 2));
			} else if(player.getX() == 2863) {
				player.getPA().movePlayer(new Position(2862, 5354, 2));
			}
			break;
			
		case 26504:
			if(player.getX() == 2909) {
				player.getPA().movePlayer(new Position(2908, 5265, 0));
			} else if(player.getX() == 2908) {
				player.getPA().movePlayer(new Position(2909, 5265, 0));
			}
			break;
			
		case 26505:
			if(player.getY() == 5333) {
				player.getPA().movePlayer(new Position(2925, 5332, 2));
			} else if(player.getY() == 5332) {
				player.getPA().movePlayer(new Position(2925, 5333, 2));
			}
			break;
		
		case 13641:
			if(player.getArea().inWild()) {
				return;
			}
			if (player.onAuto) {
				player.getActionSender().sendMessage("You can't switch spellbooks with Autocast enabled.");
				return;
			}
			switch (player.getSpellBook()) {
			case MODERN:
				player.setSpellBook(SpellBook.ANCIENT);
				player.write(new SendSidebarInterfacePacket(6, 12855));
				player.getActionSender().sendMessage("An ancient wisdom fills your mind.");
				break;
			case ANCIENT:
				player.setSpellBook(SpellBook.LUNAR);
				player.write(new SendSidebarInterfacePacket(6, 29999));
				player.getActionSender().sendMessage("The power of the moon overpowers you.");
				break;
			case LUNAR:
				player.setSpellBook(SpellBook.MODERN);
				player.write(new SendSidebarInterfacePacket(6, 1151));
				player.getActionSender().sendMessage("You feel a drain on your memory.");
				break;
			}
			player.autocastId = -1;
			player.getPA().resetAutoCast();
			player.onAuto = false;
			break;
		
		case 1728:
			if(player.getX() == 3007) {
				player.getPA().walkTo(+1, 0);
			} else if (player.getX() == 3008 && player.getY() == 3850) {
				player.getPA().walkTo(-1, 0);
			}
			break;
			
		case 1727:
			if(player.getX() == 3007 && player.getY() == 3849) {
				player.getPA().walkTo(+1, 0);
			} else if (player.getX() == 3008) {
				player.getPA().walkTo(-1, 0);
			}
			break;
		
		case 15653:
		case 16671:
			if (player.absY == 3546) {
				if (player.absX == 2877)
					player.getPA().movePlayer(player.absX - 1, player.absY, 0);
				else if (player.absX == 2876)
					player.getPA().movePlayer(player.absX + 1, player.absY, 0);
				player.face(player, new Position(x, y));
			}
			break;

		case 24303:
			player.getPA().movePlayer(2840, 3539, 0);
			break;
		
		case 23271: 
			if (!player.ditchDelay.elapsed(1000)) {
				return;
			}
			player.face(player, new Position(x, y));
			player.ditchDelay.reset();
			if (player.getY() >= 3523) {
				WildernessDitch.leave(player);
			} else
				WildernessDitch.enter(player);
			break;
		
		case 27770:
		case 27771:
		case 27719:
		case 27718:
			player.getPA().openBank();
			break;
			
		case 2182:
			CrystalChest.searchChest(player, x, y);
			break;
		
		/**
		 * Shortcuts
		 */
		case 9328:
		case 16509:
		case 11844:
		case 9301:
		case 9302:
		case 2322:
		case 2323:
		case 2296:
		case 5100:
		case 21738:
		case 21739:
		case 14922:
		case 3067:
		case 9309:
		case 9310:
		case 2618:
		case 2332:
		case 20882:
		case 20884:
		case 4615:
		case 4616:
		case 3933:
		case 12127:
		case 16510:
		case 16544:
		case 16539:
		case 993:
		case 51:
		case 8739:
			Shortcut.processAgilityShortcut(player);
			break;

		/**
		 * Lever objects
		 */

		case 5960:
			TeleportExecutor.executeLeverTeleport(player, new Teleport(
					new Position(3090, 3956, player.getZ()), TeleportType.LEVER));
			break;

		case 5959:
			TeleportExecutor.executeLeverTeleport(player, new Teleport(
					new Position(2539, 4712, player.getZ()), TeleportType.LEVER));
			break;

		case 1814:
			TeleportExecutor.executeLeverTeleport(player, new Teleport(
					new Position(3158, 3953, player.getZ()), TeleportType.LEVER));
			break;

		case 4950:
			TeleportExecutor.executeLeverTeleport(player, new Teleport(
					new Position(3087, 3500, player.getZ()), TeleportType.LEVER));
			break;

		case 1816:
			TeleportExecutor.executeLeverTeleport(player, new Teleport(
					new Position(2271, 4680, player.getZ()), TeleportType.LEVER));
			break;

		case 1817:
			TeleportExecutor.executeLeverTeleport(player, new Teleport(
					new Position(3067, 10253, player.getZ()), TeleportType.LEVER));
			break;
			
		case 26761:
			TeleportExecutor.executeLeverTeleport(player, new Teleport(
					new Position(3153, 3923, player.getZ()), TeleportType.LEVER));
			break;
			
		case 1815:
			TeleportExecutor.executeLeverTeleport(player, new Teleport(
					new Position(3090, 3475, player.getZ()), TeleportType.LEVER));
			break;

		/**
		 * Dagannoth cave
		 */

		case 8959:
			if (player.absX == 2490) {
				player.getPA().movePlayer1(2491, 10147);
			} else if (player.absX == 2491) {
				player.getPA().movePlayer1(2490, 10147);
			}
			break;

		case 10177:
			player.getPA().movePlayer(1863, 4373, 2);
			break;

		case 10212:
			player.getPA().movePlayer(2545, 10143, 0);
			break;

		case 10213:
			player.getPA().movePlayer(1827, 4362, 1);
			break;

		case 10211:
			player.getPA().movePlayer(1864, 4389, 1);
			break;

		case 10210:
			player.getPA().movePlayer(1864, 4387, 2);
			break;

		case 10214:
			player.getPA().movePlayer(1863, 4370, 1);
			break;

		case 10215:
			player.getPA().movePlayer(1890, 4409, 0);
			break;

		case 10216:
			player.getPA().movePlayer(1890, 4409, 1);
			break;

		case 10230:
			player.getPA().movePlayer(2900, 4449, 0);
			break;

		case 10229:
			player.getPA().movePlayer(1912, 4367, 0);
			break;

		case 4383:
			player.getPA().movePlayer(2442, 10147, 0);
			break;

		/**
		 * Scorpia pit
		 */

		case 26762:
			player.getPA().movePlayer(3243, 10351, 0);
			break;

		case 26763:
			player.getPA().movePlayer(3232, 3950, 0);
			break;

		/**
		 * Webs
		 */
		case 733:
			slashWeb(player, x, y);
			break;

		/**
		 * Brimhaven Dungeon
		 */

		case 21726:
			player.getPA().movePlayer(2637, 9517, 0);
			break;

		case 21725:
			player.getPA().movePlayer(2636, 9510, 2);
			break;

		/**
		 * Switch prayer books
		 */
		case 6552:
			if (player.onAuto) {
				player.getActionSender().sendMessage("You can't switch spellbooks with Autocast enabled.");
				return;
			}
			switch (player.getSpellBook()) {
			case MODERN:
				player.setSpellBook(SpellBook.ANCIENT);
				player.write(new SendSidebarInterfacePacket(6, 12855));
				player.getActionSender().sendMessage("An ancient wisdom fills your mind.");
				break;
			case ANCIENT:
				player.setSpellBook(SpellBook.LUNAR);
				player.write(new SendSidebarInterfacePacket(6, 29999));
				player.getActionSender().sendMessage("The power of the moon overpowers you.");
				break;
			case LUNAR:
				player.setSpellBook(SpellBook.MODERN);
				player.write(new SendSidebarInterfacePacket(6, 1151));
				player.getActionSender().sendMessage("You feel a drain on your memory.");
				break;
			}
			player.autocastId = -1;
			player.getPA().resetAutoCast();
			player.onAuto = false;
			break;

		/**
		 * Slayer tower
		 */
		case 16537:
			if (player.heightLevel == 0) {
				player.getPA()
						.movePlayer(player.getX(), player.getY(), 1);
			} else if (player.heightLevel == 1) {
				player.getPA()
						.movePlayer(player.getX(), player.getY(), 2);
			}
			break;

		case 16538:
			if (player.heightLevel == 1) {
				player.getPA()
						.movePlayer(player.getX(), player.getY(), 0);
			} else if (player.heightLevel == 2) {
				player.getPA()
						.movePlayer(player.getX(), player.getY(), 1);
			}
			break;

		case 2120:
		case 4494:
			if (player.heightLevel == 2) {
				player.getPA().movePlayer(player.getX() - 5,
						player.getY(), 1);
			} else if (player.heightLevel == 1) {
				player.getPA().movePlayer(player.getX() + 5,
						player.getY(), 0);
			}
			break;

		case 2114:
			if (player.heightLevel == 0) {
				player.getPA().movePlayer(3433, 3538, 1);
			} else if (player.heightLevel == 1) {
				player.getPA().movePlayer(3433, 3538, 1);
			}
			break;

		case 2119:
			if (player.heightLevel == 1) {
				player.getPA().movePlayer(3417, 3540, 2);
			}
			break;

		/**
		 * Sparkling pool
		 */

		case 2879:
			player.getPA().movePlayer(2538, 4716, 0);
			break;

		case 2878:
			player.getPA().movePlayer(2509, 4689, 0);
			break;

		/**
		 * Lever Mage arena
		 */

		case 9706:
			TeleportExecutor.teleport(player, new Teleport(new Position(3105,
					3951, 0), TeleportType.NORMAL), false);
			break;

		case 9707:
			TeleportExecutor.teleport(player, new Teleport(new Position(3105,
					3956, 0), TeleportType.NORMAL), false);
			break;

		default:
			break;

		}
	}

	public void secondClickObject(int id, int x, int y) {
		if (player.inDebugMode()) {
			player.getActionSender().sendMessage("[Debug] Second click object - ObjectId: [@red@" + id+"@bla@]");
		}
		if (player.isTeleporting()) {
			return;
		}
		
		player.clickObjectType = 0;
		player.face(player, new Position(x, y));

		ObjectDefinition objectDef = ObjectDefinition.getObjectDef(id);
		switch (objectDef.name.toLowerCase()) {

		case "bank":
		case "Bank":
		case "bank booth":
		case "booth":
			player.getPA().openBank();
			break;
			
		}
		
		switch (id) {
		
		case 11730:
			player.getThieving().stealFromStall(Stalls.BAKERS_STALL, id);
			break;
		case 11731:
			player.getThieving().stealFromStall(Stalls.GEM_STALL, id);
			break;
		case 11732:
			player.getThieving().stealFromStall(Stalls.FUR_STALL, id);
			break;
		case 11734:
			player.getThieving().stealFromStall(Stalls.SILVER_STALL, id);
			break;
		case 14011:
			player.getThieving().stealFromStall(Stalls.MARKET_STALL, id);
			break;
		
		case 14827:
		case 14828:
		case 14829:
			Obelisks.chooseTeleport(player, -1);
			break;
			
		case 6943:
		case 27720:
		case 27721:
		case 27719:
		case 27718:
			player.getPA().openBank();
			break;
			
		}
	}

	public void thirdClickObject(int objectType, int obX, int obY) {
		if (player.inDebugMode()) {
			player.getActionSender().sendMessage("[Debug] Third click object - ObjectId: [@red@" + objectType+"@bla@]");
		}
		player.clickObjectType = 0;

		switch (objectType) {
		default:
			break;
		}
	}

	public void firstClickNpc(NPC npc) {
		player.clickNpcType = 0;
		
		if (player.inDebugMode()) {
			player.getActionSender().sendMessage("First click "+npc.npcId);
		}
		
		if (FishingSpot.fishingNPC(npc.npcId)) {
			Fishing.attemptFishing(player, npc, 1);
			return;
		}
		
		switch (npc.npcId) {
		
		/**
		 * Kamfreena	
		 */
		case 2461:
			player.getWarriorsGuild().handleDoor();
			break;
		
		case 3257:
			player.getThieving().pickpocket(Pickpocket.FARMER, npc);
			break;
		
		case 315:
			player.dialogue().start("emblem_trader_dialogue", player);
			break;
		
		case 5567:
			if (!player.deathShopChat) {
				player.dialogue().start("DEATH_SHOP_DIALOGUE", player);
			} else {
				player.dialogue().start("DEATH_SHOP_DIALOGUE2", player);
			}
			break;

		case 2180:
			player.dialogue().start("FIGHT_CAVE");
			break;

		case 6742:
			player.dialogue().start("MAXCAPE", player);
			break;

		case 954:
			player.dialogue().start("BARROWS", player);
			break;

		/**
		 * Shops
		 */

		case 3254:
			Shop.SHOPS.get("Donator Ticket Shop").openShop(player);
			break;

		case 6599:
			player.dialogue().start("MANDRITH", player);
			break;

		case 5362:
			Shop.SHOPS.get("Vote Rewards Shop").openShop(player);
			player.getActionSender().sendMessage("You currently have @blu@" + player.getVotePoints()
					+ "@bla@ vote points, and @blu@" + player.getTotalVotes() + "@bla@ total votes.");
			break;

		case 4058:
			Shop.SHOPS.get("Royalty Shop").openShop(player);
			break;

		case 505:
			Shop.SHOPS.get("Skilling Shop").openShop(player);
			break;

		case 2200:
			Shop.SHOPS.get("Team Cape Shop").openShop(player);
			break;

		case 1304:
			Shop.SHOPS.get("Low Level Shop").openShop(player);
			break;

		case 3193:
			Shop.SHOPS.get("Costume Shop").openShop(player);
			break;

		case 3894:
			player.dialogue().start("SIGMUND_THE_MERCHANT", player);
			break;

		case 3951:
			Shop.SHOPS.get("Gear Point Store").openShop(player);
			player.getActionSender().sendMessage("@red@Gear points@bla@ refill to @blu@2500@bla@ every 5 minutes.");
			player.getActionSender().sendMessage(
					"@blu@Did you know, you can type ::food, ::veng, ::barrage, and ::pots, to spawn them?");
			break;

		case 508:
		case 506:
			Shop.SHOPS.get("General Store").openShop(player);
			break;

		/**
		 * Skillcape shop
		 */
		case 4306:
			Shop.openSkillCape(player);
			break;

		/**
		 * Slayer masters
		 */
		case 401: // Turael
			player.dialogue().start("TURAEL_DIALOGUE", player);
			break;
		case 402: // Mazchna
			player.dialogue().start("MAZCHNA_DIALOGUE", player);
			break;

		case 403: // Vannaka
			player.dialogue().start("VANNAKA_DIALOGUE", player);
			break;

		case 404: // Chaeldar
			player.dialogue().start("CHAELDAR_DIALOGUE", player);
			break;

		case 405: // Duradel
			player.dialogue().start("DURADEL_DIALOGUE", player);
			break;

		case 490: // Nieve
			player.dialogue().start("NIEVE_DIALOGUE", player);
			break;

		/**
		 * Appearance npc
		 */
		case 1306:
			player.write(new SendInterfacePacket(3559));
			break;
		}
	}

	public void secondClickNpc(NPC npc) {
		
		player.clickNpcType = 0;
		
		if (player.inDebugMode()) {
			player.getActionSender().sendMessage("Second click: "+npc.npcId);
		}
		
		if (Pet.pickup(player, npc)) {
			return;
		}
		
		if (FishingSpot.fishingNPC(npc.npcId)) {
			Fishing.attemptFishing(player, npc, 2);
			return;
		}

		switch (npc.npcId) {
		
		case 2180:
			player.secondOption = true;
			player.dialogue().start("FIGHT_CAVE");
			break;
		
		case 3078:
			player.getThieving().pickpocket(Pickpocket.MAN, npc);
			break;
		
		case 315:
			Shop.SHOPS.get("Bounty Hunter Store").openShop(player);
			break;
		
		case 5567:
			if (!player.deathShopChat) {
				player.dialogue().start("DEATH_SHOP_DIALOGUE", player);
			} else {
				player.dialogue().start("DEATH_SHOP_DIALOGUE2", player);
			}
			break;
		
		/**
		 * Dialogues
		 */
			
			/**
			 * Ending dialogues
			 */

			/**
			 * Shops
			 */
			case 7007:
			case 539:
				Shop.SHOPS.get("Vote Rewards Shop.").openShop(player);
				player.getActionSender().sendMessage("You currently have @blu@" + player.getVotePoints() + "@bla@ vote points, and @blu@" + player.getTotalVotes() + "@bla@ total votes.");
				break;

			case 7008:
			case 547:
			case 6599:
				Shop.SHOPS.get("Player Killing Reward Shop.").openShop(player);
				break;

			case 6060:
				Shop.SHOPS.get("Ranged Equipment.").openShop(player);
				break;

			case 1052:
				Shop.SHOPS.get("Betty's Magic Emporium.").openShop(player);
				break;

			case 5251:
				Shop.SHOPS.get("Tutab's Magical Market.").openShop(player);
				break;

			case 1791:
				Shop.SHOPS.get("Food Shop.").openShop(player);
				break;

			case 1174:
				Shop.SHOPS.get("Potions Shop.").openShop(player);
				break;

			case 535:
				Shop.SHOPS.get("Horvik's Armour Shop.").openShop(player);
				break;

			case 1944:
				Shop.SHOPS.get("Weapons And Accessories Galore.").openShop(player);
				break;
				
			case 508:
			case 506:
				Shop.SHOPS.get("General Store").openShop(player);
				break;

		/**
		 * End of shops
		 */

		/**
		 * Slayer dialogues
		 */

		case 401: // Turael
			player.dialogue().start("TURAEL_DIALOGUE", player);
			break;
		case 402: // Mazchna
			player.dialogue().start("MAZCHNA_DIALOGUE", player);
			break;

		case 403: // Vannaka
			player.dialogue().start("VANNAKA_DIALOGUE", player);
			break;

		case 404: // Chaeldar
			player.dialogue().start("CHAELDAR_DIALOGUE", player);
			break;

		case 405: // Duradel
			player.dialogue().start("DURADEL_DIALOGUE", player);
			break;

		case 490: // Nieve
			player.dialogue().start("NIEVE_DIALOGUE", player);
			break;

		/**
		 * End of Slayer dialogues
		 */

		case 394:
			player.getPA().openBank();
			break;

		/**
		 * End of Bankers
		 */

		}
	}

	public void thirdClickNpc(NPC npc) {
		
		player.clickNpcType = 0;
		
		if (player.inDebugMode()) {
			player.getActionSender().sendMessage("Third click: "+npc.npcId);
		}
		
		switch (npc.npcId) {

		/**
		 * Dialogues
		 */

		/**
		 * Ending dialogues
		 */

		/**
		 * Slayer masters
		 */

		case 401: // Turael
			Shop.SHOPS.get("Slayer Equipment").openShop(player);
			break;
		case 402: // Mazchna
			Shop.SHOPS.get("Slayer Equipment").openShop(player);
			break;

		case 403: // Vannaka
			Shop.SHOPS.get("Slayer Equipment").openShop(player);
			break;

		case 404: // Chaeldar
			Shop.SHOPS.get("Slayer Equipment").openShop(player);
			break;

		case 405: // Duradel
			Shop.SHOPS.get("Slayer Equipment").openShop(player);
			break;

		case 490: // Nieve
			Shop.SHOPS.get("Slayer Equipment").openShop(player);
			break;

		/**
		 * End of slayer masters
		 */

		/**
		 * Shops
		 */
		case 6599:
			Shop.SHOPS.get("PK Points Shop").openShop(player);
			break;

		/**
		 * End of Shops
		 */

		}
	}

	public void fourthClickNpc(NPC npc) {
		
		player.clickNpcType = 0;
		
		if (player.inDebugMode()) {
			player.getActionSender().sendMessage("Fourth click: "+npc.npcId);
		}

		switch (npc.npcId) {
		
		/**
		 * Slayer masters
		 */
		
		case 401: // Turael
			Shop.SHOPS.get("Slayer Rewards").openShop(player);
			break;
			
		case 402: //Mazchna
			Shop.SHOPS.get("Slayer Rewards").openShop(player);
			break;

		case 403: //Vannaka
			Shop.SHOPS.get("Slayer Rewards").openShop(player);
			break;

		case 404: //Chaeldar
			Shop.SHOPS.get("Slayer Rewards").openShop(player);
			break;

		case 405: //Duradel
			Shop.SHOPS.get("Slayer Rewards").openShop(player);
			break;
			
		case 490: //Nieve
			Shop.SHOPS.get("Slayer Rewards").openShop(player);
			break;

		}
	}
	
	private static void slashWeb(Player player, int objectX, int objectY) {
		player.playAnimation(Animation.create(451));
		if (Utility.getRandom(2) == 0) {
			player.getPA().removeWeb(objectX, objectY);
			player.getActionSender().sendMessage("You slash through the web!");
		} else
			player.getActionSender().sendMessage("You fail to cut through the web.");
	}

}