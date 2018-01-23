package com.venenatis.game.content.Perks;


import com.venenatis.game.content.skills.slayer.interfaceController.UnlockInterface;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.task.Task;
import com.venenatis.server.Server;

public class PerkInterface {

	/**
	 * Opens the interface (not used yet)
	 * 
	 * @param player
	 */
	public void open(Player player) {
		Server.getTaskScheduler().schedule(new Task(1) {
			@Override
			public void execute() {
				BuyPerkHandler perk = new BuyPerkHandler();
				perk.openBuyInterface(player);
				this.stop();
			}
		});
	}
	
	public void openAssign(Player player) {
		Server.getTaskScheduler().schedule(new Task(1) {
			@Override
			public void execute() {
				BuyPerkHandler perk = new BuyPerkHandler();
				perk.openAssignInterface(player);
				this.stop();
			}
		});
	}
	
	public void perkActions(Player player) {
		
	}
	
	
}
