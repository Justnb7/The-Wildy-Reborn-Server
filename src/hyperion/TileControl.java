package hyperion;

import com.model.game.character.Entity;
import com.model.game.character.npc.NPC;
import hyperion.region.RegionStore;
import hyperion.region.RegionManager;
import clipmap.Tile;

import java.util.HashMap;

public class TileControl {
	
	private static TileControl singleton = null;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private HashMap<Entity, Tile[]> occupiedLocations = new HashMap();

    public static TileControl getSingleton() {
        if (singleton == null) {
            singleton = new TileControl();
        }
        return singleton;
    }

    public static Tile[] getHoveringTiles(Entity mob) {
        return getHoveringTiles(mob, mob.getPosition());
    }

    public static Tile[] getHoveringTiles(Entity mob, Tile location) {
        int buf = 0;
        int offset = 0;
        if (mob.isNPC()) {
        	buf = ((NPC) mob).getSize();
        }
        Tile[] locations = new Tile[buf * buf];
        if (locations.length == 1)
            locations[offset] = location;
        else {
            for (int x = 0; x < buf; x++) {
                for (int y = 0; y < buf; y++) {
                    locations[(offset++)] = Tile.create(location.getX() + x, location.getY() + y, location.getZ());
                }
            }
        }
        return locations;
    }

    public static int calculateDistance(Entity mobA, Entity mobB) {
        Tile[] pointsA = getHoveringTiles(mobA);
        Tile[] pointsB = getHoveringTiles(mobB);
        int lowestCount = 16;
        int distance = 16;
        for (Tile pointA : pointsA) {
            for (Tile pointB : pointsB) {
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

    public static int calculateDistance(Tile pointA, Tile pointB) {
        int offsetX = Math.abs(pointA.getX() - pointB.getX());
        int offsetY = Math.abs(pointA.getY() - pointB.getY());
        return offsetX > offsetY ? offsetX : offsetY;
    }

    public Tile[] getOccupiedLocations(Entity mob) {
        return this.occupiedLocations.get(mob);
    }

    public void setOccupiedLocation(Entity mob, Tile[] locations) {
        if ((mob == null) || (locations == null))
            return;
        this.occupiedLocations.remove(mob);
        this.occupiedLocations.put(mob, locations);
    }

    public boolean locationOccupied(int x, int y, int z, int world) {
        Tile location = Tile.create(x, y, z);
        Tile[] npcLocations = null;
    	 for (RegionStore r : RegionManager.get().getSurroundingRegions(location)) {
    		 for (NPC npc : r.getNpcs()) {
    			 if (npc == null)
    				 continue;
    			 npcLocations = getOccupiedLocations(npc);
                 if (npcLocations != null) {
                	 for (Tile locs : npcLocations) {
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
    public boolean locationOccupied(Tile[] locations, Entity mob) {
        if ((locations == null) || (mob == null))
            return true;
        Tile[] npcLocations = null;
        for (RegionStore r : RegionManager.get().getSurroundingRegions(mob.getPosition())) {
            for (NPC npc : r.getNpcs()) {
                if ((mob.isNPC()) && ((npc == null) || (npc == mob))) {
                    continue;
                }
                npcLocations = getOccupiedLocations(npc);
                if (npcLocations != null) {
                    for (Tile loc : locations) {
                        for (Tile loc2 : npcLocations) {
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
