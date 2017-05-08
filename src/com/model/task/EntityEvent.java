package com.model.task;

import com.model.game.character.Entity;

/**
 * An event that has a check to perform the event
 * 
 * @author Arithium
 * 
 */
public abstract class EntityEvent extends ScheduledTask {

	/**
	 * The entity performing the event
	 */
	private Entity entity;

	/**
	 * Constructs a new <code>EntityEvent</code> event
	 * 
	 * @param entity
	 *            The entity performing the event
	 * @param delay
	 *            The delay of the event
	 */
	public EntityEvent(Entity entity, int delay) {
		super(delay);
		this.entity = entity;
	}

	/**
	 * Should the event be ran?
	 * 
	 * @return
	 */
	public boolean shouldRun() {
		if (entity == null) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the entity performing the event
	 * 
	 * @return
	 */
	public Entity getEntity() {
		return entity;
	}
}