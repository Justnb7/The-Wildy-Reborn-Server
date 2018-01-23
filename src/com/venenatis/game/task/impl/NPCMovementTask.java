package com.venenatis.game.task.impl;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.NpcCombat;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.pathfinder.impl.SizedPathFinder;

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

	/**
	 * Method to manage if NPCs should return (walk home) to their origin spawn tile if they're further
	 * than X tiles -- where x is defined as the strollRange in NPC spawns json
	 * @param npc
	 */
	private void handle_npc_walking(NPC npc) {

		if (npc.getCombatState().isDead())
			return;

		// Firstly, returning to Spawn Position? if too far away.
		if (!npc.walkingHome) {
			//System.out.println(npc.getDefinition().getName());
			// Npcs who are linked to a player don't walk home when out of distance. They follow forever.
			/*if (npc.spawnedBy == null) {
				// If stroll range is >0 it will have been loaded in npcSpawns. If not (default 0) default to value 10 instead.
				final int maxDistance = npc.strollRange > 0 ? npc.strollRange : 10;
				if (!npc.getLocation().withinDistance(npc.spawnTile, maxDistance)) {
					npc.walkingHome = true;
					npc.getCombatState().setTarget(null);
					npc.resetFaceTile();
					npc.sendForcedMessage("home time");
					npc.doPath(new SizedPathFinder(), npc.spawnTile.getX(), npc.spawnTile.getY());
				}
			}*/
		
			if (npc.spawnedBy == null) {
				// If stroll range is >0 it will have been loaded in npcSpawns. If not (default 0) default to value 10 instead.
				 int maxDistance = npc.strollRange > 0 ? npc.strollRange : 10;
				// npc.sendForcedMessage("Max distance: "+maxDistance+ " " +npc.walkingHome+" "+npc.randomWalk+" "+npc.strollRange); // debug
			
				 if(npc.getCombatState().inCombat()) {
						//maxDistance = maxDistance + NpcCombat.distanceRequired(npc);
					//if player is max distance stop following
					if (!npc.getLocation().withinDistance(npc.spawnTile, maxDistance)) {
						npc.following().setFollowing(null);
						npc.resetFaceTile();
					}
				} else {
					if (!npc.getLocation().withinDistance(npc.spawnTile, maxDistance) && !npc.getCombatState().isDead()) {
						npc.walkingHome = true;
						npc.sendForcedMessage("home time "+npc.getId());
						//npc.doPath(new SizedPathFinder(), npc.spawnTile.getX(), npc.spawnTile.getY());
					}
				}
				
				
				
				
				
				
				/*if (!npc.getLocation().withinDistance(npc.spawnTile, maxDistance) && !npc.hasAttribute("attack")) {
					npc.walkingHome = true;
					npc.resetFaceEntity();
					npc.getCombatState().setTarget(null);
					npc.following().setFollowing(null);
					npc.sendForcedMessage("home time");
					
				}*/
			}
			
		}
		if (npc.walkingHome && npc.getLocation().equals(npc.spawnTile)) {
			npc.walkingHome = false;
			npc.randomWalk = true;
		}
		
		if(npc.walkingHome && npc.getCombatState().getTarget() != null) {
			npc.walkingHome = false;
		}
		
		if (npc.walkingHome && npc.getWalkingQueue().isEmpty()) {
			final int x = npc.spawnTile.getX() + Utility.random(1);
			final int y = npc.spawnTile.getY() + Utility.random(1);
			npc.faceEntity(null);
			npc.face(npc.spawnTile);
			npc.getCombatState().setTarget(null);
			npc.resetFaceTile();
			npc.getCombatState().setUnderAttackBy(null);
			npc.getUpdateFlags().flag(UpdateFlag.FACE_ENTITY);
			npc.doPath(new SizedPathFinder(), x, y);
			npc.walkingHome = true;
			//System.out.println("Going home..");
		
		}
	
		if (!npc.walkingHome && npc.randomWalk && (npc.getDefinition() == null || npc.strollRange > 0) && !npc.isInteracting()) {
			int random = Utility.getRandom(15);
			if (random == 1) {
				int strollRange = npc.getDefinition() == null ? 1 : npc.strollRange;
				Location toTile = null;
				int attempts = 3;
				while ((toTile == null || npc.spawnTile.distance(toTile) > npc.strollRange+1) && --attempts > 0)
					toTile = npc.getLocation().transform(Utility.random(2) == 1 ? 1 : -1 * Utility.random(1, strollRange), Utility.random(2) == 1 ? 1 : -1 * Utility.random(1, strollRange));

				npc.doPath(new SizedPathFinder(), toTile.getX(), toTile.getY());
			}
		}
	}

}