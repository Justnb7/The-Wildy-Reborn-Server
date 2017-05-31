package com.model.game.character.player.skill.fishing;

import com.model.game.World;
import com.model.game.character.Animation;
import com.model.game.character.npc.NPC;
import com.model.game.character.npc.pet.Pet;
import com.model.game.character.npc.pet.Pets;
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
	public static boolean attemptFishing(Player player, NPC npc, int optionId) {
		int spotId = npc.getId();
		
		FishingSpot data = FishingSpot.forId(spotId, optionId);
		
		
		if (!meetsRequirements(player, data)) {
			return false;
		}

		player.playAnimation(Animation.create(data.getAnimationId()));
		player.getMovementHandler().stopMovement();
		player.setAttribute("fishing", true);
		player.getActionSender().sendMessage("You begin fishing...");
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
			player.getActionSender().sendMessage("You need a fishing level of " + data.getLevelRequired() + " to fish here.");
			return false;
		}
		if (data.isBaitRequired() && !player.getInventory().contains(313)) {
			player.getActionSender().sendMessage("You need some fishing bait to fish here.");
			return false;
		}
		if (!player.getInventory().contains(data.getToolId(), 1)) {
			player.getActionSender().sendMessage("You need a " + ItemDefinition.forId(data.getToolId()).getName() + " to fish here.");
			return false;
		}
		return true;
	}

	@Override
	public void execute() {
		if (getPlayer() == null || !getPlayer().isActive() || !SkillTask.noInventorySpace(getPlayer(), "fishing") || !(Boolean) getPlayer().getAttribute("fishing")) {
			getPlayer().playAnimation(Animation.create(-1));
			getPlayer().setAttribute("fishing", false);
			stop();
			return;
		}
		
		Item fish = new Item(data.getFishId(), 1);
		Item secondFish = new Item(data.getSecondFishId(), 1);
		if (data.isSecondFishAvailable() && Utility.getRandom(3) == 0) {
			getPlayer().getInventory().add(secondFish);
			getPlayer().getActionSender().sendMessage("You manage to catch some " + secondFish.getDefinition().getName().toLowerCase() + ".");
		} else {
			getPlayer().getInventory().add(fish);
			getPlayer().getActionSender().sendMessage("You manage to catch some " + fish.getDefinition().getName().toLowerCase() + ".");
		}
		heronPet(getPlayer(), data);
		if (data.isBaitRequired() && getPlayer().getInventory().contains(313) && Utility.getRandom(2) == 0) {
			getPlayer().getInventory().remove(new Item(313, 1));
		}
		getPlayer().getSkills().addExperience(Skills.FISHING, data.getExperience());
	}
	
	/**
	 * Spawns the Heron pet when we roll on the table.
	 * @param player
	 *        The player receiving the Heron pet.
	 * @param spot
	 *        The fishing spot
	 */
	void heronPet(Player player, FishingSpot spot) {
		int random = Utility.random(spot.getPetChance());
		if (random == 0) {
			if (player.isPetSpawned()) {
				//TODO spawn item in inv or bank
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getName() + " has just received 1x Heron.",
						false);
			} else {
				Pets pets = Pets.HERON;
				Pet pet = new Pet(player, pets.getNpc());
				player.setPetSpawned(true);
				player.setPet(pets.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getName() + " has just received 1x Heron.",
						false);
			}
		}
	}
}