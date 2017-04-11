package com.model.game.character.player.content;

import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.SendInterfacePacket;
import com.model.utility.Utility;

public class KillTracker {

	public static class KillEntry {

		private String name;
		private int amount;

		public KillEntry(String name, int amount) {
			this.name = name;
			this.amount = amount;
		}

		public String getName() {
			return name;
		}

		public int getAmount() {
			return amount;
		}
	}
	
	public static boolean entryExist(Player player, KillEntry entry) {
		return player.getKillTracker().contains(entry);
	}

	public static void submit(Player player, KillEntry entry, boolean message) {
		int index = getIndex(player, entry);
		if (index >= 0) {
			player.getKillTracker().get(index).amount += entry.amount;
			entry = player.getKillTracker().get(index);
		} else {
			player.getKillTracker().add(entry);
		}
		if (message) {
			player.getActionSender().sendMessage("Your " + entry.getName() + " kill count is: <col=ff0000>" + Utility.formatDigits(entry.getAmount()) + "</col>.");			
		}
	}

	public static void submit(Player player, KillEntry[] entry) {
		for (KillEntry kill : entry) {
			if (kill != null) {
				submit(player, kill, false);
			}
		}
	}

	public static int getIndex(Player player, KillEntry entry) {
		for (int index = 0; index < player.getKillTracker().size(); index++) {
			if (player.getKillTracker().get(index).name.equals(entry.name)) {
				return index;
			}
		}
		return -1;
	}

	public static void open(Player player) {

		int line = 45011;

		for (KillEntry entry : player.getKillTracker()) {
			
			if (entry == null) {
				continue;
			}
			
			player.getActionSender().sendString(entry.getName(), line);
			line++;
			player.getActionSender().sendString(Utility.formatDigits(entry.getAmount()), line);
			line++;
		}
		
		player.getActionSender().sendScrollBar(45010, 200);

		player.write(new SendInterfacePacket(45000));
	}
	
	public static final void loadDefault(Player player) {
		for (int id : NPC.BOSSES) {
			KillEntry entry = new KillEntry(new NPC(id).getName(), 0);
			if (!entryExist(player, entry)) {
				submit(player, entry, false);
			}
		}
	}

}