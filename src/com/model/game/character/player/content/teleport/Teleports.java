package com.model.game.character.player.content.teleport;

import java.util.Arrays;

import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.teleport.TeleportHandler.TeleportationTypes;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.item.Item;
import com.model.game.location.Location;

/**
 * 
 * @author Patrick van Elderen
 * http://www.rune-server.org/members/ipkmills/
 *
 */
public class Teleports {
	
	@SuppressWarnings("unused")
	private static final int AIR_RUNE = 556, WATER_RUNE = 555,
			EARTH_RUNE = 557, FIRE_RUNE = 554, MIND_RUNE = 558,
			NATURE_RUNE = 561, CHAOS_RUNE = 562, DEATH_RUNE = 560,
			BLOOD_RUNE = 565, SOUL_RUNE = 566, ASTRAL_RUNE = 9075,
			LAW_RUNE = 563, STEAM_RUNE = 4694, MIST_RUNE = 4695,
			DUST_RUNE = 4696, SMOKE_RUNE = 4697, MUD_RUNE = 4698,
			LAVA_RUNE = 4699, ARMADYL_RUNE = 21773, BANANA = 1963;
	
	enum MagicBookTeleportData {
		
		HOME_MODERN_SPELL_BOOK("Home", 4171, 0, 0, -1, -1, 0),
		HOME_ANCIENT_SPELLBOOK("Home", 50056, 0, 0, -1, -1, 0),
		//VARROCK("Varrock", 4140, 25, 35, 3182, 3441, 0, new Item(LAW_RUNE), new Item(AIR_RUNE, 3), new Item(FIRE_RUNE)),
		//LUMBRIDGE("Lumbridge", 4143, 31, 41, 3222, 3218, 0, new Item(LAW_RUNE), new Item(AIR_RUNE, 3), new Item(EARTH_RUNE)),
		FALADOR("Falador", 4146, 37, 48, 2945, 3371, 0, new Item(LAW_RUNE), new Item(AIR_RUNE, 3), new Item(WATER_RUNE)),
		//CAMELOT("Camelot", 4150, 45, 55, 2747, 3477, 0, new Item(LAW_RUNE), new Item(AIR_RUNE, 5)),
		//ARDOUGNE("Ardougne", 6004, 55, 61, 2662, 3305, 0, new Item(LAW_RUNE, 2), new Item(WATER_RUNE, 2)),
		WATCHTOWER("Watchtower", 6005, 58, 68, 2549, 3112, 0, new Item(LAW_RUNE, 2), new Item(EARTH_RUNE, 2)),
		TROLLHEIM("Trollheim", 29031, 61, 68, 2888, 3674, 0, new Item(LAW_RUNE, 2), new Item(FIRE_RUNE, 2)),
		APE_ATOLL("Ape atoll", 72038, 64, 74, 2753, 2785, 0, new Item(LAW_RUNE, 2), new Item(FIRE_RUNE, 2), new Item(WATER_RUNE, 2), new Item(BANANA)),
		//PADDEWWA("Paddewwa", 50235, 54, 64, 3098, 9884, 0, new Item(LAW_RUNE, 2), new Item(FIRE_RUNE), new Item(WATER_RUNE)),
		//SENNTISTEN("Senntisten", 50245, 60, 70, 3322, 3336, 0, new Item(LAW_RUNE, 2), new Item(SOUL_RUNE)),
		//KHARYLL("Kharyll", 50253, 66, 76, 3492, 3471, 0, new Item(LAW_RUNE, 2), new Item(BLOOD_RUNE, 2)),
		//LASSAR("Lassar", 51005, 72, 82, 3006, 3471, 0, new Item(LAW_RUNE, 2), new Item(WATER_RUNE, 4)),
		//DAREEYAK("Dareeyak", 51013, 78, 88, 3161, 3671, 0, new Item(LAW_RUNE, 2), new Item(FIRE_RUNE, 3), new Item(AIR_RUNE, 2)),
		CARRALLANGAR("Carrallanger", 51023, 84, 94, 3156, 3666, 0, new Item(LAW_RUNE, 2), new Item(SOUL_RUNE, 2)),
		ANNAKARL("Annakarl", 51031, 90, 100, 3288, 3886, 0, new Item(LAW_RUNE, 2), new Item(BLOOD_RUNE, 2)),
		GHORROCK("Ghorrock", 51039, 96, 106, 2977, 3873, 0, new Item(LAW_RUNE, 2), new Item(WATER_RUNE, 8));
		
		String teleportName;
		
		private final int buttonId, levelReq, experience, x, y, h;
		
		private Item[] requiredRunes;
		
		private MagicBookTeleportData(String teleportName, int buttonId, int levelReq, int experience, int x, int y, int h, Item... requiredRunes) {
			this.teleportName = teleportName;
			this.buttonId = buttonId;
			this.levelReq = levelReq;
			this.experience = experience;
			this.x = x;
			this.y = y;
			this.h = h;
			this.requiredRunes = requiredRunes;
		}
		
		public String getTeleportName() {
			return teleportName;
		}
		
		public int getButton() {
			return buttonId;
		}
		
		public int getMagicLevelRequirement() {
			return levelReq;
		}
		
		public int getExperience() {
			return experience;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public int getHeight() {
			return h;
		}

		public Item[] getRequiredRunes() {
			return requiredRunes;
		}

	}
	
	private static void castHomeTeleport(Player player) {
		TeleportExecutor.teleport(player, new Location(3096, 3503, 0));
	}
	
	public static boolean startTeleport(Player player, int buttonId) {
		if (!player.teleblock.elapsed(player.teleblockLength)) {
			return false;
		}

		if (player.getArea().inWild() && player.wildLevel > 20 && !player.usingGlory && !player.usingObelisk) {
			player.write(new SendMessagePacket("You cannot teleport above level 20 wilderness."));
			return false;
		}
		
		//Minigame teleports
		if(buttonId == 4140 || buttonId == 50235 || buttonId == 117112) {
			TeleportHandler.open(player, TeleportationTypes.MINIGAME);
			return false;
		}
		
		//PvP teleport
		if(buttonId == 4150 || buttonId == 51005 || buttonId == 117154) {
			TeleportHandler.open(player, TeleportationTypes.PVP);
			return false;
		}
		
		//Shop teleport
		if(buttonId == 6004 || buttonId == 51013 || buttonId == 117162) {
			player.write(new SendMessagePacket("Shops were moved to the home area, type ::home to access the shops."));
			return false;
		}
		
		//Skilling teleports
		if(buttonId == 4143 || buttonId == 50245 || buttonId == 117123) {
			TeleportHandler.open(player, TeleportationTypes.SKILLING);
			return false;
		}
		
		//PVM teleports
		if(buttonId == 4146 || buttonId == 50253 || buttonId == 117131) {
			TeleportHandler.open(player, TeleportationTypes.PVM);
			return false;
		}
		
		if (buttonId == 4171 || buttonId == 50056 || buttonId == 75010 || buttonId == 84237) {
			castHomeTeleport(player);
		} else {
			for (MagicBookTeleportData data : MagicBookTeleportData.values()) {
				if (data.getButton() == buttonId) {
					if (player.getSkills().getExperience(Skills.MAGIC) < data.getMagicLevelRequirement()) {
						player.write(new SendMessagePacket("You need atleast level "+data.getMagicLevelRequirement()+" magic to teleport to "+data.getTeleportName()+"."));
					}
					else if (Arrays.asList(data.getRequiredRunes()).stream().filter(i -> player.getItems().playerHasItems(i)).toArray().length == data.getRequiredRunes().length) {
					     TeleportExecutor.teleport(player, new Location(data.getX(), data.getY(), data.getHeight()));
					     player.getSkills().addExperience(Skills.MAGIC, data.getExperience());
					     Arrays.asList(data.getRequiredRunes()).stream().forEach(i -> player.getItems().remove(i));
					     return true;
					} else {
					     player.write(new SendMessagePacket("You do not have the correct teleporting materials."));
					     return false;
					}
					break;
				}
			}
		}
		return false;
	}
	
	public static final int[] teleport_button_ids = new int[] {
		4171, 50056, 4140, 4143, 4146, 4150, 6004, 6005, 29031, 72038, 50235, 50245, 50253, 51005, 51013, 51023, 51031, 51039, 117112, 117154, 117162, 117123, 117131
	};

	public static boolean isTeleportButton(Player player, int button) {
		for (int btn : teleport_button_ids) {
			if (button == btn) {
				return true;
			}
		}
		return false;
	}


}
