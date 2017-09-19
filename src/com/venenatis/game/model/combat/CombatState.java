package com.venenatis.game.model.combat;


import java.util.concurrent.TimeUnit;

import com.venenatis.game.consumables.Consumables.Food;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.masks.Graphic;
import com.venenatis.game.util.Stopwatch;
import com.venenatis.game.util.Utility;

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
	 * The entity's target
	 */
	private Entity target, lastTarget;
	
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

	/**
	 * Handles the smite prayer effect
	 * @param attacker
	 * @param victim
	 * @param damage
	 */
	public void handleSmite(Entity attacker, Player victim, int damage) {
		victim.getSkills().decreaseCurrentLevel(Skills.PRAYER, (damage / 4), 0);
	}

	/**
	 * Handles the retribution prayer effect
	 * @param killed
	 * @param killer
	 */
	public void handleRetribution(Player killed, Player killer) {
		killed.playGraphic(new Graphic(437));
		if (killer.getLocation().isWithinDistance(killer.getLocation(), 3)) {
			killer.take_hit(killed, Utility.random(15));
		}
	}
	
	public void recoil(Entity attacker, int damage) {
		Combat.recoil(entity.asPlayer(), attacker, damage);
	}

	public int getStartHeight() {
		Player player = (Player)entity;
		return player.getMagic().getStartHeight(entity.asPlayer());
	}

	public int getEndHeight() {
		Player player = (Player)entity;
		return player.getMagic().getEndHeight(entity.asPlayer());
	}

	public int getStartDelay() {
		Player player = (Player)entity;
		return player.getMagic().getStartDelay(entity.asPlayer());
	}

	public int getStartGfxHeight() {
		Player player = (Player)entity;
		return player.getMagic().getStartGfxHeight(entity.asPlayer());
	}
	
	public int calculateMeleeMaxHit() {
		return CombatFormulae.calculateMeleeMaxHit(entity.asPlayer(), entity.asPlayer().getCombatState().target);
	}
	
	public int calculateRangeMaxHit() {
		return calculateRangeMaxHit(false); // dont ignore by default
	}
	
	public int calculateRangeMaxHit(boolean ignoreArrowRangeStr) {
		return CombatFormulae.calculateRangeMaxHit(entity.asPlayer(), entity.asPlayer().getCombatState().target, 
				entity.asPlayer().isUsingSpecial(), ignoreArrowRangeStr 
				);
	}

	public void reset() {
		// Nullify target
		target = null;
		// Reset all styles
		entity.asPlayer().setCombatType(null);
		entity.asPlayer().setFollowing(null);
		entity.faceEntity(null);
	}
	
	/**
	 * The spell delay. (Each value of 1 counts for 600ms, e.g. 3 = 1800ms).
	 */
	private int spellDelay;

	/**
	 * @return the spellDelay
	 */
	public int getSpellDelay() {
		return spellDelay;
	}

	/**
	 * @param spellDelay the spellDelay to set
	 */
	public void setSpellDelay(int spellDelay) {
		this.spellDelay = spellDelay;
	}

	/**
	 * @param spellDelay the spellDelay to set
	 */
	public void decreaseSpellDelay(int amount) {
		this.spellDelay -= amount;
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
		player.message("You have been teleblocked!");
	}
	
	public boolean isTeleblocked() {
		return getTeleblock().elapsed(TimeUnit.SECONDS) < getTeleblockUnlock();
	}
	
	/**
	 * The players damage map
	 */
	private DamageMap damageMap = new DamageMap();
	
	/**
	 * Gets the players damage map
	 * 
	 * @return
	 */
	public DamageMap getDamageMap() {
		return damageMap;
	}
	
	/**
	 * The entity's state of life.
	 */
	private boolean isDead;

	/**
	 * Gets the entity's state of life.
	 * @return The entity's state of life.
	 */
	public boolean isDead() {
		return isDead;
	}

	/**
	 * Sets the entity's state of life.
	 * @param isDead The state of life to set.
	 */
	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}
	
	private int eatDelay;
	
	public void setEatDelay(int eatDelay) {
		this.eatDelay = eatDelay;
	}

	public int getEatDelay() {
		return eatDelay;
	}

	public void decreaseEatDelay(int i) {
		eatDelay -= i;
		if (eatDelay <= 0) {
			setCanEat(true);
		}
	}
	
	/**
	 * The eating flag.
	 */
	private boolean canEat = true;
	
	
	/**
	 * Sets the mob's eating flag.
	 * @param canEat The eating flag to set.
	 */
	public void setCanEat(boolean canEat) {
		this.canEat = canEat;
	}
	
	/**
	 * Gets the mob's eating flag.
	 * @return The mob's eating flag.
	 */
	public boolean canEat() {
		return canEat;
	}
	
	/**
	 * The drinking flag.
	 */
	private boolean canDrink = true;
	
	/**
	 * Sets the mob's drinking flag.
	 * @param canEat The drinking flag to set.
	 */
	public void setCanDrink(boolean canDrink) {
		this.canDrink = canDrink;
	}
	
	/**
	 * Gets the mob's drinking flag.
	 * @return The mob's drinking flag.
	 */
	public boolean canDrink() {
		return canDrink;
	}
	
	private Food lastAte;

	public Food getLastAte() {
		return lastAte;
	}
	
	public void setLastAte(Food lastAte) {
		this.lastAte = lastAte;
	}
	
	/**
	 * Ring of Recoil use amount.
	 */
	private int ringOfRecoil = 40;

	/**
	 * @return the ringOfRecoil
	 */
	public int getRingOfRecoil() {
		return ringOfRecoil;
	}

	/**
	 * @param ringOfRecoil the ringOfRecoil to set
	 */
	public void setRingOfRecoil(int ringOfRecoil) {
		this.ringOfRecoil = ringOfRecoil;
	}

	private boolean inCombat;
	
	public boolean inCombat() {
		return inCombat;
	}
	
	public void setInCombat(boolean inCombat) {
		this.inCombat = inCombat;
	}

	public Entity getLastTarget() {
		return lastTarget;
	}

	public void setLastTarget(Entity lastTarget) {
		this.lastTarget = lastTarget;
	}
}
