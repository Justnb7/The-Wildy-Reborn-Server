package com.model.game.character.player.skill;

import com.model.game.character.player.Player;
import com.model.task.Stackable;
import com.model.task.Walkable;

/**
 * A cycling skill task
 * 
 * @author Mobster
 *
 */
public abstract class CycleSkillTask extends SkillTask {

	public CycleSkillTask(Player player, int delay, Walkable walkable, Stackable stackable, boolean immediate) {
		super(player, delay, walkable, stackable, immediate);
	}

	@Override
	public void execute() {
		if (meetsRequirements()) {
			iteration();
		} else {
			stop();
		}
	}

	/**
	 * Meets the requirements to continue cycling
	 * 
	 * @return The requirements are met to continue cycling
	 */
	public abstract boolean meetsRequirements();

	/**
	 * Iterates the next cycle
	 */
	public abstract void iteration();

}
