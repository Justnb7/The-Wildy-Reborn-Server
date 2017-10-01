package com.venenatis.game.model.entity.following;

import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.data.CombatStyle;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.npc.NPC;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.pathfinder.ProjectilePathFinder;
import com.venenatis.game.world.pathfinder.clipmap.Region;

import java.util.stream.Stream;

/**
 * Abstract because methods are static only
 */
public abstract class PlayerFollowing {

    public static void moveOutFromUnderLargeNpc(Player player, Entity other) {

        boolean inside = false;
        boolean projectiles = player.getCombatType() != CombatStyle.MELEE;
        for (Location tile : other.getTiles()) {
            if (player.getX() == tile.getX() && player.getY() == tile.getY()) {
                inside = true;
                break;
            }
        }

        if (inside) {
            double lowDist = 99;
            int lowX = 0;
            int lowY = 0;
            int z = other.getZ();
            int x2 = other.getX();
            int y2 = other.getY();
            int x3 = x2;
            int y3 = y2 - 1;
            boolean ignoreClip = other.isNPC() && Stream.of(494, 5535, 5534, 492, 493, 496).anyMatch(i -> i == ((NPC)other).getId());

            for (int k = 0; k < 4; k++) {
                for (int i = 0; i < other.size() - (k == 0 ? 1 : 0); i++) {
                    if (k == 0) {
                        x3++;
                    } else if (k == 1) {
                        if (i == 0) {
                            x3++;
                        }
                        y3++;
                    } else if (k == 2) {
                        if (i == 0) {
                            y3++;
                        }
                        x3--;
                    } else if (k == 3) {
                        if (i == 0) {
                            x3--;
                        }
                        y3--;
                    }

                    Location location = new Location(x3, y3, z);
                    double d = location.distance(player.getLocation());
                    if (d < lowDist) {
                        if (ignoreClip || !projectiles || projectiles
                                && ProjectilePathFinder.isProjectilePathClear(location, other.getLocation())) {
                            if (ignoreClip || projectiles || !projectiles
                                    && ProjectilePathFinder.isInteractionPathClear(location, other.getLocation())) {
                                lowDist = d;
                                lowX = x3;
                                lowY = y3;
                            }
                        }
                    }
                }
            }

            if (lowX > 0 && lowY > 0) {
                player.playerWalk(lowX, lowY);
            }
        }
    }

    /**
	 * The player following another player.
	 * @param forCombat
	 *        Checks if the player is in combat
	 * @param target
	 *        The entity we're following
	 */
    public static void follow(Player player, boolean forCombat, Entity target) {

        //Whenever out target is null or death stop the following task
        if (target == null || target.getCombatState().isDead() || player.getCombatState().isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
            player.following().setFollowing(null);
            return;
        }
        if (player.frozen()) {
            return;
        }

        //Calculate the x and y offsets
        int otherX = target.getX();
        int otherY = target.getY();

        //When out of distance stop the task
        if (!player.goodDistance(otherX, otherY, player.getX(), player.getY(), 25)) {
            player.following().setFollowing(null);
            player.debug("out of range");
            return;
        }

        boolean inside = manoveringFromUnderTarg(player, target);
        if (inside)
            return;

        //Start facing the player you want to follow
        player.faceEntity(target);

        if (!forCombat) {
            Location last = target.lastTile == null ? target.getLocation().transform(1, 0) : target.lastTile;
            int fx = last.getX();
            int fy = last.getY();

            int delay = (player.getWalkingQueue().isMoving() || ((Player)target).getWalkingQueue().isMoving()) ? 1
                : (player.walkTutorial + 1 >= Integer.MAX_VALUE ? player.walkTutorial = 0 : player.walkTutorial++);
            int remainder = delay % 2;
            if (remainder == 1) {
                int x = fx - player.getX();
                int y = fy - player.getY();
                player.playerWalk(player.getX() + x, player.getY() + y);
            }
        } else {
            /*
             * Check our regular combat styles for distance
             */
            if (player.getCombatType() == CombatStyle.MELEE && player.goodDistance(otherX, otherY, player.getX(), player.getY(), 1)) {
                if (otherX != player.getX() && otherY != player.getY()) {
                    stopDiagonal(player, otherX, otherY);
                    return;
                } else {
                    // successfully next to them
                    player.getWalkingQueue().reset();
                    return;
                }
            }
            Location followLoc = null;

            if (target.size() == 1) {
                Location[] locs = {new Location(otherX + 1, otherY, player.getZ()), new Location(otherX - 1, otherY, player.getZ()), new Location(otherX, otherY + 1, player.getZ()),
                        new Location(otherX, otherY - 1, player.getZ()),};
                for (Location i : locs) {
                    if (followLoc == null || player.getLocation().getDistance(i) < player.getLocation().getDistance(followLoc)) {
                        followLoc = i;
                    }
                }
            } else {
                followLoc = Location.create(target.getX(), target.getY(), target.getZ()).
                        closestTileOf(Location.create(player.getX(), player.getY(), player.getZ()), target.size(), target.size());
            }
            if (followLoc != null) {
                player.playerWalk(followLoc.getX(), followLoc.getY());
            }
        }
    }

    private static boolean manoveringFromUnderTarg(Player player, Entity following) {
        int otherX = following.getX();
        int otherY = following.getY();
        boolean inside = false;
        if (following.size() == 1) {
            if (player.getX() == otherX && player.getY() == otherY) {
                inside = true;
            }
        } else {
            Location[] occupied = following.getTiles();
            for (Location tile : occupied) {
                if (player.getX() == tile.getX() && player.getY() == tile.getY()) {
                    inside = true;
                    break;
                }
            }
        }
        if (inside) {
            if (following.size() > 1) {
                moveOutFromUnderLargeNpc(player, following);
            } else {
                if (Region.getClipping(player.getX() - 1, player.getY(), player.getZ(), -1, 0)) {
                    walkTo(player, -1, 0);
                } else if (Region.getClipping(player.getX() + 1, player.getY(), player.getZ(), 1, 0)) {
                    walkTo(player, 1, 0);
                } else if (Region.getClipping(player.getX(), player.getY() - 1, player.getZ(), 0, -1)) {
                    walkTo(player, 0, -1);
                } else if (Region.getClipping(player.getX(), player.getY() + 1, player.getZ(), 0, 1)) {
                    walkTo(player, 0, 1);
                }
            }
            player.debug("inside target, manovouring..");
            return true;
        }
        return false;
    }

    /**
	 * Stops diagonal movements
	 *
	 * @param player
	 *            The player
	 * @param targetX
	 *            The targets x position
	 * @param targetY
	 *            The targets y position
	 */
    public static void stopDiagonal(Player player, int targetX, int targetY) {
    	if (player.frozen()) {
            return;
        }
        player.getWalkingQueue().reset();
        int xMove = targetX - player.getX();
        int yMove = 0;

        if (xMove == 0) {
            yMove = targetY - player.getY();
        }

        player.getWalkingQueue().addStep(player.getX() + xMove, player.getY() + yMove);
    }

    public static void walkTo(Player player, int changeX, int changeY) {
        player.getWalkingQueue().reset();
        player.getWalkingQueue().addStep(player.getX() + changeX, player.getY() + changeY);
        player.getWalkingQueue().finish();
    }

}
