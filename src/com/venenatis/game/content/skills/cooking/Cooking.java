package com.venenatis.game.content.skills.cooking;

import com.venenatis.game.cache.definitions.AnyRevObjectDefinition;
import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.content.skills.SkillTask;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Stackable;
import com.venenatis.game.task.Walkable;
import com.venenatis.game.util.Utility;


/**
 * The cooking skill
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 * @version 1.0 @date 06-03-2017
 */
public class Cooking extends SkillTask {
	
	/**
	 * Pulls the cookable data
	 */
	private final Cookables cookables;

	/**
	 * The task
	 * @param player
	 *         The player cooking the cookable
	 * @param delay
	 *         The timer of the task, a.k.a the delay
	 * @param cookables
	 *         The cookable data
	 */
	public Cooking(Player player, int delay, Cookables data) {
		super(player, 4, Walkable.NON_WALKABLE, Stackable.NON_STACKABLE, false);
		this.cookables = data;
	}

	/**
	 * If we passed all the checks we can goahead and start the task.
	 * @param player
	 *         The player cooking
	 * @param id
	 *         The item being cooked
	 * @param object
	 *         The object were cooking on
	 */
	public static void attemptCooking(Player player, int id, int object) {

		Cookables data = Cookables.forId(id);

		if (!meetsRequirements(player, data, object)) {
			return;
		}
		player.getActionSender().removeAllInterfaces();
		player.getWalkingQueue().reset();
		player.setSkillTask(new Cooking(player, id, data));
	}

	/**
	 * Have we passed all requirements?
	 * @param player
	 *         The player to check
	 * @param cookable
	 *         The cookable item from the [{@code Cookables}
	 * @param object
	 *         The objectId
	 * @return
	 */
	private static boolean meetsRequirements(Player player, Cookables cookable, int object) {
		AnyRevObjectDefinition objectDef = AnyRevObjectDefinition.get(object);
		if (cookable == null) {
			return false;
		}
		if (player.getSkills().getLevel(Skills.COOKING) < cookable.getLvl()) {
			player.getActionSender().sendMessage("You need a cooking level of " + cookable.getLvl() + " to cook this food.");
			return false;
		}
		if (!player.getInventory().contains(cookable.getRawItem(), 1)) {
			player.getActionSender().sendMessage("You have ran out of food to cook");
			return false;
		}
		if (cookable.isStoveOrRangeOnly()) {
			if (objectDef.getName().contains("stove") || objectDef.getName().contains("range") || objectDef.getName().contains("Cooking range")) {
				return true;
			} else {
				player.getActionSender().sendMessage("You may only cook this on a stove or cooking range.");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Burning the food based on equipment and levels
	 * @param cook
	 *         The item we're we cooking
	 * @param player
	 *         The player cooking the cookable
	 * @return
	 */
	private boolean burned(Cookables cook, Player player) {
		int level = player.getSkills().getLevel(Skills.COOKING);
		if (player.getEquipment().get(EquipmentConstants.GLOVES_SLOT).getId() == 775) {
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
		if (!getPlayer().getInventory().contains(cookables.getRawItem(), 1)) {
			getPlayer().getActionSender().sendMessage("You have run out of food to cook.");
			stop();
			return;
		}
		getPlayer().getActionSender().sendMessage("You attempt to cook the " + ItemDefinition.get(cookables.getProduct()).getName().toLowerCase() + ".");
		getPlayer().playAnimation(Animation.create(896));
		
		if ((getPlayer().getSkills().getLevel(Skills.COOKING) >= cookables.getBurningLvl()) ? false : burned(cookables, getPlayer())) {
			getPlayer().getInventory().remove(new Item(cookables.getRawItem(), 1));
			getPlayer().getInventory().add(new Item(cookables.getBurntId(), 1));
			getPlayer().getActionSender().sendMessage("Oops.. you have accidentally burnt a " + ItemDefinition.get(cookables.getRawItem()).getName().toLowerCase() + "");
		} else {
			getPlayer().getInventory().remove(new Item(cookables.getRawItem(), 1));
			getPlayer().getInventory().add(new Item(cookables.getProduct(), 1));
			getPlayer().getActionSender().sendMessage("You successfully cook the " + ItemDefinition.get(cookables.getRawItem()).getName().toLowerCase() + ".");
			getPlayer().getSkills().addExperience(Skills.COOKING, cookables.getXp());
		}
	}
	
}
