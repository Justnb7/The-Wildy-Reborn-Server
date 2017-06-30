package com.venenatis.game.task.impl;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.game.task.Stackable;
import com.venenatis.game.task.Walkable;

public class TradeNotificationTask extends Task {
	
	private final Player player;
	private int time = 11;
	
	public TradeNotificationTask(Player player) {
		super(player, 1, true, Walkable.NON_WALKABLE, Stackable.NON_STACKABLE);
		this.player = player;
	}

	@Override
	public void execute() {
		if (!player.getTradeSession().isTrading()) {
			stop();
			return;
		}
		
		if (time <= 0) {
			stop();
			return;
		}
		
		time--;

		player.getActionSender().sendString(time % 2 == 0 ? "<col=ff0000>Trade has been modified!" : "", 33030);			
		
	}

	@Override
	public void onStop() {
		player.getActionSender().sendString("", 33030);		
	}

}