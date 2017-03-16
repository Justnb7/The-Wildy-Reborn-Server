package com.model.game.character.player.skill.fletching;

import com.model.game.character.player.Player;

/**
 * @author Jesse Pinkman (Rune-Server.org)
 */
public class FletchingHandler {
	
	public static int getRef(Player player, int itemUsed, int useWith) {
		for (int refItem : Fletching.refItems) {
			if (itemUsed == refItem) {
				player.fletchItem = useWith;
				return itemUsed;
			} else if (useWith == refItem) {
				player.fletchItem = itemUsed;
				return useWith;
			}
		}
		for (final CrossBow bow : CrossBow.values()) {
			if (itemUsed == bow.getLimbs()) {
				player.fletchItem = useWith;
				return itemUsed;
			} else if (useWith == bow.getLimbs()) {
				player.fletchItem = itemUsed;
				return useWith;
			}
		}
		for (final Bolts bolt : Bolts.values()) {
			if (itemUsed == bolt.getUnfBolts()) {
				player.fletchItem = useWith;
				return itemUsed;
			} else if (useWith == bolt.getUnfBolts()) {
				player.fletchItem = itemUsed;
				return useWith;
			}
		}
		return -1;
	}

	public static void appendType(Player player, int itemUsed, int useWith) {

		player.getActionSender().sendRemoveInterfacePacket();
		int refID = getRef(player, itemUsed, useWith);
		if (refID <= 0)
			return;
		if (refID == Fletching.refItems[2]) {
			if (player.fletchItem == player.arrowShaft) {
				player.fletchThis = "headlessarrow";
				player.fletchSprites[2] = Fletching.getHeadless();
				Fletching.openDialogue(player);
				return;
			} else {
				for (final Bolts bolt : Bolts.values())
					if (bolt.getType().equals("bolt")) {
						if (bolt.getUnfBolts() == player.fletchItem) {
							player.fletchThis = "bolt";
							player.fletchSprites[2] = bolt.getReward();
							break;
						}
					}
			}
			Fletching.openDialogue(player);
			return;
		}
		if (refID == Fletching.refItems[1]) {
			for (final Arrows arrow : Arrows.values())
				if (arrow.getTips() == player.fletchItem) {
					player.fletchThis = "arrow";
					player.fletchSprites[2] = arrow.getArrow();
					break;
				}
			Fletching.openDialogue(player);
			return;
		}
		if (refID == Fletching.refItems[3]) {
			for (final Bows bow : Bows.values())
				if (player.fletchItem == bow.getUnstrungBow()) {
					player.fletchThis = "stringBow";
					player.fletchSprites[2] = bow.getStrungBow();
					break;
				}
			Fletching.openDialogue(player);
			return;
		}
		if (refID == Fletching.refItems[4]) {
			for (final CrossBow bow : CrossBow.values())
				if (player.fletchItem == bow.getUnstrungCrossbow()) {
					player.fletchThis = "stringCross";
					player.fletchSprites[2] = bow.getCrossbow();
					break;
				}
			Fletching.openDialogue(player);
			return;
		}
		if (refID == Fletching.refItems[5]) {
			for (final BoltTips tips : BoltTips.values())
				if (tips.getGem() == player.fletchItem) {
					player.fletchThis = "tips";
					player.fletchSprites[2] = tips.getReward().getId();
					break;
				}
			Fletching.openDialogue(player);
			return;
		}
		if (refID == Fletching.refItems[0]) {
			for (final Bows bow : Bows.values())
				if (bow.getLog() == player.fletchItem) {
					player.fletchThis = "log";
					player.fletchSprites[0] = player.arrowShaft;
					for (final CrossBow cbow : CrossBow.values())
						if (player.fletchItem == cbow.getLog()) {
							player.fletchSprites[1] = cbow.getStock();
							break;
						}
					if (bow.getBowType().equals("short"))
						player.fletchSprites[2] = bow.getUnstrungBow();
					if (bow.getBowType().equals("long"))
						player.fletchSprites[3] = bow.getUnstrungBow();
				}
			Fletching.openDialogue(player);
			return;
		}
		for (final CrossBow bow : CrossBow.values()) {
			if (player.fletchItem == bow.getStock() && refID == bow.getLimbs()) {
				player.fletchThis = "limb";
				player.fletchSprites[2] = bow.getUnstrungCrossbow();
				Fletching.openDialogue(player);
				return;
			}
		}
		for (final Bolts bolt : Bolts.values()) {
			if (bolt.getType().equals("boltGem")) {
				if (bolt.getBoltTips() == player.fletchItem
						&& refID == bolt.getUnfBolts()) {
					player.fletchThis = "boltGem";
					player.fletchSprites[2] = bolt.getReward();
					Fletching.openDialogue(player);
					break;
				}
			}
		}
	}
}