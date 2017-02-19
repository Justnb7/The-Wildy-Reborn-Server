package com.model.game.item;

import com.model.utility.Utility;

/**
 * @author lare96
 */
public class IntervalItem extends Item {

	private int min;
	private int max;

	public IntervalItem(int id) {
		this(id, 1, 1);
	}

	public IntervalItem(int id, int min, int max) {
		super(id, min == max ? min : Utility.inclusiveRandom(min, max));
		this.min = min;
		this.max = max;
	}

	@Override
	public IntervalItem clone() {
		return new IntervalItem(id, min, max);
	}
}