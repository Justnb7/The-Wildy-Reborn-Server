package com.venenatis.game.content.mystery_box;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import com.venenatis.game.content.mystery_box.MysteryBoxInformation.MysteryBoxItem;
import com.venenatis.game.content.mystery_box.MysteryBoxInformation.MysteryBoxTable;
import com.venenatis.game.content.mystery_box.MysteryBoxInformation.Rarities;
import com.venenatis.game.model.Item;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.util.Utility;

public class MysteryBox {

	private final Player player;

	public MysteryBox(final Player player) {
		this.player = player;
	}

	private final Random random = new Random();

	/**
	 * Represents the mystery box drop table
	 */
	private MysteryBoxTable table = null;

	/**
	 * Executed when opening a mystery box
	 * 
	 * @param item
	 * @param slot
	 */
	public void open(final Item item, final int slot) {
		table = MysteryBoxTable.get(item.getId());
		if (Objects.isNull(table)) {
			return;
		}
		player.getInventory().removeFromSlot(slot, item.id, item.amount);
		player.message("You open the " + item.getDefinition().getName() + "....");
		final int coinCount = (1 + random.nextInt(149999));
		player.getInventory().add(new Item(995, coinCount));
		player.message("And manage to find " + Utility.formatNumbers(coinCount) + " coins");
		final Item mysteryBoxItem = getReward().get(0);
		if (mysteryBoxItem != null) {
			player.getInventory().replace(new Item(6199), mysteryBoxItem);
			player.message("And it seems that you found another item in your mystery box...");
		}
	}

	public List<Item> getReward() {
		List<Item> items = new ArrayList<>();

		final MysteryBoxTable mTable = MysteryBoxTable.get(6199);

		final int itemCapacity = mTable.getMysteryBoxItems().length;

		List<MysteryBoxItem> commonItems = new ArrayList<>();
		List<MysteryBoxItem> uncommonItems = new ArrayList<>();
		List<MysteryBoxItem> rareItems = new ArrayList<>();
		List<MysteryBoxItem> veryRareItems = new ArrayList<>();
		List<MysteryBoxItem> superRareItems = new ArrayList<>();

		for (int index = 0; index < itemCapacity; index++) {
			final MysteryBoxItem mysteryBoxItem = mTable.getMysteryBoxItems()[index];
			if (mysteryBoxItem.getRarity() == Rarities.COMMON) {
				commonItems.add(mysteryBoxItem);
			} else if (mysteryBoxItem.getRarity() == Rarities.UNCOMMON) {
				uncommonItems.add(mysteryBoxItem);
			} else if (mysteryBoxItem.getRarity() == Rarities.RARE) {
				rareItems.add(mysteryBoxItem);
			} else if (mysteryBoxItem.getRarity() == Rarities.VERY_RARE) {
				veryRareItems.add(mysteryBoxItem);
			} else if (mysteryBoxItem.getRarity() == Rarities.SUPER_RARE) {
				superRareItems.add(mysteryBoxItem);
			}
		}
		final double roll = (random.nextDouble() * 100);
		final Rarities rarityTable = getTable(roll);
		if (rarityTable != null) {
			int id = -1;
			int count = 0;
			int randomCount = 0;
			if (rarityTable == Rarities.COMMON && !commonItems.isEmpty()) {
				final MysteryBoxItem commonItem = commonItems.get(random.nextInt(commonItems.size() - 1));
				id = commonItem.getId();
				count = commonItem.getMinimumCount();
				randomCount = commonItem.getRandomCount() > 0 ? random.nextInt(commonItem.getRandomCount()) : 0;
			} else if (rarityTable == Rarities.COMMON && !uncommonItems.isEmpty()) {
				final MysteryBoxItem uncommonItem = uncommonItems.get(random.nextInt(uncommonItems.size() - 1));
				id = uncommonItem.getId();
				count = uncommonItem.getMinimumCount();
				randomCount = uncommonItem.getRandomCount() > 0 ? random.nextInt(uncommonItem.getRandomCount()) : 0;
			} else if (rarityTable == Rarities.RARE && !rareItems.isEmpty()) {
				final MysteryBoxItem rareItem = rareItems.get(random.nextInt(rareItems.size() - 1));
				id = rareItem.getId();
				count = rareItem.getMinimumCount();
				randomCount = rareItem.getRandomCount() > 0 ? random.nextInt(rareItem.getRandomCount()) : 0;
			} else if (rarityTable == Rarities.VERY_RARE && !veryRareItems.isEmpty()) {
				final MysteryBoxItem veryRareItem = veryRareItems.get(random.nextInt(veryRareItems.size()));
				id = veryRareItem.getId();
				count = veryRareItem.getMinimumCount();
				randomCount = veryRareItem.getRandomCount() > 0 ? random.nextInt(veryRareItem.getRandomCount()) : 0;
			} else if (rarityTable == Rarities.SUPER_RARE && !superRareItems.isEmpty()) {
				final MysteryBoxItem superRareItem = superRareItems.get(random.nextInt(superRareItems.size()));
				id = superRareItem.getId();
				count = superRareItem.getMinimumCount();
				randomCount = superRareItem.getRandomCount() > 0 ? random.nextInt(superRareItem.getRandomCount()) : 0;
			}
			if (id > 0) {
				items.add(new Item(id, count + randomCount));
			}
		}

		return items;
	}

	//
	private Rarities getTable(double roll) {
		Rarities rarity = null;
		if (roll <= 50) {
			rarity = Rarities.COMMON;
		}
		if (roll <= 25) {
			rarity = Rarities.UNCOMMON;
		}
		if (roll <= 15) {
			rarity = Rarities.RARE;
		}
		if (roll <= 5) {
			rarity = Rarities.VERY_RARE;
		}
		if (roll <= .1) {
			rarity = Rarities.SUPER_RARE;
		}
		return rarity;
	}
}