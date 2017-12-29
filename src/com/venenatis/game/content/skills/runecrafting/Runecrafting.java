package com.venenatis.game.content.skills.runecrafting;

import com.venenatis.game.action.impl.Skill;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.npc.pet.Follower;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;

/**
 * This class represents the runecrafting skill and its functionality.
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class Runecrafting extends Skill {
	
	/**
	 * The talisman
	 */
	private Item talisman;

	/**
	 * The altar.
	 */
	private GameObject object;
	
	public GameObject getObject() {
		return object;
	}

	/**
	 * The runecrafting animation
	 */
	private Animation RUNECRAFTING_ANIMATION = Animation.create(791);

	/**
	 * The runecrafting graphic
	 */
	private Graphic RUNECRAFTING_GRAPHIC = Graphic.create(186);

	public Runecrafting(Player player, GameObject object) {
		super(player);
		this.object = object;
	}
	
	/**
	 * Teleports the player to the altar using the locate option on the talisman
	 * @param player
	 *        The player teleporting
	 * @return
	 */
	private boolean locateTalisman(Player player) {
		Talisman t = Talisman.forId(talisman.getId());
		
		player.getTeleportAction().teleport(t.getOutsideLocation());
		return true;
	}

	@Override
	public boolean start(Player player) {
		//Safety check, player should never be null
		if (player == null) {
			return false;
		}

		//Safety check
		if (talisman != null) {
			Talisman t = Talisman.forId(talisman.getId());
			
			//Check if we have an talisman if we do we can use the locate option
			if (t != null) {
				return locateTalisman(player);
			}
			//If we're not locating the alter we are trying to craft runes
		} else {
			return true;
		}

		return false;
	}
	
	@Override
	public boolean execute(Player player) {
		//First check if we have the required level
		if (player.getSkills().getLevel(Skills.RUNECRAFTING) < Altar.getAltar(getObject().getId()).getLevelRequired()) {
			SimpleDialogues.sendStatement(player, "You need a " + Skills.RUNECRAFTING + " level of " + Altar.getAltar(getObject().getId()).getLevelRequired() + " to craft this rune.");
			stop();
			return false;
		}

		//Then we can check if we have any essence
		if (!player.getInventory().contains(7936) && !player.getInventory().contains(1436)) {
			SimpleDialogues.sendStatement(player, "You do not have any rune essence to bind.");
			stop();
			return false;
		}

		//And lastly check if we can only use pure essence
		if (Altar.getAltar(getObject().getId()).isPureEssenceOnly() && !player.getInventory().contains(7936)) {
			SimpleDialogues.sendStatement(player, "You do not have any pure essence to bind.");
			stop();
			return false;
		}
		
		//Start animation and play the graphic
		player.playAnimation(RUNECRAFTING_ANIMATION);
		player.playGraphic(RUNECRAFTING_GRAPHIC);
		return true;
	}
	
	@Override
	public boolean finish(Player player) {
		Altar altar = Altar.getAltar(getObject().getId());
		
		//The essence multiplier based on runecrafting level
		int multiplier = (int) Math.floor(player.getSkills().getLevel(Skills.RUNECRAFTING) / altar.getDoubleRunesLevel());
        multiplier += 1;
		
		//The amount off essence we have in our inventory
		int essenceAmount = player.getInventory().getAmount(getEssType(player, altar));
		
		player.message("You bind the temple's power into " + ItemDefinition.get(altar.getRune()).getName() + "s");
		
		//Add experience based on the amount of runes we are crafting
		player.getSkills().addExperience(Skills.RUNECRAFTING, altar.getExperience() * essenceAmount);
		
		if (RANDOM.nextInt(altar.getBaseChance() - (player.getSkills().getLevel(Skills.RUNECRAFTING) * 25)) == 1) {
			Pet p = altar.petDrop();

			//First check if we already have this pet
			if (player.alreadyHasPet(player, altar.petDrop().getItem()) || player.getPet() == p.getNpc()) {
				return false;
			}
			
			//spawn the pet if we passed all checks
			if (player.getPet() > -1) {
				player.getInventory().addOrSentToBank(player, new Item(altar.petDrop().getItem()));
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received "+ItemDefinition.get(altar.petDrop().getItem()).getName()+".", false);
			} else {
				Follower pet = new Follower(player, p.getNpc());
				player.setPet(p.getNpc());
				World.getWorld().register(pet);
				World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received "+ItemDefinition.get(altar.petDrop().getItem()).getName()+".", false);
				player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
			}
		}
		
		//Remove all essence
		player.getInventory().removeAll(new Item(getEssType(player, altar)));
		
		//Replace with the rune we just crafted
		player.getInventory().add(new Item(altar.getRune(), essenceAmount * multiplier));
		return true;
	}

	/**
	 * Starts the runecrafting task
	 * 
	 * @param player
	 *            The player trying to activate the altar
	 * @param object
	 *            The actual altar we're trying to craft runes from
	 * @return
	 */
	public static boolean startAction(Player player, GameObject object) {
		if(Altar.alterIds(object.getId())) {
			Runecrafting runecrafting = new Runecrafting(player, object);
			
			//Activate the SkillAction task
			runecrafting.execute();
			
			//And sent the task to the World aswell
			World.getWorld().schedule(runecrafting);
			return true;
		}
		return false;
	}

	/**
	 * Checks which type off essence we should be using
	 * 
	 * @param player
	 *            The player to check
	 * @param altar
	 *            The altar to check
	 * @return The essence type required
	 */
	private int getEssType(Player player, Altar altar) {
		if (player.getInventory().contains(7936)) {
			return 7936;
		} else if (player.getInventory().contains(1436) && !altar.isPureEssenceOnly()) {
			return 1436;
		}
		return -1;
	}
}