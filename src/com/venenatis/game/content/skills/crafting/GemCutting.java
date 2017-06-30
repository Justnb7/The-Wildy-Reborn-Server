package com.venenatis.game.content.skills.crafting;

import com.venenatis.game.content.skills.SkillTask;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Walkable;

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
		super(player, 4, Walkable.ON_MOVE, StackType.NEVER_STACK, false);
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
			player.getActionSender().sendMessage("You need a crafting level of " + gem.getRequiredLevel() + " to cut this uncut.");
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
		if (!getPlayer().getInventory().contains(CHISEL)) {
			getPlayer().getActionSender().sendMessage("You do not have a chisel.");
			stop();
			return;
		}
		if (!getPlayer().getInventory().contains(gem.getUncutVersion(), 1)) {
			getPlayer().getActionSender().sendMessage("You have run out of uncuts.");
			stop();
			return;
		}
		getPlayer().playAnimation(gem.getAnimation());
		getPlayer().getInventory().remove(new Item(gem.getUncutVersion(), 1));
		getPlayer().getInventory().add(new Item(gem.cutReward(), 1));
		getPlayer().getSkills().addExperience(Skills.CRAFTING, gem.getExperience());
	}

}
