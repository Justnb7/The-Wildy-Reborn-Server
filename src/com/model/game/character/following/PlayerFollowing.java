package com.model.game.character.following;

import com.model.game.character.Entity;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.ProjectilePathFinder;
import com.model.game.character.player.Skills;
import com.model.game.character.walking.PathFinder;
import com.model.game.location.Location;
import com.model.utility.cache.map.Region;
import com.model.utility.cache.map.Tile;

import java.util.stream.Stream;

public class PlayerFollowing {
	
	/**
     * The player.
     */
	private final Player player;

	/**
     * Creates an follow action for the specified player.
     *
     * @param player The player to create the follow action for.
     */
	public PlayerFollowing(Player player) {
		this.player = player;
	}

    public static void moveOutFromUnderLargeNpc(Player player, Entity other) {

        boolean inside = false;
        boolean projectiles = player.getCombatType() != CombatStyle.MELEE;
        for (Location tile : other.getTiles()) {
            if (player.absX == tile.getX() && player.absY == tile.getY()) {
                inside = true;
                break;
            }
        }

        if (inside) {
            double lowDist = 99;
            int lowX = 0;
            int lowY = 0;
            int z = other.heightLevel;
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
                player.getPlayerFollowing().playerWalk(lowX, lowY);
            }
        }
    }

    /**
	 * The player following another player.
	 * @param forCombat
	 *        Checks if the player is in combat
	 * @param following
	 *        The entity we're following
	 */
    public void follow(boolean forCombat, Entity following, int stopIfDistance) {
        
        //Whenever out target is null or death stop the following task
        if (following == null || following.isDead() || player.isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
            player.setFollowing(null);
            return;
        }
        if (player.frozen()) {
            return;
        }

        //Calculate the x and y offsets
        int otherX = following.getX();
        int otherY = following.getY();

        //When out of distance stop the task
        if (!player.goodDistance(otherX, otherY, player.getX(), player.getY(), 25)) {
            player.setFollowing(null);
            return;
        }

        boolean inside = false;
        if (following.size() == 1) {
            if (player.absX == otherX && player.absY == otherY) {
                inside = true;
            }
        } else {
            Location[] occupied = following.getTiles();
            for (Location tile : occupied) {
                if (player.absX == tile.getX() && player.absY == tile.getY()) {
                    inside = true;
                    break;
                }
            }
            if (!inside) {
                for (Location npcloc : occupied) {
                    double distance = npcloc.distance(player.getLocation());
                    if (distance <= stopIfDistance) {
                        player.getMovementHandler().stopMovement();
                        return;
                    }
                }
            }
        }
        if (inside) {
            if (following.size() > 1) {
                moveOutFromUnderLargeNpc(player, following);
            } else {
                if (Region.getClipping(player.getX() - 1, player.getY(), player.heightLevel, -1, 0)) {
                    walkTo(-1, 0);
                } else if (Region.getClipping(player.getX() + 1, player.getY(), player.heightLevel, 1, 0)) {
                    walkTo(1, 0);
                } else if (Region.getClipping(player.getX(), player.getY() - 1, player.heightLevel, 0, -1)) {
                    walkTo(0, -1);
                } else if (Region.getClipping(player.getX(), player.getY() + 1, player.heightLevel, 0, 1)) {
                    walkTo(0, 1);
                }
            }
            return;
        }

        //Start facing the player you want to follow
        player.faceEntity(following);

        if (!forCombat) {
            int fx = following.lastTile.getX();
            int fy = following.lastTile.getY();

            int delay = (player.getMovementHandler().isMoving() || ((Player)following).getMovementHandler().isMoving()) ? 1
                : (player.walkTutorial + 1 >= Integer.MAX_VALUE ? player.walkTutorial = 0 : player.walkTutorial++);
            int remainder = delay % 2;
            if (remainder == 1) {
                int x = fx - player.getX();
                int y = fy - player.getY();
                playerWalk(player.getX() + x, player.getY() + y);
            }
        } else {

            boolean goodCombatDistance = player.goodDistance(otherX, otherY, player.getX(), player.getY(), stopIfDistance);
            /*
             * Check for other range weapons which require a distance of 4
             */
            if (goodCombatDistance) {
                player.getMovementHandler().stopMovement();
                return;
            }
            /*
             * Check our regular combat styles for distance
             */
            if (player.getCombatType() == CombatStyle.MELEE && player.goodDistance(otherX, otherY, player.getX(), player.getY(), 1)) {
                if (otherX != player.getX() && otherY != player.getY()) {
                    stopDiagonal(player, otherX, otherY);
                    return;
                } else {
                    player.getMovementHandler().stopMovement();
                    return;
                }
            }
            Location followLoc = null;

            if (following.size() == 1) {
                Location[] locs = {new Location(otherX + 1, otherY, player.getZ()), new Location(otherX - 1, otherY, player.getZ()), new Location(otherX, otherY + 1, player.getZ()),
                        new Location(otherX, otherY - 1, player.getZ()),};
                for (Location i : locs) {
                    if (followLoc == null || player.getLocation().getDistance(i) < player.getLocation().getDistance(followLoc)) {
                        followLoc = i;
                    }
                }
            } else {
                followLoc = Tile.create(following.absX, following.absY, following.heightLevel).
                        closestTileOf(Tile.create(player.absX, player.absY, player.heightLevel), following.size(), following.size());
            }
            if (followLoc != null) {
                playerWalk(followLoc.getX(), followLoc.getY());
            }
        }
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
        player.getMovementHandler().reset();
        int xMove = targetX - player.getX();
        int yMove = 0;

        if (xMove == 0) {
            yMove = targetY - player.getY();
        }

        player.getMovementHandler().addToPath(new Location(player.getX() + xMove, player.getY() + yMove, 0));
    }

	/**
	 * Walk the player to the given X and Y position
	 * 
	 * @param moveX
	 *            The x position
	 * @param moveY
	 *            The y position
	 */
    public void walkTo(int moveX, int moveY) {
        player.getMovementHandler().reset();
        player.getMovementHandler().addToPath(new Location(player.getX() + moveX, player.getY() + moveY, player.getZ()));
        player.getMovementHandler().finish();
    }

	/**
	 * 
	 * @param x
	 *            The x position
	 * @param y
	 *            The y position
	 */
    public void playerWalk(int x, int y) {
        PathFinder.getPathFinder().findRoute(player, x, y, true, 1, 1);
    }
}
