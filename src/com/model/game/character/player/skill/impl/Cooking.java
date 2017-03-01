package com.model.game.character.player.skill.impl;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendClearScreen;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.SkillTask;
import com.model.task.Stackable;
import com.model.task.Walkable;
import com.model.utility.Utility;
import com.model.utility.cache.ObjectDefinition;
import com.model.utility.json.definitions.ItemDefinition;


/**
 * 
 * @author Patrick van Elderen
 * http://www.rune-server.org/members/_patrick_/
 * @version 1.0 @date 29-3-2016 19:26:10
 */

public class Cooking extends SkillTask {
	

	public enum Cookables {
		
		/**
		 * Meat
		 */
		RAW_MEAT(2132, 1, 200, 30, 2146, 2142, false),
		RAW_CHICKEN(2138, 1, 200, 30, 2144, 4291, false),
		RAW_RABBIT(3226, 1, 200, 30, 7222, 3228, false),
		RAW_CRAB_MEAT(7518, 21, 200, 100, 7520, 7521, false),
		
		/**
		 * Fish
		 */
		RAW_SHRIMP(317, 1, 34, 30, 7954, 315, false),
		RAW_KARAMBWANJI(3150, 1, 35, 10, 3148, 3151, false),
		RAW_SARDINE(327, 1, 38, 40, 323, 325, false),
		RAW_ANCHOVIES(321, 1, 34, 30, 323, 319, false),
		POISON_KARAMBWAN(3142, 1, 20, 80, 3148, 3151, false),
		RAW_HERRING(345, 5, 37, 50, 357, 347, false),
		RAW_MACKEREL(353, 10, 45, 60, 357, 355, false),
		RAW_TROUT(335, 15, 50, 70, 343, 333, false),
		RAW_COD(341, 18, 39, 75, 343, 339, false),
		RAW_PIKE(349, 20, 52, 80, 349, 351, false),
		RAW_SALMON(331, 25, 58, 90, 343, 329, false),
		RAW_SLIMY_EEL(3379, 28, 58, 95, 3383, 3381, false),
		RAW_TUNA(359, 30, 63, 100, 367, 361, false),
		RAW_KARAMBWAN(3142, 5, 94, 225, 3148, 3144, false),
		RAW_CAVE_EEL(5001, 38, 40, 115, 5006, 5003, false),
		RAW_LOBSTER(377, 40, 66, 120, 381, 379, false),
		RAW_BASS(363, 43, 80, 130, 367, 365, false),
		RAW_SWORDFISH(371, 45, 86, 140, 375, 373, false),
		RAW_LAVA_EEL(2148, 53, 53, 30, -1, 2149, false),
		RAW_MONKFISH(7944, 62, 90, 150, 7948, 7946, false),
		RAW_SHARK(383, 80, 100, 210, 387, 385, false),
		RAW_SEA_TURTLE(395, 82, 200, 212, 399, 397, false),
		RAW_ANGLERFISH(13439, 84, 200, 230, 13443, 13441, false),
		DARK_CRAB(11934, 90, 200, 215, 11938, 11936, false),
		RAW_MANTA_RAY(389, 91, 200, 216, 393, 391, false),
		
		/**
		 * Pies
		 */
		RAW_REDBERRY_PIE(2321, 10, 200, 78, 2329, 2325, true),
		RAW_MEAT_PIE(2319, 20, 200, 104, 2329, 2327, true),
		RAW_MUD_PIE(7168, 29, 200, 128, 2329, 7170, true),
		RAW_APPLE_PIE(2317, 30, 200, 130, 2329, 2323, true),
		RAW_GARDEN_PIE(7176, 34, 200, 138, 2329, 7178, true),
		RAW_FISH_PIE(7186, 47, 200, 164, 2329, 7188, true),
		RAW_ADMIRAL_PIE(7196, 70, 200, 210, 2329, 7198, true),
		RAW_WILD_PIE(7206, 85, 200, 240, 2329, 7208, true),
		RAW_SUMMER_PIE(7216, 95, 200, 260, 2329, 7218, true),
		
		/**
		 * Pizza
		 */
		PLAIN_PIZZA(2287, 35, 200, 143, 2305, 2289, true),
		
		/**
		 * Cake
		 */
		RAW_FISHCAKE(7529, 31, 200, 100, 7531, 7530, true),
		CAKE(1889, 40, 200, 180, 1903, 1891, true),

		/**
		 * Potato
		 */
		RAW_POTATO(1942, 7, 200, 15, 6699, 6701, false);
		
		/**
		 * The uncooked fish ID
		 */
		private final int raw;
		
		/**
		 * The level required to cook the fish
		 */
		private final int lvl;
		
		/**
		 * The level the fish stops burning at
		 */
		private final int burningLvl;
		
		/**
		 * The experience you receive per succesful cook.
		 */
		private final int xp;
		
		/**
		 * The id of the burnt fish.
		 */
		private final int burnt;
		
		/**
		 * The cooked fishes ID
		 */
		private final int cooked;
		
		/**
		 * Can only be used on fire
		 */
		private final boolean stoveOrRangeOnly;

		private Cookables(int raw, int lvl, int burningLvl, int exp,
				int burnt, int cooked, boolean stoveOrRangeOnly) {
			this.raw = raw;
			this.lvl = lvl;
			this.burningLvl = burningLvl;
			this.xp = exp;
			this.burnt = burnt;
			this.cooked = cooked;
			this.stoveOrRangeOnly = stoveOrRangeOnly;
		}

		/**
		 * An unmodifiable enum set
		 */

	    private static final Set<Cookables> cook = Collections.unmodifiableSet(EnumSet.allOf(Cookables.class));

	    public int getRawItem() {
			return raw;
		}

		public int getLvl() {
			return lvl;
		}

		public int getBurntId() {
			return burnt;
		}

		public int getProduct() {
			return cooked;
		}

		public int getXp() {
			return xp;
		}

		public int getBurningLvl() {
			return burningLvl;
		}

		public boolean isStoveOrRangeOnly() {
			return stoveOrRangeOnly;
		}

		/**
		 * 
		 * @param id
		 * @return the uncooked fish id
		 */

		public static Cookables forId(int id) {
			return cook.stream().filter(cooking -> cooking.raw == id).findFirst().orElse(null);
		}

		/**
		 * 
		 * @param fishId
		 * @return the fish Id
		 */

		public static boolean isCookable(int id) {
			return cook.stream().anyMatch(cooking -> cooking.raw == id);
		}
	}
	
	private final Cookables data;

	public Cooking(Player player, int delay, Cookables data) {
		super(player, 4, Walkable.NON_WALKABLE, Stackable.NON_STACKABLE, false);
		this.data = data;
	}

	public static void attemptCooking(Player player, int id, int object) {

		Cookables data = Cookables.forId(id);

		if (!meetsRequirements(player, data, object)) {
			return;
		}
		player.write(new SendClearScreen());
		player.getMovementHandler().stopMovement();
		player.setSkillTask(new Cooking(player, id, data));
	}

	private static boolean meetsRequirements(Player player, Cookables data, int object) {
		ObjectDefinition objectDef = ObjectDefinition.getObjectDef(object);
		if (data == null) {
			return false;
		}
		if (player.getSkills().getLevel(Skills.COOKING) < data.getLvl()) {
			player.message("You need a cooking level of " + data.getLvl() + " to cook this food.");
			return false;
		}
		if (!player.getItems().playerHasItem(data.getRawItem(), 1)) {
			player.write(new SendMessagePacket("You have ran out of food to cook"));
			return false;
		}
		if (data.isStoveOrRangeOnly()) {
			if (objectDef.name.contains("stove") || objectDef.name.contains("range") || objectDef.name.contains("Cooking range")) {
				return true;
			} else {
				player.message("You may only cook this on a stove or cooking range.");
				return false;
			}
		}
		return true;
	}
	
	private boolean isBurned(Cookables cook, Player player) {
		int level = player.getSkills().getLevel(Skills.COOKING);
		if (player.getEquipment().getGlovesId() == 775) {
			if (level >= (cook.getBurningLvl() - (cook.getProduct() == 391 ? 0 : 6)))
				return false;
		}
		int levelsToStopBurn = cook.getBurningLvl() - level;
		if (levelsToStopBurn > 20) {
			levelsToStopBurn = 20;
		}
		return Utility.getRandom(34) <= levelsToStopBurn;
	}

	@Override
	public void execute() {
		if (getPlayer() == null || !getPlayer().isActive()) {
			stop();
			return;
		}
		if (!getPlayer().getItems().playerHasItem(data.getRawItem(), 1)) {
			getPlayer().message("You have run out of food to cook.");
			stop();
			return;
		}
		getPlayer().write(new SendMessagePacket("You attempt to cook the " + ItemDefinition.forId(data.getProduct()).getName().toLowerCase() + "."));
		getPlayer().playAnimation(Animation.create(896));
		if ((getPlayer().getSkills().getLevel(Skills.COOKING) >= data.getBurningLvl()) ? false : isBurned(data, getPlayer())) {
			getPlayer().getItems().deleteItem(data.getRawItem(), 1);
			getPlayer().getItems().addItem(data.getBurntId(), 1);
			getPlayer().write(new SendMessagePacket("Oops.. you have accidentally burnt a " + ItemDefinition.forId(data.getRawItem()).getName().toLowerCase() + ""));
		} else {
			getPlayer().getItems().deleteItem(data.getRawItem(), 1);
			getPlayer().getItems().addItem(data.getProduct(), 1);
			getPlayer().write(new SendMessagePacket("You successfully cook the " + ItemDefinition.forId(data.getRawItem()).getName().toLowerCase() + "."));
			getPlayer().getSkills().addExperience(Skills.COOKING, data.getXp());
		}
	}
	
}
