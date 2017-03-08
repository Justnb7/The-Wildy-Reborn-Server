package com.model.game.character.player.skill.herblore;

import com.model.game.item.Item;

/**
 * The potion data, for the herblore skill.
 * @author Unknown
 *
 */
public enum Potion {
	
	ATTACK_POTION(new Item(121), 3, 25, new Item(249), new Item(221)),
	ANTIPOISON(new Item(175), 5, 37, new Item(251), new Item(235)),
	STRENGTH_POTION(new Item(115), 12, 50, new Item(253), new Item(1526)),
	RESTORE_POTION(new Item(127), 22, 62, new Item(255), new Item(223)),
	GUTHIX_BALANCE(new Item(7662), 30, 75, new Item(257), new Item(223), new Item(1550), new Item(7650)),
	ENERGY_POTION(new Item(3010), 26, 80, new Item(255), new Item(1975)),
	COMBAT_POTION(new Item(9741), 36, 84, new Item(255), new Item(9736)),
	PRAYER_POTION(new Item(139), 38, 87, new Item(257), new Item(231)),
	SUPER_ATTACK(new Item(145), 45, 100, new Item(259), new Item(221)),
	SUPER_ANTIPOISON(new Item(181), 48, 106, new Item(259), new Item(235)),
	SUPER_ENERGY(new Item(3018), 52, 117, new Item(261), new Item(2970)),
	SUPER_STRENGTH(new Item(157), 55, 125, new Item(263), new Item(225)),
	SUPER_RESTORE(new Item(3026), 63, 142, new Item(3000), new Item(223)),
	SUPER_DEFENCE(new Item(163), 66, 150, new Item(265), new Item(239)),
	ANTIDOTE_PLUS(new Item(5945), 68, 155, new Item(2998), new Item(6049)),
	ANTIFIRE(new Item(2454), 69, 157, new Item(2481), new Item(243)),
	RANGING(new Item(169), 72, 162, new Item(267), new Item(245)),
	MAGIC(new Item(3042), 76, 172, new Item(2481), new Item(3138)),
	ANTIDOTE_PLUS_PLUS(new Item(5954), 79, 178, new Item(259)),
	SARADOMIN_BREW(new Item(6687), 81, 180, new Item(2998), new Item(6693)),
	SUPER_COMBAT_POTION(new Item(12697), 90, 190, new Item(269), new Item(2436), new Item(2440), new Item(2442)),
	ANTI_VENOM(new Item(12907), 87, 120, new Item(5954), new Item(12934, 15)),
	ANTI_VENOM_PLUS(new Item(12915), 94, 125, new Item(12907), new Item(269)),
	SANFEW_SERUM(new Item(10919), 99, 150, new Item(3000), new Item(223), new Item(235), new Item(1526), new Item(10937));
	
	/**
	 * The primary ingredient required
	 */
	private final Item primary;
	
	/**
	 * An array of {@link Item} objects that represent the ingredients
	 */
	private final Item[] ingredients;
	
	/**
	 * The item received from combining the ingredients
	 */
	private final Item result;
	
	/**
	 * The level required to make this potion
	 */
	private final int level;
	
	/**
	 * The experience gained from making this potion
	 */
	private final int experience;
	
	/**
	 * Creates a new in-game potion that will be used in herblore
	 * @param result		the result from combining ingredients
	 * @param level			the level required
	 * @param experience	the experience
	 * @param ingredients	the ingredients to make the result
	 */
	private Potion(Item result, int level, int experience, Item primary, Item... ingredients) {
		this.result = result;
		this.level = level;
		this.experience = experience;
		this.primary = primary;
		this.ingredients = ingredients;
	}
	
	/**
	 * The result from combining the ingredients
	 * @return	the result
	 */
	public Item getResult() {
		return result;
	}
	
	/**
	 * The level required to combine the ingredients
	 * @return	the level required
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * The total amount of experience gained in the herblore skill
	 * @return	the experience gained
	 */
	public int getExperience() {
		return experience;
	}
	
	/**
	 * An array of {@link Item} objects that represent the ingredients required
	 * to create this potion.
	 * @return	the ingredients required
	 */
	public Item[] getIngredients() {
		return ingredients;
	}
	
	/**
	 * The primary ingredient required for the potion
	 * @return	the primary ingredient
	 */
	public Item getPrimary() {
		return primary;
	}

}
