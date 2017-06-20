package com.model.task.impl;

import com.model.game.character.Entity;
import com.model.task.ScheduledTask;

public class EnergyRestoreTick extends ScheduledTask {

	/**
	 * The mob whos energy we are restoring.
	 */
	private Entity entity;
	
	public EnergyRestoreTick(Entity entity) {
		super(4);
		this.entity = entity;
	}

	@Override
	public void execute() {
		/*if(entity.getWalkingQueue().getEnergy() < 100) {
			entity.getWalkingQueue().setEnergy(entity.getWalkingQueue().getEnergy() + 1);
			if(entity.getActionSender() != null) {
				entity.getActionSender().sendRunEnergy();
			}
		} else {
			entity.getEnergyRestoreTick().stop();
			entity.setEnergyRestoreTick(null);
		}*/
	}

}