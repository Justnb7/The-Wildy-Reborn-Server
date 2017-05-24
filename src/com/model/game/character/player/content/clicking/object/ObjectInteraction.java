package com.model.game.character.player.content.clicking.object;

import com.model.action.Action;
import com.model.action.impl.actions.RestorePrayerPointsAction;
import com.model.game.character.player.Player;
import com.model.game.character.player.skill.agility.Agility;
import com.model.game.character.player.skill.thieving.Stalls;
import com.model.game.character.player.skill.woodcutting.Woodcutting;
import com.model.game.character.player.skill.woodcutting.Woodcutting.Tree;
import com.model.game.location.Location;
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
	 * @param objectId
	 *            The object
	 */
	public static void handleFirstClickAction(Player player, Location position, int objectId) {
		if (player.inDebugMode()) {
			System.out.println(String.format("[ObjectInteraction] - position: %s object: %d ", position, objectId));
		}
		
		Action action = null;
		Tree tree = Tree.forId(objectId);
		
		if (tree != null) {
			//TODO obj action idk how to do it....
			action = new Woodcutting(player, null);
		}
		
		switch(objectId) {
		case 7812: //Altar at Clan Wars
			action = new RestorePrayerPointsAction(player, objectId);
			break;
		case 26707: //Clan wars bank chest
			player.getBank().open();
			break;
		case 26642: //clan wars portal
		case 26644:
			player.getActionSender().sendMessage("Coming soon...");
			break;
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
