package com.model.game.character.combat;


import com.model.game.character.Entity;
import com.model.game.character.combat.magic.CombatSpells;
import com.model.game.character.player.Player;

public class CombatState {
	
	/**
	 * The mob whose combat state this is.
	 */
	private Entity mob;
	
	/**
	 * Creates the combat state class for the specified entity.
	 * @param entity The entity.
	 */
	public CombatState(Entity entity) {
		this.mob = entity;
	}
	
	/**
	 * The mob's target
	 */
	private Entity target;
	
	/**
	 * Gets the target of the attack action
	 * @return
	 */
	public Entity getTarget() {
		return target;
	}
	
	/**
	 * Sets the target
	 * @param target
	 */
	public void setTarget(Entity target) {
		this.target = target;
	}

	public boolean noTarget() {
		return target == null;
	}

	public void applySmite(Player defender, int damage) {
		PrayerHandler.handleSmite(mob.asPlayer(), defender, damage);
	}

	/**
	 * 
	 * @param attacker The person who is hitting someone. The target is the one with veng active.
	 */
	public void vengeance(Entity attacker, int damage, int delay) {
		CombatSpells.vengeance(mob.asPlayer(), attacker, damage, delay);
	}
	
	public int calculateMeleeMaxHit() {
		return CombatFormulae.calculateMeleeMaxHit(mob.asPlayer(), mob.asPlayer().getCombatState().target);
	}
	
	public int calculateRangeMaxHit() {
		return CombatFormulae.calculateRangeMaxHit(mob.asPlayer(), mob.asPlayer().getCombatState().target, mob.asPlayer().isUsingSpecial());
	}

	public void reset() {
		// Nullify target
		target = null;
		// Reset all styles
		mob.asPlayer().setCombatType(null);
		mob.asPlayer().setFollowing(null);
	}
	
	/**
	 * The attack delay. (Each value of 1 counts for 600ms, e.g. 3 = 1800ms).
	 */
	private int attackDelay;
	
	/**
	 * Gets the current attack delay.
	 * @return The current attack delay.
	 */
	public int getAttackDelay() {
		return attackDelay;
	}

	/**
	 * Sets the current attack delay.
	 * @param attackDelay The attack delay to set.
	 */
	public void setAttackDelay(int attackDelay) {
		this.attackDelay = attackDelay;
	}

	/**
	 * Decreases the current attack delay.
	 * @param amount The amount to decrease by.
	 */
	public void decreaseAttackDelay(int amount) {
		this.attackDelay -= amount;
	}

	/**
	 * Increases the current attack delay.
	 * @param amount The amount to increase by.
	 */
	public void increaseAttackDelay(int amount) {
		this.attackDelay += amount;
	}
}
