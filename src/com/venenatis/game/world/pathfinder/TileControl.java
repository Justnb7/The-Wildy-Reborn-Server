package com.venenatis.game.world.pathfinder;

import java.util.HashMap;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.world.pathfinder.region.RegionStore;
import com.venenatis.game.world.pathfinder.region.RegionStoreManager;

/**
 * System to track if an entity covers a tile(s) depending on how large it is ex 1x1 (player) or 2x2 3x3 npc
 * Used to adjust movement; npcs can 'stack' such as npcs in fight caves which get stuck behind each other
 * This doesn't happen for players.
 * Entity based=)
 */
public class TileControl {
	
	private static TileControl singleton = null;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private HashMap<Entity, Location[]> occupiedLocations = new HashMap();

    public static TileControl getSingleton() {
        if (singleton == null) {
            singleton = new TileControl();
        }
        return singleton;
    }

    public static Location[] getHoveringTiles(Entity mob) {
        return getHoveringTiles(mob, mob.getLocation());
    }

    public static Location[] getHoveringTiles(Entity mob, Location location) {
        int buf = 0;
        int offset = 0;
        if (mob.isNPC()) {
        	buf = ((NPC) mob).getSize();
        }
        Location[] locations = new Location[buf * buf];
        if (locations.length == 1)
            locations[offset] = location;
        else {
            for (int x = 0; x < buf; x++) {
                for (int y = 0; y < buf; y++) {
                    locations[(offset++)] = Location.create(location.getX() + x, location.getY() + y, location.getZ());
                }
            }
        }
        return locations;
    }

    public static int calculateDistance(Entity mobA, Entity mobB) {
        Location[] pointsA = getHoveringTiles(mobA);
        Location[] pointsB = getHoveringTiles(mobB);
        int lowestCount = 16;
        int distance = 16;
        for (Location pointA : pointsA) {
            for (Location pointB : pointsB) {
                if (pointA.equals(pointB)) {
                    return 0;
                }
                distance = calculateDistance(pointA, pointB);
                if (distance < lowestCount) {
                    lowestCount = distance;
                }
            }
        }

        return lowestCount;
    }

    public static int calculateDistance(Location pointA, Location pointB) {
        int offsetX = Math.abs(pointA.getX() - pointB.getX());
        int offsetY = Math.abs(pointA.getY() - pointB.getY());
        return offsetX > offsetY ? offsetX : offsetY;
    }

    public Location[] getOccupiedLocations(Entity mob) {
        return this.occupiedLocations.get(mob);
    }

    public void setOccupiedLocation(Entity mob, Location[] locations) {
        if ((mob == null) || (locations == null))
            return;
        this.occupiedLocations.remove(mob); // good removes previous
        this.occupiedLocations.put(mob, locations);
    }

    public boolean locationOccupied(int x, int y, int z, int world) {
        Location location = Location.create(x, y, z);
        Location[] npcLocations = null;
    	 for (RegionStore r : RegionStoreManager.get().getSurroundingRegions(location)) {
    		 for (NPC npc : r.getNpcs()) {
    			 if (npc == null)
    				 continue;
    			 npcLocations = getOccupiedLocations(npc);
                 if (npcLocations != null) {
                	 for (Location locs : npcLocations) {
                		 if (locs.equals(location)) {
                             return true;
                         }
                	 }
                 }
    		 }
    	 }
    	return false;
    }

    /**
     *
     * @param locations
     * @param mob
     * @return
     */
    public boolean locationOccupied(Location[] locations, Entity mob) {
        if ((locations == null) || (mob == null))
            return true;
        Location[] npcLocations = null;
        for (RegionStore r : RegionStoreManager.get().getSurroundingRegions(mob.getLocation())) {
            for (NPC npc : r.getNpcs()) {
                if ((mob.isNPC()) && ((npc == null) || (npc == mob))) {
                    continue;
                }
                npcLocations = getOccupiedLocations(npc);
                if (npcLocations != null) {
                    for (Location loc : locations) {
                        for (Location loc2 : npcLocations) {
                            if (loc.equals(loc2)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
