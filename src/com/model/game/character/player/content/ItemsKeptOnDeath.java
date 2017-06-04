package com.model.game.character.player.content;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Queue;

import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.player.Player;
import com.model.game.item.Item;
import com.model.game.item.Item.ItemComparator;

/**
 * The class which represents functionality for the items kept on death container.
 * 
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van Elderen</a>
 * @author Battle-OS team members for certain parts of the code.
 */
public class ItemsKeptOnDeath {


	/**
	 * Opens the items kept on death interface.
	 * 
	 * @param player
	 *            The player viewing the items kept on death interface.
	 */
	public static void open(Player player) {
		int kept = 0;

		// Are we skulled if we are keep only 1 item when using the protect item
		// otherwise keep 0
		if (player.isSkulled()) {
			kept = player.isActivePrayer(Prayers.PROTECT_ITEM) ? 1 : 0;
		} else {
			// We're not skulled we keep 3 items unless we're using protect item
			// then we keep 4
			kept = player.isActivePrayer(Prayers.PROTECT_ITEM) ? 4 : 3;
		}

		final Item[] keep = new Item[kept];

		final Queue<Item> items = new PriorityQueue<Item>(ItemComparator.SHOP_VALUE_COMPARATOR);

		//Loops through our inventory
		for (final Item item : player.getInventory().toNonNullArray()) {
			if (item != null) {
				items.add(item.copy());
			}
		}

		//Loops through our equipment
		for (final Item item : player.getEquipment().toNonNullArray()) {
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

		player.getActionSender().sendString("~ " + kept + " ~", 17112);

		//Sends the strings to the interface
		switch (kept) {
		case 0:
		default:
			player.getActionSender().sendString("You're marked with a \\n<col=ff0000>skull. </col>This reduces the \\nitems you keep from \\nthree to zero!", 17110);
			break;
		case 1:
			player.getActionSender().sendString("You're marked with a \\n<col=ff0000>skull. </col>This reduces the \\nitems you keep from \\nthree to zero! \\nHowever, you also have the \\n<col=ff0000>Protect </col>Items prayer \\nactive, which saves you \\one extra item!", 17110);
			break;
		case 3:
			player.getActionSender().sendString("You have no factors affecting \\nthe items you keep.", 17110);
			break;
		case 4:
			player.getActionSender().sendString("You have the <col=ff0000>Protect Item</col> \\nprayer active, which saves \\nyou one extra item!", 17110);
			break;
		}

		final Item[] dropped = items.toArray(new Item[0]);

		BigInteger risked = BigInteger.ZERO;
		while (items.peek() != null) {
			final Item dropping = items.poll();

			if (dropping == null) {
				continue;
			}

			risked = risked.add(new BigInteger(String.valueOf(dropping.getValue())).multiply(new BigInteger(String.valueOf(dropping.getAmount()))));
		}

		//Sent the risked wealth string
		if (risked.equals(BigInteger.ZERO)) {
			player.getActionSender().sendString("Value of lost items:", 17115);
			player.getActionSender().sendString("0 gp", 17116);
		} else {
			player.getActionSender().sendString("Value of lost items:", 17115);
			player.getActionSender().sendString(""+ NumberFormat.getNumberInstance(Locale.US).format(risked) + "gp", 17116);
		}

		player.getActionSender().sendItemOnInterface(17113, keep);
		player.getActionSender().sendItemOnInterface(17114, dropped);
		player.getActionSender().sendInterface(17100);
	}

	
}