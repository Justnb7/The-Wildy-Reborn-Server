package com.venenatis.game.task.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;

public class SecondTask extends Task {
	
	public SecondTask() {
		super(2);
	}

	public void execute() {
		for (Player p : World.getWorld().getPlayers())
			if (p != null)
				timers(p);
	}

	public void timers(Player p) {
		if (p.getDfsTimer() > 0) {
			p.setDfsTimer(p.getDfsTimer() -1);
		}
	}

}
