package com.venenatis.game.model.entity.following;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.combat.NpcCombat;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.pathfinder.RouteFinder;
import com.venenatis.game.world.pathfinder.impl.SizedPathFinder;
import com.venenatis.server.Server;

public class NPCFollowing {
	
	/**
	 * Handles following a player
	 * 
	 * @param npc
	 *            The {@link NPC} which is following the player
	 * @param target
	 *            The id of the player being followed
	 */
	public static void attemptFollowEntity(NPC npc, Entity target) {
		// When an NPC is returning to their spawn tile, they still face their old target but it is reset. 
		if (npc.walkingHome) {
			npc.getCombatState().setTarget(null);
			npc.following().setFollowing(null);
			npc.sendForcedMessage("Failed attempt to follow i am walking home.");
			return;
		}
		if (target == null || npc == null) {
			npc.following().setFollowing(null);
			npc.sendForcedMessage("Resetting follow");
			npc.resetFaceTile();
			return;
		}
		
		if(npc.getId() == 5947) //spinolyp
			return;
		
		boolean isBoss = false; // TODO
		if (npc.getId() == 5945) { // Rock Lobster
			npc.transforming = true;
			//npc.playAnimation(Animation.create(7135));
			npc.requestTransform(5944);
			npc.aggressive = true;
			npc.setHitpoints(npc.getHitpoints());
			Server.getTaskScheduler().schedule(new Task(3) {
				@Override
				public void execute() {
					npc.transforming = false; //enable attacking 3 cycles later
					this.stop();
				}
			});
		}
		// Only check rooms if npc is even remotely related to GWD.. otherwise.. LAG!
		if (isBoss) {
			if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS)) {
				if (!Boundary.isIn(target, Boundary.GODWARS_BOSSROOMS)) {
					npc.following().setFollowing(null);
					npc.resetFaceTile();
					npc.getCombatState().reset(); // reset cb as well.. not valid
					return;
				}
			}
		}

		if (target.getCombatState().isDead() || !target.isVisible() || npc.getZ() != target.getZ()) {
			npc.following().setFollowing(null);
			npc.resetFaceTile();
			npc.walkingHome = true;
			return;
		}

		int targX = target.getX();
		int targY = target.getY();

		//npc.sendForcedMessage("delta "+(npc.getX()-targX)+" by "+(npc.getY()-targY))
		// At this point, the target is valid, don't start walking off randomly.
		// Stop the npc from walking home and from random walking
		npc.walkingHome = npc.randomWalk = false;
		

		if (npc.frozen()) {
			return;
		}
		boolean sameSpot = npc.getX() == target.getX() && npc.getY() == target.getY() && npc.getSize() == 1;
		if (sameSpot) {
			npc.doPath(new SizedPathFinder(), targX, targY-1);
			return;
		}

		/*
		 * If close enough, stop following
		 */
		
		/*System.out.println("COMBAT STYLE  "+npc.getCombatType()+" Distance: "+NpcCombat.distanceRequired(npc));
		npc.sendForcedMessage(" "+npc.getLocation().distanceToPoint(target.getLocation()));
		for (Location pos : npc.getTiles()) {
			
			double distance = pos.distance(target.getLocation());
			boolean magic = npc.getCombatType() == CombatStyle.MAGIC;
			boolean ranged = !magic && npc.getCombatType() == CombatStyle.RANGE;
			boolean melee = !magic && !ranged;
			boolean dont_check = npc.getId() == 6616 || npc.getId() == 6768;//6768 is the spawn
			if (melee || npc.isPet || dont_check) {
				if (distance <= 1) { // Stop following when close
					npc.faceEntity(target);
					System.out.println("probs");
					return;
				}
			} else {
				if (distance <= (ranged ? 6 : magic ? 6 : 1)) {
					// so its 10 by default, checks melee range then asssumes anything else instead of magic
					System.out.println("Stopping the follow we are close enough");
					npc.following().setFollowing(null);
					npc.pathStop = true;
					npc.resetFaceTile();
					return;
				} else {
					npc.pathStop = false;
				}
			} 
		}*/
		
		/*Location last = target.lastTile == null ? target.getLocation().transform(1, 0) : target.lastTile;
        int fx = last.getX();
        int fy = last.getY();
        int x = fx - npc.getX();
        int y = fy - npc.getY();
        npc.npcWalk(npc.getX() + x, npc.getY() + y);*/
		 int otherX = target.getX();
	        int otherY = target.getY();
	        Location followLoc = null;
		boolean locked_to_plr = npc.spawnedBy != null; // pets have spawnBy set
		boolean in_spawn_area = npc.getCentreLocation().withinDistance(npc.spawnTile, 15);
		if (locked_to_plr || in_spawn_area) {
			npc.face(target.getLocation());
			//npc.npcWalk(targX, targY); 
			if (target.size() == 1) {
                Location[] locs = {new Location(otherX + 1, otherY, npc.getZ()), new Location(otherX - 1, otherY, npc.getZ()), new Location(otherX, otherY + 1, npc.getZ()),
                        new Location(otherX, otherY - 1, npc.getZ()),};
                for (Location i : locs) {
                    if (followLoc == null || npc.getLocation().getDistance(i) < npc.getLocation().getDistance(followLoc)) {
                        followLoc = i;
                    }
                }
            } else {
                followLoc = Location.create(target.getX(), target.getY(), target.getZ()).
                        closestTileOf(Location.create(npc.getX(), npc.getY(), npc.getZ()), target.size(), target.size());
            }
			if (followLoc != null) {
                npc.npcWalk(followLoc.getX(), followLoc.getY());
            }
	        
			// npc.doPath(new SizedPathFinder(), targX, targY); // update walking queue to new target pos
			
		} else {
			// Reset following
			npc.following().setFollowing(null);
			npc.resetFaceTile();
			npc.walkingHome = true;
			npc.sendForcedMessage("reset "+locked_to_plr+" or "+in_spawn_area);
		}
	}

}