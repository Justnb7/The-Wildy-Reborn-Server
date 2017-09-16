package com.venenatis.game.model.entity.following;

import java.util.ArrayList;
import java.util.List;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.world.pathfinder.clipmap.Region;

public class Following {

	public static Location getDestination(Entity mob, Entity victim) {// this might work.
		// If X > 0 - victim > mob, x < 0 victim < mob.
		int size = mob.getWidth();
		if (mob.isNPC()) {
			NPC n = (NPC) mob;
			size = n.getSize();
		}
		Location delta = mob.getCentreLocation().getDelta(victim.getCentreLocation());
		boolean vertical = (delta.getY() < 0 ? -delta.getY() : delta.getY()) > (delta.getX() < 0 ? -delta.getX() : delta.getX());
		List<Location> victimList = null;
		List<Location> mobList = null;
		int z = mob.getLocation().getZ();
		if (vertical) {
			if (delta.getY() > 0) { // Victim has higher Y than mob.
				victimList = getSurrounding(victim, victim.getLocation().getY(), victim.getLocation().getX(), z, -1, true);
				mobList = getSurrounding(mob, mob.getLocation().getY(), mob.getLocation().getX(), z, size, true);
			} else {
				victimList = getSurrounding(victim, victim.getLocation().getY(), victim.getLocation().getX(), z, victim.getWidth(), true);
				mobList = getSurrounding(mob, mob.getLocation().getY(), mob.getLocation().getX(), z, -1, true);
			}
		} else {
			if (delta.getX() > 0) { // Victim has higher X than mob.
				victimList = getSurrounding(victim, victim.getLocation().getX(), victim.getLocation().getY(), z, -1, false);
				mobList = getSurrounding(mob, mob.getLocation().getX(), mob.getLocation().getY(), z, size, false);
			} else {
				victimList = getSurrounding(victim, victim.getLocation().getX(), victim.getLocation().getY(), z, victim.getWidth(), false);
				mobList = getSurrounding(mob, mob.getLocation().getX(), mob.getLocation().getY(), z, -1, false);
			}
		}
		double currentDistance = 999; // Random high number so we override first in the loop.
		Location victLoc = victim.getLocation();
		for (Location sl : mobList) {
			for (Location vl : victimList) {
				double distance = sl.distance(vl);
				if (distance < currentDistance) {
					currentDistance = distance;
					victLoc = vl;
				}
			}
		}
		return victLoc;
	}

	private static List<Location> getSurrounding(Entity entity, int i, int j, int z, int size, boolean switched) {
		List<Location> list = new ArrayList<Location>();
		int x = i + size;
		for (int y = j; y < j + size; y++) {
			Location l = switched ? Location.create(y, x, z) : Location.create(x, y, z);
			if (Region.getClippingMask(l.getX(), l.getY(), l.getZ()) > 0) {
				continue;
			}
			list.add(l);
		}
		return list;
	}

}
