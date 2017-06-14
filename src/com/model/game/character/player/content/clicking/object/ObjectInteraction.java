package com.model.game.character.player.content.clicking.object;

import cache.definitions.AnyRevObjectDefinition;
import com.model.action.Action;
import com.model.action.impl.actions.RestorePrayerPointsAction;
import com.model.game.character.player.Player;
import com.model.game.character.player.skill.agility.Agility;
import com.model.game.character.player.skill.mining.Mining;
import com.model.game.character.player.skill.mining.Mining.Rock;
import com.model.game.character.player.skill.thieving.Stalls;
import com.model.game.character.player.skill.woodcutting.Woodcutting;
import com.model.game.character.player.skill.woodcutting.Woodcutting.Tree;
import com.model.game.location.Location;
import com.model.game.object.GameObject;

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
		
		if (player.inDebugMode()) {
			player.getActionSender().sendMessage(String.format("[ObjectInteraction first option] - position: %s object: %d ", location, objectId));
		}
		
		Action action = null;
		Tree tree = Tree.forId(objectId);
		Rock rock = Rock.forId(objectId);
		GameObject obj = new GameObject(objectId, location.getX(), location.getY(), location.getZ());
		if (tree != null) {
			action = new Woodcutting(player, obj);
		} else if (rock != null) {
			action = new Mining(player, obj);
		}
		
		switch (def.getName().toLowerCase()) {

		case "bank":
		case "Bank":
		case "bank booth":
		case "booth":
		case "bank chest":
			player.getBank().open();
			break;
			
		}
		
		switch(objectId) {
		case 7812: //Altar at Clan Wars
		case 409:
			action = new RestorePrayerPointsAction(player, objectId);
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
		
			default:
				if (action == null) {
					player.getActionSender().sendMessage("Nothing interesting happens.");
				}
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
	public static void handleSecondClickAction(Player player, Location position, int id) {
		if (player.inDebugMode()) {
			player.getActionSender().sendMessage(String.format("[ObjectInteraction option 2] - position: %s object: %d ", position, id));
		}

		AnyRevObjectDefinition objectDef = AnyRevObjectDefinition.get(id);
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
		if (player.inDebugMode()) {
			player.getActionSender().sendMessage(String.format("[ObjectInteraction option 3] - position: %s object: %d ", position, id));
		}
	}

}
