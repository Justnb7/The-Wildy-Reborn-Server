package com.model.game.character.player.skill.fletching;

import com.model.game.character.player.Player;
import com.model.game.character.player.packets.encode.impl.SendClearScreen;

/**
 * @author Jesse Pinkman (Rune-Server.org)
 */

public class FletchingHandler {

	public enum Bows {
		SHORTBOWS(1511, 1, 5, 50, 841, 6678, "short"), OAKS(1521, 20, 17, 54, 843, 6679, "short"), WILLOWS(1519, 35, 33, 60, 849, 6680, "short"), MAPLES(1517, 50, 50, 64, 853, 6681, "short"), YEWS(1515, 65, 68, 68, 857, 6682, "short"), MAGICS(1513, 80, 83, 72, 861, 6683, "short"), LONGBOWL(1511, 10, 10, 48, 839, 6684, "long"), OAKL(1521, 25, 25, 56, 845, 6685, "long"), WILLOWL(1519, 40, 42, 60, 847, 6686, "long"), MAPLEL(1517, 55, 58, 62, 851, 6687, "long"), YEWL(1515, 65, 70, 66, 855, 6688, "long"), MAGICL(1513, 85, 92, 70, 859, 6689, "long");

		private int logType, req, exp, bowUnStr, bowStr, emote;
		private String bowType = "";

		public int getLog() {
			return logType;
		}

		public int getReq() {
			return req;
		}

		public int getExp() {
			return exp;
		}

		public int getBowU() {
			return bowUnStr;
		}

		public int getBow() {
			return bowStr;
		}

		public int getEmote() {
			return emote;
		}

		public String getBowType() {
			return bowType;
		}

		private Bows(int log, int req, int exp, int bow, int bow2, int emote, String type) {
			this.logType = log;
			this.req = req;
			this.exp = exp;
			this.bowUnStr = bow;
			this.bowStr = bow2;
			this.emote = emote;
			this.bowType = type;
		}
	}

	public enum CrossBow {
		BRONZE(1511, 9, 6, 12, 9440, 9420, 9454, 9174, 6671), IRON(1519, 24, 16, 32, 9444, 9423, 9457, 9177, 6673), MITHRIL(1517, 54, 32, 64, 9448, 9427, 9461, 9181, 6675), RUNE(1515, 69, 50, 100, 9452, 9431, 9465, 9185, 6677);

		private int logType, req, exp1, exp2, stock, limbs, bowU, bow, emote;

		public int getLog() {
			return logType;
		}

		public int getReq() {
			return req;
		}

		public int getExp1() {
			return exp1;
		}

		public int getExp2() {
			return exp2;
		}

		public int getStock() {
			return stock;
		}

		public int getLimbs() {
			return limbs;
		}

		public int getBow() {
			return bow;
		}

		public int getBowU() {
			return bowU;
		}

		public int getEmote() {
			return emote;
		}

		private CrossBow(int log, int req, int exp, int exp2, int stock, int limbs, int bowU, int bow, int emote) {
			this.logType = log;
			this.req = req;
			this.exp1 = exp;
			this.exp2 = exp2;
			this.stock = stock;
			this.limbs = limbs;
			this.bowU = bowU;
			this.bow = bow;
			this.emote = emote;
		}

		public static CrossBow forID(int logId) {
			for (CrossBow bow : CrossBow.values())
				if (bow.getLog() == logId)
					return bow;
			return null;
		}

		public static CrossBow forUID(int bowU) {
			for (CrossBow bow : CrossBow.values())
				if (bow.getBowU() == bowU)
					return bow;
			return null;
		}

		public static CrossBow forStockID(int stock) {
			for (CrossBow bow : CrossBow.values())
				if (bow.getStock() == stock)
					return bow;
			return null;
		}
	}

	public enum Arrows {
		BRONZE(1, 3, 39, 882), IRON(15, 4, 40, 884), STEEL(30, 6, 41, 886), MITHRIL(45, 9, 42, 888), ADAMANT(60, 10, 43, 890), RUNE(75, 14, 44, 892), DRAGON(90, 16, 11237, 11212);

		private int req, exp, tips, arrow;

		public int getTips() {
			return tips;
		}

		public int getReq() {
			return req;
		}

		public int getExp() {
			return exp;
		}

		public int getArrow() {
			return arrow;
		}

		private Arrows(int req, int exp, int tips, int arrow) {
			this.req = req;
			this.exp = exp;
			this.tips = tips;
			this.arrow = arrow;
		}

		public static Arrows forId(int tips) {
			for (Arrows arrow : Arrows.values())
				if (arrow.getTips() == tips)
					return arrow;
			return null;
		}
	}

	public enum Bolts {
		BRONZE(9375, -1, 877, 9, 1, "bolt"), IRON(9377, -1, 9140, 39, 2, "bolt"), STEEL(9378, -1, 9141, 46, 4, "bolt"), MITHRIL(9379, -1, 9142, 54, 5, "bolt"), ADAMANT(9380, -1, 9143, 61, 7, "bolt"), RUNE(9381, -1, 9144, 69, 10, "bolt"), OPAL(877, 45, 879, 11, 2, "boltGem"), PEARL(9140, 46, 880, 41, 3, "boltGem"), REDTOPAZ(9141, 9188, 9336, 48, 4, "boltGem"), SAPPHIRE(9142, 9189, 9337, 56, 5, "boltGem"), EMERALD(9142, 9190, 9338, 58, 6, "boltGem"), RUBY(9143, 9191, 9339, 63, 6, "boltGem"), DIAMOND(9143, 9192, 9340, 65, 7, "boltGem"), DRAGON(9144, 9193, 9341, 71, 8, "boltGem"), ONYX(9144, 9194, 9342, 73, 9, "boltGem")
		, MITHRIL_DART(822, -1, 809, 52, 1, "bolt"), ADAMANT_DART(823, -1, 810, 52, 18, "bolt"),  RUNITE_DART(824, -1, 811, 52, 23, "bolt");

		private int input1, input2, output, req, exp;
		private String type = "";

		public int getInput1() {
			return input1;
		}

		public int getInput2() {
			return input2;
		}

		public int getOutput() {
			return output;
		}

		public int getReq() {
			return req;
		}

		public int getExp() {
			return exp;
		}

		public String getType() {
			return type;
		}

		private Bolts(int item, int item2, int item3, int req, int exp, String type) {
			this.input1 = item;
			this.input2 = item2;
			this.output = item3;
			this.req = req;
			this.exp = exp;
			this.type = type;
		}
	}

	public enum BoltTips {
		OPAL(1609, 45, 12, 11, 2), PEARL(411, 46, 6, 41, 2), REDTOPAZ(1613, 9188, 12, 48, 3), SAPPHIRE(1607, 9189, 12, 56, 4), EMERALD(1605, 9190, 12, 58, 6), RUBY(1603, 9191, 12, 63, 6), DIAMOND(1601, 9192, 12, 65, 7), DRAGON(1615, 9193, 12, 71, 8), ONYX(6573, 9194, 24, 73, 9);

		private int input1, output, amt, req, exp;

		public int getInput() {
			return input1;
		}

		public int getOutput() {
			return output;
		}

		public int getAmt() {
			return amt;
		}

		public int getReq() {
			return req;
		}

		public int getExp() {
			return exp;
		}

		private BoltTips(int item, int item2, int amt, int req, int exp) {
			this.input1 = item;
			this.output = item2;
			this.amt = amt;
			this.req = req;
			this.exp = exp;
		}
	}

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
			if (itemUsed == bolt.getInput1()) {
				player.fletchItem = useWith;
				return itemUsed;
			} else if (useWith == bolt.getInput1()) {
				player.fletchItem = itemUsed;
				return useWith;
			}
		}
		return -1;
	}

	public static void appendType(Player player, int itemUsed, int useWith) {
		if(player.isSkilling) {
			return;
		}
		player.write(new SendClearScreen());
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
						if (bolt.getInput1() == player.fletchItem) {
							player.fletchThis = "bolt";
							player.fletchSprites[2] = bolt.getOutput();
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
				if (player.fletchItem == bow.getBowU()) {
					player.fletchThis = "stringBow";
					player.fletchSprites[2] = bow.getBow();
					break;
				}
			Fletching.openDialogue(player);
			return;
		}
		if (refID == Fletching.refItems[4]) {
			for (final CrossBow bow : CrossBow.values())
				if (player.fletchItem == bow.getBowU()) {
					player.fletchThis = "stringCross";
					player.fletchSprites[2] = bow.getBow();
					break;
				}
			Fletching.openDialogue(player);
			return;
		}
		if (refID == Fletching.refItems[5]) {
			for (final BoltTips tips : BoltTips.values())
				if (tips.getInput() == player.fletchItem) {
					player.fletchThis = "tips";
					player.fletchSprites[2] = tips.getOutput();
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
						player.fletchSprites[2] = bow.getBowU();
					if (bow.getBowType().equals("long"))
						player.fletchSprites[3] = bow.getBowU();
				}
			Fletching.openDialogue(player);
			return;
		}
		for (final CrossBow bow : CrossBow.values()) {
			if (player.fletchItem == bow.getStock() && refID == bow.getLimbs()) {
				player.fletchThis = "limb";
				player.fletchSprites[2] = bow.getBowU();
				Fletching.openDialogue(player);
				return;
			}
		}
		for (final Bolts bolt : Bolts.values()) {
			if (bolt.getType().equals("boltGem")) {
				if (bolt.getInput2() == player.fletchItem && refID == bolt.getInput1()) {
					player.fletchThis = "boltGem";
					player.fletchSprites[2] = bolt.getOutput();
					Fletching.openDialogue(player);
					break;
				}
			}
		}
	}
}