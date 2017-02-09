package com.model.game.character.player.content.questtab;

import com.model.game.character.npc.Npc;
import com.model.game.character.player.Player;


/**
 * The home quest tab page with all of the player information
 * 
 * @author Patrick van Elderen
 *
 */
public class HomeQuestTabPage extends QuestTabPage {

	@Override
	public void write(Player player) {
		write(player, "<img=12><col=FFFFFF>Kill death ratio: <col=00CC00>"+player.getPA().displayRatio(player), 1);
		write(player, "<img=27><col=FFFFFF>Kills: <col=00CC00>"+ player.getKillCount(), 2);
		write(player, "<img=27><col=FFFFFF>Deaths: <col=00CC00>"+ player.getDeathCount(), 3);
		write(player, "<img=27><col=FFFFFF>Current killstreak: <col=00CC00>"+ player.getCurrentKillStreak(), 4);
		write(player, "<img=27><col=FFFFFF>Highest killstreak: <col=00CC00>"+ player.getHighestKillStreak(), 5);
		write(player, "<img=27><col=FFFFFF>Wilderness killstreak: <col=00CC00>"+ player.getWildernessKillStreak(), 6);
		write(player, "<img=27><col=FFFFFF>Targets Killed: <col=00CC00>"+ player.getWildernessKillStreak(), 7);
		write(player, "<img=27><col=FFFFFF>Target Points: <col=00CC00>"+ player.getWildernessKillStreak(), 8);
		write(player, "<img=27><col=FFFFFF>Bounties: <col=00CC00>"+ player.getBountyPoints(), 9);
		if(player.getSlayerTaskAmount() <= 0) {
			write(player, "<img=28><col=FFFFFF>Task: <col=00CC00>None", 10);
		} else {
			write(player, "<img=28><col=FFFFFF>Task: <col=00CC00>"+player.getSlayerTaskAmount()+ " "+Npc.getName(player.getSlayerTask()), 10);
		}
		write(player, "<img=28><col=FFFFFF>tasks completed: <col=00CC00>"+ player.getSlayerTasksCompleted(), 11);
		write(player, "<img=28><col=FFFFFF>Slayer Reward Points: <col=00CC00>"+ player.getSlayerPoints(), 12);
		write(player, "<img=29><col=FFFFFF>Total Votes: <col=00CC00>"+ player.getTotalVotes(), 13);
		write(player, "<img=29><col=FFFFFF>Vote points: <col=00CC00>"+ player.getVotePoints(), 14);
		write(player, "<img=26><col=FFFFFF>Amount Donated: <col=00CC00>"+ player.getTotalAmountDonated()+ "$", 15);
	}
	
	@Override
	public void onButtonClick(Player player, int button) {
		switch (button) {

		}
	}

}
