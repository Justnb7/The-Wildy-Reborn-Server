package com.venenatis.game.content;

import java.util.HashMap;
import java.util.Map;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.object.GameObject;

public class MageArenaGodPrayer {
	
	static Map<Integer, Integer> contains = new HashMap<>();
	
	
	public static boolean godPrayer(Player player, GameObject obj) {
		if(obj == null)
           return false;
		if (!contains.containsKey(obj.getId())) {
			return false;
		}
		GroundItem groundItem = new GroundItem(new Item(contains.get(obj.getId())), player.getLocation(), player);
		System.out.println(contains.get(obj.getId()));
		if (containsGodCape(player) || player.hasAttribute("droppedGodCape")) {
			player.getActionSender().sendMessage("You already own a God Cape.");
			return false;
		}
		player.setAttribute("busy", true);
		player.playAnimation(Animation.create(645));
		
		World.getWorld().schedule(new Task(4) {

			@Override
			public void execute() {
				this.stop();
				player.removeAttribute("busy");
				player.getActionSender().sendGroundItem(groundItem);
			}
			
		});
		return true;
	}
	
	private static boolean containsGodCape(Player player) {
		boolean sara = player.getBank().contains(2412) || player.getInventory().contains(2412) || player.getEquipment().contains(2412);
		boolean guthix = player.getBank().contains(2413) || player.getInventory().contains(2413) || player.getEquipment().contains(2413);
		boolean zammy = player.getBank().contains(2414) || player.getInventory().contains(2414) || player.getEquipment().contains(2414);
		return sara || guthix || zammy;
	}
	
	static {
		contains.put(2873, 2412);
		
		contains.put(2874, 2414);
		
		contains.put(2875, 2413);
	}
}