package com.model.game.character.player.dialogue.impl.slayer;

import com.model.game.character.npc.NPC;
import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;
import com.model.game.character.player.packets.out.SendMessagePacket;
import com.model.game.character.player.skill.slayer.Slayer;
import com.model.game.character.player.skill.slayer.SlayerMasters;
import com.model.game.character.player.skill.slayer.SlayerTaskManagement;
import com.model.game.shop.Shop;

/**
 * The dialogue enacted by Nieve the Slayer Master.
 * 
 * @author Patrick van Elderen
 */
public class NieveDialogue extends Dialogue {
	
	private static final int NPC_ID = 490;
	
	@Override
	protected void start(Object... parameters) {
		send(Type.NPC, NPC_ID, Expression.DEFAULT, "Hello there Adventurer.", "I am Nieve one of the more elite Slayer Masters.", "What do you need?");
		if (Slayer.suitableMaster(player) == SlayerMasters.NIEVE || Slayer.suitableMaster(player) == SlayerMasters.DURADEL) {
			setPhase(0);
		} else {
			setPhase(18);
		}
	}
	
	@Override
	protected void next() {
		if (getPhase() == 0) {
			send(Type.CHOICE, DEFAULT_OPTION_TITLE, "I need an assignment.", "Do you have anything for trade?", "What rewards can I get for Slayer?", "What is social-slayer?", "Nevermind.");
			setPhase(1);
		} else {
			if (getPhase() == 2) {
				if (Slayer.hasTask(player)) {
					send(Type.NPC, NPC_ID, Expression.DEFAULT, "You already have an assignment.", "Would you like to reset your task?");
					setPhase(3);
				} else if (Slayer.suitableMaster(player) == SlayerMasters.NIEVE && !Slayer.hasTask(player)) {
					SlayerTaskManagement.eliteTask(player);
					send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay, your new task is to kill " + player.getSlayerTaskAmount(), "@blu@" + NPC.getName(player.getSlayerTask()) + "s@bla@. Good luck " + player.getName() + ".");
					setPhase(9);
				} else if (player.combatLevel < 85) {
					send(Type.NPC, NPC_ID, Expression.DEFAULT, "You are not strong enough to handle my assignments.", "Come back to me when you are a bit more experienced.");
					player.write(new SendMessagePacket("You need a combat level of 85 to get an assignment from Nieve."));
					setPhase(9);
				} else if (Slayer.suitableMaster(player) == SlayerMasters.DURADEL) {
					send(Type.NPC, NPC_ID, Expression.DEFAULT, "You are really strong. Did you know", "you can talk to Duradel and receive a boss", "slayer task? Would you like to get", "an assignment from him?");
					setPhase(10);
				}
			} else {
				if (getPhase() == 3) {
					send(Type.CHOICE, DEFAULT_OPTION_TITLE, "Yes please.", "No thank you.");
					setPhase(4);
				} else {
					if (getPhase() == 5) {
						send(Type.NPC, NPC_ID, Expression.DEFAULT, "That will cost you 10 Slayer Points.", "Are you sure you wish to do this?");
						setPhase(6);
					} else {
						if (getPhase() == 6) {
							send(Type.CHOICE, "Reset your task", "Yes", "No");
							setPhase(7);
						} else {
							if (getPhase() == 8) {
								if (SlayerTaskManagement.resetTask(player)) {
									send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay, your task has been reset.");
									setPhase(9);
								} else {
									send(Type.NPC, NPC_ID, Expression.DEFAULT, "You either do not have a task or you", "do not have enough points for me to complete", "this transaction.");
									setPhase(9);
								}
							} else {
								if (getPhase() == 9) {
									stop();
								} else {
									if (getPhase() == 10) {
										send(Type.CHOICE, DEFAULT_OPTION_TITLE, "Yes, I'd like a challenge.", "No thanks, I'd like an assignment from you.");
										setPhase(11);
									} else {
										if (getPhase() == 12) {
											send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay. Duradel can be found in Burthorpe.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
											setPhase(9);
										} else {
											if (getPhase() == 13) {
												SlayerTaskManagement.eliteTask(player);
												send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay, fine. Your task is to kill " + player.getSlayerTaskAmount(), "@blu@" + NPC.getName(player.getSlayerTask()) + "s@bla@. Good luck " + player.getName() + ".");
												setPhase(9);
											} else {
												if (getPhase() == 14) {
													stop();
												} else {
													if (getPhase() == 15) {
														Shop.SHOPS.get("Slayer Rewards").openShop(player);
													} else {
														if (getPhase() == 16) {
															send(Type.NPC, NPC_ID, Expression.DEFAULT, "Social-Slayer is like normal slayer", "except you can complete the task with a friend.", "The points are divided between both players", "and the experience too.");
															setPhase(17);
														} else {
															if (getPhase() == 17) {
																send(Type.NPC, NPC_ID, Expression.DEFAULT, "You can do Social-Slayer by using the", "option on the enchanted gem and inviting", "your partner.");
																setPhase(9);
															} else {
																if (getPhase() == 18) {
																	send(Type.CHOICE, DEFAULT_OPTION_TITLE, "Who are you?", "Can I reset my slayer task?", "Nevermind.");
																	setPhase(19);
																} else {
																	if (getPhase() == 20) {
																		send(Type.NPC, NPC_ID, Expression.DEFAULT, "I am Nieve, an experienced Slayer", "master better than the likes of those you have", "seen before. I can set elite tasks", "but as it seems you are too weak for those.");
																		setPhase(9);
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	protected void select(int index) {
		if (getPhase() == 1) {
			if (index == 1) {
				send(Type.PLAYER, Expression.DEFAULT, "I need an assignment.");
				setPhase(2);
			} else if (index == 2) {
				send(Type.PLAYER, Expression.DEFAULT, "Do you have anything for trade?");
				setPhase(14);
			} else if (index == 3) {
				send(Type.PLAYER, Expression.DEFAULT, "What rewards can I get from Slayer?");
				setPhase(15);
			} else if (index == 4) {
				send(Type.PLAYER, Expression.DEFAULT, "What is social-slayer?");
				setPhase(16);
			} else if (index == 5) {
				send(Type.PLAYER, Expression.DEFAULT, "Nevermind.");
				setPhase(9);
			}
		} else {
			if (getPhase() == 4) {
				if (index == 1) {
					send(Type.PLAYER, Expression.DEFAULT, "Yes please.");
					setPhase(5);
				} else if (index == 2) {
					send(Type.PLAYER, Expression.DEFAULT, "No thank you.");
					setPhase(9);
				}
			} else {
				if (getPhase() == 7) {
					if (index == 1) {
						send(Type.PLAYER, Expression.DEFAULT, "Yes.");
						setPhase(8);
					} else if (index == 2) {
						send(Type.PLAYER, Expression.DEFAULT, "No thank you.");
						setPhase(9);
					}
				} else {
					if (getPhase() == 11) {
						if (index == 1) {
							send(Type.PLAYER, Expression.DEFAULT, "Yes, I'd like more of a challenge.");
							setPhase(12);
						} else if (index == 2) {
							send(Type.PLAYER, Expression.DEFAULT, "No thanks, I'd like an assignment from you.");
							setPhase(13);
						}
					} else {
						if (getPhase() == 19) {
							if (index == 1) {
								send(Type.PLAYER, Expression.DEFAULT, "Who are you?");
								setPhase(20);
							} else if (index == 2) {
								send(Type.PLAYER, Expression.DEFAULT, "Can you reset my task?");
								setPhase(5);
							} else if (index == 3) {
								send(Type.PLAYER, Expression.DEFAULT, "Nevermind.");
								setPhase(9);
							}
						}
					}
				}
			}
		}
	}
}
