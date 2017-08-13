package com.venenatis.game.model.combat.impl;

import com.venenatis.game.model.entity.player.Player;

public class PlayerKilling {

	public static void addHostToList(Player player, String host) {
		if (player == null) {
			return;
		}

		if (player.getLastKilledPlayers().contains(host)) {
			return;
		}

		if (host.equals(null)) {
			return;
		}

		if (player.getLastKilledPlayers() == null) {
			player.getLastKilledPlayers().clear();
		}

		if (player.getLastKilledPlayers().size() >= 3) {
			player.getLastKilledPlayers().remove();
		}

		player.getLastKilledPlayers().add(host);
	}

	public static boolean hostOnList(Player player, String host) {
		if (player == null) {
			return false;
		}

		if (host == null) {
			return false;
		}

		if (host.length() == 0) {
			return false;
		}

		if (player.getLastKilledPlayers() == null) {
			return false;
		}

		return player.getLastKilledPlayers().contains(host);
	}

	public static boolean removeHostFromList(Player player, String host) {
		return player.getLastKilledPlayers().remove(host);
	}

}