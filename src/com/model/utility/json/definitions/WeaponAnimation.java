package com.model.utility.json.definitions;

import java.util.HashMap;
import java.util.Map;

import com.model.game.character.player.Player;
import com.model.game.item.Item;

/**
 * The container class that represents all the weapon equipment animtions.
 *
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 * Credits to Lare96
 */
public final class WeaponAnimation {

    /**
     * The hash collection of weapon animations.
     */
    public static final Map<Integer, WeaponAnimation> ANIMATIONS = new HashMap<>();

    /**
     * The standing animation for this weapon animation.
     */
    private final int standing;
    
    /**
     * The turning animation for this weapon animation.
     */
    private final int turning;
    
    /**
     * The turning animation for this weapon animation.
     */
    private final int turn180;
    
    /**
     * The turning animation for this weapon animation.
     */
    private final int turn90ClockWise;
    
    /**
     * The turning animation for this weapon animation.
     */
    private final int turn90CounterClockWise;
    
    /**
     * The walking animation for this weapon animation.
     */
    private final int walking;

    /**
     * The running animation for this weapon animation.
     */
    private final int running;

	/**
	 * Creates a new {@link WeaponAnimation}.
	 * 
	 * @param standing
	 *            the standing animation for this weapon animation.
	 * @param turning
	 *            the turning animation for this weapon animation.
	 * @param turn180
	 *            the turning animation for this weapon animation.
	 * @param turn90CW
	 *            the turning animation for this weapon animation.
	 * @param turn90CCW
	 *            the turning animation for this weapon animation.
	 * @param walking
	 *            the walking animation for this weapon animation.
	 * @param running
	 *            the running animation for this weapon animation.
	 */
    public WeaponAnimation(int standing, int turning, int turn180, int turn90CW, int turn90CCW, int walking, int running) {
        this.standing = standing;
        this.turning = turning;
        this.turn180 = turn180;
        this.turn90ClockWise = turn90CW;
        this.turn90CounterClockWise = turn90CCW;
        this.walking = walking;
        this.running = running;
    }

    /**
     * A substitute for {@link Object#clone()} that creates another 'copy' of
     * this instance. The created copy is <i>safe</i> meaning it does not hold
     * <b>any</b> references to the original instance.
     *
     * @return a reference-free copy of this instance.
     */
    public WeaponAnimation copy() {
        return new WeaponAnimation(standing, turning, turn180, turn90ClockWise, turn90CounterClockWise, walking, running);
    }

    /**
     * The method executed when weapon {@code item} is equipped that assigns a
     * weapon animation to {@code player}.
     *
     * @param player
     *            the player equipping the item.
     * @param item
     *            the item the player is equipping.
     */
    public static void execute(Player player, Item item) {
        if (item == null)
            return;
        WeaponAnimation animation = ANIMATIONS.get(item.getId());
        player.setWeaponAnimation(animation == null ? null : animation.copy());
    }

    /**
     * Gets the standing animation for this weapon.
     *
     * @return the standing animation.
     */
    public int getStanding() {
        return standing;
    }
    
    /**
     * Gets the turning animation for this weapon.
     * @return the turning animation.
     */
    public int getTurn() {
    	return turning;
    }
    
    /**
     * Gets the turn 180 degrees animation for this weapon.
     * @return the turn180 animation.
     */
    public int turn180() {
    	return turn180;
    }
    
    /**
     * Gets the turn 90 degrees clock wise animation for this weapon.
     * @return
     */
    public int turn90CW() {
    	return turn90ClockWise;
    }
    
    /**
     * Gets the turn 90 degrees Counter clock wise animation
     *  for this weapon.
     * @return
     */
    public int turn90CCW() {
    	return turn90CounterClockWise;
    }

    /**
     * Gets the walking animation for this weapon.
     *
     * @return the walking animation.
     */
    public int getWalking() {
        return walking;
    }

    /**
     * Gets the running animation for this weapon.
     *
     * @return the running animation.
     */
    public int getRunning() {
        return running;
    }
}