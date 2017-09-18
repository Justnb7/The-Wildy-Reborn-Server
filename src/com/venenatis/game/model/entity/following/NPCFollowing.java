package com.venenatis.game.model.entity.following;

import com.venenatis.game.constants.WalkingConstants;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.entity.Boundary;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.world.pathfinder.ProjectilePathFinder;

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
		if (target == null || npc == null) {
			npc.setFollowing(null);
			npc.resetFace();
			return;
		}
		
		boolean isBoss = false; // TODO
		
		// Only check rooms if npc is even remotely related to GWD.. otherwise.. LAG!
		if (isBoss) {
			if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS)) {
				if (!Boundary.isIn(target, Boundary.GODWARS_BOSSROOMS)) {
					npc.setFollowing(null);
					npc.resetFace();
					npc.targetId = 0; // reset cb as well.. not valid
					return;
				}
			}
		}

		if (target.getCombatState().isDead() || !target.isVisible() || npc.getZ() != target.getZ()) {
			npc.setFollowing(null);
			npc.resetFace();
			npc.walkingHome = true;
			npc.underAttack = false;
			return;
		}

		int targX = target.getX();
		int targY = target.getY();

		//npc.sendForcedMessage("delta "+(npc.getX()-targX)+" by "+(npc.getY()-targY));

		// At this point, the target is valid, don't start walking off randomly.
		
		// Stop the npc from walking home and from random walking
		npc.walkingHome = npc.randomWalk = false;
		

		if (npc.frozen()) {
			// Don't reset, we just can't reach.
			return;
		}
		boolean sameSpot = npc.getX() == target.getX() && npc.getY() == target.getY() && npc.getSize() == 1;
		if (sameSpot) {
			walkToNextTile(npc, targX, targY-1);
			return;
		}

		/*
		 * If close enough, stop following
		 */
		
		for (Location pos : npc.getTiles()) {
			double distance = pos.distance(target.getLocation());
			boolean magic = npc.getCombatType() == CombatStyle.MAGIC;
			boolean ranged = !magic && npc.getCombatType() == CombatStyle.RANGE;
			boolean melee = !magic && !ranged;
			boolean dont_check = npc.getId() == 6616 || npc.getId() == 6768;//6768 is the spawn
			if (melee || npc.isPet || dont_check) {
				if (distance <= 1) { // Stop following when close
					return;
				}
			} else {
				if (distance <= (ranged ? 7 : magic ? 10 : 1)) {
					// so its 10 by default, checks melee range then asssumes anything else instead of magic
					return;
				}
			}
		}
		
		// Spawned by a player.. we're (1) a pet (2) a warrior guild armour.. we follow forever
		boolean locked_to_plr = npc.spawnedBy > 0 || npc.ownerId > 0; // pets have spawnBy set
		// Within +/- 15 tiles from where our spawn pos is.
		boolean in_spawn_area = ((npc.getX() < npc.makeX + 15) && (npc.getX() > npc.makeX - 15) && (npc.getY() < npc.makeY + 15) && (npc.getY() > npc.makeY - 15));
		
		// Let's calculate a path to the target now.
		if (locked_to_plr || in_spawn_area) {
			npc.face(target.getLocation());
			//npc.setFollowing(null); // reset existing walking queue
			walkToNextTile(npc, targX, targY); // update walking queue to new target pos
			//npc.sendForcedMessage("followin "+target);
			
		} else {
			// Reset following
			npc.setFollowing(null);
			npc.resetFace();
			npc.walkingHome = true;
			npc.underAttack = false;
			//npc.sendForcedMessage("reset "+locked_to_plr+" or "+in_spawn_area);
		}
	}
	
	/**
	 * Calculates the movement required to reach a target X Y
	 * @param mob
	 * @param destinationX
	 * @param destinationY
	 */
	public static void walkToNextTile(NPC mob, int destinationX, int destinationY) {
		if (mob.getX() == destinationX && mob.getY() == destinationY)
			return;

		int direction = -1;

		final int x = mob.getX();
		final int y = mob.getY();
		final int xDifference = destinationX - x;
		final int yDifference = destinationY - y;

		int toX = 0;
		int toY = 0;

		if (xDifference > 0) {
			toX = 1;
		} else if (xDifference < 0) {
			toX = -1;
		}

		if (yDifference > 0) {
			toY = 1;
		} else if (yDifference < 0) {
			toY = -1;
		}

		int toDir = ProjectilePathFinder.getDirection(x, y, x + toX, y + toY);

		if (mob.canMoveTo(mob.getLocation(), toDir)) {
			direction = toDir;
		} else {
			if (toDir == 0) {
				if (mob.canMoveTo(mob.getLocation(), 3)) {
					direction = 3;
				} else if (mob.canMoveTo(mob.getLocation(), 1)) {
					direction = 1;
				}
			} else if (toDir == 2) {
				if (mob.canMoveTo(mob.getLocation(), 1)) {
					direction = 1;
				} else if (mob.canMoveTo(mob.getLocation(), 4)) {
					direction = 4;
				}
			} else if (toDir == 5) {
				if (mob.canMoveTo(mob.getLocation(), 3)) {
					direction = 3;
				} else if (mob.canMoveTo(mob.getLocation(), 6)) {
					direction = 6;
				}
			} else if (toDir == 7) {
				if (mob.canMoveTo(mob.getLocation(), 4)) {
					direction = 4;
				} else if (mob.canMoveTo(mob.getLocation(), 6)) {
					direction = 6;
				}
			}
		}

		if (direction == -1) {
			return;
		}

		mob.setLocation(mob.getLocation().transform(WalkingConstants.DIRECTION_DELTA_X[direction], WalkingConstants.DIRECTION_DELTA_Y[direction]));
		mob.direction = direction;
		mob.setOnTile(mob.getX(), mob.getY(), mob.getZ());
	}

}