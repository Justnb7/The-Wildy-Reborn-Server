package com.model.game.character.player.content.achievements;

import java.io.BufferedWriter;

import com.model.game.character.player.Player;
import com.model.game.character.player.content.achievements.Achievements.Achievement;
import com.model.game.character.player.packets.out.SendConfigPacket;
import com.model.game.character.player.packets.out.SendInterfacePacket;



public class AchievementHandler {
	
	Player player;
	
	public int currentInterface;
	private static final int MAXIMUM_TIER_ACHIEVEMENTS = 100;
	private static final int MAXIMUM_TIERS = 3;
	private int[][] amountRemaining = new int[MAXIMUM_TIERS][MAXIMUM_TIER_ACHIEVEMENTS];
	private boolean[][] completed = new boolean[MAXIMUM_TIERS][MAXIMUM_TIER_ACHIEVEMENTS];
	
	public int points;
	
	/**
	 * WARNING: ADD TO THE END OF THE LIST.
	 */
	private int boughtItems[][] = {
			{ 12436, -1, 7 }, { 12437, -1, 13 }, { 12439, -1, 5 }, { 11847, -1, 15 },
	};
	
	public AchievementHandler(Player player) {
		this.player = player;
	}
	
	public void print(BufferedWriter writer, int tier) {
		try {
			for(Achievements.Achievement achievement : Achievement.ACHIEVEMENTS) {
				if(achievement.getTier().ordinal() == tier) {
					if(amountRemaining[tier][achievement.getId()] > 0) {
						writer.write(achievement.name().toLowerCase() + " = "+amountRemaining[tier][achievement.getId()]+ "\t"+completed[tier][achievement.getId()]);
						writer.newLine();
					}
				}
			}
		} catch (Exception e) {}
	}
	
	public void read(String name, int tier, int amount, boolean state) {
		for(Achievements.Achievement achievement : Achievements.Achievement.ACHIEVEMENTS) {
			if(achievement.getTier().ordinal() == tier) {
				if(achievement.name().toLowerCase().equals(name)) {
					this.setComplete(tier, achievement.getId(), state);
					this.setAmountRemaining(tier, achievement.getId(), amount);
					break;
				}
			}
		}
	}
	
	public void drawInterface(int tier) {
		int scrollId = tier == 0 ? 49101 : tier == 1 ? 51101 : 53101;
		player.getActionSender().sendInterfaceConfig(tier == 0 ? 0 : 1, 49100);
		player.getActionSender().sendInterfaceConfig(tier == 1 ? 0 : 1, 51100);
		player.getActionSender().sendInterfaceConfig(tier == 2 ? 0 : 1, 53100);
		player.write(new SendConfigPacket(800, tier == 0 ? 1 : 0));
		player.write(new SendConfigPacket(801, tier == 1 ? 1 : 0));
		player.write(new SendConfigPacket(802, tier == 2 ? 1 : 0));
		player.getActionSender().sendString(Integer.toString(this.getPoints()), 49016);
		int components = 0;
		for(Achievement achievement : Achievement.ACHIEVEMENTS) {
			if(achievement.getTier().ordinal() == tier) {
				components++;
				int amount = getAmountRemaining(achievement.getTier().ordinal(), achievement.getId());
				if(amount > achievement.getAmount())
					amount = achievement.getAmount();
				player.getActionSender().sendString(Integer.toString(achievement.getPoints()), scrollId + 300 + achievement.getId());
				player.getActionSender().sendString(achievement.name().toUpperCase().replaceAll("_", " "), scrollId + 400 + achievement.getId());
				player.getActionSender().sendString(achievement.getDescription(), scrollId + 500 + achievement.getId());
				player.getActionSender().sendString(amount+"/"+achievement.getAmount(), scrollId + 700 + achievement.getId());
			}
		}
		player.getActionSender().sendString(Integer.toString(components), 49020);
		player.write(new SendInterfacePacket(49000));
	}
	
	public boolean hasCompletedAll() {
		int amount = 0;
		for(Achievement achievement : Achievement.ACHIEVEMENTS) {
			if(isComplete(achievement.getTier().ordinal(), achievement.getId()))
				amount++;
		}
		return amount == Achievements.getMaximumAchievements();
	}
	
	public boolean completedTier(AchievementTier tier) {
			for(Achievement achievement : Achievement.ACHIEVEMENTS)
				if(achievement.getTier() == tier)
					if(!isComplete(achievement.getTier().ordinal(), achievement.getId()))
						return false;
			return true;
	}
	
	public boolean isComplete(int tier, int index) {
		return completed[tier][index];
	}
	
	public boolean setComplete(int tier, int index, boolean state) {
		return this.completed[tier][index] = state;
	}

	public int getAmountRemaining(int tier, int index) {
		return amountRemaining[tier][index];
	}

	public void setAmountRemaining(int tier, int index, int amountRemaining) {
		this.amountRemaining[tier][index] = amountRemaining;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
	
	public boolean isAchievementItem(int itemId) {
		for(int i = 0; i < boughtItems.length; i++)
			if(boughtItems[i][0] == itemId)
				return true;
		return false;
	}
	
	public boolean hasBoughtItem(int itemId) {
		for(int i = 0; i < boughtItems.length; i++)
			if(boughtItems[i][0] == itemId)
				if(boughtItems[i][1] != -1)
					return true;
		return false;
	}
	
	public void setBoughtItem(int itemId) {
		for(int i = 0; i < boughtItems.length; i++)
			if(boughtItems[i][0] == itemId)
				boughtItems[i][1] = 1;
	}
	
	public int[][] getBoughtItems() {
		return this.boughtItems;
	}
	
	public void setBoughtItem(int index, int value) {
		if(index > this.boughtItems.length - 1)
			return;
		this.boughtItems[index][1] = value;
	}
	
	public int getCost(int itemId) {
		for (int i = 0; i < boughtItems.length; i++) {
			if (itemId == boughtItems[i][0]) {
				return boughtItems[i][2];
			}
		}
		return Integer.MAX_VALUE;
	}
	
}