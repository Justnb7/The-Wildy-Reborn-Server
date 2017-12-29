package com.venenatis.game.task.impl;

import java.util.Random;

import com.venenatis.game.content.minigames.multiplayer.duel_arena.DuelArena.DuelStage;
import com.venenatis.game.location.Location;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.combat.data.SkullType;
import com.venenatis.game.model.combat.impl.PlayerDrops;
import com.venenatis.game.model.combat.impl.PlayerKilling;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.model.entity.player.account.Account;
import com.venenatis.game.model.masks.Animation;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.model.masks.UpdateFlags.UpdateFlag;
import com.venenatis.game.task.Task;
import com.venenatis.game.util.Utility;
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
	 * The death animation
	 */
	private static final Animation DEATH_ANIMATION = Animation.create(836);

	/**
	 * The player who has died.
	 */
	private Player victim;
	
	/**
	 * The state for this event.
	 */
	private int state;

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
	
	private static final String getKillMessage(Player player) {
		String username = Utility.formatName(player.getUsername());
		switch (new Random().nextInt(10)) {

		case 0:
			return "What an embarrasing performance by " + username + ".";

		case 1:
			return "A humiliating defeat for " + username + ".";

		case 2:
			return "You have defeated " + username + ".";

		case 3:
			return "The struggle for " + username + " is real.";

		case 4:
			return username + " was no match for you.";

		case 5:
			return username + " falls before your might.";

		case 6:
			return "You were clearly a better fighter than " + username + ".";

		case 7:
			return "RIP " + username + ".";

		case 8:
			return username + " didn't stand a chance against you.";

		case 9:
			return "Can anyone defeat you? Certainly not " + username + ".";

		case 10:
			return "A certain, crouching-over-face animation would be suitable for " + username + " right now.";

		}
		return null;
	}

	@Override
	public void execute() {
		if (victim == null) {
			stop();
			return;
		}
		
		// Look up most damage.
		Player killer = World.getWorld().lookupPlayerByName(victim.getCombatState().getDamageMap().getKiller());
		
		victim.getWalkingQueue().reset();
		
		switch (state) {
		case 0:
			if (PrayerHandler.isActivated(victim, PrayerHandler.RETRIBUTION) && killer != null) {
				int damage = new Random().nextInt((int) (victim.getSkills().getLevelForExperience(Skills.PRAYER) * .25));
				victim.playGraphic(Graphic.create(437, 0, 0));
				victim.take_hit(killer, damage);
			}
			victim.getCombatState().reset();
			victim.getActionSender().sendMessage("State "+state);
			break;

		case 1:
			victim.playAnimation(DEATH_ANIMATION);
			victim.getActionSender().sendMessage("State "+state);
			break;

		case 2:
			if (victim.getMinigame() != null) {
				victim.getMinigame().onDeath(victim);
				stop();
				return;
			}
			victim.getActionSender().sendMessage("State "+state);
			final boolean inDuel = victim.getAttributes().get("duel_stage") != null && victim.getAttributes().get("duel_stage") == DuelStage.FIGHTING_STAGE;
			if (inDuel) {
				victim.getActionSender().sendMessage("You have lost the duel!");
				killer.getActionSender().sendMessage("You have won the duel!");
			} else {
				victim.getActionSender().sendMessage("Oh dear, you are dead!");
				
				if(victim.getAccount().getType().loseStatusOnDeath() && victim.getRights().isHardcoreIronman(victim)) {//Double check if the victim is a Hardcore ironman for extra safety
					victim.getAccount().setType(Account.IRON_MAN_TYPE);
					victim.setRights(Rights.IRON_MAN);
					victim.message("You have fallen as a Hardcore Iron Man, your Hardcore status has been revoked.");
				}
				
				// ken if statement
				// player = victim, probably add losing items for player in this
				// if else block
				if (victim.isSkulled()) {
					Combat.skull(victim, SkullType.NONE, 0);
				}
			}
			if (killer != null && killer.isPlayer() && !inDuel) {
				killer.getActionSender().sendMessage(getKillMessage(victim));
			}
			PlayerDrops.dropItems(victim, killer);
			PrayerHandler.resetAll(victim);
			for (int index = 0; index < Skills.SKILL_COUNT; index++) {
				victim.getSkills().setLevel(index, victim.getSkills().getLevelForExperience(index));
				victim.getActionSender().sendSkills();
			}
			if (inDuel) {
				victim.setDuelsLost(victim.getDuelsLost() + 1);
				killer.setDuelsWon(killer.getDuelsWon() + 1);
				victim.getActionSender().sendMessage("You now have won " + victim.getDuelsWon() + " duels and lost " + victim.getDuelsLost() + " duels.");
				killer.getActionSender().sendMessage("You now have won " + killer.getDuelsWon() + " duels and lost " + killer.getDuelsLost() + " duels.");
				victim.getDuelArena().finishDuelMatch();
				killer.getDuelArena().invokeDuelVictory();
			} else {
				victim.setTeleportTarget(new Location(3087, 3500, 0));
			}
			victim.getCombatState().getDamageMap().resetDealtDamage();
			break;

		case 3:
			victim.getActionSender().sendMessage("State "+state);
			victim.getCombatState().setDead(false);
			
			//Reset misc actions
			PlayerKilling.removeHostFromList(victim, victim.getHostAddress());
			victim.setUsingSpecial(false);
			victim.setCurrentKillStreak(0);
			victim.getActionSender().sendWidget(2, 0);
			victim.getActionSender().sendWidget(3, 0);
			victim.setInfection(0);
			victim.setDefaultAnimations();
			victim.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			stop();
			break;
		}

		state++;
	}
}