package com.model.game.character.player.content;

import com.model.game.World;
import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.combat.weapon.AttackStyle;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.ground.GroundItem;
import com.model.game.item.ground.GroundItemHandler;
import com.model.game.item.ground.GroundItemType;
import com.model.net.packet.out.SendSidebarInterfacePacket;
import com.model.net.packet.out.SendWalkableInterfacePacket;

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

		String asdf = player.getDamageMap().getKiller();
		player.debug("killed by: "+asdf);
		Player killer = World.getWorld().getPlayerByName(asdf);

		if (killer == null) {
			player.debug("Killer not found, you can run back for your stuff.");
			killer = player;
			killer.debug("i am the killer: "+killer.getName());
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
		player.write(new SendSidebarInterfacePacket(0, 5855));
		player.getActionSender().sendString("Unarmed", 5857);
		player.write(new SendWalkableInterfacePacket(-1));
	}
}