package com.venenatis.game.task.impl;

import com.venenatis.game.content.DeathDropHandler;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.combat.Combat;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
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

	private Player p;

	private int timer;

	/**
	 * Creates the death event for the specified entity.
	 * 
	 * @param entity
	 *            The player whose death has just happened.
	 */
	public DeathEvent(Player p) {
		super(p, 1);
		this.timer = 6;
		this.p = p;
		p.setDead(true);
		Combat.resetCombat(p);
	}

	@Override
	public void execute() {
		if (!shouldRun()) {
			this.stop();
			return;
		}
		if (timer > 0) {
			timer--;
			if (timer == 5) {
				p.playAnimation(Animation.create(0x900));
			} else if (timer == 3) {
				DeathDropHandler.handleDeathDrop(p);
			} else if (timer == 1) {
				if (p != null) {
					/*p.getActionSender().sendWidget(2, 0);
					p.getActionSender().sendWidget(3, 0);*/
					p.getActionQueue().clearRemovableActions();
					p.setTeleportTarget(Entity.DEFAULT_LOCATION);
					p.getSkills().setLevel(Skills.HITPOINTS, p.getSkills().getLevelForExperience(Skills.HITPOINTS));
					p.getActionSender().sendMessage("Oh dear, you are dead!");
					p.setDead(false);
					p.freeze(0);
					PrayerHandler.resetAllPrayers(p);
					p.setSpecialAmount(100);
					p.setUsingSpecial(false);
					p.getDamageMap().resetDealtDamage();
				}
			} else if (timer == 0) {
				p.playAnimation(Animation.create(-1));
			}
		} else {
			this.stop();
		}
	}
}