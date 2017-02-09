package com.model.task.impl;

import com.model.task.ScheduledTask;

/**
 * A {@link ScheduledTask} which runs when a certain requirement is met such as
 * reaching a destination
 * 
 * @author Patrick van Elderen
 *
 */
public abstract class DistancedActionTask extends ScheduledTask {

	@Override
	public void execute() {
		if (reached()) {
			onReach();
		}
	}

	/**
	 * The actions taken when reached
	 */
	public abstract void onReach();

	/**
	 * The check to determine if the location has been reached
	 * 
	 * @return The location has been reached
	 */
	public abstract boolean reached();

}
