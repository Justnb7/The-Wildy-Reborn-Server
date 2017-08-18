package com.venenatis.game.task.impl;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.content.achievements.AchievementHandler;
import com.venenatis.game.content.achievements.AchievementList;
import com.venenatis.game.content.activity.minigames.MinigameHandler;
import com.venenatis.game.location.Area;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.combat.data.SkullType;
import com.venenatis.game.model.combat.impl.PlayerDrops;
import com.venenatis.game.model.combat.impl.PlayerKilling;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.entity.player.account.Account;
import com.venenatis.game.model.entity.player.controller.Controller;
import com.venenatis.game.model.entity.player.controller.ControllerManager;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.task.Task;
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
			if (killer != null && Area.inWilderness(killer)) {
				dropPlayerItems(victim, killer);
				reset(victim);
				victim.setTeleportTarget(Constants.RESPAWN_PLAYER_LOCATION);
			} else {
				/*Here we add support for none wilderness related activities*/
				if (victim.isDueling()) {
					victim.getDuelArena().onDeath();
				} else if (MinigameHandler.search(victim).isPresent()) {
					MinigameHandler.search(victim).ifPresent($it -> $it.onDeath(victim));
				} else {
					// this is non wilderness death
					if (!victim.canKeepItems()) {
						dropPlayerItems(victim, killer);
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
		if(victim.getAccount().getType().loseStatusOnDeath()) {
			victim.getAccount().setType(Account.HARDCORE_IRON_MAN_TYPE);
			victim.setRights(Rights.HARDCORE_IRON_MAN);
		}
		victim.getCombatState().setDead(false);
		victim.freeze(0);
		victim.setCurrentKillStreak(0);
		PrayerHandler.resetAllPrayers(victim);
		victim.setSpecialAmount(100);
		victim.setUsingSpecial(false);
		PlayerKilling.removeHostFromList(victim, victim.getHostAddress());
		Combat.skull(victim, SkullType.NONE, 0);
		victim.getCombatState().getDamageMap().resetDealtDamage();
		victim.playAnimation(Animation.create(-1));
		AchievementHandler.activate(victim, AchievementList.TIME_FOR_LESSON, 1);
	}
	
	/**
	 * Are we allowed to drop the victims items?
	 * 
	 * @param victim
	 *            The player losing his items
	 */
	public void dropPlayerItems(Player victim, Entity attacker) {
		
		/**
		 * Are admins allowed to keep their items upon death?
		 */
		boolean admin_keeps_items = victim.getUsername().equalsIgnoreCase("patrick") && victim.inDebugMode() || victim.getUsername().equalsIgnoreCase("matthew") ? true : false;
		
		controller = victim.getController() == null ? ControllerManager.DEFAULT_CONTROLLER : victim.getController();
		if (!controller.isSafe() && !admin_keeps_items) {
			PlayerDrops.dropItems(victim, attacker);
		}
	}
}