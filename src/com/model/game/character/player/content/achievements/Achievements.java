package com.model.game.character.player.content.achievements;

import java.util.EnumSet;
import java.util.Set;

import com.model.game.character.player.Player;
import com.model.game.character.player.PlayerUpdating;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;


public class Achievements {
	
	public enum Achievement {
		
		/**
		 * Tier 1 Achievement Start
		 */
	INTERMEDIATE_PKER(0, AchievementTier.TIER_1, AchievementType.KILL_PLAYER, null, "Kill 25 Players", 25, 1),
	
		/**
		 * Tier 2 Achievement Start
		 */
	AMAZING_PKER(0, AchievementTier.TIER_2, AchievementType.KILL_PLAYER, null, "Kill 250 Players", 250, 2),
                
		/**
		 * Tier 3 Achievement Start
		 */
	INSANE(0, AchievementTier.TIER_3, AchievementType.KILL_PLAYER, null, "Kill 800 Players", 800, 3);
		
		
		
		
		private AchievementTier tier;
		private AchievementRequirement requirement;
		private AchievementType type;
		private String description;
		private int amount, identification, points;
		
		Achievement(int identification, AchievementTier tier, AchievementType type, AchievementRequirement requirement, String description, int amount, int points) {
			this.identification = identification;
			this.tier = tier;
			this.type = type;
			this.requirement = requirement;
			this.description = description;
			this.amount = amount;
			this.points = points;
		}
		
		public int getId() {
			return identification;
		}
		
		public AchievementTier getTier() {
			return tier;
		}
		
		public AchievementType getType() {
			return type;
		}
		
		public AchievementRequirement getRequirement() {
			return requirement;
		}
		
		public String getDescription() {
			return description;
		}
		
		public int getAmount() {
			return amount;
		}
		
		public int getPoints() {
			return points;
		}
		
		public static final Set<Achievement> ACHIEVEMENTS = EnumSet.allOf(Achievement.class);
		
		public static Achievement getAchievement(AchievementTier tier, int ordinal) {
			for(Achievement achievement : ACHIEVEMENTS)
				if(achievement.getTier() == tier && achievement.ordinal() == ordinal)
					return achievement;
			return null;
		}
		
		public static boolean hasRequirement(Player player, AchievementTier tier, int ordinal) {
			for(Achievement achievement : ACHIEVEMENTS) {
				if(achievement.getTier() == tier && achievement.ordinal() == ordinal) {
					if(achievement.getRequirement() == null)
						return true;
					if(achievement.getRequirement().isAble(player))
						return true;
				}
			}
			return false;
		}
	}
	
	public static void increase(Player player, AchievementType type, int amount) {
		for (Achievement achievement : Achievement.ACHIEVEMENTS) {
			if (achievement.getType() == type) {
				if (achievement.getRequirement() == null || achievement.getRequirement().isAble(player)) {
					int currentAmount = player.getAchievements().getAmountRemaining(achievement.getTier().ordinal(), achievement.getId());
					int tier = achievement.getTier().ordinal();
					if (currentAmount < achievement.getAmount() && !player.getAchievements().isComplete(achievement.getTier().ordinal(), achievement.getId())) {
						player.getAchievements().setAmountRemaining(tier, achievement.getId(), currentAmount + amount);
						if ((currentAmount + amount) >= achievement.getAmount()) {
							String name = achievement.name().toLowerCase().replaceAll("_", " ");
							player.getAchievements().setComplete(tier, achievement.getId(), true);
							player.getAchievements().setPoints(achievement.getPoints() + player.getAchievements().getPoints());
							player.write(new SendMessagePacket("Achievement completed on tier " + (tier + 1) + ": '" + achievement.name().toLowerCase().replaceAll("_", " ") + "' and receive " + achievement.getPoints() + " point(s)."));
							PlayerUpdating.executeGlobalMessage("<col=7a008e>" + player.getName() + "</col> completed the achievement " + name + " on tier <col=ff0033> " + (tier + 1) + "</col>.");
	 						
						}
					}
				}
			}
		}
	}
	
	public static void reset(Player player, AchievementType type) {
		for(Achievement achievement : Achievement.ACHIEVEMENTS) {
			if(achievement.getType() == type) {
				if(achievement.getRequirement() == null || achievement.getRequirement().isAble(player)) {
					if(!player.getAchievements().isComplete(achievement.getTier().ordinal(), achievement.getId())) {
						player.getAchievements().setAmountRemaining(achievement.getTier().ordinal(), achievement.getId(), 0);
					}
				}
			}
		}
	}
	
	public static void complete(Player player, AchievementType type) {
		for(Achievement achievement : Achievement.ACHIEVEMENTS) {
			if(achievement.getType() == type) {
				if(achievement.getRequirement() != null && achievement.getRequirement().isAble(player)
						&& !player.getAchievements().isComplete(achievement.getTier().ordinal(), achievement.getId())) {
					int tier = achievement.getTier().ordinal();
					//String name = achievement.name().replaceAll("_", " ");
					player.getAchievements().setAmountRemaining(tier, achievement.getId(), achievement.getAmount());
					player.getAchievements().setComplete(tier, achievement.getId(), true);
					player.getAchievements().setPoints(achievement.getPoints() + player.getAchievements().getPoints());
					player.write(new SendMessagePacket("Achievement completed on tier "+(tier + 1)+": '"+achievement.name().toLowerCase().replaceAll("_", " ")+"' and receive "+achievement.getPoints()+" point(s)."));
				}
			}
		}
	}
	
	public static int getMaximumAchievements() {
		return Achievement.ACHIEVEMENTS.size();
	}
}