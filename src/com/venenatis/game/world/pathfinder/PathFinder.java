package com.venenatis.game.world.pathfinder;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;

public interface PathFinder {
	
	 public static final int SOUTH_FLAG = 0x1, WEST_FLAG = 0x2, NORTH_FLAG = 0x4, EAST_FLAG = 0x8;

	 public static final int SOUTH_WEST_FLAG = SOUTH_FLAG | WEST_FLAG;
	 public static final int NORTH_WEST_FLAG = NORTH_FLAG | WEST_FLAG;
	 public static final int SOUTH_EAST_FLAG = SOUTH_FLAG | EAST_FLAG;
	 public static final int NORTH_EAST_FLAG = NORTH_FLAG | EAST_FLAG;

	 public static final int SOLID_FLAG = 0x20000;
 	 public static final int UNKNOWN_FLAG = 0x40000000;

	 public PathState findPath(Entity mob, Entity target, Location base, int srcX, int srcY, int dstX, int dstY, int radius, boolean running, boolean ignoreLastStep, boolean moveNear);



	static PathState doPath(PathFinder pathFinder, Entity mob, Entity target, int x, int y) {
		return doPath(pathFinder, mob, target, x, y, false, true);
	}
	static PathState doPath(PathFinder pathFinder, Entity mob, int x, int y) {
		return doPath(pathFinder, mob, null, x, y, false, true);
	}

	static PathState doPath(final PathFinder pathFinder, final Entity mob, final Entity target, final int x, final int y, final boolean ignoreLastStep, boolean addToWalking) {
		if (mob.isDead()) {
			PathState state = new PathState();
			state.routeFailed();
			return state;
		}
		//if (++mob.dummyIdx >= World.dummies.length) // debug. dummies is int[] {random item ids.. potions] for visual debugging.
		//public static int[] dummies = new int[] {995, 6685, 3024, 2440, 2436, 2444, 2432};
		//	mob.dummyIdx = 0;//reset
		//int item = World.dummies[mob.dummyIdx];
		Location destination = Location.create(x, y, mob.getPosition().getZ());
		Location base = mob.getPosition();
		int srcX = base.getLocalX();
		int srcY = base.getLocalY();
		int destX = destination.getLocalX(base);
		int destY = destination.getLocalY(base);
		PathState state = pathFinder.findPath(mob, target, mob.getPosition(), srcX, srcY, destX, destY, 1, mob.isPlayer() && ((Player)mob).getWalkingQueue().isRunningQueue(), ignoreLastStep, true);
		if (state != null && addToWalking) {
			if (mob.isPlayer()) {
				Player p = (Player)mob;
				p.getWalkingQueue().reset();
				for (BasicPoint step : state.getPoints()) {
					//p.sendForcedMessage("point: "+step.getX()+","+step.getY()+","+step.getZ()+" from "+srcX+","+srcY+" to "+destX+","+destY);
					//p.getActionSender().sendGroundItem(new GroundItem(new Item(item, 1), step.getX(), step.getY(), step.getZ(), p));
					p.getWalkingQueue().addStep(step.getX(), step.getY());
				}
				p.getWalkingQueue().finish();
				//p.debug("Calc'd "+state.getPoints().size()+" moves for goal dist "+base.distance(destination));
			} else {
				System.err.println("HELP WHO");
			}
		}
		return state;
	}
}
