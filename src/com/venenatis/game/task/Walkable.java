package com.venenatis.game.task;

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
	NEVER,

	/**
	 * The task will end if the entity walks
	 */
	ON_MOVE

}
