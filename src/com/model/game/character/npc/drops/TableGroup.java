package com.model.game.character.npc.drops;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Range;

import com.model.game.World;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.utility.Utility;
import com.model.utility.json.definitions.ItemDefinition;

@SuppressWarnings("serial")
public class TableGroup extends ArrayList<Table> {

	/**
	 * The non-playable character that has access to this group of tables
	 */
	private final List<Integer> npcIds;

	/**
	 * Creates a new group of tables
	 * 
	 * @param npcId the npc identification value
	 */
	public TableGroup(List<Integer> npcsIds) {
		this.npcIds = npcsIds;
	}

	/**
	 * Accesses each {@link Table} in this {@link TableGroup} with hopes of retrieving a {@link List} of {@link GameItem} objects.
	 * 
	 * @return
	 */
	public List<Item> access(Player player, double modifier, int repeats) {
		int rights = player.getRights().getValue() - 1;
		List<Item> items = new ArrayList<>();
		for (Table table : this) {
			TablePolicy policy = table.getPolicy();
			if (policy.equals(TablePolicy.CONSTANT)) {
				for (Drop drop : table) {
					int minimumAmount = drop.getMinimumAmount();

					items.add(new Item(drop.getItemId(), minimumAmount + Utility.random(drop.getMaximumAmount() - minimumAmount)));
				}
			} else {
				for (int i = 0; i < repeats; i++) {
					double chance = (1.0 / (double) (table.getAccessibility() * modifier)) * 100D;

					double roll = Utility.preciseRandom(Range.between(0.0, 100.0));

					if (chance > 100.0) {
						chance = 100.0;
					}
					if (roll <= chance) {
						Drop drop = table.fetchRandom();
						int minimumAmount = drop.getMinimumAmount();
						Item item = new Item(drop.getItemId(), minimumAmount + Utility.random(drop.getMaximumAmount() - minimumAmount));

						items.add(item);
						if (chance <= 1.5) {
							if (policy.equals(TablePolicy.VERY_RARE) || policy.equals(TablePolicy.RARE)) {
								if (Item.getName(item.getId()).toLowerCase().contains("cowhide")
										|| Item.getName(item.getId()).toLowerCase().contains("feather")
										|| Item.getName(item.getId()).toLowerCase().contains("arrow")
										|| Item.getName(item.getId()).toLowerCase().contains("sq shield")
										|| Item.getName(item.getId()).toLowerCase().contains("rune warhammer")
										|| Item.getName(item.getId()).toLowerCase().contains("rune battleaxe")
										|| Item.getName(item.getId()).toLowerCase().contains("casket")
										|| Item.getName(item.getId()).toLowerCase().contains("silver ore")
										|| Item.getName(item.getId()).toLowerCase().contains("rune spear")
										|| item.getId() >= 554 && item.getId() <= 566) {
									
								} else {
									World.getWorld().sendWorldMessage("<col=FF0000>[Lootations] @cr19@ </col><col=255><img=" + rights + ">" + Utility.capitalize(player.getName()) + "</col> received <col=255>" + item.getAmount() + "</col>x <col=255>" + ItemDefinition.forId(item.getId()).getName() + "</col>.", false);
								}
							}
						}
					}
				}
			}
		}
		return items;
	}

	/**
	 * The non-playable character identification values that have access to this group of tables.
	 * 
	 * @return the non-playable character id values
	 */
	public List<Integer> getNpcIds() {
		return npcIds;
	}
}