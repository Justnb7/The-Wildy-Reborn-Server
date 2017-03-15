package com.model.game.character.player.skill.crafting;

import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.skill.SkillTask;
import com.model.game.item.Item;
import com.model.task.Stackable;
import com.model.task.Walkable;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * Creating boltips, a part of the crafting skill which grants fletching exp.
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class BoltCrafting extends SkillTask {
	
	/**
	 * The crafting animation
	 */
	private Animation ANIMATION = Animation.create(4462);
	
	/**
	 * The type of gem we are cutting.
	 */
	private BoltTip tip;
	
	/**
	 * The chisel item
	 */
	public static final Item CHISEL = new Item(1755, 1);

	/**
	 * Creates an unwalkable task for the player
	 *
	 * @param player
	 *            The player cutting gems
	 * @param tip
	 *            The data for the gem to be cut
	 */
	public BoltCrafting(Player player, BoltTip tip) {
		super(player, 1, Walkable.NON_WALKABLE, Stackable.NON_STACKABLE, false);
		this.tip = tip;
	}
	
	/**
	 * Attempts to create bolt tips
	 *
	 * @param player
	 *            The player attempting to create a bolt tips
	 * @param useWith
	 *            The first item used
	 * @param itemUsed
	 *            The second item used
	 * @return
	 */
	public static boolean attemptBoltTipCreation(Player player, int useWith, int itemUsed) {
		int gemId = useWith == CHISEL.getId() ? itemUsed : useWith;

		BoltTip tip = BoltTip.forId(gemId);
		if (!meetsRequirements(player, tip)) {
			return false;
		}
		player.setSkillTask(new BoltCrafting(player, tip));
		return true;
	}

	/**
	 * Meets the requirements to start cutting gems
	 *
	 * @param player
	 *            The player cutting the gem
	 * @param data
	 *            The data for the gem being cut
	 * @return The player can cut the gem
	 */
	private static boolean meetsRequirements(Player player, BoltTip tip) {
		if (tip == null) {
			return false;
		}
		if (player.getSkills().getLevel(Skills.FLETCHING) < tip.getLevelReq()) {
			player.message("You need a Fletching level of " + tip.getLevelReq() + " to fletch this.");
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
		if (!getPlayer().getItems().playerHasItem(tip.getGem(), 1)) {
			getPlayer().message("You have run out of gems.");
			stop();
			return;
		}
		getPlayer().playAnimation(ANIMATION);
		getPlayer().getItems().deleteItem(tip.getGem(), 1);
		getPlayer().getItems().addItemtoInventory(tip.getReward());
		getPlayer().getSkills().addExperience(Skills.CRAFTING, tip.getExperience());
		getPlayer().message("You succesfully craft " + tip.getReward().getAmount() + " " + ItemDefinition.forId(tip.getReward().getId()).getName() + ".");
	}

}
