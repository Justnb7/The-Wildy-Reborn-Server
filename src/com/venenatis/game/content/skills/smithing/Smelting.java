package com.venenatis.game.content.skills.smithing;

import java.util.Random;

import com.venenatis.game.location.Area;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;

public class Smelting extends Task {
	
	/**
	 * The random number generator
	 */
	private Random random = new Random();

	private final Player player;
	private final SmeltingData data;
	private final int amount;
	private int smelted = 0;
	private final String name;
	public static final Animation SMELTING_ANIMATION = new Animation(899);
	public static final String A = "You smelt ";
	public static final String B = ".";
	public static final String IRON_FAILURE = "You fail to refine the iron ore.";

	public Smelting(Player player, int amount, SmeltingData data) {
		super(player, 2, true, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE);
		this.player = player;
		this.data = data;
		this.amount = amount;
		name = data.getResult().getDefinition().getName();

		player.getActionSender().removeAllInterfaces();

		if (!canSmelt(player, data, false)) {
			stop();
		}
	}

	public boolean canSmelt(Player player, SmeltingData data, boolean taskRunning) {
		if (player.getSkills().getLevel(Skills.SMITHING) < data.getLevelRequired()) {
			player.getActionSender().sendMessage("You need a Smithing level of " + data.getLevelRequired() + " to smelt this bar.");
			return false;
		}

		for (Item i : data.getRequiredOres()) {
			if (!player.getInventory().hasItemAmount(i.getId(), i.getAmount())) {
				player.getActionSender().sendMessage(taskRunning ? "You have run out of " + i.getDefinition().getName() + "." : "You don't not have any " + i.getDefinition().getName().toLowerCase() + " to smelt.");
				return false;
			}
		}

		return true;
	}

	@Override
	public void execute() {
		if (!canSmelt(player, data, true)) {
			stop();
			return;
		}

		player.playAnimation(SMELTING_ANIMATION);

		player.getInventory().remove(data.getRequiredOres());

		if (data == SmeltingData.IRON_BAR) {
			if (Skills.isSuccess(player, 13, data.getLevelRequired())) {
				player.getInventory().add(data.getResult(), false);
				player.getActionSender().sendMessage("You smelt " + Utility.getAOrAn(name) + " " + name + ".");
			} else {
				player.getActionSender().sendMessage("You fail to refine the iron ore.");
			}
		} else {
			player.getInventory().add(data.getResult(), false);
			player.getActionSender().sendMessage("You smelt " + Utility.getAOrAn(name) + " " + name + ".");
		}

		player.getInventory().refresh();
		
		if(Area.inWilderness(player) && random.nextInt(10) < 7) {
			player.getInventory().addOrCreateGroundItem(player, new Item(13307, Utility.random(1, 5)));
		}

		player.getSkills().addExperience(Skills.SMITHING, data.getExp());

		if (++smelted == amount)
			stop();
	}

	public boolean isSuccess(Player player, SmeltingData data) {
		return Skills.isSuccess(player, 13, data.levelRequired);
	}

	@Override
	public void onStop() {
	}
}