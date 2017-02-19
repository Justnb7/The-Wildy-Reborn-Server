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
		NOVICE_PKER(0, AchievementTier.TIER_1, AchievementType.KILL_PLAYER, null, "Kill 25 Players", 25, 1),
		NOVICE_BOUNTY_HUNTER(1, AchievementTier.TIER_1, AchievementType.BOUNTY_HUNTER, null, "Kill 50 Targets", 50, 1),
		KILL_50_VENENATIS(2, AchievementTier.TIER_1, AchievementType.VENENATIS, null, "Kill 50 Venenatis", 50, 1),
		KILL_50_CALLISTO(3, AchievementTier.TIER_1, AchievementType.CALLISTO, null, "Kill 50 Callistos", 50, 1),
		KILL_50_VETION(4, AchievementTier.TIER_1, AchievementType.VETION, null, "Kill 50 Vet'ions", 50, 1),
		KILL_50_KING_BLACK_DRAGON(5, AchievementTier.TIER_1, AchievementType.KING_BLACK_DRAGON, null, "Kill 50 King black dragons", 50, 1),
		KILL_50_SCORPIA(6, AchievementTier.TIER_1, AchievementType.SCORPIA, null, "Kill 50 Scorpias", 50, 1),
		KILL_50_KRAKEN(7, AchievementTier.TIER_1, AchievementType.KRAKEN, null, "Kill 50 Krakens", 50, 1),
		KILL_50_CHAOS_FANATIC(8, AchievementTier.TIER_1, AchievementType.CHAOS_FANATIC, null, "Kill 50 Chaos fanatics", 50, 1),
		KILL_50_CRAZY_ARCHAEOLOGIST(9, AchievementTier.TIER_1, AchievementType.CRAZY_ARCHAEOLOGIST, null, "Kill 50 Crazy archaeologists", 50, 1),
		KILL_50_CHAOS_ELEMENTAL(10, AchievementTier.TIER_1, AchievementType.CHAOS_ELEMENTAL, null, "Kill 50 Chaos elementals", 50, 1),
		KILL_50_CORPOREAL_BEAST(11, AchievementTier.TIER_1, AchievementType.CORPOREAL_BEAST, null, "Kill 50 Corporeal beasts", 50, 1),
		KILL_50_ZOMBIE_CHAMPION(12, AchievementTier.TIER_1, AchievementType.ZOMBIE_CHAMPION, null, "Kill 50 Zombies champions", 50, 1),
		KILL_50_BARRELCHEST(13, AchievementTier.TIER_1, AchievementType.BARRELCHEST, null, "Kill 50 Barrelchests", 50, 1),
		KILL_50_KREE_ARRA(14, AchievementTier.TIER_1, AchievementType.KREE_ARRA, null, "Kill 50 Kree'arras", 50, 1),
		KILL_50_GENERAL_GRAARDOR(15, AchievementTier.TIER_1, AchievementType.GENERAL_GRAARDOR, null, "Kill 50 General Graardors", 50, 1),
		KILL_50_COMMANDER_ZILYANA(16, AchievementTier.TIER_1, AchievementType.COMMANDER_ZILYANA, null, "Kill 50 Commander zilyanas", 50, 1),
		KILL_50_KRIL_TSUTSAROTH(17, AchievementTier.TIER_1, AchievementType.KRIL_TSUTSAROTH, null, "Kill 50 K'ril Tsutsaroths", 50, 1),
		TREASURE_TRIALS(18, AchievementTier.TIER_1, AchievementType.TREASURE_TRIAL, null, "Complete 75 treasure trials", 75, 1),
		
		/**
		 * Tier 2 Achievement Start
		 */
		INTERMEDIATE_PKER(0, AchievementTier.TIER_2, AchievementType.KILL_PLAYER, null, "Kill 250 Players", 250, 2),
		INTERMEDIATE_BOUNTY_HUNTER(1, AchievementTier.TIER_2, AchievementType.BOUNTY_HUNTER, null, "Kill 250 Targets", 250, 2),
		BOUNTY_HUNTER(2, AchievementTier.TIER_2, AchievementType.BOUNTIES, null, "Claim 50 million bounties", 50_000_000, 2),
		
		
		/**
		 * Tier 3 Achievement Start
		 */
		VETERAN(0, AchievementTier.TIER_3, AchievementType.KILL_PLAYER, null, "Kill 800 Players", 800, 3),
		VETERAN_BOUNTY_HUNTER(1, AchievementTier.TIER_3, AchievementType.BOUNTY_HUNTER, null, "Kill 500 Targets", 500, 3);
		

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
			for (Achievement achievement : ACHIEVEMENTS)
				if (achievement.getTier() == tier && achievement.ordinal() == ordinal)
					return achievement;
			return null;
		}

		public static boolean hasRequirement(Player player, AchievementTier tier, int ordinal) {
			for (Achievement achievement : ACHIEVEMENTS) {
				if (achievement.getTier() == tier && achievement.ordinal() == ordinal) {
					if (achievement.getRequirement() == null)
						return true;
					if (achievement.getRequirement().isAble(player))
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