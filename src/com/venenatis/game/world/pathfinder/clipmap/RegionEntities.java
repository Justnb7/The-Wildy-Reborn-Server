package com.venenatis.game.world.pathfinder.clipmap;

import java.util.LinkedList;

import com.venenatis.game.location.Location;

/**
 * Created by Jak on 12/06/2017.
 *
 * A duplicate of the Coverage system in hyperion. Npc stacking where-as they cannot walk through each other in certain circumstances.
 */
public class RegionEntities {

    private LinkedList<Location> npcs = null;

    public void putNpcOnTile(int x, int y, int z) {
        if (npcs == null) {
            npcs = new LinkedList<Location>();
        }
        if (!npcs.contains(new Location(x, y, z)))
            npcs.add(new Location(x, y, z));
    }

    public boolean isNpcOnTile(int x, int y, int z) {
        if (npcs == null) {
            return false;
        }
        return npcs.contains(new Location(x, y, z));
    }

    public void removeNpcFromTile(int x, int y, int z) {
        if (npcs == null) {
            return;
        }
        npcs.remove(new Location(x, y, z));
    }
}
