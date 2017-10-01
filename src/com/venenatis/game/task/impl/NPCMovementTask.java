package com.venenatis.game.task.impl;

import com.venenatis.game.model.entity.following.NPCFollowing;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

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
				NPCFollowing.walkToNextTile(npc, npc.spawnTile.getX(), npc.spawnTile.getY());
			}
		} else if (npc.randomWalk && (npc.getDefinition() == null || npc.strollRange == 1) && !npc.isInteracting()) {

			int random = Utility.getRandom(3);

			// 30% chance to random walk
			if (random == 1) {
				int MoveX = 0;
				int MoveY = 0;
				int Rnd = Utility.getRandom(9);
				int strollRange = npc.getDefinition() == null ? 1 : npc.strollRange;
				// choose a random direction to walk.. this is dumb as fuck but whatever. Better to do random(direction) equivalent
				switch (Rnd) {
					case 1:
						MoveX = strollRange;
						MoveY = strollRange;
						break;
					case 2:
						MoveX = -strollRange;
						break;
					case 3:
						MoveY = -strollRange;
						break;
					case 4:
						MoveX = strollRange;
						break;
					case 5:
						MoveY = strollRange;
						break;
					case 6:
						MoveX = -strollRange;
						MoveY = -strollRange;
						break;
					case 7:
						MoveX = -strollRange;
						MoveY = strollRange;
						break;
					case 8:
						MoveX = strollRange;
						MoveY = -strollRange;
						break;
				}

				// check if the destination exceeds the maximum walk area
				if (MoveX == strollRange) {
					if (npc.getX() + MoveX < npc.spawnTile.getX() + 1) {
						npc.moveX = MoveX;
					} else {
						npc.moveX = 0;
					}

				}
				if (MoveX == -strollRange) {
					if (npc.getX() - MoveX > npc.spawnTile.getX() - 1) {
						npc.moveX = MoveX;
					} else {
						npc.moveX = 0;
					}

				}
				if (MoveY == strollRange) {
					if (npc.getY() + MoveY < npc.spawnTile.getY() + 1) {
						npc.moveY = MoveY;
					} else {
						npc.moveY = 0;
					}

				}
				if (MoveY == -strollRange) {
					if (npc.getY() - MoveY > npc.spawnTile.getY() - 1) {
						npc.moveY = MoveY;
					} else {
						npc.moveY = 0;
					}

				}
				NPCFollowing.walkToNextTile(npc, npc.getX() + npc.moveX, npc.getY() + npc.moveY);
			}
		}
	}

}