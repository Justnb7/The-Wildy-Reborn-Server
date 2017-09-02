package com.venenatis.game.content.teleportation.lever;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.server.Server;

/**
 * Levers are the objects that exist in the game that aid player teleportation.
 * 
 * @author Patrick van Elderen
 * @date Aug 11, 2017, 14:00:00 PM
 */
public class Levers {

	private static Map<Location, Lever> LEVERS = new HashMap<Location, Lever>();
	private static final Animation LEVER_ANIMATION = Animation.create(2140);

	public static boolean handle(final Player player, GameObject obj) {
		if (obj == null) {
			return false;
		}
		final Lever lever = LEVERS.get(obj.getLocation());
		if (lever == null) {
			return false;
		}
		if (!player.getLocation().isNextTo(obj.getLocation())) {
			return false;
		}
		/*
		 * Prevents mass clicking them.
		 */
		if (player.getTeleportAction().getLastTeleport() < 3000 || player.getCombatState().isTeleblocked()) {
			return true;
		}
		player.playAnimation(LEVER_ANIMATION);
		player.setAttribute("busy", true);
		player.getTeleportAction().setLastTeleport(System.currentTimeMillis());
		if (obj.getId() == 1817 || obj.getId() == 5959 || obj.getId() == 5960 || obj.getId() == 26761
				|| obj.getId() == 1815 || obj.getId() == 1814 || obj.getId() == 1816) {
			World.getWorld().schedule(new Task(1) {

				@Override
				public void execute() {
					this.stop();
					Server.getGlobalObjects().replaceObject(obj,
							new GameObject(obj.getLocation(), 5961, obj.getType(), obj.getDirection()), 7);
				}

			});
		}
		World.getWorld().schedule(new Task(2) {

			@Override
			public void execute() {
				player.playAnimation(Animation.create(714));
				player.playGraphic(Graphic.create(308, 48, 100));
				World.getWorld().schedule(new Task(3) {

					@Override
					public void execute() {
						player.setTeleportTarget(lever.getTargetLocation());
						player.playAnimation(Animation.create(-1));
						player.removeAttribute("busy");
						this.stop();
					}

				});
				this.stop();
			}

		});
		return true;
	}

	/**
	 * This populates the map.
	 */
	static {
		/*
		 * King Black Dragon levers.
		 */
		LEVERS.put(Location.create(3067, 10253, 0), new Lever(Location.create(2271, 4680, 0), 3, 3));
		LEVERS.put(Location.create(2271, 4680, 0), new Lever(Location.create(3067, 10253, 0), 3, 3));

		/*
		 * Edgeville -> Wild
		 */
		LEVERS.put(Location.create(3090, 3475, 0), new Lever(Location.create(3153, 3923, 0), 0, 0));
		/*
		 * Ardougne -> Wild
		 */
		LEVERS.put(Location.create(2561, 3311, 0), new Lever(Location.create(3153, 3923, 0), 0, 0));
		/*
		 * Wilderness -> Ardougne
		 */
		LEVERS.put(Location.create(3153, 3923, 0), new Lever(Location.create(2561, 3311, 0), 0, 0));

		/*
		 * Wild -> Magebank
		 */
		LEVERS.put(Location.create(3090, 3956, 0), new Lever(Location.create(2539, 4712, 0), 0, 0));

		/*
		 * Magebank -> Wild
		 */
		LEVERS.put(Location.create(2539, 4712, 0), new Lever(Location.create(3090, 3956, 0), 0, 0));

		/*
		 * Wilderness -> Mage Arena
		 */
		LEVERS.put(Location.create(3104, 3956, 0), new Lever(Location.create(3105, 3951, 0), 0, 0));

		/*
		 * Mage Arena -> Wilderness
		 */
		LEVERS.put(Location.create(3105, 3952, 0), new Lever(Location.create(3105, 3956, 0), 0, 0));
	}

}
