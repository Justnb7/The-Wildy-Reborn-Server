package com.venenatis.game.task.impl;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.DeathDropHandler;
import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.content.rewards.WildernessRewards;
import com.venenatis.game.location.Area;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.controller.Controller;
import com.venenatis.game.model.entity.player.controller.ControllerManager;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.RandomGenerator;
import com.venenatis.game.world.World;


/**
 * The death event handles player and npc deaths. Drops loot, does animation,
 * teleportation, etc.
 * 
 * @author Graham
 * @author Patrick van Elderen
 * 
 */
public class DeathTask extends Task {

	/**
	 * The player who has died.
	 */
	private Player victim;
	
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
	public DeathTask(Player victim, int ticks) {
		super(ticks);
		this.victim = victim;
		Combat.resetCombat(victim);
	}

	@Override
	public void execute() {
		stop();
		if (victim.getCombatState().isDead()) {
			Player killer = World.getWorld().lookupPlayerByName(victim.getCombatState().getDamageMap().getKiller());
			if (killer != null && Area.inWilderness(killer) ) {
				switch (RandomGenerator.nextInt(10)) {
				default:
				case 0:
					killer.getActionSender().sendMessage("You have defeated " + victim.getUsername() + ".");
					break;
				case 1:
					killer.getActionSender().sendMessage("Can anyone defeat you? Certainly not " + victim.getUsername() + ".");
					break;
				case 2:
					killer.getActionSender().sendMessage(victim.getUsername() + " falls before your might.");
					break;
				case 3:
					killer.getActionSender().sendMessage("A humiliating defeat for " + victim.getUsername() + ".");
					break;
				case 4:
					killer.getActionSender().sendMessage("You were clearly a better fighter than " + victim.getUsername() + ".");
					break;
				case 5:
					killer.getActionSender().sendMessage(victim.getUsername() + " has won a free ticket to Edgeville.");
					break;
				case 6:
					killer.getActionSender().sendMessage("It's all over for " + victim.getUsername() + ".");
					break;
				case 7:
					killer.getActionSender().sendMessage("With a crushing blow you finish " + victim.getUsername() + ".");
					break;
				case 8:
					killer.getActionSender().sendMessage(victim.getUsername() + " regrets the day they met you in combat.");
					break;
				case 9:
					killer.getActionSender().sendMessage(victim.getUsername() + " didn't stand a chance against you.");
					break;
				}
				WildernessRewards.receive_reward(killer, victim);
			} else {
				/*Here we add support for none wilderness related activities*/
				if (victim.isDueling()) {
					victim.getDuelArena().onDeath();
				} else if (MinigameHandler.search(victim).isPresent()) {
					MinigameHandler.search(victim).ifPresent($it -> $it.onDeath(victim));
				} else {
					if (!victim.canKeepItems()) {
						dropPlayerItems(victim);
					}
					victim.setTeleportTarget(Constants.RESPAWN_PLAYER_LOCATION);
					reset(victim);
				}
			}
		}
	}
	
	public static void reset(Player victim) {
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
		WildernessRewards.clearList(victim);
		victim.getCombatState().getDamageMap().resetDealtDamage();
		victim.playAnimation(Animation.create(-1));
	}
	
	/**
	 * Are we allowed to drop the victims items?
	 * 
	 * @param victim
	 *            The player losing his items
	 */
	public void dropPlayerItems(Player victim) {
		/**
		 * Are admins allowed to keep their items upon death?
		 */
		boolean admin_keeps_items = victim.getUsername().equalsIgnoreCase("patrick") || victim.getUsername().equalsIgnoreCase("matthew") ? true : false;
		
		controller = victim.getController() == null ? ControllerManager.DEFAULT_CONTROLLER : victim.getController();
		if (!controller.isSafe() && !admin_keeps_items) {
			DeathDropHandler.handleDeathDrop(victim);
		}
	}
}