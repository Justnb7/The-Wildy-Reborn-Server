package com.venenatis.game.content.clicking.objects;

import com.venenatis.game.action.Action;
import com.venenatis.game.cache.definitions.AnyRevObjectDefinition;
import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.content.skills.agility.Agility;
import com.venenatis.game.content.skills.agility.Agility.Obstacle;
import com.venenatis.game.content.skills.mining.Mining;
import com.venenatis.game.content.skills.mining.Mining.Rock;
import com.venenatis.game.content.skills.runecrafting.Runecrafting;
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
		final GameObject obj = RegionStoreManager.get().getGameObject(location, objectId);
		
		Action action = null;
		Tree tree = Tree.forId(objectId);
		Rock rock = Rock.forId(objectId);
		final Obstacle obstacle = Obstacle.forLocation(location);
		if (tree != null) {
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
		} else if (Runecrafting.handleObject(player, obj)) {
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
			/*DialogueManager.start(player, 9);
			player.setDialogueOptions(new DialogueOptions() {
				@Override
				public void handleOption(Player player, int option) {
					switch(option) {
					case 1: //Normal spellbook option
						player.getActionSender().removeAllInterfaces();
						player.setSpellBook(SpellBook.MODERN_MAGICS);
						player.getActionSender().sendSidebarInterface(6, 1151);
						break;
					case 2: //Ancient spellbook option
						player.getActionSender().removeAllInterfaces();
						player.setSpellBook(SpellBook.ANCIENT_MAGICKS);
						player.getActionSender().sendSidebarInterface(6, 12855);
						break;
					case 3: //Lunar spellbook option
						player.getActionSender().removeAllInterfaces();
						player.setSpellBook(SpellBook.LUNAR_MAGICS);
						player.getActionSender().sendSidebarInterface(6, 29999);
						break;
					case 4: //Cancel option
						player.getActionSender().removeAllInterfaces();
						break;
					}
				}
			});	*/		
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
