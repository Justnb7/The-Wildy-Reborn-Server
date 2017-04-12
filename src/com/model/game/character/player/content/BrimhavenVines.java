package com.model.game.character.player.content;

import com.model.game.character.player.Player;

/**
 * @author Violation
 */
public class BrimhavenVines {

	public static void handleBrimhavenVines(Player c, int objectType) {
		switch (objectType) {
		case 12987:
		case 12986:
			BrimhavenVines.moveThroughVinesX(c, 3213, -2, 0, 2, 0);
			break;
		case 21731:
			BrimhavenVines.moveThroughVinesX(c, 2689, 2, 0, -2, 0);
			break;
		case 21732:
			BrimhavenVines.moveThroughVinesY(c, 9568, 0, 2, 0, -2);
			break;
		case 21733:
			BrimhavenVines.moveThroughVinesX(c, 2672, 2, 0, -2, 0);
			break;
		case 21734:
			BrimhavenVines.moveThroughVinesX(c, 2675, 2, 0, -2, 0);
			break;
		case 21735:
			BrimhavenVines.moveThroughVinesX(c, 2694, 2, 0, -2, 0);
			break;
		}
	}

	public static void moveThroughVinesX(Player c, int originX, int x1, int y1, int x2, int y2) {
		if (c.getX() <= originX) {
			c.getPlayerFollowing().walkTo(x1, y1);
		} else {
			c.getPlayerFollowing().walkTo(x2, y2);
		}
	}

	public static void moveThroughVinesY(Player c, int originY, int x1, int y1, int x2, int y2) {
		if (c.getY() <= originY) {
			c.getPlayerFollowing().walkTo(x1, y1);
		} else {
			c.getPlayerFollowing().walkTo(x2, y2);
		}
	}

}
