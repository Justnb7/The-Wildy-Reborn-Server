package com.model.game.character.player.skill.crafting;

import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.skill.SkillTask;
import com.model.game.item.Item;
import com.model.task.Stackable;
import com.model.task.Walkable;

/**
 * Cutting gems, a part of the crafting skill
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class GemCutting extends SkillTask {

	/**
	 * The type of gem we are cutting.
	 */
	private Gems gem;
	
	public static final Item CHISEL = new Item(1755, 1);
	
	/**
	 * Creates an unwalkable task for the player
	 *
	 * @param player
	 *            The player cutting gems
	 * @param gem
	 *            The data for the gem to be cut
	 */
	public GemCutting(Player player, Gems gem) {
		super(player, 4, Walkable.NON_WALKABLE, Stackable.NON_STACKABLE, false);
		this.gem = gem;
	}
	
	/**
	 * Attempts to cut gems in your inventory
	 *
	 * @param player
	 *            The player attempting to cut a gem
	 * @param useWith
	 *            The first item used
	 * @param itemUsed
	 *            The second item used
	 * @return
	 */
	public static boolean attemptGemCutting(Player player, int useWith, int itemUsed) {
		int gemId = useWith == CHISEL.getId() ? itemUsed : useWith;

		Gems gem = Gems.forId(gemId);
		if (!meetsRequirements(player, gem)) {
			return false;
		}
		player.playAnimation(gem.getAnimation());
		player.setSkillTask(new GemCutting(player, gem));
		return true;
	}

	/**
	 * Meets the requirements to start cutting gems
	 *
	 * @param player
	 *            The player cutting the gem
	 * @param gem
	 *            The data for the gem being cut
	 * @return The player can cut the gem
	 */
	private static boolean meetsRequirements(Player player, Gems gem) {
		if (gem == null) {
			return false;
		}
		if (player.getSkills().getLevel(Skills.CRAFTING) < gem.getRequiredLevel()) {
			player.message("You need a crafting level of " + gem.getRequiredLevel() + " to cut this uncut.");
			return false;
		}
		return true;
	}

	@Override
	public void execute() {
		if (getPlayer() == null || !getPlayer().isActive()) {
			stop();
			return;
		}
		if (!getPlayer().getItems().playerHasItem(CHISEL)) {
			getPlayer().message("You do not have a chisel.");
			stop();
			return;
		}
		if (!getPlayer().getItems().playerHasItem(gem.getUncutVersion(), 1)) {
			getPlayer().message("You have run out of uncuts.");
			stop();
			return;
		}
		getPlayer().playAnimation(gem.getAnimation());
		getPlayer().getItems().deleteItem(gem.getUncutVersion(), 1);
		getPlayer().getItems().addItem(gem.cutReward(), 1);
		getPlayer().getSkills().addExperience(Skills.CRAFTING, gem.getExperience());
	}

}
