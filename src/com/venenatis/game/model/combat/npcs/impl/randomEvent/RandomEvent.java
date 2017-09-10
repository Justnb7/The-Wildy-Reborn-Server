package com.venenatis.game.model.combat.npcs.impl.randomEvent;

import com.venenatis.game.world.World;

/**
 * @author Andy || ReverendDread Mar 29, 2017
 */
public abstract class RandomEvent {

	//Called when exectuting an event.
	public abstract boolean start();
	
	//Called every tick when event is being executed. Place conditions here.
	public abstract boolean preStartupCheck();
	
	//Called every tick when event is being executed.
	public abstract int process();
	
	//Called when stopping an event.
	public abstract void stop();
	
	//Sets delay for the event processing.
	protected final void setDelay(World world, int delay) {
		World.getWorld().getEventManager().setEventDelay(delay);
	}
	
	public void sendMessage(String message) {
		World.getWorld().sendWorldMessage("<img=30><col=ff0000>[EVENT]: " + message + "</col>", false);
	}

}
