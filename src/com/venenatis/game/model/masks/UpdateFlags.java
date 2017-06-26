package com.venenatis.game.model.masks;

import java.util.BitSet;

import com.venenatis.game.model.entity.Hit;

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
		
		/**
		 * Update flag to move the entity.
		 */
		FORCE_MOVEMENT,
	}
	
	/**
	 * Damage mask 1 damage
	 */
	public Hit primary;
	
	/**
	 * Damage mask 2 damage
	 */
	public Hit secondary;
	
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