package com.model;

import java.util.BitSet;

/**
 * Holds update flags.
 * @author Graham Edgecombe
 *
 */
public class UpdateFlags {
	
	/**
	 * The bitset (flag data).
	 */
	private BitSet flags = new BitSet();
	
	/**
	 * Represents a single type of update flag.
	 * @author Graham Edgecombe
	 *
	 */
	public enum UpdateFlag {
		
		/**
		 * Appearance update.
		 */
		APPEARANCE,
		
		/**
		 * Chat update.
		 */
		CHAT,
		
		/**
		 * Graphics update.
		 */
		GRAPHICS,
		
		/**
		 * Animation update.
		 */
		ANIMATION,
		
		/**
		 * Forced chat update.
		 */
		FORCED_CHAT,
		
		/**
		 * Interacting entity update.
		 */
		FACE_ENTITY,
		
		/**
		 * Face coordinate entity update.
		 */
		FACE_COORDINATE,
		
		/**
		 * Hit update.
		 */
		HIT,
		
		/**
		 * Hit 2 update/
		 */
		HIT_2,
		
		/**
		 * Update flag used to transform npc to another.
		 */
		TRANSFORM,
	}
	
	/**
	 * Damage mask 1 damage
	 */
	private int damage_1;
	
	/**
	 * Damage mask 2 damage
	 */
	private int damage_2;
	
	/**
	 * The hitmask for damage 1
	 */
	private int hit_type_1;
	
	/**
	 * The hitmask for damage 2
	 */
	private int hit_type_2;
	
	/**
	 * The forced message the entity will send
	 */
	private String forcedMessage;
	
	/**
	 * Checks if an update required.
	 * @return <code>true</code> if 1 or more flags are set,
	 * <code>false</code> if not.
	 */
	public boolean isUpdateRequired() {
		return !flags.isEmpty();
	}
	
	/**
	 * Flags (sets to true) a flag.
	 * @param flag The flag to flag.
	 */
	public void flag(UpdateFlag flag) {
		flags.set(flag.ordinal(), true);
	}
	
	/**
	 * Sets a flag.
	 * @param flag The flag.
	 * @param value The value.
	 */
	public void set(UpdateFlag flag, boolean value) {
		flags.set(flag.ordinal(), value);
	}
	
	/**
	 * Gets the value of a flag.
	 * @param flag The flag to get the value of.
	 * @return The flag value.
	 */
	public boolean get(UpdateFlag flag) {
		return flags.get(flag.ordinal());
	}

	/**
	 * Resest all update flags.
	 */
	public void reset() {
		flags.clear();
	}

	/**
	 * Gets the first hit type damage
	 * 
	 * @return
	 */
	public int getDamage1() {
		return damage_1;
	}

	/**
	 * Sets the first hit type damage
	 * 
	 * @param damage
	 */
	public void setDamage1(int damage) {
		this.damage_1 = damage;
	}

	/**
	 * Gets the second hit type damage
	 * 
	 * @return
	 */
	public int getDamage2() {
		return damage_2;
	}

	/**
	 * Sets the second hit type damage
	 * 
	 * @param damage
	 */
	public void setDamage2(int damage) {
		this.damage_2 = damage;
	}

	/**
	 * Gets the first hit type
	 * 
	 * @return
	 */
	public int getHitType1() {
		return hit_type_1;
	}

	/**
	 * Sets the first hit type
	 * 
	 * @param type
	 */
	public void setHitType1(int type) {
		this.hit_type_1 = type;
	}

	/**
	 * Gets the second hit type
	 * 
	 * @return
	 */
	public int getHitType2() {
		return hit_type_2;
	}

	/**
	 * Sets the second hit type
	 * 
	 * @param type
	 *            The type of hit
	 */
	public void setHitType2(int type) {
		this.hit_type_2 = type;
	}

	/**
	 * Gets the forced message
	 * 
	 * @return
	 */
	public String getForcedMessage() {
		return forcedMessage;
	}

	/**
	 * Sets the forced message
	 * 
	 * @param message
	 */
	public void setForcedMessage(String message) {
		this.forcedMessage = message;
		flag(UpdateFlag.FORCED_CHAT);
	}
}