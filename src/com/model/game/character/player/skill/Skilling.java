package com.model.game.character.player.skill;

import java.util.Optional;

import com.model.game.character.player.Player;
import com.model.game.character.player.skill.SkillHandler.Skill;
import com.model.task.events.CycleEvent;
import com.model.task.events.CycleEventHandler;

public class Skilling {
	
	Player player;
	
	private Optional<Skill> skill = Optional.empty();
	
	public Skilling(Player player) {
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
	
	public Skill getSkill() {
		return skill.orElse(null);
	}
	
	public void setSkill(Skill skill) {
		this.skill = Optional.of(skill);
	}

}