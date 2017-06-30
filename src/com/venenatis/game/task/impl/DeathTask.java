package com.venenatis.game.task.impl;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.DeathDropHandler;
import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.controller.Controller;
import com.venenatis.game.model.entity.player.controller.ControllerManager;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;


/**
 * The death event handles player and npc deaths. Drops loot, does animation,
 * teleportation, etc.
 * 
 * @author Graham
 * 
 */
public class DeathTask extends Task {

	/**
	 * The player who has died.
	 */
	private Player victim;

	/**
	 * The remaining ticks
	 */
	private int ticks;
	
	/**
	 * The controller for this event.
	 */
	private Controller controller;

	/**
	 * Creates the death event for the specified entity.
	 * 
	 * @param entity
	 *            The player whose death has just happened.
	 */
	public DeathTask(Player victim) {
		super(victim, 1);
		this.ticks = 6;
		this.victim = victim;
		victim.getCombatState().setDead(true);
		Combat.resetCombat(victim);
	}

	@Override
	public void execute() {
		if (ticks > 0) {
			ticks--;
			if (ticks == 5) {
				victim.playAnimation(Animation.create(0x900));
			} else if (ticks == 3) {
				if (victim.isDueling()) {
					victim.getDuelArena().onDeath();
				} else if (MinigameHandler.search(victim).isPresent()) {
					MinigameHandler.search(victim).ifPresent($it -> $it.onDeath(victim));
				} else {
					if (!victim.canKeepItems()) {
						dropPlayerItems(victim);
					}
					victim.setTeleportTarget(Constants.RESPAWN_PLAYER_LOCATION);
				}
			} else if (ticks == 1) {
				if (victim != null) {
					victim.getActionSender().sendWidget(2, 0);
					victim.getActionSender().sendWidget(3, 0);
					victim.getActionQueue().clearRemovableActions();
					victim.getSkills().setLevel(Skills.HITPOINTS, victim.getSkills().getLevelForExperience(Skills.HITPOINTS));
					victim.getActionSender().sendMessage("Oh dear, you are dead!");
					victim.getCombatState().setDead(false);
					victim.freeze(0);
					PrayerHandler.resetAllPrayers(victim);
					victim.setSpecialAmount(100);
					victim.setUsingSpecial(false);
					victim.getCombatState().getDamageMap().resetDealtDamage();
				}
			} else if (ticks == 0) {
				victim.playAnimation(Animation.create(-1));
			}
		} else {
			this.stop();
		}
	}
	
	/**
	 * Are we allowed to drop the victims items?
	 * 
	 * @param victim
	 *            The player losing his items
	 */
	public void dropPlayerItems(Player victim) {
		controller = victim.getController() == null ? ControllerManager.DEFAULT_CONTROLLER : victim.getController();

		if (!controller.isSafe()) {
			DeathDropHandler.handleDeathDrop(victim);
		}
	}
}