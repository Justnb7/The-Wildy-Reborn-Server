package com.venenatis.game.content.skills.runecrafting;

import com.venenatis.game.content.skills.SkillTask;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.definitions.ItemDefinition;
import com.venenatis.game.model.entity.npc.pet.Pet;
import com.venenatis.game.model.entity.npc.pet.Pets;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.dialogue.SimpleDialogues;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;

/**
 * The runecrafting skill
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 *
 */
public class Runecrafting extends SkillTask {
	
	/**
	 * The altar object
	 */
	private GameObject object;

	/**
	 * @return The altar objectId
	 */
	public GameObject getObject() {
		return object;
	}

	/**
	 * Send the task
	 * @param player
	 *        The player crafting runes
	 * @param object
	 *        The altar
	 */
	public Runecrafting(Player player, GameObject object) {
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
	public static boolean meetsRequirements(Player player, GameObject id) {
		if(id == null) {
			return false;
		}
		Talisman talisman = Talisman.getTalismanByAltar(id.getId());
		if(talisman != null) {
			if(player.getSkills().getLevel(Skills.RUNECRAFTING) < Talisman.getTalismanByAltar(talisman.getAlterId()).getLevel()) {
				player.getActionSender().sendMessage("You do not have the required level to craft these runes.");
				return false;
			}
			if(!player.getInventory().contains(7936) && !player.getInventory().contains(1436)) {
				SimpleDialogues.sendStatement(player, "You do not have any rune essence to bind.");
				return false;
			}
			if(Talisman.getTalismanByAltar(talisman.getAlterId()).pureEssOnly() && !player.getInventory().contains(7936)) {
				SimpleDialogues.sendStatement(player, "You do not have any pure essence to bind.");
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
		Player player = (Player) getPlayer();
		if (player == null || !player.isActive()) {
			stop();
			return;
		}
		
		Talisman talisman = Talisman.getTalismanByAltar(getObject().getId());
		int amount = getAmount(player, talisman);
		int essCount = player.getInventory().getAmount(getEssType(player, talisman));
		
		//Grant experience
		player.getActionSender().sendMessage("You bind the temple's power into "+ItemDefinition.get(talisman.getRuneReward()).getName()+"s");
		player.getSkills().addExperience(Skills.RUNECRAFTING, talisman.getExperience()*essCount);
		
		//A random chance of receiving a runecrafting pet
		int random = Utility.random(1500);
		if (random == 0) {
			switch (talisman.getId()) {
			case 1438:
				if (player.getPet() > -1) {
					player.getInventory().addOrSentToBank(player, new Item(20667));
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
				} else {
					Pets pets = Pets.RIFT_GUARDIAN_AIR;
					Pet pet = new Pet(player, pets.getNpc());
					player.setPet(pets.getNpc());
					World.getWorld().register(pet);
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
					player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
				}
				break;
			case 1448:
				if (player.getPet() > -1) {
					player.getInventory().addOrSentToBank(player, new Item(20669));
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
				} else {
					Pets pets = Pets.RIFT_GUARDIAN_MIND;
					Pet pet = new Pet(player, pets.getNpc());
					player.setPet(pets.getNpc());
					World.getWorld().register(pet);
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
					player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
				}
				break;
			case 1444:
				if (player.getPet() > -1) {
					player.getInventory().addOrSentToBank(player, new Item(20671));
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
				} else {
					Pets pets = Pets.RIFT_GUARDIAN_WATER;
					Pet pet = new Pet(player, pets.getNpc());
					player.setPet(pets.getNpc());
					World.getWorld().register(pet);
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
					player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
				}
				break;
			case 1440:
				if (player.getPet() > -1) {
					player.getInventory().addOrSentToBank(player, new Item(20673));
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
				} else {
					Pets pets = Pets.RIFT_GUARDIAN_EARTH;
					Pet pet = new Pet(player, pets.getNpc());
					player.setPet(pets.getNpc());
					World.getWorld().register(pet);
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
					player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
				}
				break;
			case 1442:
				if (player.getPet() > -1) {
					player.getInventory().addOrSentToBank(player, new Item(20665));
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
				} else {
					Pets pets = Pets.RIFT_GUARDIAN_FIRE;
					Pet pet = new Pet(player, pets.getNpc());
					player.setPet(pets.getNpc());
					World.getWorld().register(pet);
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
					player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
				}
				break;
			case 1446:
				if (player.getPet() > -1) {
					player.getInventory().addOrSentToBank(player, new Item(20677));
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
				} else {
					Pets pets = Pets.RIFT_GUARDIAN_BODY;
					Pet pet = new Pet(player, pets.getNpc());
					player.setPet(pets.getNpc());
					World.getWorld().register(pet);
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
					player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
				}
				break;
			case 1454:
				if (player.getPet() > -1) {
					player.getInventory().addOrSentToBank(player, new Item(20677));
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
				} else {
					Pets pets = Pets.RIFT_GUARDIAN_COSMIC;
					Pet pet = new Pet(player, pets.getNpc());
					player.setPet(pets.getNpc());
					World.getWorld().register(pet);
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
					player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
				}
				break;
			case 1452:
				if (player.getPet() > -1) {
					player.getInventory().addOrSentToBank(player, new Item(20675));
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
				} else {
					Pets pets = Pets.RIFT_GUARDIAN_CHAOS;
					Pet pet = new Pet(player, pets.getNpc());
					player.setPet(pets.getNpc());
					World.getWorld().register(pet);
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
					player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
				}
				break;
			case 1462:
				if (player.getPet() > -1) {
					player.getInventory().addOrSentToBank(player, new Item(20681));
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
				} else {
					Pets pets = Pets.RIFT_GUARDIAN_NATURE;
					Pet pet = new Pet(player, pets.getNpc());
					player.setPet(pets.getNpc());
					World.getWorld().register(pet);
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
					player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
				}
				break;
			case 1458:
				if (player.getPet() > -1) {
					player.getInventory().addOrSentToBank(player, new Item(20683));
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
				} else {
					Pets pets = Pets.RIFT_GUARDIAN_LAW;
					Pet pet = new Pet(player, pets.getNpc());
					player.setPet(pets.getNpc());
					World.getWorld().register(pet);
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
					player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
				}
				break;
			case 1456:
				if (player.getPet() > -1) {
					player.getInventory().addOrSentToBank(player, new Item(20685));
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
				} else {
					Pets pets = Pets.RIFT_GUARDIAN_DEATH;
					Pet pet = new Pet(player, pets.getNpc());
					player.setPet(pets.getNpc());
					World.getWorld().register(pet);
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
					player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
				}
				break;
			case 1450:
				if (player.getPet() > -1) {
					player.getInventory().addOrSentToBank(player, new Item(20691));
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
				} else {
					Pets pets = Pets.RIFT_GUARDIAN_BLOOD;
					Pet pet = new Pet(player, pets.getNpc());
					player.setPet(pets.getNpc());
					World.getWorld().register(pet);
					World.getWorld().sendWorldMessage("<col=7f00ff>" + player.getUsername() + " has just received Rift guardian.", false);
					player.getActionSender().sendMessage("You have a funny feeling like you're being followed.");
				}
				break;
			}
		}
		
		//Delete essence and reward runes
		player.getInventory().remove(new Item(getEssType(player, talisman), amount));
		player.getInventory().add(new Item(talisman.getRuneReward(), amount));
		
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
	public static boolean handleObject(Player player, GameObject gameObject) {
		if (!meetsRequirements(player, gameObject)) {
			return false;
		}
		if(Talisman.altar(gameObject.getId())){
			Runecrafting runecrafting = new Runecrafting(player, gameObject);
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
