package com.model.task.impl;

import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;
import com.model.task.Stackable;
import com.model.task.Walkable;

public class TradeNotificationTask extends ScheduledTask {
	
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