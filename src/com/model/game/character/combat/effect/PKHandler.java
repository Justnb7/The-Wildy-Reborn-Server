package com.model.game.character.combat.effect;

import com.model.game.character.player.Player;


public class PKHandler {
	public static final int MAXIMUM_ENTRIES = 6;

	public static boolean requiresClearing(Player client) {
		return client.lastKilledList.size() >= MAXIMUM_ENTRIES;
	}

	public static void clearList(Player client) {
		client.lastKilledList.clear();
		client.lastKilledList.trimToSize();
	}

	public static void addKilledEntry(String name, Player client) {
		client.lastKilledList.add(name);
	}

	public static int getAmountInList(Player client) {
		return client.lastKilledList != null && client.lastKilledList.size() > -1 ? client.lastKilledList.size() : 0;
	}

	public static boolean hasKilledRecently(String name, Player client) {
		if (client.lastKilledList.contains(name))
			return true;
		if (requiresClearing(client))
			clearList(client);
		return false;
	}

	public static boolean isSameConnection(Player other, Player client) {
		return client.connectedFrom.equals(other.connectedFrom) || client.getMacAddress().equals(other.getMacAddress()) || client.getIdentity().equals(other.getIdentity());
	}
	
	public static boolean hasKilledRecently(Player player, Player other) {
		if(player.connectedFrom.equals(other.connectedFrom) || player.getMacAddress().equals(other.getMacAddress()) || player.getIdentity().equals(other.getIdentity())) {
			return true;
		}
		return false;
	}

}
