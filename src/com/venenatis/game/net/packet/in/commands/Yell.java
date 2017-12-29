package com.venenatis.game.net.packet.in.commands;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

public class Yell {
	
	public static String[] BAD_STRINGS = { ".com", "@cr", "<img=", "</col", "<col=", };
	
	public static String getPrefix(Player player) {
		final String name = Utility.formatName(player.getUsername());

		final Rights rights = player.getRights();

		String color = "";

		if (player.getYellColor() == "" || player.getYellColor() == null) {
			color = rights.getColor();
		} else {
			color = player.getYellColor();
		}

		return "[<col=" + color + ">" + rights.getName() + "</col>]<col=" + color + "> " + player.getRights().getStringForRights(player) + " " + name + ":"/*"</col>:"*/;
	}
	
	public static void yell(Player player, String message) {

		if(player.isYellMuted()) {
			player.getActionSender().sendMessage("You cannot yell because you're yell muted.");
			return;
		}
		
		if (player.getRights().equal(Rights.PLAYER) && player.getKillCount() < 100 && !player.getUsername().equalsIgnoreCase("killa") && !player.getUsername().equalsIgnoreCase("julio") && !player.getUsername().equalsIgnoreCase("impossible")) {
			player.getActionSender().sendMessage("You need atleast a killcount of 100 to yell.");
			return;
		}

		boolean can = true;
		for (final String bad : BAD_STRINGS) {
			if (message.contains(bad)) {
				can = false;
			}
		}

		if (!can) {
			player.getActionSender().sendMessage("Your message contains characters that are not allowed!");
			return;
		}

		String formatted_message = Utility.capitalizeSentence(message);

		World.getWorld().yell(getPrefix(player) + " " + formatted_message);
	}

}