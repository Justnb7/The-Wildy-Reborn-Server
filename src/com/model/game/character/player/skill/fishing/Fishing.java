package com.model.game.character.player.skill.fishing;

import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.skill.SkillTask;
import com.model.game.item.Item;
import com.model.task.Stackable;
import com.model.task.Walkable;
import com.model.utility.Utility;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * The fishing skill
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class Fishing extends SkillTask {

	/**
	 * The data for the fish to be fished.
	 */
	private final FishingSpot data;

	/**
	 * Creates an unwalkable task for the player
	 *
	 * @param player
	 *            The player fishing for fish
	 * @param data
	 *            The data for the fish to be fished
	 */
	public Fishing(Player player, FishingSpot data, int timer) {
		super(player, timer, Walkable.NON_WALKABLE, Stackable.NON_STACKABLE, false);
		this.data = data;
	}

	/**
	 * Attempts to fish
	 *
	 * @param player
	 *            The player attempting to fish
	 * @return
	 */
	public static boolean attemptFishing(Player player, Npc npc, int optionId) {
		int spotId = npc.npcId;
		
		FishingSpot data = FishingSpot.forId(spotId, optionId);
		
		
		if (!meetsRequirements(player, data)) {
			return false;
		}

		player.playAnimation(Animation.create(data.getAnimationId()));
		player.getMovementHandler().stopMovement();
		player.setAttribute("fishing", true);
		player.message("You begin fishing...");
		player.setSkillTask(new Fishing(player, data, data.getTimer()));
		player.playAnimation(Animation.create(data.getAnimationId()));
		return true;
	}
	

	/**
	 * Meets the requirements to start fishing
	 *
	 * @param player
	 *            The player fishing
	 * @param data
	 *            The data for fishing
	 * @return The player can start to fish the spot
	 */
	private static boolean meetsRequirements(Player player, FishingSpot data) {
		if (data == null) {
			return false;
		}
		if (player.getSkills().getLevel(Skills.FISHING) < data.getLevelRequired()) {
			player.message("You need a fishing level of " + data.getLevelRequired() + " to fish here.");
			return false;
		}
		if (data.isBaitRequired() && !player.getItems().playerHasItem(313)) {
			player.message("You need some fishing bait to fish here.");
			return false;
		}
		if (!player.getItems().playerHasItem(data.getToolId(), 1)) {
			player.message("You need a " + ItemDefinition.forId(data.getToolId()).getName() + " to fish here.");
			return false;
		}
		return true;
	}
	
	//Where would i apply -1 since its all one task

	@Override
	public void execute() {
		if (getPlayer() == null || !getPlayer().isActive() || !SkillTask.noInventorySpace(getPlayer(), "fishing") || !(Boolean) getPlayer().getAttribute("fishing")) {
			getPlayer().playAnimation(Animation.create(-1));// i did it here but doesn't work
			getPlayer().setAttribute("fishing", false);
			stop();
			return;
		}
		
		Item fish = new Item(data.getFishId(), 1);
		Item secondFish = new Item(data.getSecondFishId(), 1);
		if (data.isSecondFishAvailable() && Utility.getRandom(3) == 0) {
			getPlayer().getItems().addItem(secondFish);
			getPlayer().message("You manage to catch some " + secondFish.getDefinition().getName().toLowerCase() + ".");
		} else {
			getPlayer().getItems().addItem(fish);
			getPlayer().message("You manage to catch some " + fish.getDefinition().getName().toLowerCase() + ".");
		}
		//heronPet(getPlayer(), data);
		if (data.isBaitRequired() && getPlayer().getItems().playerHasItem(313) && Utility.getRandom(2) == 0) {
			getPlayer().getItems().deleteItem(313, 1);
		}
		getPlayer().getSkills().addExperience(Skills.FISHING, data.getExperience());
	}
	
	void heronPet(Player player, FishingSpot spot) {
		int random = Utility.random(spot.getPetChance());
		if (random == 0) {
			//TODO spawn pet
			World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getName() + " has just received the Heron pet.", false);
		}
	}
}