package com.venenatis.game.model.combat.impl;

import java.util.PriorityQueue;
import java.util.Queue;

import com.venenatis.game.content.BrokenItem;
import com.venenatis.game.content.achievements.AchievementHandler;
import com.venenatis.game.content.achievements.AchievementList;
import com.venenatis.game.content.bounty.BountyHunter;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.Item.ItemComparator;
import com.venenatis.game.model.combat.PrayerHandler;
import com.venenatis.game.model.entity.Entity;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.model.entity.player.account.Account;
import com.venenatis.game.util.Utility;
import com.venenatis.game.world.World;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.ground_item.GroundItemHandler;

/**
 * Class created to handle what happens to a player upon death.
 * 
 * @author Daniel && Patrick van Elderen
 *
 */
public class PlayerDrops {

	/**
	 * Messages of killing a player.
	 */
	public static final String[] DEATH_MESSAGES = { "You have defeated -victim-.", "With a crushing blow, you defeat -victim-.", "It's a humiliating defeat for -victim-.", "-victim- didn't stand a chance against you.", "You have defeated -victim-.", "It's all over for -victim-.", "-victim- regrets the day they met you in combat.", "-victim- falls before your might.", "Can anyone defeat you? Certainly not -victim-.", "You were clearly a better fighter than -victim-." };

	/**
	 * Handles special statements that happen when killing a player.
	 * 
	 * @param victim
	 * @param killer
	 */
	public static void handleSpecial(Player victim, Player killer) {
		if (victim.equals(killer)) {
			return;
		}
		
		int blood_money = 5;

		killer.getActionSender().sendMessage(Utility.randomElement(DEATH_MESSAGES).replaceAll("-victim-", Utility.formatName(victim.getUsername())));

		if (PlayerKilling.hostOnList(killer, victim.getHostAddress())) {
			killer.getActionSender().sendMessage("<col=ff0000>You have already killed " + Utility.formatName(victim.getUsername()) + " recently and were not rewarded.");
			return;
		}

		PlayerKilling.addHostToList(killer, victim.getHostAddress());
		
		AchievementHandler.activate(killer, AchievementList.FIRST_KILL, 1);
		AchievementHandler.activate(killer, AchievementList.LEARNING_CURVE, 1);
		AchievementHandler.activate(killer, AchievementList.MASTER, 1);
		
		BountyHunter.handleBountyHunterKill(victim, killer);
		
		killer.setCurrentKillStreak(killer.getCurrentKillStreak() + 1);
		killer.setWildernessStreak(killer.getWildernessKillStreak() + 1);
		
		if (killer.getCurrentKillStreak() > 2) {
			if (killer.getWildernessKillStreak() == killer.getCurrentKillStreak()) {
				World.getWorld().sendWorldMessage("<img=12>[@red@Server@bla@]: @dre@" + killer.getUsername() + "@red@ has killed @dre@" + victim.getUsername() + "@red@ and is on a @dre@" + killer.getCurrentKillStreak() + "@red@ Wilderness killstreak!", false);
			} else {
				World.getWorld().sendWorldMessage("<img=12>[@red@Server@bla@]: @dre@" + killer.getUsername() + "@red@ has killed @dre@" + victim.getUsername() + "@red@ and is on a @dre@" + killer.getCurrentKillStreak() + "@red@ killstreak! Wildy Streak: @dre@" + killer.getWildernessKillStreak(), false);
			}
			if (killer.getCurrentKillStreak() < 6) {
				killer.getActionSender().sendMessage("@bla@You gain @red@" + killer.getCurrentKillStreak() + " @bla@extra PK Points because of your @red@" + killer.getCurrentKillStreak() + " @bla@killstreak.");
				blood_money += killer.getCurrentKillStreak();
			} else {
				killer.getActionSender().sendMessage("@bla@You gain @red@ 1 @bla@extra PK Points because of your @red@" + killer.getCurrentKillStreak() + " @bla@killstreak.");
				blood_money += 1;
			}
		}
		
		if (killer.getHighestKillStreak() == 30) {
			AchievementHandler.activate(killer, AchievementList.SETTING_THE_RECORD, 1);
		}
		
		//Apply member bonus
		if(killer.getTotalAmountDonated() >= 10) {
			blood_money += Utility.isWeekend() ? 10 : 20;
		} else if(killer.getTotalAmountDonated() >= 30) {
			blood_money += Utility.isWeekend() ? 15 : 30;
		} else if(killer.getTotalAmountDonated() >= 100) {
			blood_money += Utility.isWeekend() ? 20 : 40;
		}
		
		if(killer.getAccount().equals(Account.IRON_MAN_TYPE)) {
			blood_money += Utility.isWeekend() ? 1 : 2;
		}
		
		//Apply the pkp reward
		//killer.setPkPoints(killer.getPkPoints() + blood_money);
		killer.getInventory().addOrCreateGroundItem(killer, new Item(13307, blood_money));
		killer.getActionSender().sendMessage("You have received @red@"+blood_money+"@bla@ blood money for that kill.");
		//killer.getActionSender().sendMessage("You now have @blu@" + killer.getPkPoints() + " @red@PK Points@bla@. (@blu@+" + blood_money + "@bla@)");
		
		if (killer.getCurrentKillStreak() > killer.getHighestKillStreak()) {
			killer.getActionSender().sendMessage("Congratulations, your highest kill streak has increased!");
			killer.setHighestKillStreak(killer.getCurrentKillStreak());
		}
		
		if (victim.getCurrentKillStreak() >= 30) {
			AchievementHandler.activate(killer, AchievementList.RECORD_BREAKER, 1);
		}
		
		//Increase death and kill count
		killer.setKillCount(killer.getKillCount() + 1);
		victim.setDeathCount(victim.getDeathCount() + 1);
		
		//Renew special attack for the killer
		killer.setSpecialAmount(100);
		killer.getWeaponInterface().restoreWeaponAttributes();
	}

	/**
	 * Drops all the player's items on the floor.
	 * 
	 * @param victim
	 *            The player losing their items
	 */
	public static void dropItems(Player victim, Entity killer) { // note killer is always a player
		
		Entity finalBlow = victim.getAttribute("killer", null);
		boolean npcDeath = finalBlow != null && finalBlow.isNPC();

		// A null killer means players did no damage. Self death or Npc death
		if (killer == null) {
			killer = victim;
		}

		final Item[] keep = victim.isSkulled() ? new Item[PrayerHandler.isActivated(victim, PrayerHandler.PROTECT_ITEM) ? 1 : 0] : new Item[PrayerHandler.isActivated(victim, PrayerHandler.PROTECT_ITEM) ? 4 : 3];

		final Queue<Item> items = new PriorityQueue<Item>(ItemComparator.SHOP_VALUE_COMPARATOR);

		for (final Item item : victim.getInventory().toNonNullArray()) {
			if (item != null) {
				items.add(item.copy());
			}
		}

		for (final Item item : victim.getEquipment().toNonNullArray()) {
			if (item != null) {
				items.add(item.copy());
			}
		}

		final Queue<Item> temp = new PriorityQueue<>(items);

		for (int index = 0, taken = 0; index < keep.length; index++) {
			keep[index] = temp.poll();
			items.remove(keep[index]);

			if (keep[index] != null) {
				if (keep[index].getAmount() == keep.length - taken) {
					break;
				}

				if (keep[index].getAmount() > keep.length - taken) {
					items.add(new Item(keep[index].getId(), keep[index].getAmount() - (keep.length - taken)));
					keep[index].setAmount(keep.length - taken);
					break;
				}

				taken += keep[index].getAmount();
			}
		}

		victim.getInventory().clear(false);
		victim.getEquipment().clear(true);
		
		if (victim.getInventory().add(keep) == 0) {
			victim.getInventory().refresh();
		}

		while (!items.isEmpty()) {
			final Item item = items.poll();

			if (item == null) {
				continue;
			}
			
			int id = item.getId();
			BrokenItem brokenItem = BrokenItem.get(id);
			if(brokenItem != null) {
				id = brokenItem.getBrokenItem();
				victim.getInventory().add(new Item(id, item.getAmount()));
				// only give back if its a broken item
			}
			
			if(!item.isTradeable() || item.isDestroyable()) {
				continue;
			}
			
			// If killer is null, drop is for victim
			if (killer == null) {
				GroundItemHandler.createGroundItem(new GroundItem(item, victim.getLocation(), victim));
			// If killer is an NPC, drop is for victim	
			} else if (npcDeath) {
				GroundItemHandler.createGroundItem(new GroundItem(item, victim.getLocation(), victim));
			// Drop all items for killer
			} else if (killer != null) {
				GroundItemHandler.createGroundItem(new GroundItem(item, victim.getLocation(), killer.asPlayer()));
			}
		}

		if (killer != null && killer.isPlayer()) {
			GroundItemHandler.createGroundItem(new GroundItem(new Item(526), victim.getLocation().clone(), killer.asPlayer()));
			handleSpecial(victim, killer.asPlayer());
		} else {
			// self or npc death
			GroundItemHandler.createGroundItem(new GroundItem(new Item(526), victim.getLocation().clone(), victim));
		}
	}

}