package com.venenatis.game.content.minigames.singleplayer.barrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.World;
import com.venenatis.game.world.object.GameObject;

/**
 * Handles barrows doors
 * 
 * @author Stan
 * 
 */

public enum BarrowsDoors {
		
	NORTH_WEST_ROOM_NORTH(new Location(3534, 9718, 0), 0, false),
	NORTH_WEST_ROOM_EAST(new Location(3541, 9711, 0), 1, false),
	NORTH_WEST_ROOM_SOUTH(new Location(3534, 9705, 0), 0, false),
	NORTH_WEST_ROOM_WEST(new Location(3528, 9711, 0), 1, false),
	
	NORTH_ROOM_EAST(new Location(3558, 9711, 0), 1, false),
	NORTH_ROOM_SOUTH(new Location(3551, 9705, 0), 0, true),
	NORTH_ROOM_WEST(new Location(3545, 9711, 0), 1, false),
	
	NORTH_EAST_ROOM_NORTH(new Location(3568, 9718, 0), 0, false),
	NORTH_EAST_ROOM_EAST(new Location(3575, 9711, 0), 1, false),
	NORTH_EAST_ROOM_SOUTH(new Location(3568, 9705, 0), 0, false),
	NORTH_EAST_ROOM_WEST(new Location(3562, 9711, 0), 1, false),		
	
	WEST_ROOM_NORTH(new Location(3534, 9701, 0), 0, false),
	WEST_ROOM_EAST(new Location(3541, 9694, 0), 1, true),
	WEST_ROOM_SOUTH(new Location(3534, 9688, 0), 0, false),
	
	CHEST_ROOM_NORTH(new Location(3551, 9701, 0), 0, false),
	CHEST_ROOM_EAST(new Location(3558, 9694, 0), 1, false),
	CHEST_ROOM_SOUTH(new Location(3551, 9688, 0), 0, false),
	CHEST_ROOM_WEST(new Location(3545, 9694, 0), 1, false),
	
	EAST_ROOM_NORTH(new Location(3568, 9701, 0), 0, false),
	EAST_ROOM_SOUTH(new Location(3568, 9688, 0), 0, false),
	EAST_ROOM_WEST(new Location(3562, 9694, 0), 1, true),				
		
	SOUTH_WEST_ROOM_NORTH(new Location(3534, 9684, 0), 0, false),
	SOUTH_WEST_ROOM_EAST(new Location(3541, 9677, 0), 1, false),
	SOUTH_WEST_ROOM_SOUTH(new Location(3534, 9671, 0), 0, false),
	SOUTH_WEST_ROOM_WEST(new Location(3528, 9677, 0), 1, false),
	
	SOUTH_ROOM_NORTH(new Location(3551, 9684, 0), 0, true),
	SOUTH_ROOM_EAST(new Location(3558, 9677, 0), 1, false),
	SOUTH_ROOM_WEST(new Location(3545, 9677, 0), 1, false),
	
	SOUTH_EAST_ROOM_NORTH(new Location(3568, 9684, 0), 0, false),
	SOUTH_EAST_ROOM_EAST(new Location(3575, 9677, 0), 1, false),
	SOUTH_EAST_ROOM_SOUTH(new Location(3568, 9671, 0), 0, false),
	SOUTH_EAST_ROOM_WEST(new Location(3562, 9677, 0), 1, false);
	
	
	/**
	 * The identifier of the door of the two with the lowest coordinates
	 */
	private final Location doorLocation;
	
	/**
	 * The orientation of the doors
	 * 0: Horizontal
	 * 1: Vertical
	 */
	private final int orientation;
	
	/**
	 * Determines if the door is a puzzle door
	 */
	private final boolean puzzle;
	
	
	private BarrowsDoors(final Location doorLocation, final int orientation, final boolean puzzle) {
		this.doorLocation = doorLocation;
		this.orientation = orientation;
		this.puzzle = puzzle;
	}
	
	private static final HashMap<String, BarrowsDoors> doorsMap = new HashMap<String, BarrowsDoors>();
	
	private static final Location chestLocation = new Location(3551, 9695);
	
	static {
		for (BarrowsDoors def : values()) {
			doorsMap.put(def.doorLocation.toString(), def);
			doorsMap.put(new Location(def.doorLocation.getX() + (def.orientation == 0 ? 1 : 0), def.doorLocation.getY() + (def.orientation == 1 ? 1 : 0), 0).toString(), def);
		}
	}
	
	/**
	 * Handles the click on a barrows door
	 */
	public static boolean handleDoor(Player player, GameObject object) {
		BarrowsDoors def = doorsMap.get(object.getLocation().toString());
		if (def == null) {
			return false;
		}
		
		int objectX = object.getLocation().getX();
		int objectY = object.getLocation().getY();
		int playerX = player.getLocation().getX();
		int playerY = player.getLocation().getY();
		
		if (def.orientation == 0) {
			playerX = objectX;
			if (object.getDirection() == 1) {
				playerY = objectY + (playerY == objectY ? 1 : 0);
			} else {
				playerY = objectY + (playerY < objectY ? 0 : -1);
			}
		} else {
			playerY = objectY;
			if (object.getDirection() == 2) {
				playerX = objectX + (playerX == objectX ? 1 : 0);
			} else {
				playerX = objectX + (playerX < objectX ? 0 : -1);
			}
		}
		
		if (def.puzzle && new Location(playerX, playerY).distanceToPoint(chestLocation) < player.getLocation().distanceToPoint(chestLocation)) {
			player.setPuzzleLocation(new Location(playerX, playerY));
			BarrowsHandler.getSingleton().openPuzzle(player);
			return true;
		}
		
		if (player.getBarrowsDetails().getSpawnedBrother() > 0) {
			if (World.getWorld().getNPCs().get(player.getBarrowsDetails().getSpawnedBrother()) != null) {
				World.getWorld().getNPCs().get(player.getBarrowsDetails().getSpawnedBrother()).remove();
			}
			player.getBarrowsDetails().setSpawnedBrother(-1);
		}
		
		player.setLocation(new Location(playerX, playerY));
		player.setTeleportTarget((new Location(playerX, playerY)));
		
		Random random = new Random();
		
		if (random.nextInt(15) == 0) {
			List<Integer> killedBrothers = new ArrayList<>();
			boolean[] brothers = player.getBarrowsDetails().getBrothersKilled();
			for (int i = 0; i < 6; i++) {
				if (!brothers[i]) killedBrothers.add(i);
			}
			int brotherId = killedBrothers.get(random.nextInt(killedBrothers.size()));
			player.getBarrowsDetails().setSpawnedBrother(
				NPC.spawnNpc(player, BarrowsInformation.forBrotherIdentifier(brotherId).get().getNpcId(), player.getLocation(), 0, true, true).getIndex()
			);
		}
		
		return true;		
	}
	
}