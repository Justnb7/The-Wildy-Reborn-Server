package com.venenatis.game.world.object.impl.webs;

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
import com.venenatis.server.Server;

/**
 * Took this Slashing webs base from a random Hyperion and edited the code.
 * @author Patrick van Elderen And original owner, don't know who that is.
 *
 */
public class SlashWebObject {
	
	private static Map<Location, Webs> webList = new HashMap<>();
	
	/**
	 * Handles the slash web action for the player.
	 */
	public static boolean slash(Player player, GameObject obj) {
		if (obj == null) {
			return false;
		}
		final Webs web = webList.get(obj.getLocation());
		if (web == null) {
			return false;
		}
		if(player.getTeleportAction().getLastTeleport() < 3000) {
			return false;
		}
		boolean success = Utility.random(4) < 2;
		Item weapon = player.getEquipment().get(EquipmentConstants.WEAPON_SLOT);
		if (weapon == null) {
			return false;
		}
		String name = weapon.getDefinition().getName().toLowerCase();
		boolean requiredWep = name.contains("sword") || name.contains("axe") || name.contains("scimitar") || name.contains("dagger");

		Optional<Item> sharpItem = Arrays.stream(player.getInventory().getItems()).filter(Objects::nonNull).filter(i -> containsSharpObject(i.getDefinition().getName())).findAny();

		if (!requiredWep && sharpItem.isPresent()) {
			requiredWep = true;
		}

		if (!requiredWep) {
			return false;
		}

		player.getActionSender().sendMessage("You attempt to cut the web.");
		player.getTeleportAction().setLastTeleport(System.currentTimeMillis());
		player.playAnimation(Animation.create(451));
		if (success) {
			World.getWorld().schedule(new Task(1) {

				@Override
				public void execute() {
					this.stop();
					Server.getGlobalObjects().replaceObject(obj, new GameObject(734, obj.getX(), obj.getY(), obj.getZ(), obj.getDirection()), 30);
					player.getActionSender().sendMessage("You successfully cut the web.");
				}
			});
		}
		return true;
	}
	
	private static boolean containsSharpObject(String name) {
        if (name == null) {
            return false;
        }
		name = name.toLowerCase();
		return name.contains("knife") || name.contains("sword") || name.contains("axe") || name.contains("scimitar") || name.contains("dagger");
	}
	
	static {
		webList.put(Location.create(3158,3951), new Webs(733, 1));

		webList.put(Location.create(3095,3957), new Webs(733, 1));

		webList.put(Location.create(3092,3957), new Webs(733, 1));
		
		webList.put(Location.create(3105, 3958), new Webs(733, 3));
		
		webList.put(Location.create(3106, 3958), new Webs(733, 3));

		webList.put(Location.create(3210, 9898), new Webs(733, 1));
	}

}
