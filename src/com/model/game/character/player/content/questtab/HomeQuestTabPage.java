package com.model.game.character.player.content.questtab;

import java.util.concurrent.TimeUnit;

import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.npc.NPC;
import com.model.game.character.player.Player;
import com.model.server.Server;


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
		player.getActionSender().sendString("Online: @gre@" + World.getWorld().getActivePlayers() + "", 29155);
		player.getActionSender().sendString("Information", 663);
		
		write(player, "<col=FFFFFF>Rank: <col=00CC00>"+Constants.rank(player, player.getRights().getValue()), 1);
		write(player, "<col=FFFFFF>Played: <col=00CC00>"+time, 2);
		write(player, "<col=FFFFFF>Did you know: "+(player.didYouKnow ? "<col=00CC00>Enabled" : "<col=ff0000>Disabled"), 3);
		write(player, "<col=FFFFFF>Trivia: <col=00CC00>"+(player.trivia ? "<col=00CC00>Enabled" : "<col=ff0000>Disabled"), 4);
		write(player, "", 5);
		
		write(player, "<img=27><col=FFFFFF>Kills: <col=00CC00>"+ player.getKillCount(), 6);
		write(player, "<img=27><col=FFFFFF>Deaths: <col=00CC00>"+ player.getDeathCount(), 7);
		write(player, "<img=27><col=FFFFFF>Current killstreak: <col=00CC00>"+ player.getCurrentKillStreak(), 8);
		write(player, "<img=27><col=FFFFFF>Highest killstreak: <col=00CC00>"+ player.getHighestKillStreak(), 9);
		write(player, "<img=27><col=FFFFFF>Wilderness killstreak: <col=00CC00>"+ player.getWildernessKillStreak(), 10);
		write(player, "<img=27><col=FFFFFF>Targets Killed: <col=00CC00>"+ player.getWildernessKillStreak(), 11);
		write(player, "<img=27><col=FFFFFF>Target Points: <col=00CC00>"+ player.getWildernessKillStreak(), 12);
		write(player, "<img=27><col=FFFFFF>Bounties: <col=00CC00>"+ player.getBountyPoints(), 13);
		if(player.getSlayerTaskAmount() <= 0) {
			write(player, "<img=28><col=FFFFFF>Task: <col=00CC00>None", 14);
		} else {
			write(player, "<img=28><col=FFFFFF>Task: <col=00CC00>"+player.getSlayerTaskAmount()+ " "+NPC.getName(player.getSlayerTask()), 14);
		}
		write(player, "<img=28><col=FFFFFF>tasks completed: <col=00CC00>"+ player.getSlayerTasksCompleted(), 15);
		write(player, "<img=28><col=FFFFFF>Slayer Reward Points: <col=00CC00>"+ player.getSlayerPoints(), 16);
		write(player, "<img=29><col=FFFFFF>Total Votes: <col=00CC00>"+ player.getTotalVotes(), 17);
		write(player, "<img=29><col=FFFFFF>Vote points: <col=00CC00>"+ player.getVotePoints(), 18);
		write(player, "<img=26><col=FFFFFF>Amount Donated: <col=00CC00>"+ player.getTotalAmountDonated()+ "$", 19);
	}
	
	@Override
	public void onButtonClick(Player player, int button) {
		switch (button) {
		case 113236:
			player.setDidYouKnow(player.didYouKnow = !player.didYouKnow);
			write(player, "<col=FFFFFF>Did you know: "+(player.didYouKnow ? "<col=00CC00>Enabled" : "<col=ff0000>Disabled"), 3);
			break;
		case 113237:
			player.setTrivia(player.trivia = !player.trivia);
			write(player, "<col=FFFFFF>Trivia: <col=00CC00>"+(player.trivia ? "<col=00CC00>Enabled" : "<col=ff0000>Disabled"), 4);
			break;
			
			
		case 114092:
			Server.getDropManager().open(player);
			break;
		}
	}

}
