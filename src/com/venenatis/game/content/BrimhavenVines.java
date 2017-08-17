package com.venenatis.game.content;

import com.venenatis.game.model.entity.player.Player;

/**
 * @author Violation
 */
public class BrimhavenVines {

	public static boolean handleBrimhavenVines(Player player, int objectType) {
		switch (objectType) {
		case 12987:
		case 12986:
			BrimhavenVines.moveThroughVinesX(player, 3213, -2, 0, 2, 0);
			return true;
		case 21731:
			BrimhavenVines.moveThroughVinesX(player, 2689, 2, 0, -2, 0);
			return true;
		case 21732:
			BrimhavenVines.moveThroughVinesY(player, 9568, 0, 2, 0, -2);
			return true;
		case 21733:
			BrimhavenVines.moveThroughVinesX(player, 2672, 2, 0, -2, 0);
			return true;
		case 21734:
			BrimhavenVines.moveThroughVinesX(player, 2675, 2, 0, -2, 0);
			return true;
		case 21735:
			BrimhavenVines.moveThroughVinesX(player, 2694, 2, 0, -2, 0);
			return true;
		}
		return false;
	}

	public static void moveThroughVinesX(Player player, int originX, int x1, int y1, int x2, int y2) {
		if (player.getX() <= originX) {
			player.getPlayerFollowing().walkTo(x1, y1);
		} else {
			player.getPlayerFollowing().walkTo(x2, y2);
		}
	}

	public static void moveThroughVinesY(Player player, int originY, int x1, int y1, int x2, int y2) {
		if (player.getY() <= originY) {
			player.getPlayerFollowing().walkTo(x1, y1);
		} else {
			player.getPlayerFollowing().walkTo(x2, y2);
		}
	}

}
