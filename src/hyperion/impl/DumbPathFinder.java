package hyperion.impl;

import com.model.game.character.Entity;
import com.model.game.location.Location;
import com.model.utility.Utility;
import hyperion.PathFinder;
import hyperion.region.RegionStoreManager;

public class DumbPathFinder {
	
	
	public static void generateMovement(Entity entity) {
		Location loc = entity.getPosition();
		int dir = -1;
		if (!RegionStoreManager.get().blockedNorth(loc, entity)) {
			dir = 0;
		} else if (!RegionStoreManager.get().blockedEast(loc, entity)) {
			dir = 4;
		} else if (!RegionStoreManager.get().blockedSouth(loc, entity)) {
			dir = 8;
		} else if (!RegionStoreManager.get().blockedWest(loc, entity)) {
			dir = 12;
		}
		int random = Utility.random(3);
		boolean found = false;
		if (random == 0) {
			if (!RegionStoreManager.get().blockedNorth(loc, entity)) {
				PathFinder.doPath(new SizedPathFinder(), entity,
						loc.getX(), loc.getY() + 1);
				found = true;
			}
		} else if (random == 1) {
			if (!RegionStoreManager.get().blockedEast(loc, entity)) {
				PathFinder.doPath(new SizedPathFinder(), entity,
						loc.getX() + 1, loc.getY());
				found = true;
			}
		} else if (random == 2) {
			if (!RegionStoreManager.get().blockedSouth(loc, entity)) {
				PathFinder.doPath(new SizedPathFinder(), entity,
						loc.getX(), loc.getY() - 1);
				found = true;
			}
		} else if (random == 3) {
			if (!RegionStoreManager.get().blockedWest(loc, entity)) {
				PathFinder.doPath(new SizedPathFinder(), entity,
						loc.getX() - 1, loc.getY());
				found = true;
			}
		}
		if (!found) {
			if (dir == 0) {
				PathFinder.doPath(new SizedPathFinder(), entity,
						loc.getX(), loc.getY() + 1);
			} else if (dir == 4) {
				PathFinder.doPath(new SizedPathFinder(), entity,
						loc.getX() + 1, loc.getY());
			} else if (dir == 8) {
				PathFinder.doPath(new SizedPathFinder(), entity,
						loc.getX(), loc.getY() - 1);
			} else if (dir == 12) {
				PathFinder.doPath(new SizedPathFinder(), entity,
						loc.getX() - 1, loc.getY());
			}
		}
	}

	
}
