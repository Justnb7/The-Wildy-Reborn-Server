package com.venenatis.game.content.skills.smithing;

import com.venenatis.game.content.SkillCapePerks;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class SmithingTask extends Task {
	
	private static int SMITHING_MODIFIER = 5;
	
	public static void start(Player p, int item, int amount, int interfaceId, int slot) {
		String check = Item.getDefinition(item).getName().substring(0, 3);

		int bar = -1;

		int make = 1;

		if (Item.getDefinition(item).isStackable()) {
			make = 15;
		}

		switch (check) {
		case "Bro":
			bar = SmithingConstants.BARS[0];
			break;
		case "Iro":
			bar = SmithingConstants.BARS[1];
			break;
		case "Ste":
			bar = SmithingConstants.BARS[2];
			break;
		case "Mit":
			bar = SmithingConstants.BARS[3];
			break;
		case "Ada":
			bar = SmithingConstants.BARS[4];
			break;
		case "Run":
			bar = SmithingConstants.BARS[5];
			break;
		}

		World.getWorld().schedule(new SmithingTask(p, new Item(item, make), new Item(bar, SmithingConstants.getBarAmount(interfaceId, slot)), amount));
	}

	private final Player player;
	private final Item smith;
	private final Item bar;
	private final int amount;

	private int loop = 0;

	public SmithingTask(Player player, Item smith, Item bar, int amount) {
		super(player, 2, true, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE);
		this.player = player;
		this.smith = smith;
		this.bar = bar;
		this.amount = amount;

		int lvl = SmithingConstants.getLevel(smith.getId());

		if (player.getSkills().getLevel(Skills.SMITHING) < lvl) {
			player.getActionSender().sendMessage("You need a Smithing level of " + lvl + " to make that.");
			stop();
		} else if (!player.getInventory().hasItemAmount(new Item(bar))) {
			player.getActionSender().sendMessage("You do not have enough bars to make that.");
			stop();
		} else if (!player.getInventory().contains(2347)) {
			player.getActionSender().sendMessage("You don't have a hammer to smith with.");
			stop();
		} else {
			player.getActionSender().removeAllInterfaces();
		}
	}

	@Override
	public void execute() {
		if (!hasRequirements()) {
			stop();
			return;
		}

		player.getSkills().addExperience(Skills.SMITHING, getExperience() * SMITHING_MODIFIER);
		/**
		 * Chance of saving a bar while wearing herblore or max cape
		 */
		if (SkillCapePerks.SMITHING.isWearing(player) || SkillCapePerks.isWearingMaxCape(player)) {
			if (Utility.random(4) == 2) {
				player.getActionSender().sendMessage("You manage to save a bar.");
			} else {
				player.getInventory().remove(new Item(bar), false);
			}
		} else {
			player.getInventory().remove(new Item(bar), false);
		}
		player.getInventory().add(new Item(smith), true);

		player.playAnimation(Animation.create(898));
		player.getActionSender().sendSound(468, 10, 10);

		if (smith.getAmount() == 1)
			player.getActionSender().sendMessage("You make " + Utility.getAOrAn(smith.getDefinition().getName()) + " " + smith.getDefinition().getName() + ".");
		else {
			player.getActionSender().sendMessage("You make " + smith.getAmount() + " " + smith.getDefinition().getName() + (!smith.getDefinition().getName().endsWith("s") ? "s" : "") + ".");
		}

		if (++loop == amount)
			stop();
	}

	public double getExperience() {
		switch (bar.getId()) {
		case 2349:
			return 12.5D * bar.getAmount();
		case 2351:
			return 25 * bar.getAmount();
		case 2353:
			return 37.5D * bar.getAmount();
		case 2359:
			return 50 * bar.getAmount();
		case 2361:
			return 62.5D * bar.getAmount();
		case 2363:
			return 75 * bar.getAmount();
		case 2350:
		case 2352:
		case 2354:
		case 2355:
		case 2356:
		case 2357:
		case 2358:
		case 2360:
		case 2362:
		}
		return 0.0D;
	}

	public boolean hasRequirements() {
		if (!player.getInventory().hasItemAmount(new Item(bar))) {
			player.getActionSender().sendMessage("You have run out of " + bar.getDefinition().getName() + "s.");
			return false;
		}

		if (!player.getInventory().contains(2347)) {
			player.getActionSender().sendMessage("You don't have a hammer to smith with.");
			return false;
		}

		return true;
	}

	@Override
	public void onStop() {
	}
}