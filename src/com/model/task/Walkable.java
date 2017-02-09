package com.model.task;

/**
 * Determines if the task is walkable or not
 * 
 * @author Mobster
 *
 */
public enum Walkable {

	/**
	 * The task will continue if the entity walks
	 */
	WALKABLE,

	/**
	 * The task will end if the entity walks
	 */
	NON_WALKABLE

}
