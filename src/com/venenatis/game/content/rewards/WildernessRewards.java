package com.venenatis.game.content.rewards;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import com.venenatis.game.constants.Constants;
import com.venenatis.game.constants.EquipmentConstants;
import com.venenatis.game.content.bounty.BountyHunter;
import com.venenatis.game.content.quest_tab.QuestTabPageHandler;
import com.venenatis.game.content.quest_tab.QuestTabPages;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;

/**
 * This class represents the wilderness rewards for killing players.
 * Rewards such as blood money points emblems etc...
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van  Elderen</a>
 *
 */
public class WildernessRewards {
	
	/**
	 * This method will reward the killer
	 * 
	 * @param killer
	 *            The player who killed his opponent
	 * @param opponent
	 *            The player who died
	 */
	public static boolean receive_reward(Player killer, Player opponent) {
		
		long wealth = getWealth(opponent);
		int amount = 0;
		killer = World.getWorld().lookupPlayerByName(opponent.getCombatState().getDamageMap().getKiller());
		
		if(wealth > Constants.PK_POINTS_WEALTH && isSameConnection(opponent, killer) && !killer.getRights().isOwner(killer)) {
			if(hasKilledRecently(opponent.getUsername(), killer)) {
				killer.getActionSender().sendMessage("You have already killed "+opponent.getUsername()+" kill 3 more players before receiving rewards again for killing "+opponent.getUsername());
				return false;
			}
			
			if(opponent.getUsername().equalsIgnoreCase("patrick") || opponent.getUsername().equalsIgnoreCase("matthew")) {
				//Owners don't drop items, reward them with a random blood money reward
				killer.getInventory().addOrCreateGroundItem(new Item(13307, Utility.random(5, 25)));
			}
			
			//The amount of blood money we receive per kill
			switch (killer.getRights()) {

			case PLAYER:
				amount = 2;
				break;

			case DONATOR:
				amount = 3;
				break;

			case SUPER_DONATOR:
				amount = 5;
				break;

			case ELITE_DONATOR:
				amount = 7;
				break;

			case EXTREME_DONATOR:
				amount = 10;
				break;

			default:
				break;
			}
			//Apply blood money reward
			Item blood_money_reward = new Item(13307, amount);
			killer.getInventory().addOrCreateGroundItem(blood_money_reward);
			
			//Send killstreak reward
			killer.getKillstreak().reward();
			return true;
		} else {
			killer.getKillstreak().increase();
			if(killer.getCurrentKillStreak() >= 5) {
				World.getWorld().sendWorldMessage("<img=22>[@red@Killstreak@bla@]: @dre@"+killer.getUsername()+"@red@ just "+killMessage[new java.util.Random().nextInt(killMessage.length)]+" @dre@"+opponent.getUsername()+"'s@red@ "+opponent.getCurrentKillStreak()+" killstreak!", false);
			}
			
			//Add identity to anti cheat list
			addKilledEntry(opponent.getIdentity(), killer);
			
			//Apply bounty hunter kill
			BountyHunter.handleBountyHunterKill(opponent, killer);
			
			//Increase death and kill count
			killer.setKillCount(killer.getKillCount() + 1);
			opponent.setDeathCount(opponent.getDeathCount() + 1);
			
			//Renew special attack for the killer
			killer.setSpecialAmount(100);
			killer.getWeaponInterface().sendSpecialBar(killer.getEquipment().get(EquipmentConstants.WEAPON_SLOT));
			killer.getWeaponInterface().refreshSpecialAttack();
			
			//Update information tab
			QuestTabPageHandler.write(killer, QuestTabPages.HOME_PAGE);
			QuestTabPageHandler.write(opponent, QuestTabPages.HOME_PAGE);
		}
		return false;
	}
	
	/**
	 * A set of streak ending messages
	 */
	private static final String[] killMessage = {"wrecked", "destroyed", "ended", "cleared", "ruined"};
	
	/**
	 * Calculates the players current wealth, checks for armour and inventory.
	 * 
	 * @param player
	 *            The player whose current wealth we're checking
	 */
	public static long getWealth(Player player) {
		LinkedList<Item> all = new LinkedList<>();
		
		Item[] inv_items = player.getInventory().toNonNullArray();
		for (Item inventory : inv_items) {
			all.add(inventory);
		}
		
		Item[] equipment_items = player.getEquipment().toNonNullArray();
		for(Item equipment : equipment_items) {
			all.addLast(equipment);
		}

		int finalamount = player.isSkulled() ? 0 : 3;
		if (player.isActivePrayer(Prayers.PROTECT_ITEM))
			finalamount++;
		int amount = finalamount;
		if (amount > 0) {
			all.sort(Collections.reverseOrder((one, two) -> Double.compare(one.getValue(), two.getValue())));
			for (Iterator<Item> it = all.iterator(); it.hasNext();) {
				Item next = it.next();
				if (amount == 0) {
					break;
				}
				if (next.isStackable() && next.getAmount() > 1) {
					next.amount -= finalamount == 0 ? 1 : finalamount;
				} else {
					it.remove();
				}
				amount--;
			}
		}
		long wealth = 0;
		for (Item totalWealth : all) {
			wealth += (totalWealth.getValue() * totalWealth.amount);
		}
		return wealth;
	}
	
	/**
	 * The maximum amount of entries we store
	 */
	public static final int MAXIMUM_ENTRIES = 3;

	/**
	 * After killing three victims we clear the list we can receive rewards
	 * again.
	 * 
	 * @param player
	 *            The player who can receive rewards again for killing the same
	 *            player
	 */
	public static boolean requiresClearing(Player player) {
		return player.lastKilledList.size() >= MAXIMUM_ENTRIES;
	}

	/**
	 * We call this method upon death.
	 * 
	 * @param player
	 *            the player we clear the list of.
	 */
	public static void clearList(Player player) {
		player.lastKilledList.clear();
		player.lastKilledList.trimToSize();
	}

	public static void addKilledEntry(String name, Player player) {
		player.lastKilledList.add(name);
	}

	/**
	 * Have we killed this player already?
	 * 
	 * @param name
	 *            The player we killed.
	 * @param player
	 *            The killer.
	 */
	public static boolean hasKilledRecently(String name, Player player) {
		if (player.lastKilledList.contains(name))
			return true;
		if (requiresClearing(player))
			clearList(player);
		return false;
	}

	/**
	 * Checks if the killer and the opponent are using the same address.
	 * 
	 * @param opponent
	 *            The player that lost the battle
	 * @param killer
	 *            The player who won the battle
	 */
	public static boolean isSameConnection(Player opponent, Player killer) {
		return killer.getHostAddress().equals(opponent.getHostAddress()) || killer.getMacAddress().equals(opponent.getMacAddress()) || killer.getIdentity().equals(opponent.getIdentity());
	}


}
