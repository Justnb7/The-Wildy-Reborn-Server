package com.model.game.character.player.skill.fletching;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.task.ScheduledTask;

/**
 * @author Jesse Pinkman (Rune-Server.org)
 */
public class Fletching {
	
	/**
	 * ClickingButtons Data
	 */
	public static int[][] otherButtons = {
			{ 34245, 0, 1 },
			{ 34244, 0, 5 },
			{ 34243, 0, 10 },
			{ 34242, 0, 28 }, // Far Left Picture, 1,5,10,X
			
			{ 34249, 1, 1 },
			{ 34248, 1, 5 },
			{ 34247, 1, 10 },
			{ 34246, 1, 28 }, // Left Picture,  1,5,10,X
			
			{ 34253, 2, 1 },
			{ 34252, 2, 5 },
			{ 34251, 2, 10 },
			{ 34250, 2, 28 }, // Middle Picture, 1,5,10,X
			
			{ 35001, 3, 1 },
			{ 35000, 3, 5 },
			{ 34255, 3, 10 },
			{ 34254, 3, 28 }, // Right Picture, 1,5,10,X
			
			{ 35005, 4, 1 }, 
			{ 35004, 4, 5 }, 
			{ 35003, 4, 10 },
			{ 35002, 4, 28 }, // Far Right Picture, 1,5,10,X
	};

	/**
	 * Reference Items (Ex: Feathers,Knife,Chisel)
	 */
	public static int[] refItems = { /* knife */946,
		/* headless */53, 
		/* feather */ 314,
		/* bowString */1777,
		/* cBowString */9438,
		/* chisel */1755 };

	/**
	 * Returns Reference Item Ids
	 */
	public static int getKnife() {
		return refItems[0];
	}

	public static int getHeadless() {
		return refItems[1];
	}

	public static int getFeather() {
		return refItems[2];
	}

	public static int getBS() {
		return refItems[3];
	}

	public static int getCBS() {
		return refItems[4];
	}

	public static int getChisel() {
		return refItems[5];
	}

	/**
	 * Opens Fletching Dialogue Interface
	 */
	public static void openDialogue(Player player) {
		if (player.fletchSprites[2] <= 0) {
			resetFletching(player);
			return;
		}
		int i1 = player.fletchSprites[0], i2 = player.fletchSprites[1], i3 = player.fletchSprites[2], i4 = player.fletchSprites[3], i5 = player.fletchSprites[4];
		chooseItem5(player, "What would you like to fletch?", name(player, i1), name(player, i2), name(player, i3), name(player, i4), name(player, i5), i1, i2, i3, i4, i5);
	}

	/**
	 * Gets the item name and trim's if needed
	 */
	public static String name(Player player, int item) {
		if (item <= 0)
			return "";
		String[] remove = { "Oak ", "Maple ", "Willow ", "Yew ", "Magic ",
				"Arrow ", "Crossbow ", "Bronze ", "Iron ", "Mithril ",
				"Adamant ", "Rune ", "Opal ", "Jade ", "Topaz ", "Sapphire ",
				"Emerald ", "Ruby ", "Diamond ", "Dragonstone ", "Dragon ",
				"Onyx " };
		String name = player.getItems().getItemName(item);
		for (String bad : remove) {
			if (name.contains(bad)) {
				name = name.replaceAll(bad, "");
			}
		}
		return name;
	}

	/**
	 * Handles Fletching Interface Clicks
	 */
	public static void handleFletchingClick(Player player, int actionID) {
		int fletchLevel = player.getSkills().getLevelForExperience(Skills.FLETCHING);
		for (int i = 0; i < otherButtons.length; i++) {
			if (otherButtons[i][0] == actionID) {
				player.fletchIndex = otherButtons[i][1];
				player.fletchAmount = otherButtons[i][2];
				if (player.fletchSprites[player.fletchIndex] <= 0)
					return;
				player.isFletching = true;
				if (player.fletchAmount > 1) {
					player.needsFletchDelay = true;
					startCycle(player);
				}
				player.fletchAmount--;
				if (player.fletchThis.equals("log")) {
					if (player.fletchIndex == 0) {
						player.fletchThis = "shaft";
						shaft(player);
						break;
					} else if (player.fletchIndex == 1) {
						player.fletchThis = "stock";
						stock(player, fletchLevel);
						break;
					} else if (player.fletchIndex == 2) {
						player.fletchThis = "short";
						bow(player, fletchLevel);
						break;
					} else if (player.fletchIndex == 3) {
						player.fletchThis = "long";
						bow(player, fletchLevel);
						break;
					}
				}
				if (player.fletchThis.equals("headlessarrow")) {
					headless(player);
					break;
				} else if (player.fletchThis.equals("arrow")) {
					arrows(player, fletchLevel);
					break;
				} else if (player.fletchThis.equals("bolt") || player.fletchThis.equals("boltGem")) {
					bolts(player, fletchLevel);
					break;
				} else if (player.fletchThis.equals("stringBow")) {
					stringBow(player, fletchLevel);
					break;
				} else if (player.fletchThis.equals("stringCross")) {
					stringCrossbow(player, fletchLevel);
					break;
				} else if (player.fletchThis.equals("tips")) {
					boltTips(player, fletchLevel);
					break;
				} else if (player.fletchThis.equals("limb")) {
					addLimbs(player, fletchLevel);
					break;
				}
				break;
			}
		}
	}

	/**
	 * Resets the Fletching Variables
	 */
	public static void resetFletching(Player player) {
		player.fletchDelay = player.fletchAmount = player.fletchItem = player.fletchIndex = -1;
		player.fletchThis = "";
		player.lastFletch = 0;
		player.isFletching = player.needsFletchDelay = false;
		for (int r = 0; r < player.fletchSprites.length; r++)
			player.fletchSprites[r] = -1;
	}

	/**
	 * Handles Timer
	 */
	public static void startCycle(final Player player) {

		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			@Override
			public void execute() {
				if (!player.isFletching) {
					this.stop();
					return;
				}
				if (!player.isActive())
					this.stop();
				if (player.lastFletch <= 0 || System.currentTimeMillis() - player.lastFletch >= 1600) {
					if (player.fletchAmount > 0)
						appendDelay(player);
					else
						this.stop();
				}
			}

			@Override
			public void onStop() {
				resetFletching(player);
			}
		}.attach(player));
	}

	public static void headless(Player player) {
		if (player.getItems().playerHasItem(player.arrowShaft, 1)) {
			if (player.getItems().playerHasItem(getFeather(), 1)) {
				int Slot = player.getItems().getItemSlot(player.arrowShaft), amount = -1,
						Slot2 = player.getItems().getItemSlot(getFeather()), amount2 = -1;
				if (Slot != -1)
					amount = player.itemAmount[Slot];
				if (Slot2 != -1)
					amount2 = player.itemAmount[Slot2];
				if (amount >= 15 && amount2 >= 15) {
					player.getItems().deleteItem(player.arrowShaft, 15);
					player.getItems().deleteItem(getFeather(), 15);
					player.getItems().addItem(getHeadless(), 15);
					player.getSkills().addExperience(Skills.FLETCHING, 15);
				} else {
					if (amount <= amount2) {
						player.getItems().deleteItem(player.arrowShaft, amount);
						player.getItems().deleteItem(getFeather(), amount);
						player.getItems().addItem(getHeadless(), amount);
						player.getSkills().addExperience(Skills.FLETCHING, 15);
					} else {
						player.getItems().deleteItem(player.arrowShaft, amount2);
						player.getItems().deleteItem(getFeather(), amount2);
						player.getItems().addItem(getHeadless(), amount2);
						player.getSkills().addExperience(Skills.FLETCHING, 15);
					}
				}
				player.lastFletch = System.currentTimeMillis();
			} else {
				player.getActionSender().sendRemoveInterfacePacket();
				if (player.fletchItem > 0)
					resetFletching(player);
			}
		} else {
			player.getActionSender().sendRemoveInterfacePacket();
			if (player.fletchItem > 0)
				resetFletching(player);
		}
		player.getActionSender().sendRemoveInterfacePacket();
	}

	public static void arrows(Player player, int fletchLevel) {
		if (Arrows.forId(player.fletchItem) != null) {
			Arrows arrow = Arrows.forId(player.fletchItem);
			if (fletchLevel >= arrow.getLevelReq()) {
				if (player.getItems().playerHasItem(arrow.getTips(), 1)) {
					if (player.getItems().playerHasItem(getHeadless(), 1)) {
						int Slot = player.getItems().getItemSlot(arrow.getTips()), amount = -1,
								Slot2 = player.getItems().getItemSlot(getHeadless()), amount2 = -1;
						if (Slot != -1)
							amount = player.itemAmount[Slot];
						if (Slot2 != -1)
							amount2 = player.itemAmount[Slot2];
						if (amount >= 15 && amount2 >= 15) {
							player.getItems().deleteItem(arrow.getTips(), 15);
							player.getItems().deleteItem(getHeadless(), 15);
							player.getItems().addItem(arrow.getArrow(), 15);
							player.getSkills().addExperience(Skills.FLETCHING, arrow.getExperience() * 15);
						} else {
							if (amount <= amount2) {
								player.getItems().deleteItem(arrow.getTips(), amount);
								player.getItems().deleteItem(getHeadless(), amount);
								player.getItems().addItem(arrow.getArrow(), amount);
								player.getSkills().addExperience(Skills.FLETCHING, arrow.getExperience());
							} else {
								player.getItems().deleteItem(arrow.getTips(), amount2);
								player.getItems().deleteItem(getHeadless(), amount2);
								player.getItems().addItem(arrow.getArrow(), amount2);
								player.getSkills().addExperience(Skills.FLETCHING, arrow.getExperience());
							}
						}
						player.lastFletch = System.currentTimeMillis();
					} else {
						player.getActionSender().sendRemoveInterfacePacket();
						if (player.fletchItem > 0)
							resetFletching(player);
					}
				} else {
					player.getActionSender().sendRemoveInterfacePacket();
					if (player.fletchItem > 0)
						resetFletching(player);
				}
			} else {
				player.getActionSender().sendRemoveInterfacePacket();
				if (player.fletchItem > 0)
					resetFletching(player);
				player.getActionSender().sendMessage("You need a Fletching level of " + arrow.getLevelReq() + " to fletch a "
						+ player.getItems().getItemName(arrow.getArrow()));
			}
		} else {
			player.getActionSender().sendRemoveInterfacePacket();
			if (player.fletchItem > 0)
				resetFletching(player);
		}
		player.getActionSender().sendRemoveInterfacePacket();
	}

	public static void boltTips(Player player, int fletchLevel) {
		for (final BoltTips tip : BoltTips.values())
			if (tip.getGem() == player.fletchItem)
				if (player.getItems().playerHasItem(tip.getGem(), 1)) {
					if (fletchLevel >= tip.getLevelReq()) {
						player.getItems().deleteItem(tip.getGem(), 1);
						player.getItems().addItem(tip.getReward());
						player.getSkills().addExperience(Skills.FLETCHING, tip.getExperience());
						player.getActionSender().sendRemoveInterfacePacket();
						player.lastFletch = System.currentTimeMillis();
					} else {
						player.getActionSender().sendRemoveInterfacePacket();
						if (player.fletchItem > 0)
							resetFletching(player);
						player.getActionSender().sendMessage("You need a Fletching level of " + tip.getLevelReq() + " to fletch " + player.getItems().getItemName(tip.getReward().getId()));
					}
				} else {
					player.getActionSender().sendRemoveInterfacePacket();
					if (player.fletchItem > 0)
						resetFletching(player);
				}
	}

	public static void bolts(Player player, int fletchLevel) {
		int unfBolts = 0, tips = 0, reward = 0, levelRequirement = 0, experience = 0;
		for (final Bolts bolt : Bolts.values()) {
			if (player.fletchThis.equals("bolt") && bolt.getType().equals("bolt") && player.fletchItem == bolt.getUnfBolts()) {
				unfBolts = bolt.getUnfBolts();
				tips = getFeather();
				reward = bolt.getReward();
				levelRequirement = bolt.getLevelRequirement();
				experience = bolt.getExperience();
				break;
			} else if (player.fletchThis.equals("boltGem") && bolt.getType().equals("boltGem") && player.fletchItem == bolt.getBoltTips()) {
				unfBolts = bolt.getUnfBolts();
				tips = bolt.getBoltTips();
				reward = bolt.getReward();
				levelRequirement = bolt.getLevelRequirement();
				experience = bolt.getExperience();
				break;
			}
		}
		int firstSlot = player.getItems().getItemSlot(unfBolts), firstAmount = -1, secondSlot = player.getItems().getItemSlot(tips), secondAmount = -1;
		if (firstSlot != -1)
			firstAmount = player.itemAmount[firstSlot];
		if (secondSlot != -1)
			secondAmount = player.itemAmount[secondSlot];
		if (fletchLevel >= levelRequirement) {
			if (player.getItems().playerHasItem(unfBolts, 1) && player.getItems().playerHasItem(tips, 1)) {
				if (firstAmount >= 15 && secondAmount >= 15) {
					player.getItems().deleteItem(unfBolts, 15);
					player.getItems().deleteItem(tips, 15);
					player.getItems().addItem(reward, 15);
					player.getSkills().addExperience(Skills.FLETCHING, (experience) * 15);
				} else {
					if (firstAmount <= secondAmount) {
						player.getItems().deleteItem(unfBolts, firstAmount);
						player.getItems().deleteItem(tips, firstAmount);
						player.getItems().addItem(reward, firstAmount);
						player.getSkills().addExperience(Skills.FLETCHING, experience);
					} else {
						player.getItems().deleteItem(unfBolts, secondAmount);
						player.getItems().deleteItem(tips, secondAmount);
						player.getItems().addItem(reward, secondAmount);
						player.getSkills().addExperience(Skills.FLETCHING, experience);
					}
				}
				player.lastFletch = System.currentTimeMillis();
			}
		} else {
			player.getActionSender().sendRemoveInterfacePacket();
			if (player.fletchItem > 0)
				resetFletching(player);
			player.getActionSender().sendMessage("You need a Fletching level of " + levelRequirement + " to fletch " + player.getItems().getItemName(reward));
		}
		player.getActionSender().sendRemoveInterfacePacket();
	}

	public static void shaft(Player player) {
		if (player.getItems().playerHasItem(player.fletchItem, 1)) {
			player.getItems().deleteItem(player.fletchItem, 1);
			player.getItems().addItem(player.arrowShaft, 15);
			player.getSkills().addExperience(Skills.FLETCHING, 5);
			player.getActionSender().sendRemoveInterfacePacket();
			player.playAnimation(Animation.create(1248));
			player.lastFletch = System.currentTimeMillis();
		} else {
			player.getActionSender().sendRemoveInterfacePacket();
			if (player.fletchItem > 0)
				resetFletching(player);
		}
	}

	public static void stock(Player player, int fletchLevel) {
		for (final CrossBow bow : CrossBow.values()) {
			if (player.fletchItem == bow.getLog()) {
				if (fletchLevel >= bow.getLevelReq()) {
					if (player.getItems().playerHasItem(bow.getLog()) && player.getItems().playerHasItem(getKnife())) {
						player.getItems().deleteItem(bow.getLog(), 1);
						player.getItems().addItem(bow.getStock(), 1);
						player.getSkills().addExperience(Skills.FLETCHING, bow.getExp1());
						player.playAnimation(Animation.create(1248));
						player.lastFletch = System.currentTimeMillis();
					}
				} else {
					player.getActionSender().sendRemoveInterfacePacket();
					if (player.fletchItem > 0)
						resetFletching(player);
					player.getActionSender().sendMessage("You need a Fletching level of " + bow.getLevelReq() + " to fletch a " + player.getItems().getItemName(bow.getUnstrungCrossbow()));
				}
			}
		}
		player.getActionSender().sendRemoveInterfacePacket();
	}

	public static void addLimbs(Player player, int fletchLevel) {
		for (final CrossBow bow : CrossBow.values()) {
			if (player.fletchItem == bow.getStock()) {
				if (fletchLevel >= bow.getLevelReq()) {
					if (player.getItems().playerHasItem(bow.getStock())
							&& player.getItems().playerHasItem(bow.getLimbs())) {
						player.getItems().deleteItem(bow.getStock(), 1);
						player.getItems().deleteItem(bow.getLimbs(), 1);
						player.getItems().addItem(bow.getUnstrungCrossbow(), 1);
						player.getSkills().addExperience(Skills.FLETCHING, bow.getExp1());
						player.lastFletch = System.currentTimeMillis();
					}
				} else {
					player.getActionSender().sendRemoveInterfacePacket();
					if (player.fletchItem > 0)
						resetFletching(player);
					player.getActionSender().sendMessage("You need a Fletching level of " + bow.getLevelReq() + " to fletch a "
							+ player.getItems().getItemName(bow.getUnstrungCrossbow()));
				}
			}
		}
		player.getActionSender().sendRemoveInterfacePacket();
	}

	public static void stringBow(Player player, int fletchLevel) {
		for (final Bows bow : Bows.values()) {
			if (player.fletchItem == bow.getUnstrungBow()) {
				if (fletchLevel >= bow.getLevelReq()) {
					if (player.getItems().playerHasItem(getBS()) && player.getItems().playerHasItem(bow.getUnstrungBow())) {
						player.getItems().deleteItem(bow.getUnstrungBow(), 1);
						player.getItems().deleteItem(getBS(), 1);
						player.getItems().addItem(bow.getStrungBow(), 1);
						player.getSkills().addExperience(Skills.FLETCHING, bow.getExperience());
						player.playAnimation(bow.getAnimation());
						player.lastFletch = System.currentTimeMillis();
					}
				} else {
					player.getActionSender().sendRemoveInterfacePacket();
					if (player.fletchItem > 0)
						resetFletching(player);
					player.getActionSender().sendMessage("You need a Fletching level of " + bow.getLevelReq() + " to string a "
							+ player.getItems().getItemName(bow.getStrungBow()));
				}
			}
		}
		player.getActionSender().sendRemoveInterfacePacket();
	}

	public static void stringCrossbow(Player player, int fletchLevel) {
		for (final CrossBow bow : CrossBow.values()) {
			if (player.fletchItem == bow.getUnstrungCrossbow()) {
				if (fletchLevel >= bow.getLevelReq()) {
					if (player.getItems().playerHasItem(getCBS()) && player.getItems().playerHasItem(bow.getUnstrungCrossbow())) {
						player.getItems().deleteItem(bow.getUnstrungCrossbow(), 1);
						player.getItems().deleteItem(getCBS(), 1);
						player.getItems().addItem(bow.getCrossbow(), 1);
						player.getSkills().addExperience(Skills.FLETCHING, bow.getExp2());
						player.playAnimation(bow.getAnimation());
						player.lastFletch = System.currentTimeMillis();
					}
				} else {
					player.getActionSender().sendRemoveInterfacePacket();
					if (player.fletchItem > 0)
						resetFletching(player);
					player.getActionSender().sendMessage("You need a Fletching level of " + bow.getLevelReq() + " to string a " + player.getItems().getItemName(bow.getCrossbow()));
				}
			}
		}
		player.getActionSender().sendRemoveInterfacePacket();
	}

	public static void bow(Player player, int fletchLevel) {
		int log = 0, unstrungBow = 0, levelRequirement = 0, experience = 0;
		for (final Bows bow : Bows.values()) {
			if (player.fletchThis.equals("short") && bow.getBowType().equals("short") && player.fletchItem == bow.getLog()) {
				log = bow.getLog();
				unstrungBow = bow.getUnstrungBow();
				levelRequirement = bow.getLevelReq();
				experience = bow.getExperience();
				break;
			} else if (player.fletchThis.equals("long") && bow.getBowType().equals("long") && player.fletchItem == bow.getLog()) {
				log = bow.getLog();
				unstrungBow = bow.getUnstrungBow();
				levelRequirement = bow.getLevelReq();
				experience = bow.getExperience();
				break;
			}
		}
		if (fletchLevel >= levelRequirement) {
			if (player.getItems().playerHasItem(log, 1)) {
				player.getItems().deleteItem(log, 1);
				player.getItems().addItem(unstrungBow, 1);
				player.playAnimation(Animation.create(1248));
				player.lastFletch = System.currentTimeMillis();
				player.getSkills().addExperience(Skills.FLETCHING, experience);
			} else {
				player.getActionSender().sendRemoveInterfacePacket();
				if (player.fletchItem > 0)
					resetFletching(player);
			}
		} else {
			player.getActionSender().sendRemoveInterfacePacket();
			if (player.fletchItem > 0)
				resetFletching(player);
			player.getActionSender().sendMessage(
					"You need a Fletching level of " + levelRequirement + " to fletch a " + player.getItems().getItemName(unstrungBow));
		}
		player.getActionSender().sendRemoveInterfacePacket();
	}

	public static void appendDelay(Player player) {
		int fletchLevel = player.getSkills().getLevelForExperience(Skills.FLETCHING);
		if (player.fletchAmount > 0) {
			player.fletchAmount--;
			if (player.fletchThis.equals("log")) {
				if (player.fletchIndex == 0) {
					player.fletchThis = "shaft";
					shaft(player);
				} else if (player.fletchIndex == 1) {
					player.fletchThis = "stock";
					stock(player, fletchLevel);
				} else if (player.fletchIndex == 2) {
					player.fletchThis = "short";
					bow(player, fletchLevel);
				} else if (player.fletchIndex == 3) {
					player.fletchThis = "long";
					bow(player, fletchLevel);
				}
			}
			if (player.fletchThis.equals("headlessarrow"))
				headless(player);
			else if (player.fletchThis.equals("arrow"))
				arrows(player, fletchLevel);
			else if (player.fletchThis.equals("bolt") || player.fletchThis.equals("boltGem"))
				bolts(player, fletchLevel);
			else if (player.fletchThis.equals("stringBow"))
				stringBow(player, fletchLevel);
			else if (player.fletchThis.equals("stringCross"))
				stringCrossbow(player, fletchLevel);
			else if (player.fletchThis.equals("tips"))
				boltTips(player, fletchLevel);
			else if (player.fletchThis.equals("limb"))
				addLimbs(player, fletchLevel);
			else if (player.fletchThis.equals("shaft"))
				shaft(player);
			else if (player.fletchThis.equals("stock"))
				stock(player, fletchLevel);
			else if (player.fletchThis.equals("short") || player.fletchThis.equals("long"))
				bow(player, fletchLevel);
		}
	}

	public static void chooseItem5(Player player, String q1, String n1, String n2, String n3, String n4, String n5,
			int i1, int i2, int i3, int i4, int i5) {
		player.getActionSender().sendString(n1, 8949);
		player.getActionSender().sendString(n2, 8953);
		player.getActionSender().sendString(n3, 8957);
		player.getActionSender().sendString(n4, 8961);
		player.getActionSender().sendString(n5, 8965);
		player.getActionSender().sendString(q1, 8966);
		player.getActionSender().sendItemOnInterface(8941, 190, i1);
		player.getActionSender().sendItemOnInterface(8942, 190, i2);
		player.getActionSender().sendItemOnInterface(8943, 190, i3);
		player.getActionSender().sendItemOnInterface(8944, 190, i4);
		player.getActionSender().sendItemOnInterface(8945, 190, i5);
		player.getActionSender().sendChatInterface(8938);
	}
}