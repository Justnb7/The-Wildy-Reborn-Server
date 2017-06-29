package com.venenatis.game.model.combat;


import java.util.concurrent.TimeUnit;

import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.model.combat.magic.MagicData;
import com.venenatis.game.model.combat.magic.lunar.CombatSpells;
import com.venenatis.game.model.combat.range.RangeData;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Stopwatch;

public class CombatState {
	
	/**
	 * The mob whose combat state this is.
	 */
	private Entity entity;
	
	/**
	 * Creates the combat state class for the specified entity.
	 * @param entity The entity.
	 */
	public CombatState(Entity entity) {
		this.entity = entity;
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
		PrayerHandler.handleSmite(entity.asPlayer(), defender, damage);
	}

	/**
	 * 
	 * @param attacker The person who is hitting someone. The target is the one with veng active.
	 */
	public void vengeance(Entity attacker, int damage, int delay) {
		CombatSpells.vengeance(entity.asPlayer(), attacker, damage, delay);
	}

	public int getRangeStartGFX() {
		return RangeData.getRangeStartGFX(entity.asPlayer());
	}

	public int getRangeProjectileGFX() {
		return RangeData.getRangeProjectileGFX(entity.asPlayer());
	}

	public int getProjectileShowDelay() {
		return RangeData.getProjectileShowDelay(entity.asPlayer());
	}

	public int getProjectileSpeed() {
		return RangeData.getProjectileSpeed(entity.asPlayer());
	}
	
	public boolean properJavalins() {
		return usingJavalins(entity.asPlayer().getEquipment().get(EquipmentConstants.AMMO_SLOT).getId());
	}

	public boolean usingDbow() {
		return entity.asPlayer().getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId() == 11235;
	}

	public boolean properBolts() {
		return usingBolts(entity.asPlayer().getEquipment().get(EquipmentConstants.AMMO_SLOT).getId());
	}
	
	public boolean usingJavalins(int javalin) {
		return (javalin >= 825 && javalin <= 830) || javalin == 19484;
	}

	public boolean usingBolts(int i) {
		return (i >= 9140 && i <= 9145) || i >= 9334 && i <= 9344 || (i >= 9236 && i <= 9245) || i == 11875;
	}

	public int getStartHeight() {
		return MagicData.getStartHeight(entity.asPlayer());
	}

	public int getEndHeight() {
		return MagicData.getEndHeight(entity.asPlayer());
	}

	public int getStartDelay() {
		return MagicData.getStartDelay(entity.asPlayer());
	}

	public int getStaffNeeded() {
		return MagicData.getStaffNeeded(entity.asPlayer());
	}

	public boolean godSpells() {
		return MagicData.godSpells(entity.asPlayer());
	}

	public int getStartGfxHeight() {
		return MagicData.getStartGfxHeight(entity.asPlayer());
	}

	public void fireProjectileAtTarget() {
		RangeData.fireProjectileAtTarget(entity.asPlayer());
	}
	
	public int calculateMeleeMaxHit() {
		return CombatFormulae.calculateMeleeMaxHit(entity.asPlayer(), entity.asPlayer().getCombatState().target);
	}
	
	public int calculateRangeMaxHit() {
		return CombatFormulae.calculateRangeMaxHit(entity.asPlayer(), entity.asPlayer().getCombatState().target, entity.asPlayer().isUsingSpecial());
	}

	public void reset() {
		// Nullify target
		target = null;
		// Reset all styles
		entity.asPlayer().setCombatType(null);
		entity.asPlayer().setFollowing(null);
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
	
	private final Stopwatch teleblock = new Stopwatch();
	
	public Stopwatch getTeleblock() {
		return teleblock;
	}
	
	private int teleblockUnlock;

	public int getTeleblockUnlock() {
		return teleblockUnlock;
	}

	public void setTeleblockUnlock(int teleblockUnlock) {
		this.teleblockUnlock = teleblockUnlock;
	}
	
	public void teleblock(int seconds) {
		Player player = (Player) entity;
		setTeleblockUnlock(seconds);
		getTeleblock().reset();
		player.getActionSender().sendWidget(4, seconds);
		player.getActionSender().sendMessage("You have been teleblocked!");
	}
	
	public boolean isTeleblocked() {
		return getTeleblock().elapsed(TimeUnit.SECONDS) < getTeleblockUnlock();
	}
}
