package com.model.game.location;

import com.model.game.character.player.Boundary;
import com.model.game.character.player.Player;

public class Area {
	
	private final Player player;
	
	public Area(Player player) {
		this.player = player;
	}
	
	public boolean inBarrows() {
		return (player.getX() > 3539 && player.getX() < 3582 && player.getY() >= 9675 && player.getY() < 9722) || player.getX() > 3532 && player.getX() < 3546 && player.getY() > 9698 && player.getY() < 9709;
	}
	
	public boolean inWild() {
		if(((player.getX() > 2941 && player.getX() < 3392 && player.getY() > 3524 && player.getY() < 3968 || player.getX() > 2941 && player.getX() < 3392 && player.getY() > 9918 && player.getY() < 10366))){
			return true;
		}
		return false;
	}
	
	public boolean inMulti() {
		if (Boundary.isIn(player, Boundary.KRAKEN)) {
			return true;
		}
		
		if (Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS) || Boundary.isIn(player, Boundary.SCORPIA_PIT)) {
			return true;
		}
		
		if ((player.getX() >= 3136 && player.getX() <= 3327 && player.getY() >= 3519 && player.getY() <= 3607) || (player.getX() >= 3190 && player.getX() <= 3327 && player.getY() >= 3648 && player.getY() <= 3839)
				|| (player.getX() >= 3200 && player.getX() <= 3390 && player.getY() >= 3840 && player.getY() <= 3967) || (player.getX() >= 2992 && player.getX() <= 3007 && player.getY() >= 3912 && player.getY() <= 3967)
				|| (player.getX() >= 2946 && player.getX() <= 2959 && player.getY() >= 3816 && player.getY() <= 3831) || (player.getX() >= 3008 && player.getX() <= 3199 && player.getY() >= 3856 && player.getY() <= 3903)
				|| (player.getX() >= 2824 && player.getX() <= 2944 && player.getY() >= 5258 && player.getY() <= 5369) || (player.getX() >= 3008 && player.getX() <= 3071 && player.getY() >= 3600 && player.getY() <= 3711)
				|| (player.getX() >= 3072 && player.getX() <= 3327 && player.getY() >= 3608 && player.getY() <= 3647) || (player.getX() >= 2624 && player.getX() <= 2690 && player.getY() >= 2550 && player.getY() <= 2619)
				|| (player.getX() >= 2371 && player.getX() <= 2422 && player.getY() >= 5062 && player.getY() <= 5117) || (player.getX() >= 2896 && player.getX() <= 2927 && player.getY() >= 3595 && player.getY() <= 3630)
				|| (player.getX() >= 2892 && player.getX() <= 2932 && player.getY() >= 4435 && player.getY() <= 4464) || (player.getX() >= 2256 && player.getX() <= 2287 && player.getY() >= 4680 && player.getY() <= 4711)
				|| (player.getX() >= 2962 && player.getX() <= 3006 && player.getY() >= 3621 && player.getY() <= 3659)
				|| (player.getX() >= 3155 && player.getX() <= 3214 && player.getY() >= 3755 && player.getY() <= 3803)
				|| (player.getX() >= 2932 && player.getX() <= 2992 && player.getY() >= 9745 && player.getY() <= 9825)
				|| (player.getX() >= 2680 && player.getX() <= 2750 && player.getY() >= 3685 && player.getY() <= 3765)) {
			return true;
		}
		return false;
	}
	
	public boolean inDuelArena() {
		if ((player.getX() > 3322 && player.getY() < 3394 && player.getY() > 3195 && player.getY() < 3291)
				|| (player.getX() > 3311 && player.getX() < 3323 && player.getY() > 3223 && player.getY() < 3248)) {
			return true;
		}
		return false;
	}
	
	public boolean inArea(int x1, int y1, int x2, int y2) {
		if (player.getX() > x1 && player.getX() < x2 && player.getY() > y1 && player.getY() < y2) {
			return true;
		}
		return false;
	}

	public boolean inFightCaves() {
		return player.getX() >= 2360 && player.getX() <= 2445 && player.getY() >= 5045 && player.getY() <= 5125;
	}

	public boolean inPcGame() {
		return player.getX() >= 2624 && player.getX() <= 2690 && player.getY() >= 2550 && player.getY() <= 2619;
	}
	
	public boolean inPcBoat() {
		return player.getX() >= 2660 && player.getX() <= 2663 && player.getY() >= 2638 && player.getY() <= 2643;
	}
	
	public boolean inCyclopsRoom() {
		return inArea(2837, 2875, 3543, 3557);
	}
	
}