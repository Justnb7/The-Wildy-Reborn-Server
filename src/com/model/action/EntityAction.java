package com.model.action;

import com.model.game.character.Entity;

public abstract class EntityAction extends Action {
	
	protected final Entity entity;
	
	protected EntityAction(Entity entity) {
		this(entity, 1);
	}
	
	public EntityAction(Entity entity, int ticks) {
		super(entity, ticks);
		this.entity = entity;
	}

}