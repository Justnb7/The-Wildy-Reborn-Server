package com.model.game.character.player.content;

import com.model.game.character.combat.PrayerHandler.Prayers;
import com.model.game.character.player.Player;
import com.model.game.character.player.packets.out.SendInterfacePacket;
import com.model.utility.json.definitions.ItemDefinition;

/**
 * @author Someonez
 * @author NewKid
 */
public class ItemOnDeath {

	public static void activateItemsOnDeath(Player player) {
			StartBestItemScan(player);
			player.EquipStatus = 0;

			for (int k = 0; k < 4; k++)
				sendFrame34a(player, 10494, -1, k, 1);
			for (int k = 0; k < 39; k++)
				sendFrame34a(player, 10600, -1, k, 1);

			if (player.WillKeepItem1 > 0)
				sendFrame34a(player, 10494, player.WillKeepItem1, 0, player.WillKeepAmt1);
			if (player.WillKeepItem2 > 0)
				sendFrame34a(player, 10494, player.WillKeepItem2, 1, player.WillKeepAmt2);
			if (player.WillKeepItem3 > 0)
				sendFrame34a(player, 10494, player.WillKeepItem3, 2, player.WillKeepAmt3);
			if (player.WillKeepItem4 > 0 && player.isActivePrayer(Prayers.PROTECT_ITEM))
				sendFrame34a(player, 10494, player.WillKeepItem4, 3, 1);

			for (int ITEM = 0; ITEM < 28; ITEM++) {
				if (player.playerInventory[ITEM] - 1 > 0 && !(player.playerInventory[ITEM] - 1 == player.WillKeepItem1 && ITEM == player.WillKeepItem1Slot)
						&& !(player.playerInventory[ITEM] - 1 == player.WillKeepItem2 && ITEM == player.WillKeepItem2Slot) && !(player.playerInventory[ITEM] - 1 == player.WillKeepItem3 && ITEM == player.WillKeepItem3Slot)
						&& !(player.playerInventory[ITEM] - 1 == player.WillKeepItem4 && ITEM == player.WillKeepItem4Slot)) {
					sendFrame34a(player, 10600, player.playerInventory[ITEM] - 1, player.EquipStatus, player.itemAmount[ITEM]);
					player.EquipStatus += 1;
				} else if (player.playerInventory[ITEM] - 1 > 0 && (player.playerInventory[ITEM] - 1 == player.WillKeepItem1 && ITEM == player.WillKeepItem1Slot) && player.itemAmount[ITEM] > player.WillKeepAmt1) {
					sendFrame34a(player, 10600, player.playerInventory[ITEM] - 1, player.EquipStatus, player.itemAmount[ITEM] - player.WillKeepAmt1);
					player.EquipStatus += 1;
				} else if (player.playerInventory[ITEM] - 1 > 0 && (player.playerInventory[ITEM] - 1 == player.WillKeepItem2 && ITEM == player.WillKeepItem2Slot) && player.itemAmount[ITEM] > player.WillKeepAmt2) {
					sendFrame34a(player, 10600, player.playerInventory[ITEM] - 1, player.EquipStatus, player.itemAmount[ITEM] - player.WillKeepAmt2);
					player.EquipStatus += 1;
				} else if (player.playerInventory[ITEM] - 1 > 0 && (player.playerInventory[ITEM] - 1 == player.WillKeepItem3 && ITEM == player.WillKeepItem3Slot) && player.itemAmount[ITEM] > player.WillKeepAmt3) {
					sendFrame34a(player, 10600, player.playerInventory[ITEM] - 1, player.EquipStatus, player.itemAmount[ITEM] - player.WillKeepAmt3);
					player.EquipStatus += 1;
				} else if (player.playerInventory[ITEM] - 1 > 0 && (player.playerInventory[ITEM] - 1 == player.WillKeepItem4 && ITEM == player.WillKeepItem4Slot) && player.itemAmount[ITEM] > 1) {
					sendFrame34a(player, 10600, player.playerInventory[ITEM] - 1, player.EquipStatus, player.itemAmount[ITEM] - 1);
					player.EquipStatus += 1;
				}
			}
			for (int EQUIP = 0; EQUIP < 14; EQUIP++) {
				if (player.playerEquipment[EQUIP] > 0 && !(player.playerEquipment[EQUIP] == player.WillKeepItem1 && EQUIP + 28 == player.WillKeepItem1Slot)
						&& !(player.playerEquipment[EQUIP] == player.WillKeepItem2 && EQUIP + 28 == player.WillKeepItem2Slot) && !(player.playerEquipment[EQUIP] == player.WillKeepItem3 && EQUIP + 28 == player.WillKeepItem3Slot)
						&& !(player.playerEquipment[EQUIP] == player.WillKeepItem4 && EQUIP + 28 == player.WillKeepItem4Slot)) {
					sendFrame34a(player, 10600, player.playerEquipment[EQUIP], player.EquipStatus, player.playerEquipmentN[EQUIP]);
					player.EquipStatus += 1;
				} else if (player.playerEquipment[EQUIP] > 0 && (player.playerEquipment[EQUIP] == player.WillKeepItem1 && EQUIP + 28 == player.WillKeepItem1Slot) && player.playerEquipmentN[EQUIP] > 1
						&& player.playerEquipmentN[EQUIP] - player.WillKeepAmt1 > 0) {
					sendFrame34a(player, 10600, player.playerEquipment[EQUIP], player.EquipStatus, player.playerEquipmentN[EQUIP] - player.WillKeepAmt1);
					player.EquipStatus += 1;
				} else if (player.playerEquipment[EQUIP] > 0 && (player.playerEquipment[EQUIP] == player.WillKeepItem2 && EQUIP + 28 == player.WillKeepItem2Slot) && player.playerEquipmentN[EQUIP] > 1
						&& player.playerEquipmentN[EQUIP] - player.WillKeepAmt2 > 0) {
					sendFrame34a(player, 10600, player.playerEquipment[EQUIP], player.EquipStatus, player.playerEquipmentN[EQUIP] - player.WillKeepAmt2);
					player.EquipStatus += 1;
				} else if (player.playerEquipment[EQUIP] > 0 && (player.playerEquipment[EQUIP] == player.WillKeepItem3 && EQUIP + 28 == player.WillKeepItem3Slot) && player.playerEquipmentN[EQUIP] > 1
						&& player.playerEquipmentN[EQUIP] - player.WillKeepAmt3 > 0) {
					sendFrame34a(player, 10600, player.playerEquipment[EQUIP], player.EquipStatus, player.playerEquipmentN[EQUIP] - player.WillKeepAmt3);
					player.EquipStatus += 1;
				} else if (player.playerEquipment[EQUIP] > 0 && (player.playerEquipment[EQUIP] == player.WillKeepItem4 && EQUIP + 28 == player.WillKeepItem4Slot) && player.playerEquipmentN[EQUIP] > 1
						&& player.playerEquipmentN[EQUIP] - 1 > 0) {
					sendFrame34a(player, 10600, player.playerEquipment[EQUIP], player.EquipStatus, player.playerEquipmentN[EQUIP] - 1);
					player.EquipStatus += 1;
				}
			}
			ResetKeepItems(player);
			player.write(new SendInterfacePacket(17100));
	}

	public static void ResetKeepItems(Player player) {
		player.WillKeepAmt1 = -1;
		player.WillKeepItem1 = -1;
		player.WillKeepAmt2 = -1;
		player.WillKeepItem2 = -1;
		player.WillKeepAmt3 = -1;
		player.WillKeepItem3 = -1;
		player.WillKeepAmt4 = -1;
		player.WillKeepItem4 = -1;
	}

	public static void StartBestItemScan(Player player) {
		if (player.isSkulled && !player.isActivePrayer(Prayers.PROTECT_ITEM)) {
			ItemKeptInfo(player, 0);
			return;
		}
		FindItemKeptInfo(player);
		ResetKeepItems(player);
		BestItem1(player);
	}

	public static void FindItemKeptInfo(Player player) {
		if (player.isSkulled && player.isActivePrayer(Prayers.PROTECT_ITEM))
			ItemKeptInfo(player, 1);
		else if (!player.isSkulled && !player.isActivePrayer(Prayers.PROTECT_ITEM))
			ItemKeptInfo(player, 3);
		else if (!player.isSkulled && player.isActivePrayer(Prayers.PROTECT_ITEM))
			ItemKeptInfo(player, 4);
	}

	public static void ItemKeptInfo(Player player, int Lose) {
		for (int i = 17109; i < 17131; i++) {
			player.getActionSender().sendString("", i);
		}
		player.getActionSender().sendString("Items you will keep on death:", 17104);
		player.getActionSender().sendString("Items you will lose on death:", 17105);
		player.getActionSender().sendString("Player Information", 17106);
		player.getActionSender().sendString("Max items kept on death:", 17107);
		player.getActionSender().sendString("~ " + Lose + " ~", 17108);
		player.getActionSender().sendString("The normal amount of", 17111);
		player.getActionSender().sendString("items kept is three.", 17112);
		switch (Lose) {
		case 0:
		default:
			player.getActionSender().sendString("Items you will keep on death:", 17104);
			player.getActionSender().sendString("Items you will lose on death:", 17105);
			player.getActionSender().sendString("You're marked with a", 17111);
			player.getActionSender().sendString("@red@skull. @lre@This reduces the", 17112);
			player.getActionSender().sendString("items you keep from", 17113);
			player.getActionSender().sendString("three to zero!", 17114);
			break;
		case 1:
			player.getActionSender().sendString("Items you will keep on death:", 17104);
			player.getActionSender().sendString("Items you will lose on death:", 17105);
			player.getActionSender().sendString("You're marked with a", 17111);
			player.getActionSender().sendString("@red@skull. @lre@This reduces the", 17112);
			player.getActionSender().sendString("items you keep from", 17113);
			player.getActionSender().sendString("three to zero!", 17114);
			player.getActionSender().sendString("However, you also have", 17115);
			player.getActionSender().sendString("the @red@Protect @lre@Items prayer", 17116);
			player.getActionSender().sendString("active, which saves you", 17117);
			player.getActionSender().sendString("one extra item!", 17118);
			break;
		case 3:
			player.getActionSender().sendString("Items you will keep on death(if not skulled):", 17104);
			player.getActionSender().sendString("Items you will lose on death(if not skulled):", 17105);
			player.getActionSender().sendString("You have no factors", 17111);
			player.getActionSender().sendString("affecting the items you", 17112);
			player.getActionSender().sendString("keep.", 17113);
			break;
		case 4:
			player.getActionSender().sendString("Items you will keep on death(if not skulled):", 17104);
			player.getActionSender().sendString("Items you will lose on death(if not skulled):", 17105);
			player.getActionSender().sendString("You have the @red@Protect", 17111);
			player.getActionSender().sendString("@red@Item @lre@prayer active,", 17112);
			player.getActionSender().sendString("which saves you one", 17113);
			player.getActionSender().sendString("extra item!", 17114);
			break;
		}
	}

	private static int deathValue(int itemId) {
		return ItemDefinition.forId(itemId).getGeneralPrice();
	}
	public static void BestItem1(Player player) {
		int BestValue = 0;
		int NextValue;
		int ItemsContained = 0;
		player.WillKeepItem1 = 0;
		player.WillKeepItem1Slot = 0;
		for (int ITEM = 0; ITEM < 28; ITEM++) {
			if (player.playerInventory[ITEM] > 0) {
				ItemsContained += 1;
				NextValue = (int) Math.floor(deathValue(player.playerInventory[ITEM] - 1));
				if (NextValue > BestValue) {
					BestValue = NextValue;
					player.WillKeepItem1 = player.playerInventory[ITEM] - 1;
					player.WillKeepItem1Slot = ITEM;
					if (player.itemAmount[ITEM] > 2 && !player.isActivePrayer(Prayers.PROTECT_ITEM)) {
						player.WillKeepAmt1 = 3;
					} else if (player.itemAmount[ITEM] > 3 && player.isActivePrayer(Prayers.PROTECT_ITEM)) {
						player.WillKeepAmt1 = 4;
					} else {
						player.WillKeepAmt1 = player.itemAmount[ITEM];
					}
				}
			}
		}
		for (int EQUIP = 0; EQUIP < 14; EQUIP++) {
			if (player.playerEquipment[EQUIP] > 0) {
				ItemsContained += 1;
				NextValue = (int) Math.floor(deathValue(player.playerEquipment[EQUIP]));
				if (NextValue > BestValue) {
					BestValue = NextValue;
					player.WillKeepItem1 = player.playerEquipment[EQUIP];
					player.WillKeepItem1Slot = EQUIP + 28;
					if (player.playerEquipmentN[EQUIP] > 2 && !player.isActivePrayer(Prayers.PROTECT_ITEM)) {
						player.WillKeepAmt1 = 3;
					} else if (player.playerEquipmentN[EQUIP] > 3 && player.isActivePrayer(Prayers.PROTECT_ITEM)) {
						player.WillKeepAmt1 = 4;
					} else {
						player.WillKeepAmt1 = player.playerEquipmentN[EQUIP];
					}
				}
			}
		}
		if (!player.isSkulled && ItemsContained > 1 && (player.WillKeepAmt1 < 3 || (player.isActivePrayer(Prayers.PROTECT_ITEM) && player.WillKeepAmt1 < 4))) {
			BestItem2(player, ItemsContained);
		}
	}

	public static void BestItem2(Player player, int ItemsContained) {
		int BestValue = 0;
		int NextValue;
		player.WillKeepItem2 = 0;
		player.WillKeepItem2Slot = 0;
		for (int ITEM = 0; ITEM < 28; ITEM++) {
			if (player.playerInventory[ITEM] > 0) {
				NextValue = (int) Math.floor(deathValue(player.playerInventory[ITEM] - 1));
				if (NextValue > BestValue && !(ITEM == player.WillKeepItem1Slot && player.playerInventory[ITEM] - 1 == player.WillKeepItem1)) {
					BestValue = NextValue;
					player.WillKeepItem2 = player.playerInventory[ITEM] - 1;
					player.WillKeepItem2Slot = ITEM;
					if (player.itemAmount[ITEM] > 2 - player.WillKeepAmt1 && !player.isActivePrayer(Prayers.PROTECT_ITEM)) {
						player.WillKeepAmt2 = 3 - player.WillKeepAmt1;
					} else if (player.itemAmount[ITEM] > 3 - player.WillKeepAmt1 && player.isActivePrayer(Prayers.PROTECT_ITEM)) {
						player.WillKeepAmt2 = 4 - player.WillKeepAmt1;
					} else {
						player.WillKeepAmt2 = player.itemAmount[ITEM];
					}
				}
			}
		}
		for (int EQUIP = 0; EQUIP < 14; EQUIP++) {
			if (player.playerEquipment[EQUIP] > 0) {
				NextValue = (int) Math.floor(deathValue(player.playerEquipment[EQUIP]));
				if (NextValue > BestValue && !(EQUIP + 28 == player.WillKeepItem1Slot && player.playerEquipment[EQUIP] == player.WillKeepItem1)) {
					BestValue = NextValue;
					player.WillKeepItem2 = player.playerEquipment[EQUIP];
					player.WillKeepItem2Slot = EQUIP + 28;
					if (player.playerEquipmentN[EQUIP] > 2 - player.WillKeepAmt1 && !player.isActivePrayer(Prayers.PROTECT_ITEM)) {
						player.WillKeepAmt2 = 3 - player.WillKeepAmt1;
					} else if (player.playerEquipmentN[EQUIP] > 3 - player.WillKeepAmt1 && player.isActivePrayer(Prayers.PROTECT_ITEM)) {
						player.WillKeepAmt2 = 4 - player.WillKeepAmt1;
					} else {
						player.WillKeepAmt2 = player.playerEquipmentN[EQUIP];
					}
				}
			}
		}
		if (!player.isSkulled && ItemsContained > 2 && (player.WillKeepAmt1 + player.WillKeepAmt2 < 3 || (player.isActivePrayer(Prayers.PROTECT_ITEM) && player.WillKeepAmt1 + player.WillKeepAmt2 < 4))) {
			BestItem3(player, ItemsContained);
		}
	}

	private static void BestItem3(Player player, int ItemsContained) {
		int BestValue = 0;
		int NextValue;
		player.WillKeepItem3 = 0;
		player.WillKeepItem3Slot = 0;
		for (int ITEM = 0; ITEM < 28; ITEM++) {
			if (player.playerInventory[ITEM] > 0) {
				NextValue = (int) Math.floor(deathValue(player.playerInventory[ITEM] - 1));
				if (NextValue > BestValue && !(ITEM == player.WillKeepItem1Slot && player.playerInventory[ITEM] - 1 == player.WillKeepItem1)
						&& !(ITEM == player.WillKeepItem2Slot && player.playerInventory[ITEM] - 1 == player.WillKeepItem2)) {
					BestValue = NextValue;
					player.WillKeepItem3 = player.playerInventory[ITEM] - 1;
					player.WillKeepItem3Slot = ITEM;
					if (player.itemAmount[ITEM] > 2 - (player.WillKeepAmt1 + player.WillKeepAmt2) && !player.isActivePrayer(Prayers.PROTECT_ITEM)) {
						player.WillKeepAmt3 = 3 - (player.WillKeepAmt1 + player.WillKeepAmt2);
					} else if (player.itemAmount[ITEM] > 3 - (player.WillKeepAmt1 + player.WillKeepAmt2) && player.isActivePrayer(Prayers.PROTECT_ITEM)) {
						player.WillKeepAmt3 = 4 - (player.WillKeepAmt1 + player.WillKeepAmt2);
					} else {
						player.WillKeepAmt3 = player.itemAmount[ITEM];
					}
				}
			}
		}
		for (int EQUIP = 0; EQUIP < 14; EQUIP++) {
			if (player.playerEquipment[EQUIP] > 0) {
				NextValue = (int) Math.floor(deathValue(player.playerEquipment[EQUIP]));
				if (NextValue > BestValue && !(EQUIP + 28 == player.WillKeepItem1Slot && player.playerEquipment[EQUIP] == player.WillKeepItem1)
						&& !(EQUIP + 28 == player.WillKeepItem2Slot && player.playerEquipment[EQUIP] == player.WillKeepItem2)) {
					BestValue = NextValue;
					player.WillKeepItem3 = player.playerEquipment[EQUIP];
					player.WillKeepItem3Slot = EQUIP + 28;
					if (player.playerEquipmentN[EQUIP] > 2 - (player.WillKeepAmt1 + player.WillKeepAmt2) && !player.isActivePrayer(Prayers.PROTECT_ITEM)) {
						player.WillKeepAmt3 = 3 - (player.WillKeepAmt1 + player.WillKeepAmt2);
					} else if (player.playerEquipmentN[EQUIP] > 3 - player.WillKeepAmt1 && player.isActivePrayer(Prayers.PROTECT_ITEM)) {
						player.WillKeepAmt3 = 4 - (player.WillKeepAmt1 + player.WillKeepAmt2);
					} else {
						player.WillKeepAmt3 = player.playerEquipmentN[EQUIP];
					}
				}
			}
		}
		if (!player.isSkulled && ItemsContained > 3 && player.isActivePrayer(Prayers.PROTECT_ITEM) && ((player.WillKeepAmt1 + player.WillKeepAmt2 + player.WillKeepAmt3) < 4)) {
			BestItem4(player);
		}
	}

	private static void BestItem4(Player player) {
		int BestValue = 0;
		int NextValue;
		player.WillKeepItem4 = 0;
		player.WillKeepItem4Slot = 0;
		for (int ITEM = 0; ITEM < 28; ITEM++) {
			if (player.playerInventory[ITEM] > 0) {
				NextValue = (int) Math.floor(deathValue(player.playerInventory[ITEM] - 1));
				if (NextValue > BestValue && !(ITEM == player.WillKeepItem1Slot && player.playerInventory[ITEM] - 1 == player.WillKeepItem1)
						&& !(ITEM == player.WillKeepItem2Slot && player.playerInventory[ITEM] - 1 == player.WillKeepItem2) && !(ITEM == player.WillKeepItem3Slot && player.playerInventory[ITEM] - 1 == player.WillKeepItem3)) {
					BestValue = NextValue;
					player.WillKeepItem4 = player.playerInventory[ITEM] - 1;
					player.WillKeepItem4Slot = ITEM;
				}
			}
		}
		for (int EQUIP = 0; EQUIP < 14; EQUIP++) {
			if (player.playerEquipment[EQUIP] > 0) {
				NextValue = (int) Math.floor(deathValue(player.playerEquipment[EQUIP]));
				if (NextValue > BestValue && !(EQUIP + 28 == player.WillKeepItem1Slot && player.playerEquipment[EQUIP] == player.WillKeepItem1)
						&& !(EQUIP + 28 == player.WillKeepItem2Slot && player.playerEquipment[EQUIP] == player.WillKeepItem2) && !(EQUIP + 28 == player.WillKeepItem3Slot && player.playerEquipment[EQUIP] == player.WillKeepItem3)) {
					BestValue = NextValue;
					player.WillKeepItem4 = player.playerEquipment[EQUIP];
					player.WillKeepItem4Slot = EQUIP + 28;
				}
			}
		}
	}

	private static void sendFrame34a(Player c, int frame, int item, int slot, int amount) {
		c.outStream.putFrameVarShort(34);
		int offset = c.outStream.offset;
		c.outStream.writeShort(frame);
		c.outStream.writeByte(slot);
		c.outStream.writeShort(item + 1);
		c.outStream.writeByte(255);
		c.outStream.putInt(amount);
		c.outStream.putFrameSizeShort(offset);
	}

}
