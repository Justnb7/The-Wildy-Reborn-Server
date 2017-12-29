package com.venenatis.game.content;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

/**
 * 
 * @author Jack
 *
 */

public class KillDeathRatioHighscores {

	/**
	 * Represents interface id
	 */
	private static final int INTERFACE_ID = 6308;

	/**
	 * Represents title string id
	 */
	private static final int TTILE_STRING_ID = 6399;

	/**
	 * Represents start line string id
	 */
	private static int startLineStringId = 6402;

	/**
	 * Decimal formatter
	 */
	private static final NumberFormat FORMATTER = new DecimalFormat("#0.00");

	/**
	 * Opens highscore
	 * 
	 * @param player
	 */
	public static void openInterface(Player player) {
		reset(player);
		player.getActionSender().sendString("        Top 10 Kill Death Ratio'", TTILE_STRING_ID);
		for (int index = 0; index < getTopKillDeathRatios().size(); index++) {
			if (getTopKillDeathRatios().get(index) == null)
				continue;
			final Player players = getTopKillDeathRatios().get(index);
			player.getActionSender().sendString(Utility.formatName(players.getUsername()) + " (" + players.getSkills().getCombatLevel() + ") - Kill Death Ratio:.. " + FORMATTER.format(players.getKillDeathRatio()), startLineStringId + index);
		}
		player.getActionSender().sendInterface(INTERFACE_ID);
	}

	/**
	 * Resets interface strings & highscore list
	 * 
	 * @param player
	 */
	private static void reset(Player player) {
		for (int index = 6402; index < 6412; index++) {
			player.getActionSender().sendString("", index);
		}
		for (int index = 8578; index < 8618; index++) {
			player.getActionSender().sendString("", index);
		}
		getTopKillDeathRatios().clear();
	}

	/**
	 * Fetches top death ratio'
	 * 
	 * @return
	 */
	private static List<Player> getTopKillDeathRatios() {
		List<Player> list = new ArrayList<>();

		for (Player player : World.getWorld().getPlayers()) {
			if (player == null) {
				continue;
			}
			list.add(player);
		}

		list.sort((playerA, playerB) -> Double.compare(playerB.getKillDeathRatio(), playerA.getKillDeathRatio()));
		return list;
	}
}