package com.venenatis.game.world.pathfinder.impl;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.world.object.GameObject;
import com.venenatis.game.world.pathfinder.Directions;
import com.venenatis.game.world.pathfinder.clipmap.ProjectileClipping;
import com.venenatis.game.world.pathfinder.clipmap.Region;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;

public class ProjectilePathFinder {
	
	public static boolean projectileBlocked(Location pos, Location pos2) {
		if (pos == null || pos2 == null) {
			return true;
		}
		int z = pos.getZ();
		
		double offsetX = Math.abs(pos.getX() - pos2.getX());
		double offsetY = Math.abs(pos.getY() - pos2.getY());
		int xDis = Math.abs(pos.getX() - pos2.getX());
		int yDis = Math.abs(pos.getY() - pos2.getY());
		int distance =  xDis > yDis ? xDis : yDis;
		if (distance == 0) {
			return true;
		}
		offsetX = offsetX > 0 ? offsetX / distance : 0;
		offsetY = offsetY > 0 ? offsetY / distance : 0;
		int[][] path = new int[distance][5];
		int curX = pos.getX();
		int curY = pos.getY();
		int next = 0;
		int nextMoveX = 0;
		int nextMoveY = 0;
		double currentTileXCount = 0.0;
		double currentTileYCount = 0.0;
		while(distance > 0) {
			distance--;
			nextMoveX = 0;
			nextMoveY = 0;
			if (curX > pos2.getX()) {
				currentTileXCount += offsetX;
				if (currentTileXCount >= 1.0) {
					nextMoveX--;
					curX--;	
					currentTileXCount -= offsetX;
				}		
			} else if (curX < pos2.getX()) {
				currentTileXCount += offsetX;
				if (currentTileXCount >= 1.0) {
					nextMoveX++;
					curX++;
					currentTileXCount -= offsetX;
				}
			}
			if (curY > pos2.getY()) {
				currentTileYCount += offsetY;
				if (currentTileYCount >= 1.0) {
					nextMoveY--;
					curY--;	
					currentTileYCount -= offsetY;
				}	
			} else if (curY < pos2.getY()) {
				currentTileYCount += offsetY;
				if (currentTileYCount >= 1.0) {
					nextMoveY++;
					curY++;
					currentTileYCount -= offsetY;
				}
			}
			path[next][0] = curX;
			path[next][1] = curY;
			path[next][2] = z;
			path[next][3] = nextMoveX;
			path[next][4] = nextMoveY;
			next++;	
		}
		for (int i = 0; i < path.length; i++) {
			if (!ProjectileClipping.getClippingMask(path[i][0], path[i][1], z, path[i][3], path[i][4], false)) {
				return true;
			}
		}
		return false;
	}

	
	public static boolean hasLineOfSight(Entity entity, Entity target) {
		int z = entity.getCoverage().center().getZ();
		Location start_loc = Location.create(entity.getCoverage().center().getX(), entity.getCoverage().center().getY(), z);
		Location end_loc = Location.create(target.getCoverage().center().getX(), target.getCoverage().center().getY(), z);
		Location currentTile = start_loc;
    	Directions.NormalDirection localDirection = null;
    	Directions.NormalDirection localDirectionInverse = null;
    	boolean projectileCheck = entity.getCombatType() == CombatStyle.MELEE;
    	while (currentTile != end_loc) {
    		Directions.NormalDirection globalDirection = Directions.directionFor(currentTile, end_loc);
    		if (globalDirection == null) {
    			return true;
    		}
			Location nextTile = currentTile.transform(Directions.DIRECTION_DELTA_X[globalDirection.intValue()], Directions.DIRECTION_DELTA_Y[globalDirection.intValue()], 0);
    		localDirection = Directions.directionFor(currentTile, nextTile);	
    		localDirectionInverse = Directions.directionFor(nextTile, currentTile);
    		GameObject currentObject = RegionStoreManager.get().getWallObject(currentTile);
    		GameObject nextObject = RegionStoreManager.get().getWallObject(nextTile);
    		if (projectileCheck) {
    			if (currentObject != null && !currentObject.getDefinition().rangableObject()) {
        			if (nextObject != null && !nextObject.getDefinition().rangableObject()) {
    					if (!currentTile.canMove(localDirection, 1, false) || !nextTile.canMove(localDirectionInverse, 1, false))
    						break;
        			} else {
        				if (!currentTile.canMove(localDirection, 1, true) || !nextTile.canMove(localDirectionInverse, 1, false)) 
    	    				break;
        			}
        		} else if (nextObject != null) {
        			if (!currentTile.canMove(localDirection, 1, false) || !nextTile.canMove(localDirectionInverse, 1, false))
    					break;
        		}
        		if (currentTile.canMove(localDirection, 1, false) && currentTile.canMove(localDirectionInverse, 1, false)) {
        			currentTile = nextTile;
        			continue;
        		} else {
        			boolean solid = (Region.getClippingMask(nextTile.getX(), nextTile.getY(), nextTile.getZ()) & 0x20000) != 0;
        			boolean solid2 = (Region.getClippingMask(currentTile.getX(), currentTile.getY(), currentTile.getZ()) & 0x20000) != 0;
        			if (!solid && !solid2) {
        				currentTile = nextTile;
        				continue;
        			} else 
        				break;
        		}
    		} else {
    			if (currentObject != null || nextObject != null) {
        			if (!currentTile.canMove(localDirection, 1, false) || !nextTile.canMove(localDirectionInverse, 1, false)) 
    	    				break;
        		}
        		if (currentTile.canMove(localDirection, 1, false) && currentTile.canMove(localDirectionInverse, 1, false)) {
        			currentTile = nextTile;
        			continue;
        		} else {
        			boolean solid = (Region.getClippingMask(nextTile.getX(), nextTile.getY(), nextTile.getZ()) & 0x20000) != 0;
        			boolean solid2 = (Region.getClippingMask(currentTile.getX(), currentTile.getY(), currentTile.getZ()) & 0x20000) != 0;
        			if (!solid && !solid2) {
        				currentTile = nextTile;
        				continue;
        			} else {
        				break;
        			}
        		}
    		}
    	}
    	return currentTile == end_loc;
    }
}
