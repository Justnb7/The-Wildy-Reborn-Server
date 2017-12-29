package com.venenatis.game.model.combat;


import java.util.concurrent.TimeUnit;

import com.venenatis.game.consumables.Consumables.Food;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Stopwatch;

/**
 * The combat state, a class where we store all combat related things.
 * 
 * @author Patrick van Elderen
 *
 */
public class CombatState {
	
	/**
	 * The entity whose combat state this is.
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

	/**
	 * Check if we have no target
	 * @return target
	 */
	public boolean noTarget() {
		return target == null;
	}
	
	/**
	 * The mob which is under attack by another mob
	 */
	private Entity underAttackBy;
	
	/**
	 * @return the underAttackBy
	 */
	public Entity getUnderAttackBy() {
		return underAttackBy;
	}

	/**
	 * @param underAttackBy
	 *            the underAttackBy to set
	 */
	public void setUnderAttackBy(Entity underAttackBy) {
		this.underAttackBy = underAttackBy;
	}

	/**
	 * Handles the smite prayer effect
	 * 
	 * @param attacker
	 *            The player draining the victim
	 * @param victim
	 *            The player defending the attacker
	 * @param damage
	 *            The amount of damage that was dealt
	 */
	public void handleSmite(Entity attacker, Player victim, int damage) {
		victim.getSkills().decreaseCurrentLevel(Skills.PRAYER, (damage / 4), 0);
	}
	
	/**
	 * Handles the recoil damage effect
	 * 
	 * @param attacker
	 *            The player attacking the defendee
	 * @param damage
	 *            The damage we received
	 */
	public void recoil(Entity attacker, int damage) {
		Combat.recoil(entity.asPlayer(), attacker, damage);
	}

	/**
	 * The magic spells start height
	 * 
	 * @return the start height
	 */
	public int getStartHeight() {
		Player player = (Player)entity;
		return player.getMagic().getStartHeight(entity.asPlayer());
	}

	/**
	 * The ending height of the magic spell
	 * 
	 * @return the end height
	 */
	public int getEndHeight() {
		Player player = (Player)entity;
		return player.getMagic().getEndHeight(entity.asPlayer());
	}

	/**
	 * The spells delay
	 * 
	 * @return The delay
	 */
	public int getStartDelay() {
		Player player = (Player)entity;
		return player.getMagic().getStartDelay(entity.asPlayer());
	}

	/**
	 * The spells GFX height
	 * 
	 * @return the GFX height
	 */
	public int getStartGfxHeight() {
		Player player = (Player)entity;
		return player.getMagic().getStartGfxHeight(entity.asPlayer());
	}
	
	/**
	 * The maximum melee hit
	 * 
	 * @return The max hit
	 */
	public int calculateMeleeMaxHit() {
		return CombatFormulae.calculateMeleeMaxHit(entity.asPlayer(), entity.asPlayer().getCombatState().target);
	}
	
	/**
	 * The maximum range hit, counting darts strength etc.
	 * 
	 * @return The max hit
	 */
	public int calculateRangeMaxHit() {
		return calculateRangeMaxHit(false); // dont ignore by default
	}
	
	/**
	 * The maximum range hit
	 * 
	 * @param ignoreArrowRangeStr
	 *            Checks if we are ignoring arrows equipment
	 * @return The max hit
	 */
	public int calculateRangeMaxHit(boolean ignoreArrowRangeStr) {
		return CombatFormulae.calculateRangeMaxHit(entity.asPlayer(), entity.asPlayer().getCombatState().target, 
				entity.asPlayer().isUsingSpecial(), ignoreArrowRangeStr 
				);
	}

	/**
	 * Resets the combat state
	 */
	public void reset() {
		// Nullify target
		target = null;
		// Reset all styles
		entity.setCombatType(null);
		entity.following().setFollowing(null);
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
	
	/**
	 * The teleblock timer
	 */
	private final Stopwatch teleblock = new Stopwatch();
	
	/**
	 * Gets the teleblock timer
	 * @return teleblock
	 */
	public Stopwatch getTeleblock() {
		return teleblock;
	}
	
	/**
	 * The amount int ticks we're saving
	 */
	private int teleblockUnlock;

	/**
	 * Retrieves the saved amount in ticks
	 * @return teleblockUnlock
	 */
	public int getTeleblockUnlock() {
		return teleblockUnlock;
	}

	/**
	 * Saves the amount of unblockable ticks
	 * @param teleblockUnlock
	 */
	public void setTeleblockUnlock(int teleblockUnlock) {
		this.teleblockUnlock = teleblockUnlock;
	}
	
	/**
	 * Teleblock an player for X amount of minutes
	 * 
	 * @param seconds
	 *            The amount of seconds the player is teleblocked for
	 */
	public void teleblock(int seconds) {
		Player player = (Player) entity;
		setTeleblockUnlock(seconds);
		getTeleblock().reset();
		player.getActionSender().sendWidget(4, seconds);
		player.message("You have been teleblocked!");
	}
	
	/**
	 * Checks if the player is teleblocked
	 * @return getTeleblock
	 */
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
	 * @return damageMap
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
	
	/**
	 * The last food
	 */
	private Food lastAte;

	/**
	 * Check the last food we ate
	 * 
	 * @return lastAte
	 */
	public Food getLastAte() {
		return lastAte;
	}
	
	/**
	 * Sets the last food we ate
	 * 
	 * @param lastAte
	 *            The last food EX: Karambwan
	 */
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

	/**
	 * We're in combat
	 */
	private boolean inCombat;
	
	/**
	 * Are we in combat?
	 * 
	 * @return inCombat
	 */
	public boolean inCombat() {
		return inCombat;
	}
	
	/**
	 * Set the player active in combat
	 * 
	 * @param inCombat
	 *            Are we in combat true or false?
	 */
	public void setInCombat(boolean inCombat) {
		this.inCombat = inCombat;
	}

	/**
	 * Checks the last target we fought
	 * @return lastTarget
	 */
	public Entity getLastTarget() {
		return lastTarget;
	}

	/**
	 * Set the last fought target
	 * 
	 * @param lastTarget
	 *            The last target, example rock crab index (100)
	 */
	public void setLastTarget(Entity lastTarget) {
		this.lastTarget = lastTarget;
	}
	
	/**
	 * A field that represents when {@link Entity} was last hit
	 */
	private long lastHit;

	/**
	 * @return the lastHit
	 */
	public long getLastHit() {
		return lastHit;
	}

	/**
	 * @param lastHit
	 *            the lastHit to set
	 */
	public void setLastHit(long lastHit) {
		this.lastHit = lastHit;
	}
}