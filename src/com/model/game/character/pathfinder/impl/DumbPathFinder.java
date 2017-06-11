package com.model.game.character.pathfinder.impl;

import com.model.game.character.Entity;
import com.model.utility.cache.map.Tile;

public class DumbPathFinder {
	
	
	public static void generateMovement(Entity entity) {
		Tile loc = entity.getPosition();
		int dir = -1;
		if (!RegionManager.blockedNorth(loc, entity)) {
			dir = 0;
		} else if (!RegionManager.blockedEast(loc, entity)) {
			dir = 4;
		} else if (!RegionManager.blockedSouth(loc, entity)) {
			dir = 8;
		} else if (!RegionManager.blockedWest(loc, entity)) {
			dir = 12;
		}
		int random = RandomGenerator.nextInt(3);
		boolean found = false;
		if (random == 0) {
			if (!RegionManager.blockedNorth(loc, entity)) {
				entity.doPath(new SizedPathFinder(), entity,
						loc.getX(), loc.getY() + 1);
				found = true;
			}
		} else if (random == 1) {
			if (!RegionManager.blockedEast(loc, entity)) {
				entity.doPath(new SizedPathFinder(), entity,
						loc.getX() + 1, loc.getY());
				found = true;
			}
		} else if (random == 2) {
			if (!RegionManager.blockedSouth(loc, entity)) {
				entity.doPath(new SizedPathFinder(), entity,
						loc.getX(), loc.getY() - 1);
				found = true;
			}
		} else if (random == 3) {
			if (!RegionManager.blockedWest(loc, entity)) {
				entity.doPath(new SizedPathFinder(), entity,
						loc.getX() - 1, loc.getY());
				found = true;
			}
		}
		if (!found) {
			if (dir == 0) {
				entity.doPath(new SizedPathFinder(), entity,
						loc.getX(), loc.getY() + 1);
			} else if (dir == 4) {
				entity.doPath(new SizedPathFinder(), entity,
						loc.getX() + 1, loc.getY());
			} else if (dir == 8) {
				entity.doPath(new SizedPathFinder(), entity,
						loc.getX(), loc.getY() - 1);
			} else if (dir == 12) {
				entity.doPath(new SizedPathFinder(), entity,
						loc.getX() - 1, loc.getY());
			}
		}
	}

	
}
