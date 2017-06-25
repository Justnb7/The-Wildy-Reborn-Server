package com.venenatis.game.content;

import com.venenatis.game.model.Item;
import com.venenatis.game.model.combat.PrayerHandler.Prayers;
import com.venenatis.game.model.combat.data.AttackStyle;
import com.venenatis.game.model.entity.player.Player;
import com.venenatis.game.world.World;
import com.venenatis.game.world.ground_item.GroundItem;
import com.venenatis.game.world.ground_item.GroundItemHandler;
import com.venenatis.game.world.ground_item.GroundItemType;

/**
 * Handles dropping items on death
 * 
 * @author Arithium
 * 
 */
public class DeathDropHandler {

	/**
	 * Handles dropping the items on death
	 * 
	 * @param player
	 */
	public static void handleDeathDrop(Player player) {

		String k = player.getDamageMap().getKiller();
		Player killer = World.getWorld().lookupPlayerByName(k);

		if (killer == null) {
			killer = player;
		}

		Item[] keep = new Item[3 + (player.isActivePrayer(Prayers.PROTECT_ITEM) ? 1 : 0)];

		Item[] drop = new Item[(28 - player.getInventory().getFreeSlots()) + (14 - player.getEquipment().getFreeSlots())];

		eqp_loop: for (Item equip : player.getEquipment().toArray()) {
			if (equip != null) {
				int itemValue = equip.getValue();

				int keepValue = 0;
				for (int i = 0; i < keep.length; i++) {
					if (keep[i] != null) {
						keepValue = keep[i].getValue();
					}
					if (itemValue > keepValue) {
						keep[i] = equip;
						keepValue = 0;
						continue eqp_loop;
					}
					keepValue = 0;
				}
				for (int x = 0; x < drop.length; x++) {
					if (drop[x] == null) {
						drop[x] = equip;
						continue eqp_loop;
					}
				}
			}
		}
		inv_loop: for (Item equip : player.getInventory().toArray()) {
			if (equip != null) {
				int itemValue = equip.getValue();
				int keepValue = 0;
				for (int i = 0; i < keep.length; i++) {
					if (keep[i] != null) {
						keepValue = keep[i].getValue();
					}
					if (itemValue > keepValue) {
						keep[i] = equip;
						keepValue = 0;
						continue inv_loop;
					}
					keepValue = 0;
				}
				for (int x = 0; x < drop.length; x++) {
					if (drop[x] == null) {
						drop[x] = equip;
						continue inv_loop;
					}
				}
			}
		}

		player.getEquipment().clear(true);
		player.getInventory().clear(true);

		for (Item item : drop) {
			if (item != null) {
				GroundItem x = new GroundItem(item, player.getLocation(), killer);
				x.setGroundItemType(GroundItemType.PRIVATE);
				GroundItemHandler.register(x);
				killer.getActionSender().sendGroundItem(x);
			}
		}

		for (Item item : keep) {
			if (item != null) {
				player.getInventory().add(item);
			}
		}
		player.setUsingSpecial(false);
		AttackStyle.adjustAttackStyleOnLogin(player);
		player.getActionSender().sendSidebarInterface(0, 5855);
		player.getActionSender().sendString("Unarmed", 5857);
		player.getActionSender().sendWalkableInterface(-1);
	}
}