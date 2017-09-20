package com.venenatis.game.task.impl;

import com.venenatis.game.task.Task;


public abstract  class StoppingTick extends Task {

	/**
	 * Creates a tickable with the specified amount of ticks.
	 *
	 * @param ticks The amount of ticks.
	 */
	public StoppingTick(int ticks) {
		super(ticks);
	}

	@Override
	public void execute() {
		stop();
		executeAndStop();
	}

	public abstract void executeAndStop();
}