package com.venenatis.game.content.titles;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;

import com.venenatis.game.content.achievements.*;
import com.venenatis.game.content.achievements.Achievements.Achievement;
import com.venenatis.game.model.Skills;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.Rights;

/**
 * Each element of the enum represents a singular title with an array of qualities.
 * <p>
 * <b> Please note that by default the description is wrapped to fit into the open area so there is no need to use escape characters in the description. </b>
 * </p>
 * 
 * @author Jason MacKeigan
 * @date Jan 22, 2015, 3:44:52 PM
 */
public enum Title implements Comparator<Title> {
	NONE("None", 0, TitleCurrency.NONE, Titles.NO_REQUIREMENT, "No title will be displayed."),

	CUSTOM("Custom", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isDonator(player) || player.getRights().isStaffMember(player);
		}
	}, "Have the option of choosing your own 16-character title. You must be a donator of any rank to display this title."),

	DONATOR("Donator", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().inherits(Rights.DONATOR);
		}
	}, "This title is for Donators. You must be a Donator to purchase and display this title."),

	SUPER_DONATOR("Super Donator", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().inherits(Rights.SUPER_DONATOR);
		}
	}, "This title is for Super Donators. You must be a Super Donator to purchase and display this title."),

	ELITE_DONATOR("Elite Donator", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().inherits(Rights.ELITE_DONATOR);
		}
	}, "This title is for Elite Donators. You must be a Elite Donator to purchase and display this title."),

	EXTREME_DONATOR("Extreme Donator", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().inherits(Rights.EXTREME_DONATOR);
		}
	}, "This title is for Extreme Donators. You must be a Extreme Donator to purchase and display this title."),

	HELPER("Helper", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isHelper(player);
		}
	}, "This title represents the helper rank. You must be a helper to purchase or display this title."),

	MODERATOR("Moderator", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isModerator(player);
		}
	}, "A unique and powerful title that when displayed represents the high level of power the owner has." + "Only moderators on the staff team may display this."),

	ADMINISTRATOR("Administrator", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isOwner(player);
		}
	}, "A unique and powerful title that when displayed represents the high level of power the owner has." + "Only administrators on the staff team may display this."),

	EXECUTIVE_OFFICER("Executive Officer", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isOwner(player);
		}
	}, "The highest ranking title any person may have. This title can only be worn by those who truly deserve it."),

	JUNIOR_CADET("Junior Cadet", 50, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getKillCount() >= 15;
		}
	}, "A Junior Cadet is a player that has achieved at least fifteen player kills."),

	SENIOR_CADET("Senior Cadet", 75, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getKillCount() >= 25;
		}
	}, "A Senior Cadet is a player that has achieved at least twenty five kills."),

	SEGEART("Sergeant", 90, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getKillCount() >= 50;
		}
	}, "A Sergeant is a player that has achieved at least fifty player kills."),

	COMMANDER("Commander", 115, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getKillCount() >= 100;
		}
	}, "A Commander is a player that has achieved at least one hundred player kills."),

	MAJOR("Major", 120, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getKillCount() >= 150;
		}
	}, "A Major is a player that has achieved at least one hundred and fifty player kills."),

	CORPORAL("Corporal", 125, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getKillCount() >= 200;
		}
	}, "A Corporal is a player that has achieved at least two hundred player kills."),

	INSANE("Insane", 145, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getKillCount() >= 250;
		}
	}, "Insane is a player that has achieved at least two hundred and fifty player kills."),

	WARCHIEF("War-chief", 200, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getKillCount() >= 500;
		}
	}, "A War-chief is a player that has achieved at least five hundred player kills."),
	
	FISHERMAN("Fisherman", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return Achievements.isCompleted(player, Achievement.FISHERMAN);
		}
	}, "A Fisherman is a player that has fished at least 1,000 fish."),
	
	LUMBERJACK("Lumberjack", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return Achievements.isCompleted(player, Achievement.LUMBERJACK);
		}
	}, "A Lumberjack is a player that has cut down trees and has accumulated at least 1,000 logs."),
	
	SKILLER("#Skiller", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getSkills().maxedSkiller();
		}
	}, "To receive access to this title, a player must have maxed out all skills while having combat stats at 1."),

	DO_YOU_LIFT("#DOuEvenLift", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getSkills().maxedCertain(0, 6);
		}
	}, "To receive access to this title, a player must have maxed out all combat skills."),
	
	MORPH("#CanUSeeMe", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getInventory().contains(20005) && player.getInventory().contains(20017);
		}
	}, "To receive access to this title, a player must have the ring of coins and ring of nature in their inventory."),

	IRON_MAN("Ironman", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isIronman(player);
		}
	}, "To receive access to this title, a player must be on the ironman game-mode."),

	ULTIMATE_IRON_MAN("Ult. Ironman", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isUltimateIronman(player);
		}
	}, "To receive access to this title, a player must be on the ultimate ironman game-mode."),
	
	HARDCORE_IRON_MAN("Hardcore. Ironman", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isHardcoreIronman(player);
		}
	}, "To receive access to this title, a player must be on the ultimate ironman game-mode."),
	
	SLAYER_MASTER("Slayer Master", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getSkills().isCertainSkillMaxed(Skills.SLAYER);
		}
	}, "To receive access to this title, a player must be have reached 99 slayer."),
	
	SLAYER_FANATIC("Slayer Fanatic", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getSlayerTasksCompleted() >= 100;
		}
	}, "To receive access to this title, a player must be have completed atleast 100 slayer tasks."),
	
	TOP_VOTER("#Top Voter", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getTotalVotes() >= 500;
		}
	}, "To receive access to this title, a player must have voted atleast 500 times."),
	
	RNG_GOD("#RNG God", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.unlockedRngGod();
		}
	}, "To receive access to this title, a player must have speced over 75 with an AGS!"),
	
	COMPLETIONIST("#BAH Exp Waste", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.unlockedRngGod();
		}
	}, "To receive access to this title, a player must have unlocked the max cape!"),
	
	LUCKY("#Lucky", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.lucky();
		}
	}, "To receive access to this title, a player must have received a rare item from a mystery box."),

	MILLIONAIRE("#Millionaire", 100_000_000, TitleCurrency.COINS, Titles.NO_REQUIREMENT, "To receive access to this title, a player must be a millionaire. Simple.");

	/**
	 * The name of the title when displayed
	 */
	private final String name;

	/**
	 * The requirement to display or purchase this title
	 */
	private final TitleRequirement requirement;

	/**
	 * The currency used to purchase the title
	 */
	private final TitleCurrency currency;

	/**
	 * The cost to purchase the title
	 */
	private final int cost;

	/**
	 * The description displayed on the interface
	 */
	private final String description;

	/**
	 * Represents a single title.
	 * 
	 * @param name the name of the title
	 * @param color the color of each character
	 * @param currency the currency used to purchase
	 * @param cost the cost, or amount, of currency required to purchase
	 * @param requirement the requirement to purcahse or display
	 */
	private Title(String name, int cost, TitleCurrency currency, TitleRequirement requirement, String description) {
		this.name = name;
		this.cost = cost;
		this.currency = currency;
		this.requirement = requirement;
		this.description = description;
	}

	/**
	 * The name of the title
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The currency used to purchse the title
	 * 
	 * @return the currency
	 */
	public TitleCurrency getCurrency() {
		return currency;
	}

	/**
	 * The cost to purchase the title
	 * 
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * The requirement that must be met before purchasing the title
	 * 
	 * @return the requirement
	 */
	public TitleRequirement getRequirement() {
		return requirement;
	}

	/**
	 * The sequence of words that define the title
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Compares the cost value of both titles and returns -1, 0, or +1. This follows the comparable contract.
	 */
	@Override
	public int compare(Title o1, Title o2) {
		if (o1.cost > o2.cost) {
			return 1;
		} else if (o1.cost < o2.cost) {
			return -1;
		}
		return 0;
	}

	/**
	 * A set of elements from the {@link Title} enum. This set is unmodifiable in any regard. This set serves as a convenience.
	 */
	public static final Set<Title> TITLES = Collections.unmodifiableSet(EnumSet.allOf(Title.class));
}