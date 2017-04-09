package com.model.game.character.player.dialogue.impl.slayer;

import com.model.game.character.npc.NPC;
import com.model.game.character.player.dialogue.Dialogue;
import com.model.game.character.player.dialogue.Expression;
import com.model.game.character.player.dialogue.Type;
import com.model.game.character.player.serialize.PlayerSave;
import com.model.game.character.player.skill.slayer.Slayer;
import com.model.game.character.player.skill.slayer.SlayerMasters;
import com.model.game.character.player.skill.slayer.SlayerTaskManagement;

/**
 * The dialogue enacted by Turael the beginner slayer master (Beginner Tasks)
 * 
 * @author Patrick van Elderen
 *
 */
public class TuraelDialogue extends Dialogue {

	private static final int NPC_ID = 401;

	@Override
	protected void start(Object... parameters) {
		send(Type.NPC, NPC_ID, Expression.DEFAULT, "'Ello, and what are you after, then?");
		if (!player.getFirstSlayerTask()) {
			setPhase(0);
		} else {
			setPhase(24);
		}
	}

	@Override
	protected void next() {
		System.out.println("next : phase " + getPhase());
		if (getPhase() == 0) {
			send(Type.CHOICE, DEFAULT_OPTION_TITLE, "Who are you?", "Do you have anything for trade?",
					"Erm... nothing...");
			setPhase(1);
		} else {
			if (getPhase() == 2) {
				send(Type.NPC, NPC_ID, Expression.DEFAULT, "I'm one of the Slayer Masters.",
						"I can offer you an assignment where you can kill",
						"an assigned monster for 'Slayer' experience.");
				setPhase(3);
			} else {
				if (getPhase() == 3) {
					send(Type.NPC, NPC_ID, Expression.DEFAULT,
							"After every task you complete you gain @blu@Slayer Points@bla@",
							"the amount varies depending on the ifficulty of",
							"the task. You can spend these points in the",
							"@blu@Rewars Store@bla@ accessible through Nieve.");
					setPhase(4);
				} else {
					if (getPhase() == 4) {
						send(Type.PLAYER, Expression.DEFAULT, "Wait... there's more Slayer Masters?");
						setPhase(5);
					} else {
						if (getPhase() == 5) {
							send(Type.NPC, NPC_ID, Expression.HAPPY, "Yes!",
									"Mazchna, Vannaka, Chaeldar, Nieve, Duradel and I can",
									"all give you assignments based on your combat level");
							setPhase(6);
						} else {
							if (getPhase() == 6) {
								send(Type.NPC, NPC_ID, Expression.DEFAULT,
										"To start you off I will give you a 'beginner' task.",
										"After you have finished that assignment",
										"return to me for another assignment.");
								setPhase(7);
							} else {
								if (getPhase() == 7) {
									send(Type.NPC, NPC_ID, Expression.DEFAULT,
											"You can contact me using an @blu@Enchanted gem@bla@.",
											"Slayer Masters can talk to you through this gem",
											"or you may ask questions through it.");
									setPhase(8);
								} else {
									if (getPhase() == 8) {
										send(Type.STATEMENT, "Turael hands you an @blu@Enchanted gem@bla@.");
										player.getItems().addItem(4155, 1);
										setPhase(9);
									} else {
										if (getPhase() == 9) {
											send(Type.NPC, NPC_ID, Expression.DEFAULT,
													"As this is your first task I will give you this gem for free.",
													"If you lose it you will need to buy another",
													"from a Slayer Master for a fee of 1 coin.");
											setPhase(10);
										} else {
											if (getPhase() == 10) {
												SlayerTaskManagement.beginnerTask(player);
												send(Type.NPC, NPC_ID, Expression.DEFAULT, "We'll start you off hunting @blu@" + NPC.getName(player.getSlayerTask()) + "s@bla@,", "you'll need to kill 10 of them");
												player.setFirstSlayerTask(true);
												setPhase(11);
											} else {
												if (getPhase() == 11) {
													send(Type.CHOICE, DEFAULT_OPTION_TITLE, "Got any tips for me?", "Okay great!");
													setPhase(12);
												} else {
													if (getPhase() == 13) {
														send(Type.NPC, NPC_ID, Expression.DEFAULT, "Slayer is an independent skill.", "Slayer Monsters are typically found in dungeons but", "they can be scattered amongst Divine-Reality.");
														setPhase(14);
													} else {
														if (getPhase() == 14) {
															send(Type.PLAYER, Expression.DEFAULT, "Okay, great!");
															player.setFirstSlayerTask(true);
															//PlayerSerialization.saveGame(player);
															PlayerSave.save(player);
															setPhase(16);
														} else {
															if (getPhase() == 15) {
																send(Type.NPC, NPC_ID, Expression.DEFAULT, "Yes, but you have yet to start the 'Slayer' skill.");
																player.getActionSender().sendMessage("You haven't started Slayer yet. Speak to Turael to do so.");
																setPhase(16);
															} else {
																if (getPhase() == 16) {
																	stop();
																} else {
																	if (getPhase() == 17) {
																		if (Slayer.hasTask(player)) {
																			send(Type.NPC, NPC_ID, Expression.DEFAULT, "You already have an assignment, you can reset", "your task by talking to Nieve.");
																			setPhase(16);
																		} else if (Slayer.suitableMaster(player) == SlayerMasters.TURAEL && !Slayer.hasTask(player)) {
																			SlayerTaskManagement.beginnerTask(player);
																			send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay. Your task is to kill " + player.getSlayerTaskAmount(), "@blu@" + player.getName() + ".");
																			setPhase(16);
																		} else if (Slayer.suitableMaster(player) == SlayerMasters.MAZCHNA && !Slayer.hasTask(player)) {
																			send(Type.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Mazchna.", "Would you like to get an assignment from him?");
																			setPhase(19);
																		} else if (Slayer.suitableMaster(player) == SlayerMasters.VANNAKA && !Slayer.hasTask(player)) {
																			send(Type.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Vannaka.", "Would you like to get an assignment from him?");
																			setPhase(19);
																		} else if (Slayer.suitableMaster(player) == SlayerMasters.CHAELDAR && !Slayer.hasTask(player)) {
																			send(Type.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Chaeldar.", "Would you like to get an assignment from her?");
																			setPhase(19);
																		} else if (Slayer.suitableMaster(player) == SlayerMasters.NIEVE && !Slayer.hasTask(player)) {
																			send(Type.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Nieve.", "Would you like to get an assignment from her?");
																			setPhase(19);
																		} else if (Slayer.suitableMaster(player) == SlayerMasters.DURADEL && !Slayer.hasTask(player)) {
																			send(Type.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Duradel.", "Would you like to get an assignment from him?");
																			setPhase(19);
																		}
																	} else {
																		if (getPhase() == 19) {
																			send(Type.CHOICE, DEFAULT_OPTION_TITLE, "Yes, I'd like more of a challenge.", "No thanks, I'd like an assignment from you.");
																			setPhase(20);
																		} else {
																			if (getPhase() == 21) {
																				if (Slayer.suitableMaster(player) == SlayerMasters.MAZCHNA) {
																					send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay. Mazchna can be found in Canifis.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
																					setPhase(16);
																				} else if (Slayer.suitableMaster(player) == SlayerMasters.VANNAKA) {
																					send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay. Vannaka can be found in Edgeville Dungeon.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
																					setPhase(16);
																				} else if (Slayer.suitableMaster(player) == SlayerMasters.CHAELDAR) {
																					send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay. Chaeldar can be found in Zanaris.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
																					setPhase(16);
																				} else if (Slayer.suitableMaster(player) == SlayerMasters.NIEVE) {
																					send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay. Nieve can be found in Draynor.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
																					setPhase(16);
																				} else if (Slayer.suitableMaster(player) == SlayerMasters.DURADEL) {
																					send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay. Duradel can be found in Burthorpe.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
																					setPhase(16);
																				}
																			} else {
																				if (getPhase() == 22) {
																					SlayerTaskManagement.beginnerTask(player);
																					send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay, fine. Your task is to kill " + player.getSlayerTaskAmount(), "@blu@" + NPC.getName(player.getSlayerTask()) + "s@bla@.");
																					setPhase(16);
																				} else {
																					if (getPhase() == 23) {
																						stop();
																						player.getActionSender().sendMessage("The @blu@Rewards@bla@ store can be accessed by speaking to Nieve.");
																					} else {
																						if (getPhase() == 24) {
																							send(Type.CHOICE, DEFAULT_OPTION_TITLE, "I'd like an assignment.", "Do you have anything for trade?", "Nevermind.");
																							setPhase(25);
																						} else {
																							if (getPhase() == 26) {
																								if (Slayer.hasTask(player)) {
																									send(Type.NPC, NPC_ID, Expression.DEFAULT, "You already have an assignment.");
																								} else {
																									setPhase(27);
																								}
																							} else {
																								if (getPhase() == 27) {
																									if (Slayer.suitableMaster(player) == SlayerMasters.MAZCHNA) {
																										send(Type.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Mazchna.", "Would you like to get an assignment from him?");
																										setPhase(28);
																									} else if (Slayer.suitableMaster(player) == SlayerMasters.VANNAKA) {
																										send(Type.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Vannaka.", "Would you like to get an assignment from him?");
																										setPhase(28);
																									} else if (Slayer.suitableMaster(player) == SlayerMasters.CHAELDAR) {
																										send(Type.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Chaeldar.", "Would you like to get an assignment from her?");
																										setPhase(28);
																									} else if (Slayer.suitableMaster(player) == SlayerMasters.NIEVE) {
																										send(Type.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Nieve.", "Would you like to get an assignment from her?");
																										setPhase(28);
																									} else if (Slayer.suitableMaster(player) == SlayerMasters.DURADEL) {
																										send(Type.NPC, NPC_ID, Expression.DEFAULT, "Someone of your strength should go and see Duradel.", "Would you like to get an assignment from him?");
																										setPhase(28);
																									}
																								} else {
																									if (getPhase() == 28) {
																										send(Type.CHOICE, DEFAULT_OPTION_TITLE, "Yes, I'd like more of a challenge.", "No thanks, I'd like an assignment from you.");
																										setPhase(29);
																									} else {
																										if (getPhase() == 30) {
																											if (Slayer.suitableMaster(player) == SlayerMasters.MAZCHNA) {
																												send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay. Mazchna can be found in Canifis.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
																												setPhase(16);
																											} else if (Slayer.suitableMaster(player) == SlayerMasters.VANNAKA) {
																												send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay. Vannaka can be found in Edgeville Dungeon.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
																												setPhase(16);
																											} else if (Slayer.suitableMaster(player) == SlayerMasters.CHAELDAR) {
																												send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay. Chaeldar can be found in Zanaris.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
																												setPhase(16);
																											} else if (Slayer.suitableMaster(player) == SlayerMasters.NIEVE) {
																												send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay. Nieve can be found in Draynor.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
																												setPhase(16);
																											} else if (Slayer.suitableMaster(player) == SlayerMasters.DURADEL) {
																												send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay. Duradel can be found in Burthorpe.", "You can get there using the 'Teleport' option", "on an Enchanted gem.");
																												setPhase(16);
																											}
																										} else {
																											if (getPhase() == 31) {
																												SlayerTaskManagement.beginnerTask(player);
																												send(Type.NPC, NPC_ID, Expression.DEFAULT, "Okay, fine. Your task is to kill ", player.getSlayerTaskAmount(), "@blu@" + NPC.getName(player.getSlayerTask()) + "s@bla@.");
																												setPhase(16);
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
		System.out.println("Phase" + getPhase() + " index : " + index);
		if (getPhase() == 1) {
			if (index == 1) {
				send(Type.PLAYER, Expression.DEFAULT, "Who are you?");
				setPhase(2);
			} else if (index == 2) {
				stop();
				player.getActionSender().sendMessage("The Slayer store cannot be opened as you have not started the 'Slayer' skill.");
			} else if (index == 3) {
				send(Type.PLAYER, Expression.DEFAULT, "Erm... nothing...");
				setPhase(16);
			}
		} else {
			if (getPhase() == 12) {
				if (index == 1) {
					send(Type.PLAYER, Expression.DEFAULT, "Got any tips for me?");
					setPhase(13);
				} else if (index == 2) {
					send(Type.PLAYER, Expression.DEFAULT, "Okay great!");
					setPhase(16);
					player.setFirstSlayerTask(true);
				}
			} else {
				if (getPhase() == 20) {
					if (index == 1) {
						send(Type.PLAYER, Expression.DEFAULT, "Yes, I'd like more of a challenge.");
						setPhase(21);
					} else if (index == 2) {
						send(Type.PLAYER, Expression.DEFAULT, "No thanks, I'd like an assignment from you.");
						setPhase(22);
					}
				} else {
					if (getPhase() == 25) {
						if (index == 1) {
							send(Type.PLAYER, Expression.DEFAULT, "I'd like an assignment.");
							setPhase(17);
						} else if (index == 2) {
							send(Type.PLAYER, Expression.DEFAULT, "Do you have anything for trade?");
							setPhase(23);
						} else if (index == 3) {
							send(Type.PLAYER, Expression.DEFAULT, "Nevermind.");
							setPhase(16);
						}
					} else {
						if (getPhase() == 29) {
							if (index == 1) {
								send(Type.PLAYER, Expression.DEFAULT, "Yes, I'd like more of a challenge.");
								setPhase(30);
							} else if (index == 2) {
								send(Type.PLAYER, Expression.DEFAULT, "No thanks, I'd like an assignment from you.");
								setPhase(31);
							}
						}
					}
				}
			}
		}
	}
}