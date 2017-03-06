package com.model.task.impl;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.content.questtab.QuestTabPageHandler;
import com.model.game.character.player.content.questtab.QuestTabPages;
import com.model.task.ScheduledTask;

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