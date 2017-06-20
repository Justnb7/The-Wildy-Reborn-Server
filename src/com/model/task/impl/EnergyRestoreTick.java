package com.model.task.impl;

import com.model.game.character.player.Player;
import com.model.task.ScheduledTask;

/**
 * A simple task to restore the players energy.
 * @author Patrick van Elderen
 * Credits to the creator, took this from a random Hyperion base.
 *
 */
public class EnergyRestoreTick extends ScheduledTask {

	/**
	 * The player whose energy we are restoring.
	 */
	private Player player;
	
	public EnergyRestoreTick(Player player) {
		super(1);
		this.player = player;
	}

	@Override
	public void execute() {
		if(player.getWalkingQueue().getEnergy() < 100) {
			player.getWalkingQueue().setEnergy(player.getWalkingQueue().getEnergy() + 1);
			if(player.getActionSender() != null) {
				player.getActionSender().sendRunEnergy();
			}
		} else {
			player.getEnergyRestoreTick().stop();
			player.setEnergyRestoreTick(null);
		}
	}

}