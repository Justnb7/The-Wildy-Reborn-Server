package com.venenatis.game.content.clicking.objects;

import com.venenatis.game.action.Action;
import com.venenatis.game.cache.definitions.AnyRevObjectDefinition;
import com.venenatis.game.content.BrimhavenVines;
import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.content.skills.agility.Agility;
import com.venenatis.game.content.skills.agility.Agility.Obstacle;
import com.venenatis.game.content.skills.agility.Shortcut;
import com.venenatis.game.content.skills.farming.Farming;
import com.venenatis.game.content.skills.mining.Mining;
import com.venenatis.game.content.skills.mining.Mining.Rock;
import com.venenatis.game.content.skills.runecrafting.Runecrafting;
import com.venenatis.game.content.skills.smithing.SmithingConstants;
import com.venenatis.game.content.skills.thieving.Stalls;
import com.venenatis.game.content.skills.woodcutting.Woodcutting;
import com.venenatis.game.content.skills.woodcutting.Woodcutting.Tree;
import com.venenatis.game.content.teleportation.lever.Levers;
import com.venenatis.game.content.teleportation.obelisk.Obelisks;
import com.venenatis.game.location.Area;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.object.impl.webs.SlashWebObject;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;
import com.venenatis.server.Server;

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
	 * @param location
	 *            The position of the object
	 * @param objectId
	 *            The object
	 */
	public static void handleFirstClickAction(Player player, Location location, int objectId) {
		AnyRevObjectDefinition def = AnyRevObjectDefinition.get(objectId);
		
		player.debug(String.format("[ObjectInteraction first option] - position: %s object: %d ", location, objectId));
		//System.out.println(String.format("[ObjectInteraction first option] - position: %s object: %d ", location, objectId));
		final GameObject obj = Server.getGlobalObjects().customOrCache(objectId, location); 
		
		/*if (obj == null) {
			player.debug("No valid object at "+location+" with id "+objectId);
			return;
		}*/
		
		//Like im using the regionStoreManager to get the object tho
		
		Action action = null;
		Tree tree = Tree.forId(objectId);
		Rock rock = Rock.forId(objectId);
		final Obstacle obstacle = Obstacle.forLocation(location);
		
		if(BrimhavenVines.handleBrimhavenVines(player, objectId)) {
			return;
		}
		
		/*if(BrimhavenVines.chop(player, obj)) {
			return;
		}*/
		
		if (SmithingConstants.clickAnvil(player, objectId)) {
			return;
		}
		
		if (tree != null) {
			player.debug("wc "+tree);
			action = new Woodcutting(player, obj);
		} else if (rock != null) {
			action = new Mining(player, obj);
		} else if (obstacle != null) {
			action = new Action(player, 0) {
				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}

				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.NEVER;
				}

				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}

				@Override
				public void execute() {
					this.stop();
					Agility.tackleObstacle(player, obstacle, obj);
				}
			};
		}
		
		if (def.getName().toLowerCase().contains("altar") && def.getActions()[0].toLowerCase().contains("pray")) {
			player.getSkills().getPrayer().prayAltar(location);
			return;
		}
		
		if (def.getName().toLowerCase().contains("crevice")) {
			if (player.getY() == 9797) {
				player.getKraken().start(player);
			} else if (player.getY() == 5798) {
				player.setTeleportTarget(new Location(2486, 9797, 0));
			} else if (player.getX() == 2444) {
				player.setTeleportTarget(new Location(2430, 3424, 0));
			}
		}
		
		if (Farming.harvest(player, location.getX(), location.getY())) {
			return;
		}
		
		if (Farming.inspectObject(player, location.getX(), location.getY())) {
			return;
		}
		
		if (Runecrafting.handleObject(player, obj)) {
			return;
		}
		
		/** Duel Arena */
		if (player.getDuelArena().isDueling()) {
			player.getDuelArena().onFirstClickObject(obj);
			return;
		}
		
		/** Levers */
		if (Levers.handle(player, obj) && obj != null) {
			return;
		}
		
		/** Spider Webs */
		if (SlashWebObject.slash(player, obj) && obj != null) {
			return;
		}
		
		/** Minigame */
		MinigameHandler.execute(player, $it -> $it.onFirstClickObject(player, obj));
		
		/** Obelisk teleportation */
		if (obj != null)
			Obelisks.get().activate(player, obj.getId());
		
		switch (def.getName().toLowerCase()) {
		
		case "furnace":
			SmithingConstants.sendSmeltSelectionInterface(player);
			break;
			
		case "anvil":
			if (SmithingConstants.clickAnvil(player, obj.getId())) {
				return;
			}
			break;

		case "bank":
		case "Bank":
		case "bank booth":
		case "booth":
		case "bank chest":
			if(Area.inWilderness(player))
				return;
			player.getBank().open();
			break;
			
		}
		
		switch(objectId) {
		
		case 26720:
			Location root = player.getLocation();
			if(root.getX() == 2356) {
				player.setTeleportTarget(Location.create(root.getX() + 2, root.getY(), location.getZ()));
			} else if(root.getX() == 2358) {
				player.setTeleportTarget(Location.create(root.getX() - 2, root.getY(), location.getZ()));
			}
			break;
		
		case 11833:
			player.getFightCave().enter_cave(player);
			break;
			
		case 11834:
			player.getFightCave().stop(player);
			break;
		
		case 26502:
			Location gwdLoc = player.getLocation();
			if (gwdLoc.getY() == 5294) {
				player.setTeleportTarget(Location.create(gwdLoc.getX(), gwdLoc.getY() + 2, location.getZ()));
			} else if (gwdLoc.getY() == 5296) {
				player.setTeleportTarget(Location.create(gwdLoc.getX(), gwdLoc.getY() - 2, location.getZ()));
			}
			break;
			
		case 26503:
			gwdLoc = player.getLocation();
			if (gwdLoc.getX() == 2862) {
				player.setTeleportTarget(Location.create(gwdLoc.getX() + 2, gwdLoc.getY(), location.getZ()));
			} else if (gwdLoc.getX() == 2864) {
				player.setTeleportTarget(Location.create(gwdLoc.getX() - 2, gwdLoc.getY(), location.getZ()));
			}
			break;
			
		case 26504:
			gwdLoc = player.getLocation();
			if (gwdLoc.getX() == 2909) {
				player.setTeleportTarget(Location.create(gwdLoc.getX() - 2, gwdLoc.getY(), location.getZ()));
			} else if (gwdLoc.getX() == 2907) {
				player.setTeleportTarget(Location.create(gwdLoc.getX() + 2, gwdLoc.getY(), location.getZ()));
			}
			break;
			
		case 26505:
			gwdLoc = player.getLocation();
			if (gwdLoc.getY() == 5333) {
				player.setTeleportTarget(Location.create(gwdLoc.getX(), gwdLoc.getY() - 2, location.getZ()));
			} else if (gwdLoc.getY() == 5331) {
				player.setTeleportTarget(Location.create(gwdLoc.getX(), gwdLoc.getY() + 2, location.getZ()));
			}
			break;
		
		/**
		 * Slayer tower
		 */
		case 16537:
			if (player.getZ() == 0) {
				player.setTeleportTarget(new Location(player.getX(), player.getY(), 1));
			} else if (player.getZ() == 1) {
				player.setTeleportTarget(new Location(player.getX(), player.getY(), 2));
			}
			break;

		case 16538:
			if (player.getZ() == 1) {
				player.setTeleportTarget(new Location(player.getX(), player.getY(), 0));
			} else if (player.getZ() == 2) {
				player.setTeleportTarget(new Location(player.getX(), player.getY(), 1));
			}
			break;

		case 2120:
		case 4494:
			if (player.getZ() == 2) {
				player.setTeleportTarget(new Location(player.getX() - 5, player.getY(), 1));
			} else if (player.getZ() == 1) {
				player.setTeleportTarget(new Location(player.getX() + 5, player.getY(), 0));
			}
			break;

		case 2114:
			if (player.getZ() == 0) {
				player.setTeleportTarget(new Location(3433, 3538, 1));
			} else if (player.getZ() == 1) {
				player.setTeleportTarget(new Location(3433, 3538, 1));
			}
			break;

		case 2119:
			if (player.getZ() == 1) {
				player.setTeleportTarget(new Location(3417, 3540, 2));
			}
			break;
		
		/**
		 * Shortcuts
		 */
		case 9328:
		case 9301:
		case 9302:
		case 2322:
		case 2323:
		case 2296:
		case 5100:
		case 21738:
		case 21739:
		case 14922:
		case 3067:
		case 9309:
		case 9310:
		case 2618:
		case 2332:
		case 20882:
		case 20884:
		case 4615:
		case 4616:
		case 3933:
		case 12127:
		case 16539:
		case 993:
		case 51:
		case 8739:
			Shortcut.processAgilityShortcut(player, obj);
			break;
		
		case 1727:
			if (player.getX() == 3007)
				player.setTeleportTarget(new Location(3008, 3849, 0));
			else if (player.getX() == 3008)
				player.setTeleportTarget(new Location(3007, 3849, 0));
			break;
		
		case 1728:
			if (player.getX() == 3007)
				player.setTeleportTarget(new Location(3008, 3850, 0));
			else if (player.getX() == 3008)
				player.setTeleportTarget(new Location(3007, 3850, 0));
			break;
			
		case 18987:
			player.setTeleportTarget(new Location(2271, 4698, 0));
			break;
		
		case 10229:
			player.setTeleportTarget(new Location(1912, 4367, 0));
		break;
		
		case 10230:
			player.setTeleportTarget(new Location(2899, 4449, 0));
		break;
		
		case MAGICAL_ALTAR:
			player.getDialogueManager().start("MAGIC_BOOK", player);	
			break;

		case REJUVENATION_POOL:
			if(player.getTotalAmountDonated() >= 100) {
				player.setSpecialAmount(100);
				player.getWeaponInterface().restoreWeaponAttributes();
			} else {
				player.getActionSender().sendMessage("@red@[DYK]: Elite donators renew special attack aswell?");
			}
			player.getSkills().setLevel(Skills.HITPOINTS, player.getSkills().getLevelForExperience(Skills.HITPOINTS));
			player.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevelForExperience(Skills.PRAYER));
			player.setRunEnergy(100);
			player.getActionSender().sendRunEnergy();
			player.playGraphics(new Graphic(683));
			break;
			
		/* Deposit Box */
		case 6948:
		case 9398:
		case 11747:
		case 25937:
			player.getActionSender().sendString("The Bank of Venenatis - Deposit Box", 7421);
			player.getActionSender().sendInterfaceWithInventoryOverlay(4465, 197);
			player.getActionSender().sendItemOnInterface(7423, player.getInventory().toArray());
			break;
		case 26642: //clan wars portal
		case 26644:
			player.getActionSender().sendMessage("Coming soon...");
			break;
		case 23271:
				if (location.getX() == 2996) {
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
		
		}
		if (action != null) {
			player.message("added action");
			player.getActionQueue().addAction(action);
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
	public static void handleSecondClickAction(Player player, Location location, int id) {
		player.debug(String.format("[ObjectInteraction option 2] - position: %s object: %d ", location, id));

		AnyRevObjectDefinition objectDef = AnyRevObjectDefinition.get(id);
		
		final GameObject obj = RegionStoreManager.get().getGameObject(location, id);
		//GameObject obj = new GameObject(objectId, location.getX(), location.getY(), location.getZ());
		
		MinigameHandler.execute(player, $it -> $it.onSecondClickObject(player, obj));
		
		switch (objectDef.getName().toLowerCase()) {
		
		case "anvil":
			if (SmithingConstants.clickAnvil(player, obj.getId())) {
				return;
			}
			break;
		
		case "furnace":
			SmithingConstants.sendSmeltSelectionInterface(player);
			break;

		case "bank":
		case "Bank":
		case "bank booth":
		case "booth":
		case "bank chest":
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
		player.debug(String.format("[ObjectInteraction option 3] - position: %s object: %d ", position, id));
	}
	
	private static final int MAGICAL_ALTAR = 29150;
	private static final int REJUVENATION_POOL = 29241;

}
