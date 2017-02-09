package com.model.game.character.player.skill.fishing;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendClearScreen;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.SkillHandler;
import com.model.game.character.player.skill.SkillTask;
import com.model.task.ScheduledTask;
import com.model.task.Stackable;
import com.model.task.Walkable;
import com.model.utility.Utility;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * 
 * @author Patrick van Elderen
 * @date 31-3-2016 10:15 PM
 *
 */
public class Fishing extends SkillTask {

	/**
	 * The data for the fish to be fished.
	 */
	private final FishableSpot data;

	/**
	 * Creates an unwalkable task for the player
	 *
	 * @param player
	 *            The player fishing for fish
	 * @param data
	 *            The data for the fish to be fished
	 */
	public Fishing(Player player, FishableSpot data, int timer) {
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
	public static boolean attemptFishing(Player player, int npcId, int optionId) {
		int spotId = npcId;
		
		FishableSpot data = FishableSpot.forId(spotId, optionId);
		
		
		if (!meetsRequirements(player, data)) {
			return false;
		}
		player.write(new SendClearScreen());
		continueAnimation(player, npcId, optionId);
		player.getMovementHandler().stopMovement();
		player.write(new SendMessagePacket("You begin fishing..."));
		player.setSkillTask(new Fishing(player, data, data.getTimer()));
		player.playAnimation(Animation.create(data.getAnimationId()));
		return true;
	}
	
	private static void continueAnimation(Player player, int npcId, int optionId) {
		FishableSpot data = FishableSpot.forId(npcId, optionId);
		
		Server.getTaskScheduler().schedule(new ScheduledTask(player, 4, Walkable.NON_WALKABLE, Stackable.STACKABLE) {
			@Override
			public void execute() {
				if(!player.isActive()) {
						this.stop();
				}
				if(!SkillHandler.noInventorySpace(player, "FISHING")) {
					this.stop();
				}
				player.playAnimation(Animation.create(data.getAnimationId()));
			}
		});
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
	private static boolean meetsRequirements(Player player, FishableSpot data) {
		if (data == null) {
			return false;
		}
		if (player.getSkills().getLevel(Skills.FISHING) < data.getLevelRequired()) {
			player.write(new SendMessagePacket("You need a fishing level of " + data.getLevelRequired() + " to fish here."));
			return false;
		}
		if (data.isBaitRequired() && !player.getItems().playerHasItem(313)) {
			player.write(new SendMessagePacket("You need some fishing bait to fish here."));
			return false;
		}
		if (!player.getItems().playerHasItem(data.getToolId(), 1)) {
			player.write(new SendMessagePacket("You need a " + ItemDefinition.forId(data.getToolId()).getName() + " to fish here."));
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
		/**
		 * If the player has no inventory space stop the task
		 */
		if (!SkillHandler.noInventorySpace(getPlayer(), "fishing")) {
			stop();
			return;
		}
		if (!getPlayer().getItems().playerHasItem(data.getToolId(), 1)) {
			getPlayer().write(new SendMessagePacket("You need a " + ItemDefinition.forId(data.getToolId()).getName() + " to fish here."));
			stop();
			return;
		}

		if (data.isSecondFishAvailable() && Utility.getRandom(3) == 0) {
			getPlayer().getItems().addItem(data.getSecondFishId(), 1);
		} else {
			getPlayer().getItems().addItem(data.getFishId(), 1);
		}
		if (data.isBaitRequired() && getPlayer().getItems().playerHasItem(313) && Utility.getRandom(2) == 0) {
			getPlayer().getItems().deleteItem(313, 1);
		}
		getPlayer().getSkills().addExperience(Skills.FISHING, data.getExperience());
	}
}