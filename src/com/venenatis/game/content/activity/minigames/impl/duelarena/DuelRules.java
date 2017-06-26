package com.venenatis.game.content.activity.minigames.impl.duelarena;

import java.util.BitSet;

/**
 * An EnumBitSet-like implementation that contains various functions for settings the flags of
 * rules for dueling.
 *
 * @author Seven
 */
public final class DuelRules {

	/**
	 * A set of flags that denote rules that are currently active.
	 */
	private BitSet flags = new BitSet();

	/**
	 * The value for sending multiple toggles on the dueling interface.
	 */
	private int value;

	/**
	 * Resets the config values back to {@code 0}. This clears the red signs.
	 */
	public void resetConfigValue() {
		this.value = 0;
	}

	/**
	 * Increments the dueling config by another config value.
	 *
	 * @param value The other config.
	 */
	public void incrementValue(int value) {
		this.value += value;
	}

	/**
	 * Decrements the dueling config value by another config.
	 *
	 * @param value The other config.
	 */
	public void decrementValue(int value) {
		this.value -= value;
	}

	/**
	 * The value that tells that client which configs are enabled/disabled.
	 *
	 * @return The value.
	 */
	public int getConfigValue() {
		return value;
	}

	/**
	 * Gets set of flags.
	 *
	 * @@return The set of flags.
	 */
	public BitSet getFlags() {
		return flags;
	}

	/**
	 * Sets the current flags to a specified {@code flags}.
	 */
	public void setFlags(BitSet flags) {
		this.flags = flags;
	}

	/**
	 * Sets all flags in the bitset to {@code false}.
	 */
	public void clear() {
		flags.clear();
	}

	/**
	 * Sets the flag of a {@code rule} to {@code true}.
	 */
	public void flag(DuelRule rule) {
		flags.set(rule.ordinal(), true);
	}

	/**
	 * Alternates the value of a flag.
	 *
	 * @param flag The flag to alternate.
	 */
	public void alternate(DuelRule flag) {
		flags.set(flag.ordinal(), get(flag) ? false : true);
	}

	/**
	 * Gets the config value of this rule.
	 *
	 * @return The config value.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Sets the flag of a {@code rule}.
	 */
	public void set(DuelRule rule, boolean flag) {
		flags.set(rule.ordinal(), flag);
	}

	/**
	 * Gets a {@code rule} from the bitset and checks its value.
	 *
	 * @param rule The flag to retrieve from the bitset.
	 * @return The value of the {@code rule} which is either {@code true} or
	 * {@code false}.
	 */
	public boolean get(DuelRule rule) {
		return flags.get(rule.ordinal());
	}

}