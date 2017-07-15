package com.venenatis.game.task.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.world.World;

public class RestoreSpecialStats extends Task {

	public RestoreSpecialStats() {
		super(8);
	}

	private int counter = 0;

	@Override
	public void execute() {
		counter++;
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null) {
					if (counter >= 4) {
						if (player.getSpecialAmount() < 100) {
							player.setSpecialAmount(player.getSpecialAmount() + 10);
							if (player.getSpecialAmount() > 100) {
								player.setSpecialAmount(100);
							}
							player.getSpecial().update();
					}
				}
			}
		}
				
		if (counter >= 4)
			counter = 0;
	}
}