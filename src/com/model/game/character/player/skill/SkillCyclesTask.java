package com.model.game.character.player.skill;

import java.util.Optional;

import com.model.game.character.player.Player;
import com.model.task.events.CycleEvent;
import com.model.task.events.CycleEventHandler;

public class SkillCyclesTask {
	
	Player player;
	
	private Optional<Integer> skill = Optional.empty();
	
	public SkillCyclesTask(Player player) {
		this.player = player;
	}
	
	public void add(CycleEvent event, int ticks) {
		CycleEventHandler.getSingleton().addEvent(this, event, ticks);
	}
	
	public void stop() {
		CycleEventHandler.getSingleton().stopEvents(this);
		skill = Optional.empty();
	}
	
	public boolean isSkilling() {
		return skill.isPresent();
	}
	
	public Integer getSkill() {
		return skill.orElse(null);
	}
	
	public void setSkill(int skill) {
		this.skill = Optional.of(skill);
	}
	
	/**
	 * Returns if a skill is currently active
	 * 
	 * @param player
	 *            The {@link Player} To check if the skill is active for
	 * @param skill
	 *            The id of the skill to check for
	 * @return If the skill is currently active
	 */
	public static boolean isSkillActive(Player player, int skill) {
		return player.getAttribute("skill_" + skill + "_active", false);
	}

	/**
	 * Sets a skill active/inactive
	 * 
	 * @param player
	 *            The {@link Player} to set the skill active/inactive for
	 * @param skill
	 *            The id of the skill
	 * @param active
	 *            The active status of the skill
	 */
	public static void setSkillActive(Player player, int skill, boolean active) {
		player.setAttribute("skill_" + skill + "_active", active);
	}

}