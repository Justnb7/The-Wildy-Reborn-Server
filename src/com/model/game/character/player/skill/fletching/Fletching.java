package com.model.game.character.player.skill.fletching;

import com.model.Server;
import com.model.game.character.Animation;
import com.model.game.character.player.Player;
import com.model.game.character.player.Skills;
import com.model.game.character.player.packets.encode.impl.SendClearScreen;
import com.model.game.character.player.packets.encode.impl.SendMessagePacket;
import com.model.game.character.player.skill.SkillHandler;
import com.model.game.character.player.skill.fletching.FletchingHandler.BoltTips;
import com.model.game.character.player.skill.fletching.FletchingHandler.Bolts;
import com.model.game.character.player.skill.fletching.FletchingHandler.Bows;
import com.model.game.character.player.skill.fletching.FletchingHandler.CrossBow;
import com.model.task.ScheduledTask;

/**
 * @author Jesse Pinkman (Rune-Server.org)
 */

public class Fletching {
	/**
	 * ClickingButtons Data
	 */
	public static int[][] otherButtons = { { 34245, 0, 1 }, { 34244, 0, 5 }, { 34243, 0, 10 }, { 34242, 0, 28 }, // Far
																													// Left
																													// Picture,
																													// 1,5,10,X
			{ 34249, 1, 1 }, { 34248, 1, 5 }, { 34247, 1, 10 }, { 34246, 1, 28 }, // Left
																					// Picture,
																					// 1,5,10,X
			{ 34253, 2, 1 }, { 34252, 2, 5 }, { 34251, 2, 10 }, { 34250, 2, 28 }, // Middle
																					// Picture,
																					// 1,5,10,X
			{ 35001, 3, 1 }, { 35000, 3, 5 }, { 34255, 3, 10 }, { 34254, 3, 28 }, // Right
																					// Picture,
																					// 1,5,10,X
			{ 35005, 4, 1 }, { 35004, 4, 5 }, { 35003, 4, 10 }, { 35002, 4, 28 }, // Far
																					// Right
																					// Picture,
																					// 1,5,10,X
	};

	/**
	 * Reference Items (Ex: Feathers,Knife,Chisel)
	 */
	public static int[] refItems = {/* knife */946,/* headless */53,/* feather */
	314,/* bowString */1777,/* cBowString */9438,/* chisel */1755 };

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
		player.getPA().chooseItem5("What would you like to fletch?", name(player, i1), name(player, i2), name(player, i3), name(player, i4), name(player, i5), i1, i2, i3, i4, i5);
	}

	/**
	 * Gets the item name and trim's if needed
	 */
	public static String name(Player player, int item) {
		if (item <= 0)
			return "";
		String[] remove = { "Oak ", "Maple ", "Willow ", "Yew ", "Magic ", "Arrow ", "Crossbow ", "Bronze ", "Iron ", "Mithril ", "Adamant ", "Rune ", "Opal ", "Jade ", "Topaz ", "Sapphire ", "Emerald ", "Ruby ", "Diamond ", "Dragonstone ", "Dragon ", "Onyx " };
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
		player.isSkilling  = false;
		player.isFletching = player.needsFletchDelay = false;
		for (int r = 0; r < player.fletchSprites.length; r++)
			player.fletchSprites[r] = -1;
	}

	/**
	 * Handles Timer
	 */
	public static void startCycle(final Player player) {
		if(SkillHandler.isSkillActive(player, Skills.WOODCUTTING)) {
			player.write(new SendMessagePacket("You cannot perform this action"));
			return;
		}
		player.isSkilling = true;
		Server.getTaskScheduler().schedule(new ScheduledTask(1) {
			@Override
			public void execute() {
				if (!player.isSkilling) {
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
				int Slot = player.getItems().getItemSlot(player.arrowShaft), amount = -1, Slot2 = player.getItems().getItemSlot(getFeather()), amount2 = -1;
				if (Slot != -1)
					amount = player.playerItemsN[Slot];
				if (Slot2 != -1)
					amount2 = player.playerItemsN[Slot2];
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
				player.write(new SendClearScreen());
				if (player.fletchItem > 0)
		            resetFletching(player);
			}
		} else {
			player.write(new SendClearScreen());
			if (player.fletchItem > 0)
	            resetFletching(player);
		}
		player.write(new SendClearScreen());
	}

	public static void arrows(Player player, int fletchLevel) {
		if (FletchingHandler.Arrows.forId(player.fletchItem) != null) {
			FletchingHandler.Arrows arrow = FletchingHandler.Arrows.forId(player.fletchItem);
			if (fletchLevel >= arrow.getReq()) {
				if (player.getItems().playerHasItem(arrow.getTips(), 1)) {
					if (player.getItems().playerHasItem(getHeadless(), 1)) {
						int Slot = player.getItems().getItemSlot(arrow.getTips()), amount = -1, Slot2 = player.getItems().getItemSlot(getHeadless()), amount2 = -1;
						if (Slot != -1)
							amount = player.playerItemsN[Slot];
						if (Slot2 != -1)
							amount2 = player.playerItemsN[Slot2];
						if (amount >= 15 && amount2 >= 15) {
							player.getItems().deleteItem(arrow.getTips(), 15);
							player.getItems().deleteItem(getHeadless(), 15);
							player.getItems().addItem(arrow.getArrow(), 15);
							player.getSkills().addExperience(Skills.FLETCHING, arrow.getExp() * 15);
						} else {
							if (amount <= amount2) {
								player.getItems().deleteItem(arrow.getTips(), amount);
								player.getItems().deleteItem(getHeadless(), amount);
								player.getItems().addItem(arrow.getArrow(), amount);
								player.getSkills().addExperience(Skills.FLETCHING, arrow.getExp());
							} else {
								player.getItems().deleteItem(arrow.getTips(), amount2);
								player.getItems().deleteItem(getHeadless(), amount2);
								player.getItems().addItem(arrow.getArrow(), amount2);
								player.getSkills().addExperience(Skills.FLETCHING, arrow.getExp());
							}
						}
						player.lastFletch = System.currentTimeMillis();
					} else {
						player.write(new SendClearScreen());
						if (player.fletchItem > 0)
				            resetFletching(player);
					}
				} else {
					player.write(new SendClearScreen());
					if (player.fletchItem > 0)
			            resetFletching(player);
				}
			} else {
				player.write(new SendClearScreen());
				if (player.fletchItem > 0)
		            resetFletching(player);
				player.write(new SendMessagePacket("You need a Fletching level of " + arrow.getReq() + " to fletch a " + player.getItems().getItemName(arrow.getArrow())));
			}
		} else {
			player.write(new SendClearScreen());
			if (player.fletchItem > 0)
	            resetFletching(player);
		}
		player.write(new SendClearScreen());
	}

	public static void boltTips(Player player, int fletchLevel) {
		for (final BoltTips tip : BoltTips.values())
			if (tip.getInput() == player.fletchItem)
				if (player.getItems().playerHasItem(tip.getInput(), 1)) {
					if (fletchLevel >= tip.getReq()) {
						player.getItems().deleteItem(tip.getInput(), 1);
						player.getItems().addItem(tip.getOutput(), tip.getAmt());
						player.getSkills().addExperience(Skills.FLETCHING, tip.getExp());
						player.write(new SendClearScreen());
						player.lastFletch = System.currentTimeMillis();
					} else {
						player.write(new SendClearScreen());
						if (player.fletchItem > 0)
				            resetFletching(player);
						player.write(new SendMessagePacket("You need a Fletching level of " + tip.getReq() + " to fletch " + player.getItems().getItemName(tip.getOutput())));
					}
				} else {
					player.write(new SendClearScreen());
					if (player.fletchItem > 0)
			            resetFletching(player);
				}
	}

	public static void bolts(Player player, int fletchLevel) {
		int in1 = 0, in2 = 0, out = 0, req = 0, xp = 0;
		for (final Bolts bolt : Bolts.values()) {
			if (player.fletchThis.equals("bolt") && bolt.getType().equals("bolt") && player.fletchItem == bolt.getInput1()) {
				in1 = bolt.getInput1();
				in2 = getFeather();
				out = bolt.getOutput();
				req = bolt.getReq();
				xp = bolt.getExp();
				break;
			} else if (player.fletchThis.equals("boltGem") && bolt.getType().equals("boltGem") && player.fletchItem == bolt.getInput2()) {
				in1 = bolt.getInput1();
				in2 = bolt.getInput2();
				out = bolt.getOutput();
				req = bolt.getReq();
				xp = bolt.getExp();
				break;
			}
		}
		int Slot = player.getItems().getItemSlot(in1), amount = -1, Slot2 = player.getItems().getItemSlot(in2), amount2 = -1;
		if (Slot != -1)
			amount = player.playerItemsN[Slot];
		if (Slot2 != -1)
			amount2 = player.playerItemsN[Slot2];
		if (fletchLevel >= req) {
			if (player.getItems().playerHasItem(in1, 1) && player.getItems().playerHasItem(in2, 1)) {
				if (amount >= 15 && amount2 >= 15) {
					player.getItems().deleteItem(in1, 15);
					player.getItems().deleteItem(in2, 15);
					player.getItems().addItem(out, 15);
					player.getSkills().addExperience(Skills.FLETCHING, xp * 15);
				} else {
					if (amount <= amount2) {
						player.getItems().deleteItem(in1, amount);
						player.getItems().deleteItem(in2, amount);
						player.getItems().addItem(out, amount);
						player.getSkills().addExperience(Skills.FLETCHING, xp);
					} else {
						player.getItems().deleteItem(in1, amount2);
						player.getItems().deleteItem(in2, amount2);
						player.getItems().addItem(out, amount2);
						player.getSkills().addExperience(Skills.FLETCHING, xp);
					}
				}
				player.lastFletch = System.currentTimeMillis();
			}
		} else {
			player.write(new SendClearScreen());
			if (player.fletchItem > 0)
	            resetFletching(player);
			player.write(new SendMessagePacket("You need a Fletching level of " + req + " to fletch " + player.getItems().getItemName(out)));
		}
		player.write(new SendClearScreen());
	}

	public static void shaft(Player player) {
		if (player.getItems().playerHasItem(player.fletchItem, 1)) {
			player.getItems().deleteItem(player.fletchItem, 1);
			player.getItems().addItem(player.arrowShaft, 15);
			player.getSkills().addExperience(Skills.FLETCHING, 5);
			player.write(new SendClearScreen());
			player.playAnimation(Animation.create(1248));
			player.lastFletch = System.currentTimeMillis();
		} else {
			player.write(new SendClearScreen());
			if (player.fletchItem > 0)
	            resetFletching(player);
		}
	}

	public static void stock(Player player, int fletchLevel) {
		for (final CrossBow bow : CrossBow.values()) {
			if (player.fletchItem == bow.getLog()) {
				if (fletchLevel >= bow.getReq()) {
					if (player.getItems().playerHasItem(bow.getLog()) && player.getItems().playerHasItem(getKnife())) {
						player.getItems().deleteItem(bow.getLog(), 1);
						player.getItems().addItem(bow.getStock(), 1);
						player.getSkills().addExperience(Skills.FLETCHING, bow.getExp1());
						player.playAnimation(Animation.create(1248));
						player.lastFletch = System.currentTimeMillis();
					}
				} else {
					player.write(new SendClearScreen());
					if (player.fletchItem > 0)
			            resetFletching(player);
					player.write(new SendMessagePacket("You need a Fletching level of " + bow.getReq() + " to fletch a " + player.getItems().getItemName(bow.getBowU())));
				}
			}
		}
		player.write(new SendClearScreen());
	}

	public static void addLimbs(Player player, int fletchLevel) {
		for (final CrossBow bow : CrossBow.values()) {
			if (player.fletchItem == bow.getStock()) {
				if (fletchLevel >= bow.getReq()) {
					if (player.getItems().playerHasItem(bow.getStock()) && player.getItems().playerHasItem(bow.getLimbs())) {
						player.getItems().deleteItem(bow.getStock(), 1);
						player.getItems().deleteItem(bow.getLimbs(), 1);
						player.getItems().addItem(bow.getBowU(), 1);
						player.getSkills().addExperience(Skills.FLETCHING, bow.getExp1());
						player.lastFletch = System.currentTimeMillis();
					}
				} else {
					player.write(new SendClearScreen());
					if (player.fletchItem > 0)
			            resetFletching(player);
					player.write(new SendMessagePacket("You need a Fletching level of " + bow.getReq() + " to fletch a " + player.getItems().getItemName(bow.getBowU())));
				}
			}
		}
		player.write(new SendClearScreen());
	}

	public static void stringBow(Player player, int fletchLevel) {
		for (final Bows bow : Bows.values()) {
			if (player.fletchItem == bow.getBowU()) {
				if (fletchLevel >= bow.getReq()) {
					if (player.getItems().playerHasItem(getBS()) && player.getItems().playerHasItem(bow.getBowU())) {
						player.getItems().deleteItem(bow.getBowU(), 1);
						player.getItems().deleteItem(getBS(), 1);
						player.getItems().addItem(bow.getBow(), 1);
						player.getSkills().addExperience(Skills.FLETCHING, bow.getExp());
						player.playAnimation(Animation.create(bow.getEmote()));
						player.lastFletch = System.currentTimeMillis();
					}
				} else {
					player.write(new SendClearScreen());
					if (player.fletchItem > 0)
			            resetFletching(player);
					player.write(new SendMessagePacket("You need a Fletching level of " + bow.getReq() + " to string a " + player.getItems().getItemName(bow.getBow())));
				}
			}
		}
		player.write(new SendClearScreen());
	}

	public static void stringCrossbow(Player player, int fletchLevel) {
		for (final CrossBow bow : CrossBow.values()) {
			if (player.fletchItem == bow.getBowU()) {
				if (fletchLevel >= bow.getReq()) {
					if (player.getItems().playerHasItem(getCBS()) && player.getItems().playerHasItem(bow.getBowU())) {
						player.getItems().deleteItem(bow.getBowU(), 1);
						player.getItems().deleteItem(getCBS(), 1);
						player.getItems().addItem(bow.getBow(), 1);
						player.getSkills().addExperience(Skills.FLETCHING, bow.getExp2());
						player.playAnimation(Animation.create(bow.getEmote()));
						player.lastFletch = System.currentTimeMillis();
					}
				} else {
					player.write(new SendClearScreen());
					if (player.fletchItem > 0)
			            resetFletching(player);
					player.write(new SendMessagePacket("You need a Fletching level of " + bow.getReq() + " to string a " + player.getItems().getItemName(bow.getBow())));
				}
			}
		}
		player.write(new SendClearScreen());
	}

	public static void bow(Player player, int fletchLevel) {
		int in1 = 0, out = 0, req = 0, xp = 0;
		for (final Bows bow : Bows.values()) {
			if (player.fletchThis.equals("short") && bow.getBowType().equals("short") && player.fletchItem == bow.getLog()) {
				in1 = bow.getLog();
				out = bow.getBowU();
				req = bow.getReq();
				xp = bow.getExp();
				break;
			} else if (player.fletchThis.equals("long") && bow.getBowType().equals("long") && player.fletchItem == bow.getLog()) {
				in1 = bow.getLog();
				out = bow.getBowU();
				req = bow.getReq();
				xp = bow.getExp();
				break;
			}
		}
		if (fletchLevel >= req) {
			if (player.getItems().playerHasItem(in1, 1)) {
				player.getItems().deleteItem(in1, 1);
				player.getItems().addItem(out, 1);
				player.playAnimation(Animation.create(1248));
				player.lastFletch = System.currentTimeMillis();
				player.getSkills().addExperience(Skills.FLETCHING, xp);
			} else {
				player.write(new SendClearScreen());
				if (player.fletchItem > 0)
		            resetFletching(player);
			}
		} else {
			player.write(new SendClearScreen());
			if (player.fletchItem > 0)
	            resetFletching(player);
			player.write(new SendMessagePacket("You need a Fletching level of " + req + " to fletch a " + player.getItems().getItemName(out)));
		}
		player.write(new SendClearScreen());
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
}