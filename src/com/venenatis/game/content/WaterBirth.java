package com.venenatis.game.content;

import java.util.Collection;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.pathfinder.clipmap.Region;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;
import com.venenatis.server.Server;

public class WaterBirth {

	
	// 1910, 4367, 0
	
	
	//room 1799 4406 3
	//1809 4405 2
	//room 1825 4404 3
	//room 1812 4394 2
	//room 1809 4394 1
	//room 1799 4385 2
	//1798 4382 1
	//1802 4370 2 //another room
	// 1827, 4362, 1  //room
	// 1863, 4373, 2  //ladder up
	// 1864, 4389, 1 // ladder down
	// 1890, 4407, 0 rock lobsters
	// 2900, 4449, 0 main lair
	
	public static boolean action(Player player, GameObject objId) {
		
		if(objId.getId() == 8960 || objId.getId() == 8959 || objId.getId() == 8958) {
		final Collection<Player> localPlayers = RegionStoreManager.get().getLocalPlayers(player);
		for(final Player near : localPlayers) {
			if(player.getLocation().matches(2490, 10132) && near.getLocation().matches(2490, 10130)
					|| player.getLocation().matches(2490, 10130) && near.getLocation().matches(2490, 10132)
					|| player.getLocation().matches(2490, 10146) && near.getLocation().matches(2490, 10148)
					|| player.getLocation().matches(2490, 10148) && near.getLocation().matches(2490, 10146)
					|| player.getLocation().matches(2490, 10162) && near.getLocation().matches(2490, 10164)
					|| player.getLocation().matches(2490, 10164) && near.getLocation().matches(2490, 10162)) {
				
				
					Server.getGlobalObjects().replaceObject (objId, new GameObject(8963, objId.getLocation()), 10);		
				} else { 
					player.getActionSender().sendMessage("You need a second person or a pet rock");
				
			}
		}
	}
		switch(objId.getId()) {
		
		case 8929:
			player.setTeleportTarget(new Location(2442, 10147, 0));
			return true;
		case 10177: // enter first cave
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1798, 4407, 3));
			return true;	
		case 10193: // go back to the first big room to waterbirth
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(2546, 10143, 0));
			return true;
			
		case 10196: //ladder up to first room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1807, 4405, 3));
			return true;
		case 10195: // ladder down to second room
			player.setTeleportTarget(new Location(1809, 4405, 2));
			player.playAnimation(new Animation(828));
			return true;
		case 10198: // up to 3rd room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1825, 4404, 3));
			return true;
		case 10197: //down to second room from 3rd room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1823, 4404, 2));
		return true;
		case 10199: // ladder down to 4th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1834, 4388, 2));
			return true;
		case 10200: // ladder up to 3rd room from 4th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1834, 4390, 3));
			return true;
		case 10201: // ladder down to 5th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1811, 4394, 1));
			return true;
		case 10202: // ladder up to 4th room from 5th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1812, 4394, 2));
			return true;
		case 10203: // ladder up to 6th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1799, 4386, 2));
			return true;
		case 10204: // ladder down to 5th room from 6th
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1799, 4388, 1));
			return true;
		case 10205: // ladder up to 7th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1797, 4382, 1));
			return true;
		case 10206: // ladder up to 7th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1796, 4382, 2));
			return true;
		case 10207: // ladder up to 8th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1800, 4369, 2));
			return true;	
		case 10208: // ladder down to 7th room from 8th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1802, 4369, 1));
			return true;	
		case 10209: // ladder down to 9th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1827, 4362, 1));
			return true;	
		case 10210: // ladder from 10th to 9th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1825, 4362, 2));
			return true;
		case 10211: // ladder to 11th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1863, 4373, 2));
			return true;
		case 10212: // ladder to 10th room from 11th
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1863, 4370, 1));
			return true;
		case 10213: // ladder to 121th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1864, 4389, 1));
			return true;
		case 10214: // ladder to 11th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1864, 4388, 2));
			return true;
		case 10215: // ladder to 11th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1890, 4408, 0));
			return true;
		case 10216: // ladder to 11th room
			player.playAnimation(new Animation(828));
			player.setTeleportTarget(new Location(1889, 4407, 1));
			return true;
			
		
		}
		return false;
	}
	
	
	
}
