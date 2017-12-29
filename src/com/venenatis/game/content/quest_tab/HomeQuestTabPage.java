package com.venenatis.game.content.quest_tab;

import java.util.concurrent.TimeUnit;

import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.World;


/**
 * The home quest tab page with all of the player information
 * 
 * @author Patrick van Elderen
 *
 */
public class HomeQuestTabPage extends QuestTabPage {

	@Override
	public void write(Player player) {
		long milliseconds = (long) player.getTimePlayed() * 600;
		long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
		long hours = TimeUnit.MILLISECONDS.toHours(milliseconds - TimeUnit.DAYS.toMillis(days));
		String time = days + " days, " + hours + " hours.";
		player.getActionSender().sendString("Online: @gre@" + World.getWorld().getPlayerCount() + "", 29155);
		player.getActionSender().sendString("Information", 663);
		
		write(player, "<col=FFFFFF>Rank: <col=00CC00>"+player.getRights().getName(), 1);
		write(player, "<col=FFFFFF>Played: <col=00CC00>"+time, 2);
		write(player, "<col=FFFFFF>Did you know: "+(player.is_did_you_know_activated() ? "<col=00CC00>Enabled" : "<col=ff0000>Disabled"), 3);
		write(player, "<col=FFFFFF>Trivia: <col=00CC00>"+(player.is_trivia_activated() ? "<col=00CC00>Enabled" : "<col=ff0000>Disabled"), 4);
		
		write(player, "<img=22><col=FFFFFF>Kills: <col=00CC00>"+ player.getKillCount(), 6);
		write(player, "<img=22><col=FFFFFF>Deaths: <col=00CC00>"+ player.getDeathCount(), 7);
		write(player, "<img=22><col=FFFFFF>Current killstreak: <col=00CC00>"+ player.getCurrentKillStreak(), 8);
		write(player, "<img=22><col=FFFFFF>Highest killstreak: <col=00CC00>"+ player.getHighestKillStreak(), 9);
		write(player, "<img=22><col=FFFFFF>Bounties: <col=00CC00>"+ player.getBountyPoints(), 10);
		if(player.getSlayerTaskAmount() <= 0) {
			write(player, "<img=17><col=FFFFFF>Task: <col=00CC00>None", 11);
		} else {
			write(player, "<img=17><col=FFFFFF>Task: <col=00CC00>"+player.getSlayerTaskAmount()+ " "+player.getSlayerTask(), 11);
		}
		write(player, "<img=17><col=FFFFFF>tasks completed: <col=00CC00>"+ player.getSlayerTasksCompleted(), 12);
		write(player, "<img=17><col=FFFFFF>Slayer Reward Points: <col=00CC00>"+ player.getSlayerPoints(), 13);
		write(player, "<img=23><col=FFFFFF>Total Votes: <col=00CC00>"+ player.getTotalVotes(), 14);
		write(player, "<img=23><col=FFFFFF>Vote points: <col=00CC00>"+ player.getVotePoints(), 15);
		write(player, "<img=26><col=FFFFFF>Amount Donated: <col=00CC00>"+ player.getTotalAmountDonated()+ "$", 16);
	}
	
	@Override
	public void onButtonClick(Player player, int button) {
		switch (button) {
		case 113236:
			if(player.is_did_you_know_activated()) {
				player.setDidYouKnow(false);
			} else {
				player.setDidYouKnow(true);
			}
			write(player, "<col=FFFFFF>Did you know: "+(player.is_did_you_know_activated() ? "<col=00CC00>Enabled" : "<col=ff0000>Disabled"), 3);
			break;
		case 113237:
			if(player.is_trivia_activated()) {
				player.setTrivia(false);
			} else {
				player.setTrivia(true);
			}
			write(player, "<col=FFFFFF>Trivia: <col=00CC00>"+(player.is_trivia_activated() ? "<col=00CC00>Enabled" : "<col=ff0000>Disabled"), 4);
			break;
		}
	}

}
