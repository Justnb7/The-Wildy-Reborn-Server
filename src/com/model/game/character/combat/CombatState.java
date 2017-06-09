package com.model.game.character.combat;


import com.model.game.character.Entity;
import com.model.game.character.combat.magic.CombatSpells;
import com.model.game.character.combat.magic.MagicData;
import com.model.game.character.combat.range.RangeData;
import com.model.game.character.player.Player;
import com.model.game.item.container.impl.equipment.EquipmentConstants;

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

	public int getRangeStartGFX() {
		return RangeData.getRangeStartGFX(mob.asPlayer());
	}

	public int getRangeProjectileGFX() {
		return RangeData.getRangeProjectileGFX(mob.asPlayer());
	}

	public int getProjectileShowDelay() {
		return RangeData.getProjectileShowDelay(mob.asPlayer());
	}

	public int getProjectileSpeed() {
		return RangeData.getProjectileSpeed(mob.asPlayer());
	}
	
	public boolean properJavalins() {
		return usingJavalins(mob.asPlayer().getEquipment().get(EquipmentConstants.AMMO_SLOT).getId());
	}

	public boolean usingDbow() {
		return mob.asPlayer().getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId() == 11235;
	}

	public boolean properBolts() {
		return usingBolts(mob.asPlayer().getEquipment().get(EquipmentConstants.AMMO_SLOT).getId());
	}
	
	public boolean usingJavalins(int javalin) {
		return (javalin >= 825 && javalin <= 830) || javalin == 19484;
	}

	public boolean usingBolts(int i) {
		return (i >= 9140 && i <= 9145) || i >= 9334 && i <= 9344 || (i >= 9236 && i <= 9245) || i == 11875;
	}

	public int getFreezeTime() {
		return MagicData.getFreezeTime(mob.asPlayer());
	}

	public int getStartHeight() {
		return MagicData.getStartHeight(mob.asPlayer());
	}

	public int getEndHeight() {
		return MagicData.getEndHeight(mob.asPlayer());
	}

	public int getStartDelay() {
		return MagicData.getStartDelay(mob.asPlayer());
	}

	public int getStaffNeeded() {
		return MagicData.getStaffNeeded(mob.asPlayer());
	}

	public boolean godSpells() {
		return MagicData.godSpells(mob.asPlayer());
	}

	public int getEndGfxHeight() {
		return MagicData.getEndGfxHeight(mob.asPlayer());
	}

	public int getStartGfxHeight() {
		return MagicData.getStartGfxHeight(mob.asPlayer());
	}

	public void fireProjectileAtTarget() {
		RangeData.fireProjectileAtTarget(mob.asPlayer());
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
		mob.asPlayer().usingMagic = mob.asPlayer().usingBow = false;
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
