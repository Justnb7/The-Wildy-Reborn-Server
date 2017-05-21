package com.model.game.character.player.content.clicking.object;

import com.model.Server;
import com.model.action.Action;
import com.model.game.character.Animation;
import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.rewards.CrystalChest;
import com.model.game.character.player.content.rewards.ShinyChest;
import com.model.game.character.player.content.teleport.Obelisks;
import com.model.game.character.player.content.teleport.Teleport;
import com.model.game.character.player.content.teleport.TeleportExecutor;
import com.model.game.character.player.content.teleport.Teleport.TeleportType;
import com.model.game.character.player.minigames.pest_control.PestControl;
import com.model.game.character.player.skill.agility.Agility;
import com.model.game.character.player.skill.agility.Agility.Obstacle;
import com.model.game.character.player.skill.runecrafting.Runecrafting;
import com.model.game.character.player.skill.thieving.Stalls;
import com.model.game.character.player.skill.woodcutting.Woodcutting;
import com.model.game.character.player.skill.woodcutting.Woodcutting.Tree;
import com.model.game.location.Location;
import com.model.game.object.GameObject;
import com.model.game.object.impl.SlashWebObject;
import com.model.task.ScheduledTask;
import com.model.utility.cache.ObjectDefinition;

/**
 * This class handles the object actions. So we don't have to add all object
 * actions in the packet.
 * 
 * @author Patrick van Elderen
 *
 */
public class ObjectInteraction {

	/**
	 * The first object action
	 * 
	 * @param player
	 *            The player using this option
	 * @param position
	 *            The position of the object
	 * @param id
	 *            The object
	 */
	public static void handleFirstClickAction(Player player, Location position, int id) {
		if (player.inDebugMode()) {
			System.out.println(String.format("[ObjectInteraction] - position: %s object: %d ", position, id));
		}

		ObjectDefinition definition = ObjectDefinition.getObjectDef(id);
		
		final Obstacle obstacle = Obstacle.forLocation(position);

		player.getMining().mine(id, position);

		Action action;
		Tree tree = Tree.forId(id);
		GameObject obj = new GameObject(id, position.getX(), position.getY(), position.getZ());
		
		if (tree != null) {
			action = new Woodcutting(player, obj);
			return;
		}

		Obelisks.get().activate(player, id);

		if (Runecrafting.handleObject(player, id)) {
			return;
		}
		
		if (obstacle != null) {
			Agility.tackleObstacle(player, obstacle, id);
		}

		if (definition.name == null || definition.name.length() == 0) {
			return;
		}
		if (definition.getName().toLowerCase().contains("altar")
				&& definition.actions[0].toLowerCase().contains("pray")) {
			player.getSkills().getPrayer().prayAltar(position);
			return;
		}
		switch (definition.name.toLowerCase()) {
		case "open chest":
			if (player.getInventory().playerHasItem(85)) {
				ShinyChest.searchChest(player, position);
				return;
			} else if (player.getInventory().playerHasItem(989)) {
				CrystalChest.searchChest(player, position);
				return;
			} else {
				player.getActionSender().sendMessage("You need a key to open this chest.");
			}
			break;

		case "magic chest":

			break;

		case "ladder":
			// KBD ladder
			if (player.getArea().inWild() && player.getX() == 3069 && player.getY() == 10255) {
				player.playAnimation(Animation.create(828));
				player.setForcedMovement(true);
				Server.getTaskScheduler().schedule(new ScheduledTask(2) {
					@Override
					public void execute() {
						player.setForcedMovement(false);
						player.movePlayer(new Location(3017, 3850, 0));
						this.stop();
					}
				});
			}
			if (player.getArea().inWild() && player.getX() == 3017 && player.getY() == 3850) {
				player.playAnimation(Animation.create(828));
				player.setForcedMovement(true);
				Server.getTaskScheduler().schedule(new ScheduledTask(2) {
					@Override
					public void execute() {
						player.setForcedMovement(false);
						player.movePlayer(new Location(3069, 10255, 0));
						this.stop();
					}
				});
			}
			break;

		case "bank":
		case "bank booth":
			player.getBank().open();
			break;

		case "crevice":
			if (player.getY() == 9797) {
				player.getKraken().start(player);
			} else if (player.getY() == 5798) {
				player.movePlayer(new Location(2486, 9797, 0));
			} else if (player.getX() == 2444) {
				player.movePlayer(new Location(2430, 3424, 0));
			}
			break;

		case "cave":
			if (player.getX() == 2430) {
				player.movePlayer(new Location(2444, 9825, 0));
			}
			break;

		case "passage":
			if (player.getX() == 2970) {
				player.movePlayer(new Location(2974, 4384, 2));
			} else if (player.getX() == 2974) {
				player.movePlayer(new Location(2970, 4384, 2));
			}
			break;

		case "lever":
			if (player.getX() == 3153)
				TeleportExecutor.executeLeverTeleport(player,
						new Teleport(new Location(3090, 3475, player.getZ()), TeleportType.LEVER));
			break;
		}

		switch (id) {

		/**
		 * Entering the Fight Caves.
		 */
		case 11833:
			player.getFCI().start(player);
			break;

		case 11834:
			if (Boundary.isIn(player, Boundary.FIGHT_CAVE)) {
				player.getFightCave().exitCave(1);
				return;
			}
			break;

		/**
		 * Pest control
		 */
		case 14315:
			PestControl.addToLobby(player);
			break;

		case 14314:
			PestControl.removeFromLobby(player);
			break;

		/**
		 * Warriors guild
		 */
		case 24306:
		case 24309:
			if (player.getHeight() == 2) {
				player.getWarriorsGuild().handleDoor();
				return;
			}
			if (player.heightLevel == 0) {
				if (player.absX == 2855 || player.absX == 2854) {
					if (player.absY == 3546)
						player.movePlayer(new Location(player.absX, player.absY - 1, 0));
					else if (player.absY == 3545)
						player.movePlayer(new Location(player.absX, player.absY + 1, 0));
				}
			}
			break;

		/**
		 * Godwars dungeon doorsw
		 */
		case 26502:
			if (player.getY() == 5294) {
				player.movePlayer(new Location(2839, 5295, 2));
			} else if (player.getY() == 5295 || player.getY() == 5296) {
				player.movePlayer(new Location(2839, 5294, 2));
			}
			break;

		case 26503:
			if (player.getX() == 2862) {
				player.movePlayer(new Location(2863, 5354, 2));
			} else if (player.getX() == 2863) {
				player.movePlayer(new Location(2862, 5354, 2));
			}
			break;

		case 26504:
			if (player.getX() == 2909) {
				player.movePlayer(new Location(2908, 5265, 0));
			} else if (player.getX() == 2908) {
				player.movePlayer(new Location(2909, 5265, 0));
			}
			break;

		case 26505:
			if (player.getY() == 5333) {
				player.movePlayer(new Location(2925, 5332, 2));
			} else if (player.getY() == 5332) {
				player.movePlayer(new Location(2925, 5333, 2));
			}
			break;

		/**
		 * Wilderness ditch
		 */
		case 23271:
			if (position.getX() == 2996) {
				return;
			}
			player.getAttributes().put("busy", true);
			int yPos = 0;
			int direction = 0;
			if (player.getLocation().getY() == 3523) {
				yPos = -3;
				direction = 2;
			} else if (player.getLocation().getY() == 3520) {
				yPos = 3;
				direction = 0;
			}
			int[] forceMovementVars = { 0, 0, 0, yPos, 33, 60, direction, 2 };
			Agility.jumpDitch(player, 6132, forceMovementVars, 0, true);
		break;

		/**
		 * Lever objects
		 */
		case 5960:
			TeleportExecutor.executeLeverTeleport(player,
					new Teleport(new Location(3090, 3956, player.getZ()), TeleportType.LEVER));
			break;

		case 5959:
			TeleportExecutor.executeLeverTeleport(player,
					new Teleport(new Location(2539, 4712, player.getZ()), TeleportType.LEVER));
			break;

		case 1814:
			TeleportExecutor.executeLeverTeleport(player,
					new Teleport(new Location(3158, 3953, player.getZ()), TeleportType.LEVER));
			break;

		case 4950:
			TeleportExecutor.executeLeverTeleport(player,
					new Teleport(new Location(3087, 3500, player.getZ()), TeleportType.LEVER));
			break;

		case 1816:
			TeleportExecutor.executeLeverTeleport(player,
					new Teleport(new Location(2271, 4680, player.getZ()), TeleportType.LEVER));
			break;

		case 1817:
			TeleportExecutor.executeLeverTeleport(player,
					new Teleport(new Location(3067, 10253, player.getZ()), TeleportType.LEVER));
			break;

		case 26761:
			TeleportExecutor.executeLeverTeleport(player,
					new Teleport(new Location(3153, 3923, player.getZ()), TeleportType.LEVER));
			break;

		case 1815:
			TeleportExecutor.executeLeverTeleport(player,
					new Teleport(new Location(3090, 3475, player.getZ()), TeleportType.LEVER));
			break;

		/**
		 * Scorpia pit
		 */
		case 26762:
			player.movePlayer(new Location(3243, 10351, 0));
			break;

		case 26763:
			player.movePlayer(new Location(3232, 3950, 0));
			break;

		/**
		 * Webs
		 */
		case 733:
			SlashWebObject.slashWeb(player, new Location(position.getX() , position.getY()), false);
			break;

		/**
		 * Sparkling pool
		 */
		case 2879:
			player.movePlayer(new Location(2538, 4716, 0));
			break;

		case 2878:
			player.movePlayer(new Location(2509, 4689, 0));
			break;

		/**
		 * Lever Mage arena
		 */
		case 9706:
			TeleportExecutor.teleport(player, new Teleport(new Location(3105, 3951, 0), TeleportType.NORMAL), false);
			break;

		case 9707:
			TeleportExecutor.teleport(player, new Teleport(new Location(3105, 3956, 0), TeleportType.NORMAL), false);
			break;
		}
	}
	
	/**
	 * The second object action
	 * 
	 * @param player
	 *            The player using this option
	 * @param position
	 *            The position of the object
	 * @param id
	 *            The object
	 */
	public static void handleSecondClickAction(Player player, Location position, int id) {
		if (player.inDebugMode()) {
			System.out.println(String.format("[ObjectInteraction option 2] - position: %s object: %d ", position, id));
		}
		
		ObjectDefinition objectDef = ObjectDefinition.getObjectDef(id);
		switch (objectDef.name.toLowerCase()) {

		case "bank":
		case "Bank":
		case "bank booth":
		case "booth":
			player.getBank().open();
			break;
			
		}
		
		switch (id) {
		
		case 11730:
			player.getThieving().stealFromStall(Stalls.BAKERS_STALL, id);
			break;
		case 11731:
			player.getThieving().stealFromStall(Stalls.GEM_STALL, id);
			break;
		case 11732:
			player.getThieving().stealFromStall(Stalls.FUR_STALL, id);
			break;
		case 11734:
			player.getThieving().stealFromStall(Stalls.SILVER_STALL, id);
			break;
		case 14011:
			player.getThieving().stealFromStall(Stalls.MARKET_STALL, id);
			break;
			
		}
	}
	
	/**
	 * The third object action
	 * 
	 * @param player
	 *            The player using this option
	 * @param position
	 *            The position of the object
	 * @param id
	 *            The object
	 */
	public static void handleThirdClickAction(Player player, Location position, int id) {
		if (player.inDebugMode()) {
			System.out.println(String.format("[ObjectInteraction option 3] - position: %s object: %d ", position, id));
		}
	}

}
