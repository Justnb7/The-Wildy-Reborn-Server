package com.venenatis.game.content.skills.runecrafting;

import com.venenatis.game.content.skills.SkillTask;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;

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
		super(player, 2, BreakType.ON_MOVE, StackType.NEVER_STACK, false);
		this.object = object;
	}
	
	/**
	 * Teleports the player to the altar using the locate option on the talisman
	 * @param player
	 *        The player teleporting
	 * @param item
	 *        The talisman
	 * @return
	 */
	public static boolean locateTalisman(Player player, Item item) {
		if (player == null) {
			return false;
		}
		if (item != null) {
			if(item.getId() == 1438) {//Air talisman
				player.getTeleportAction().teleport(new Location(2841, 4829, 0));
			} else if(item.getId() == 1448) {//Mind talisman
				player.getTeleportAction().teleport(new Location(2792, 4827, 0));
			} else if(item.getId() == 1444) {//Water talisman
				player.getTeleportAction().teleport(new Location(3482, 4838, 0));
			} else if(item.getId() == 1440) {//Earth talisman
				player.getTeleportAction().teleport(new Location(2655, 4830, 0));
			} else if(item.getId() == 1442) {//Fire talisman
				player.getTeleportAction().teleport(new Location(2574, 4848, 0));
			} else if(item.getId() == 1446) {//Body talisman
				player.getTeleportAction().teleport(new Location(2522, 4825, 0));
			} else if(item.getId() == 1454) {//Cosmic talisman
				player.getTeleportAction().teleport(new Location(2122, 4833, 0));
			} else if(item.getId() == 1452) {//Chaos talisman
				player.getTeleportAction().teleport(new Location(2281, 4837, 0));
			} else if(item.getId() == 1462) {//Nature talisman
				player.getTeleportAction().teleport(new Location(2400, 4835, 0));
			} else if(item.getId() == 1458) {//Law talisman
				player.getTeleportAction().teleport(new Location(2464, 4818, 0));
			} else if(item.getId() == 1456) {//Death talisman
				player.getTeleportAction().teleport(new Location(2208, 4830, 0));
			}
		}
		return false;
	}
	
	/**
	 * Do we meet the requirements to continue crafting runes
	 * @param player
	 *        The player
	 * @param id
	 *        The altar
	 */
	public static boolean meetsRequirements(Player player, int id) {
		Talisman talisman = Talisman.getTalismanByAltar(id);
		if(talisman != null) {
			if(player.getSkills().getLevel(Skills.RUNECRAFTING) < Talisman.getTalismanByAltar(talisman.getAlterId()).getLevel()) {
				player.getActionSender().sendMessage("You do not have the required level to craft these runes.");
				return false;
			}
			if(!player.getInventory().contains(7936) && !player.getInventory().contains(1436)) {
				player.getActionSender().sendMessage("Did you forget something?");
				return false;
			}
			if(Talisman.getTalismanByAltar(talisman.getAlterId()).pureEssOnly() && !player.getInventory().contains(7936)) {
				player.getActionSender().sendMessage("You do not have the right essence.");
				return false;
			}
			player.playAnimation(Animation.create(791));
			player.playGraphics(Graphic.create(186));
			return true;
		}
		return false;
	}
	
	@Override
	public void execute() {
		if (getPlayer() == null || !getPlayer().isActive()) {
			stop();
			return;
		}
		
		Talisman talisman = Talisman.getTalismanByAltar(getObject());
		int amount = getAmount(getPlayer(), talisman);
		int essCount = getPlayer().getInventory().getAmount(getEssType(getPlayer(), talisman));
		
		//Grant experience
		getPlayer().getActionSender().sendMessage("You bind the temple's power into "+ItemDefinition.get(talisman.getRuneReward()).getName()+"s");
		getPlayer().getSkills().addExperience(Skills.RUNECRAFTING, talisman.getExperience()*essCount);
		
		//Delete essence and reward runes
		getPlayer().getInventory().remove(new Item(getEssType(getPlayer(), talisman), amount));
		getPlayer().getInventory().add(new Item(talisman.getRuneReward(), amount));
		
		//Stop task
		stop();
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
		
		if(Talisman.altar(altarId)){
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
		int essCount = player.getInventory().getAmount(getEssType(player, talisman));
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
	 * Gets the essence type required
	 * @param player
	 *        The player
	 * @param talisman
	 *        The talisman/tiara
	 */
	private int getEssType(Player player, Talisman talisman) {
		if (player.getInventory().contains(7936)) {
			return 7936;
		} else if (player.getInventory().contains(1436) && !talisman.pureEssOnly()) {
			return 1436;
		}
		return -1;
	}

}
