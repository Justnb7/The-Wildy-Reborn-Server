package hyperion;

import clipmap.Tile;
import com.model.game.character.Entity;
import com.model.game.character.player.Player;
import com.model.game.location.Location;

public interface PathFinder {
	
	 public static final int SOUTH_FLAG = 0x1, WEST_FLAG = 0x2, NORTH_FLAG = 0x4, EAST_FLAG = 0x8;

	 public static final int SOUTH_WEST_FLAG = SOUTH_FLAG | WEST_FLAG;
	 public static final int NORTH_WEST_FLAG = NORTH_FLAG | WEST_FLAG;
	 public static final int SOUTH_EAST_FLAG = SOUTH_FLAG | EAST_FLAG;
	 public static final int NORTH_EAST_FLAG = NORTH_FLAG | EAST_FLAG;

	 public static final int SOLID_FLAG = 0x20000;
 	 public static final int UNKNOWN_FLAG = 0x40000000;

	 public PathState findPath(Entity mob, Entity target, Tile base, int srcX, int srcY, int dstX, int dstY, int radius, boolean running, boolean ignoreLastStep, boolean moveNear);



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
		Tile destination = Tile.create(x, y, mob.getPosition().getZ());
		Tile base = mob.getPosition();
		int srcX = base.getLocalX();
		int srcY = base.getLocalY();
		int destX = destination.getLocalX(base);
		int destY = destination.getLocalY(base);
		PathState state = pathFinder.findPath(mob, target, mob.getPosition(), srcX, srcY, destX, destY, 1, mob.isPlayer() && ((Player)mob).getMovementHandler().isRunPath(), ignoreLastStep, true);
		if (state != null && addToWalking) {
			if (mob.isPlayer()) {
				Player p = (Player)mob;
				p.getMovementHandler().reset();
				for (BasicPoint step : state.getPoints()) {
					p.getMovementHandler().addToPath(new Location(step.getX(), step.getY(), p.heightLevel));
				}
				p.getMovementHandler().finish();
				p.debug("Calc'd "+state.getPoints().size()+" moves for goal dist "+base.distance(destination));
			} else {
				System.err.println("HELP WHO");
			}
		}
		return state;
	}
}
