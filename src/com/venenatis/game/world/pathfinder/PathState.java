package com.venenatis.game.world.pathfinder;

import java.util.ArrayDeque;
import java.util.Deque;

public class PathState {
	
	 private Deque<BasicPoint> points = new ArrayDeque<BasicPoint>();
	private int state = 0;
	private boolean reached;
	public int walkToData = 0;

	public Deque<BasicPoint> getPoints() {
		return points;
	}

	public void routeFailed() {
		this.state = 1;
	}

	public void routeIncomplete() {
		this.state = 2;
	}

	public boolean isRouteFound() {
		return state != 1;
	}

	public void setRouteFound(int state) {
		this.state = state;
	}

	public boolean isRouteIncomplete() {
		return state == 2;
	}

	public boolean hasReached() {
		return reached;
	}

	public void setReached(boolean reached) {
		this.reached = reached;
	}

}
