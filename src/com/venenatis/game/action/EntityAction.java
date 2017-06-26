package com.venenatis.game.action;

import com.venenatis.game.model.entity.Entity;

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