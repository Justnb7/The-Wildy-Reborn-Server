package com.model.game.character.player.packets.actions;

import com.model.game.character.player.Player;
import com.model.game.character.player.content.BossTracker;
import com.model.game.character.player.packets.PacketType;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.slayer.SlayerTaskManagement.Teleports;

/**
 * Item Click 2 Or Alternative Item Option 1
 * 
 * @author Ryan / Lmctruck30
 * 
 *         Proper Streams
 */

public class SecondItemOption implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int itemId = player.getInStream().readSignedWordA();
		if (!player.getItems().playerHasItem(itemId))
			return;
	    if (player.in_debug_mode())
		  player.write(new SendMessagePacket("Second item option clicked on a: ("+player.getItems().getItemName(itemId)+")"));
		if (player.getBankPin().requiresUnlock()) {
			player.isBanking = false;
			player.getBankPin().open(2);
			return;
		}
		switch (itemId) {
		
		case 4155:
			Teleports.teleport(player);
			break;

		case 2572:
			BossTracker.open(player);
			break;

		
		  default:
			  break;
		 

		}

	}

}
