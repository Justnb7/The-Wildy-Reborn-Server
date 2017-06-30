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
import com.venenatis.game.task.EntityEvent;


/**
 * The death event handles player and npc deaths. Drops loot, does animation,
 * teleportation, etc.
 * 
 * @author Graham
 * 
 */
public class DeathEvent extends EntityEvent {

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
	public DeathEvent(Player victim) {
		super(victim, 1);
		this.ticks = 6;
		this.victim = victim;
		victim.setDead(true);
		Combat.resetCombat(victim);
	}

	@Override
	public void execute() {
		if (!shouldRun()) {
			this.stop();
			return;
		}
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
					if (!canKeepItems()) {
						dropPlayerItems(victim);
					}
				}
			} else if (ticks == 1) {
				if (victim != null) {
					/*p.getActionSender().sendWidget(2, 0);
					p.getActionSender().sendWidget(3, 0);*/
					victim.getActionQueue().clearRemovableActions();
					victim.setTeleportTarget(Constants.RESPAWN_PLAYER_LOCATION);
					victim.getSkills().setLevel(Skills.HITPOINTS, victim.getSkills().getLevelForExperience(Skills.HITPOINTS));
					victim.getActionSender().sendMessage("Oh dear, you are dead!");
					victim.setDead(false);
					victim.freeze(0);
					PrayerHandler.resetAllPrayers(victim);
					victim.setSpecialAmount(100);
					victim.setUsingSpecial(false);
					victim.getDamageMap().resetDealtDamage();
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
	
	/**
	 * Determines if this player can keep their items upon death.
	 *
	 * @return {@code true} If they can keep their items on death. {@code false}
	 *         If they can not.
	 */
	public boolean canKeepItems() {
		if (Constants.ADMIN_CAN_KEEP_ITEMS) {
			return true;
		} else if (MinigameHandler.execute(victim, false, $it -> $it.canKeepItems())) {
			return true;
		} else {
			return false;
		}
	}
}