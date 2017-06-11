package com.model.game.character.pathfinder.impl;

import com.model.game.character.Entity;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.pathfinder.Directions;
import com.model.game.character.pathfinder.region.ProjectileClipping;
import com.model.game.character.pathfinder.region.RegionManager;
import com.model.game.object.GameObject;
import com.model.utility.cache.map.Tile;

public class ProjectilePathFinder {
	
	public static boolean projectileBlocked(Tile pos, Tile pos2) {
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
		Tile start_loc = Tile.create(entity.getCoverage().center().getX(), entity.getCoverage().center().getY(), z);
		Tile end_loc = Tile.create(target.getCoverage().center().getX(), target.getCoverage().center().getY(), z);
		Tile currentTile = start_loc;
    	Directions.NormalDirection localDirection = null;
    	Directions.NormalDirection localDirectionInverse = null;
    	boolean projectileCheck = entity.getCombatType() == CombatStyle.MELEE;
    	while (currentTile != end_loc) {
    		Directions.NormalDirection globalDirection = Directions.directionFor(currentTile, end_loc);
    		if (globalDirection == null) {
    			return true;
    		}
			Tile nextTile = currentTile.transform(Directions.DIRECTION_DELTA_X[globalDirection.intValue()], Directions.DIRECTION_DELTA_Y[globalDirection.intValue()], 0);
    		localDirection = Directions.directionFor(currentTile, nextTile);	
    		localDirectionInverse = Directions.directionFor(nextTile, currentTile);
    		GameObject currentObject = RegionManager.get().getWallObject(currentTile);
    		GameObject nextObject = RegionManager.get().getWallObject(nextTile);
    		if (projectileCheck) {
    			if (currentObject != null && !currentObject.cacheDef().rangableObject()) {
        			if (nextObject != null && !nextObject.cacheDef().rangableObject()) {
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
        			boolean solid = (RegionManager.get().getClippingMask(nextTile.getX(), nextTile.getY(), nextTile.getZ()) & 0x20000) != 0;
        			boolean solid2 = (RegionManager.get().getClippingMask(currentTile.getX(), currentTile.getY(), currentTile.getZ()) & 0x20000) != 0;
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
        			boolean solid = (RegionManager.get().getClippingMask(nextTile.getX(), nextTile.getY(), nextTile.getZ()) & 0x20000) != 0;
        			boolean solid2 = (RegionManager.get().getClippingMask(currentTile.getX(), currentTile.getY(), currentTile.getZ()) & 0x20000) != 0;
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
