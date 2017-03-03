package com.model.task.impl;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.SendString;
import com.model.task.ScheduledTask;

public class RestoreSpecialStats extends ScheduledTask {

	public RestoreSpecialStats() {
		super(8);
	}

	private int counter = 0;

	@Override
	public void execute() {
		counter++;
		//System.out.println(""+counter);
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null) {
					if (counter >= 4) {
						//System.out.println("Here");
						if (player.getSpecialAmount() < 100) {
							//System.out.println("special amount below 100");
							player.setSpecialAmount(player.getSpecialAmount() + 10);
							//System.out.println("Special amount give + 10%");
							if (player.getSpecialAmount() > 100) {
								player.setSpecialAmount(100);
								//System.out.println("Special attack already full.");
							}
							player.getWeaponInterface().sendSpecialBar(player.playerEquipment[player.getEquipment().getWeaponId()]);
							player.getWeaponInterface().refreshSpecialAttack();
							player.write(new SendString(""+player.getSpecialAmount(), 12001));
							//System.out.println("Special bar updated.");
					}
				}
			}
		}
				
		if (counter >= 4)
			counter = 0;
	}
}