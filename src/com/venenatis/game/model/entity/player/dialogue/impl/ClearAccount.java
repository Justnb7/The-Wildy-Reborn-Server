package com.venenatis.game.model.entity.player.dialogue.impl;

import com.venenatis.game.content.achievements.AchievementList;
import com.venenatis.game.content.quest_tab.QuestTabPageHandler;
import com.venenatis.game.content.quest_tab.QuestTabPages;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.dialogue.Dialogue;
import com.venenatis.game.model.entity.player.dialogue.DialogueType;
import com.venenatis.game.model.entity.player.dialogue.Expression;

public class ClearAccount extends Dialogue {
	
	/**
	 * The id of the npc
	 */
	public static final int NPC_ID = 317;

	@Override
	protected void start(Object... parameters) {
		send(DialogueType.NPC, NPC_ID, Expression.CALM_TALK, "Are you sure you want to become an iron man?", "The process that has been made on your account will be lost.");
	}

	@Override
	public void next() {
		if (isPhase(0)) {
			send(DialogueType.CHOICE, DEFAULT_OPTION_TITLE, "Yes", "No");
		}
	}

	@Override
	public void select(int index) {
		switch(index) {
		case 1: //Yes
			clearProgress();
			stop();
			break;
		case 2: //No
			player.getActionSender().removeAllInterfaces();
			stop();
			break;
		}
	}
	
	private final void clearProgress() {
		player.getInventory().clear(true);
		player.getEquipment().clear(true);
		player.setDefaultAnimations();
		player.getBank().clear(true);
		for (int skill = 0; skill < Skills.SKILL_COUNT; skill++) {
			player.getSkills().setMaxLevel(skill, 1);
			player.getSkills().setMaxLevel(Skills.HITPOINTS, 10);
		}
		for (AchievementList data : AchievementList.values()) {
			player.getPlayerAchievements().put(data, 0);
		}
		player.setAchievementPoints(0);
		player.setKillCount(0);
		player.setDeathCount(0);
		player.setPkPoints(0);
		player.setSlayerPoints(0);
		player.setVotePoints(0);
		player.setCurrentKillStreak(0);
		player.setHighestKillStreak(0);
		player.getKillTracker().loadDefault();
		player.setSlayerTask("none");
		player.setSlayerTaskAmount(0);
		player.setSlayerTaskDifficulty(0);
		player.setSlayerTasksCompleted(0);
		player.setFirstSlayerTask(false);
		player.setFirstBossSlayerTask(false);
		player.setLastSlayerTask("none");
		player.setSlayerStreak(0);
        QuestTabPageHandler.write(player, QuestTabPages.HOME_PAGE);
	}

}
