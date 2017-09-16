package com.venenatis.game.content.skills.fishing;

import java.util.Random;

import com.venenatis.game.content.SkillCapePerks;
import com.venenatis.game.content.achievements.AchievementHandler;
import com.venenatis.game.content.achievements.AchievementList;
import com.venenatis.game.location.Area;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.npc.pet.Pets;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.Task.BreakType;
import com.venenatis.game.task.Task.StackType;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class Fishing {
	
	/**
	 * The random number generator
	 */
	private Random random = new Random();

	private boolean canFish(Player p, FishableData.Fishable fish, boolean message) {
		if (p.getSkills().getLevel(Skills.FISHING) < fish.getRequiredLevel()) {
			if (message) {
				p.getActionSender().sendMessage("You need a fishing level of " + fish.getRequiredLevel() + " to fish here.");
			}
			return false;
		}

		if (!hasFishingItems(p, fish, message)) {
			return false;
		}

		return true;
	}

	private boolean hasFishingItems(Player player, FishableData.Fishable fish, boolean message) {
		Item tool = new Item(fish.getToolId());
		Item bait = new Item(fish.getBaitRequired());

		if (tool.getId() == 311) {
			if (!player.getInventory().contains(tool)) {
				Item weapon = player.getEquipment().getItems()[3];

				if ((weapon != null) && (weapon.getId() == 10129)) {
					return true;
				}
				if (message) {
					player.getActionSender().sendMessage("You don't have the right tool to fish here.");
				}
				return false;
			}
		} else if ((!player.getInventory().contains(tool)) && (message)) {
			String name = tool.getName();
			player.getActionSender().sendMessage("You need " + Utility.getAOrAn(name) + " " + name + " to fish here.");
			return false;
		}

		if ((bait.getId() > -1) && (!player.getInventory().contains(bait))) {
			String name = bait.getName();
			if (message) {
				player.getActionSender().sendMessage("You need " + Utility.getAOrAn(name) + " " + name + " to fish here.");
			}
			return false;
		}

		return true;
	}

	private final Player player;

	private FishableData.Fishable[] fishing = null;

	private ToolData.Tools tool = null;

	public Fishing(Player player) {
		this.player = player;
	}

	public boolean clickNpc(Player player, NPC npc, int option) {
		if (FishingSpot.forId(npc.getId()) == null) {
			return false;
		}

		FishingSpot spot = FishingSpot.forId(npc.getId());

		FishableData.Fishable[] f = new FishableData.Fishable[3];
		int amount = 0;
		FishableData.Fishable[] fish;
		switch (option) {
		case 1:
			fish = spot.getOption_1();
			for (int i = 0; i < fish.length; i++) {
				if (canFish(player, fish[i], i == 0)) {
					f[i] = fish[i];
					amount++;
				}
			}
			break;
		case 2:
			fish = spot.getOption_2();
			for (int i = 0; i < fish.length; i++) {
				if (canFish(player, fish[i], i == 0)) {
					f[i] = fish[i];
					amount++;
				}
			}

		}

		if (amount == 0) {
			return true;
		}

		FishableData.Fishable[] fishing = new FishableData.Fishable[amount];

		for (int i = 0; i < amount; i++) {
			fishing[i] = f[i];
		}

		start(player, fishing, 0);

		return true;
	}

	private boolean fish() {
		if (fishing == null) {
			return false;
		}

		FishableData.Fishable[] fish = new FishableData.Fishable[5];

		byte c = 0;

		for (int i = 0; i < fishing.length; i++) {
			if (canFish(player, fishing[i], false)) {
				fish[c] = fishing[i];
				c = (byte) (c + 1);
			}
		}
		if (c == 0) {
			return false;
		}

		FishableData.Fishable f = fish[Utility.randomNumber(c)];

		if (player.getInventory().getFreeSlots() == 0) {
			SimpleDialogues.sendStatement(player, "You can't carry anymore fish.");
			return false;
		}

		if (success(f)) {
			if (f.getBaitRequired() != -1) {
				int r = player.getInventory().remove(new Item(f.getBaitRequired(), 1));

				if (r == 0) {
					player.getActionSender().sendMessage("You have run out of bait.");
					return false;
				}
			}

			player.getActionSender().sendSound(378, 0, 0);

			Item id = new Item(f.getRawFishId(), SkillCapePerks.FISHING.isWearing(player) && Utility.random(2) == 1 ? 2 : 1);
			String name = id.getName();
			player.getInventory().add(id);
			player.getSkills().addExperience(Skills.FISHING, f.getExperience());
			clueBottles(player);
			
			if (Boundary.isIn(player, Boundary.RESOURCE_AREA)) {
				if (Utility.random(15) == 5) {
					int randomAmount = Utility.random(5) + 1;
					player.getActionSender().sendMessage("You received x" + randomAmount + " blood money while fishing!");
					player.getInventory().add(13307, randomAmount);
				}
			}

			if (Utility.random(12000) == 5555) {
				int[] anglerOuftit = { 13258, 13259, 13260, 13261 };
				player.getInventory().addOrCreateGroundItem(player, new Item(anglerOuftit[Utility.random(anglerOuftit.length - 1)]));
				player.getActionSender().sendMessage("You notice a angler piece floating in the water and pick it up.");
			}
			
			if(Area.inWilderness(player) && random.nextInt(10) < 7) {
				player.getInventory().addOrCreateGroundItem(player, new Item(13307, Utility.random(1, 5)));
			}
			
			heronPet(player);
			player.getActionSender().sendMessage("You manage to catch " + getFishStringMod(name) + name+".");
			AchievementHandler.activate(player, AchievementList.FISHERMAN, 1);

		}

		return true;
	}

	private void clueBottles(Player player) {
		int chance = 20000 / 40;
		int bottleRoll = Utility.random(10);
		if (Utility.random(chance) == 1) {
			player.getActionSender().sendMessage("You catch a clue bottle!");
			if (bottleRoll < 6) {
				player.getInventory().add(13648, 1);
			} else if (bottleRoll > 5 && bottleRoll < 9) {
				player.getInventory().addOrCreateGroundItem(player, new Item(13649));
			} else {
				player.getInventory().addOrCreateGroundItem(player, new Item(13650));
			}
		}
	}

	private String getFishStringMod(String name) {
		return name.substring(name.length() - 2, name.length() - 1).equals("s") ? "some " : "a ";
	}

	private void reset() {
		fishing = null;
		tool = null;
	}

	private void start(final Player player, FishableData.Fishable[] fishing, int option) {
		if ((fishing == null) || (fishing[option] == null) || (fishing[option].getToolId() == -1)) {
			return;
		}

		this.fishing = fishing;

		tool = ToolData.Tools.forId(fishing[option].getToolId());

		if (!hasFishingItems(player, fishing[option], true)) {
			return;
		}

		player.getActionSender().sendSound(289, 0, 0);

		player.playAnimation(Animation.create(tool.getAnimationId()));

		Task skill = new Task(player, 4, false, StackType.NEVER_STACK, BreakType.ON_MOVE) {
			@Override
			public void execute() {
				player.playAnimation(Animation.create(tool.getAnimationId()));

				if (!fish()) {
					stop();
					reset();
					return;
				}
			}

			@Override
			public void onStop() {
			}
		};
		World.getWorld().schedule(skill);
	}

	private boolean success(FishableData.Fishable fish) {
		return Skills.isSuccess(player, 10, fish.getRequiredLevel());
	}
	
	/**
	 * Spawns the Heron pet when we roll on the table.
	 * @param player
	 *        The player receiving the Heron pet.
	 */
	private void heronPet(Player player) {
		Pets pets = Pets.HERON;
		Pet pet = new Pet(player, pets.getNpc());
		
		if(player.alreadyHasPet(player, 13320) || player.getPet() == pets.getNpc()) {
			return;
		}
		
		int random = Utility.random(5000);
		if (random == 0) {
			if (player.getPet() > -1) {
				player.getInventory().addOrSentToBank(player, new Item(13320));
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Heron.", false);
			} else {
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Heron.", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}
	}
}