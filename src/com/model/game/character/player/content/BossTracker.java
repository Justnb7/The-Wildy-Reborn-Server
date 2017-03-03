package com.model.game.character.player.content;

import java.util.Map.Entry;

import com.model.game.character.npc.BossDeathTracker.BossName;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.SendInterface;
import com.model.game.character.player.packets.out.SendScrollbar;
import com.model.game.character.player.packets.out.SendString;
import com.model.utility.Utility;

public class BossTracker {
	
	public static void open(Player player) {

		int line = 45011;

		for (Entry<BossName, Integer> entry : player.getBossDeathTracker().getTracker().entrySet()) {
			
			if (entry == null) {
				continue;
			}
			
			player.write(new SendString(entry.getKey().format(), line));
			line++;
			player.write(new SendString(Utility.formatDigits(entry.getValue()), line));
			line++;
		}
		
		player.write(new SendScrollbar(45010, 200));

		player.write(new SendInterface(45000));
	}

	public static int getTotalKills(Player player) {
		int total = 0;
		for (Entry<BossName, Integer> entry : player.getBossDeathTracker().getTracker().entrySet()) {
			if (entry == null)
				continue;
			total += entry.getValue();
		}
		return total;
	}

}
