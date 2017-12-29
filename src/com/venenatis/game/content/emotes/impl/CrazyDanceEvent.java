package com.venenatis.game.content.emotes.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;

public class CrazyDanceEvent extends Task {
	
	public int ticks = 0;
	
	public CrazyDanceEvent(Player player) {
        super(player, 1, true, StackType.NEVER_STACK, BreakType.ON_MOVE);
        attach(player);
    }

	@Override
	public void execute() {
		
		Player player = (Player) getAttachment();
		if (!player.getWalkingQueue().isEmpty()) {
			player.playAnimation(Animation.RESET_ANIMATION);
			stop();
			return;
		}
		switch (ticks) {
			case 0:
				player.playAnimation(Animation.create(7537));
				break;
				
			case 5:
				player.playAnimation(Animation.create(7536));
				break;
				
			case 10:
				stop();
				break;
		}
		ticks++;
	}

}
