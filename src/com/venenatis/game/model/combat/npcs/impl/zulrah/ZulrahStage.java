package com.venenatis.game.model.combat.npcs.impl.zulrah;

import com.venenatis.game.event.CycleEvent;
import com.venenatis.game.model.entity.player.Player;

public abstract class ZulrahStage extends CycleEvent {

	protected Zulrah zulrah;

	protected Player player;

	public ZulrahStage(Zulrah zulrah, Player player) {
		this.zulrah = zulrah;
		this.player = player;
	}

}
