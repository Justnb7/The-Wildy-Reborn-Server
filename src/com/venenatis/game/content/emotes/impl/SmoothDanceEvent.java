package com.venenatis.game.content.emotes.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;

public class SmoothDanceEvent extends Task {
	
	public int ticks = 0;
	
	public SmoothDanceEvent(Player player) {
        super(1, true);
        attach(player);
    }

	@Override
	public void execute() {
		Player player = (Player) this.getAttachment();
		if (!player.getWalkingQueue().isEmpty()) {
			player.playAnimation(Animation.RESET_ANIMATION);
			stop();
			return;
		}
		switch (ticks) {
			case 0:
				player.playAnimation(Animation.create(7535));
				break;
				
			case 2:
				player.playAnimation(Animation.create(7534));
				break;
				
			case 4:
				player.playAnimation(Animation.create(7533));
				break;
				
			case 9:
				stop();
				break;
		}
		ticks++;
	}

}
