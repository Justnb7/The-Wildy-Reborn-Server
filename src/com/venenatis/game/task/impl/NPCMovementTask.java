package com.venenatis.game.task.impl;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.pathfinder.impl.DefaultPathFinder;

/**
 *
 * 
 * @author Patrick van Elderen
 */
public final class NPCMovementTask extends Task {

	public NPCMovementTask() {
		super(1);
	}
	
	public int getMove(int Place1, int Place2) {
		if ((Place1 - Place2) == 0) {
			return 0;
		} else if ((Place1 - Place2) < 0) {
			return 1;
		} else if ((Place1 - Place2) > 0) {
			return -1;
		}
		return 0;
	}

	@Override
	public void execute() {
		for (int index = 0; index < World.getWorld().getNPCs().capacity(); index++) {
			NPC npc = World.getWorld().getNPCs().get(index);
			if (npc == null) {
				continue;
			}
			handle_npc_walking(npc);
		}
	}

	private void handle_npc_walking(NPC npc) {

		if (npc.getCombatState().isDead())
			return;

		// Returning to Spawn Position?
		if (npc.walkingHome) {
			npc.resetFaceTile();
			//System.out.println(npc.getDefinition().getName());
			npc.targetId = 0;

			// Npcs who are linked to a player don't walk home when out of distance. They follow forever.
			if (npc.spawnedBy == null) {
				if (!npc.getLocation().withinDistance(npc.spawnTile, npc.strollRange)) {
					npc.walkingHome = true;
				}
			}
			if (npc.walkingHome && npc.getLocation().equals(npc.spawnTile)) {
				npc.walkingHome = false;
				npc.randomWalk = true;
			} else if (npc.walkingHome) {
				npc.doPath(new DefaultPathFinder(), npc, npc.spawnTile.getX(), npc.spawnTile.getY());
			}
		} else if (npc.randomWalk && (npc.getDefinition() == null || npc.strollRange == 1) && !npc.isInteracting()) {

			int random = Utility.getRandom(3);

			// 30% chance to random walk
			if (random == 1) {
				int strollRange = npc.getDefinition() == null ? 1 : npc.strollRange;

				Location toTile = null;
				while (toTile == null || npc.spawnTile.distance(toTile) > npc.strollRange)
					toTile = npc.getLocation().transform(-strollRange + (2 * strollRange), -strollRange + (2*strollRange));

				npc.doPath(new DefaultPathFinder(), npc, toTile.getX(), toTile.getY());
			}
		}
	}

}