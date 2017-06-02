package com.model.game.character.player.skill.herblore;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import com.model.game.Constants;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.definitions.ItemDefinition;
import com.model.game.item.Item;

/**
 * The herblore skill.
 * @author Unknown
 *
 */
public class Herblore {
	
	/**
	 * A {@link Set} of all {@link Herb} elements from it's respective enumeration.
	 */
	private static final Set<Herb> HERBS = Collections.unmodifiableSet(EnumSet.allOf(Herb.class));
	
	/**
	 * A {@link Set} of all {@link Potion} elements from it's respective enumeration.
	 */
	private static final Set<Potion> POTIONS = Collections.unmodifiableSet(EnumSet.allOf(Potion.class));
	
	/**
	 * The player that will be operating this skill
	 */
	private final Player player;
	
	/**
	 * A class for managing herblore operation
	 * @param player	the player
	 */
	public Herblore(Player player) {
		this.player = player;
	}
	
	/**
	 * Cleans a single her
	 * @param itemId	the herb attempting to be cleaned
	 */
	public void clean(int itemId) {
		Optional<Herb> herb = HERBS.stream().filter(h -> h.getGrimy() == itemId).findFirst();
		herb.ifPresent(h -> {
			if (!player.getInventory().contains(h.getGrimy())) {
				player.getActionSender().sendMessage("You need the grimy herb to do this.");
				return;
			}
			if (player.getSkills().getLevel(Skills.HERBLORE) < h.getLevel()) {
				player.getActionSender().sendMessage("You need a herblore level of " + h.getLevel() + " to clean this grimy herb.");
				return;
			}
			ItemDefinition definition = ItemDefinition.get(h.getClean());
			player.getSkills().addExperience(Skills.HERBLORE, h.getExperience() * Constants.SKILL_MODIFIER);
			player.getInventory().remove(new Item(h.getGrimy(), 1));
			player.getInventory().add(new Item(h.getClean(), 1));
			player.getActionSender().sendMessage("You identify the herb as " + definition.getName() + ".");
		});
	}
	
	public void mix(int primary) {
		Optional<Potion> potion = POTIONS.stream().filter(p -> p.getPrimary().getId() == primary && containsSecondaries(p)).findFirst();
		potion.ifPresent(p -> {
			ItemDefinition definition = ItemDefinition.get(p.getResult().getId());
			if (player.getSkills().getLevel(Skills.HERBLORE) < p.getLevel()) {
				player.getActionSender().sendMessage("You need a herblore level of " + p.getLevel() + " to make " + definition.getName() + ".");
				return;
			}
			if (!player.getInventory().contains(227)) {
				player.getActionSender().sendMessage("You need a regular vial of water to do this.");
				return;
			}
			Arrays.asList(p.getIngredients()).stream().forEach(ing -> player.getInventory().remove(new Item(ing.getId(), ing.getAmount())));
			player.getInventory().remove(new Item(227, 1));
			player.getInventory().remove(new Item(p.getPrimary().getId(), p.getPrimary().getAmount()));
			player.getInventory().add(new Item(p.getResult().getId(), p.getResult().getAmount()));
			player.getSkills().addExperience(Skills.HERBLORE, p.getExperience() * Constants.SKILL_MODIFIER);
			player.getActionSender().sendMessage("You combine all of the ingredients and make a " + definition.getName() + ".");
			//Achievements.increase(player, AchievementType.POTIONS, 1);
		});
	}
	
	/**
	 * Determines if the player has all of the ingredients required for the potion.
	 * @param potion	the potion we're determining this for
	 * @return			{@code true} if we have all of the ingredients, otherwise {@code false}
	 */
	private boolean containsSecondaries(Potion potion) {
		int required = potion.getIngredients().length;
		for (Item ingredient : potion.getIngredients()) {
			if (player.getInventory().contains(ingredient.getId(), ingredient.getAmount())) {
				required--;
			}
		}
		return required == 0;
	}
}
