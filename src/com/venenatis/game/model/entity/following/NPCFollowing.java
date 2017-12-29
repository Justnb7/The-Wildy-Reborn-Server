package com.venenatis.game.model.entity.following;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.combat.NpcCombat;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
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
		if (!npc.getLocation().withinDistance(npc.spawnTile, npc.strollRange) && !target.getLocation().isWithinDistance(npc.spawnTile, npc.strollRange))  {
			npc.getCombatState().setTarget(null);
			npc.following().setFollowing(null);
			npc.sendForcedMessage("Max distance achieved stopping following.");
			//if(!npc.getCombatState().inCombat())
				//npc.walkingHome = true;
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
			//npc.walkingHome = true;
			return;
		}

		int targX = target.getX();
		int targY = target.getY();

		//npc.sendForcedMessage("delta "+(npc.getX()-targX)+" by "+(npc.getY()-targY))
		// At this point, the target is valid, don't start walking off randomly.
		// Stop the npc from walking home and from random walking
		npc.walkingHome = npc.randomWalk = false;
		npc.sendForcedMessage("Distance to tile: "+npc.getLocation().distanceToPoint(npc.spawnTile));

		if (npc.frozen()) {
			return;
		}
		boolean sameSpot = npc.getX() == target.getX() && npc.getY() == target.getY() && npc.getSize() == 1;
		if (sameSpot) {
			npc.doPath(new SizedPathFinder(), targX, targY-1);
			return;
		}

	
		 int otherX = target.getX();
	        int otherY = target.getY();
	        Location followLoc = null;
		boolean locked_to_plr = npc.spawnedBy != null; // pets have spawnBy set
		boolean in_spawn_area = npc.getCentreLocation().withinDistance(npc.spawnTile, 15);
		if (locked_to_plr || in_spawn_area) {
			//npc.face(target.getLocation());
			//npc will not walk all the way up if its distance required is > 2
			if (NpcCombat.distanceRequired(npc) > 1) {
				int distance = NpcCombat.distanceRequired(npc)-Utility.random(3);
                Location[] locs = {new Location(otherX + distance, otherY, npc.getZ()), new Location(otherX - distance, otherY, npc.getZ()), new Location(otherX, otherY + distance, npc.getZ()),
                        new Location(otherX, otherY - distance, npc.getZ()),};
                for (Location i : locs) {
                    if (followLoc == null || npc.getLocation().getDistance(i) < npc.getLocation().getDistance(followLoc)) {
                        followLoc = i;
                    }
                }
            } else
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
		} else {
			// Reset following
			npc.following().setFollowing(null);
			npc.resetFaceTile();
			npc.walkingHome = true;
			npc.sendForcedMessage("reset "+locked_to_plr+" or "+in_spawn_area);
		}
	}

	}