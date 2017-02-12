package com.model.game.character.player;
import com.model.Server;
import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.combat.magic.SpellBook;
import com.model.game.character.npc.NPCHandler;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.content.BrimhavenVines;
import com.model.game.character.player.content.CrystalChest;
import com.model.game.character.player.content.ShinyChest;
import com.model.game.character.player.content.WildernessDitch;
import com.model.game.character.player.content.teleport.Obelisks;
import com.model.game.character.player.content.teleport.Teleport;
import com.model.game.character.player.content.teleport.Teleport.TeleportType;
import com.model.game.character.player.content.teleport.TeleportExecutor;
import com.model.game.character.player.packets.encode.impl.SendInterface;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.packets.encode.impl.SendSidebarInterface;
import com.model.game.character.player.skill.agility.Shortcut;
import com.model.game.character.player.skill.crafting.leather.Tanning;
import com.model.game.character.player.skill.fishing.FishableSpot;
import com.model.game.character.player.skill.fishing.Fishing;
import com.model.game.character.player.skill.impl.Runecrafting;
import com.model.game.character.player.skill.impl.Thieving.Pickpocket;
import com.model.game.character.player.skill.impl.Thieving.Stall;
import com.model.game.character.player.skill.woodcutting.Tree;
import com.model.game.character.player.skill.woodcutting.Woodcutting;
import com.model.game.location.Location;
import com.model.game.shop.Shop;
import com.model.task.ScheduledTask;
import com.model.utility.Location3D;
import com.model.utility.Utility;
import com.model.utility.cache.ObjectDefinition;

public class ActionHandler {

	private Player player;

	public ActionHandler(Player player) {
		this.player = player;
	}

	public void firstClickObject(int id, int x, int y) {

		ObjectDefinition def = ObjectDefinition.getObjectDef(id);
		final Location loc = Location.create(x, y, player.heightLevel);
		if (player.in_debug_mode()) {
			player.write(new SendMessagePacket("[Debug] First click object - ObjectId: [@red@" + id + "@bla@] objectX:[@red@" + x + "@bla@]@bla@] objectY:[@red@" + y + "@bla@]"));
		}

		player.clickObjectType = 0;
		player.turnPlayerTo(x, y);

		if (id >= 21731 && id <= 21737 || id == 12987 || id == 12986) {
			BrimhavenVines.handleBrimhavenVines(player, id);
			return;
		}

		if (player.followId > 0 || player.followId2 > 0)
			player.getPA().resetFollow();
		if (player.stopPlayerPacket || player.teleporting) {
			return;
		}

		if (id != 2283) {
			player.turnPlayerTo(player.objectX, player.objectY);
		}
		
		Tree tree = Tree.forObject(id);
		if (tree != null) {
			Woodcutting.getInstance().chop(player, id, x, y);
			return;
		}

		if (id == 14897) {
			Runecrafting.craftEssence(player, 556, 1, 5, false, 11, 2, 22, 3, 34, 4, 44, 5, 55, 6, 66, 7, 77, 88, 9, 99, 10);
		} else if (id == 14897) {
			Runecrafting.craftEssence(player, 556, 1, 5, false, 11, 2, 22, 3, 34, 4, 44, 5, 55, 6, 66, 7, 77, 88, 9, 99, 10);
		} else if (id == 2479) {
			Runecrafting.craftEssence(player, 558, 2, 5.5, false, 14, 2, 28, 3, 42, 4, 56, 5, 70, 6, 84, 7, 98, 8);
		} else if (id == 2480) {
			Runecrafting.craftEssence(player, 555, 5, 6, false, 19, 2, 38, 3, 57, 4, 76, 5, 95, 6);
		} else if (id == 2481) {
			Runecrafting.craftEssence(player, 557, 9, 6.5, false, 26, 2, 52, 3, 78, 4);
		} else if (id == 2482) {
			Runecrafting.craftEssence(player, 554, 14, 7, false, 35, 2, 70, 3);
		} else if (id == 2483) {
			Runecrafting.craftEssence(player, 559, 20, 7.5, false, 46, 2, 92, 3);
		} else if (id == 2484) {
			Runecrafting.craftEssence(player, 564, 27, 8, true, 59, 2);
		} else if (id == 2487) {
			Runecrafting.craftEssence(player, 562, 35, 8.5, true, 74, 2);
		} else if (id == 17010) {
			Runecrafting.craftEssence(player, 9075, 40, 8.7, true, 82, 2);
		} else if (id == 2486) {
			Runecrafting.craftEssence(player, 561, 45, 9, true, 91, 2);
		} else if (id == 2485) {
			Runecrafting.craftEssence(player, 563, 50, 9.5, true);
		} else if (id == 2488) {
			Runecrafting.craftEssence(player, 560, 65, 10, true);
		} else if (id == 30624) {
			Runecrafting.craftEssence(player, 565, 77, 10.5, true);
		} else if (id == 2452) {
			int hatId = player.getEquipment().getHelmetId();
			if (hatId == Runecrafting.AIR_TIARA || player.getItems().playerHasItem(1438, 1)) Runecrafting.enterAirAltar(player);
		} else if (id == 2455) {
			int hatId = player.getEquipment().getHelmetId();
			if (hatId == Runecrafting.EARTH_TIARA || player.getItems().playerHasItem(1440, 1)) Runecrafting.enterEarthAltar(player);
		} else if (id == 2456) {
			int hatId = player.getEquipment().getHelmetId();
			if (hatId == Runecrafting.FIRE_TIARA || player.getItems().playerHasItem(1442, 1)) Runecrafting.enterFireAltar(player);
		} else if (id == 2454) {
			int hatId = player.getEquipment().getHelmetId();
			if (hatId == Runecrafting.WATER_TIARA || player.getItems().playerHasItem(1444, 1)) Runecrafting.enterWaterAltar(player);
		} else if (id == 2457) {
			int hatId = player.getEquipment().getHelmetId();
			if (hatId == Runecrafting.BODY_TIARA || player.getItems().playerHasItem(1446, 1)) Runecrafting.enterBodyAltar(player);
		} else if (id == 2453) {
			int hatId = player.getEquipment().getHelmetId();
			if (hatId == Runecrafting.MIND_TIARA || player.getItems().playerHasItem(1448, 1)) Runecrafting.enterMindAltar(player);
		}
		
		player.getFarming().patchObjectInteraction(id, -1, x, y);
		/*Obelisks.get().activate(player, id);*/
		player.getMining().mine(id, new Location3D(x, y, player.heightLevel));
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
				player.write(new SendMessagePacket("You need a key to open this chest."));
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
						player.getPA().movePlayer(new Location(3017, 3850, 0));
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
						player.getPA().movePlayer(new Location(3069, 10255, 0));
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
				player.getPA().movePlayer(new Location(2486, 9797, 0));
			} else if (player.getX() == 2444) {
				player.getPA().movePlayer(new Location(2430, 3424, 0));
			}
			break;

		case "cave":
			if (player.getX() == 2430) {
				player.getPA().movePlayer(new Location(2444, 9825, 0));
			}
			break;
			
		case "passage":
			if (player.getX() == 2970) {
				player.getPA().movePlayer(new Location(2974, 4384, 2));
			} else if (player.getX() == 2974) {
				player.getPA().movePlayer(new Location(2970, 4384, 2));
			}
			break;
			
		case "lever":
			if (player.getX() == 3153)
			TeleportExecutor.executeLeverTeleport(player, new Teleport(new Location(3090, 3475, player.getZ()), TeleportType.LEVER));
			break;
		}

		switch (id) {
		
		case 13641:
			if(player.getArea().inWild()) {
				return;
			}
			if (player.onAuto) {
				player.write(new SendMessagePacket("You can't switch spellbooks with Autocast enabled."));
				return;
			}
			switch (player.getSpellBook()) {
			case MODERN:
				player.setSpellBook(SpellBook.ANCIENT);
				player.write(new SendSidebarInterface(6, 12855));
				player.write(new SendMessagePacket("An ancient wisdom fills your mind."));
				break;
			case ANCIENT:
				player.setSpellBook(SpellBook.LUNAR);
				player.write(new SendSidebarInterface(6, 29999));
				player.write(new SendMessagePacket("The power of the moon overpowers you."));
				break;
			case LUNAR:
				player.setSpellBook(SpellBook.MODERN);
				player.write(new SendSidebarInterface(6, 1151));
				player.write(new SendMessagePacket("You feel a drain on your memory."));
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
				player.turnPlayerTo(x, y);
			}
			break;

		case 24303:
			player.getPA().movePlayer(2840, 3539, 0);
			break;
		
		case 23271: 
			if (!player.ditchDelay.elapsed(1000)) {
				return;
			}
			player.turnPlayerTo(x, y);
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
					new Location(3090, 3956, player.getZ()), TeleportType.LEVER));
			break;

		case 5959:
			TeleportExecutor.executeLeverTeleport(player, new Teleport(
					new Location(2539, 4712, player.getZ()), TeleportType.LEVER));
			break;

		case 1814:
			TeleportExecutor.executeLeverTeleport(player, new Teleport(
					new Location(3158, 3953, player.getZ()), TeleportType.LEVER));
			break;

		case 4950:
			TeleportExecutor.executeLeverTeleport(player, new Teleport(
					new Location(3087, 3500, player.getZ()), TeleportType.LEVER));
			break;

		case 1816:
			TeleportExecutor.executeLeverTeleport(player, new Teleport(
					new Location(2271, 4680, player.getZ()), TeleportType.LEVER));
			break;

		case 1817:
			TeleportExecutor.executeLeverTeleport(player, new Teleport(
					new Location(3067, 10253, player.getZ()), TeleportType.LEVER));
			break;
			
		case 26761:
			TeleportExecutor.executeLeverTeleport(player, new Teleport(
					new Location(3153, 3923, player.getZ()), TeleportType.LEVER));
			break;
			
		case 1815:
			TeleportExecutor.executeLeverTeleport(player, new Teleport(
					new Location(3090, 3475, player.getZ()), TeleportType.LEVER));
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
				player.write(new SendMessagePacket("You can't switch spellbooks with Autocast enabled."));
				return;
			}
			switch (player.getSpellBook()) {
			case MODERN:
				player.setSpellBook(SpellBook.ANCIENT);
				player.write(new SendSidebarInterface(6, 12855));
				player.write(new SendMessagePacket("An ancient wisdom fills your mind."));
				break;
			case ANCIENT:
				player.setSpellBook(SpellBook.LUNAR);
				player.write(new SendSidebarInterface(6, 29999));
				player.write(new SendMessagePacket("The power of the moon overpowers you."));
				break;
			case LUNAR:
				player.setSpellBook(SpellBook.MODERN);
				player.write(new SendSidebarInterface(6, 1151));
				player.write(new SendMessagePacket("You feel a drain on your memory."));
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
			TeleportExecutor.teleport(player, new Teleport(new Location(3105,
					3951, 0), TeleportType.NORMAL), false);
			break;

		case 9707:
			TeleportExecutor.teleport(player, new Teleport(new Location(3105,
					3956, 0), TeleportType.NORMAL), false);
			break;

		default:
			break;

		}
	}

	public void secondClickObject(int objectType, int obX, int obY) {
		if (player.in_debug_mode()) {
			player.write(new SendMessagePacket("[Debug] Second click object - ObjectId: [@red@" + objectType+"@bla@]"));
		}
		if (player.teleporting) {
			return;
		}
		
		player.clickObjectType = 0;
		
		player.getFarming().patchObjectInteraction(objectType, -1, obX, obY);
		
		new Location3D(obX, obY, player.heightLevel);
		ObjectDefinition objectDef = ObjectDefinition.getObjectDef(objectType);
		switch (objectDef.name.toLowerCase()) {

		case "bank":
		case "Bank":
		case "bank booth":
		case "booth":
			player.getPA().openBank();
			break;
			
		case "furnace":
			player.getSmithing().sendSmelting(player);
			break;
		}
		
		Location3D location = new Location3D(obX, obY, player.heightLevel);
		switch (objectType) {
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
		
		/**
		 * Thieving stalls
		 */
		case 11730:
			player.getThieving().steal(Stall.CAKE, objectType, location);
			break;
			
		case 11731:
			player.getThieving().steal(Stall.GEM, objectType, location);
			break;
			
		case 11732:
			player.getThieving().steal(Stall.FUR, objectType, location);
			break;
			
		case 11734:
			player.getThieving().steal(Stall.SILVER, objectType, location);
			break;
			
		case 14011:
			player.getThieving().steal(Stall.WINE, objectType, location);
			break;
			
		}
	}

	public void thirdClickObject(int objectType, int obX, int obY) {
		if (player.in_debug_mode()) {
			player.write(new SendMessagePacket("[Debug] Third click object - ObjectId: [@red@" + objectType+"@bla@]"));
		}
		player.clickObjectType = 0;

		switch (objectType) {
		default:
			break;
		}
	}

	public void firstClickNpc(Npc npc) {
		int npcType = player.npcType;
		player.clickNpcType = 0;
		player.rememberNpcIndex = player.npcClickIndex;
		if (player.in_debug_mode()) {
			player.write(new SendMessagePacket("First click NPC:  " + npcType));
		}

		if (FishableSpot.fishingNPC(npcType)) {
			Fishing.attemptFishing(player, npcType, 1);
			return;
		}
		if (player.petId > 0) {
			player.getPets().talktoPet(player, npcType, npc);
		}
		switch (npcType) {
		
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
			player.write(new SendMessagePacket("You currently have @blu@" + player.getVotePoints()
					+ "@bla@ vote points, and @blu@" + player.getTotalVotes() + "@bla@ total votes."));
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
			player.write(new SendMessagePacket("@red@Gear points@bla@ refill to @blu@2500@bla@ every 5 minutes."));
			player.write(new SendMessagePacket(
					"@blu@Did you know, you can type ::food, ::veng, ::barrage, and ::pots, to spawn them?"));
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
			if (!player.canChanceAppearance()) {
				player.write(new SendMessagePacket("You must remove your equipment before changing your appearence."));
				player.canChangeAppearance = false;
			} else {
				player.write(new SendInterface(3559));
				player.canChangeAppearance = true;
			}
			break;

		/**
		 * Tanning npc
		 */
		case 5809:
			Tanning.sendTanningInterface(player);
			break;

		/**
		 * Banker
		 */
		case 394:
			player.getDialogueHandler().sendDialogues(10000, 394);
			break;

		default:
			player.dialogueAction = -1;
			if (player.in_debug_mode()) {
				Utility.println("First Click Npc : " + npcType);
			}
			break;
		}
	}

	public void secondClickNpc(Npc npc) {
		int npcType = player.npcType;
		
		player.clickNpcType = 0;
		player.rememberNpcIndex = player.npcClickIndex;
		
		if (FishableSpot.fishingNPC(npcType)) {
			Fishing.attemptFishing(player, npcType, 2);
			return;
		}

		if(player.petId > 0 && player.getPets().isPetNPC(npcType)) {
			player.getPets().pickupPet(player, true, npc);
			return;
		}

		switch (npcType) {
		
		case 5567:
			if (!player.deathShopChat) {
				player.dialogue().start("DEATH_SHOP_DIALOGUE", player);
			} else {
				player.dialogue().start("DEATH_SHOP_DIALOGUE2", player);
			}
			break;
		
		/**
		 * Thieving pickpocket npcs
		 */
		case 3078:
			player.getThieving().steal(Pickpocket.MAN, NPCHandler.npcs[player.rememberNpcIndex]);
			break;
			
		case 3086:
			player.getThieving().steal(Pickpocket.FARMER, NPCHandler.npcs[player.rememberNpcIndex]);
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
				player.write(new SendMessagePacket("You currently have @blu@" + player.getVotePoints() + "@bla@ vote points, and @blu@" + player.getTotalVotes() + "@bla@ total votes."));
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
			
		default:
			if (player.in_debug_mode()) {
				player.write(new SendMessagePacket("Second Click Npc : " + npcType));
			}
			break;

		}
	}

	public void thirdClickNpc(int npcType) {
		
		player.clickNpcType = 0;
		player.rememberNpcIndex = player.npcClickIndex;
		
		if (player.getPets().isPetNPC(npcType)) {
			if (player.getPets().hasNextStage(player, npcType)) {
				player.getPets().handleNextStage(player);
			} else {
				player.getPets().pickupPet(player, true, World.getWorld().getNpcs().get(player.npcClickIndex));
			}
		}
		
		switch (npcType) {

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

		default:
			if (player.in_debug_mode()) {
				Utility.println("Third Click NPC : " + npcType);
			}
			break;

		}
	}

	public void fourthClickNpc(int npcType) {
		
		player.clickNpcType = 0;
		player.rememberNpcIndex = player.npcClickIndex;

		switch (npcType) {
		
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
	
		default:
			if (player.in_debug_mode()) {
				player.write(new SendMessagePacket("Fourth Click NPC : " + npcType));
			}
			break;

		}
	}
	
	private static void slashWeb(Player player, int objectX, int objectY) {
		player.playAnimation(Animation.create(451));
		if (Utility.getRandom(2) == 0) {
			player.getPA().removeWeb(objectX, objectY);
			player.write(new SendMessagePacket("You slash through the web!"));
		} else
			player.write(new SendMessagePacket("You fail to cut through the web."));
	}

}