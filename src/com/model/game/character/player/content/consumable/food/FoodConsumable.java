package com.model.game.character.player.content.consumable.food;

import com.model.game.character.Animation;
import com.model.game.character.combat.Combat;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.item.Item;
import com.model.utility.Utility;

import java.util.HashMap;
import java.util.Optional;

public class FoodConsumable {

	private final Player player;

	public FoodConsumable(Player player) {
		this.player = player;
	}

	public static enum FoodToEat {
		Easter_Egg(1961, 12, "Easter Egg", 0, "Food", false), Pumpkin(1959, 14,
				"Pumpkin", 0, "Food", false), Half_Jug_of_Wine(1989, 7,
				"Half Full Wine Jug", 1935, "Drink", false),
		// GROG(1915, 3, "Grog", 1919, "Drink", true),
		CUP_OF_TEA(1978, 2 + Utility.getRandom(1), "Cup of Tea", 1980, "Drink", true), CUP_OF_TEA2(
				712, 2 + Utility.getRandom(1), "Cup of Tea", 1980, "Drink", true),
		// WIZARD_MIND(1907, 0, "Wizard Mind Bomb", 0, "Drink", true),
		LEMON(2102, 2, "Lemon", 0, "Food", false), LIME(2120, 2, "Lime", 0,
				"Food", false), PINEAPPLE(2114, 2, "Pineapple", 0, "Food",
				false), CHOCOLATE_BAR(1973, 2, "Chocolate Bar", 0, "Food",
				false), Wine(1993, 11, "Wine", 1935, "Drink", false), MACKERAL(
				355, 6, "Mackeral", 0, "Food", false), MANTA(391, 22,
				"Manta Ray", 0, "Food", false), DARK_CRAB(11936, 24,
						"Dark crab", 0, "Food", false), SHARK(385, 20, "Shark", 0,
				"Food", false), ROCKTAIL(15272, 23, "Rocktail", 0,
						"Food", false), LOBSTER(379, 12, "Lobster", 0, "Food", false),
		KARAMBWAN(3144, 18, "Karambwan", 0, "Food", false),
		BEER(1917, 1, "Beer", 1919, "Drink", true), GREENMANS_ALE(1909, 1,
				"Greenman's Ale", 1919, "Drink", true), TROUT(333, 7, "Trout",
				0, "Food", false), SALMON(329, 9, "Salmon", 0, "Food", false), SWORDFISH(
				373, 14, "Swordfish", 0, "Food", false), TUNA(361, 10, "Tuna",
				0, "Food", false), MOONLIGHT_MEAD(2955, 4, "Moonlight mead",
				1919, "Drink", false), MONKFISH(7946, 16, "Monkfish", 0,
				"Food", false), SEA_TURTLE(397, 21, "Sea Turtle", 0, "Food",
				false), CABBAGE(1965, 1, "Cabbage", 0, "Food", false), SPINACH(
				1969, 2, "Spinach Roll", 0, "Food", false), CAKE(1891, 4,
				"Cake", 1893, "Food", false), CAKE2(1893, 4, "2/3 Cake", 1895,
				"Food", false), SLICE_OF_CAKE(1895, 4, "2/3 Cake", 0, "Food",
				false), BASS(365, 13, "Bass", 0, "Food", false), COD(339, 7,
				"Cod", 0, "Food", false), POTATO(1942, 1, "Potato", 0, "Food",
				false), BAKED_POTATO(6701, 4, "Baked Potato", 0, "Food", false), POTATO_WITH_CHEESE(
				6705, 16, "Potato with Cheese", 0, "Food", false), EGG_POTATO(
				7056, 16, "Egg Potato", 0, "Food", false), CHILLI_POTATO(7054,
				14, "Chilli Potato", 0, "Food", false), MUSHROOM_POTATO(7058,
				20, "Mushroom Potato", 0, "Food", false), TUNA_POTATO(7060, 22,
				"Tuna Potato", 0, "Food", false), SHRIMPS(315, 3, "Shrimps", 0,
				"Food", false), HERRING(347, 5, "Herring", 0, "Food", false), SARDINE(
				325, 4, "Sardine", 0, "Food", false), CHOCOLATE_CAKE(1897, 5,
				"Chocolate Cake", 1899, "Food", false), HALF_CHOCOLATE_CAKE(
				1899, 5, "2/3 Chocolate Cake", 1901, "Food", false), CHOCOLATE_SLICE(
				1901, 5, "Chocolate Slice", 0, "Food", false), ANCHOVIES(319,
				2, "Anchovies", 0, "Food", false), PLAIN_PIZZA(2289, 7,
				"Plain Pizza", 2291, "Food", false), HALF_PLAIN_PIZZA(2291, 7,
				"1/2 Plain pizza", 0, "Food", false), MEAT_PIZZA(2293, 8,
				"Meat Pizza", 2295, "Food", false), CHICKEN(2140, 3, "Chicken",
				0, "Food", false), MEAT(2142, 2, "Meat", 0, "Food", false), HALF_MEAT_PIZZA(
				2295, 8, "1/2 Meat Pizza", 0, "Food", false), ANCHOVY_PIZZA(
				2297, 9, "Anchovy Pizza", 2299, "Food", false), HALF_ANCHOVY_PIZZA(
				2299, 9, "1/2 Anchovy Pizza", 0, "Food", false), PINEAPPLE_PIZZA(
				2301, 11, "Pineapple Pizza", 2303, "Food", false), HALF_PINEAPPLE_PIZZA(
				2303, 11, "1/2 Pineapple Pizza", 0, "Food", false), BREAD(2309,
				5, "Bread", 0, "Food", false), APPLE_PIE(2323, 7, "Apple Pie",
				2335, "Food", false), HALF_APPLE_PIE(2335, 7, "Half Apple Pie",
				2313, "Food", false), REDBERRY_PIE(2325, 5, "Redberry Pie",
				2333, "Food", false), HALF_REDBERRY_PIE(2333, 5,
				"Half Redberry Pie", 2313, "Food", false), Ugthanki_kebab(1883,
				2, "Ugthanki kebab", 0, "Food", false),
		// Kebab(1971, 2, "kebab", 0, "food", false),
		SEAWEED(403, 4, "Edible Seaweed", 0, "Food", false), MEAT_PIE(2327, 6,
				"Meat Pie", 2331, "Food", false), HALF_MEAT_PIE(2331, 6,
				"Half Meat Pie", 2313, "Food", false), SUMMER_PIE(7218, 11,
				"Summer Pie", 7220, "Food", false), HALF_SUMMER_PIE(7220, 11,
				"Half Summer Pie", 2313, "Food", false), PIKE(351, 8, "Pike",
				0, "Food", false), POTATO_WITH_BUTTER(6703, 14,
				"Potato with Butter", 0, "Food", false), SLICED_BANANA(3162, 2,
				"Sliced Banana", 0, "Food", false), BANANA(1963, 2, "Banana",
				0, "Food", false), PEACH(6883, 8, "Peach", 0, "Food", false), ORANGE(
				2108, 2, "Orange", 0, "Food", false), PINEAPPLE_RINGS(2118, 2,
				"Pineapple Rings", 0, "Food", false), PINEAPPLE_CHUNKS(2116, 2,
				"Pineapple Chunks", 0, "Food", false), EASTER_EGG(7928, 1,
				"Easter Egg", 0, "Food", false), EASTER_EGG2(7929, 1,
				"Easter Egg", 0, "Food", false), EASTER_EGG3(7930, 1,
				"Easter Egg", 0, "Food", false), EASTER_EGG4(7931, 1,
				"Easter Egg", 0, "Food", false), EASTER_EGG5(7932, 1,
				"Easter Egg", 0, "Food", false), EASTER_EGG6(7933, 1,
				"Easter Egg", 0, "Food", false), PURPLE_SWEETS(10476, 9,
				"Purple Sweets", 0, "Food", false), POT_OF_CREAM(2130, 1,
				"Pot of cream", 0, "Food", false), FILED_RATION(7934, 9,
				"Field Ration", 0, "Food", false), STEW(2003, 11, "Stew", 1923,
				"Food", false), CURRY(2011, 19, "Curry", 1923, "Drink", false), BANDAGES(
				4049, 3, "Bandages", 0, "Food", false),
		        ANGLERFISH(13441, 22, "Anglerfish", 0, "Food", false);

		private int id;
		private int heal;
		private String name;
		private int replace;
		private String type;
		private boolean foodEffect;

		private FoodToEat(int id, int heal, String name, int replaceWith,
				String type, boolean foodEffect) {
			this.id = id;
			this.heal = heal;
			this.name = name;
			replace = replaceWith;
			this.type = type;
			this.foodEffect = foodEffect;
		}

		private boolean hasEffect() {
			return foodEffect;
		}

		private int getId() {
			return id;
		}

		private String getType() {
			return type;
		}

		private int getHeal() {
			return heal;
		}

		public String getName() {
			return name;
		}

		public int replaceWith() {
			return replace;
		}

		public static HashMap<Integer, FoodToEat> food = new HashMap<Integer, FoodToEat>();

		static {
			for (FoodToEat f : FoodToEat.values()) {
				food.put(f.getId(), f);
			}
		}
	}

	public int random(int r) {
		return Utility.getRandom(r);
	}

	public void eat(int id, int slot) {
		
		if (!player.getController().canEat(player)) {
			return;
		}
		
		if (player.isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
			return;
		}
		if (!player.lastSpear.elapsed(4000)) {
			player.getActionSender().sendMessage("You are stunned and can not eat!");
			return;
		}
		FoodToEat f = FoodToEat.food.get(id);
		boolean eatingKarambwam = lastEaten.isPresent() && !lastEaten.get().equals (FoodToEat.KARAMBWAN) && f.equals(FoodToEat.KARAMBWAN) || player.lastClickedItem >= 6685 && player.lastClickedItem <= 6691;
		
		if ((player.foodDelay.elapsed(1500) || eatingKarambwam) && player.getSkills().getLevel(Skills.HITPOINTS) > 0) {
			Combat.resetCombat(player);
			if (player.getCombatState().getAttackDelay() + 1 <= player.getCombatCooldownDelay() + 2) {
				player.getCombatState().increaseAttackDelay(1);
			}
			player.playAnimation(Animation.create(829));
			player.getActionSender().sendSound(317, 1, 2);
			player.getInventory().removeSlot(slot, 1, true);
			
			if (f.hasEffect()) {
				//TODO implement effects
			}
			
			if (f.replaceWith() > 0) {
				player.getInventory().add(new Item(f.replaceWith(), 1));
			}
			
			if (f.getType().equalsIgnoreCase("Food")) {
				player.getActionSender().sendMessage("You eat the " + f.getName() + ".");
			}
			
			if (player.getSkills().getLevel(Skills.HITPOINTS) < player.getMaximumHealth()) {
				player.getSkills().setLevel(Skills.HITPOINTS, player.getSkills().getLevel(Skills.HITPOINTS) + f.getHeal());
				player.getActionSender().sendMessage("It heals some health.");
			}
			
			if (player.getSkills().getLevel(Skills.HITPOINTS) > player.getMaximumHealth()) {
				player.getSkills().setLevel(Skills.HITPOINTS, player.getMaximumHealth());
			}
			
			if (!eatingKarambwam) {
				player.foodDelay.reset();
			}
			
			lastEaten = Optional.of(f);
			
		}
	}

	public boolean isFood(int id) {
		return FoodToEat.food.containsKey(id);
	}
	
	/**
	 * The last food the player has consumed
	 */
	private Optional<FoodToEat> lastEaten = Optional.empty();
}