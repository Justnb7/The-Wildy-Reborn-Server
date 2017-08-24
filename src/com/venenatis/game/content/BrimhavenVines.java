package com.venenatis.game.content;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.object.impl.vines.Vine;
import com.venenatis.server.Server;

/**
 * 
 * @author Patrick van Elderen
 *
 */
public class BrimhavenVines {
	
	private static Map<Location, Vine> vines = new HashMap<>();
	
	public static boolean chop(Player player, GameObject obj) {
		if (obj == null) {
			return false;
		}
		final Vine vine = vines.get(obj.getLocation());
		if (vine == null) {
			return false;
		}
		
		boolean success = Utility.random(4) < 2;
		Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		if (weapon == null) {
			return false;
		}
		String name = weapon.getDefinition().getName().toLowerCase();
		boolean hasAxe = name.contains("axe");

		Optional<Item> containsAxe = Arrays.stream(player.getInventory().getItems()).filter(Objects::nonNull).filter(i -> hasAxe(i.getDefinition().getName())).findAny();

		if (!hasAxe && containsAxe.isPresent()) {
			hasAxe = true;
		}

		if (!hasAxe) {
			return false;
		}
		
		player.getActionSender().sendMessage("You attempt to chop down the vines.");
		
		player.playAnimation(Animation.create(2846));
		if (success) {
			World.getWorld().schedule(new Task(1) {

				@Override
				public void execute() {
					this.stop();
					Server.getGlobalObjects().replaceObject(obj, new GameObject(21730, obj.getX(), obj.getY(), obj.getZ(), obj.getDirection()), 12);
					player.getActionSender().sendMessage("You successfully chop down the vines.");
				}
			});
		}
		return true;
	}
	
	private static boolean hasAxe(String name) {
        if (name == null) {
            return false;
        }
		name = name.toLowerCase();
		return name.contains("axe");
	}
	
	static {
		vines.put(Location.create(2690,9564), new Vine(21731, 1));
	}

	public static boolean handleBrimhavenVines(Player player, int objectType) {
		switch (objectType) {
		case 12987:
		case 12986:
			BrimhavenVines.moveThroughVinesX(player, 3213, -2, 0, 2, 0);
			return true;
		case 21731:
			BrimhavenVines.moveThroughVinesX(player, 2689, 2, 0, -2, 0);
			return true;
		case 21732:
			BrimhavenVines.moveThroughVinesY(player, 9568, 0, 2, 0, -2);
			return true;
		case 21733:
			BrimhavenVines.moveThroughVinesX(player, 2672, 2, 0, -2, 0);
			return true;
		case 21734:
			BrimhavenVines.moveThroughVinesX(player, 2675, 2, 0, -2, 0);
			return true;
		case 21735:
			BrimhavenVines.moveThroughVinesX(player, 2694, 2, 0, -2, 0);
			return true;
		}
		return false;
	}

	public static void moveThroughVinesX(Player player, int originX, int x1, int y1, int x2, int y2) {
		if (player.getX() <= originX) {
			player.getPlayerFollowing().walkTo(x1, y1);
		} else {
			player.getPlayerFollowing().walkTo(x2, y2);
		}
	}

	public static void moveThroughVinesY(Player player, int originY, int x1, int y1, int x2, int y2) {
		if (player.getY() <= originY) {
			player.getPlayerFollowing().walkTo(x1, y1);
		} else {
			player.getPlayerFollowing().walkTo(x2, y2);
		}
	}

}
