package com.model.game.character.player.skill.runecrafting;

import com.model.game.character.Animation;
import com.model.game.character.Graphic;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.skill.SkillTask;
import com.model.game.character.player.skill.runecrafting.Talisman.Talisman;
import com.model.game.item.Item;
import com.model.task.Stackable;
import com.model.task.Walkable;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * The runecrafting skill
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class Runecrafting extends SkillTask {
	
	/**
	 * The altar object
	 */
	private int object;

	/**
	 * @return The altar objectId
	 */
	public int getObject() {
		return object;
	}

	/**
	 * Send the task
	 * @param player
	 *        The player crafting runes
	 * @param object
	 *        The altar
	 */
	public Runecrafting(Player player, int object) {
		super(player, 2, Walkable.NON_WALKABLE, Stackable.NON_STACKABLE, false);
		this.object = object;
	}
	
	/**
	 * Were trying to locate the altar
	 * @param player 
	 *        The player looking for an altar
	 */
	public static boolean findAltar(Player player, Item item) {
		if (player == null) {
			return false;
		}
		if (item != null) {
			Talisman talisman = Talisman.forId(item.getId());
			if (talisman != null) {
				return locateAlter(player, talisman);
			}
		} else {
			return true;
		}
		return false;
	}
	
	/**
	 * Do we meet the requirements to continue crafting runes
	 * @param player
	 *        The player
	 * @param object
	 *        The altar
	 */
	public static boolean meetsRequirements(Player player, int object) {
		if(player.getSkills().getLevel(Skills.RUNECRAFTING) < Talisman.getTalismanByAlter(object).getLevel()) {
			player.message("You do not have the required level to craft these runes.");
			return false;
		}
		if(!player.getItems().playerHasItem(7936) && !player.getItems().playerHasItem(1436)) {
			System.out.println("test");
			return false;
		}
		if(Talisman.getTalismanByAlter(object).getRequirePureEss() && !player.getItems().playerHasItem(7936)) {
			System.out.println("test2");
			return false;
		}
		player.playAnimation(Animation.create(791));
		player.playGraphics(Graphic.create(186));
		return true;
	}
	
	@Override
	public void execute() {
		if (getPlayer() == null || !getPlayer().isActive()) {
			stop();
			return;
		}
		
		Talisman talisman = Talisman.getTalismanByAlter(getObject());
		int amount = getAmount(getPlayer(), talisman);
		int essCount = getPlayer().getItems().getItemCount(getEssType(getPlayer(), talisman));
		getPlayer().message("You bind the temple's power into "+ItemDefinition.forId(talisman.getRewardId()).getName()+"s");
		getPlayer().getSkills().addExperience(Skills.RUNECRAFTING, talisman.getRewardExp()*essCount);
		getPlayer().getItems().remove(new Item(getEssType(getPlayer(), talisman)));
		getPlayer().getItems().addItem(new Item(talisman.getRewardId(), amount));
		stop();
	}
	
	/**
	 * We can locate a altar with this method
	 * @param player
	 *        The player trying to locate an altar
	 * @param talisman
	 *        The talisman of the altar we're trying to locate
	 */
	private static boolean locateAlter(Player player, Talisman talisman){
		String mainDirection = "";
		String subDirection = "";
		if(talisman.getOutsidePosition().getY() > player.getPosition().getY()){
			mainDirection = "north";
		}
		else if(talisman.getOutsidePosition().getY() < player.getPosition().getY()){
			mainDirection = "south";
		}
		if(talisman.getOutsidePosition().getX() > player.getPosition().getX()){
			subDirection = "east";
		}
		else if(talisman.getOutsidePosition().getX() < player.getPosition().getX()){
			subDirection = "west";
		}
		if(talisman.getOutsidePosition() == player.getPosition()){
			mainDirection = "center";
		}
		if(mainDirection != "" && subDirection != ""){
			player.message("The talisman pulls towards the "+mainDirection+"-"+subDirection);
		}else{
			player.message("The talisman pulls towards the "+mainDirection+subDirection);
		}
		return false;
	}

	/**
	 * The altar were crafting runes on
	 * @param player
	 *        The player
	 * @param altarId
	 *        The altar
	 */
	public static boolean handleObject(Player player, int altarId) {
		if (!meetsRequirements(player, altarId)) {
			return false;
		}
		
		if(Talisman.alterIds(altarId)){
			Runecrafting runecrafting = new Runecrafting(player, altarId);
			runecrafting.execute();
			player.setSkillTask(runecrafting);
			return true;
		}
		return false;
	}
	
	/**
	 * The amount of runes we can craft
	 * @param player
	 *        The player crafting
	 * @param talisman
	 *        The talisman or tiara required
	 */
	private int getAmount(Player player, Talisman talisman) {
		int rcLevel = player.getSkills().getLevel(Skills.RUNECRAFTING);
		int essCount = player.getItems().getItemCount(getEssType(player, talisman));
		if (talisman.equals(Talisman.AIR_TALISMAN)) {
			if (rcLevel < 11) {
				return essCount;
			} else if (rcLevel >= 11 && rcLevel < 22) {
				return essCount * 2;
			} else if (rcLevel >= 22 && rcLevel < 33) {
				return essCount * 3;
			} else if (rcLevel >= 33 && rcLevel < 44) {
				return essCount * 4;
			} else if (rcLevel >= 44 && rcLevel < 55) {
				return essCount * 5;
			} else if (rcLevel >= 55 && rcLevel < 66) {
				return essCount * 6;
			} else if (rcLevel >= 66 && rcLevel < 77) {
				return essCount * 7;
			} else if (rcLevel >= 77 && rcLevel < 88) {
				return essCount * 8;
			} else if (rcLevel >= 88 && rcLevel < 99) {
				return essCount * 9;
			} else if (rcLevel >= 99) {
				return essCount * 10;
			}
		} else if (talisman.equals(Talisman.MIND_TALISMAN)) {
			if (rcLevel < 14) {
				return essCount;
			} else if (rcLevel >= 14 && rcLevel < 28) {
				return essCount * 2;
			} else if (rcLevel >= 28 && rcLevel < 42) {
				return essCount * 3;
			} else if (rcLevel >= 42 && rcLevel < 56) {
				return essCount * 4;
			} else if (rcLevel >= 56 && rcLevel < 70) {
				return essCount * 5;
			} else if (rcLevel >= 70 && rcLevel < 84) {
				return essCount * 6;
			} else if (rcLevel >= 84) {
				return essCount * 7;
			}
		} else if (talisman.equals(Talisman.WATER_TALISMAN)) {
			if (rcLevel < 19) {
				return essCount;
			} else if (rcLevel >= 19 && rcLevel < 38) {
				return essCount * 2;
			} else if (rcLevel >= 38 && rcLevel < 57) {
				return essCount * 3;
			} else if (rcLevel >= 57 && rcLevel < 76) {
				return essCount * 4;
			} else if (rcLevel >= 76 && rcLevel < 95) {
				return essCount * 5;
			} else if (rcLevel >= 95) {
				return essCount * 6;
			}
		} else if (talisman.equals(Talisman.EARTH_TALISMAN)) {
			if (rcLevel < 26) {
				return essCount;
			} else if (rcLevel >= 26 && rcLevel < 52) {
				return essCount * 2;
			} else if (rcLevel >= 52 && rcLevel < 78) {
				return essCount * 3;
			} else if (rcLevel >= 78) {
				return essCount * 4;
			}
		} else if (talisman.equals(Talisman.FIRE_TALISMAN)) {
			if (rcLevel < 35) {
				return essCount;
			} else if (rcLevel >= 35 && rcLevel < 70) {
				return essCount * 2;
			} else if (rcLevel >= 70) {
				return essCount * 3;
			}
		} else if (talisman.equals(Talisman.BODY_TALISMAN)) {
			if (rcLevel < 46) {
				return essCount;
			} else if (rcLevel >= 46 && rcLevel < 92) {
				return essCount * 2;
			} else if (rcLevel >= 92) {
				return essCount * 3;
			}
		} else if (talisman.equals(Talisman.COSMIC_TALISMAN)) {
			if (rcLevel < 59) {
				return essCount;
			} else if (rcLevel >= 59) {
				return essCount * 2;
			}
		} else if (talisman.equals(Talisman.CHAOS_TALISMAN)) {
			if (rcLevel < 74) {
				return essCount;
			} else if (rcLevel >= 74) {
				return essCount * 2;
			}
		} else if (talisman.equals(Talisman.NATURE_TALISMAN)) {
			if (rcLevel < 91) {
				return essCount;
			} else if (rcLevel >= 91) {
				return essCount * 2;
			}
		}
		return essCount;
	}

	/**
	 * Gets the essence type we need
	 * @param player
	 *        The player
	 * @param talisman
	 *        The talisman/tiara
	 */
	private int getEssType(Player player, Talisman talisman) {
		if (player.getItems().playerHasItem(7936)) {
			return 7936;
		} else if (player.getItems().playerHasItem(1436) && !talisman.getRequirePureEss()) {
			return 1436;
		}
		return -1;
	}

}
