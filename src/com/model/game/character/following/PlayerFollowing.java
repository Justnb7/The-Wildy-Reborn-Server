package com.model.game.character.following;

import com.model.game.character.Entity;
import com.model.game.character.combat.combat_data.CombatStyle;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.walking.PathFinder;
import com.model.game.location.Location;
import com.model.utility.Utility;
import com.model.utility.cache.map.Region;

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
	
	/**
	 * The player following another player.
	 * @param forCombat
	 *        Checks if the player is in combat
	 * @param following
	 *        The entity we're following
	 */
    public void followPlayer(boolean forCombat, Entity following) {
    	//The combat distance
        int cbDist = player.followDistance;
        
        //Whenever out target is null or death stop the following task
        if (following == null || following.isDead()) {
            player.setFollowing(null);
            return;
        }
        
        //We cannot follow someone when being frozen
        if (player.frozen()) {
            return;
        }

        //Whenever we're null or death stop the following task
        if (player.isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
            player.setFollowing(null);
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

        //When both on the same tile move the playerto another direction
        boolean sameSpot = (player.absX == otherX && player.absY == otherY);
        if (sameSpot) {
            if (Region.getClipping(player.getX() - 1, player.getY(), player.heightLevel, -1, 0)) {
                walkTo(-1, 0);
            } else if (Region.getClipping(player.getX() + 1, player.getY(), player.heightLevel, 1, 0)) {
                walkTo(1, 0);
            } else if (Region.getClipping(player.getX(), player.getY() - 1, player.heightLevel, 0, -1)) {
                walkTo(0, -1);
            } else if (Region.getClipping(player.getX(), player.getY() + 1, player.heightLevel, 0, 1)) {
                walkTo(0, 1);
            }
            return;
        }

        //Start facing the player you want to follow
        player.faceEntity(following);

        /**
         * Out of combat following, possibly a bug or 2?
         */
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
                return;
            }
        } else {

            boolean goodCombatDistance = player.goodDistance(otherX, otherY, player.getX(), player.getY(), cbDist);
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

            Location[] locs = { new Location(otherX + 1, otherY, player.getZ()), new Location(otherX - 1, otherY, player.getZ()), new Location(otherX, otherY + 1, player.getZ()),
                    new Location(otherX, otherY - 1, player.getZ()), };

            Location followLoc = null;

            for (Location i : locs) {
                if (followLoc == null || player.getPosition().getDistance(i) < player.getPosition().getDistance(followLoc)) {
                    followLoc = i;
                }
            }
            if (followLoc != null) {
                playerWalk(followLoc.getX(), followLoc.getY());
                player.getMovementHandler().followPath = true;
            }
        }
    }
    
    /**
     * The player following an npc
     * @param targ
     *        The npc we're following
     */
    public void followNpc(Entity targ) {

    	//If the npc is either null or death we stop the following task
        if (targ == null || targ.isDead()) {
            player.setFollowing(null);
            return;
        }
        
        //Whenever the player is frozen he cannot follow
        if (player.frozen()) {
            return;
        }
        
        //If the player is death we cannot follow an npc
        if (player.isDead() || player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
            player.setFollowing(null);
            return;
        }

        //Calculate the x and y offsets
        int otherX = targ.getX();
        int otherY = targ.getY();

        boolean goodCombatDist = player.goodDistance(otherX, otherY, player.getX(), player.getY(), player.followDistance);

        //If we're not stop the following task
        if (!player.goodDistance(otherX, otherY, player.getX(), player.getY(), 25)) {
            player.setFollowing(null);
            return;
        }

        
		if (goodCombatDist) {
			return;
		}

        NPC npc = (NPC) targ;

        boolean inside = false;
        for (Location tile : npc.getTiles()) {
            if (player.absX == tile.getX() && player.absY == tile.getY()) {
                inside = true;
                break;
            }
        }

        if (!inside) {
            for (Location npcloc : npc.getTiles()) {
                double distance = npcloc.distance(player.getPosition());
                if (distance <= player.followDistance) {
                    player.getMovementHandler().stopMovement();
                    return;
                }
            }
        }

        if (inside) {
            int r = Utility.getRandom(3);
            switch (r) {
            case 0:
                walkTo(0, -1);
                break;
            case 1:
                walkTo(0, 1);
                break;
            case 2:
                walkTo(1, 0);
                break;
            case 3:
                walkTo(-1, 0);
                break;
            }
        } else {
        	Location[] locs = { new Location(otherX + 1, otherY, player.getZ()), new Location(otherX - 1, otherY, player.getZ()), new Location(otherX, otherY + 1, player.getZ()),
                    new Location(otherX, otherY - 1, player.getZ()), };

            Location followLoc = null;

            for (Location i : locs) {
                if (followLoc == null || player.getPosition().getDistance(i) < player.getPosition().getDistance(followLoc)) {
                    followLoc = i;
                }
            }

            if (followLoc != null) {
                playerWalk(followLoc.getX(), followLoc.getY());
                player.getMovementHandler().followPath = true;
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
