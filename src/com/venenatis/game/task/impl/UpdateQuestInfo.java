package com.venenatis.game.task.impl;

import com.venenatis.game.content.quest_tab.QuestTabPageHandler;
import com.venenatis.game.content.quest_tab.QuestTabPages;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.ScheduledTask;
import com.venenatis.game.world.World;

public class UpdateQuestInfo extends ScheduledTask {

	public UpdateQuestInfo() {
		super(60);
	}

	@Override
	public void execute() {
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null) {
				QuestTabPageHandler.write(player, QuestTabPages.HOME_PAGE);
			}
		}

	}

}