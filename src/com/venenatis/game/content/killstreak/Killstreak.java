package com.venenatis.game.content.killstreak;

import com.venenatis.game.location.Area;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;

/**
 * 
 * The class which represents the killstreaks, there are two types of
 * killstreak. One wilderness streak where if you leave the wilderness it will
 * be reset. And the normal killstreak that stacks untill you die.
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van  Elderen</a>
 *
 */
public class Killstreak {

	/**
	 * The player receiving the killstreaks
	 */
	private Player player;

	/**
	 * Creates a new object that will manage player killstreaks
	 * 
	 * @param player the player
	 */
	public Killstreak(Player player) {
		this.player = player;
	}

	
	/**
	 * Remove the players killstreak when a player dies in PvP
	 */
	public void reset() {
		player.setCurrentKillStreak(0);
	}

	/**
	 * Increases the wilderness kill streak
	 */
	public void increase() {
		if(Area.inWilderness(player)) {
			player.setCurrentKillStreak(player.getCurrentKillStreak() + 1);
			
			if(player.getCurrentKillStreak() > player.getHighestKillStreak()) {
				player.setHighestKillStreak(player.getCurrentKillStreak());
				player.getActionSender().sendMessage("<img=22>[@red@Killstreak@bla@]: <col=4891c9>Congratulations, your highest kill streak has increased!</col>");
			}
		}
	}

	/**
	 * Rewards the player with a random currency, this can vary from Blood money, coins, emblems to points.
	 */
	public void reward() {
		
		int pk_points = 3;
		
		Item[] emblem = { new Item(12750), new Item(12751), new Item(12752), new Item(12753), new Item(12754), new Item(12755), new Item(12756) };
		
		if(player.getCurrentKillStreak() > 2 && player.getCurrentKillStreak() <= 10) {
			pk_points = player.getCurrentKillStreak();
			player.setPkPoints(player.getPkPoints() + pk_points);
			player.getActionSender().sendMessage("<img=21>[Killstreak]: @bla@You gain @red@" + pk_points + " @bla@extra pk points because of your @red@" + player.getCurrentKillStreak() + " @bla@killstreak.");
		} else {
			player.setPkPoints(player.getPkPoints() + pk_points);
			player.getActionSender().sendMessage("<img=21>[Killstreak]: @bla@You gain @red@ 3 @bla@extra pk points because of your @red@" + player.getCurrentKillStreak() + " @bla@killstreak.");
		}
		
		//Once reached killstreak 10 we'll start rewarding emblems
		switch(player.getCurrentKillStreak()) {
		case 10:
			player.getInventory().addOrCreateGroundItem(emblem[0]);
			break;
		case 15:
			player.getInventory().add(emblem[1]);
			break;
		case 20:
			player.getInventory().add(emblem[2]);
			break;
		case 25:
			player.getInventory().add(emblem[3]);
			break;
		case 30:
			player.getInventory().add(emblem[4]);
			break;
		case 40:
			player.getInventory().add(emblem[5]);
			break;
		case 50:
			player.getInventory().add(emblem[6]);
			break;
		}
		
	}

}
