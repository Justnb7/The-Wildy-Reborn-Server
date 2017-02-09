package com.model.game.character.player.skill.impl;

import com.model.game.character.Animation;
import com.model.game.character.Graphic;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.location.Location;
import com.model.utility.json.definitions.ItemDefinition;

public class Runecrafting {
	
	/**
	 * @author Patrick van Elderen, http://www.rune-server.org/members/_patrick_/
	 */
	
	/**
	 * Rune pouch level requirements.
	 */
	public final static int[] LEVEL_REQ = { 1, 25, 50, 75 };
	
	/**
	 * Tiara's
	 */
	public final static int RUNE_ESSENCE = 1436, PURE_ESSENCE = 7936,
			AIR_TIARA = 5527, MIND_TIARA = 5529, WATER_TIARA = 5531,
			BODY_TIARA = 5533, EARTH_TIARA = 5535, FIRE_TIARA = 5537,
			COSMIC_TIARA = 5539, NATURE_TIARA = 5541, CHAOS_TIARA = 5543,
			LAW_TIARA = 5545, DEATH_TIARA = 5547, BLOOD_TIARA = 5549,
			SOUL_TIARA = 5551, ASTRAL_TIARA = 9106;

	/**
	 * Checks if the player has/ is wearing a Tiara.
	 * @param id
	 * @return
	 */
	public static boolean isTiara(int id) {
		return id == AIR_TIARA || id == MIND_TIARA || id == WATER_TIARA
				|| id == BODY_TIARA || id == EARTH_TIARA || id == FIRE_TIARA
				|| id == COSMIC_TIARA || id == NATURE_TIARA
				|| id == CHAOS_TIARA || id == LAW_TIARA || id == DEATH_TIARA
				|| id == BLOOD_TIARA || id == SOUL_TIARA || id == ASTRAL_TIARA;
	}
	
	/**
	 * Moves the player to the location of the altar they have entered.
	 * @param player
	 * @param dest
	 */
	private static void enterAltar(Player player, Location dest) {
		player.write(new SendMessagePacket("A mysterious force grabs hold of you."));
		player.useStairs(-1, dest, 0, 1);
	}
	
	/**
	 * Entering the Air Altar
	 * @param player
	 */
	public static void enterAirAltar(Player player) {
		enterAltar(player, new Location(2841, 4829, 0));
	}

	/**
	 * Entering the Mind Altar
	 * @param player
	 */
	public static void enterMindAltar(Player player) {
		enterAltar(player, new Location(2792, 4827, 0));
	}
	
	/**
	 * Entering the Water Altar
	 * @param player
	 */
	public static void enterWaterAltar(Player player) {
		enterAltar(player, new Location(3482, 4838, 0));
	}

	/**
	 * Entering the Earth Altar
	 * @param player
	 */
	public static void enterEarthAltar(Player player) {
		enterAltar(player, new Location(2655, 4830, 0));
	}
	
	/**
	 * Entering the Fire Altar
	 * @param player
	 */
	public static void enterFireAltar(Player player) {
		enterAltar(player, new Location(2574, 4848, 0));
	}

	/**
	 * Entering the Body Altar
	 * @param player
	 */
	public static void enterBodyAltar(Player player) {
		enterAltar(player, new Location(2522, 4825, 0));
	}
	
	/**
	 * Method used to craft Rune Essence into Runes
	 * @param player
	 * @param rune
	 * @param level
	 * @param experience
	 * @param pureEssOnly
	 * @param multipliers
	 */
	public static void craftEssence(Player player, int rune, int level, double experience, boolean pureEssOnly, int... multipliers) {
		//System.out.println("reached method crafEssence");
		int actualLevel = player.getSkills().getLevel(Skills.RUNECRAFTING);
		if (actualLevel < level) {
			player.getDialogueHandler().sendStatement(player, "You need a runecrafting level of " + level + " to craft this rune.");
			return;
		}
		//System.out.println("pure essence check");
		int runes = player.getItems().getItemAmount(PURE_ESSENCE);
		if (runes > 0)
			//System.out.println("delete pure essence runes");
			player.getItems().deleteItem(PURE_ESSENCE, runes);
		if (!pureEssOnly) {
			//System.out.println("rune essence check");
			int normalEss = player.getItems().getItemAmount(RUNE_ESSENCE);
			if (normalEss > 0) {
				//System.out.println("delete rune essence");
				player.getItems().deleteItem(RUNE_ESSENCE, normalEss);
				runes += normalEss;
			}
		}
		if (runes == 0) {
			player.getDialogueHandler().sendStatement(player, "You don't have " + (pureEssOnly ? "pure" : "rune") + " essence.");
			return;
		}
		double totalXp = experience * runes;
		if (hasRcingSuit(player))
			totalXp *= 1.025;
		player.getSkills().addExperience(Skills.RUNECRAFTING, totalXp);
		for (int i = multipliers.length - 2; i >= 0; i -= 2) {
			if (actualLevel >= multipliers[i]) {
				runes *= multipliers[i + 1];
				break;
			}
		}
		//System.out.println("Runes were crafted and experience was granted.");
		player.playGraphics(Graphic.create(186));
		player.playAnimation(Animation.create(791));
		player.lock(5);
		player.getItems().addItem(rune, runes);
		player.write(new SendMessagePacket("You bind the temple's power into " + ItemDefinition.forId(rune).getName().toLowerCase() + "s."));
	}
	
	/**
	 * @exception 667 revision
	 * Runecrafting suite
	 * @param player
	 * @return
	 */
	public static boolean hasRcingSuit(Player player) {
		if (player.getEquipment().getHelmetId() == 21485 && player.getEquipment().getChestId() == 21484 && player.getEquipment().getLegsId() == 21486 && player.getEquipment().getBootsId() == 21487)
			return true;
		return false;
	}
	
	/**
	 * A method used to locate an altar based on what tiara/ talisman the player has.
	 * @param p
	 * @param xPos
	 * @param yPos
	 */
	public static void locate(Player p, int xPos, int yPos) {
		String x = "";
		String y = "";
		int absX = p.getX();
		int absY = p.getY();
		if (absX >= xPos)
			x = "west";
		if (absY > yPos)
			y = "South";
		if (absX < xPos)
			x = "east";
		if (absY <= yPos)
			y = "North";
		p.write(new SendMessagePacket("The talisman pulls towards " + y + "-" + x + "."));
	}
	
	/**
	 * Checks the amount of Runes that are in a pouch
	 * @param p
	 * @param i
	 */
	public static void checkPouch(Player player, int i) {
		if (i < 0)
			return;
		player.write(new SendMessagePacket("This pouch contains " + player.getPouches()[i] + " rune essence."));
	}

	/**
	 * The amount of Runes that can be stored in the pouches
	 */
	public static final int[] POUCH_SIZE = { 3, 6, 9, 12 };

	/**
	 * Fills your pouch with essence
	 * @param player
	 * @param i
	 */
	public static void fillPouch(Player player, int i) {
		if (i < 0)
			return;
		if (LEVEL_REQ[i] > player.getSkills().getLevel(Skills.RUNECRAFTING)) {
			player.write(new SendMessagePacket("You need a runecrafting level of " + LEVEL_REQ[i] + " to fill this pouch."));
			return;
		}
		int essenceToAdd = POUCH_SIZE[i] - player.getPouches()[i];
		if (essenceToAdd > player.getItems().getItemAmount(1436))
			essenceToAdd = player.getItems().getItemAmount(1436);
		if (essenceToAdd > POUCH_SIZE[i] - player.getPouches()[i])
			essenceToAdd = POUCH_SIZE[i] - player.getPouches()[i];
		if (essenceToAdd > 0) {
			player.getItems().deleteItem(1436, essenceToAdd);
			player.getPouches()[i] += essenceToAdd;
		}
		if (!player.getItems().playerHasItem(1436)) {
			player.write(new SendMessagePacket("You don't have any essence with you."));
			return;
		}
		if (essenceToAdd == 0) {
			player.write(new SendMessagePacket("Your pouch is full."));
			return;
		}
	}

	/**
	 * Empties the essence in your pouch
	 * @param player
	 * @param i
	 */
	public static void emptyPouch(Player player, int i) {
		if (i < 0)
			return;
		int toAdd = player.getPouches()[i];
		if (toAdd > player.getItems().getFreeSlots())
			toAdd = player.getItems().getFreeSlots();
		if (toAdd > 0) {
			player.getItems().addItem(1436, toAdd);
			player.getPouches()[i] -= toAdd;
		}
		if (toAdd == 0) {
			player.write(new SendMessagePacket("Your pouch contains no essence."));
			return;
		}
	}

}
