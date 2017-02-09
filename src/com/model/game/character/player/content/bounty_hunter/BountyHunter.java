package com.model.game.character.player.content.bounty_hunter;

import java.util.Objects;
import java.util.Optional;

import com.model.Server;
import com.model.game.Constants;
import com.model.game.World;
import com.model.game.character.combat.combat_data.CombatRequirements;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.content.teleport.TeleportExecutor;
import com.model.game.character.player.packets.encode.impl.CreatePlayerHint;
import com.model.game.character.player.packets.encode.impl.SendConfig;
import com.model.game.character.player.packets.encode.impl.SendString;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.packets.encode.impl.SendStringColor;
import com.model.game.location.Location;
import com.model.task.ScheduledTask;
import com.model.utility.Utility;
import com.model.utility.cache.map.Region;



/**
 * Handles the bounty hunter system
 * 
 * @author Mobster
 *
 */
public class BountyHunter extends ScheduledTask {
	
	/**
	 * The time in milliseconds of the players last teleport
	 */
	static long lastTeleport;

	/**
	 * Pulses the bounty hunter system for the {@link Player}
	 * 
	 * @param player
	 *            The {@link Player} to process bounty hunter for
	 */
	public static void pulse(Player player) {

		/*
		 * Write the interface since we are in the wilderness
		 */
		if (player.getArea().inWild()) {
			
			if (Constants.bountyHunterActive) {
				writeInterface(player);
			}

			/*
			 * If we dont' have a target, find one
			 */
			if (player.getAttribute(BountyHunterConstants.BOUNTY_TARGET, 0) == 0) {
				if (Utility.getRandom(BountyHunterConstants.NEXT_TARGET_DELAY) == 1) {
					findTarget(player);
				}
			}
		}

		/*
		 * Make sure we have a target first, then make sure your target is still
		 * logged in and in the wilderness
		 */
		if (player.getAttribute(BountyHunterConstants.BOUNTY_TARGET, 0) != 0) {
			if (!validateTarget(player)) {
				player.setAttribute(BountyHunterConstants.BOUNTY_TARGET, 0);
				player.write(new CreatePlayerHint(10, -1));
				for (int i = 0; i < 6; i++) {
					player.write(new SendConfig(BountyHunterConstants.V_LOW_WEALTH_CONFIG + i, 0));
				}
				player.write(new SendConfig(BountyHunterConstants.DEFAULT_CONFIG, 0));
			}
		}
	}

	/**
	 * Writes the bounty hunter interface for the player
	 * 
	 * @param player
	 *            The {@link Player} to write the interface for
	 */
	public static void writeInterface(Player player) {
		String targetName = "None";
		int level = -1;
		int minLevel = 0;
		int maxLevel = 0;
		int combat = 0;
		int index = player.getAttribute(BountyHunterConstants.BOUNTY_TARGET, 0);
		Player target = World.getWorld().getPlayers().get(index);
		String wealth = "---";
		if (target != null) {
			targetName = target.getName();
			level = target.getArea().inWild() ? target.wildLevel : 0;
			minLevel = level - 2 <= 0 ? 1 : level - 2;
			maxLevel = level + 2;
			combat = target.combatLevel;
			wealth = target.getAttribute("bounty_wealth", "---");
		}

		player.write(new SendString(wealth, BountyHunterConstants.WEALTH_STRING_ID));
		player.write(new SendString(targetName, BountyHunterConstants.TARGET_NAME_STRING_ID));
		int levelDifference = player.wildLevel - level;
		int color = (0 << 10 | 255 << 5 | 0);
		if (level != -1) {
			if (levelDifference > 10 || levelDifference < -10) {
				color = (255 << 10 | 0 << 5 | 0);
			} else if (levelDifference > 5 || levelDifference < -5) {
				color = (255 << 10 | 255 << 5 | 0);
			}
		}
		player.write(new SendStringColor(BountyHunterConstants.LEVEL_COMBAT_STRING_ID, color));
		player.write(new SendString((level == 0 ? "Safe" : level > 0 ? (("Lvl " + minLevel + "-" + maxLevel) + ", Cmb " + combat) : "Level: ------"), BountyHunterConstants.LEVEL_COMBAT_STRING_ID));
	}

	/**
	 * Validates your target is still good
	 * 
	 * @param player
	 *            The {@link Player} to validate the target for
	 * @return If the target is still good
	 */
	private static boolean validateTarget(Player player) {
		Player target = World.getWorld().getPlayers().get(player.getAttribute(BountyHunterConstants.BOUNTY_TARGET, 0));

		if (target == null || !target.isActive()) {
			player.write(new SendMessagePacket("@red@Your target has logged out, searching for new target."));
			return false;
		}

		if (!player.getArea().inWild()) {

			long delay = player.getAttribute("left_wild_delay", 0L);
			long elapsed = System.currentTimeMillis() - delay;

			if (elapsed <= 1000) {
				player.write(new SendMessagePacket("@red@You have 2 minutes to return to the Wilderness before you lose your target."));
			} else if (elapsed >= 60_000 && elapsed <= 60_599) {
				player.write(new SendMessagePacket("@red@You have 1 minutes to return to the Wilderness before you lose your target."));
			} else if (System.currentTimeMillis() - delay >= 120_000) {
				player.write(new SendMessagePacket("@red@You have abandoned your target."));
				return false;
			}
		}

		if (!target.getArea().inWild()) {
			long delay = target.getAttribute("left_wild_delay", 0L);
			if (System.currentTimeMillis() - delay >= 120_000) {
				player.write(new SendMessagePacket("@red@The target has left the wilderness, searching for new target."));
				return false;
			}

		}
		return true;
	}
	
	/**
	 * Teleports the player the their target so long as both players
	 * meet certain conditions such as level requirement, item requirements,
	 * etcetera. 
	 */
	public static void teleportToTarget(Player player) {
		int index = player.getAttribute(BountyHunterConstants.BOUNTY_TARGET, 0);
		World.getWorld();
		Player target = World.getWorld().getPlayers().get(index);
		if (!player.spellAccessible) {
			player.write(new SendMessagePacket("You do not have access to this spell, you must learn about it first."));
			return;
		}
		if (player.getSkills().getLevel(Skills.MAGIC) < 85) {
			player.write(new SendMessagePacket("You need a magic level of 85 to use this spell."));
			return;
		}
		/*if (!target.isActive()) {
			player.sendMessage("You need to have a target to use this spell.");
			return;
		}*/
		if (System.currentTimeMillis() - lastTeleport < 30_000) {
			player.write(new SendMessagePacket("You can only use this spell every 30 seconds."));
			return;
		}
		if (Objects.isNull(target)) {
			player.write(new SendMessagePacket("Your target cannot be found."));
			return;
		}
		if (!target.getArea().inWild()) {
			player.write(new SendMessagePacket("Your target is not in the wilderness, they must be to be teleported to."));
			return;
		}
		if (player.playerIndex > 0 && target.getIndex() == player.playerIndex) {
			player.write(new SendMessagePacket("You cannot use this spell whilst in combat with your target."));
			return;
		}
		int targetX = target.getX();
		int targetY = target.getY();
		for (int teleX = targetX - 1; teleX < targetX + 2; teleX++) {
			for (int teleY = targetY - 1; teleY < targetY + 2; teleY++) {
				if (!Region.canMove(teleX, teleY, teleX + 1, teleY + 1, target.heightLevel, 1, 1)) {
					player.write(new SendMessagePacket("Your target is in a blocked in area, you cannot teleport to them right now."));
					return;
				}
			}
		}
		TeleportExecutor.teleport(player, new Location(targetX, targetY - 1, target.heightLevel));
		lastTeleport = System.currentTimeMillis();
	}

	/**
	 * Finds a new {@link Player} to set as a bounty target
	 * 
	 * @param player
	 *            The {@link Player} To find a new target for
	 */
	public static void findTarget(Player player) {
		for (Player target : World.getWorld().getPlayers()) {
			if (target == null || !target.getArea().inWild() || target.equals(player)) {
				continue;
			}
			
			if (System.currentTimeMillis() - player.getAttribute("login_delay", 0L) < BountyHunterConstants.LOGIN_DELAY_WAIT) {
				continue;
			}
			
			if (System.currentTimeMillis() - player.getAttribute("last_bounty_kill", 0L) < BountyHunterConstants.LAST_BOUNTY_KILL_DELAY) {
				continue;
			}

			/*
			 * The difference between yours and the targets combat level
			 */
			int difference = CombatRequirements.getCombatDifference(player.combatLevel, target.combatLevel);

			/*
			 * Check that they are within 3 combat levels of us
			 */
			if (difference < -BountyHunterConstants.MAXIMUM_LEVEL_DIFFERENCE || difference > BountyHunterConstants.MAXIMUM_LEVEL_DIFFERENCE) {
				continue;
			}

			/*
			 * check if the target already has a bounty hunter target
			 */
			if (target.getAttribute(BountyHunterConstants.BOUNTY_TARGET, 0) != 0) {
				continue;
			}

			/*
			 * Prevent ourself and people connected to our identity to be set as
			 * a target
			 */
			if (target.getIdentity().equalsIgnoreCase(player.getIdentity()) || target.getMacAddress().equalsIgnoreCase(player.getMacAddress())) {
				if (Server.isLive()) {
					continue;
				}
			}
			
			if (System.currentTimeMillis() - target.getAttribute("login_delay", 0L) < BountyHunterConstants.LOGIN_DELAY_WAIT) {
				continue;
			}
			
			if (System.currentTimeMillis() - target.getAttribute("last_bounty_kill", 0L) < BountyHunterConstants.LAST_BOUNTY_KILL_DELAY) {
				continue;
			}
			player.setAttribute(BountyHunterConstants.BOUNTY_TARGET, target.getIndex());
			player.write(new CreatePlayerHint(10, target.getIndex()));
			determineWealth(player);

			target.setAttribute(BountyHunterConstants.BOUNTY_TARGET, player.getIndex());
			target.write(new CreatePlayerHint(10, player.getIndex()));
			determineWealth(target);

			return;
		}
	}

	/**
	 * Writes the bounty hunter strings to the interface
	 * 
	 * @param player
	 *            The {@link Player} to write the strings for
	 */
	public static void writeBountyStrings(Player player) {
		player.write(new SendString(Integer.toString(player.getAttribute(BountyHunterConstants.ROGUE_CURRENT, 0)), BountyHunterConstants.ROGUE_CURRENT_STRING_ID));
		player.write(new SendString(Integer.toString(player.getAttribute(BountyHunterConstants.ROGUE_RECORD, 0)), BountyHunterConstants.ROGUE_RECORD_STRING_ID));
		player.write(new SendString(Integer.toString(player.getAttribute(BountyHunterConstants.HUNTER_CURRENT, 0)), BountyHunterConstants.HUNTER_CURRENT_STRING_ID));
		player.write(new SendString(Integer.toString(player.getAttribute(BountyHunterConstants.HUNTER_RECORD, 0)), BountyHunterConstants.HUNTER_RECORD_STRING_ID));
	}

	/**
	 * Determines how much the player is carrying
	 * 
	 * @param player
	 *            The {@link Player} to determine the wealth for
	 */
	public static void determineWealth(Player player) {
		World.getWorld();
		Player target = World.getWorld().getPlayers().get(player.getAttribute(BountyHunterConstants.BOUNTY_TARGET, 0));

		if (target == null) {
			return;
		}

		/*
		 * Disable all of the active configs
		 */
		for (int i = 0; i < 6; i++) {
			target.write(new SendConfig(BountyHunterConstants.V_LOW_WEALTH_CONFIG + i, 0));
		}

		/*
		 * Determine our carried wealth
		 */
		long carried_wealth = player.getItems().getWealth();

		int configId = -1;

		/*
		 * Determine which config to send based on the current bounty wealth
		 */
		if (carried_wealth > BountyHunterConstants.V_HIGH_WEALTH) {
			configId = BountyHunterConstants.V_HIGH_WEALTH_CONFIG;
			player.setAttribute("bounty_wealth", "Wealth: V. High");
		} else if ((carried_wealth >= BountyHunterConstants.HIGH_WEALTH) && (carried_wealth < BountyHunterConstants.V_HIGH_WEALTH)) {
			configId = BountyHunterConstants.HIGH_WEALTH_CONFIG;
			player.setAttribute("bounty_wealth", "Wealth: High");
		} else if ((carried_wealth >= BountyHunterConstants.MEDIUM_WEALTH) && (carried_wealth < BountyHunterConstants.HIGH_WEALTH)) {
			configId = BountyHunterConstants.MEDIUM_WEALTH_CONFIG;
			player.setAttribute("bounty_wealth", "Wealth: Medium");
		} else if ((carried_wealth >= BountyHunterConstants.LOW_WEALTH) && (carried_wealth < BountyHunterConstants.MEDIUM_WEALTH)) {
			configId = BountyHunterConstants.LOW_WEALTH_CONFIG;
			player.setAttribute("bounty_wealth", "Wealth: Low");
		} else {
			configId = BountyHunterConstants.V_LOW_WEALTH_CONFIG;
			player.setAttribute("bounty_wealth", "Wealth: V. Low");
		}

		target.write(new SendConfig(configId, 1));
		target.write(new SendConfig(BountyHunterConstants.DEFAULT_CONFIG, 1));
	}

	/**
	 * Handles what happens on death when a player kills you
	 * 
	 * @param player
	 *            The {@link Player} who has died
	 * @param killer
	 *            The {@link Player} who killed this player
	 */
	public static void handleOnDeath(Player player, Player killer) {

		/*
		 * Reset the killers target
		 */
		killer.setAttribute(BountyHunterConstants.BOUNTY_TARGET, 0);
		killer.write(new CreatePlayerHint(10, -1));
		killer.setAttribute("last_bounty_kill", System.currentTimeMillis());
		for (int i = 0; i < 6; i++) {
			killer.write(new SendConfig(BountyHunterConstants.V_LOW_WEALTH_CONFIG + i, 0));
		}
		killer.write(new SendConfig(BountyHunterConstants.DEFAULT_CONFIG, 0));

		/*
		 * Reset the player who died
		 */
		player.setAttribute(BountyHunterConstants.BOUNTY_TARGET, 0);
		player.write(new CreatePlayerHint(10, -1));
		player.setAttribute("left_wild_delay", 0);
	}

	/**
	 * Handles the actions taken when you kill a player in bounty hunter
	 * 
	 * @param player
	 *            The {@link Player} that has died
	 * @param killer
	 *            The {@link Player} who has killed this player
	 */
	public static void handleBountyHunterKill(Player player, Player killer) {
		int index = killer.getAttribute(BountyHunterConstants.BOUNTY_TARGET, 0);

		/*
		 * The killer has killed his target
		 */
		if (index == player.getIndex()) {
			int current = killer.getAttribute(BountyHunterConstants.HUNTER_CURRENT, 0);
			int record = killer.getAttribute(BountyHunterConstants.HUNTER_RECORD, 0);

			if (current + 1 > record) {
				killer.setAttribute(BountyHunterConstants.HUNTER_RECORD, current + 1);
			}
			killer.setAttribute(BountyHunterConstants.HUNTER_CURRENT, current + 1);
			BountyTierHandler.upgrade(killer);
			killer.setAttribute("receive_emblem", handleItemGiving(player, killer));
			killer.setTargetPoints(killer.getTargetPoints() + 1);
			killer.setTotalTargetsKilled(killer.getTotalTargetsKilled() + 1);
			killer.write(new SendMessagePacket("@blu@You have killed your target, and received @mag@1@blu@ target point."));
			killer.write(new SendMessagePacket("@blu@You now have @mag@"+killer.getTargetPoints()+ "@blu@ target points, and you have killed @mag@"+killer.getTotalTargetsKilled()+"@blu@ targets."));
		} else {
			int current = killer.getAttribute(BountyHunterConstants.ROGUE_CURRENT, 0);
			killer.setAttribute(BountyHunterConstants.ROGUE_CURRENT, current + 1);
		}
		writeBountyStrings(killer);
	}

	/**
	 * Handles the random chance to give a tier 1 emblem
	 * 
	 * @param player
	 *            The {@link Player} that has died
	 * @param killer
	 *            The {@link Player} that killed the target
	 */
	private static boolean handleItemGiving(Player player, Player killer) {
		long carried_wealth = player.getItems().getWealth();

		int random = 0;
		if (carried_wealth > BountyHunterConstants.V_HIGH_WEALTH) {
			random = 1;
		} else if ((carried_wealth >= BountyHunterConstants.HIGH_WEALTH) && (carried_wealth < BountyHunterConstants.V_HIGH_WEALTH)) {
			random = 2;
		} else if ((carried_wealth >= BountyHunterConstants.MEDIUM_WEALTH) && (carried_wealth < BountyHunterConstants.HIGH_WEALTH)) {
			random = 3;
		} else if ((carried_wealth >= BountyHunterConstants.LOW_WEALTH) && (carried_wealth < BountyHunterConstants.MEDIUM_WEALTH)) {
			random = 4;
		} else {
			random = 5;
		}

		int randomChance = Utility.getRandom(random);
		return randomChance == 1;
	}

	@Override
	public void execute() {
		World.getWorld();
		for (Player player : World.getWorld().getPlayers()) {
			if (player != null) {
				pulse(player);
			}
		}
	}
	
	/**
	 * Calculates the total networth for the emblems in a players inventory.
	 * @return	the total networth of all emblems in a players inventory
	 */
	public int getNetworthForEmblems(Player player) {
		int worth = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			int itemId = player.playerItems[i] - 1;
			Optional<BountyHunterEmblem> containsItem = BountyHunterEmblem.EMBLEMS.stream().filter(emblem ->
				emblem.getItemId() == itemId).findFirst();
			if (containsItem.isPresent()) {
				worth += containsItem.get().getBounties();
			}
		}
		return worth;
	}

}