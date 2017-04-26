package com.model.game.character.player.skill;

import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;
import com.model.task.Stackable;
import com.model.task.Walkable;

/**
 * A simple class to manage skill tasks so that you cannot overlap skills.
 * 
 * @author Mobster
 *
 */
public abstract class SkillTask extends ScheduledTask {
	
	private final Player player;
	
	public SkillTask(Player player, int delay, Walkable walkable, Stackable stackable) {
		this(player, delay, walkable, stackable, false);
	}
	
	public SkillTask(Player player, int delay, Walkable walkable, Stackable stackable, boolean immediate) {
		super(player, delay, immediate, walkable, stackable);
		this.player = player;
	}
	
	public SkillTask(Player player, int delay) {
		this(player, delay, Walkable.WALKABLE, Stackable.STACKABLE);
	}
	
	public SkillTask(Player player, int delay, boolean immediate) {
		this(player, delay, Walkable.WALKABLE, Stackable.STACKABLE, immediate);
	}
	
	public Player getPlayer() {
		return player;
	}

	public static boolean noInventorySpace(Player player, String skill) {
		if (player.getInventory().remaining() == 0) {
			player.getActionSender().sendMessage("You don't have enough inventory space.");
			return false;
		}
		return true;
	}
	
}
