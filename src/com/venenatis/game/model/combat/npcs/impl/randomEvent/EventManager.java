package com.venenatis.game.model.combat.npcs.impl.randomEvent;

import java.util.Timer;
import java.util.TimerTask;

import com.venenatis.game.util.Utility;

/**
 * @author Andy || ReverendDread Mar 29, 2017
 */
public class EventManager {

	private RandomEvent event;
	private int delay;
	private int[] eventAmount = new int[3];
	Timer timer = new Timer();
	
	/**
	 * Constructs a new EventManager object.
	 * @param player 
	 */
	public EventManager() {

	}
	
	/**
	 * Processes task on {@link World} thread every game tick.
	 */
	public void process() {
		if (event != null) {
			if (!event.preStartupCheck()) {
				forceStop();
			}
		}
		if (delay > 0) {
			delay--;
			return;
		}
		if (event == null)
			return;
		int delay = event.process();
		if (delay == -1) {
			forceStop();
			return;
		}
		this.delay += delay;
	}
	
	/**
	 * Force stops an event safely.
	 */
	public void forceStop() {
		if (event == null)
			return;
		event.stop();
		event = null;
	}
	
	/**
	 * Gets the delay.
	 * @return The delay.
	 */
	public int getEventDelay() {
		return delay;
	}
	 
	/**
	 * Adds delay the the event processing. 
	 * @param delay The event.
	 */
	public void addEventDelay(int delay) {
		this.delay += delay;
	}

	/**
	 * Sets the delay.
	 * @param delay The delay.
	 */
	public void setEventDelay(int delay) {
		this.delay = delay;
	}
	
	/**
	 * Sets the current event and stops the previous one.
	 * @param event {@link Event} The event.
	 * @return
	 */
	public boolean setEvent(RandomEvent event) {
		forceStop();
		if (!event.start())
			return false;
		this.event = event;
		return true;
	}
	
	/**
	 * Gets the {@link Event} event.
	 * @return the event.
	 */
	public RandomEvent getEvent() {
		return event;
	}
	
	/**
	 * Starts event boss timer.
	 */
	public void appendTimer() {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {	
				switch (Utility.random(2)) {
					case 0:
						setEvent(new RandomBossEvent());
					    eventAmount[1]++;
					    System.out.println("Exectuing Event: " + "BossEvent: #" + eventAmount[0]);
					    break;
					default:
						setEvent(new RandomBossEvent());
					    eventAmount[1]++;
					    System.out.println("Exectuing Event: " + "BossEvent: #" + eventAmount[0]);
				  }
			  }
		}, 30 * 60 * 1000, 30 * 60 * 1000); //Execute every 30 minutes.
	}
	
}