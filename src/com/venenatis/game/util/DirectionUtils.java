package com.venenatis.game.util;

/**
 * A utility class for direction-related methods.
 * @author Graham Edgecombe
 *
 */
public class DirectionUtils {

	/**
	 * Get a direction by the coordinate modifiers (must be at least -1 and at
	 * most 1)
	 *
	 * @param dx
	 *            the x coordinate modifier
	 * @param dy
	 *            the y coordinate modifier
	 * @return the walking direction (denoted as -1 for none or 0-8 for the
	 *         player update paket direction index)
	 */
	public static int direction(int dx, int dy) {
		if(dx < 0) {
			if(dy < 0) {
				return 5;
			} else if(dy > 0) {
				return 0;
			} else {
				return 3;
			}
		} else if(dx > 0) {
			if(dy < 0) {
				return 7;
			} else if(dy > 0) {
				return 2;
			} else {
				return 4;
			}
		} else {
			if(dy < 0) {
				return 6;
			} else if(dy > 0) {
				return 1;
			} else {
				return -1;
			}
		}
	}

}