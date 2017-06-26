package com.venenatis.game.model.entity.npc.drop_system;

import java.util.ArrayList;

import com.venenatis.game.util.Utility;

@SuppressWarnings("serial")
public class Table extends ArrayList<Drop> {

	/**
	 * The policy of the table
	 */
	private final TablePolicy policy;

	/**
	 * The chance of access for the table
	 */
	private final int accessibility;

	/**
	 * Creates a new table
	 * 
	 * @param policy the policy of the table
	 * @param accessibility the probability that the table will be access
	 */
	public Table(TablePolicy policy, int accessibility) {
		this.policy = policy;
		this.accessibility = accessibility;
	}

	/**
	 * Selects and fetches a random Drop from the backing list
	 * 
	 * @return a random drop
	 */
	public Drop fetchRandom() {
		return get(Utility.random(size() - 1));
	}

	/**
	 * The policy of the table
	 * 
	 * @return the policy
	 */
	public TablePolicy getPolicy() {
		return policy;
	}

	/**
	 * The accessibility of the table
	 * 
	 * @return the accessibility
	 */
	public int getAccessibility() {
		return accessibility;
	}
}