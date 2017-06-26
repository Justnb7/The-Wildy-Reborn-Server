package com.venenatis.game.model.entity.player.controller;

import com.venenatis.game.location.Area;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.controller.impl.DefaultController;
import com.venenatis.game.model.entity.player.controller.impl.DuelArenaController;
import com.venenatis.game.model.entity.player.controller.impl.WildernessController;

/**
 * Handles controllers for specific areas to moderate a players gameplay
 * 
 * @author Mobster
 * @author Mikey`
 *
 */
public class ControllerManager {

	/**
	 * The default controller which is handled when the player has no other
	 * active controller
	 */
	public static final DefaultController DEFAULT_CONTROLLER = new DefaultController();

	/**
	 * The controller for the wilderness
	 */
	private static final WildernessController WILDERNESS_CONTROLLER = new WildernessController();
	
	/**
	 * The controller for the duel arena
	 */
	private static final DuelArenaController DUEL_ARENA_CONTROLLER = new DuelArenaController();

	/**
	 * Sets the controller when a player takes a step or teleports
	 * 
	 * @param player
	 *            The {@link Player} to set the controller for
	 */
	public static void setControllerOnWalk(Player player) {
		try {
			if ((player.getController() != null) && (!player.getController().transitionOnWalk(player))) {
				return;
			}

			Controller controller = DEFAULT_CONTROLLER;
			if (Area.inWilderness(player)) {
				controller = WILDERNESS_CONTROLLER;
			} else if(Area.inDuelArena(player)) {
				controller = DUEL_ARENA_CONTROLLER;
			}
			if ((controller == null) || (player.getController() == null) || (!player.getController().equals(controller))) {
				player.getController().onControllerLeave(player);
				player.setController(controller);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the controller when the player is walking
	 * 
	 * @param player
	 */
	public static void updateControllerOnWalk(Player player) {
		if (player.getController() == null) {
			return;
		}

		player.getController().onWalk(player);
	}

	public static void onForceLogout(Player player) {

	}

}
